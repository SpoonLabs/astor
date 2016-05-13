package fr.inria.astor.test.repair.evaluation.other;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
/**
 * Fake ingredient strategy used for test
 * @author Matias Martinez
 *
 */
public class FakeIngredientStrategy extends IngredientSearchStrategy {

	

	public FakeIngredientStrategy(IngredientSpace<CtElement, CtCodeElement, String> space) {
		super(space);
	}

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {
		return null;
	}



}
