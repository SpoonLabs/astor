package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;

import java.util.ArrayList;
import java.util.List;

import fr.inria.lille.repair.expression.combination.Operator;
import spoon.reflect.code.UnaryOperatorKind;

public enum UnaryOperatorSpoon implements Operator {
	INV(Boolean.class, "!", UnaryOperatorKind.NOT, OperatorPosition.PRE);

	/*
	 * PREINC(Number.class, "++", OperatorPosition.PRE), POSTINC(Number.class, "++",
	 * OperatorPosition.POST), PREDEC(Number.class, "--", OperatorPosition.PRE),
	 * POSTDEC(Number.class, "--", OperatorPosition.POST)
	 */

	private final Class returnType;
	private final String symbol;
	private final UnaryOperatorKind op;
	private final OperatorPosition position;

	/**
	 *
	 */
	UnaryOperatorSpoon(Class returnType, String symbol, UnaryOperatorKind op, OperatorPosition position) {
		this.returnType = returnType;
		this.symbol = symbol;
		this.position = position;
		this.op = op;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public Class getReturnType() {
		return returnType;
	}

	@Override
	public List<Class> getTypeParameters() {
		List<Class> output = new ArrayList<>();
		output.add(boolean.class);
		return output;
	}

	public OperatorPosition getPosition() {
		return position;
	}

	public enum OperatorPosition {
		PRE, POST
	}

	public UnaryOperatorKind getOp() {
		return op;
	}
}
