package fr.inria.astor.core.entities;


/**
 * Result of the program validation (i.e. execution of the junit test)
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public interface TestCaseVariantValidationResult extends VariantValidationResult {


	public int getFailureCount();

	public boolean isRegressionExecuted();

	public void setRegressionExecuted(boolean regressionExecuted);

	public int getPassingTestCases();

	public int getCasesExecuted();
}
