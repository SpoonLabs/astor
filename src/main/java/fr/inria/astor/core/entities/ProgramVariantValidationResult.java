package fr.inria.astor.core.entities;

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
	boolean resultSucess = false;
	TestResult testResult;

	public ProgramVariantValidationResult(TestResult result) {
		super();
		numberPassingTestCases = result.casesExecuted - result.failures;
		numberFailingTestCases = result.failures;
		testResult = result;
		resultSucess = (result.casesExecuted == result.failures);

	}

	public ProgramVariantValidationResult(TestResult result,
			boolean resultSucess, boolean regressionExecuted) {
		this(result);
		this.regressionExecuted = regressionExecuted;
		this.resultSucess = resultSucess;
	}

	/**
	 * Result of the original failing test cases
	 */
	//private List<Result> testResults = new ArrayList<Result>();

	/**
	 * Result of the original failing test cases
	 */
//	private List<Result> testRegressionResults = new ArrayList<Result>();

	
	

	public boolean wasSuccessful() {
	
		return numberFailingTestCases == 0 ;
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

	public String toString(){
		return "Variant Validation: successfull? "+this.wasSuccessful() + " "+this.getTestResult();
	}

	public TestResult getTestResult() {
		return testResult;
	}

	public void setTestResult(TestResult testResult) {
		this.testResult = testResult;
	}

}
