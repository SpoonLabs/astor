package fr.inria.astor.test.repair.evaluation.other;

import fr.inria.astor.core.validation.validators.ProcessEvoSuiteValidator;
/**
 * Class Used in testing
 * @author Matias Martinez
 *
 */
public class ProcessVal4Test extends ProcessEvoSuiteValidator {

	public ProcessVal4Test() {
	}
	
	@Override
	public boolean isOverOriginal() {
		//We force to process 
		return false;
	}
}
