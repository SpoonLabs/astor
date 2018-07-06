package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Zimin Chen
 *
 */
public class MaxLcsSimSearchStrategy extends IngredientSearchStrategy {

	private List<CtElement> locationsAnalyzed = new ArrayList<>();

	public MaxLcsSimSearchStrategy(IngredientPool space) {
		super(space);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		// Get the string representation of modificationPoint
		String modificationPoint_toString = modificationPoint.getCodeElement().toString().trim();

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

			// Sort the ingredient by normalized_lcs distance
			Collections.sort(ingredientsLocationSort, new sortByLcsDistance(modificationPoint_toString));

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

	// Comparator to sort ingredient by normalized longest common subsequence
	private class sortByLcsDistance implements Comparator<Ingredient> {
		private String modificationPoint_toString;

		sortByLcsDistance(String modificationPoint_toString) {
			this.modificationPoint_toString = modificationPoint_toString;
		}

		public int compare(Ingredient o1, Ingredient o2) {
			double lcs1 = getLcsSimilarity(modificationPoint_toString, o1.getCode().toString().trim());
			double lcs2 = getLcsSimilarity(modificationPoint_toString, o2.getCode().toString().trim());
			// return the reverse since larger value means higher similarity,
			// which should be placed first
			return Double.compare(lcs2, lcs1);
		}
	}

	// Normalized longest common subsequence (LCS) distance.
	// http://heim.ifi.uio.no/%7Edanielry/StringMetric.pdf
	public double getLcsSimilarity(String a, String b) {
		// Exact copy cannot be ingredient, therefore we give it score 0
		if (a.replaceAll("\\s+", "").equals(b.replaceAll("\\s+", ""))) {
			return 0;
		}

		int m = a.length();
		int n = b.length();
		int[][] lcsDistance = new int[m + 1][n + 1];
		// Dynamic programming to compute lcs distance
		for (int i = 0; i <= m; i++) {
			for (int j = 0; j <= n; j++) {
				if (i == 0 || j == 0) {
					lcsDistance[i][j] = 0;
				} else if (a.charAt(i - 1) == b.charAt(j - 1)) {
					lcsDistance[i][j] = lcsDistance[i - 1][j - 1] + 1;
				} else {
					lcsDistance[i][j] = Math.max(lcsDistance[i - 1][j], lcsDistance[i][j - 1]);
				}
			}
		}

		// Normalize lcs distance with max(m,n)
		return (double) lcsDistance[m][n] / Math.max(m, n);
	}

}
