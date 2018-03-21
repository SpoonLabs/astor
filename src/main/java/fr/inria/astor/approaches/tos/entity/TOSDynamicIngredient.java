package fr.inria.astor.approaches.tos.entity;

import fr.inria.astor.core.loop.spaces.ingredients.transformations.DynamicIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import spoon.reflect.code.CtCodeElement;

/**
 * Represents an ingredient that cames from a TOS
 * 
 * @author Matias Martinez
 *
 */
public class TOSDynamicIngredient extends DynamicIngredient {

	TOSEntity tosDerived = null;

	public TOSDynamicIngredient(VarCombinationForIngredient combination, VarMapping mapping,
			CtCodeElement baseIngredient, TOSEntity tos) {

		super(combination, mapping, baseIngredient);
		this.tosDerived = tos;

	}

}
