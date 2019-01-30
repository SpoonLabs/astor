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
	public boolean needIngredient() {
		return true;
	}

	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint, Ingredient ingredient,
			IngredientTransformationStrategy transformationStrategy) {

		List<OperatorInstance> operatorIntances = new ArrayList<>();

		if (ingredient == null) {
			log.error("The ingredient cannot be null");
			return operatorIntances;
		}

		if (transformationStrategy != null) {

			List<Ingredient> ingredientsAfterTransformation = transformationStrategy.transform(modificationPoint,
					ingredient);

			if (ingredientsAfterTransformation == null) {
				log.debug("Empty transformations mp " + modificationPoint + " " + ingredient);
				return operatorIntances;
			}

			for (Ingredient ingredientTransformed : ingredientsAfterTransformation) {

				OperatorInstance operatorInstance = this.createOperatorInstance(modificationPoint,
						ingredientTransformed);
				if (operatorInstance != null) {
					operatorIntances.add(operatorInstance);
				}

			}
		} else {// No transformation
			OperatorInstance opInstance = createOperatorInstance(modificationPoint);
			opInstance.setModified(ingredient.getCode());
			opInstance.setIngredient(ingredient);
		}
		return operatorIntances;
	}

	protected OperatorInstance createOperatorInstance(ModificationPoint modificationPoint, Ingredient ingredient) {
		OperatorInstance operatorInstance = this.createOperatorInstance(modificationPoint);
		operatorInstance.setModified(ingredient.getCode());
		operatorInstance.setIngredient(ingredient);
		return operatorInstance;

	}

	protected OperatorInstance createOperatorInstance(ModificationPoint mp) {
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(mp.getCodeElement());
		operation.setOperationApplied(this);
		operation.setModificationPoint(mp);
		return operation;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		throw new IllegalAccessError(
				"An ingredient-based operator needs an ingredient. This method could never be called.");
	}

}
