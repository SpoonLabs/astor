package fr.inria.astor.core.loop.population;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.extension.AstorExtensionPoint;

/**
 * Fitness function
 * 
 * @author Matias Martinez
 *
 */
public interface FitnessFunction extends  AstorExtensionPoint {

	
	public double calculateFitnessValue(ProgramVariant variant);

	
	public double getWorstMaxFitnessValue();
	
}
