package fr.inria.astor.core.manipulation.synthesis.dynamoth.combinations;

import fr.inria.lille.repair.expression.combination.binary.BinaryExpression;
import fr.inria.lille.repair.expression.factory.ValueFactory;
import fr.inria.lille.repair.expression.value.Value;

/**
 * is the generic type of a binary expression
 */
public class BinaryExpressionEvaluator {
	private BinaryExpression binaryExpression;

	BinaryExpressionEvaluator(BinaryExpression binaryExpression) {
		this.binaryExpression = binaryExpression;
	}

	Value eval() {
		try {
			return ValueFactory.create(perform());
		} catch (Throwable e) {
			// ignore exception
		}
		return null;
	}

	private Object perform() {
		Object firstExpression = binaryExpression.getFirstExpression().getValue().getRealValue();
		Value value = binaryExpression.getSecondExpression().getValue();
		Object secondExpression = false;
		if (value != Value.NOVALUE) {
			secondExpression = value.getRealValue();
		}
		switch (binaryExpression.getOperator()) {
		case AND:
			return new And((Boolean) firstExpression, (Boolean) secondExpression).eval();
		case OR:
			return new Or((Boolean) firstExpression, (Boolean) secondExpression).eval();
		case EQ:
			if (firstExpression == null) {
				return secondExpression == null;
			}
			if (firstExpression instanceof Number) {
				if (!(secondExpression instanceof Number)) {
					throw new RuntimeException("Not comparable");
				}
			} else if (secondExpression instanceof Number) {
				if (!(firstExpression instanceof Number)) {
					throw new RuntimeException("Not comparable");
				}
			} else if (firstExpression instanceof Boolean) {
				if (!(secondExpression instanceof Boolean)) {
					throw new RuntimeException("Not comparable");
				}
			} else if (secondExpression instanceof Boolean) {
				if (!(firstExpression instanceof Boolean)) {
					throw new RuntimeException("Not comparable");
				}
			}
			return firstExpression.equals(secondExpression);
		case NEQ:
			if (firstExpression == null) {
				return secondExpression != null;
			}
			if (firstExpression instanceof Number) {
				if (!(secondExpression instanceof Number)) {
					throw new RuntimeException("Not comparable");
				}
			} else if (secondExpression instanceof Number) {
				if (!(firstExpression instanceof Number)) {
					throw new RuntimeException("Not comparable");
				}
			} else if (firstExpression instanceof Boolean) {
				if (!(secondExpression instanceof Boolean)) {
					throw new RuntimeException("Not comparable");
				}
			} else if (secondExpression instanceof Boolean) {
				if (!(firstExpression instanceof Boolean)) {
					throw new RuntimeException("Not comparable");
				}
			}
			return !firstExpression.equals(secondExpression);
		case LESSEQ:
			return new LessEq((Number) firstExpression, (Number) secondExpression).eval();
		case LESS:
			return new Less((Number) firstExpression, (Number) secondExpression).eval();
		case ADD:
			return new Add((Number) firstExpression, (Number) secondExpression).eval();
		case SUB:
			return new Sub((Number) firstExpression, (Number) secondExpression).eval();
		case MULT:
			return new Mul((Number) firstExpression, (Number) secondExpression).eval();
		case DIV:
			return new Div((Number) firstExpression, (Number) secondExpression).eval();
		}
		throw new RuntimeException("Not supported operator " + binaryExpression.getOperator());
	}

	// comparison operator
	class Or {
		private Boolean a;
		private Boolean b;

		Or(Boolean a, Boolean b) {
			this.a = a;
			this.b = b;
		}

		Boolean eval() {
			return a || b;
		}
	}

	class And {
		private Boolean a;
		private Boolean b;

		And(Boolean a, Boolean b) {
			this.a = a;
			this.b = b;
		}

		Boolean eval() {
			return a && b;
		}
	}

	// comparison operator
	class Less {
		private Number a;
		private Number b;

		Less(Number a, Number b) {
			this.a = a;
			this.b = b;
		}

