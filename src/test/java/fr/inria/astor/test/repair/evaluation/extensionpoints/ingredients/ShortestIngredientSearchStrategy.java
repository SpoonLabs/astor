package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
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

	public ShortestIngredientSearchStrategy(IngredientPool space) {
		super(space);

	}

	@SuppressWarnings("unchecked")
	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		// We get ingredients that we can use to modify the point.
		List<Ingredient> ingredientsLocation = null;
		if (operationType instanceof ReplaceOp)
			// we specify the kind of operator, so, ingredients are returned
			// according to it.
			ingredientsLocation = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(),
					modificationPoint.getCodeElement().getClass().getSimpleName());
		else {
			// if we do not specify the kind of operator to apply to the point,
			// any kind of ingredient can be returned
			ingredientsLocation = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());

		}
		if (ingredientsLocation == null || ingredientsLocation.isEmpty()) {
			return null;
		}
		// We store the location to avoid sorting the ingredient twice.
		if (!locationsAnalyzed.contains(modificationPoint.getCodeElement())) {
			locationsAnalyzed.add(modificationPoint.getCodeElement());
			// We create the list to reorder the ingredients without modifying
			// the original.
			List<Ingredient> ingredientsLocationSort = new ArrayList<>(ingredientsLocation);

			// We have never analyze this location, let's sort the ingredients.
			Collections.sort(ingredientsLocationSort, new Comparator<Ingredient>() {

				@Override
				public int compare(Ingredient o1, Ingredient o2) {
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
			CtElement element = ingredientsLocation.get(0).getCode();
			// we remove it from space
			ingredientsLocation.remove(0);
			return new Ingredient(element, this.ingredientSpace.spaceScope());
		} // any ingredient
		return null;
	}

}
