package fr.inria.astor.approaches.extensions.rt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.path.CtRole;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.LineFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RtEngine extends AstorCoreEngine {
	List<SuspiciousCode> allExecutedStatements = null;

	List<TestClassificationResult> resultByTest = null;

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
		List<String> allTestCases = new ArrayList();

		List<String> allTestCasesWithoutParent = this.getProjectFacade().getProperties().getRegressionTestCases();

		for (String tc : allTestCasesWithoutParent) {
			allTestCases.add(tc);
			CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(tc);

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

		// For each class name
		for (String aNameOfTestClass : allTestCases) {

			if (notexec.contains(aNameOfTestClass)) {
				log.debug("Ignoring -not executed line- test: " + aNameOfTestClass);
				continue;
			}
			log.info("*-*-*-*----- Analying TestClass: " + aNameOfTestClass);
			CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(aNameOfTestClass);
			if (aTestModelCtClass == null) {
				log.error("No class modeled for " + aNameOfTestClass);
				continue;
			}

			List<String> testMethodsFromClass = passingCoveredTestCaseFromClass.get(aNameOfTestClass);

			if (testMethodsFromClass == null || testMethodsFromClass.isEmpty()) {
				log.error("No method executed for class " + aNameOfTestClass);
				continue;
			}

			for (String aTestMethodFromClass : testMethodsFromClass) {

				log.info("**** Analying TestMethod: " + aTestMethodFromClass);

				Optional<CtExecutableReference<?>> testMethodOp = aTestModelCtClass.getAllExecutables().stream()
						.filter(e -> e.getSimpleName().equals(aTestMethodFromClass)).findFirst();
				if (!testMethodOp.isPresent()) {
					log.error("Problem " + aTestMethodFromClass + " not found in class " + aNameOfTestClass);
					continue;
				}
				CtExecutable testMethodModel = testMethodOp.get().getDeclaration();

				if (!passingCoveredTestCaseFromClass.containsKey(aNameOfTestClass)
						|| !(passingCoveredTestCaseFromClass.get(aNameOfTestClass).contains(aTestMethodFromClass))) {

					log.debug("ignoring test " + aTestMethodFromClass + " from class " + aNameOfTestClass);
					continue;

				}
				// get all statements
				List<CtStatement> allStmtsFromClass = testMethodModel.getElements(new LineFilter());
				List<CtInvocation> allAssertionsFromTest = filterAssertions(allStmtsFromClass);
				List<Helper> allHelperInvocationFromTest = filterHelper(allStmtsFromClass, new ArrayList());
				// filter from assertions the missed fail
				List<CtInvocation> allMissedFailFromTest = filterMissedFail(allAssertionsFromTest);
				// The missed fails are removed from the assertion list (they are a
				// sub-category).
				allAssertionsFromTest.removeAll(allMissedFailFromTest);

				List<CtReturn> allSkipFromTest = filterSkips(allStmtsFromClass, testMethodModel, aTestModelCtClass);

				Classification<AsAssertion> rAssert = classifyAssertions(testMethodModel, mapLinesCovered,
						aTestModelCtClass, allAssertionsFromTest);

				Classification<Helper> rHelperAssertion = classifyHelpersAssertionExecution(aTestModelCtClass,
						allHelperInvocationFromTest, mapCacheSuspicious, true);

				Classification<Helper> rHelperCall = classifyHelpersAssertionExecution(aTestModelCtClass,
						allHelperInvocationFromTest, mapCacheSuspicious, false);

				boolean isFullR = allAssertionsFromTest.isEmpty() && allHelperInvocationFromTest.isEmpty();

				checkTwoBranches(rAssert, rAssert, rHelperCall, rHelperAssertion);
				checkTwoBranches(rHelperCall, rAssert, rHelperCall, rHelperAssertion);
				checkTwoBranches(rHelperAssertion, rAssert, rHelperCall, rHelperAssertion);

				TestClassificationResult resultTestCase = new TestClassificationResult(rAssert, rHelperAssertion,
						rHelperCall, aNameOfTestClass, aTestMethodFromClass, allMissedFailFromTest, allSkipFromTest,
						isFullR);

				resultByTest.add(resultTestCase);
			}

		}

		log.info("End processing RT");

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
			CtElement el = null;
			// case having a single argument
			if (anInvocation.getArguments().size() == 1) {
				el = (CtElement) anInvocation.getArguments().get(0);
				// case having a message as first arg
			} else if (anInvocation.getArguments().size() == 2) {
				el = (CtElement) anInvocation.getArguments().get(1);
			}

			if (el != null) {
				String contentArgumentLC = el.toString().toLowerCase();
				if (contentArgumentLC.equals("\"true\"") || contentArgumentLC.equals("\"false\"")
						|| contentArgumentLC.equals("boolean.true") || contentArgumentLC.equals("boolean.false"))
					missedFails.add(anInvocation);
			}

		}
		return missedFails;
	}

	private List<CtReturn> filterSkips(List<CtStatement> allStmtsFromClass, CtExecutable method,
			CtClass aTestModelCtClass) {

		List<CtReturn> skips = new ArrayList<>();
		for (CtStatement aStatement : allStmtsFromClass) {
			if (aStatement instanceof CtReturn) {
				// check the parent class is the test method (discarding elements from anonymous
				// classes)
				if (aTestModelCtClass.equals(aStatement.getParent(CtClass.class)) &&
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

	/**
	 * Classification of a particular test
	 * 
	 * @author Matias Martinez
	 *
	 */
	public class TestClassificationResult {
		String nameOfTestClass;
		String testMethodFromClass;
		Classification<AsAssertion> rAssert = null;
		Classification<Helper> rHelperAssertion = null;
		Classification<Helper> rHelperCall = null;
		boolean isFullR = false;
		List<CtInvocation> allMissedFailFromTest;
		List<CtReturn> allSkipFromTest;

		public TestClassificationResult(Classification<AsAssertion> rAssert, Classification<Helper> rHelperAssertion,
				Classification<Helper> rHelperCall, String aNameOfTestClass, String aTestMethodFromClass,
				List<CtInvocation> allMissedFailFromTest, List<CtReturn> allSkipFromTest, boolean isFullR) {
			super();
			this.rAssert = rAssert;
			this.rHelperAssertion = rHelperAssertion;
			this.rHelperCall = rHelperCall;
			this.allMissedFailFromTest = allMissedFailFromTest;
			this.allSkipFromTest = allSkipFromTest;
			this.nameOfTestClass = aNameOfTestClass;
			this.testMethodFromClass = aTestMethodFromClass;
			this.isFullR = isFullR;
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

		public List<CtInvocation> getAllMissedFailFromTest() {
			return allMissedFailFromTest;
		}

		public List<CtReturn> getAllSkipFromTest() {
			return allSkipFromTest;
		}

		public boolean isRotten() {
			return isFullR || !this.getClassificationAssert().getResultNotExecuted().isEmpty()
					|| !this.getClassificationHelperAssertion().getResultNotExecuted().isEmpty()
					|| !this.getAllMissedFailFromTest().isEmpty() || !this.getAllSkipFromTest().isEmpty();
		}

		public boolean isFullR() {
			return isFullR;
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
				// a halper must be an Invocation to a something different to assertion
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

	private boolean isCovered(MapList<String, Integer> executedLines, CtStatement aStatementNotInvoked,
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

	/**
	 * Return if the invocation is an assertion
	 * 
	 * @param targetInvocation
	 * @return
	 */
	private boolean isAssertion(CtInvocation targetInvocation) {
		// We should check the target.
		return targetInvocation.getExecutable().getSimpleName().toLowerCase().startsWith("assert");
	}

	public class ResultRT {

	}

	public List<TestClassificationResult> getResultByTest() {
		return resultByTest;
	}

	public JsonObject toJson() {

		JsonObject root = new JsonObject();
		root.addProperty("project", this.projectFacade.getProperties().getFixid());
		JsonObject summary = new JsonObject();
		root.add("project", summary);
		String location = ConfigurationProperties.getProperty("location");
		String commitid = executeCommand(location, "git rev-parse HEAD");
		String branch = executeCommand(location, "git rev-parse --abbrev-ref HEAD");
		String remote = executeCommand(location, "git config --get remote.origin.url");
		String projectsubfolder = ConfigurationProperties.getProperty("projectsubfolder");
		root.addProperty("commitid", commitid);

		int nrRtest = 0, nrRtAssertion = 0, nrRtHelperCall = 0, nrRttHelperAssert = 0, nrSkip = 0, nrAllMissed = 0,
				nrRtFully = 0;

		JsonArray testsAssertionArray = new JsonArray();
		root.add("tests", testsAssertionArray);
		Set<String> rTestclasses = new HashSet<>();
		for (TestClassificationResult tr : resultByTest) {
			JsonObject testjson = new JsonObject();
			testjson.addProperty("testclass", tr.getNameOfTestClass());
			testjson.addProperty("testname", tr.getTestMethodFromClass());

			boolean onerotten = false;

			nrRtFully += (tr.isFullR()) ? 1 : 0;
			// Asserts
			List<AsAssertion> notExecutedAssert = tr.getClassificationAssert().getResultNotExecuted();
			if (!notExecutedAssert.isEmpty()) {

				log.debug("-- Test  " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

				JsonArray assertionarray = new JsonArray();
				testjson.add("rotten_assertions", assertionarray);
				for (AsAssertion assertion : notExecutedAssert) {
					CtInvocation anInvocation = assertion.getCtAssertion();
					log.debug("-R-Assertion:-> " + anInvocation);
					JsonObject jsonsingleAssertion = new JsonObject();
					jsonsingleAssertion.addProperty("code", anInvocation.toString());
					jsonsingleAssertion.addProperty("line", anInvocation.getPosition().getLine());
					jsonsingleAssertion.addProperty("path", getRelativePath(anInvocation));
					jsonsingleAssertion.addProperty("inbranch", assertion.isFp());

					writeJsonLink(commitid, branch, remote, projectsubfolder, anInvocation, jsonsingleAssertion);
					assertionarray.add(jsonsingleAssertion);
					onerotten = true;
					jsonsingleAssertion.add("parent_types", getParentTypes(anInvocation));
					nrRtAssertion++;
				}
			}
			//
			List<Helper> notExecutedHelper = tr.getClassificationHelperAssertion().getResultNotExecuted();
			if (!notExecutedHelper.isEmpty()) {
				log.debug("-R Helper assertion- " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

				JsonArray helperarray = new JsonArray();
				testjson.add("rotten_helpers_assertion", helperarray);
				List<JsonObject> result = helperToJson(notExecutedHelper, commitid, branch, remote, projectsubfolder,
						false);

				if (!result.isEmpty()) {
					onerotten = true;
					for (JsonObject jsonObject : result) {
						helperarray.add(jsonObject);
					}
				}

				nrRttHelperAssert += notExecutedHelper.size();
			}
			//
			List<Helper> notExecutedHelperInvoc = tr.getClassificationHelperCall().getResultNotExecuted();
			if (!notExecutedHelperInvoc.isEmpty()) {
				System.out.println("-- R Helper call  " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

				JsonArray helperarray = new JsonArray();
				testjson.add("rotten_helpers_call", helperarray);
				List<JsonObject> result = helperToJson(notExecutedHelperInvoc, commitid, branch, remote,
						projectsubfolder, true);

				if (!result.isEmpty()) {
					onerotten = true;
					for (JsonObject jsonObject : result) {
						helperarray.add(jsonObject);
					}
				}
				nrRtHelperCall += notExecutedHelperInvoc.size();
			}

			//
			if (!tr.getAllSkipFromTest().isEmpty()) {
				JsonArray skiprarray = new JsonArray();
				testjson.add("rotten_skip", skiprarray);
				for (CtReturn skip : tr.getAllSkipFromTest()) {
					JsonObject singleSkip = new JsonObject();
					singleSkip.addProperty("code_assertion", skip.toString().toString());
					singleSkip.addProperty("line_assertion", skip.getPosition().getLine());
					singleSkip.add("parent_types", getParentTypes(skip));
					onerotten = true;
					skiprarray.add(singleSkip);
					nrSkip++;
				}
			}

			//
			if (!tr.getAllMissedFailFromTest().isEmpty()) {
				JsonArray missrarray = new JsonArray();
				testjson.add("rotten_missed", missrarray);
				for (CtInvocation missedInv : tr.getAllMissedFailFromTest()) {
					JsonObject missedJson = new JsonObject();
					missedJson.addProperty("code_assertion", missedInv.toString().toString());
					missedJson.addProperty("line_assertion", missedInv.getPosition().getLine());
					missedJson.addProperty("path_assertion", getRelativePath(missedInv));

					onerotten = true;
					missrarray.add(missedJson);
					nrAllMissed++;
				}
			}

			///
			if (onerotten) {
				testsAssertionArray.add(testjson);
				nrRtest++;
				rTestclasses.add(tr.getNameOfTestClass());

			}
		}

		summary.addProperty("remote", remote);
		summary.addProperty("localLocation", location);
		summary.addProperty("nrRtestclasses", rTestclasses.size());
		summary.addProperty("nrRtestunit", nrRtest);
		summary.addProperty("nrRtAssertion", nrRtAssertion);
		summary.addProperty("nrRtHelperCall", nrRtHelperCall);
		summary.addProperty("nrRttHelperAssert", nrRttHelperAssert);
		summary.addProperty("nrSkip", nrSkip);
		summary.addProperty("nrAllMissed", nrAllMissed);
		summary.addProperty("nrRtFully", nrRtFully);
		return root;
	}

	public void writeJsonLink(String commitid, String branch, String remote, String projectsubfolder,
			CtInvocation anInvocation, JsonObject singleAssertion) {
		if (remote != null && branch != null && commitid != null) {
			singleAssertion.addProperty("githublink", remote
					// "https://github.com/" + projectname
					+ "/tree/" + commitid// branch
					+ ((projectsubfolder != null) ? "/" + projectsubfolder : "") + "/" + getRelativePath(anInvocation)
					+ "#L" + anInvocation.getPosition().getLine());
		}
	}

	public List<JsonObject> helperToJson(List<Helper> notExecutedHelper, String commitid, String branch, String remote,
			String projectsubfolder, boolean isCall) {

		List<JsonObject> result = new ArrayList();

		for (Helper anHelper : notExecutedHelper) {
			log.debug("-Helper-> " + anHelper);
			CtInvocation ctAssertion = anHelper.getAssertion().getCtAssertion();
			JsonObject jsonsingleHelper = new JsonObject();

			JsonObject assertionjson = getJsonElement(commitid, branch, remote, projectsubfolder, ctAssertion);
			jsonsingleHelper.add("assertion", assertionjson);
			jsonsingleHelper.addProperty("inbranch", anHelper.isFp());
			JsonArray callsarray = new JsonArray();
			for (CtInvocation call : anHelper.getCalls()) {
				// callsarray.add(call.toString());
				callsarray.add(getJsonElement(commitid, branch, remote, projectsubfolder, call));
			}
			jsonsingleHelper.add("calls", callsarray);

			if (isCall) {

				if (anHelper.getCalls().size() > 0)
					writeJsonLink(commitid, branch, remote, projectsubfolder, anHelper.getCalls().get(0),
							jsonsingleHelper);

			} else {

				writeJsonLink(commitid, branch, remote, projectsubfolder, ctAssertion, jsonsingleHelper);
			}

			result.add(jsonsingleHelper);

		}
		return result;
	}

	public JsonObject getJsonElement(String commitid, String branch, String remote, String projectsubfolder,
			CtInvocation ctAssertion) {
		JsonObject jsonsingleHelper = new JsonObject();
		jsonsingleHelper.addProperty("code", ctAssertion.toString());
		jsonsingleHelper.addProperty("line", ctAssertion.getPosition().getLine());
		writeJsonLink(commitid, branch, remote, projectsubfolder, ctAssertion, jsonsingleHelper);
		return jsonsingleHelper;
	}

	private JsonArray getParentTypes(CtElement anElement) {
		JsonArray parentArray = new JsonArray();
		CtElement parent = anElement.getParent();
		while (parent != null) {
			// removing the prefix "Ct" and suffix "Impl"
			parentArray.add(cleanTypeName(parent.getClass().getSimpleName()));
			parent = parent.getParent();
			// we discard parents from package
			if (parent instanceof CtPackage) {
				break;
			}
		}

		return parentArray;
	}

	public String cleanTypeName(String parent) {
		if (parent.length() < 6) {
			return parent;
		}
		return parent.substring(2, parent.length() - 4);
	}

	public String getRelativePath(CtInvocation anInvocation) {
		return anInvocation.getPosition().getFile().getAbsolutePath().replace("./", "")
				.replace(this.getProjectFacade().getProperties().getOriginalProjectRootDir().replace("./", ""), "");
	}

	@Override
	public void atEnd() {

		super.atEnd();
		JsonObject json = toJson();
		System.out.println("rtjsonoutput: " + json);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String ppjson = gson.toJson(json);

		String out = (ConfigurationProperties.getProperty("out") != null) ? ConfigurationProperties.getProperty("out")
				: ConfigurationProperties.getProperty("workingDirectory");
		String outpath = out + File.separator + ConfigurationProperties.getProperty("id") + ".json";
		log.info("Saving json at " + outpath);
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
			// inmediate parent)
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

	private String executeCommand(String location, String command) {

		log.debug("Running command  " + command + " at " + location);
		ProcessBuilder builder = new ProcessBuilder();

		builder.command(command.split(" "));

		builder.directory(new File(location));

		try {

			Process process = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String content = "";
			String line;
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
			log.info("Command result " + content);
			return content.trim();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

}
