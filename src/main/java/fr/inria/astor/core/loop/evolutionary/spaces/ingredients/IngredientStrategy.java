package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class IngredientStrategy {
	
	public abstract Ingredient getFixIngredient(ModificationPoint modificationPoint, 
			AstorOperator operationType);
	
	public abstract void init(ProgramVariant variant);

}
