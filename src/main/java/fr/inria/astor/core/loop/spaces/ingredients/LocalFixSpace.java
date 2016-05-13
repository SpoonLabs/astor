package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * 
 * @author Matias Martinez
 *
 */
public class LocalFixSpace extends UniformRandomFixSpace<CtElement,String, CtCodeElement,String>{

	public LocalFixSpace(AbstractFixSpaceProcessor<?> processor)
			throws JSAPException {
		super(processor);
	}

	public LocalFixSpace(List<AbstractFixSpaceProcessor<?>> processors)
			throws JSAPException {
		super(processors);
	}

	public LocalFixSpace() throws JSAPException {
		super();
	
	}
	

	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> affected = variant.getAffectedClasses();
			for (CtType<?> CtType : affected) {
					this.createFixSpaceFromAClass(CtType, CtType);
			}
				
	}

	@Override
	protected String getType(CtCodeElement element) {
		
		return element.getClass().getSimpleName();
	}

	@Override
	public IngredientSpaceScope spaceScope() {
		return IngredientSpaceScope.LOCAL;
	}

	@Override
	protected String convertKey(CtElement original) {
		
		if(original instanceof CtType<?>)
			return ((CtType) original).getQualifiedName();
		return original.getParent(CtType.class).getQualifiedName();
		//return original.getSignature();
	}
	

}
