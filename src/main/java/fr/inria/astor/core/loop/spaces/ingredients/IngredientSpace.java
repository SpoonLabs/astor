package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import spoon.reflect.declaration.CtElement;

/**
 * Representation of Fix Location Space.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 *         L is the location of the code ingredient (e.g., ingredient from File
 *         A)
 * 
 *         I the content of the ingredient (e.g., a= b+c;) T type of the
 *         ingredient (e.g., a statement, if, a while, etc)
 */
public interface IngredientSpace<L extends Object, I extends CtElement, T extends Object> {

	/**
	 * Creates the Space using the classes from a Variant
	 * 
	 * @param affected
	 * 
	 * @param all
	 *            corresponds to all types from the program under analysis.
	 */
	public void defineSpace(ProgramVariant variant);

	/**
	 * Return list of ingredients from location
	 * 
	 * @param location
	 * @return
	 */
	public List<I> getIngredients(L location);

	/**
	 * Return list of ingredient of type from location
	 * 
	 * @param location
	 * @param type
	 * @return
	 */
	public List<I> getIngredients(L location, T type);

	public IngredientSpaceScope spaceScope();

}
