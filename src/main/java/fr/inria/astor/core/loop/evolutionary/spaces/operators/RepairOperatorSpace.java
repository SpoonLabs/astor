package fr.inria.astor.core.loop.evolutionary.spaces.operators;

import fr.inria.astor.core.entities.taxonomy.Operator;
/**
 * Defines a interface of repair spaces
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface RepairOperatorSpace {
	
	public Operator getNextOperator();
	
	public Operator getNextOperator(double suspiciousValue);
	
	public int size();
	
	public Operator[] values();
}
