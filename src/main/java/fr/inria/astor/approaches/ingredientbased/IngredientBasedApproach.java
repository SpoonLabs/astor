package fr.inria.astor.approaches.ingredientbased;

import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface IngredientBasedApproach {

	// Ingredients pools
	public IngredientSpace getIngredientPool();

	public void setIngredientPool(IngredientSpace ingredientPool);

	// Ingredients transformations
	public IngredientTransformationStrategy getIngredientTransformationStrategy();

	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy);

	// Selection of Ingredient
	public IngredientSearchStrategy getIngredientSearchStrategy();

	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy);
}
