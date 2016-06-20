package fr.inria.astor.core.loop.spaces.ingredients;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;

/**
 * Abstract class that represent a strategy to pick an ingredient from the
 * search space. Astor interacts using this interface for demanding ingredients.
 * 
 * @author Matias Martinez
 *
 */
public abstract class IngredientSearchStrategy {

	protected IngredientSpace ingredientSpace = null;

	/**
	 * The strategy receives as parameter the FixSpace
	 * 
	 * @param space
	 */
	public IngredientSearchStrategy(IngredientSpace space) {
		super();
		this.ingredientSpace = space;
	}

	/**
	 * Method that returns an Ingredient from the ingredient space given a
	 * modification point and a Operator
	 * 
	 * @param modificationPoint
	 *            point to be modified using an ingredient
	 * @param operationType
	 *            operation applied to the modif point
	 * @return an ingredient
	 */
	public abstract Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType);

	public IngredientSpace getIngredientSpace() {
		return ingredientSpace;
	}

}
