package fr.inria.astor.core.ingredientbased;

import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface IngredientBasedApproach {

	// Ingredients pools
	public IngredientPool getIngredientPool();

	public void setIngredientPool(IngredientPool ingredientPool);

	// Ingredients transformations
	public IngredientTransformationStrategy getIngredientTransformationStrategy() throws Exception;

	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy);

	// Selection of Ingredient
	public IngredientSearchStrategy getIngredientSearchStrategy();

	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy);
}
