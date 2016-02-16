package fr.inria.astor.core.loop.evolutionary.spaces.operators;

import fr.inria.astor.core.entities.taxonomy.Operation;
/**
 * Defines a interface of repair spaces
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface RepairOperatorSpace {
	
	public Operation getNextMutation();
	
	public Operation getNextMutation(double suspiciousValue);
	
	public int size();
	
	public Operation[] values();
}
