package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import spoon.reflect.declaration.CtElement;

/**
 * Represents the default strategy: it does not apply any code transformation.
 * It only return the ingredient as is in case it fix.
 * 
 * @author Matias Martinez
 *
 */
public class NoIngredientTransformationWithCheck implements IngredientTransformationStrategy {

	protected static Logger log = Logger.getLogger(NoIngredientTransformationWithCheck.class.getName());

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient) {

		List<Ingredient> result = new ArrayList<>();

		CtElement elementFromIngredient = ingredient.getCode();

		boolean fit = VariableResolver.fitInPlace(modificationPoint.getContextOfModificationPoint(),
				elementFromIngredient);

		if (fit) {
			IngredientPoolScope scope = VariableResolver.determineIngredientScope(modificationPoint.getCodeElement(),
					elementFromIngredient);

			boolean changeShadow = VariableResolver.changeShadowedVars(modificationPoint.getCodeElement(),
					elementFromIngredient);
			if (changeShadow) {
				log.debug("Transforming shadowed variable in " + elementFromIngredient);
			}

			// Only one ingredient was to be returned (the original)
			result.add(new Ingredient(elementFromIngredient, scope));
		}
		return result;
	}

}
