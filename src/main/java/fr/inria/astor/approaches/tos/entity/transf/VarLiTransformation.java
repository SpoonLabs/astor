package fr.inria.astor.approaches.tos.entity.transf;

import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableAccess;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarLiTransformation implements Transformation {

	@SuppressWarnings("rawtypes")
	CtVariableAccess previousVariable = null;
	CtLiteral newLiteral = null;

	public VarLiTransformation(CtVariableAccess previousVariable, CtLiteral newLiteral) {
		super();
		this.previousVariable = previousVariable;
		this.newLiteral = newLiteral;
	}

	@Override
	public void apply() {
		previousVariable.replace(newLiteral);

	}

	@Override
	public void revert() {
		newLiteral.replace(previousVariable);

	}

	public String toString() {
		return this.getClass().getSimpleName() + " " + newLiteral + " --> "
				+ previousVariable;
	}
}
