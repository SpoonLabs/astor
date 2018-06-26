package fr.inria.astor.approaches.cardumen;

import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;

/**
 * Operator space of Cardumen
 * @author Matias Martinez
 *
 */
public class CardumenOperatorSpace extends OperatorSpace {

	public CardumenOperatorSpace() {
		super.register(new ExpressionReplaceOperator());
	}
}
