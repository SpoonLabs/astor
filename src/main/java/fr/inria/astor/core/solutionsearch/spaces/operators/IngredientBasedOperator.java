package fr.inria.astor.core.solutionsearch.spaces.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class IngredientBasedOperator extends AstorOperator {

	@Override
	public final boolean needIngredient() {
		return true;
	}

	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint, Ingredient ingredient,
			IngredientTransformationStrategy transformationStrategy) {

		List<OperatorInstance> operator = new ArrayList<>();

		if (ingredient == null) {
			log.error("The ingredient cannot be null");
			return operator;
		}

		if (transformationStrategy != null) {

			List<Ingredient> ingredientsAfterTransformation = transformationStrategy.transform(modificationPoint,
					ingredient);

			for (Ingredient ingredientTransformed : ingredientsAfterTransformation) {

				OperatorInstance operatorInstance = this.createOperatorInstance(modificationPoint);
				operatorInstance.setModified(ingredientTransformed.getCode());
				operatorInstance.setIngredient(ingredientTransformed);
				operator.add(operatorInstance);

			}
		} else {// No transformation
			OperatorInstance opInstance = createOperatorInstance(modificationPoint);
			opInstance.setModified(ingredient.getCode());
			opInstance.setIngredient(ingredient);
		}
		return operator;
	}

	protected OperatorInstance createOperatorInstance(ModificationPoint mp) {
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(mp.getCodeElement());
		operation.setOperationApplied(this);
		operation.setModificationPoint(mp);
		operation.defineParentInformation(mp);
		return operation;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		throw new IllegalAccessError(
				"An ingredient-based operator needs an ingredient. This method could never be called.");
	}

}
