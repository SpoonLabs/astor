package fr.inria.astor.core.loop.spaces.ingredients;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

/**
 * Abstract class that represent a strategy to pick an ingredient from the search space.
 * Astor interacts using this interface for demanding ingredients.
 * @author Matias Martinez
 *
 */
public abstract class IngredientStrategy {
	
	
	
	/**
	 * Method that returns an Ingredient from the ingredient space given a modification point and a 
	 * Operator
	 * @param modificationPoint point to be modified using an ingredient
	 * @param operationType operation applied to the moint
	 * @return an ingredient
	 */
	public abstract Ingredient getFixIngredient(ModificationPoint modificationPoint, 
			AstorOperator operationType);
	
	/**
	 * Initialize the strategy, which in turns, will create the ingredient space
	 * @param variant a program variant is necessary for creating initialize the strategy and the space. 
	 */
	public abstract void initIngredientSpace(ProgramVariant variant);
	
	/**
	 * The strategy receives as parameter the FixSpace
	 * @param space
	 */
	public abstract void setIngredientSpace(FixIngredientSpace<CtElement, CtCodeElement, String> space );

}
