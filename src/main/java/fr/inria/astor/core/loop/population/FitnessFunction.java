package fr.inria.astor.core.loop.population;

import fr.inria.astor.core.entities.VariantValidationResult;
import fr.inria.astor.core.loop.extension.AstorExtensionPoint;

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
