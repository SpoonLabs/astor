package fr.inria.astor.core.loop.spaces.operators;

import fr.inria.astor.core.entities.SuspiciousModificationPoint;

/**
 * Abstract class that represents a strategy for navigating a given operator space 
 * @author Matias Martinez
 *
 */
public abstract class OperatorSelectionStrategy {

	
	protected OperatorSpace operatorSpace;
	
	public OperatorSelectionStrategy(OperatorSpace space) {
		super();
		this.operatorSpace = space;
	}
	
	/**
	 * Returns an Operator
	 * @return
	 */
	public abstract AstorOperator getNextOperator();
	
	/**
	 * Given a modification point, it retrieves an operator to apply to that point.
	 * @param modificationPoint
	 * @return
	 */
	public abstract AstorOperator getNextOperator(SuspiciousModificationPoint modificationPoint);

	public OperatorSpace getOperatorSpace() {
		return operatorSpace;
	}

	

}
