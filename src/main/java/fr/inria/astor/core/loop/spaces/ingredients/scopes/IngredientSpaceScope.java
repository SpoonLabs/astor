package fr.inria.astor.core.loop.spaces.ingredients.scopes;
/**
 * Enumeration of ingredients' types
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public enum IngredientSpaceScope {
	//Order from most restrictive to less
	LOCAL,
	PACKAGE,
	GLOBAL,
	CUSTOM;
}
