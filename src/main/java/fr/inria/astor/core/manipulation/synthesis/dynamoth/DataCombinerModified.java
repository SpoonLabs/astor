package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.combination.CombinationExpression;
import fr.inria.lille.repair.expression.combination.Operator;
import fr.inria.lille.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.repair.expression.combination.binary.BinaryOperator;
import fr.inria.lille.repair.expression.combination.unary.UnaryExpression;
import fr.inria.lille.repair.expression.combination.unary.UnaryOperator;
import fr.inria.lille.repair.expression.factory.AccessFactory;
import fr.inria.lille.repair.expression.factory.CombinationFactory;
import fr.inria.lille.repair.expression.value.Value;

/**
 * Created by Thomas Durieux on 12/03/15.
 */
public class DataCombinerModified {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static int maxDepth;

	private final List<CombineListener> listeners = new ArrayList<>();
	private boolean stop = false;
	private long startTime;
	private long maxTime;
	private long executionTime;
	private NopolContext nopolContext;

	private int MAX = 100;

	public Candidates combine(Candidates candidates, Object angelicValue, long maxTime, NopolContext nopolContext) {
		this.nopolContext = nopolContext;
		maxDepth = nopolContext.getSynthesisDepth();
		this.maxTime = maxTime;
		this.startTime = System.currentTimeMillis();
		executionTime = System.currentTimeMillis() - startTime;
		logger.debug("[combine] start on " + candidates.size() + " elements");
		Candidates result = new Candidates();
		result.addAll(candidates);
		List<Expression> lastTurn = new ArrayList<>();
		lastTurn.addAll(candidates);
		int previousSize = 0;
		executionTime = System.currentTimeMillis() - startTime;
		List<Operator> operators = new ArrayList<>();
		operators.addAll(Arrays.asList(UnaryOperator.values()));
		operators.addAll(Arrays.asList(BinaryOperator.values()));
		for (int i = 0; i < maxDepth - 1 && !stop && executionTime <= maxTime; i++) {
			lastTurn.addAll(newCombiner(lastTurn, operators, i == maxDepth - 2 ? angelicValue : null));

			/*
			 * lastTurn.addAll(combinePrimitives(lastTurn, previousSize, i ==
			 * maxDepth - 2 ? angelicValue : null)); if (stop) { return result;
			 * } lastTurn.addAll(combineComplex(lastTurn, previousSize, i ==
			 * maxDepth - 2 ? angelicValue : null)); previousSize =
			 * candidates.size();
			 */
			executionTime = System.currentTimeMillis() - startTime;
		}
		// result.addAll(lastTurn);
		logger.debug("[combine] end " + lastTurn.size() + " evaluated elements");
		return result;
	}

