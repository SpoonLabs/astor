package fr.inria.astor.core.faultlocalization.cocospoon;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.inria.astor.core.faultlocalization.cocospoon.code.SourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.code.StatementSourceLocation;
import fr.inria.astor.core.faultlocalization.cocospoon.metrics.Metric;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestCase;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestCasesListener;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestResult;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestResultImpl;
import fr.inria.astor.core.faultlocalization.cocospoon.testrunner.TestSuiteExecution;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import instrumenting._Instrumenting;

/**
 * Created by bdanglot on 10/3/16.
 */
public class CocoSpoonEngineFaultLocalizer {

	protected Map<SourceLocation, List<TestResult>> countPerSourceLocation;

	private final Metric metric;
	protected int nbSucceedTest;
	protected int nbFailingTest;
	protected Map<String, Boolean> resultsPerNameOfTest ;
	protected List<StatementSourceLocation> statements;

	public CocoSpoonEngineFaultLocalizer(Metric metric) {
		this.metric = metric;
		this.statements = new ArrayList<>();
	}

	public void runTests(String[] testClasses, ClassLoader cl, ProjectRepairFacade project) {

		TestCasesListener listener = new TestCasesListener();
		resultsPerNameOfTest = new HashMap<>();
		Map<String, Map<SourceLocation, Boolean>> linesExecutedPerTestNames = new HashMap<>();
		nbFailingTest = 0;
		nbSucceedTest = 0;
		for (int i = 0; i < testClasses.length; i++) {
			try {
				for (String methodName : getTestMethods(cl.loadClass(testClasses[i]))) {
					String testMethod = testClasses[i] + "#" + methodName;
					TestSuiteExecution.runTest(testMethod, cl, listener);
					// Since we executed one test at the time, the listener
					// contains one and only one TestCase
					boolean testSucceed = listener.numberOfFailedTests() == 0;
					resultsPerNameOfTest.put(testMethod, testSucceed);
					if (testSucceed) {
						nbSucceedTest++;
					} else {
						nbFailingTest++;
					}
					linesExecutedPerTestNames.put(testMethod, copyExecutedLinesAndReinit(_Instrumenting.lines));
				}
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		this.buildTestResultPerSourceLocation(resultsPerNameOfTest, linesExecutedPerTestNames);
	}

	private void sortBySuspiciousness() {
		for (SourceLocation sourceLocation : this.countPerSourceLocation.keySet()) {
			StatementSourceLocation current = new StatementSourceLocation(this.metric, sourceLocation);
			int ef = 0;
			int ep = 0;
			for (TestResult results : this.countPerSourceLocation.get(sourceLocation)) {
				if (results.isSuccessful())
					ep++;
				else
					ef++;
			}
			current.setNf(nbFailingTest - ef);
			current.setNp(nbSucceedTest - ep);
			current.setEp(ep);
			current.setEf(ef);
			statements.add(current);
		}
		Collections.sort(statements, new Comparator<StatementSourceLocation>() {
			@Override
			public int compare(StatementSourceLocation o1, StatementSourceLocation o2) {
				return Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness());
			}
		});

		LinkedHashMap<SourceLocation, List<TestResult>> map = new LinkedHashMap<>();
		for (StatementSourceLocation source : statements) {
			map.put(source.getLocation(), this.countPerSourceLocation.get(source.getLocation()));
		}
		this.countPerSourceLocation = map;
	}

	/**
	 * This method copy the original map given, and set all its boolean at false
	 * to re-run a new test case
	 *
	 * @param original
	 *            map to copy and reinit
	 * @return a copy of the map original
	 */
	protected Map<SourceLocation, Boolean> copyExecutedLinesAndReinit(Map<String, Map<Integer, Boolean>> original) {
		Map<SourceLocation, Boolean> copy = new HashMap<>();
		for (String s : original.keySet()) {
			for (Integer i : original.get(s).keySet()) {
				copy.put(new SourceLocation(s, i), original.get(s).get(i));
				original.get(s).put(i, false);
			}
		}
		return copy;
	}

	/**
	 * @param resultsPerNameOfTest
	 * @param linesExecutedPerTestNames
	 */
	protected void buildTestResultPerSourceLocation(Map<String, Boolean> resultsPerNameOfTest,
			Map<String, Map<SourceLocation, Boolean>> linesExecutedPerTestNames) {
		this.countPerSourceLocation = new HashMap<>();
		for (String fullQualifiedMethodTestName : linesExecutedPerTestNames.keySet()) {
			Map<SourceLocation, Boolean> coveredLines = linesExecutedPerTestNames.get(fullQualifiedMethodTestName);
			for (SourceLocation sourceLocation : coveredLines.keySet()) {
				if (coveredLines.get(sourceLocation)) {
					if (!this.countPerSourceLocation.containsKey(sourceLocation))
						this.countPerSourceLocation.put(sourceLocation, new ArrayList<TestResult>());
					this.countPerSourceLocation.get(sourceLocation)
							.add(new TestResultImpl(TestCase.from(fullQualifiedMethodTestName),
									resultsPerNameOfTest.get(fullQualifiedMethodTestName)));
				}
			}
		}
	}

	/**
	 * using reflection to build the name of all test methods to be run
	 *
	 * @param classOfTestCase
	 * @return
	 */
	protected List<String> getTestMethods(Class classOfTestCase) {
		List<String> methodsNames = new ArrayList<>();
		for (Method method : classOfTestCase.getMethods()) {
			if (method.getAnnotation(Test.class) != null || isPublicTestMethod(method))
				methodsNames.add(method.getName());
		}
		return methodsNames;
	}

	private boolean isPublicTestMethod(Method m) {
		return this.isTestMethod(m) && Modifier.isPublic(m.getModifiers());
	}

	private boolean isTestMethod(Method m) {
		return m.getParameterTypes().length == 0 && m.getName().startsWith("test")
				&& m.getReturnType().equals(Void.TYPE);
	}

	public Map<SourceLocation, List<TestResult>> getTestListPerStatement() {
		sortBySuspiciousness();

		return this.countPerSourceLocation;
	}

	public List<? extends StatementSourceLocation> getStatements() {
		return statements;
	}

	public Map<String, Boolean> getResultsPerNameOfTest() {
		return resultsPerNameOfTest;
	}

	public void setResultsPerNameOfTest(Map<String, Boolean> resultsPerNameOfTest) {
		this.resultsPerNameOfTest = resultsPerNameOfTest;
	}

}
