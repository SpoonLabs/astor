package fr.inria.astor.core.loop.spaces.operators;

/**
 * Defines a interface of repair spaces
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface RepairOperatorSpace {
	
	public AstorOperator getNextOperator();
		
	/**
	 * Number of operators in the space
	 * @return
	 */
	public int size();
	/**
	 * Return all operators from the space
	 * @return
	 */
	public AstorOperator[] values();
}
