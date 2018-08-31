package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jdi.AbsentInformationException;
import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.Location;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import com.sun.jdi.request.EventRequestManager;

import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.access.Variable;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.synthesis.collect.DynamothDataCollector;
import fr.inria.lille.repair.synthesis.collect.SpoonElementsCollector;
import fr.inria.lille.repair.synthesis.collect.spoon.ClassCollector;
import fr.inria.lille.repair.synthesis.collect.spoon.DynamothConstantCollector;
import fr.inria.lille.repair.synthesis.collect.spoon.MethodCollector;
import fr.inria.lille.repair.synthesis.collect.spoon.StatCollector;
import fr.inria.lille.repair.synthesis.collect.spoon.VariableTypeCollector;
import fr.inria.lille.repair.synthesis.collect.spoon.VariablesInSuspiciousCollector;
import fr.inria.lille.repair.vm.DebugJUnitRunner;
import spoon.processing.ProcessingManager;
import spoon.processing.Processor;
import spoon.support.RuntimeProcessingManager;

/**
 * Created by Thomas Durieux on 06/03/15.
 * 
 * Code that collects runtime values
 * 
 * @modified by Matias Martinez
 *
 */
public class DynamothCollector {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final File[] projectRoots;
	private final SourceLocation location;
	private final URL[] classpath;
	private Map<String, Object[]> oracle;
	private final String[] tests;
	private final int dataCollectionTimeoutInSeconds;
	/**
	 * key: test name, value: list of runtime contexts (if a statement is executed
	 * several times in the same test
	 */
	private final SortedMap<String, List<Candidates>> values;
	private final NopolContext nopolContext;

	private int nbExpressionEvaluated = 0;

	private String currentTestClass;
	private String currentTestMethod;
	private int currentIteration;
	private VirtualMachine vm;
	private Candidates constants;
	private List<String> classes;
	private int nbBreakPointCalls = 0;
	private long startTime;
	private long initExecutionTime;
	private long collectExecutionTime;
	private String buggyMethod;
	private SpoonElementsCollector spoonElementsCollector;
	private StatCollector statCollector;
	private Map<String, String> variableType;
	private Set<String> calledMethods;

	private SuspiciousModificationPoint mp = null;

	/**
	 * Create a new DynaMoth synthesizer
	 * 
	 * @param projectRoots the root folders of the project
	 * @param location     the location of the code to synthesiz
	 * @param classpath    the classpath of the project
	 * @param oracle       the oracle of the project Map<testClass#testMethod,
	 *                     {value iteration 1, value iteration 2, ...}>
	 * @param tests        tests to execute
	 */

	public DynamothCollector(SuspiciousModificationPoint smp, File[] projectRoots, URL[] classpath, String[] tests,
			NopolContext nopolContext) {
		this(null, smp, projectRoots, classpath, tests, nopolContext);
		Map<String, Object[]> oracle = new HashMap<>();

		for (String testCase : tests) {
			oracle.put(testCase, new Boolean[] { true });
		}
		this.oracle = oracle;
	}

