package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.validation.entity.TestResult;
/**
 * This class stores two validation result. One using traditional astor validation mechanism such as process for running junit.
 * The second one corresponds to the result from test generated with Evosuite.
 * Note that this validator does  NOT take in account the EvoSuite result for deciding the correctness of a variant.
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