		Boolean eval() {
			if (a instanceof Integer) {
				int firstValue = a.intValue();
				if (b instanceof Integer) {
					return firstValue < b.intValue();
				}
				if (b instanceof Double) {
					return firstValue < b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue < b.longValue();
				}
				if (b instanceof Float) {
					return firstValue < b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue < b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue < b.byteValue();
				}
			} else if (a instanceof Double) {
				double firstValue = a.doubleValue();
				if (b instanceof Integer) {
					return firstValue < b.intValue();
				}
				if (b instanceof Double) {
					return firstValue < b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue < b.longValue();
				}
				if (b instanceof Float) {
					return firstValue < b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue < b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue < b.byteValue();
				}
			} else if (a instanceof Long) {
				long firstValue = a.longValue();
				if (b instanceof Integer) {
					return firstValue < b.intValue();
				}
				if (b instanceof Double) {
					return firstValue < b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue < b.longValue();
				}
				if (b instanceof Float) {
					return firstValue < b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue < b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue < b.byteValue();
				}
			} else if (a instanceof Float) {
				float firstValue = a.floatValue();
				if (b instanceof Integer) {
					return firstValue < b.intValue();
				}
				if (b instanceof Double) {
					return firstValue < b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue < b.longValue();
				}
				if (b instanceof Float) {
					return firstValue < b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue < b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue < b.byteValue();
				}
			} else if (a instanceof Short) {
				short firstValue = a.shortValue();
				if (b instanceof Integer) {
					return firstValue < b.intValue();
				}
				if (b instanceof Double) {
					return firstValue < b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue < b.longValue();
				}
				if (b instanceof Float) {
					return firstValue < b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue < b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue < b.byteValue();
				}
			} else if (a instanceof Byte) {
				byte firstValue = a.byteValue();
				if (b instanceof Integer) {
					return firstValue < b.intValue();
				}
				if (b instanceof Double) {
					return firstValue < b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue < b.longValue();
				}
				if (b instanceof Float) {
					return firstValue < b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue < b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue < b.byteValue();
				}
			}
			return null;
		}
	}

	class LessEq {
		private Number a;
		private Number b;

		LessEq(Number a, Number b) {
			this.a = a;
			this.b = b;
		}

		Boolean eval() {
			if (a instanceof Integer) {
				int firstValue = a.intValue();
				if (b instanceof Integer) {
					return firstValue <= b.intValue();
				}
				if (b instanceof Double) {
					return firstValue <= b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue <= b.longValue();
				}
				if (b instanceof Float) {
					return firstValue <= b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue <= b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue <= b.byteValue();
				}
			} else if (a instanceof Double) {
				double firstValue = a.doubleValue();
				if (b instanceof Integer) {
					return firstValue <= b.intValue();
				}
				if (b instanceof Double) {
					return firstValue <= b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue <= b.longValue();
				}
				if (b instanceof Float) {
					return firstValue <= b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue <= b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue <= b.byteValue();
				}
			} else if (a instanceof Long) {
				long firstValue = a.longValue();
				if (b instanceof Integer) {
					return firstValue <= b.intValue();
				}
				if (b instanceof Double) {
					return firstValue <= b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue <= b.longValue();
				}
				if (b instanceof Float) {
					return firstValue <= b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue <= b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue <= b.byteValue();
				}
			} else if (a instanceof Float) {
				float firstValue = a.floatValue();
				if (b instanceof Integer) {
					return firstValue <= b.intValue();
				}
				if (b instanceof Double) {
					return firstValue <= b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue <= b.longValue();
				}
				if (b instanceof Float) {
					return firstValue <= b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue <= b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue <= b.byteValue();
				}
			} else if (a instanceof Short) {
				short firstValue = a.shortValue();
				if (b instanceof Integer) {
					return firstValue <= b.intValue();
				}
				if (b instanceof Double) {
					return firstValue <= b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue <= b.longValue();
				}
				if (b instanceof Float) {
					return firstValue <= b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue <= b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue <= b.byteValue();
				}
			} else if (a instanceof Byte) {
				byte firstValue = a.byteValue();
				if (b instanceof Integer) {
					return firstValue <= b.intValue();
				}
				if (b instanceof Double) {
					return firstValue <= b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue <= b.longValue();
				}
				if (b instanceof Float) {
					return firstValue <= b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue <= b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue <= b.byteValue();
				}
			}
			return null;
		}
	}

	// Arithmetic operator
	class Add {
		private Number a;
		private Number b;

		Add(Number a, Number b) {
			this.a = a;
			this.b = b;
		}

