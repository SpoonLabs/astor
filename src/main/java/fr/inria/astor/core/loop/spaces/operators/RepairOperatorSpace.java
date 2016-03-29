package fr.inria.astor.core.loop.spaces.operators;

/**
 * Defines a interface of repair spaces
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface RepairOperatorSpace {
	
	public AstorOperator getNextOperator();
	
	public AstorOperator getNextOperator(double suspiciousValue);
	
	public int size();
	
	public AstorOperator[] values();
}