	private List<Expression> newCombiner(final List<Expression> toCombine, final List<Operator> operators,
			final Object angelicValue) {
		final List<Expression> result = new ArrayList<>();

		class Combination {
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

			public synchronized List<Expression> perform() {
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

			public boolean isEnd() {
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
		if (nopolContext.isSortExpressions()) {
			Collections.sort(toCombine, Collections.reverseOrder());
		}

		for (Operator operator : operators) {
			if (angelicValue != null && !operator.getReturnType().isAssignableFrom(angelicValue.getClass())) {
				continue;
			}
			int nbExpression = operator.getTypeParameters().size();
			Combination combination = new Combination(toCombine, operator, nbExpression);
			while (!combination.isEnd()) {
				List<Expression> expressions = combination.perform();
				CombinationExpression binaryExpression = CombinationFactory.create(operator, expressions, nopolContext);
				if (addExpressionIn(binaryExpression, result, false)) {
					if (callListener(binaryExpression)) {
						if (nopolContext.isOnlyOneSynthesisResult() || result.size() >= MAX) {
							return result;
						}
					}
				}
				if (operator instanceof BinaryOperator) {
					if (!((BinaryOperator) operator).isCommutative()) {
						binaryExpression = CombinationFactory.create(operator,
								Arrays.asList(expressions.get(1), expressions.get(0)), nopolContext);
						if (addExpressionIn(binaryExpression, result, false)) {
							if (callListener(binaryExpression)) {
								if (nopolContext.isOnlyOneSynthesisResult() || result.size() >= MAX) {
									return result;
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private List<Expression> combinePrimitives(List<Expression> toCombine, int previousSize, Object value) {
		logger.debug("[combine] primitive start on " + toCombine.size() + " elements");
		List<Expression> result = new ArrayList<>();

		if (nopolContext.isSortExpressions()) {
			Collections.sort(toCombine, Collections.reverseOrder());
		}
		executionTime = System.currentTimeMillis() - startTime;
		for (int i = 0; i < toCombine.size() && executionTime <= maxTime; i++) {
			Expression expression = toCombine.get(i);
			if (expression.getValue().getType() == null
					|| (!Number.class.isAssignableFrom(expression.getValue().getType())
							&& !Boolean.class.isAssignableFrom(expression.getValue().getType()))) {
				continue;
			}
			if (expression.getValue().getType() == null) {
				continue;
			}
			if (!expression.getValue().isPrimitive()) {
				continue;
			}
			for (int j = 0; j < UnaryOperator.values().length; j++) {
				UnaryOperator operator = UnaryOperator.values()[j];
				if (value != null && operator.getReturnType() != value.getClass()) {
					continue;
				}
				if (!operator.getReturnType().isAssignableFrom(expression.getValue().getType())) {
					continue;
				}
				UnaryExpression unaryExpression = CombinationFactory.create(operator, expression, nopolContext);
				if (addExpressionIn(unaryExpression, result, value != null)) {
					// expression.getInExpressions().add(unaryExpression);
					if (callListener(unaryExpression) && nopolContext.isOnlyOneSynthesisResult()) {
						return result;
					}
				}
			}
			executionTime = System.currentTimeMillis() - startTime;
			for (int j = Math.max(i, previousSize); j < toCombine.size() && executionTime <= maxTime; j++) {
				if (i == j) {
					continue;
				}
				Expression expression1 = toCombine.get(j);

				if (expression1.getValue().getType() == null
						|| (!Number.class.isAssignableFrom(expression1.getValue().getType())
								&& !Boolean.class.isAssignableFrom(expression1.getValue().getType()))) {
					continue;
				}
				if (expression.getValue().isConstant() && expression1.getValue().isConstant()) {
					continue;
				}
				if (!expression1.getValue().isPrimitive()) {
					continue;
				}
				executionTime = System.currentTimeMillis() - startTime;
				for (int k = 0; k < BinaryOperator.values().length && executionTime <= maxTime; k++) {
					BinaryOperator operator = BinaryOperator.values()[k];
					if (value != null && operator.getReturnType() != value.getClass()) {
						continue;
					}
					if (!operator.getParam1().isAssignableFrom(expression.getValue().getType())
							|| !operator.getParam2().isAssignableFrom(expression1.getValue().getType())) {
						continue;
					}
					List returnValue = combineExpressionOperator(expression, expression1, operator, value, result);
					if (returnValue != null) {
						return returnValue;
					}
					executionTime = System.currentTimeMillis() - startTime;
				}
				executionTime = System.currentTimeMillis() - startTime;
			}
			executionTime = System.currentTimeMillis() - startTime;
		}
		return result;
	}

	private List<Expression> combineExpressionOperator(Expression expression, Expression expression1,
			BinaryOperator operator, Object value, List<Expression> result) {
		BinaryExpression binaryExpression = CombinationFactory.create(operator, expression, expression1, nopolContext);
		if (addExpressionIn(binaryExpression, result, value != null)) {
			// expression.getInExpressions().add(binaryExpression);
			if (!expression.sameExpression(expression1)) {
				// expression1.getInExpressions().add(binaryExpression);
				if (callListener(binaryExpression) && nopolContext.isOnlyOneSynthesisResult()) {
					return result;
				}
			}
		}

		if (!operator.isCommutative()) {
			binaryExpression = CombinationFactory.create(operator, expression1, expression, nopolContext);

			if (addExpressionIn(binaryExpression, result, value != null)) {
				// expression.getInExpressions().add(binaryExpression);
				if (!expression.sameExpression(expression1)) {
					// expression1.getInExpressions().add(binaryExpression);
					if (callListener(binaryExpression) && nopolContext.isOnlyOneSynthesisResult()) {
						return result;
					}
				}
			}
		}
		return null;
	}

	private List<Expression> combineComplex(List<Expression> toCombine, int previousSize, Object value) {
		Expression nullExpression = AccessFactory.literal(null, nopolContext);
		logger.debug("[combine] complex start on " + toCombine.size() + " elements");
		List<Expression> result = new ArrayList<>();
		if (value != null && value.getClass() != Boolean.class) {
			return result;
		}
		if (nopolContext.isSortExpressions()) {
			Collections.sort(toCombine, Collections.reverseOrder());
		}
		executionTime = System.currentTimeMillis() - startTime;
		for (int i = 0; i < toCombine.size() && executionTime <= maxTime; i++) {
			Expression expression = toCombine.get(i);

			if (expression.getValue().getType() != null
					&& (Number.class.isAssignableFrom(expression.getValue().getType())
							|| Boolean.class.isAssignableFrom(expression.getValue().getType()))) {
				continue;
			}
			if (expression.getValue().isPrimitive()) {
				continue;
			}

			BinaryExpression binaryExpression = CombinationFactory.create(BinaryOperator.EQ, expression, nullExpression,
					nopolContext);
			if (addExpressionIn(binaryExpression, result, value != null)) {
				// expression.getInExpressions().add(binaryExpression);
				if (!expression.sameExpression(nullExpression)) {
					// nullExpression.getInExpressions().add(binaryExpression);
					if (callListener(binaryExpression)) {
						return result;
					}
				}
			}

			binaryExpression = CombinationFactory.create(BinaryOperator.NEQ, expression, nullExpression, nopolContext);
			if (addExpressionIn(binaryExpression, result, value != null)) {
				// expression.getInExpressions().add(binaryExpression);
				if (!expression.sameExpression(nullExpression)) {
					// nullExpression.getInExpressions().add(binaryExpression);
					if (callListener(binaryExpression)) {
						return result;
					}
				}
			}
			executionTime = System.currentTimeMillis() - startTime;
		}
		return result;
	}

	private boolean addExpressionIn(Expression expression, List<Expression> results, boolean toAdd) {
		if (expression.getValue() == null || expression.getValue() == Value.NOVALUE) {
			return false;
		}
		if (expression.getValue().getRealValue() == null) {
			return false;
		}
		// logger.debug("[data] " + expression);
		return results.add(expression);
	}

	public void addCombineListener(CombineListener combineListener) {
		this.listeners.add(combineListener);
	}

	private boolean callListener(Expression expression) {
		for (CombineListener combineListener : listeners) {
			if (combineListener.check(expression)) {
				if (nopolContext.isOnlyOneSynthesisResult()) {
					stop = true;
				}
				return true;
			}
		}
		return false;
	}

	public interface CombineListener {
		boolean check(Expression expression);
	}
}