		Number eval() {
			if (a instanceof Integer) {
				int firstValue = a.intValue();
				if (b instanceof Integer) {
					return firstValue + b.intValue();
				}
				if (b instanceof Double) {
					return firstValue + b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue + b.longValue();
				}
				if (b instanceof Float) {
					return firstValue + b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue + b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue + b.byteValue();
				}
			} else if (a instanceof Double) {
				double firstValue = a.doubleValue();
				if (b instanceof Integer) {
					return firstValue + b.intValue();
				}
				if (b instanceof Double) {
					return firstValue + b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue + b.longValue();
				}
				if (b instanceof Float) {
					return firstValue + b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue + b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue + b.byteValue();
				}
			} else if (a instanceof Long) {
				long firstValue = a.longValue();
				if (b instanceof Integer) {
					return firstValue + b.intValue();
				}
				if (b instanceof Double) {
					return firstValue + b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue + b.longValue();
				}
				if (b instanceof Float) {
					return firstValue + b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue + b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue + b.byteValue();
				}
			} else if (a instanceof Float) {
				float firstValue = a.floatValue();
				if (b instanceof Integer) {
					return firstValue + b.intValue();
				}
				if (b instanceof Double) {
					return firstValue + b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue + b.longValue();
				}
				if (b instanceof Float) {
					return firstValue + b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue + b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue + b.byteValue();
				}
			} else if (a instanceof Short) {
				short firstValue = a.shortValue();
				if (b instanceof Integer) {
					return firstValue + b.intValue();
				}
				if (b instanceof Double) {
					return firstValue + b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue + b.longValue();
				}
				if (b instanceof Float) {
					return firstValue + b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue + b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue + b.byteValue();
				}
			} else if (a instanceof Byte) {
				byte firstValue = a.byteValue();
				if (b instanceof Integer) {
					return firstValue + b.intValue();
				}
				if (b instanceof Double) {
					return firstValue + b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue + b.longValue();
				}
				if (b instanceof Float) {
					return firstValue + b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue + b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue + b.byteValue();
				}
			}
			return 0;
		}
	}

	class Sub {
		private Number a;
		private Number b;

		Sub(Number a, Number b) {
			this.a = a;
			this.b = b;
		}

		Number eval() {
			if (a instanceof Integer) {
				int firstValue = a.intValue();
				if (b instanceof Integer) {
					return firstValue - b.intValue();
				}
				if (b instanceof Double) {
					return firstValue - b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue - b.longValue();
				}
				if (b instanceof Float) {
					return firstValue - b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue - b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue - b.byteValue();
				}
			} else if (a instanceof Double) {
				double firstValue = a.doubleValue();
				if (b instanceof Integer) {
					return firstValue - b.intValue();
				}
				if (b instanceof Double) {
					return firstValue - b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue - b.longValue();
				}
				if (b instanceof Float) {
					return firstValue - b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue - b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue - b.byteValue();
				}
			} else if (a instanceof Long) {
				long firstValue = a.longValue();
				if (b instanceof Integer) {
					return firstValue - b.intValue();
				}
				if (b instanceof Double) {
					return firstValue - b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue - b.longValue();
				}
				if (b instanceof Float) {
					return firstValue - b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue - b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue - b.byteValue();
				}
			} else if (a instanceof Float) {
				float firstValue = a.floatValue();
				if (b instanceof Integer) {
					return firstValue - b.intValue();
				}
				if (b instanceof Double) {
					return firstValue - b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue - b.longValue();
				}
				if (b instanceof Float) {
					return firstValue - b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue - b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue - b.byteValue();
				}
			} else if (a instanceof Short) {
				short firstValue = a.shortValue();
				if (b instanceof Integer) {
					return firstValue - b.intValue();
				}
				if (b instanceof Double) {
					return firstValue - b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue - b.longValue();
				}
				if (b instanceof Float) {
					return firstValue - b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue - b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue - b.byteValue();
				}
			} else if (a instanceof Byte) {
				byte firstValue = a.byteValue();
				if (b instanceof Integer) {
					return firstValue - b.intValue();
				}
				if (b instanceof Double) {
					return firstValue - b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue - b.longValue();
				}
				if (b instanceof Float) {
					return firstValue - b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue - b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue - b.byteValue();
				}
			}
			return 0;
		}
	}

	class Mul {
		private Number a;
		private Number b;

		Mul(Number a, Number b) {
			this.a = a;
			this.b = b;
		}

