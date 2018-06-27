package fr.inria.astor.core.manipulation.synthesis.dynamoth.combinations;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.ExpressionImpl;
import fr.inria.lille.repair.expression.access.Literal;
import fr.inria.lille.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.repair.expression.combination.binary.BinaryOperator;
import fr.inria.lille.repair.expression.factory.AccessFactory;
import fr.inria.lille.repair.expression.factory.CombinationFactory;
import fr.inria.lille.repair.expression.value.Value;

/**
 * is the generic type of a binary expression
 */

public class BinaryExpressionImpl extends ExpressionImpl implements BinaryExpression {
	private BinaryOperator operator;
	private Expression first;
	private Expression second;
	private String strExpression = null;

	/**
	 *
	 */
	public BinaryExpressionImpl(BinaryOperator operator, Expression first, Expression second,
			NopolContext nopolContext) {
		super(null, nopolContext);
		this.operator = operator;
		this.first = first;
		this.second = second;
		if (operator.isCommutative() && (first instanceof Literal || first.getValue().getRealValue() == null)) {
			this.first = second;
			this.second = first;
		}
		setValue(performExpression());
	}

	@Override
	public Value evaluate(Candidates values) {

		Value exp1Value = this.getFirstExpression().getValue();
		Value exp2Value = this.getSecondExpression().getValue();
		Value value = Value.NOVALUE;
		try {
			Value evaluate = this.getFirstExpression().evaluate(values);
			if (evaluate != Value.NOVALUE && evaluate != null) {
				this.getFirstExpression().setValue(evaluate);

				Value evaluate2 = this.getSecondExpression().evaluate(values);
				if (evaluate2 != null) {
					this.getSecondExpression().setValue(evaluate2);
					value = performExpression();
					if (value == null && evaluate2 == Value.NOVALUE) {
						value = Value.NOVALUE;
					}
				}
			}
		} finally {
			this.getFirstExpression().setValue(exp1Value);
			this.getSecondExpression().setValue(exp2Value);
		}
		return value;
	}

	public void evaluate() {
		try {
			Value value = performExpression();
			setValue(value);
		} catch (ArithmeticException e) {
			// ignore
		}
	}

	boolean isExpressionMakeSense() {
		Value firstValue = getFirstExpression().getValue();
		if (firstValue == Value.NOVALUE) {
			return false;
		}

		Value secondValue = getSecondExpression().getValue();
		if (secondValue == Value.NOVALUE && getOperator() != BinaryOperator.OR) {
			return false;
		}

		Class param1 = getOperator().getParam1();
		Class param2 = getOperator().getParam2();

		// check the compatibility with the operator
		if (!firstValue.isCompatibleWith(param1)) {
			return false;
		}

		if (secondValue != Value.NOVALUE && !secondValue.isCompatibleWith(param2)) {
			return false;
		}
		if (secondValue != Value.NOVALUE && firstValue.isConstant() && secondValue.isConstant()) {
			// return false;//Commented by MM
		}

		switch (getOperator()) {
		case EQ:
		case NEQ:
			// the two expressions type
			if (secondValue.isPrimitive() != firstValue.isPrimitive()) {
				return false;
			}

			if (getFirstExpression().sameExpression(getSecondExpression())) {
				return false;
			}
			if (!firstValue.isCompatibleWith(Boolean.class) && !firstValue.isCompatibleWith(Number.class)
					&& firstValue.getRealValue() != null && secondValue.getRealValue() != null) {
				return false;
			}
			if (!firstValue.isCompatibleWith(param2)) {
				return false;
			}

			// comparison between null and a primitive
			if (firstValue.isCompatibleWith(Number.class)) {
				if (!(secondValue.isCompatibleWith(Number.class))) {
					return false;
				}
			} else if (secondValue.isCompatibleWith(Number.class)) {
				if (!(firstValue.isCompatibleWith(Number.class))) {
					return false;
				}
			} else if (firstValue.isCompatibleWith(Boolean.class)) {
				if (!(secondValue.isCompatibleWith(Boolean.class))) {
					return false;
				}
			} else if (secondValue.isCompatibleWith(Boolean.class)) {
				if (!(firstValue.isCompatibleWith(Boolean.class))) {
					return false;
				}
			}
			break;
		case AND:
		case OR:
			if (getFirstExpression().sameExpression(getSecondExpression())) {
				return false;
			}
			if (getFirstExpression() instanceof Literal || getSecondExpression() instanceof Literal) {
				return false;
			}
			break;
		case LESSEQ:
		case LESS:
			if (getFirstExpression().sameExpression(getSecondExpression())) {
				return false;
			}
			break;
		case ADD:
			if ((firstValue.isConstant() && isValue(firstValue, 0))
					|| (secondValue.isConstant() && isValue(secondValue, 0))) {
				return false;
			}
			break;
		case SUB:
			if (firstValue.isConstant() && isValue(firstValue, 0)) {
				return false;
			}
			if (getFirstExpression().sameExpression(getSecondExpression())) {
				return false;
			}
			break;
		case MULT:
			if ((firstValue.isConstant() && isValue(firstValue, 0))
					|| (secondValue.isConstant() && isValue(secondValue, 0))) {
				return false;
			}
			if ((firstValue.isConstant() && isValue(firstValue, 1))
					|| (secondValue.isConstant() && isValue(secondValue, 1))) {
				return false;
			}
		case DIV:
			if (isValue(secondValue, 0)) {
				return false;
			}
			if ((firstValue.isConstant() && isValue(firstValue, 1))
					|| (secondValue.isConstant() && isValue(secondValue, 1))) {
				return false;
			}
			if (getFirstExpression().sameExpression(getSecondExpression())) {
				return false;
			}
			break;
		}
		return true;
	}

