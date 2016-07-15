package fr.inria.astor.test.repair.evaluation.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.AstorCtIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

/**
 * Ingredient navigation strategy created for testing. The strategy returns the
 * smallest ingredient in term of number of chars. Once it returns one
 * ingredient, it removes it from the space.
 * 
 * @author Matias Martinez
 *
 */
public class ShortestIngredientSearchStrategy extends IngredientSearchStrategy {


	private List<CtElement> locationsAnalyzed = new ArrayList<>();

	public ShortestIngredientSearchStrategy(IngredientSpace space) {
		super(space);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		// We get ingredients that we can use to modify the point.
		List<CtCodeElement> ingredientsLocation = null;
		if (operationType == null)
			//if we do not specify the kind of operator to apply to the point, any kind of ingredient can be returned
			ingredientsLocation = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());
		else {
			//we specify the kind of operator, so, ingredients are returned according to it.
			ingredientsLocation = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(),
					modificationPoint.getCodeElement().getClass().getSimpleName());

		}
		// We store the location to avoid sorting the ingredient twice.
		if (!locationsAnalyzed.contains(modificationPoint.getCodeElement())) {
			locationsAnalyzed.add(modificationPoint.getCodeElement());
			//We create the list to reorder the ingredients without modifying the original.
			List<CtCodeElement> ingredientsLocationSort = new ArrayList(ingredientsLocation);
			
			// We have never analyze this location, let's sort the ingredients.
			Collections.sort(ingredientsLocationSort, new Comparator<CtCodeElement>() {

				@Override
				public int compare(CtCodeElement o1, CtCodeElement o2) {
					return Integer.compare(o1.toString().length(), o2.toString().length());
				}
			});
			// We reintroduce the sorted list ingredient into the space
			this.ingredientSpace.setIngredients(modificationPoint.getCodeElement(), ingredientsLocationSort);
			ingredientsLocation = ingredientsLocationSort;
		}
		int size = ingredientsLocation.size();
		if (size > 0) {
			// We get the smaller element
			CtCodeElement element = ingredientsLocation.get(0);
			// we remove it from space
			ingredientsLocation.remove(0);
			return new Ingredient(element, this.ingredientSpace.spaceScope());
		}//any ingredient
		return null;
	}

}
