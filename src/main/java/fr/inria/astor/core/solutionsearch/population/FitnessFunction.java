package fr.inria.astor.core.solutionsearch.population;

import fr.inria.astor.core.entities.validation.VariantValidationResult;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * Fitness function
 * 
 * @author Matias Martinez
 *
 */
public interface FitnessFunction extends  AstorExtensionPoint {

	
	public double calculateFitnessValue(VariantValidationResult validationResult);

	
	public double getWorstMaxFitnessValue();
	
}
