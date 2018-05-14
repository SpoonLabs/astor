package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;

/**
 * Fake ingredient strategy used for test
 * @author Matias Martinez
 *
 */
public class FakeIngredientStrategy extends IngredientSearchStrategy {

	

	public FakeIngredientStrategy(IngredientPool space) {
		super(space);
	}

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {
		return null;
	}



}
