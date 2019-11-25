package fr.inria.astor.approaches.extensions.rt.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.extensions.rt.RtEngine;
import fr.inria.astor.approaches.extensions.rt.elements.AsAssertion;
import fr.inria.astor.approaches.extensions.rt.elements.Helper;
import fr.inria.astor.approaches.extensions.rt.elements.Skip;
import fr.inria.astor.approaches.extensions.rt.elements.TestElement;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtTry;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * Classification of a particular test
 * 
 * @author Matias Martinez
 *
 */
public class TestIntermediateAnalysisResult {
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	boolean onlyAssumeExecuted = false;
	List<CtInvocation> allAssumesFromTest = null;
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
	Classification<AsAssertion> rRedundantAssertion;
	List<CtInvocation> allOtherMIFromTest;
	List<CtInvocation> allFailsFromTest;

	public TestIntermediateAnalysisResult(RtEngine rtEngine, boolean onlyAssumeExecuted, List<CtInvocation> allAssumesFromTest,
			Classification<AsAssertion> rAssert, Classification<Helper> rHelperAssertion,
			Classification<Helper> rHelperCall, String aNameOfTestClass, String aTestMethodFromClass,
			CtExecutable testMethodModel, Classification<AsAssertion> allMissedFailFromTest,
			Classification<AsAssertion> rRedundantAssertion, List<CtReturn> allSkipFromTest,
			List<String> expectException, List<CtInvocation> allExpectedExceptionFromTest,
			List<CtInvocation> allMIFromTest, List<CtInvocation> allFailsFromTest) {
		super();
		this.onlyAssumeExecuted = onlyAssumeExecuted;
		this.allAssumesFromTest = allAssumesFromTest;
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
		this.rRedundantAssertion = rRedundantAssertion;
		this.allOtherMIFromTest = allMIFromTest;
		this.allFailsFromTest = allFailsFromTest;
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

	public boolean hasControlFlow() {
		return testMethodModel.getElements(new TypeFilter<>(CtIf.class)).size() > 0
				//
				|| testMethodModel.getElements(new TypeFilter<>(CtWhile.class)).size() > 0
				//
				|| testMethodModel.getElements(new TypeFilter<>(CtFor.class)).size() > 0
				//
				|| testMethodModel.getElements(new TypeFilter<>(CtForEach.class)).size() > 0
				//
				|| testMethodModel.getElements(new TypeFilter<>(CtSwitch.class)).size() > 0
				//
				|| testMethodModel.getElements(new TypeFilter<>(CtDo.class)).size() > 0;

	}

	public boolean isRotten() {
		return !this.getClassificationAssert().getResultNotExecuted().isEmpty()
				|| !this.getClassificationHelperCall().getResultNotExecuted().isEmpty()
				|| !this.getClassificationHelperAssertion().getResultNotExecuted().isEmpty()
				|| (!this.getAllMissedFailFromTest().getResultNotExecuted().isEmpty())
				|| !this.getAllSkipFromTest().isEmpty();
	}

	public boolean isSmokeTest() {
		return !isExceptionExpected() && testElementsNotPresentInTest();
	}

	public boolean testElementsNotPresentInTest() {
		return rAssert.resultExecuted.isEmpty() && rAssert.resultNotExecuted.isEmpty()
				&& rHelperCall.resultExecuted.isEmpty() && rHelperCall.resultNotExecuted.isEmpty()
				&& rHelperAssertion.resultExecuted.isEmpty() && rHelperAssertion.resultNotExecuted.isEmpty()
				&& allMissedFailFromTest.resultExecuted.isEmpty() && allMissedFailFromTest.resultNotExecuted.isEmpty();
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

	public TestAnalysisResult generateFinalResult() {
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
		List<Helper> resultNotExecutedHelperAssertion = this.getClassificationHelperAssertion().getResultNotExecuted();
		List<AsAssertion> resultNotExecutedAssertion = this.getClassificationAssert().getResultNotExecuted();

		// Skips
		if (allSkipFromTest2 != null && allSkipFromTest2.size() > 0) {
			List<Skip> skipss = new ArrayList<>();
			for (CtReturn aReturn : allSkipFromTest2) {
				Skip aSkip = new Skip(aReturn);
				aSkip.getNotExecutedTestElements().addAll(resultNotExecutedHelperCall);
				aSkip.getNotExecutedTestElements().addAll(resultNotExecutedAssertion);

				skipss.add(aSkip);

			}
			return new TestAnalysisResult(skipss);
		}

		boolean smokeTest = isSmokeTest();

		checkTwoBranches(rAssert, rAssert, rHelperCall, rHelperAssertion);
		checkTwoBranches(rHelperCall, rAssert, rHelperCall, rHelperAssertion);
		checkTwoBranches(rHelperAssertion, rAssert, rHelperCall, rHelperAssertion);

		//
		classifyComplexHelper(notComplexHelperCallComplex, resultNotExecutedHelperCallComplex,
				resultNotExecutedHelperCall, false /* not assert, a call */);
		classifyComplexHelper(notComplexHelperAssertComplex, resultNotExecutedHelperAssertComplex,
				resultNotExecutedHelperAssertion, true /* assert */);
		classifyComplexAssert(notComplexAssertComplex, resultNotExecutedAssertComplex, resultNotExecutedAssertion);

		// Executed
		List<AsAssertion> allMissedFail = this.getAllMissedFailFromTest().getAll();

		List<AsAssertion> allRedundant = this.getRedundantAssertions().getAll();

		return new TestAnalysisResult(notComplexHelperCallComplex, notComplexHelperAssertComplex,
				notComplexAssertComplex, smokeTest, allMissedFail, allRedundant, resultNotExecutedHelperCallComplex,
				resultNotExecutedHelperAssertComplex, resultNotExecutedAssertComplex, allOtherMIFromTest);

	}

	public static void checkTwoBranches(Classification<? extends TestElement> elementsToClassify,
			Map<String, SuspiciousCode> cacheSuspicious, CtClass ctclassFromElementToCheck, CtClass aTestModelCtClass) {

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
					boolean exist = RtEngine.isCovered(cacheSuspicious, anStatement, ctclassFromElementToCheck,
							aTestModelCtClass);

					if (exist) {
						target.setFp(true);
						log.debug("Found executed in the other branch");
						break;
					}
				}

			}
		}

	}

	public void classifyComplexHelper(List<Helper> notComplex, List<Helper> resultNotExecutedHelperCallComplex,
			List<Helper> resultNotExecutedAssertion, boolean checkAssertion) {
		for (Helper aHelper : resultNotExecutedAssertion) {

			CtInvocation element = (checkAssertion) ? aHelper.getAssertion().getCtAssertion()
					: aHelper.getCalls().get(0);

			CtIf parentIf = element.getParent(CtIf.class);
			if (parentIf != null) {
				// complex
				resultNotExecutedHelperCallComplex.add(aHelper);
			} else {
				// not complex
				notComplex.add(aHelper);

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

	public static void checkTwoBranches(Classification<? extends TestElement> elementsToClassify,
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

					// Assertion executed by a helper
					exist = exist || rHelperAssertion.getResultExecuted().stream()
							.filter(e -> e.getAssertion().getCtAssertion() == anStatement).findFirst().isPresent();

					exist = exist || checkStatementInCallStack(rHelperAssertion, anStatement);

					if (exist) {
						target.setFp(true);
						log.debug("Found executed in the other branch");
						break;
					}
				}

			}
		}

	}

	public static boolean checkStatementInCallStack(Classification<Helper> rHelperAssertion, CtStatement anStatement) {

		for (Helper helperAssertion : rHelperAssertion.getResultExecuted()) {

			for (CtInvocation invocationInCall : helperAssertion.getCalls()) {
				if (invocationInCall == anStatement)
					return true;
			}

		}
		return false;
	}

	@Override
	public String toString() {
		return "TestClassificationResult [nameOfTestClass=" + nameOfTestClass + ", testMethodFromClass="
				+ testMethodFromClass + "]";
	}

	public Classification<AsAssertion> getRedundantAssertions() {
		return rRedundantAssertion;
	}

	public void setrRedundantAssertion(Classification<AsAssertion> rRedundantAssertion) {
		this.rRedundantAssertion = rRedundantAssertion;
	}

	public boolean hasHelperCall() {
		return !getClassificationHelperCall().getResultExecuted().isEmpty()
				|| !getClassificationHelperCall().getResultNotExecuted().isEmpty();
	}

	public boolean hasFailInvocation() {

		return this.allFailsFromTest.size() > 0;
	}

	public boolean hasTryCatch() {
		return testMethodModel.getElements(new TypeFilter<>(CtTry.class)).size() > 0;
	}

	public boolean isExceptionExpected() {

		return ((hasTryCatch() && hasFailInvocation())
				|| (getExpectException().size() > 0 || getAllExpectedExceptionFromTest().size() > 0));
	}

	public boolean isOnlyAssumeExecuted() {
		return onlyAssumeExecuted;
	}

	public List<CtInvocation> getAllAssumesFromTest() {
		return allAssumesFromTest;
	}

	public List<CtInvocation> getAllFailsFromTest() {
		return allFailsFromTest;
	}

}