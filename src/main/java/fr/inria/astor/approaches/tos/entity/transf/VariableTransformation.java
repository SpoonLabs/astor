package fr.inria.astor.approaches.tos.entity.transf;

import java.util.Map;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class VariableTransformation implements Transformation {

	private VarCombinationForIngredient combination;
	private VarMapping mapping = null;

	Map<VarAccessWrapper, CtVariableAccess> originalMap = null;

	public VariableTransformation(VarCombinationForIngredient varCombinationForIngredient, VarMapping mapping) {
		this.combination = varCombinationForIngredient;
		this.mapping = mapping;
	}

	@Override
	public void apply() {
		Map<String, CtVariable> selectedTransformation = this.combination.getCombination();

		originalMap = VariableResolver.convertIngredient(mapping, selectedTransformation);

	}

	@Override
	public void revert() {
		VariableResolver.resetIngredient(originalMap);

	}

}
