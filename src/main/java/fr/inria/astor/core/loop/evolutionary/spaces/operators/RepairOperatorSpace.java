package fr.inria.astor.core.loop.evolutionary.spaces.operators;

import fr.inria.astor.core.entities.taxonomy.MutationOperation;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface RepairOperatorSpace {
	
	public MutationOperation getNextMutation();
	
	public MutationOperation getNextMutation(double suspiciousValue);
	
	public int size();
	
	public MutationOperation[] values();
}
