package fr.inria.astor.core.ingredientbased;

import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;

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
	public IngredientTransformationStrategy getIngredientTransformationStrategy() throws Exception;

	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy);

	// Selection of Ingredient
	public IngredientSearchStrategy getIngredientSearchStrategy();

	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy);
}