	private boolean isValue(Value v1, Number v2) {
		Value eval = new BinaryExpressionEvaluator(
				CombinationFactory.create(BinaryOperator.EQ, AccessFactory.literal(v1.getRealValue(), nopolContext),
						AccessFactory.literal(v2, nopolContext), nopolContext)).eval();
		if (eval == null) {
			return false;
		}
		return (boolean) eval.getRealValue();
	}

	Value performExpression() {
		if (!isExpressionMakeSense()) {
			return null;
		}
		return new BinaryExpressionEvaluator(this).eval();
	}

	public BinaryOperator getOperator() {
		return operator;
	}

	public Expression getFirstExpression() {
		return first;
	}

	public Expression getSecondExpression() {
		return second;
	}

	public void setFirst(Expression first) {
		this.first = first;
	}

	public void setSecond(Expression second) {
		this.second = second;
	}

	@Override
	public double getWeight() {
		double weight = 0;
		switch (getOperator()) {
		case AND:
			weight = this.nopolContext.getAndWeight();
			break;
		case OR:
			weight = this.nopolContext.getOrWeight();
			break;
		case EQ:
			weight = this.nopolContext.getEqWeight();
			break;
		case NEQ:
			weight = this.nopolContext.getnEqWeight();
			break;
		case LESS:
			weight = this.nopolContext.getLessWeight();
			break;
		case LESSEQ:
			weight = this.nopolContext.getLessEqWeight();
			break;
		case ADD:
			weight = this.nopolContext.getAddWeight();
			break;
		case SUB:
			weight = this.nopolContext.getSubWeight();
			break;
		case MULT:
			weight = this.nopolContext.getMulWeight();
			break;
		case DIV:
			weight = this.nopolContext.getDivWeight();
			break;
		}
		return weight * getPriority() * getFirstExpression().getWeight() * getSecondExpression().getWeight();
	}

	@Override
	public String toString() {
		if (strExpression == null) {
			strExpression = asPatch();
		}
		return strExpression;
	}

	@Override
	public String asPatch() {
		if (strExpression != null) {
			return strExpression;
		}
		StringBuilder sb = new StringBuilder();
		String first = getFirstExpression().asPatch().intern();
		if (getFirstExpression() instanceof BinaryExpression) {
			sb.append("(");
			sb.append(first);
			sb.append(")");
		} else {
			sb.append(first);
		}
		sb.append(" ");
		sb.append(getOperator().getSymbol());
		sb.append(" ");
		String second = getSecondExpression().asPatch().intern();
		if (getSecondExpression() instanceof BinaryExpression) {
			sb.append("(");
			sb.append(second);
			sb.append(")");
		} else {
			sb.append(second);
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		return operator.hashCode() * first.hashCode() * second.hashCode();
	}

	@Override
	public int nbSubExpression() {
		return 2;
	}
}
