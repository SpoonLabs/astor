package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.AstorIngredientSpace;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * Ingredient space for CtElements used by Astor
 * @author Matias Martinez
 *
 */
public abstract class AstorCtIngredientSpace extends AstorIngredientSpace<CtElement,String, CtCodeElement,String,CtCodeElement> {

	boolean discartDuplicates = true;
	
	public AstorCtIngredientSpace() throws JSAPException {
		super();
	}
	
	public AstorCtIngredientSpace(TargetElementProcessor<?> processor)
			throws JSAPException {
		super(processor);
	}

	public AstorCtIngredientSpace(List<TargetElementProcessor<?>> processors)
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
		TargetElementProcessor.mustClone = true;
		determineScopeOfIngredient(ingredients);
	}
	
	/**
	 * Creation of fix space from a list of ctelements.
	 * 
	 */
	public void determineScopeOfIngredient(List<CtCodeElement> ingredients) {
		
		for (CtCodeElement ctCodeElement : ingredients) {
			
			storeInSpace(ctCodeElement);
		}
		recreateTypesStructures();
		
	}

	public void storeInSpace(CtCodeElement ctCodeElement) {
		String key = mapKey(ctCodeElement);
		List<CtCodeElement> ingredientsKey = getFixSpace().get(key);
		if (!getFixSpace().containsKey(key)) {
			ingredientsKey = new CacheList<CtCodeElement>();
			getFixSpace().put(key, ingredientsKey);
		}
		if(!discartDuplicates || !ingredientsKey.contains(ctCodeElement)){
			ingredientsKey.add(ctCodeElement);
		}
	}
	
}
