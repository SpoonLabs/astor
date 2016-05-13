package fr.inria.astor.core.entities;

import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class Ingredient {
	
	CtElement code;
	IngredientSpaceScope scope;
	
	public Ingredient(CtElement element, IngredientSpaceScope scope) {
		super();
		this.code = element;
		this.scope = scope;
	}

	public CtElement getCode() {
		return code;
	}

	public void setCode(CtElement element) {
		this.code = element;
	}

	public IngredientSpaceScope getScope() {
		return scope;
	}

	public void setScope(IngredientSpaceScope scope) {
		this.scope = scope;
	}
	
}
