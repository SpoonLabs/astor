package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.util.ArrayList;
import java.util.List;

import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.combination.Operator;

public class Combination {
	private final List<List<Expression>> toCombine = new ArrayList<>();
	private final Operator operator;
	private final List<Integer> positions;
	private final int nbExpression;
	private boolean isEnd = false;

	Combination(List<Expression> toCombine, Operator operator, int nbExpression) {
		// select compatible element for each parameter
		for (int i = 0; i < operator.getTypeParameters().size(); i++) {
			Class aClass = operator.getTypeParameters().get(i);
			for (int j = 0; j < toCombine.size(); j++) {
				Expression expression = toCombine.get(j);
				if (expression.getValue().isCompatibleWith(aClass)) {
					if (this.toCombine.size() < i + 1) {
						this.toCombine.add(new ArrayList<Expression>());
					}
					this.toCombine.get(i).add(expression);
				}
			}
		}
		this.operator = operator;
		this.nbExpression = nbExpression;
		this.positions = new ArrayList<>(nbExpression);
		for (int i = 0; i < nbExpression; i++) {
			positions.add(0);
		}
		if (this.toCombine.isEmpty()) {
			isEnd = true;
		}
		for (List<Expression> expressions : this.toCombine) {
			if (expressions.isEmpty()) {
				isEnd = true;
				return;
			}
		}
	}

	public synchronized List<Expression> perform(boolean stop) {
		if (isEnd || stop) {
			return null;
		}
		List<Expression> current = new ArrayList<>();
		for (int i = 0; i < nbExpression; i++) {
			current.add(toCombine.get(i).get(positions.get(i)));
		}
		incrementPosition();
		return current;
	}

	public boolean isEnd(boolean stop) {
		return isEnd || stop;
	}

	private synchronized void incrementPosition() {
		for (int i = positions.size() - 1; i >= 0; i--) {
			Integer position = positions.get(i);
			int size = toCombine.get(i).size();
			if (position < size - 1) {
				position++;
				positions.set(i, position);
				return;
			} else {
				positions.set(i, 0);
				if (i == positions.size() - 1 && positions.size() > 1) {
					// positions.set(i, Math.min(positions.get(i - 1) +
					// 2, size - 1));
				}
				if (i == 0) {
					isEnd = true;
					return;
				}
			}
		}
	}
}