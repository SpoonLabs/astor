package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import spoon.reflect.declaration.CtElement;

/**
 * Represents the default strategy: it does not apply any code transformation.
 * It doesnt check if the ingredient fits
 * 
 * @author Matias Martinez
 *
 */
public class NoIngredientTransformation implements IngredientTransformationStrategy {

	protected static Logger log = Logger.getLogger(NoIngredientTransformation.class.getName());

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient) {

		List<Ingredient> result = new ArrayList<>();

		CtElement elementFromIngredient = ingredient.getCode();

		result.add(new Ingredient(elementFromIngredient));

		return result;
	}

}
