package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.Map;

import fr.inria.astor.core.entities.Ingredient;
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
 * Ingredient that transforms the code dynamically, that it is, when the
 * ingredient will be used.
 * 
 * @author Matias Martinez
 *
 */
public class DynamicIngredient extends Ingredient {

	private VarCombinationForIngredient combination;
	private VarMapping mapping = null;

	public DynamicIngredient(VarCombinationForIngredient combination, VarMapping mapping,
			CtCodeElement baseIngredient) {
		super(null);
		this.combination = combination;
		this.mapping = mapping;
		this.derivedFrom = baseIngredient;
	}

	@Override
	public CtElement getCode() {

		if (this.ingredientCode == null) {
			Map<String, CtVariable> selectedTransformation = this.combination.getCombination();

			Map<VarAccessWrapper, CtVariableAccess> originalMap = VariableResolver.convertIngredient(mapping,
					selectedTransformation);
			// Cloned transformed element
			this.ingredientCode = MutationSupporter.clone((CtCodeElement) this.getDerivedFrom());

			VariableResolver.resetIngredient(originalMap);
		}
		return this.ingredientCode;
	}


	public VarCombinationForIngredient getCombination() {
		return combination;
	}

	public void setCombination(VarCombinationForIngredient combination) {
		this.combination = combination;
	}

	public VarMapping getMapping() {
		return mapping;
	}

	public void setMapping(VarMapping mapping) {
		this.mapping = mapping;
	}

}
