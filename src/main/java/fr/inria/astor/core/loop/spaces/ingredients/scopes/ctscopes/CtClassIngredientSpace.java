package fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.declaration.CtClass;

/**
 * Space that divides ingredients according to the ctclass
 * @author Matias Martinez
 *
 */
public class CtClassIngredientSpace extends CtLocationIngredientSpace {

	@Override
	public Class getCtElementForSplitSpace() {
		return CtClass.class;
	}

	public CtClassIngredientSpace() throws JSAPException {
		super();
	}

	public CtClassIngredientSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super(processor);
	}

	public CtClassIngredientSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	
}
