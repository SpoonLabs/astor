package fr.inria.astor.approaches.tos.entity.transf;

import fr.inria.astor.approaches.tos.entity.placeholders.VarLiPlaceholder;
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
	VarLiPlaceholder varLiPlaceholder = null;

	public VarLiTransformation(VarLiPlaceholder varLiPlaceholder, CtVariableAccess previousVariable,
			CtLiteral newLiteral) {
		super();
		this.varLiPlaceholder = varLiPlaceholder;
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
		return this.getClass().getSimpleName() + " (" + newLiteral + " --> "
				+ this.varLiPlaceholder.getPlaceholder_name() + ") ";
	}
}
