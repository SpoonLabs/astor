package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.AstorCtIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class AstorCtSearchStrategy extends IngredientSearchStrategy {

	public AstorCtSearchStrategy(IngredientSpace space) {
		super(space);
	}

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {
		return null;
	}

}
