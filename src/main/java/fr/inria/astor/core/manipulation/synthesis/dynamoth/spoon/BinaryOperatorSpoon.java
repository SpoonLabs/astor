package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;

import java.util.Arrays;
import java.util.List;

import fr.inria.lille.repair.expression.combination.Operator;
import spoon.reflect.code.BinaryOperatorKind;

/**
 *
 */
public enum BinaryOperatorSpoon implements Operator {
	AND(Boolean.class, "&&", BinaryOperatorKind.AND, Boolean.class, Boolean.class, false),
	OR(Boolean.class, "||", BinaryOperatorKind.OR, Boolean.class, Boolean.class, false),
	EQ(Boolean.class, "==", BinaryOperatorKind.EQ, Object.class, Object.class, true),
	NEQ(Boolean.class, "!=", BinaryOperatorKind.NE, Object.class, Object.class, true),
	LESSEQ(Boolean.class, "<=", BinaryOperatorKind.LE, Number.class, Number.class, false),
	LESS(Boolean.class, "<", BinaryOperatorKind.LT, Number.class, Number.class, false),
	// GREATER(Boolean.class, ">", Number.class, Number.class, false),
	// GREATEREQ(Boolean.class, ">=", Number.class, Number.class, false),

	ADD(Number.class, "+", BinaryOperatorKind.PLUS, Number.class, Number.class, true),
	SUB(Number.class, "-", BinaryOperatorKind.MINUS, Number.class, Number.class, false),
	MULT(Number.class, "*", BinaryOperatorKind.MUL, Number.class, Number.class, true),
	DIV(Number.class, "/", BinaryOperatorKind.DIV, Number.class, Number.class, false);
	// Operator MOD(Number.class, "%", Number.class, Number.class);

	private final Class returnType;
	private final String symbol;
	private final BinaryOperatorKind opKind;
	private final Class param1;
	private final Class param2;
	private final boolean isCommutative;

	/**
	 *
	 */
	BinaryOperatorSpoon(Class returnType, String symbol, BinaryOperatorKind opKind, Class param1, Class param2,
			boolean isCommutative) {
		this.returnType = returnType;
		this.symbol = symbol;
		this.opKind = opKind;
		this.param1 = param1;
		this.param2 = param2;
		this.isCommutative = isCommutative;
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	public boolean isCommutative() {
		return this.isCommutative;
	}

	@Override
	public Class getReturnType() {
		return returnType;
	}

	@Override
	public List<Class> getTypeParameters() {
		return Arrays.asList(getParam1(), getParam2());
	}

	public Class getParam1() {
		return param1;
	}

	public Class getParam2() {
		return param2;
	}

	public BinaryOperatorKind getOpKind() {
		return opKind;
	}

}
