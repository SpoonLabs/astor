package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
/**
 * This class stores two validation result. One using traditional astor validation mechanism such as process for running junit.
 * The second one corresponds to the result from test generated with Evosuite.
 * Note that this validator does  NOT take in account the EvoSuite result for deciding the correctness of a variant.
 * @author Matias Martinez
 *
 */
public class EvoSuiteValidationResult  extends  CompoundValidationResult {



	public TestCaseVariantValidationResult getFailingTestValidation() {
		return this.validations.get("failing");
	}

	public void setFailingTestValidation(TestCaseVariantValidationResult evoValidation) {
		this.addValidation("failing", evoValidation);
	}
	
	public TestCaseVariantValidationResult getEvoValidation() {
		return this.validations.get("evo");
	}

	public void setEvoValidation(TestCaseVariantValidationResult evoValidation) {
		this.addValidation("evo", evoValidation);
	}

	public TestCaseVariantValidationResult getManualTestValidation() {
		return this.getValidation("manual");
		
	}

	public void setManualTestValidation(TestCaseVariantValidationResult manualTestValidation) {
		this.addValidation("manual",manualTestValidation);
	}
	public String toString(){
		return
			//+ ((this.getValidation("original") != null)?"\noriginal: "+ (getValidation("original")):"")
			 ((this.getFailingTestValidation() != null)?"\nfailing: "+ (getValidation("failing")):"")
		+((this.getManualTestValidation() != null)?"\nmanual_regression: "+(getManualTestValidation()):"")
		+((getEvoValidation() != null)?"\nevo_regression: "+ (getEvoValidation()):"")
		;
	}

	@Override
	public boolean isSuccessful() {
	
		return (getValidation("failing") == null || getValidation("failing").isSuccessful())
				//I dont know when I included the manual as part of ES validation.
				//&&
				//(getValidation("manual") == null || getValidation("manual").wasSuccessful())
				;
	}
	
	
}
