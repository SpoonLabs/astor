package fr.inria.astor.core.entities;

import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpaceStrategy;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class Ingredient {
	
	CtElement code;
	IngredientSpaceStrategy scope;
	
	public Ingredient(CtElement element, IngredientSpaceStrategy scope) {
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

	public IngredientSpaceStrategy getScope() {
		return scope;
	}

	public void setScope(IngredientSpaceStrategy scope) {
		this.scope = scope;
	}
	
}