		Number eval() {
			if (a instanceof Integer) {
				int firstValue = a.intValue();
				if (b instanceof Integer) {
					return firstValue * b.intValue();
				}
				if (b instanceof Double) {
					return firstValue * b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue * b.longValue();
				}
				if (b instanceof Float) {
					return firstValue * b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue * b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue * b.byteValue();
				}
			} else if (a instanceof Double) {
				double firstValue = a.doubleValue();
				if (b instanceof Integer) {
					return firstValue * b.intValue();
				}
				if (b instanceof Double) {
					return firstValue * b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue * b.longValue();
				}
				if (b instanceof Float) {
					return firstValue * b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue * b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue * b.byteValue();
				}
			} else if (a instanceof Long) {
				long firstValue = a.longValue();
				if (b instanceof Integer) {
					return firstValue * b.intValue();
				}
				if (b instanceof Double) {
					return firstValue * b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue * b.longValue();
				}
				if (b instanceof Float) {
					return firstValue * b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue * b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue * b.byteValue();
				}
			} else if (a instanceof Float) {
				float firstValue = a.floatValue();
				if (b instanceof Integer) {
					return firstValue * b.intValue();
				}
				if (b instanceof Double) {
					return firstValue * b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue * b.longValue();
				}
				if (b instanceof Float) {
					return firstValue * b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue * b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue * b.byteValue();
				}
			} else if (a instanceof Short) {
				short firstValue = a.shortValue();
				if (b instanceof Integer) {
					return firstValue * b.intValue();
				}
				if (b instanceof Double) {
					return firstValue * b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue * b.longValue();
				}
				if (b instanceof Float) {
					return firstValue * b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue * b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue * b.byteValue();
				}
			} else if (a instanceof Byte) {
				byte firstValue = a.byteValue();
				if (b instanceof Integer) {
					return firstValue * b.intValue();
				}
				if (b instanceof Double) {
					return firstValue * b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue * b.longValue();
				}
				if (b instanceof Float) {
					return firstValue * b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue * b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue * b.byteValue();
				}
			}
			return 0;
		}
	}

	class Div {
		private Number a;
		private Number b;

		Div(Number a, Number b) {
			this.a = a;
			this.b = b;
		}

		Number eval() {
			if (a instanceof Integer) {
				int firstValue = a.intValue();
				if (b instanceof Integer) {
					return firstValue / b.intValue();
				}
				if (b instanceof Double) {
					return firstValue / b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue / b.longValue();
				}
				if (b instanceof Float) {
					return firstValue / b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue / b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue / b.byteValue();
				}
			} else if (a instanceof Double) {
				double firstValue = a.doubleValue();
				if (b instanceof Integer) {
					return firstValue / b.intValue();
				}
				if (b instanceof Double) {
					return firstValue / b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue / b.longValue();
				}
				if (b instanceof Float) {
					return firstValue / b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue / b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue / b.byteValue();
				}
			} else if (a instanceof Long) {
				long firstValue = a.longValue();
				if (b instanceof Integer) {
					return firstValue / b.intValue();
				}
				if (b instanceof Double) {
					return firstValue / b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue / b.longValue();
				}
				if (b instanceof Float) {
					return firstValue / b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue / b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue / b.byteValue();
				}
			} else if (a instanceof Float) {
				float firstValue = a.floatValue();
				if (b instanceof Integer) {
					return firstValue / b.intValue();
				}
				if (b instanceof Double) {
					return firstValue / b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue / b.longValue();
				}
				if (b instanceof Float) {
					return firstValue / b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue / b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue / b.byteValue();
				}
			} else if (a instanceof Short) {
				short firstValue = a.shortValue();
				if (b instanceof Integer) {
					return firstValue / b.intValue();
				}
				if (b instanceof Double) {
					return firstValue / b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue / b.longValue();
				}
				if (b instanceof Float) {
					return firstValue / b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue / b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue / b.byteValue();
				}
			} else if (a instanceof Byte) {
				byte firstValue = a.byteValue();
				if (b instanceof Integer) {
					return firstValue / b.intValue();
				}
				if (b instanceof Double) {
					return firstValue / b.doubleValue();
				}
				if (b instanceof Long) {
					return firstValue / b.longValue();
				}
				if (b instanceof Float) {
					return firstValue / b.floatValue();
				}
				if (b instanceof Short) {
					return firstValue / b.shortValue();
				}
				if (b instanceof Byte) {
					return firstValue / b.byteValue();
				}
			}
			return 0;
		}
	}
}
