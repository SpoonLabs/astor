package fr.inria.astor.core.loop.spaces.ingredients.scopes;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * Ingredient space for CtElements used by Astor
 * @author Matias Martinez
 *
 */
public abstract class AstorCtIngredientSpace extends AstorIngredientSpace<CtElement,String, CtCodeElement,String> {

	public AstorCtIngredientSpace() throws JSAPException {
		super();
	}
	
	public AstorCtIngredientSpace(AbstractFixSpaceProcessor<?> processor)
			throws JSAPException {
		super(processor);
	}

	public AstorCtIngredientSpace(List<AbstractFixSpaceProcessor<?>> processors)
			throws JSAPException {
		super(processors);
	}

	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	protected void createFixSpaceFromAClass(CtType root) {
		List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(root);
		AbstractFixSpaceProcessor.mustClone = true;
		//this.setIngredients(root, ingredients);
		determineScopeOfIngredient(ingredients);
	}
	
	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void determineScopeOfIngredient(List<CtCodeElement> ingredients) {
		
		for (CtCodeElement ctCodeElement : ingredients) {
			
			String key = mapKey(ctCodeElement);
			if (getFixSpace().containsKey(key)) {
				getFixSpace().get(key).add(ctCodeElement);
			} else {
				List<CtCodeElement> ingr = new ArrayList<CtCodeElement>();
				ingr.add(ctCodeElement);
				getFixSpace().put(key, ingr);
			}
			
		}
		recreateTypesStructures();
		
	}
	
}
