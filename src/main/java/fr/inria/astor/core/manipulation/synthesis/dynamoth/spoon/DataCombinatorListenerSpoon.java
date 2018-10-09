package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;

import java.util.List;

import spoon.reflect.code.CtExpression;

public class DataCombinatorListenerSpoon implements CombineListenerSpoon {
	private final List<CtExpression> allCombinedNotEvaluatedExpressions;

	public DataCombinatorListenerSpoon(List<CtExpression> allCombinedNotEvaluatedExpressions) {
		this.allCombinedNotEvaluatedExpressions = allCombinedNotEvaluatedExpressions;
	}

	@Override
	public boolean check(CtExpression expression) {

		// if (!allCombinedNotEvaluatedExpressions.contains(expression)) {
		allCombinedNotEvaluatedExpressions.add(expression);
		return true;
		// }
		// return false;
	}
}