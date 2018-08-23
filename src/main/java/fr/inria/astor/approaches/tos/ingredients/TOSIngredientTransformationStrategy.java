package fr.inria.astor.approaches.tos.ingredients;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.TOSInstance;
import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.CacheTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TOSIngredientTransformationStrategy extends CacheTransformationStrategy
		implements IngredientTransformationStrategy {

	PatchGenerator patchGenerator = new PatchGenerator();

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient) {

		if (this.alreadyTransformed(modificationPoint, ingredient)) {
			return getCachedTransformations(modificationPoint, ingredient);
		}

		TOSEntity tos = (TOSEntity) ingredient;
		List<Ingredient> ingredientTransformed = new ArrayList<>();
		for (Placeholder placeholder : tos.getPlaceholders()) {
			List<Transformation> transpl = placeholder.visit(modificationPoint, patchGenerator);

			if (ingredientTransformed.isEmpty()) {
				for (Transformation transformation : transpl) {
					TOSInstance tosIn = new TOSInstance(tos.getCode(), tos);
					tosIn.getTransformations().add(transformation);
					ingredientTransformed.add(tosIn);
				}

			} else {
				List<Ingredient> newingredientTransformed = new ArrayList<>();
				for (Transformation transformation : transpl) {
					//
					for (Ingredient ingredient_i : ingredientTransformed) {
						TOSInstance tosIngredient = (TOSInstance) ingredient_i;
						TOSInstance tosIn = new TOSInstance(tos.getCode(), tos);
						tosIn.getTransformations().addAll(tosIngredient.getTransformations());
						tosIn.getTransformations().add(transformation);
						newingredientTransformed.add(tosIn);
					}
				}
				ingredientTransformed = newingredientTransformed;
			}
		}

		storingIngredients(modificationPoint, ingredient, ingredientTransformed);
		return ingredientTransformed;
	}
}
