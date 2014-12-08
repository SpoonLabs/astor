package fr.inria.astor.core.loop.mutation.mutants.core;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface IProbabilityItem extends IMutator {

	public double getProbability();
	
	public void setProbability(double prob);
}