	public DynamothCollector(Map<String, Object[]> oracle, SuspiciousModificationPoint smp, File[] projectRoots,
			URL[] classpath, String[] tests, NopolContext nopolContext) {
		this.mp = smp;
		this.projectRoots = projectRoots;
		SourceLocation location = new SourceLocation(smp.getCtClass().getQualifiedName(),
				smp.getSuspicious().getLineNumber());
		this.location = location;
		this.dataCollectionTimeoutInSeconds = nopolContext.getDataCollectionTimeoutInSecondForSynthesis();

		this.oracle = oracle;
		this.tests = tests;
		this.values = new TreeMap<>();
		this.nopolContext = nopolContext;

		this.constants = new Candidates();
		this.classes = new ArrayList<>();
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		URL[] urls = ((URLClassLoader) cl).getURLs();
		ArrayList<URL> liClasspath = new ArrayList<>();
		for (int i = 0; i < classpath.length; i++) {
			URL url = classpath[i];
			File file = new File(url.getFile());
			if (file.exists()) {
				liClasspath.add(url);
			}
		}
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			File file = new File(url.getFile());
			if (file.exists()) {
				liClasspath.add(url);
			}
		}
		this.classpath = liClasspath.toArray(new URL[0]);
	}

	public void run(long remainingTime) {
		// check if all the expected values are the same in the oracle
		HashSet<Object> setOfValues = new HashSet<>();
		for (Object[] values : oracle.values()) {
			setOfValues.addAll(Arrays.asList(values));
		}

		this.startTime = System.currentTimeMillis();

		try {
			vm = DebugJUnitRunner.run(tests, classpath, nopolContext);
			watchBuggyClass();
			vm.resume();
			processVMEvents();
			this.collectExecutionTime = System.currentTimeMillis();
			if (values.size() == 0) {
				throw new RuntimeException("should not happen, no value collected");
			}
			DebugJUnitRunner.shutdown(vm);

		} catch (IOException e) {
			throw new RuntimeException("Unable to communicate with the project", e);
		}
	}

	private void processVMEvents() {
		try {
			// process events
			final EventQueue eventQueue = vm.eventQueue();
			while (true) {
				EventSet eventSet = eventQueue.remove(TimeUnit.SECONDS.toMillis(this.dataCollectionTimeoutInSeconds));
				if (eventSet == null)
					return; // timeout
				for (Event event : eventSet) {
					if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
						// exit
						DebugJUnitRunner.process.destroy();
						logger.debug("Exit");
						return;
					} else if (event instanceof ClassPrepareEvent) {
						logger.debug("ClassPrepareEvent");
						processClassPrepareEvent();
					} else if (event instanceof BreakpointEvent) {
						logger.debug("BreakpointEvent");
						processBreakPointEvents((BreakpointEvent) event);
					}
				}
				eventSet.resume();
			} // end while true
		} catch (Exception e) {
			System.err.println("Error processing VMEvents");
			e.printStackTrace();
		} finally {
			DebugJUnitRunner.process.destroy();
		}
	}

	private void processClassPrepareEvent() throws AbsentInformationException {
		EventRequestManager erm = vm.eventRequestManager();
		List<ReferenceType> referenceTypes = vm.classesByName(this.location.getContainingClassName());
		// List listOfLocations =
		// referenceTypes.get(0).locationsOfLine(this.location.getLineNumber());

		int loc = this.location.getLineNumber();
		List listOfLocations = null;

		do {
			listOfLocations = referenceTypes.get(0).locationsOfLine(loc);
			loc--;
		} while (loc > 0 && listOfLocations.isEmpty());

		if (listOfLocations.size() == 0) {
			throw new RuntimeException("Buggy class not found " + this.location);
		}
		com.sun.jdi.Location jdiLocation = (com.sun.jdi.Location) listOfLocations.get(0);
		this.buggyMethod = jdiLocation.method().name();
		breakpointSuspicious = erm.createBreakpointRequest(jdiLocation);
		breakpointSuspicious.setEnabled(true);
		initSpoon();
		this.initExecutionTime = System.currentTimeMillis();
	}

	private boolean jumpEnabled = false;
	private BreakpointRequest breakpointJump;
	private BreakpointRequest breakpointSuspicious;

	private void jumpEndTest(ThreadReference threadRef) {
		try {
			List<StackFrame> frames = threadRef.frames();
			for (StackFrame stackFrame : frames) {
				for (String test : tests) {
					String[] splitted = test.split("#");
					test = splitted[0];
					ObjectReference thisObject = stackFrame.thisObject();
					if (thisObject == null) {
						continue;
					}
					String frameClass = thisObject.referenceType().name();
					if (frameClass.equals(test)) {
						String frameMethod = stackFrame.location().method().name();
						if (oracle.containsKey(test + "#" + frameMethod)) {
							EventRequestManager erm = vm.eventRequestManager();
							try {
								List listOfLocations = stackFrame.location().declaringType()
										.locationsOfLine(stackFrame.location().lineNumber());
								if (listOfLocations.size() == 0) {
									continue;
								}
								Location jdiLocation = (Location) listOfLocations.get(listOfLocations.size() - 1);
								breakpointJump = erm.createBreakpointRequest(jdiLocation);
								breakpointJump.setEnabled(true);
								jumpEnabled = true;
								breakpointSuspicious.setEnabled(false);
								return;
							} catch (AbsentInformationException e) {
								// ignore
							}
						}
					}
				}
			}
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
		}
	}

	private void processBreakPointEvents(BreakpointEvent breakpointEvent) throws IncompatibleThreadStateException {
		if (jumpEnabled) {
			breakpointJump.setEnabled(false);
			jumpEnabled = false;
			breakpointSuspicious.setEnabled(true);
			return;
		}
		nbBreakPointCalls++;
		ThreadReference threadRef = breakpointEvent.thread();
		try {
			getCurrentTest(threadRef);
		} catch (RuntimeException e) {
			return;
		}
		if (!oracle.containsKey(currentTestClass + "#" + currentTestMethod)) {
			return;
		}
		if (values.containsKey(currentTestClass + "#" + currentTestMethod)) {
			if (values.get(currentTestClass + "#" + currentTestMethod).size() > nopolContext
					.getMaxLineInvocationPerTest()) {
				jumpEndTest(threadRef);
				return;
			}
		}

		if (!values.containsKey(currentTestClass + "#" + currentTestMethod)) {
			values.put(currentTestClass + "#" + currentTestMethod, new ArrayList<Candidates>());
		}

		Candidates allValues = new Candidates();

		Candidates expressionCollectedBySpoon = spoonElementsCollector.collect(threadRef);
		Candidates expressionsCollectedAtRuntime = collectRuntimeValues(threadRef);

		allValues.addAll(expressionCollectedBySpoon);
		allValues.addAll(expressionsCollectedAtRuntime);

		values.get(currentTestClass + "#" + currentTestMethod).add(allValues);
	}

	private void getCurrentTest(ThreadReference threadRef) {
		try {
			List<StackFrame> frames = threadRef.frames();
			for (StackFrame stackFrame : frames) {
				for (String test : tests) {
					String[] splitted = test.split("#");
					test = splitted[0];
					ObjectReference thisObject = stackFrame.thisObject();
					if (thisObject == null) {
						continue;
					}
					String frameClass = thisObject.referenceType().name();
					if (frameClass.equals(test)) {
						String frameMethod = stackFrame.location().method().name();
						if (oracle.containsKey(test + "#" + frameMethod)) {
							if (frameClass.equals(currentTestClass) && frameMethod.equals(currentTestMethod)) {
								this.currentIteration++;
							} else {
								currentTestClass = test;
								currentTestMethod = frameMethod;
								this.currentIteration = 0;
							}
							logger.info("[test] " + currentTestClass + "#" + currentTestMethod + " iteration "
									+ this.currentIteration);
							return;
						}
					}
				}
			}
		} catch (IncompatibleThreadStateException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Unable to identify the current test");
	}

	private void initSpoon() {
		File classFile = null;
		for (File projectRoot : projectRoots) {
			classFile = new File(projectRoot.getAbsoluteFile() + "/"
					+ this.location.getContainingClassName().replaceAll("\\.", "/") + ".java");
			if (classFile.exists()) {
				break;
			}
			classFile = null;
		}

		if (nopolContext.isCollectLiterals()) {
			constants = collectLiterals();
		}
		if (nopolContext.isCollectStaticMethods()) {
			classes = collectUsedClasses();
		}

		Map<String, String> variableType = collectVariableType();
		this.calledMethods = collectMethod();
		this.variableType = variableType;
		try {
			// nopolContext.getComplianceLevel();
			StatCollector statCollector = new StatCollector(buggyMethod);

			setProcessor(statCollector);
			manager.process(mp.getCodeElement());

			this.statCollector = statCollector;
			VariablesInSuspiciousCollector variablesInSuspiciousCollector = new VariablesInSuspiciousCollector(
					location);
			setProcessor(variablesInSuspiciousCollector);
			manager.process(mp.getCodeElement());

			spoonElementsCollector = new SpoonElementsCollector(variablesInSuspiciousCollector.getVariables(),
					nopolContext);
		} catch (Exception e) {
			logger.error("Unable to collect used classes", e);
			logger.error("--> Unable to collect used classes for mp: " + mp.identified + " " + mp);
			e.printStackTrace();
			// throw new RuntimeException("Unable to communicate with the project", e);
		}
	}

	private void watchBuggyClass() {
		EventRequestManager erm = vm.eventRequestManager();
		ClassPrepareRequest classPrepareRequest = erm.createClassPrepareRequest();
		classPrepareRequest.addClassFilter(location.getContainingClassName());
		classPrepareRequest.setEnabled(true);
	}

	private Candidates collectRuntimeValues(ThreadReference threadRef) {
		if (values.containsKey(currentTestClass + "#" + currentTestMethod)) {
			if (values.get(currentTestClass + "#" + currentTestMethod).size() > nopolContext
					.getMaxLineInvocationPerTest()) {
				return new Candidates();
			}
		}
		DynamothDataCollector dataCollect = new DynamothDataCollector(threadRef, constants, location, buggyMethod,
				classes, statCollector, this.variableType, this.calledMethods, nopolContext);
		Candidates eexps = dataCollect.collect(TimeUnit.MINUTES.toMillis(7));
		return eexps;
	}

	private Candidates collectLiterals() {
		Candidates candidates = new Candidates();
		try {

			DynamothConstantCollector d = new DynamothConstantCollector(candidates, buggyMethod, nopolContext);
			setProcessor(d);
			manager.process(mp.getCodeElement());
		} catch (Exception e) {
			logger.warn("Unable to collect literals", e);
		}
		return candidates;
	}

	private List<String> collectUsedClasses() {
		try {
			ClassCollector classCollector = new ClassCollector(buggyMethod);
			setProcessor(classCollector);
			this.manager.process(mp.getCodeElement());

			return classCollector.getClasses();
		} catch (Exception e) {
			logger.warn("Unable to collect used classes", e);
		}
		return new ArrayList<>();
	}

	private Set<String> collectMethod() {
		try {
			MethodCollector methodCollector = new MethodCollector();

			return methodCollector.getMethods();
		} catch (Exception e) {
			logger.warn("Unable to collect method", e);
		}
		return new HashSet<>();
	}

	private Map<String, String> collectVariableType() {
		try {
			VariableTypeCollector variableTypeCollector = new VariableTypeCollector(buggyMethod,
					this.location.getLineNumber());

			setProcessor(variableTypeCollector);
			manager.process(mp.getCodeElement());
			return variableTypeCollector.getVariableType();
		} catch (Exception e) {
			logger.warn("Unable to collect used classes", e);
		}
		return new HashMap<>();
	}

	private void printSummary(Candidates result) {
		if (values.values().isEmpty())
			return;
		List<Candidates> next = values.values().iterator().next();
		Candidates candidate = next.get(0);
		int nbValueToCombine = candidate.size();
		int nbConstant = 0;
		int nbMethodInvocation = 0;
		int nbFieldAccess = 0;
		int nbVariable = 0;
		for (Expression expression : candidate) {
			if (expression.getValue().isConstant()) {
				nbConstant++;
			} else if (expression instanceof Variable) {
				if (((Variable) expression).getTarget() != null) {
					nbFieldAccess++;
				} else {
					nbVariable++;
				}
			} else if (expression instanceof fr.inria.lille.repair.expression.access.Method) {
				nbMethodInvocation++;
			}
		}

		System.out.println();
		System.out.println();
		System.out.println("========= Info ==========");
		System.out.println("Nb constants             " + nbConstant);
		System.out.println("Nb method invocations    " + nbMethodInvocation);
		System.out.println("Nb field access          " + nbFieldAccess);
		System.out.println("Nb variables             " + nbVariable);
		System.out.println("Total                    " + nbValueToCombine);
		System.out.println("Nb evaluated expressions " + nbExpressionEvaluated);
		System.out.println("Init Execution time      " + (initExecutionTime - startTime) + " ms");
		System.out.println("Collect Execution time   " + (collectExecutionTime - initExecutionTime) + " ms");
		double combinationDuration = System.currentTimeMillis() - collectExecutionTime;
		System.out.println("Combine Execution time   " + combinationDuration + " ms");
		double nbCombinationPerMs = nbExpressionEvaluated / combinationDuration;
		System.out.println("Nb Combination par sec   " + Math.round(nbCombinationPerMs * 1000) + " combinations/sec");
		System.out.println("Total Execution time     " + (System.currentTimeMillis() - startTime) + " ms");
		System.out.println("Nb line execution        " + nbBreakPointCalls);

		System.out.println("Nb results               " + result.size());
		System.out.println();
		System.out.println("Results:");
		for (int i = 0; i < result.size(); i++) {
			Expression expression = result.get(i);
			System.out.println((i + 1) + ". " + expression.toString());
		}

		System.out.println();
		System.out.println();

		System.out.println(this.statCollector);

		System.out.println(" & " + nbConstant + " & " + nbMethodInvocation + " & " + nbFieldAccess + " & " + nbVariable
				+ " & " + nbValueToCombine + " & " + nbExpressionEvaluated + " & "
				+ (System.currentTimeMillis() - startTime) + " ms" + " & " + nbBreakPointCalls + " &");
	}

	public Map<String, List<Candidates>> getValues() {
		return values;
	}

	private void setProcessor(Processor processor) {
		manager.getProcessors().clear();

		manager.addProcessor(processor);

	}

	ProcessingManager manager = new RuntimeProcessingManager(MutationSupporter.getFactory());

	public Map<String, Object[]> getOracle() {
		return oracle;
	}

	public void setOracle(Map<String, Object[]> oracle) {
		this.oracle = oracle;
	}

	public NopolContext getNopolContext() {
		return nopolContext;
	}
}
