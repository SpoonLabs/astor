package fr.inria.astor.approaches.extensions.rt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.extensions.rt.core.Classification;
import fr.inria.astor.approaches.extensions.rt.core.RuntimeInformation;
import fr.inria.astor.approaches.extensions.rt.core.TestIntermediateAnalysisResult;
import fr.inria.astor.approaches.extensions.rt.elements.AsAssertion;
import fr.inria.astor.approaches.extensions.rt.elements.Helper;
import fr.inria.astor.approaches.extensions.rt.out.JSonResultOriginal;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.declaration.CtClassImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RtEngine extends AstorCoreEngine {

	private static final String ASSUME = "assume";

	private static final String FAIL = "fail";

	private static final String ASSERT = "assert";

	List<SuspiciousCode> allExecutedStatements = null;

	public List<TestIntermediateAnalysisResult> resultByTest = new ArrayList<>();

	List<String> namespace = Arrays.asList("org.assertj", "org.testng", "org.mockito", "org.spockframework",
			"org.junit", "cucumber", "org.jbehave");

	public RtEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);

		ConfigurationProperties.setProperty("includeTestInSusp", "true");
		ConfigurationProperties.setProperty("limitbysuspicious", "false");
		ConfigurationProperties.setProperty("maxsuspcandidates", "1");
		ConfigurationProperties.setProperty("regressionforfaultlocalization", "true");
		ConfigurationProperties.setProperty("considerzerovaluesusp", "true");
		ConfigurationProperties.setProperty("disablelog", "false");
		ConfigurationProperties.setProperty("onlympcovered", "false");
		ConfigurationProperties.setProperty("onlympfromtest", "false");
		ConfigurationProperties.setProperty("maxGeneration", "1");
		ConfigurationProperties.setProperty("maxsuspcandidates", "100000000");
	}

	@Override
	protected void initializePopulation(List<SuspiciousCode> suspicious) throws Exception {
		allExecutedStatements = suspicious;
	}

	Exception exceptionReceived = null;

	@Override
	public void startEvolution() throws Exception {

		if (projectFacade.getProperties().getRegressionTestCases().isEmpty()) {
			log.error("No test can be found");
			exceptionReceived = new Exception("No test can be found");
		} else {

			if (!ConfigurationProperties.getPropertyBool("skipanalysis")) {
				try {
					RuntimeInformation ri = computeDynamicInformation();
					analyzeTestSuiteExecution(ri);

				} catch (Exception e) {
					e.printStackTrace();
					log.error(e);
					exceptionReceived = e;
				}
			}
		}
	}

	public RuntimeInformation computeDynamicInformation() throws Exception {
		List<String> allTestCases = new ArrayList();

		List<String> allTestCasesWithoutParent = this.getProjectFacade().getProperties().getRegressionTestCases();

		for (String tc : allTestCasesWithoutParent) {
			allTestCases.add(tc);
			CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(tc);

			if (aTestModelCtClass == null) {
				log.error("Error: Not ct model for class " + tc);
				continue;
			}

			CtTypeReference supclass = aTestModelCtClass.getSuperclass();
			while (supclass != null) {
				if (!allTestCases.contains(supclass.getQualifiedName()))
					allTestCases.add(supclass.getQualifiedName());
				else {
					// already analyzed
					break;
				}
				supclass = supclass.getSuperclass();
			}

		}

		if (allTestCases.isEmpty()) {
			log.error("No test to execute");
			throw new Exception("No test to execute");

		}

		log.debug("# Test cases: " + allTestCases.size());

		// key is test class, values are method (cases)
		MapList<String, String> passingCoveredTestCaseFromClass = new MapList<>();

		for (SuspiciousCode executed : allExecutedStatements) {

			for (TestCaseResult tcr : executed.getCoveredByTests()) {
				String testCaseName = formatTestCaseName(tcr.getTestCaseName());
				if (tcr.isCorrect() && (
				// Test class not analyzed
				!passingCoveredTestCaseFromClass.containsKey(tcr.getTestCaseClass())
						// test method not analyzed

						|| !passingCoveredTestCaseFromClass.get(tcr.getTestCaseClass()) // executed.getClassName()
								.contains(testCaseName)))
					passingCoveredTestCaseFromClass.add(tcr.getTestCaseClass(), testCaseName);
			}
		}

		// Lines of code covered grouped by test. The key is the test class name
		MapList<String, Integer> mapLinesCovered = new MapList<>();
		Map<String, SuspiciousCode> mapCacheSuspicious = new HashMap<>();

		for (SuspiciousCode executedStatement : allExecutedStatements) {
			mapLinesCovered.add(executedStatement.getClassName(), executedStatement.getLineNumber());
			mapCacheSuspicious.put(executedStatement.getClassName() + executedStatement.getLineNumber(),
					executedStatement);
		}

		// Check results
		List<String> notexec = new ArrayList<>();
		for (String test : allTestCases) {

			if (!mapLinesCovered.containsKey(test)) {
				log.error("Test " + test + " not executed");
				notexec.add(test);
			}
		}
		if (!notexec.isEmpty()) {
			log.error("nr test not ex " + notexec.size());
		}

		resultByTest = new ArrayList<>();

		log.info("End processing RT");

		RuntimeInformation runtimeinfo = new RuntimeInformation(allTestCases, allTestCasesWithoutParent,
				mapLinesCovered, mapCacheSuspicious, passingCoveredTestCaseFromClass, notexec);
		return runtimeinfo;

	}

	public void analyzeTestSuiteExecution(RuntimeInformation runtimeinfo) {
		// For each class name
		for (String aNameOfTestClass : runtimeinfo.allTestCases) {

			if (runtimeinfo.notexec.contains(aNameOfTestClass)) {
				log.debug("Ignoring -not executed line- test: " + aNameOfTestClass);
				continue;
			}
			log.info("*-*-*-*----- Analying TestClass: " + aNameOfTestClass);
			CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(aNameOfTestClass);
			if (aTestModelCtClass == null) {
				log.error("No class modeled for " + aNameOfTestClass);
				continue;
			}

			List<String> testMethodsFromClass = runtimeinfo.passingCoveredTestCaseFromClass.get(aNameOfTestClass);

			if (testMethodsFromClass == null || testMethodsFromClass.isEmpty()) {
				log.error("No method executed for class " + aNameOfTestClass);
				continue;
			}

			for (String aTestMethodFromClass : testMethodsFromClass) {

				TestIntermediateAnalysisResult resultTestCase = processTest(aTestMethodFromClass, aNameOfTestClass,
						aTestModelCtClass, runtimeinfo);
				if (resultTestCase != null) {
					resultByTest.add(resultTestCase);
				}
			}
		}
	}

	public TestIntermediateAnalysisResult processSingleTest(RuntimeInformation runtimeinfo, String aNameOfTestClass,
			String aTestMethodFromClass) {

		if (runtimeinfo.notexec.contains(aNameOfTestClass)) {
			log.debug("Ignoring -not executed line- test: " + aNameOfTestClass);
			return null;
		}
		log.info("*-*-*-*----- Analying TestClass: " + aNameOfTestClass);
		CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(aNameOfTestClass);
		if (aTestModelCtClass == null) {
			log.error("No class modeled for " + aNameOfTestClass);
			return null;
		}

		List<String> testMethodsFromClass = runtimeinfo.passingCoveredTestCaseFromClass.get(aNameOfTestClass);

		if (testMethodsFromClass == null || testMethodsFromClass.isEmpty()) {
			log.error("No method executed for class " + aNameOfTestClass);
			return null;
		}

		TestIntermediateAnalysisResult resultTestCase = processTest(aTestMethodFromClass, aNameOfTestClass, aTestModelCtClass,
				runtimeinfo);

		return resultTestCase;

	}

	public TestIntermediateAnalysisResult processTest(String aTestMethodFromClass, String aNameOfTestClass,
			CtClass aTestModelCtClass, RuntimeInformation runtimeinfo) {
		log.info("**** Analying TestMethod: " + aTestMethodFromClass);

		List<CtClass> allClasses = getClasses(aTestModelCtClass);

		Optional<CtExecutableReference<?>> testMethodOp = aTestModelCtClass.getAllExecutables().stream()
				.filter(e -> e.getSimpleName().equals(aTestMethodFromClass)).findFirst();
		if (!testMethodOp.isPresent()) {
			log.error("Problem " + aTestMethodFromClass + " not found in class " + aNameOfTestClass);
			return null;
		}
		CtExecutable testMethodModel = testMethodOp.get().getDeclaration();
		List<String> expectException = expectEx(testMethodModel);
		if (expectException.size() > 0) {
			log.debug("Method expecting exception via annotation " + aTestMethodFromClass);
		}

		if (!runtimeinfo.passingCoveredTestCaseFromClass.containsKey(aNameOfTestClass)
				|| !(runtimeinfo.passingCoveredTestCaseFromClass.get(aNameOfTestClass)
						.contains(aTestMethodFromClass))) {

			log.debug("ignoring test " + aTestMethodFromClass + " from class " + aNameOfTestClass);
			return null;

		}
		// get all statements
		List<CtStatement> allStmtsFromClass = testMethodModel.getElements(new LineFilter());

		List<CtInvocation> allExpectedExceptionFromTest = filterExpectedExceptions(allStmtsFromClass);
		if (allExpectedExceptionFromTest.size() > 0) {
			log.debug("Expected exception found at " + aTestMethodFromClass);
		}
		List<CtInvocation> allAssumesFromTest = filterAssume(allStmtsFromClass);
		List<CtInvocation> allAssertionsFromTest = filterAssertions(allStmtsFromClass);
		List<CtInvocation> allFailsFromTest = filterFails(allStmtsFromClass);
		List<Helper> allHelperInvocationFromTest = filterHelper(allStmtsFromClass, new ArrayList());
		// filter from assertions the missed fail
		List<CtInvocation> allMissedFailFromTest = filterMissedFail(allAssertionsFromTest);
		List<CtInvocation> allRedundantAssertionFromTest = filterRedundantAssertions(allAssertionsFromTest);

		allAssertionsFromTest.removeAll(allMissedFailFromTest);

		// Second step

		// The missed fails are removed from the assertion list (they are a
		// sub-category).

		List<CtReturn> allSkipFromTest = filterSkips(allStmtsFromClass, testMethodModel, allClasses);

		// Fail missing analysis
		Classification<AsAssertion> rFailMissing = classifyAssertions(testMethodModel, runtimeinfo.mapCacheSuspicious,
				aTestModelCtClass, allMissedFailFromTest);

		chechInsideTry(rFailMissing.resultExecuted, runtimeinfo.mapCacheSuspicious, aTestModelCtClass, testMethodModel);
		chechInsideTry(rFailMissing.resultNotExecuted, runtimeinfo.mapCacheSuspicious, aTestModelCtClass,
				testMethodModel);

		// Redundant
		Classification<AsAssertion> rRedundantAssertion = classifyAssertions(testMethodModel,
				runtimeinfo.mapCacheSuspicious, aTestModelCtClass, allRedundantAssertionFromTest);

		// TODO: to move?
		chechInsideTry(rRedundantAssertion.resultExecuted, runtimeinfo.mapCacheSuspicious, aTestModelCtClass,
				testMethodModel);
		chechInsideTry(rRedundantAssertion.resultNotExecuted, runtimeinfo.mapCacheSuspicious, aTestModelCtClass,
				testMethodModel);

		Classification<AsAssertion> rAssert = classifyAssertions(testMethodModel, runtimeinfo.mapCacheSuspicious,
				aTestModelCtClass, allAssertionsFromTest);

		Classification<Helper> rHelperCall = classifyHelpersAssertionExecution(aTestModelCtClass,
				allHelperInvocationFromTest, runtimeinfo.mapCacheSuspicious, testMethodModel, false);

		Classification<Helper> rHelperAssertion = classifyHelpersAssertionExecution(aTestModelCtClass,
				allHelperInvocationFromTest, runtimeinfo.mapCacheSuspicious, testMethodModel, true);

		if (rHelperAssertion.getResultExecuted().isEmpty() && rHelperCall.getResultExecuted().isEmpty()
				&& rAssert.getResultExecuted().isEmpty()) {
			boolean anyExecuted = checkAnyStatementExecuted(allStmtsFromClass, runtimeinfo.mapCacheSuspicious,
					aTestModelCtClass, testMethodModel);
			// If any statement in the test code was executed, we return.
			if (!anyExecuted) {
				log.info("NO test element executed for test " + aTestMethodFromClass + ", class " + aNameOfTestClass
						+ " any executed: " + anyExecuted);

				return null;
			}

		}
		boolean onlyAssumeExecuted = checkOnlyAssumeExecuted(allStmtsFromClass, runtimeinfo.mapCacheSuspicious,
				aTestModelCtClass, allAssumesFromTest, testMethodModel);

		// We exclude assertions and fails from the list of other method invocations.
		List<CtInvocation> allMIFromTest = testMethodModel.getBody().getElements(new TypeFilter<>(CtInvocation.class));
		allMIFromTest.removeAll(allAssertionsFromTest);
		allMIFromTest.removeAll(allFailsFromTest);

		// Removing assertion called from helpers not executed
		ignoringHelperAssertionFromNotExecutedHelper(rHelperAssertion.resultNotExecuted, rHelperCall.resultNotExecuted);

		TestIntermediateAnalysisResult resultTestCase = new TestIntermediateAnalysisResult(this, onlyAssumeExecuted, allAssumesFromTest,
				rAssert, rHelperAssertion, rHelperCall, aNameOfTestClass, aTestMethodFromClass, testMethodModel,
				rFailMissing, rRedundantAssertion, allSkipFromTest, expectException, allExpectedExceptionFromTest,
				allMIFromTest, allFailsFromTest);

		return resultTestCase;

	}

	private void ignoringHelperAssertionFromNotExecutedHelper(List<Helper> resultNotExecutedHelperAssertion,
			List<Helper> resultNotExecutedHelperCall) {

		List<Helper> assertionsToRemove = new ArrayList<>();

		for (Helper anHelperWithAssertion : resultNotExecutedHelperAssertion) {

			for (CtInvocation aCallToAssertion : anHelperWithAssertion.getCalls()) {
				boolean isAlready = false;
				for (Helper helperCallNotExecuted : resultNotExecutedHelperCall) {
					CtElement call = helperCallNotExecuted.getElement();
					if (call.equals(aCallToAssertion)) {
						isAlready = true;
						break;
					}
				}
				//
				if (isAlready) {
					assertionsToRemove.add(anHelperWithAssertion);
				}

			}

		}
		resultNotExecutedHelperAssertion.removeAll(assertionsToRemove);

	}

	private boolean checkAnyStatementExecuted(List<CtStatement> allStmtsFromClass,
			Map<String, SuspiciousCode> mapCacheSuspicious, CtClass aTestModelCtClass, CtExecutable testMethodModel) {

		for (CtStatement statement : allStmtsFromClass) {
			// We pass twice 'aTestModelCtClass' because we check if a test statement is
			// executed
			boolean covered = isCovered(mapCacheSuspicious, statement, aTestModelCtClass, aTestModelCtClass,
					testMethodModel);
			if (covered) {
				return true;
			}
		}
		return false;

	}

	private boolean checkOnlyAssumeExecuted(List<CtStatement> allStmtsFromClass,
			Map<String, SuspiciousCode> mapCacheSuspicious, CtClass aTestModelCtClass, List<CtInvocation> invocations,
			CtExecutable testMethodModel) {

		if (invocations == null || invocations.isEmpty()) {
			return false;
		}
		// Put true once we analyze the assertion
		boolean assureAnalyzed = false;
		for (CtInvocation invocation : invocations) {
			boolean invocationExecuted = false;
			boolean otherStatementExecutedAfter = false;
			for (CtStatement statement : allStmtsFromClass) {
				boolean covered = isCovered(mapCacheSuspicious, statement, aTestModelCtClass, aTestModelCtClass,
						testMethodModel);
				if (covered) {
					if (invocation == statement) {
						invocationExecuted = true;
						assureAnalyzed = true;
					} else {
						// Once we analyze the assure
						if (assureAnalyzed)
							otherStatementExecutedAfter = true;
					}
				}
			}
			return invocationExecuted && !otherStatementExecutedAfter;
		}
		return false;
	}

	/**
	 * Check if a missing assertion is inside a try-catch
	 * 
	 * @param allMissedFailFromTest
	 * @param executedLines
	 * @param mapCacheSuspicious
	 * @param parentClass
	 * @param testMethodModel
	 */
	private void chechInsideTry(List<AsAssertion> allMissedFailFromTest, Map<String, SuspiciousCode> mapCacheSuspicious,
			CtClass parentClass, CtExecutable testMethodModel) {

		for (AsAssertion aMissAssertion : allMissedFailFromTest) {

			CtTry parentTry = aMissAssertion.getCtAssertion().getParent(CtTry.class);
			if (parentTry != null) {
				for (CtCatch aCatch : parentTry.getCatchers()) {
					CtBlock block = aCatch.getBody();
					if (block != null && block.getStatements().size() > 0) {
						for (CtStatement anStatementInBlock : block.getStatements()) {

							boolean covered = isCovered(mapCacheSuspicious, anStatementInBlock, parentClass,
									parentClass, testMethodModel);
							if (covered) {
								aMissAssertion.setFp(true);
								continue;
							}

						}
					} else {
						CtBlock pblock = parentTry.getParent(CtBlock.class);
						int indexTry = pblock.getStatements().indexOf(parentTry);
						if (indexTry >= 0 && indexTry + 1 < pblock.getStatements().size()) {
							CtStatement stNext = pblock.getStatements().get(indexTry + 1);
							boolean covered = isCovered(mapCacheSuspicious, stNext, parentClass, parentClass,
									testMethodModel);
							if (covered) {
								aMissAssertion.setFp(true);
								continue;
							}
						}

					}
				}
			}
		}

	}

	/**
	 * Marks those assertions inside a try-catch
	 * 
	 * @param executedLines
	 * @param parentClass
	 * @param aMissAssertion
	 */
	public void analyzeMissingAssertionInsideTryCatch(MapList<String, Integer> executedLines, CtClass parentClass,
			AsAssertion aMissAssertion) {

	}

	private List<CtInvocation> filterExpectedExceptions(List<CtStatement> allStmtsFromClass) {
		List<CtInvocation> expectedEx = new ArrayList<>();
		for (CtStatement targetElement : allStmtsFromClass) {
			if (targetElement instanceof CtInvocation) {
				CtInvocation targetInvocation = (CtInvocation) targetElement;
				if (targetInvocation.getExecutable().getSimpleName().toLowerCase().startsWith("expect")
						&& targetInvocation.getExecutable().getDeclaringType().getSimpleName()
								.equals("ExpectedException")) {
					expectedEx.add(targetInvocation);
				}
			}
		}
		return expectedEx;
	}

	private List<String> expectEx(CtExecutable testMethodModel) {

		return testMethodModel.getAnnotations().stream()
				.filter(e -> e.getType().getSimpleName().equals("Test") && e.getValues().containsKey("expected"))
				.map(e -> e.getValues().get("expected").toString()).collect(Collectors.toList());

	}

	private String formatTestCaseName(String testCaseName) {
		int i = testCaseName.indexOf("[");
		if (i > 0)
			return testCaseName.substring(0, i);

		else
			return testCaseName;
	}

	private List<CtInvocation> filterMissedFail(List<CtInvocation> allAssertionsFromTest) {

		List<CtInvocation> missedFails = new ArrayList<>();

		for (CtInvocation anInvocation : allAssertionsFromTest) {
			filterMissingFailAssertion(missedFails, anInvocation);

		}
		return missedFails;
	}

	/**
	 * Tree cases: AssertEquals (x,x) AssertTrue(True) AssertFalse(False)
	 * 
	 * @param allAssertionsFromTest
	 * @return
	 */
	private List<CtInvocation> filterRedundantAssertions(List<CtInvocation> allAssertionsFromTest) {

		List<CtInvocation> redundantAssertion = new ArrayList<>();

		for (CtInvocation anInvocation : allAssertionsFromTest) {

			filterCaseEquals(anInvocation, redundantAssertion);

			filterCaseAssertRedundant(redundantAssertion, anInvocation);

		}
		return redundantAssertion;
	}

	protected void filterMissingFailAssertion(List<CtInvocation> redundantAssertion, CtInvocation anInvocation) {
		CtElement argument = null;
		// case having a single argument
		if (anInvocation.getArguments().size() == 1) {
			argument = (CtElement) anInvocation.getArguments().get(0);
			// case having a message as first arg
		} else if (anInvocation.getArguments().size() == 2) {
			argument = (CtElement) anInvocation.getArguments().get(1);
		}
		if (argument != null) {
			String contentArgumentLC = argument.toString().toLowerCase();
			if (isInvWithName(anInvocation, "assertFalse")) {
				// Now, we case expecting false and passing True
				if (contentArgumentLC.equals("\"true\"") || contentArgumentLC.equals("true")
						|| contentArgumentLC.equals("boolean.true"))
					redundantAssertion.add((anInvocation));

			} else if (isInvWithName(anInvocation, "assertTrue")) {
				if (contentArgumentLC.equals("\"false\"") || contentArgumentLC.equals("false")
						|| contentArgumentLC.equals("boolean.false"))
					// Now, we find for a false parameter:
					redundantAssertion.add((anInvocation));

			}
		}
	}

	public void filterCaseAssertRedundant(List<CtInvocation> redundantAssertion, CtInvocation anInvocation) {
		CtElement argument = null;
		// case having a single argument
		if (anInvocation.getArguments().size() == 1) {
			argument = (CtElement) anInvocation.getArguments().get(0);
			// case having a message as first arg
		} else if (anInvocation.getArguments().size() == 2) {
			argument = (CtElement) anInvocation.getArguments().get(1);
		}
		if (argument != null) {
			String contentArgumentLC = argument.toString().toLowerCase();
			if (isInvWithName(anInvocation, "assertTrue")) {
				// Now, we case expecting false and passing True
				if (contentArgumentLC.equals("\"true\"") || contentArgumentLC.equals("true")
						|| contentArgumentLC.equals("boolean.true"))
					redundantAssertion.add((anInvocation));

			} else if (isInvWithName(anInvocation, "assertFalse")) {
				if (contentArgumentLC.equals("\"false\"") || contentArgumentLC.equals("false")
						|| contentArgumentLC.equals("boolean.false"))
					// Now, we find for a false parameter:
					redundantAssertion.add((anInvocation));

			}
		}
	}

	private void filterCaseEquals(CtInvocation anInvocation, List<CtInvocation> redundantAssertion) {
		CtElement argument1 = null, argument2 = null;

		if (isInvWithName(anInvocation, "assertEquals")) {

			// case having a single argument
			if (anInvocation.getArguments().size() == 3) {
				argument1 = (CtElement) anInvocation.getArguments().get(1);
				argument2 = (CtElement) anInvocation.getArguments().get(2);
				// case having a message as first arg
			} else if (anInvocation.getArguments().size() == 2) {
				argument1 = (CtElement) anInvocation.getArguments().get(0);
				argument2 = (CtElement) anInvocation.getArguments().get(1);
			}

			if (argument1 != null && argument2 != null && argument1.toString().equals(argument2.toString())) {
				redundantAssertion.add(anInvocation);
			}

		}
	}

	private List<CtReturn> filterSkips(List<CtStatement> allStmtsFromClass, CtExecutable method,
			// CtClass aTestModelCtClass
			List<CtClass> allClasses) {

		List<CtReturn> skips = new ArrayList<>();

		for (CtStatement aStatement : allStmtsFromClass) {
			if (aStatement instanceof CtReturn) {
				// check the parent class is the test method (discarding elements from anonymous
				// classes)
				CtClassImpl parentClass = (CtClassImpl) aStatement.getParent(CtClassImpl.class);

				if (allClasses.contains(parentClass) &&
				// aTestModelCtClass.equals(parentClass) &&
				// we don't care about returns inside lambda
						aStatement.getParent(CtLambda.class) == null &&
						// check that is not the last statement (if it's the last one it's fine)
						checkNotLast(aStatement, method)

				) {
					skips.add((CtReturn) aStatement);
				}
			}
		}
		return skips;
	}

	/**
	 * Return true if it's not last
	 * 
	 * @param aStatement
	 * @param method
	 * @return
	 */
	private boolean checkNotLast(CtStatement aStatement, CtExecutable method) {

		CtStatement lastStatement = method.getBody().getLastStatement();

		if (lastStatement instanceof CtInvocation) {
			CtInvocation targetInvocation = (CtInvocation) lastStatement;
			if (isInvWithName(targetInvocation, FAIL)) {
				return false;
			}
		}

		// It's not the last one in the method
		return !lastStatement.equals(aStatement)
				// check that statement is not inside an element that is the last one
				&& !lastStatement.equals(aStatement.getParent(new LineFilter()));
	}

	public List<CtClass> getClasses(CtClass aTestModelCtClass) {
		List<CtClass> allClasses = new ArrayList();
		allClasses.add(aTestModelCtClass);

		CtTypeReference superclass = ((CtClassImpl) aTestModelCtClass).getSuperclass();
		if (superclass == null)
			return allClasses;
		CtType td = superclass.getTypeDeclaration();
		if (td instanceof CtClassImpl) {
			allClasses.addAll(getClasses((CtClassImpl) td));
		}
		return allClasses;
	}

	private Classification<Helper> classifyHelpersAssertionExecution(CtClass aTestModelCtClass,
			List<Helper> allHelperInvocationFromTest, Map<String, SuspiciousCode> cacheSuspicious,
			CtExecutable methodTestExecuted, boolean checkAssertion) {

		Classification<Helper> result = new Classification();
		for (Helper aHelper : allHelperInvocationFromTest) {

			CtInvocation assertion = (checkAssertion) ? aHelper.getAssertion().getCtAssertion()
					: aHelper.getCalls().get(0);
			CtClass ctclassFromAssert = assertion.getParent(CtClass.class);

			boolean covered = isCovered(cacheSuspicious, assertion, ctclassFromAssert, aTestModelCtClass,
					methodTestExecuted);

			if (!covered) {
				result.getResultNotExecuted().add(aHelper);
				if (checkAssertion)
					aHelper.unexecutedAssert = true;
				else
					aHelper.unexecutedCall = true;
			} else {
				result.getResultExecuted().add(aHelper);
			}
		}
		return result;
	}

	private String getClassName(CtType mclass) {
		if (mclass.isAnonymous()) {
			return getClassName(mclass.getDeclaringType());
		} else {
			return mclass.getQualifiedName();
		}
	}

	private boolean isCoverSingleLine(Map<String, SuspiciousCode> cacheSuspicious, CtClass aTestModelCtClass,
			CtExecutable testMethodModel, String keyLocationAssertion) {
		if (cacheSuspicious.containsKey(keyLocationAssertion)) {
			// Assertion was covered, let's check if by the current test case
			SuspiciousCode cover = cacheSuspicious.get(keyLocationAssertion);
			for (TestCaseResult tr : cover.getCoveredByTests()) {
				if (tr.getTestCaseClass().equals(aTestModelCtClass.getQualifiedName())
						&& (testMethodModel == null || tr.getTestCaseName().equals(testMethodModel.getSimpleName()))) {
					return true;
				}
			}

		}
		return false;
	}

	public static boolean isCovered(Map<String, SuspiciousCode> cacheSuspicious, CtElement elementToCheck,
			CtClass ctclassFromElementToCheck, CtClass aTestModelCtClass) {
		return isCovered(cacheSuspicious, elementToCheck, ctclassFromElementToCheck, aTestModelCtClass);
	}

	private boolean isCovered(Map<String, SuspiciousCode> cacheSuspicious, CtElement elementToCheck,
			CtClass ctclassFromElementToCheck, CtClass aTestModelCtClass, CtExecutable testMethodModel) {
		try {
			// the location of the assertion contained in the helper
			int init = elementToCheck.getPosition().getLine();
			int end = elementToCheck.getPosition().getEndLine();
			// check if cover in one range of locations
			for (int i = init; i <= end; i++) {

				String keyLocationAssertion = getClassName(ctclassFromElementToCheck) + i;

				if (isCoverSingleLine(cacheSuspicious, aTestModelCtClass, testMethodModel, keyLocationAssertion))
					return true;

			}
		} catch (Exception e) {
			log.error("Error getting position of element");
			e.printStackTrace();
		}
		return false;

	}

	public Classification<AsAssertion> classifyAssertions(CtExecutable methodOfAssertment,
			Map<String, SuspiciousCode> mapCacheSuspicious, CtClass aTestModelCtClass,
			List<CtInvocation> allAssertionsFromTest) {
		Classification<AsAssertion> result = new Classification();
		// For each assert
		for (CtInvocation anAssertFromTest : allAssertionsFromTest) {

			CtClass ctclassFromAssert = anAssertFromTest.getParent(CtClass.class);

			boolean covered = isCovered(mapCacheSuspicious, anAssertFromTest, ctclassFromAssert, aTestModelCtClass,
					methodOfAssertment);

			if (!covered) {

				result.getResultNotExecuted().add(new AsAssertion(anAssertFromTest));
				log.info("Not covered: " + anAssertFromTest + " at " + aTestModelCtClass.getQualifiedName());
			} else {
				result.getResultExecuted().add(new AsAssertion(anAssertFromTest));
			}
		}
		return result;
	}

	public String keySignatureExecuted(SuspiciousCode e) {
		return keySignatureExecuted(e.getClassName(), getTestCaseMethodName(e));
	}

	public String getTestCaseMethodName(SuspiciousCode e) {
		// We only consider a method name, which, at least in JUnit, must be unique (we
		// cannot have two test methods with the same name)
		return e.getMethodName().substring(0, e.getMethodName().indexOf("("));
	}

	public String keySignatureExecuted(String classname, String methodName) {
		return classname + "-" + methodName;
	}

	/**
	 * Retrieve the assertions from a list of statements. It does not analyze beyond
	 * the first level (no recursive)
	 * 
	 * @param allStmtsFromClass
	 * @return
	 */
	private List<CtInvocation> filterAssertions(List<CtStatement> allStmtsFromClass) {
		return filterInvocation(allStmtsFromClass, ASSERT);
	}

	private List<CtInvocation> filterFails(List<CtStatement> allStmtsFromClass) {
		return filterInvocation(allStmtsFromClass, FAIL);
	}

	private List<CtInvocation> filterAssume(List<CtStatement> allStmtsFromClass) {
		return filterInvocation(allStmtsFromClass, ASSUME);
	}

	private List<CtInvocation> filterInvocation(List<CtStatement> allStmtsFromClass, String filterName) {
		List<CtInvocation> assertions = new ArrayList<>();
		for (CtStatement targetElement : allStmtsFromClass) {
			if (targetElement instanceof CtInvocation) {
				CtInvocation targetInvocation = (CtInvocation) targetElement;
				if (isInvWithName(targetInvocation, filterName)) {
					assertions.add(targetInvocation);
				}
			}
		}
		return assertions;
	}

	/**
	 * A helper must have an invocation
	 * 
	 * @param allStmtsFromClass
	 * @param testMethodModel
	 * @return
	 */
	private List<Helper> filterHelper(List<CtStatement> allStmtsFromClass, List<CtExecutable> calls) {
		List<Helper> helpersMined = new ArrayList<>();
		// for each statement, let's find which one is a helper
		for (CtStatement targetElement : allStmtsFromClass) {
			if (targetElement instanceof CtInvocation) {
				CtInvocation targetInvocation = (CtInvocation) targetElement;
				// a helper must be an Invocation to a something different to assertion
				if (!isAssertion(targetInvocation) && targetInvocation.getExecutable() != null
						&& targetInvocation.getExecutable().getDeclaration() != null) {

					// Let's find the called method to see if it has assertions
					CtExecutable methodDeclaration = targetInvocation.getExecutable().getDeclaration();

					if (methodDeclaration.getBody() == null) {
						continue;
					}

					if (calls.contains(methodDeclaration)) {
						log.info("Already analyzed this method");
						continue;
					}
					// All the statements from the method that is invoked
					List<CtStatement> statementsFromMethod = methodDeclaration.getBody().getElements(new LineFilter());

					// We filter the assertion present in the method body
					List<CtInvocation> assertionsFromMethod = filterAssertions(statementsFromMethod);
					// If the method body has assertions, we add them.
					if (assertionsFromMethod != null && !assertionsFromMethod.isEmpty()) {

						for (CtInvocation assertion : assertionsFromMethod) {
							Helper aHelper = new Helper(new AsAssertion(assertion));
							helpersMined.add(aHelper);
							aHelper.getCalls().add(0, targetInvocation);

						}

					}
					// Now, let's check if the body has a call to another helper.
					try {
						List<CtExecutable> previouscalls = new ArrayList<>(calls);
						previouscalls.add(methodDeclaration);
						// we find if the body calls to another helper, recursively
						List<Helper> helpersFromInvocation = filterHelper(statementsFromMethod, previouscalls);
						// we add to the results
						helpersMined.addAll(helpersFromInvocation);
						// We update the helper to include the calls.
						for (Helper aHelper : helpersFromInvocation) {
							// in the first place to keep the order of the invocation
							aHelper.getCalls().add(0, targetInvocation);
						}
					} catch (Throwable l) {
						System.out.println("error ");
					}
				}
			}

		}
		// Update the distance
		for (Helper aHelper : helpersMined) {

			// Check the distance:

			// Let's get the first invocation from the invocation chain
			CtInvocation statementFirstCall = aHelper.getCalls().get(0);
			// the class where the invocation is written
			CtClass typeInvo = statementFirstCall.getParent(CtClass.class);
			// the class where the invoked method
			CtClass typeMethod = statementFirstCall.getExecutable().getDeclaration().getParent(CtClass.class);

			// if same target, distance is zero
			if (typeInvo == typeMethod) {
				aHelper.setDistance(0);
			} else {
				// if it's subtype, lets count the distance
				if (typeInvo.isSubtypeOf(typeMethod.getReference())) {

					int distance = 0;

					CtType typeToMount = typeInvo;
					while (typeToMount != null && typeToMount != typeMethod) {
						typeToMount = (CtClass) typeToMount.getSuperclass().getDeclaration();
						distance++;
					}
					aHelper.setDistance(distance);
				} else {
					// Not subtype, the method helper is somewhere else.
					aHelper.setDistance(-1);
				}
			}
		}
		return helpersMined;
	}

	private boolean isAssertion(CtInvocation targetInvocation) {
		return isInvWithName(targetInvocation, ASSERT);
	}

	/**
	 * Return if the invocation is an assertion
	 * 
	 * @param targetInvocation
	 * @return
	 */
	private boolean isInvWithName(CtInvocation targetInvocation, String methodName) {

		String package1 = (targetInvocation.getTarget() != null) ? targetInvocation.getTarget().toString() : null;

		if (package1 != null && !package1.startsWith("org.junit") && !package1.startsWith("junit.framework"))
			return false;

		boolean isAssert = targetInvocation.getExecutable().getSimpleName().toLowerCase()
				.startsWith(methodName.toLowerCase());
		if (isAssert) {
			return true;
		}
		try {
			if (targetInvocation.getExecutable() != null
					&& targetInvocation.getExecutable().getDeclaringType() != null) {
				String name = targetInvocation.getExecutable().getDeclaringType().getQualifiedName();
				// TODO: disable for the moment
				Optional<String> testnm = this.namespace.stream().filter(e -> name.startsWith(e)).findFirst();
				if (testnm.isPresent()) {
					// log.debug("assert " + targetInvocation.getExecutable().getSimpleName() + "
					// found in " + testnm.get());
					// return true;
				}
			}

			if (targetInvocation.getTarget() != null && targetInvocation.getTarget() instanceof CtInvocation) {
				CtInvocation targetInv = (CtInvocation) targetInvocation.getTarget();
				return isInvWithName(targetInv, methodName);
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			log.error("Continue executing after " + e.getMessage());
		}
		return false;
	}

	public List<TestIntermediateAnalysisResult> getResultByTest() {
		return resultByTest;
	}

	@Override
	public void atEnd() {

		super.atEnd();
		JSonResultOriginal jsoncoverted = new JSonResultOriginal();
		JsonObject json = null;
		if (exceptionReceived == null) {
			json = jsoncoverted.toJson(ConfigurationProperties.getProperty("id"), this.projectFacade,
					this.resultByTest);

		} else {
			json = jsoncoverted.toJsonError(ConfigurationProperties.getProperty("id"), this.projectFacade,
					this.exceptionReceived);
		}

		System.out.println("rtjsonoutput: " + json);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String ppjson = gson.toJson(json);

		String out = (ConfigurationProperties.getProperty("out") != null) ? ConfigurationProperties.getProperty("out")
				: ConfigurationProperties.getProperty("workingDirectory");
		String outpath = out + File.separator + "rt_" + ConfigurationProperties.getProperty("id") + ".json";
		log.info("Saving json at \n" + outpath);
		try {
			FileWriter fw = new FileWriter(new File(outpath));
			fw.write(ppjson);
			fw.flush();
			fw.close();
		} catch (IOException e) {

			e.printStackTrace();
			log.error(e);
		}
	}

}
