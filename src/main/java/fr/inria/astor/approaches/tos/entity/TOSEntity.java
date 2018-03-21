package fr.inria.astor.approaches.tos.entity;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import spoon.reflect.declaration.CtElement;

/**
 * Abstract class of a TOS, which, in turns, is an ingredient.
 * 
 * @author Matias Martinez
 *
 */
public abstract class TOSEntity extends Ingredient {

	public TOSEntity(CtElement code, IngredientSpaceScope scope, CtElement derivedFrom) {
		super(code, scope, derivedFrom);
	}

	public TOSEntity(CtElement element, IngredientSpaceScope scope) {
		super(element, scope);
	}

	public TOSEntity(CtElement element) {
		super(element);
	}

}
