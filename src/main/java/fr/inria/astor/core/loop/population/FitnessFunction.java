package fr.inria.astor.core.loop.population;

import fr.inria.astor.core.entities.ProgramVariant;

/**
 * Fitness function
 * 
 * @author Matias Martinez
 *
 */
public interface FitnessFunction {

	
	public double calculateFitnessValue(ProgramVariant variant);

	
	public double getWorstMaxFitnessValue();
	
}
