package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.validation.entity.TestResult;
/**
 * 
 * @author Matias Martinez
 *
 */
public class TestCasesProgramValidationResult extends ProgramVariantValidationResult {

	int numberFailingTestCases = 0;
	int numberPassingTestCases = 0;

	boolean regressionExecuted = false;
	boolean resultSucess = false;

	TestResult testResult;

	public TestCasesProgramValidationResult(TestResult result) {
		super();
		setTestResult(result);
	}

	public TestCasesProgramValidationResult(TestResult result, boolean resultSucess, boolean regressionExecuted) {
		this(result);
		this.regressionExecuted = regressionExecuted;
		this.resultSucess = resultSucess;
	}

	public boolean wasSuccessful() {

		return numberFailingTestCases == 0 && this.resultSucess;
	}

	public int getFailureCount() {

		return numberFailingTestCases;
	}

	public boolean isRegressionExecuted() {
		return regressionExecuted;
	}

	public void setRegressionExecuted(boolean regressionExecuted) {
		this.regressionExecuted = regressionExecuted;
	}

	public int getPassingTestCases() {
		return numberPassingTestCases;
	}

	public String toString() {
		return printTestResult(this.getTestResult());
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult result) {
		this.testResult = result;
		if (result != null) {
			numberPassingTestCases = result.casesExecuted - result.failures;
			numberFailingTestCases = result.failures;
			resultSucess = (result.casesExecuted == result.failures);
		}
	}

	protected String printTestResult(TestResult result) {
		if (result == null)
			return "";
		return "|" + result.wasSuccessful() + "|" + result.failures + "|" + result.casesExecuted + "|" + result.failTest
				+ "|";
	}

	@Override
	public int getCasesExecuted() {
		
		return getPassingTestCases() + getFailureCount();
	}

}
