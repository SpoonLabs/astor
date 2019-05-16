package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.MapList;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.LineFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RtTest {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Test
	public void testRTMath70() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-location", new File("./examples/math_70_rt").getAbsolutePath());
		cs.command.put("-parameters", "includeTestInSusp:true:"//
				+ "limitbysuspicious:false:"//
				+ "maxsuspcandidates:1:"//
				+ "regressionforfaultlocalization:true:"//
				+ "considerzerovaluesusp:true:"//
				+ "disablelog:false:"//
				+ "onlympcovered:false:"//
				+ "onlympfromtest:false");
		cs.command.put("-maxgen", "0");

		main1.execute(cs.flat());

		ConfigurationProperties.setProperty("maxsuspcandidates", "100000000");

		List<String> allTestCases = new ArrayList();

		List<String> allTestCasesWithoutParent = main1.getEngine().getProjectFacade().getProperties()
				.getRegressionTestCases();

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

		assertNotNull(allTestCases);
		assertTrue(allTestCases.size() > 0);
		log.debug("# Test cases: " + allTestCases.size());

		// Let's compute the suspicious code
		List<SuspiciousCode> allExecutedStatements = main1.getEngine().calculateSuspicious();

		// key is test class, values are method (cases)
		MapList<String, String> passingCoveredTestCaseFromClass = new MapList<>();

		for (SuspiciousCode executed : allExecutedStatements) {

			for (TestCaseResult tcr : executed.getCoveredByTests()) {
				if (tcr.isCorrect() && (
				// Test class not analyzed
				!passingCoveredTestCaseFromClass.containsKey(tcr.getTestCaseClass())
						// test method not analyzed

						|| !passingCoveredTestCaseFromClass.get(tcr.getTestCaseClass()) // executed.getClassName()
								.contains(tcr.getTestCaseName())))
					passingCoveredTestCaseFromClass.add(tcr.getTestCaseClass(), tcr.getTestCaseName());
			}
		}

		// Lines of code covered grouped by test. The key is the test class name
		MapList<String, Integer> mapLinesCovered = new MapList<>();
		Map<String, SuspiciousCode> mapCacheSuspicious = new HashMap<>();
		// The test cases (method) executed by test class
		// MapList<String, String> mapTestCasesFromClasses = new MapList<>();
		//
		for (SuspiciousCode executedStatement : allExecutedStatements) {
			mapLinesCovered.add(executedStatement.getClassName(), executedStatement.getLineNumber());
			mapCacheSuspicious.put(executedStatement.getClassName() + executedStatement.getLineNumber(),
					executedStatement);
			// mapTestCasesFromClasses.add(executedStatement.getClassName(),
			// getTestCaseMethodName(executedStatement));
		}

		List<CtInvocation> resultAssertionsNotInvoked = new ArrayList<>();
		List<CtInvocation> resultAssertionsExecuted = new ArrayList<>();

		List<Helper> resultHelperNotInvoked = new ArrayList<>();
		List<Helper> resultHelperExecuted = new ArrayList<>();

		MapList<String, CtInvocation> assertionsByTest = new MapList<>();

		// For each class name
		for (String aNameOfTestClass : allTestCases) {

			log.info("*-*-*-*----- Analying TestClass: " + aNameOfTestClass);
			CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(aNameOfTestClass);
			if (aTestModelCtClass == null) {
				log.error("No class modeled for " + aNameOfTestClass);
				continue;
			}
			// assertNotNull("no modeled class " + aNameOfTestClass, aTestModelCtClass);

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

				List<CtStatement> allStmtsFromClass = testMethodModel
						.getElements(new LineFilter())/* aTestModelCtClass */;
				List<CtInvocation> allAssertionsFromTest = filterAssertions(allStmtsFromClass);

				List<Helper> allHelperInvocationFromTest = filterHelper(allStmtsFromClass);

				analyzeAssertions(testMethodModel, mapLinesCovered, aTestModelCtClass, allAssertionsFromTest,
						resultAssertionsNotInvoked, resultAssertionsExecuted);

				// TODO: it must be by test
				analyzeHelpers(aTestModelCtClass, allHelperInvocationFromTest, resultHelperNotInvoked,
						resultHelperExecuted, mapCacheSuspicious);

			}

		}
		log.info("assert not invoked " + resultAssertionsNotInvoked.size());

		for (CtInvocation ctInvocation : resultAssertionsNotInvoked) {
			System.out.println(ctInvocation.getParent(CtClass.class).getQualifiedName() + " "
					+ ctInvocation.getParent(CtMethod.class).getSimpleName() + " " + ctInvocation);
		}

		log.info("assert invoked " + resultAssertionsExecuted.size());

		log.info("helper not invoked " + resultHelperNotInvoked.size());

		log.info("helper invoked " + resultHelperExecuted.size());

		log.info(assertionsByTest.keySet());

		for (String testName : assertionsByTest.keySet()) {
			List<CtInvocation> assertionsFromTest = assertionsByTest.get(testName);

			// log.info("\n" + testName + "(" + assertionsFromTest.size() + "): " +
			// assertionsFromTest);

		}
	}

	private void analyzeHelpers(CtClass aTestModelCtClass, List<Helper> allHelperInvocationFromTest,
			List<Helper> resultAssertionsNotInvoked, List<Helper> resultAssertionsExecuted,
			Map<String, SuspiciousCode> cacheSuspicious) {

		for (Helper aHelper : allHelperInvocationFromTest) {

			CtInvocation assertion = aHelper.getAssertion();
			CtClass ctclassFromAssert = assertion.getParent(CtClass.class);

			boolean covered = isCovered(cacheSuspicious, assertion, aTestModelCtClass, ctclassFromAssert);
			if (!covered) {
				resultAssertionsNotInvoked.add(aHelper);
			} else {
				resultAssertionsExecuted.add(aHelper);
			}
		}

	}

	private boolean isCovered(Map<String, SuspiciousCode> cacheSuspicious, CtInvocation assertion,
			CtClass aTestModelCtClass, CtClass ctclassFromAssert) {

		// the location of the assertion contained in the helper
		String keyLocationAssertion = ctclassFromAssert.getQualifiedName() + assertion.getPosition().getLine();

		if (cacheSuspicious.containsKey(keyLocationAssertion)) {
			// Assertion was covered, let's check if by the current test case
			SuspiciousCode cover = cacheSuspicious.get(keyLocationAssertion);

			for (TestCaseResult tr : cover.getCoveredByTests()) {
				if (tr.getTestCaseCompleteName().equals(aTestModelCtClass.getQualifiedName())) {
					return true;
				}
			}

		}

		return false;
	}

	public void analyzeAssertions(CtExecutable methodOfAssertment, MapList<String, Integer> linesCovered,
			CtClass aTestModelCtClass, List<CtInvocation> allAssertionsFromTest,
			List<CtInvocation> resultAssertionsNotInvoked, List<CtInvocation> resultAssertionsExecuted) {
		// For each assert
		for (CtInvocation anAssertFromTest : allAssertionsFromTest) {

			boolean covered = isCovered(linesCovered, anAssertFromTest, aTestModelCtClass);
			if (!covered) {
				// assertionsByTest.add(testCaseKey, anAssertFromTest);
				// isCovered(linesCovered, anAssertFromTest, aTestModelCtClass);
				// log.debug("test class name: " + aTestModelCtClass.getQualifiedName());
				resultAssertionsNotInvoked.add(anAssertFromTest);
				log.info("Not covered: " + anAssertFromTest + " at " + aTestModelCtClass.getQualifiedName());
			} else {
				resultAssertionsExecuted.add(anAssertFromTest);
			}
		}
	}

	public class Helper {

		List<CtInvocation> calls = new ArrayList();
		CtInvocation assertion = null;

		public Helper(CtInvocation assertion) {
			super();
			this.assertion = assertion;
		}

		public List<CtInvocation> getCalls() {
			return calls;
		}

		public void setCalls(List<CtInvocation> calls) {
			this.calls = calls;
		}

		public CtInvocation getAssertion() {
			return assertion;
		}

		public void setAssertion(CtInvocation assertion) {
			this.assertion = assertion;
		}

		@Override
		public String toString() {
			return "Helper [calls=" + calls + ", assertion=" + assertion + "]";
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
	 * @return
	 */
	private List<Helper> filterHelper(List<CtStatement> allStmtsFromClass) {
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
					List<CtStatement> statementsFromMethod = methodDeclaration.getBody().getStatements();
					List<CtInvocation> assertionsFromMethod = filterAssertions(statementsFromMethod);
					// If the method body has assertions, we add them.
					if (assertionsFromMethod != null && !assertionsFromMethod.isEmpty()) {

						for (CtInvocation assertion : assertionsFromMethod) {
							Helper aHelper = new Helper(assertion);
							helpersMined.add(aHelper);
							aHelper.getCalls().add(0, targetInvocation);
						}

					} // else {

					// we find if the body calls to another helper
					List<Helper> helpersFromInvocation = filterHelper(statementsFromMethod);
					// we add to the results
					helpersMined.addAll(helpersFromInvocation);
					// We update the helper to include the calls.
					for (Helper aHelper : helpersFromInvocation) {
						// in the first place to keep the order of the invocation
						aHelper.getCalls().add(0, targetInvocation);
					}
					// }
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
		if (linesOfTestCase.contains(aStatementNotInvoked.getPosition().getLine())
				|| linesOfTestCase.contains(aStatementNotInvoked.getPosition().getEndLine())) {
			return true;
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

}
