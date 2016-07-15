package fr.inria.astor.test.repair.evaluation.other;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.AstorCtIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;

/**
 * Fake ingredient strategy used for test
 * @author Matias Martinez
 *
 */
public class FakeIngredientStrategy extends IngredientSearchStrategy {

	

	public FakeIngredientStrategy(IngredientSpace space) {
		super(space);
	}

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {
		return null;
	}



}
