package fr.inria.astor.core.loop.population;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;

/**
 * Fitness function based on test suite execution.
 * @author Matias Martinez
 *
 */
public class TestCaseFitnessFunction implements FitnessFunction {

	/**
	 * In this case the fitness value is associate to the failures: LESS FITNESS
	 * is better.
	 */
	public double calculateFitnessValue(ProgramVariant variant) {
		
		if(variant.getValidationResult() == null)
			return this.getWorstMaxFitnessValue();
		
		TestCaseVariantValidationResult result = (TestCaseVariantValidationResult) variant.getValidationResult();
		return result.getFailureCount();
	}

	public double getWorstMaxFitnessValue() {

		return Double.MAX_VALUE;
	}

}
