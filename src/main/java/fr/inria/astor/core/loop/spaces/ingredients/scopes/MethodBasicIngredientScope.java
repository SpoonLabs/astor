package fr.inria.astor.core.loop.spaces.ingredients.scopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
/**
 * 
 * @author Matias Martinez
 *
 */
public class MethodBasicIngredientScope extends LocalIngredientSpace {

	public MethodBasicIngredientScope(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super(processor);
	}

	public MethodBasicIngredientScope(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	public MethodBasicIngredientScope() throws JSAPException {
	}

	
	@Override
	public String calculateLocation(CtElement original) {
		CtExecutable method =  original.getParent(CtExecutable.class);
		return method.getReference().toString();
	}
}
