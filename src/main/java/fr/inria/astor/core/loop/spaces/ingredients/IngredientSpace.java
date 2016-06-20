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
 *       Q type of the element to modify using an ingredient from this space.
 *       
 *        K location of the ingredient according to the space
 *        
 *         I ingredient (e.g., a= b+c;) 
 *         
 *         T type of the ingredient (e.g., a statement, if, a while, etc)
 */
public interface IngredientSpace<Q extends Object, K extends Object, I extends CtElement, T extends Object> {

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
	 * @param elementToModify
	 * @return
	 */
	public List<I> getIngredients(Q elementToModify);

	/**
	 * Return list of ingredient of type from location
	 * 
	 * @param elementToModify
	 * @param type
	 * @return
	 */
	public List<I> getIngredients(Q elementToModify, T type);
	
	/**
	 * Set the list of ingredient in the location.
	 * @param elementToModify
	 * @param ingredients
	 */
	public void setIngredients(Q elementToModify, List<I> ingredients);
	
	public IngredientSpaceScope spaceScope();
	
	/**
	 * For a given piece of code Q, it returns the location according to the scope.
	 * For instance, if the scope of the Space is file, it returns the file name that contains Q, 
	 * if the scope is package, it returns the package where Q is located.
	 * @param elementToModify
	 * @return
	 */
	public K calculateLocation(Q elementToModify);
	
	/**
	 * Get all the locations for a given scope.
	 * For instance, if the scope is 'Package' it will return all package with ingredients.
	 * If the scope is method, it returns all methods with at least one ingredient.
	 * @return
	 */
	public List<K> getLocations();

}
