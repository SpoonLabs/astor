package fr.inria.astor.core.loop.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.declaration.CtElement;

/**
 * Represents the default strategy: it does not apply any code transformation. It only return the ingredient as is.
 * @author Matias Martinez
 *
 */
public class DefaultIngredientTransformation implements IngredientTransformationStrategy {

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient) {
		
		List<Ingredient> result = new ArrayList<>();
		
		CtElement elementFromIngredient = ingredient.getCode();

		boolean fit = VariableResolver.fitInPlace(modificationPoint.getContextOfModificationPoint(),
				elementFromIngredient);
		
		if (fit) {
			IngredientSpaceScope scope = VariableResolver.determineIngredientScope(modificationPoint.getCodeElement(),
					elementFromIngredient);
		
			result.add(new Ingredient(elementFromIngredient, scope));
		}
		//Only one ingredient was to be returned (the original)
		return result;
	}

}
