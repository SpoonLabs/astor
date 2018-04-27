package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
/**
 * Strategy to Transformation an ingredient
 * @author Matias Martinez
 *
 */
public interface IngredientTransformationStrategy extends AstorExtensionPoint {

	
	/**
	 * 
	 * @param modificationPoint
	 * @param ingredient
	 * @return
	 */
	public  List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient);
}
