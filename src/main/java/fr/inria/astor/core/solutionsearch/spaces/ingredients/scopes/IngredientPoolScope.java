package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;
/**
 * Enumeration of ingredients' types
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public enum IngredientPoolScope {
	//Order from most restrictive to less
	LOCAL,
	PACKAGE,
	GLOBAL,
	CUSTOM;
}
