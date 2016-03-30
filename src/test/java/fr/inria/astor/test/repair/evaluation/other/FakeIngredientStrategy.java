package fr.inria.astor.test.repair.evaluation.other;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.FixIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
/**
 * Fake ingredient strategy used for test
 * @author Matias Martinez
 *
 */
public class FakeIngredientStrategy extends IngredientStrategy {

	

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {
		return null;
	}

	@Override
	public void refineSpaceForProgramVariant(ProgramVariant variant) {
	

	}

	@Override
	public void setIngredientSpace(FixIngredientSpace<CtElement, CtCodeElement, String> space) {
		
	}

}
