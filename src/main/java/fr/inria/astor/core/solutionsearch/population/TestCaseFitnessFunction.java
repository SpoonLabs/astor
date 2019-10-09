package fr.inria.astor.core.solutionsearch.population;

import fr.inria.astor.core.entities.validation.TestCaseVariantValidationResult;
import fr.inria.astor.core.entities.validation.VariantValidationResult;

/**
 * Fitness function based on test suite execution.
 * 
 * @author Matias Martinez
 *
 */
public class TestCaseFitnessFunction implements FitnessFunction {

	/**
	 * In this case the fitness value is associate to the failures: LESS FITNESS
	 * is better.
	 */
	public double calculateFitnessValue(VariantValidationResult validationResult) {

		if (validationResult == null)
			return this.getWorstMaxFitnessValue();

		TestCaseVariantValidationResult result = (TestCaseVariantValidationResult) validationResult;
		return result.getFailureCount();
	}

	public double getWorstMaxFitnessValue() {

		return Double.MAX_VALUE;
	}

}
