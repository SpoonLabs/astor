package fr.inria.astor.approaches.tos.operator;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;

/**
 * Operator space of TOS
 * 
 * @author Matias Martinez
 *
 */
public class TOSOperatorSpace extends OperatorSpace {
	/**
	 * We register only one operator, Statement replace operator
	 */
	public TOSOperatorSpace() {
		super.register(new ReplaceOp());
	}
}
