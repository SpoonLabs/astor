package fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.declaration.CtPackage;

/**
 * Space that divides ingredients according to the ctclass
 * @author Matias Martinez
 *
 */
public class CtPackageIngredientScope extends CtLocationIngredientSpace {
	
	@Override
	public Class getCtElementForSplitSpace() {
		return CtPackage.class;
	}

	public CtPackageIngredientScope() throws JSAPException {
		super();
	}

	public CtPackageIngredientScope(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super(processor);
	}

	public CtPackageIngredientScope(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super(processors);
	}
}
