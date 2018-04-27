package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.CtLocationIngredientSpace;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;

/**
 * In the global strategy we always have the same key: the root ct element (the
 * root package a.k.a. unnamed package').
 * 
 * @author Matias Martinez
 *
 */
public class CtGlobalIngredientScope extends CtLocationIngredientSpace {

	public CtGlobalIngredientScope() throws JSAPException {
		super();
	}

	public CtGlobalIngredientScope(TargetElementProcessor<?> processor) throws JSAPException {
		super(processor);
	}

	public CtGlobalIngredientScope(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	/**
	 * We find the root package (empty package)
	 */
	@Override
	public CtElement calculateLocation(CtElement elementToModify) {
		CtElement root = null;
		CtElement parent = elementToModify;
		do {
			root = parent;
			parent = root.getParent(getCtElementForSplitSpace());

		} while (parent != null);

		return root;
	}
	
	@Override
	public Class getCtElementForSplitSpace() {
		return CtPackage.class;
	}
}
