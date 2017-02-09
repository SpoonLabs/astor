package fr.inria.astor.core.loop.spaces.ingredients.scopes;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * Ingredient Space that uses CtElements as locations
 * @author Matias Martinez
 *
 */
public class CtLocationIngredientSpace extends AstorIngredientSpace<CtElement,CtElement, CtCodeElement,String> {

	/**
	 * This class indicate the scope of a ingredient search.
	 * If the class is a CtClass means we group ingredients by class, 
	 * if it is a CtBlock, we group ingredients according to blocks, etc.
	 */
	Class ctElementForSplitSpace = CtClass.class;
	
	public CtLocationIngredientSpace() throws JSAPException {
		super();
	}

	public CtLocationIngredientSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super(processor);
	}

	public CtLocationIngredientSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super(processors);
	
	}

	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> affected = variant.getAffectedClasses();
			for (CtType<?> CtType : affected) {
					this.createFixSpaceFromAClass(CtType);
			}
				
	}

	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	protected void createFixSpaceFromAClass(CtType root) {
		List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(root);
		AbstractFixSpaceProcessor.mustClone = true;
		determineScopeOfIngredient(ingredients);
	}
	
	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void determineScopeOfIngredient(List<CtCodeElement> ingredients) {
		
		for (CtCodeElement ctCodeElement : ingredients) {
			
			CtElement key = mapKey(ctCodeElement);
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
	@Override
	public IngredientSpaceScope spaceScope() {
		return IngredientSpaceScope.CUSTOM;
	}

	@Override
	public CtElement calculateLocation(CtElement elementToModify) {
		return elementToModify.getParent(getCtElementForSplitSpace());
	}
	
	@Override
	protected String getType(CtCodeElement element) {
		
		return element.getClass().getSimpleName();
	}

	public Class getCtElementForSplitSpace() {
		return ctElementForSplitSpace;
	}

	public void setCtElementForSplitSpace(Class ctElementForSplitSpace) {
		this.ctElementForSplitSpace = ctElementForSplitSpace;
	}

}
