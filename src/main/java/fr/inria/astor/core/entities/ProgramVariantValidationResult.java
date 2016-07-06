package fr.inria.astor.core.entities;

/**
 * Result of the program validation (i.e. execution of the junit test)
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public abstract class ProgramVariantValidationResult {

	public abstract boolean wasSuccessful();

	public abstract int getFailureCount();

	public abstract boolean isRegressionExecuted();

	public abstract void setRegressionExecuted(boolean regressionExecuted);

	public abstract int getPassingTestCases();
	
	public abstract int getCasesExecuted();
}
