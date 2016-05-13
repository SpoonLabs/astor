package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.List;
import java.util.Map;

import fr.inria.astor.core.entities.ProgramVariant;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * Representation of Fix Location Space.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 *         L is the location I the ingredient T type
 */
public interface FixIngredientSpace<L extends Object, I extends CtElement, T extends Object> {

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
	 * Return an Ingredient taken from a given location
	 * 
	 * @param location
	 * @return ingredient
	 */
	public I getElementFromSpace(L location);

	/**
	 * Return an Ingredient of type taken from a given location
	 * 
	 * @param location
	 * @return ingredient
	 */
	public I getElementFromSpace(L location, T type);

	/**
	 * Return list of ingredients from location
	 * 
	 * @param location
	 * @return
	 */
	public List<I> getFixSpace(L location);

	/**
	 * Return list of ingredient of type from location
	 * @param location
	 * @param type
	 * @return
	 */
	public List<I> getFixSpace(L location, T type);

	public IngredientSpaceScope spaceScope();

	public Map<?, List<?>> getSpace();

	/**
	 * Return the locations considered for creating the space.
	 * @return
	 */
	public List<L> locationsConsidered();

}
