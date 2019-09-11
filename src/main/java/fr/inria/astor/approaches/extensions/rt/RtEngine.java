package fr.inria.astor.approaches.extensions.rt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
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

	List<SuspiciousCode> allExecutedStatements = null;

	public List<TestInspectionResult> resultByTest = new ArrayList<>();

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

	@Override
	public void startEvolution() throws Exception {

		if (!ConfigurationProperties.getPropertyBool("skipanalysis")) {
			RuntimeInformation ri = computeDynamicInformation();
			analyzeTestSuiteExecution(ri);
		}

	}

	public RuntimeInformation computeDynamicInformation() {
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
			throw new IllegalStateException("No test to execute");
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
			// throw new IllegalStateException("Not executed tests");
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

				TestInspectionResult resultTestCase = processTest(aTestMethodFromClass, aNameOfTestClass,
						aTestModelCtClass, runtimeinfo);
				if (resultTestCase != null) {
					resultByTest.add(resultTestCase);
				}
			}
		}
	}

	public TestInspectionResult processTest(String aTestMethodFromClass, String aNameOfTestClass,
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

		List<CtInvocation> allAssertionsFromTest = filterAssertions(allStmtsFromClass);
		List<Helper> allHelperInvocationFromTest = filterHelper(allStmtsFromClass, new ArrayList());
		// filter from assertions the missed fail
		List<CtInvocation> allMissedFailFromTest = filterMissedFail(allAssertionsFromTest);
		List<CtInvocation> allAssertionTrueromTest = filterAssertionTrue(allAssertionsFromTest);

		// The missed fails are removed from the assertion list (they are a
		// sub-category).
		// allAssertionsFromTest.removeAll(allMissedFailFromTest);

		List<CtReturn> allSkipFromTest = filterSkips(allStmtsFromClass, testMethodModel, allClasses);

		Classification<AsAssertion> rMissing = classifyAssertions(testMethodModel, runtimeinfo.mapLinesCovered,
				aTestModelCtClass, allMissedFailFromTest);

		chechInsideTry(rMissing.resultExecuted, runtimeinfo.mapLinesCovered, aTestModelCtClass);
		chechInsideTry(rMissing.resultNotExecuted, runtimeinfo.mapLinesCovered, aTestModelCtClass);

		Classification<AsAssertion> rAssert = classifyAssertions(testMethodModel, runtimeinfo.mapLinesCovered,
				aTestModelCtClass, allAssertionsFromTest);

		Classification<Helper> rHelperCall = classifyHelpersAssertionExecution(aTestModelCtClass,
				allHelperInvocationFromTest, runtimeinfo.mapCacheSuspicious, false);

		Classification<Helper> rHelperAssertion = classifyHelpersAssertionExecution(aTestModelCtClass,
				allHelperInvocationFromTest, runtimeinfo.mapCacheSuspicious, true);

		if (rHelperAssertion.getResultExecuted().isEmpty() && rHelperCall.getResultExecuted().isEmpty()
				&& rAssert.getResultExecuted().isEmpty()) {
			boolean anyExecuted = checkAnyStatementExecuted(allStmtsFromClass, runtimeinfo.mapLinesCovered,
					aTestModelCtClass);
			// If any statement in the test code was executed, we return.
			if (!anyExecuted) {
				log.info("NO test element executed for test " + aTestMethodFromClass + ", class " + aNameOfTestClass
						+ " any executed: " + anyExecuted);

				return null;
			}

		}

		checkTwoBranches(rAssert, rAssert, rHelperCall, rHelperAssertion);
		checkTwoBranches(rHelperCall, rAssert, rHelperCall, rHelperAssertion);
		checkTwoBranches(rHelperAssertion, rAssert, rHelperCall, rHelperAssertion);

		TestInspectionResult resultTestCase = new TestInspectionResult(rAssert, rHelperAssertion, rHelperCall,
				aNameOfTestClass, aTestMethodFromClass, testMethodModel, rMissing, allSkipFromTest, expectException,
				allExpectedExceptionFromTest);

		return resultTestCase;

	}

	private List<CtInvocation> filterAssertionTrue(List<CtInvocation> allAssertionsFromTest) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean checkAnyStatementExecuted(List<CtStatement> allStmtsFromClass,
			MapList<String, Integer> mapLinesCovered, CtClass aTestModelCtClass) {

		for (CtStatement statement : allStmtsFromClass) {
			boolean covered = isCovered(mapLinesCovered, statement, aTestModelCtClass);
			if (covered) {
				return true;
			}
		}
		return false;

	}

	private void chechInsideTry(List<AsAssertion> allMissedFailFromTest, MapList<String, Integer> executedLines,
			CtClass parentClass) {

		for (AsAssertion aMissAssertion : allMissedFailFromTest) {

			analyzeMissingAssertion(executedLines, parentClass, aMissAssertion);

		}

	}

	public void analyzeMissingAssertion(MapList<String, Integer> executedLines, CtClass parentClass,
			AsAssertion aMissAssertion) {
		CtTry parentTry = aMissAssertion.getCtAssertion().getParent(CtTry.class);
		if (parentTry != null) {
			for (CtCatch aCatch : parentTry.getCatchers()) {
				CtBlock block = aCatch.getBody();
				if (block != null && block.getStatements().size() > 0) {
					for (CtStatement anStatementInBlock : block.getStatements()) {

						boolean isCover = isCovered(executedLines, anStatementInBlock, parentClass);
						if (isCover) {
							aMissAssertion.setFp(true);
							return;
						}

					}
				} else {
					CtBlock pblock = parentTry.getParent(CtBlock.class);
					int indexTry = pblock.getStatements().indexOf(parentTry);
					if (indexTry >= 0 && indexTry + 1 < pblock.getStatements().size()) {
						CtStatement stNext = pblock.getStatements().get(indexTry + 1);
						boolean isCover = isCovered(executedLines, stNext, parentClass);
						if (isCover) {
							aMissAssertion.setFp(true);
							return;
						}
					}

				}
			}
		}
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
			CtElement argument = null;
			// case having a single argument
			if (anInvocation.getArguments().size() == 1) {
				argument = (CtElement) anInvocation.getArguments().get(0);
				// case having a message as first arg
			} else if (anInvocation.getArguments().size() == 2) {
				argument = (CtElement) anInvocation.getArguments().get(1);
			}

			// if(isInvWithName(anInvocation, "assertTrue")) {

			// }

			if (argument != null) {
				String contentArgumentLC = argument.toString().toLowerCase();
				if (contentArgumentLC.equals("\"true\"") || contentArgumentLC.equals("\"false\"")
						|| contentArgumentLC.equals("true") || contentArgumentLC.equals("false")
						|| contentArgumentLC.equals("boolean.true") || contentArgumentLC.equals("boolean.false"))
					missedFails.add((anInvocation));
			}

		}
		return missedFails;
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
						!method.getBody().getLastStatement().equals(aStatement)
						// check that statement is not inside an element that is the last one
						&& !method.getBody().getLastStatement().equals(aStatement.getParent(new LineFilter()))

				) {
					skips.add((CtReturn) aStatement);
				}
			}
		}
		return skips;
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

	/**
	 * Classification of a particular test
	 * 
	 * @author Matias Martinez
	 *
	 */
	public class TestInspectionResult {
		String nameOfTestClass;
		String testMethodFromClass;
		Classification<AsAssertion> rAssert = null;
		Classification<Helper> rHelperAssertion = null;
		Classification<Helper> rHelperCall = null;
		Classification<AsAssertion> allMissedFailFromTest;
		List<CtReturn> allSkipFromTest;
		CtExecutable testMethodModel;
		List<String> expectException;
		List<CtInvocation> allExpectedExceptionFromTest;

		public TestInspectionResult(Classification<AsAssertion> rAssert, Classification<Helper> rHelperAssertion,
				Classification<Helper> rHelperCall, String aNameOfTestClass, String aTestMethodFromClass,
				CtExecutable testMethodModel, Classification<AsAssertion> allMissedFailFromTest,
				List<CtReturn> allSkipFromTest, List<String> expectException,
				List<CtInvocation> allExpectedExceptionFromTest) {
			super();
			this.rAssert = rAssert;
			this.rHelperAssertion = rHelperAssertion;
			this.rHelperCall = rHelperCall;
			this.testMethodModel = testMethodModel;
			this.allMissedFailFromTest = allMissedFailFromTest;
			this.allSkipFromTest = allSkipFromTest;
			this.nameOfTestClass = aNameOfTestClass;
			this.testMethodFromClass = aTestMethodFromClass;
			this.expectException = expectException;
			this.allExpectedExceptionFromTest = allExpectedExceptionFromTest;
		}

		public Classification<AsAssertion> getClassificationAssert() {
			return rAssert;
		}

		public Classification<Helper> getClassificationHelperAssertion() {
			return rHelperAssertion;
		}

		public Classification<Helper> getClassificationHelperCall() {
			return rHelperCall;
		}

		public String getNameOfTestClass() {
			return nameOfTestClass;
		}

		public String getTestMethodFromClass() {
			return testMethodFromClass;
		}

		public Classification<AsAssertion> getAllMissedFailFromTest() {
			return allMissedFailFromTest;
		}

		public List<CtReturn> getAllSkipFromTest() {
			return allSkipFromTest;
		}

		public boolean isRotten() {
			return !this.getClassificationAssert().getResultNotExecuted().isEmpty()
					|| !this.getClassificationHelperAssertion().getResultNotExecuted().isEmpty()
					|| !this.getAllMissedFailFromTest().getResultNotExecuted().isEmpty()
					|| !this.getAllSkipFromTest().isEmpty();
		}

		public boolean isSmokeTest() {
			return expectException.isEmpty() && allExpectedExceptionFromTest.isEmpty()
					&& rAssert.resultExecuted.isEmpty() && rAssert.resultNotExecuted.isEmpty()
					&& rHelperCall.resultExecuted.isEmpty() && rHelperCall.resultNotExecuted.isEmpty()
					&& rHelperAssertion.resultExecuted.isEmpty() && rHelperAssertion.resultNotExecuted.isEmpty();
		}

		public CtExecutable getTestMethodModel() {
			return testMethodModel;
		}

		public void setTestMethodModel(CtExecutable testMethodModel) {
			this.testMethodModel = testMethodModel;
		}

		public List<String> getExpectException() {
			return expectException;
		}

		public void setExpectException(List<String> expectException) {
			this.expectException = expectException;
		}

		public List<CtInvocation> getAllExpectedExceptionFromTest() {
			return allExpectedExceptionFromTest;
		}

		public void setAllExpectedExceptionFromTest(List<CtInvocation> allExpectedExceptionFromTest) {
			this.allExpectedExceptionFromTest = allExpectedExceptionFromTest;
		}

		public TestRottenAnalysisResult generateFinalResult() {
			List<CtReturn> allSkipFromTest2 = this.getAllSkipFromTest();

			List<Helper> notComplexHelperCallComplex = new ArrayList();
			List<Helper> notComplexHelperAssertComplex = new ArrayList();
			List<AsAssertion> notComplexAssertComplex = new ArrayList();
			//
			List<Helper> resultNotExecutedHelperCallComplex = new ArrayList<>();
			List<Helper> resultNotExecutedHelperAssertComplex = new ArrayList<>();
			List<AsAssertion> resultNotExecutedAssertComplex = new ArrayList<>();

			//
			List<Helper> resultNotExecutedHelperCall = this.getClassificationHelperCall().getResultNotExecuted();
			List<Helper> resultNotExecutedHelperAssertion = this.getClassificationHelperAssertion()
					.getResultNotExecuted();
			List<AsAssertion> resultNotExecutedAssertion = this.getClassificationAssert().getResultNotExecuted();

			// Skips
			if (allSkipFromTest2 != null && allSkipFromTest2.size() > 0) {
				List<Skip> skipss = new ArrayList<>();
				for (CtReturn aReturn : allSkipFromTest2) {
					Skip aSkip = new Skip(aReturn);
					aSkip.notExecutedTestElements.addAll(resultNotExecutedHelperCall);
					// Not necessary
					// aSkip.notExecutedTestElements.addAll(resultNotExecutedHelperAssertion);
					aSkip.notExecutedTestElements.addAll(resultNotExecutedAssertion);

					skipss.add(aSkip);

				}
				return new TestRottenAnalysisResult(skipss);
			}

			// Executed
			List<AsAssertion> allMissedFailFromTest2 = this.getAllMissedFailFromTest().getResultExecuted();

			boolean smokeTest = isSmokeTest();

			//
			classifyComplexHelper(notComplexHelperCallComplex, resultNotExecutedHelperCallComplex,
					resultNotExecutedHelperCall);
			classifyComplexHelper(notComplexHelperAssertComplex, resultNotExecutedHelperAssertComplex,
					resultNotExecutedHelperAssertion);
			classifyComplexAssert(notComplexAssertComplex, resultNotExecutedAssertComplex, resultNotExecutedAssertion);

			List<CtInvocation> allAssertionsFromTest = getTestMethodModel().getBody()
					.getElements(new TypeFilter<>(CtInvocation.class));

			return new TestRottenAnalysisResult(notComplexHelperCallComplex, notComplexHelperAssertComplex,
					notComplexAssertComplex, smokeTest, allMissedFailFromTest2, resultNotExecutedHelperCallComplex,
					resultNotExecutedHelperAssertComplex, resultNotExecutedAssertComplex, allAssertionsFromTest);

		}

		public void classifyComplexHelper(List<Helper> notComplex, List<Helper> resultNotExecutedHelperCallComplex,
				List<Helper> resultNotExecutedAssertion) {
			for (Helper testElement : resultNotExecutedAssertion) {

				CtIf parentIf = testElement.getElement().getParent(CtIf.class);
				if (parentIf != null) {
					// complex
					resultNotExecutedHelperCallComplex.add(testElement);
				} else {
					// not complex
					notComplex.add(testElement);

				}
			}
		}

		/**
		 * If the element has an IF parent, then goes to complex list, otherwise to the
		 * no complex.
		 * 
		 * @param notComplex
		 * @param resultNotExecutedHelperCallComplex
		 * @param resultNotExecutedAssertion
		 */
		public void classifyComplexAssert(List<AsAssertion> notComplex,
				List<AsAssertion> resultNotExecutedHelperCallComplex, List<AsAssertion> resultNotExecutedAssertion) {
			for (AsAssertion testElement : resultNotExecutedAssertion) {

				CtIf parentIf = testElement.getElement().getParent(CtIf.class);
				if (parentIf != null) {
					// complex
					resultNotExecutedHelperCallComplex.add(testElement);
				} else {
					// not complex
					notComplex.add(testElement);

				}
			}
		}

		@Override
		public String toString() {
			return "TestClassificationResult [nameOfTestClass=" + nameOfTestClass + ", testMethodFromClass="
					+ testMethodFromClass + "]";
		}

	}

	public class TestRottenAnalysisResult {

		public List<Helper> fullRottenHelperCall = Collections.EMPTY_LIST;
		public List<Helper> fullRottenHelperAssert = Collections.EMPTY_LIST;
		public List<AsAssertion> fullRottenAssert = Collections.EMPTY_LIST;
		public boolean smokeTest = false;
		public List<AsAssertion> missed = Collections.EMPTY_LIST;
		public List<Skip> skip = Collections.EMPTY_LIST;
		public List<Helper> contextHelperCall = Collections.EMPTY_LIST;
		public List<Helper> contextHelperAssertion = Collections.EMPTY_LIST;
		public List<AsAssertion> contextAssertion = Collections.EMPTY_LIST;

		List<CtInvocation> otherMethodInvocations = Collections.EMPTY_LIST;

		public TestRottenAnalysisResult(
				//
				List<Helper> fullRottenHelperCall, List<Helper> fullRottenHelperAssert, //
				List<AsAssertion> fullRottenAssert, //
				boolean smokeTest, List<AsAssertion> missed, //
				// List<Skip> skip,
				List<Helper> contextHelperCall, List<Helper> contextHelperAssertion, List<AsAssertion> contextAssertion,
				List<CtInvocation> allAssertionsFromTest) {
			super();
			this.fullRottenHelperCall = fullRottenHelperCall;
			this.fullRottenHelperAssert = fullRottenHelperAssert;
			this.fullRottenAssert = fullRottenAssert;
			this.smokeTest = smokeTest;
			this.missed = missed;
			// this.skip = skip;
			this.contextHelperCall = contextHelperCall;
			this.contextHelperAssertion = contextHelperAssertion;
			this.contextAssertion = contextAssertion;
			this.otherMethodInvocations = allAssertionsFromTest;
		}

		public TestRottenAnalysisResult(List<Skip> skip) {
			this.skip = skip;
		}

		public List<TestElement> getFullRotten() {
			List<TestElement> allRT = new ArrayList<>();
			allRT.addAll(this.fullRottenAssert);
			allRT.addAll(this.fullRottenHelperCall);
			allRT.addAll(this.fullRottenHelperAssert);
			return allRT;
		}

		public List<CtInvocation> getOtherMethodInvocations() {
			return otherMethodInvocations;
		}

		public void setOtherMethodInvocations(List<CtInvocation> otherMethodInvocations) {
			this.otherMethodInvocations = otherMethodInvocations;
		}
	}

	public class Classification<T> {

		protected List<T> resultNotExecuted = new ArrayList<>();
		protected List<T> resultExecuted = new ArrayList<>();

		public List<T> getResultNotExecuted() {
			return resultNotExecuted;
		}

		public List<T> getResultExecuted() {
			return resultExecuted;
		}

	}

	private Classification<Helper> classifyHelpersAssertionExecution(CtClass aTestModelCtClass,
			List<Helper> allHelperInvocationFromTest, Map<String, SuspiciousCode> cacheSuspicious,
			boolean checkAssertion) {

		Classification<Helper> result = new Classification<>();
		for (Helper aHelper : allHelperInvocationFromTest) {

			CtInvocation assertion = (checkAssertion) ? aHelper.getAssertion().getCtAssertion()
					: aHelper.getCalls().get(0);
			CtClass ctclassFromAssert = assertion.getParent(CtClass.class);

			boolean covered = isCovered(cacheSuspicious, assertion, aTestModelCtClass, ctclassFromAssert);
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

	@Deprecated
	private Classification<Helper> classifyHelpersInvocationExecution(CtClass aTestModelCtClass,
			List<Helper> allHelperInvocationFromTest, Map<String, SuspiciousCode> cacheSuspicious) {

		Classification<Helper> result = new Classification<>();
		for (Helper aHelper : allHelperInvocationFromTest) {

			CtInvocation firstHelperInvocation = aHelper.getCalls().get(0);
			CtClass ctclassFromAssert = firstHelperInvocation.getParent(CtClass.class);

			boolean covered = isCovered(cacheSuspicious, firstHelperInvocation, aTestModelCtClass, ctclassFromAssert);
			if (!covered) {
				result.getResultNotExecuted().add(aHelper);
			} else {
				result.getResultExecuted().add(aHelper);
			}
		}
		return result;
	}

	private boolean isCovered(Map<String, SuspiciousCode> cacheSuspicious, CtElement elementToCheck,
			CtClass aTestModelCtClass, CtClass ctclassFromAssert) {

		// the location of the assertion contained in the helper
		int init = elementToCheck.getPosition().getLine();
		int end = elementToCheck.getPosition().getEndLine();
		for (int i = init; i <= end; i++) {
			String keyLocationAssertion = ctclassFromAssert.getQualifiedName() + i;

			if (checkCoverLine(cacheSuspicious, aTestModelCtClass, keyLocationAssertion))
				return true;

		}
		return false;

	}

	public boolean checkCoverLine(Map<String, SuspiciousCode> cacheSuspicious, CtClass aTestModelCtClass,
			String keyLocationAssertion) {
		if (cacheSuspicious.containsKey(keyLocationAssertion)) {
			// Assertion was covered, let's check if by the current test case
			SuspiciousCode cover = cacheSuspicious.get(keyLocationAssertion);

			for (TestCaseResult tr : cover.getCoveredByTests()) {
				if (tr.getTestCaseClass().equals(aTestModelCtClass.getQualifiedName())) {
					return true;
				}
			}

		}
		return false;
	}

	public Classification<AsAssertion> classifyAssertions(CtExecutable methodOfAssertment,
			MapList<String, Integer> linesCovered, CtClass aTestModelCtClass,
			List<CtInvocation> allAssertionsFromTest) {
		Classification<AsAssertion> result = new Classification<>();
		// For each assert
		for (CtInvocation anAssertFromTest : allAssertionsFromTest) {

			boolean covered = isCovered(linesCovered, anAssertFromTest, aTestModelCtClass);
			if (!covered) {

				result.getResultNotExecuted().add(new AsAssertion(anAssertFromTest));
				log.info("Not covered: " + anAssertFromTest + " at " + aTestModelCtClass.getQualifiedName());
				isCovered(linesCovered, anAssertFromTest, aTestModelCtClass);
			} else {
				result.getResultExecuted().add(new AsAssertion(anAssertFromTest));
			}
		}
		return result;
	}

	public class RuntimeInformation {
		List<String> allTestCases = new ArrayList();

		List<String> allTestCasesWithoutParent = null;
		MapList<String, Integer> mapLinesCovered = new MapList<>();
		Map<String, SuspiciousCode> mapCacheSuspicious = new HashMap<>();
		MapList<String, String> passingCoveredTestCaseFromClass = new MapList<>();
		List<String> notexec = new ArrayList<>();

		public RuntimeInformation(List<String> allTestCase, List<String> allTestCasesWithoutParent,
				MapList<String, Integer> mapLinesCovered, Map<String, SuspiciousCode> mapCacheSuspicious,
				MapList<String, String> passingCoveredTestCaseFromClass, List<String> notexec) {
			super();
			this.allTestCases = allTestCase;
			this.allTestCasesWithoutParent = allTestCasesWithoutParent;
			this.mapLinesCovered = mapLinesCovered;
			this.mapCacheSuspicious = mapCacheSuspicious;
			this.passingCoveredTestCaseFromClass = passingCoveredTestCaseFromClass;
			this.notexec = notexec;
		}

	}

	public abstract class TestElement {
		private boolean fp = false;

		public boolean isFp() {
			return fp;
		}

		public void setFp(boolean fp) {
			this.fp = fp;
		}

		public abstract CtElement getElement();
	}

	public class Skip {

		CtReturn executedReturn;
		public List<TestElement> notExecutedTestElements = new ArrayList<>();

		public Skip(CtReturn executedReturn) {
			super();
			this.executedReturn = executedReturn;
		}

	}

	public class AsAssertion extends TestElement {

		CtInvocation assertion = null;

		public AsAssertion(CtInvocation assertion) {
			super();
			this.assertion = assertion;
		}

		public CtInvocation getCtAssertion() {
			return assertion;
		}

		public void setAssertion(CtInvocation assertion) {
			this.assertion = assertion;
		}

		@Override
		public String toString() {
			return assertion.toString();
		}

		@Override
		public CtElement getElement() {

			return assertion;
		}
	}

	public class Helper extends TestElement {

		List<CtInvocation> calls = new ArrayList();
		AsAssertion assertion = null;
		public boolean unexecutedAssert = false;
		public boolean unexecutedCall = false;

		public Helper(AsAssertion assertion) {
			super();
			this.assertion = assertion;
		}

		public List<CtInvocation> getCalls() {
			return calls;
		}

		public void setCalls(List<CtInvocation> calls) {
			this.calls = calls;
		}

		public AsAssertion getAssertion() {
			return assertion;
		}

		public void setAssertion(AsAssertion assertion) {
			this.assertion = assertion;
		}

		@Override
		public String toString() {
			return "Helper [calls=" + calls + ", assertion=" + assertion + "]";
		}

		@Override
		public CtElement getElement() {

			return (this.calls.size() > 0) ? this.calls.get(0) : null;
		}
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
		List<CtInvocation> assertions = new ArrayList<>();
		for (CtStatement targetElement : allStmtsFromClass) {
			if (targetElement instanceof CtInvocation) {
				CtInvocation targetInvocation = (CtInvocation) targetElement;
				if (isAssertion(targetInvocation)) {
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

					// Let's find the called method
					CtExecutable methodDeclaration = targetInvocation.getExecutable().getDeclaration();

					if (methodDeclaration.getBody() == null) {
						continue;
					}

					if (calls.contains(methodDeclaration)) {
						log.info("Already analyzed this method");
						continue;
					}

					List<CtStatement> statementsFromMethod = methodDeclaration.getBody().getElements(new LineFilter());
					// methodDeclaration.getBody().getStatements();
					List<CtInvocation> assertionsFromMethod = filterAssertions(statementsFromMethod);
					// If the method body has assertions, we add them.
					if (assertionsFromMethod != null && !assertionsFromMethod.isEmpty()) {

						for (CtInvocation assertion : assertionsFromMethod) {
							Helper aHelper = new Helper(new AsAssertion(assertion));
							helpersMined.add(aHelper);
							aHelper.getCalls().add(0, targetInvocation);
						}

					} // else {

					try {
						List<CtExecutable> previouscalls = new ArrayList<>(calls);
						previouscalls.add(methodDeclaration);
						// we find if the body calls to another helper
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
		return helpersMined;
	}

	private boolean isCovered(MapList<String, Integer> executedLines, CtElement aStatementNotInvoked,
			CtClass parentClass) {

		CtClass newParentClass = getTopParentClass(aStatementNotInvoked);

		String className = newParentClass.getQualifiedName();// parentClass.getQualifiedName();
		if (!executedLines.containsKey(className))
			return false;
		List<Integer> linesOfTestCase = executedLines.get(className);
		int start = aStatementNotInvoked.getPosition().getLine();
		int end = aStatementNotInvoked.getPosition().getEndLine();

		for (int i = start; i <= end; i++) {
			if (linesOfTestCase.contains(i)) {
				return true;
			}
		}

		return false;
	}

	public CtClass getTopParentClass(CtElement aStatementNotInvoked) {
		CtClass parent = aStatementNotInvoked.getParent(CtClass.class);
		if (parent != null) {

			CtClass top = getTopParentClass(parent);
			if (top != null)
				return top;
			else
				return parent;
		}
		return null;

	}

	private boolean isAssertion(CtInvocation targetInvocation) {
		return isInvWithName(targetInvocation, "assert");
	}

	/**
	 * Return if the invocation is an assertion
	 * 
	 * @param targetInvocation
	 * @return
	 */
	private boolean isInvWithName(CtInvocation targetInvocation, String methodName) {
		log.debug("assert " + targetInvocation.getExecutable().getSimpleName());
		boolean isAssert = targetInvocation.getExecutable().getSimpleName().toLowerCase().startsWith(methodName);
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
				return isAssertion(targetInv);
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			log.error("Continue executing after " + e.getMessage());
		}
		return false;
	}

	public class ResultRT {

	}

	public List<TestInspectionResult> getResultByTest() {
		return resultByTest;
	}

	@Override
	public void atEnd() {

		super.atEnd();
		JSonResultOriginal jsoncoverted = new JSonResultOriginal();
		JsonObject json = jsoncoverted.toJson(this.projectFacade, this.resultByTest);
		System.out.println("rtjsonoutput: " + json);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String ppjson = gson.toJson(json);

		String out = (ConfigurationProperties.getProperty("out") != null) ? ConfigurationProperties.getProperty("out")
				: ConfigurationProperties.getProperty("workingDirectory");
		String outpath = out + File.separator + ConfigurationProperties.getProperty("id") + ".json";
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

	public void checkTwoBranches(Classification<? extends TestElement> elementsToClassify,
			Classification<? extends TestElement> rAssertions, Classification<? extends TestElement> rHelperCall,
			Classification<Helper> rHelperAssertion) {

		for (TestElement target : elementsToClassify.resultNotExecuted) {

			CtElement invocation = (target instanceof Helper && ((Helper) target).unexecutedAssert)
					? ((Helper) target).getAssertion().getElement()
					: target.getElement();
			CtIf parentif = null;
			boolean inThen = false;
			// Let's retrieve the parent if (I dont use getParent because I want the
			// Immediate parent)
			if (invocation.getParent() instanceof CtIf) {
				parentif = (CtIf) invocation.getParent();
				inThen = invocation.getRoleInParent().equals(CtRole.THEN);
			} else {

				if (invocation.getParent() instanceof CtBlock
						&& (invocation.getParent().getRoleInParent().equals(CtRole.THEN)
								|| invocation.getParent().getRoleInParent().equals(CtRole.ELSE))) {

					parentif = (CtIf) invocation.getParent().getParent();
					inThen = invocation.getParent().getRoleInParent().equals(CtRole.THEN);
				}
			}
			//
			if (parentif != null) {
				CtStatement toAnalyze = inThen ? parentif.getElseStatement() : parentif.getThenStatement();

				// other statements in the other branch
				List<CtStatement> stms = (toAnalyze instanceof CtBlock) ? ((CtBlock) toAnalyze).getStatements()
						: Collections.singletonList(toAnalyze);

				// let's check if exist

				for (CtStatement anStatement : stms) {
					// let's check if the other branch has executed assertions/helpers
					boolean exist = rAssertions.getResultExecuted().stream().filter(e -> e.getElement() == anStatement)
							.findFirst().isPresent();

					// helper
					exist = exist || rHelperCall.getResultExecuted().stream().filter(e -> e.getElement() == anStatement)
							.findFirst().isPresent();

					// Assertion executed by a helper
					exist = exist || rHelperAssertion.getResultExecuted().stream()
							.filter(e -> e.getAssertion().getCtAssertion() == anStatement).findFirst().isPresent();

					if (exist) {
						target.setFp(true);
						log.debug("Found executed in the other branch");
						break;
					}
				}

			}
		}

	}

}
