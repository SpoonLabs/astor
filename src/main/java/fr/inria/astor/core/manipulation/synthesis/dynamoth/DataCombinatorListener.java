package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;

public class DataCombinatorListener implements CombineListener {
	private final Candidates allCombinedNotEvaluatedExpressions;

	public DataCombinatorListener(Candidates allCombinedNotEvaluatedExpressions) {
		this.allCombinedNotEvaluatedExpressions = allCombinedNotEvaluatedExpressions;
	}

	@Override
	public boolean check(Expression expression) {

		if (!allCombinedNotEvaluatedExpressions.contains(expression)) {
			allCombinedNotEvaluatedExpressions.add(expression);
			return true;
		}
		return false;
	}
}