package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.Result;

import fr.inria.astor.core.validation.entity.TestResult;

/**
 * Result of the program validation (i.e. execution of the junit test)
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class ProgramVariantValidationResult {

	int numberFailingTestCases = 0;
	int numberPassingTestCases = 0;

	boolean regressionExecuted = false;
	TestResult testResult;

	public ProgramVariantValidationResult(TestResult result) {
		super();
		numberPassingTestCases = result.casesExecuted - result.failures;
		numberFailingTestCases = result.failures;
		testResult = result;

	}

	public ProgramVariantValidationResult(TestResult result,
			boolean resultSucess, boolean regressionExecuted) {
		super();
		numberPassingTestCases = result.casesExecuted - result.failures;
		numberFailingTestCases = result.failures;
	}

	/**
	 * Result of the original failing test cases
	 */
	private List<Result> testResults = new ArrayList<Result>();

	/**
	 * Result of the original failing test cases
	 */
	private List<Result> testRegressionResults = new ArrayList<Result>();

	public List<Result> getTestResults() {
		return testResults;
	}

	public List<Result> getTestRegressionResults() {
		return testRegressionResults;
	}

	public void setTestRegressionResults(List<Result> testRegressionResults) {
		this.testRegressionResults = testRegressionResults;
	}

	public void setTestResults(List<Result> failingResults) {
		this.testResults = failingResults;
	}

	

	public boolean wasSuccessful() {
	
		return numberFailingTestCases == 0 ;
	}

	public int getFailureCount() {
	
		return numberFailingTestCases;
	}

	public void addResultTest(Result r) {
		this.numberFailingTestCases += r.getFailureCount();
		this.numberPassingTestCases += r.getRunCount() - r.getFailureCount();
		this.getTestResults().add(r);
	}

	public void addResultRegressionTest(Result r) {
		this.numberFailingTestCases += r.getFailureCount();
		this.numberPassingTestCases += r.getRunCount() - r.getFailureCount();
		this.getTestRegressionResults().add(r);
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

	public String toString(){
		return "Variant Validation: successfull? "+this.wasSuccessful() + " "+this.getTestResults();
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
