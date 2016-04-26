package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.validation.entity.TestResult;
/**
 * 
 * @author Matias Martinez
 *
 */
public class EvoSuiteValidationResult extends ProgramVariantValidationResult {

	public EvoSuiteValidationResult(TestResult original) {
		super(original);
	}

	protected ProgramVariantValidationResult evoValidation;

	public ProgramVariantValidationResult getEvoValidation() {
		return evoValidation;
	}

	public void setEvoValidation(ProgramVariantValidationResult evoValidation) {
		this.evoValidation = evoValidation;
	}
	
}
