package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * Ingredient space for CtElements used by Astor
 * @author Matias Martinez
 *
 */
public abstract class AstorCtIngredientPool extends IngredientPoolLocationType<CtElement,String, Ingredient,String,CtCodeElement> {

	boolean discartDuplicates = true;
	
	public AstorCtIngredientPool() throws JSAPException {
		super();
	}
	
	public AstorCtIngredientPool(TargetElementProcessor<?> processor)
			throws JSAPException {
		super(processor);
	}

	public AstorCtIngredientPool(List<TargetElementProcessor<?>> processors)
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
		List<Ingredient> ingredientsKey = getFixSpace().get(key);
		if (!getFixSpace().containsKey(key)) {
			ingredientsKey = new CacheList<Ingredient>();
			getFixSpace().put(key, ingredientsKey);
		}
		Ingredient ingredientTOInsert = new Ingredient(ctCodeElement);
		if(!discartDuplicates || !ingredientsKey.contains(ingredientTOInsert)){
			ingredientsKey.add(ingredientTOInsert);
		}
	}
	
}
