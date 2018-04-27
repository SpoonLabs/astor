package fr.inria.astor.approaches.tos.entity.transf;

import java.util.List;
import java.util.Map;

import fr.inria.astor.approaches.tos.entity.placeholders.VariablePlaceholder;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtVariableAccess;
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
	VariablePlaceholder varplaceholder = null;

	public VariableTransformation(VariablePlaceholder varplaceholder, MapList<String, CtVariableAccess> placeholders,
			VarCombinationForIngredient varCombinationForIngredient, VarMapping mapping) {
		this.varplaceholder = varplaceholder;
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

	public String toString() {
		return this.getClass().getSimpleName() + " (" + toStringMap() + ") ";
	}

	public String toStringMap() {
		String r = "";
		for (String ph_name : this.varplaceholder.getPalceholders().keySet()) {

			List<CtVariableAccess> va = this.varplaceholder.getPalceholders().get(ph_name);
			CtVariableAccess va1 = va.get(0);

			CtVariable vcomb = this.combination.getCombination().get(va1.getVariable().getSimpleName());
			r += vcomb.getSimpleName() + " -->  " + ph_name;
			r += ", ";

		}

		return r;
	}
}
