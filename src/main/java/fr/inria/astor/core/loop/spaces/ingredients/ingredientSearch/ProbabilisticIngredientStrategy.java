package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ProbabilisticIngredientStrategy extends EfficientIngredientStrategy {

	public ProbabilisticIngredientStrategy(IngredientSpace space) {
		super(space);
	}

	@Override
	protected Ingredient getOneIngredientFromList(List<Ingredient> ingredientsAfterTransformation) {
	
		if(ingredientsAfterTransformation.isEmpty()){
			log.debug("No more elements from the ingredients space");
			return null;
		}
		log.debug(String.format("Obtaining the best element out of %d: %s",ingredientsAfterTransformation.size(), ingredientsAfterTransformation.get(0).getCode()));
		//Return the first one
		return ingredientsAfterTransformation.get(0);
	}


	
}
