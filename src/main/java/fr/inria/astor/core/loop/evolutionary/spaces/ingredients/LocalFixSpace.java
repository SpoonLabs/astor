package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
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
	public void defineSpace(List<CtType<?>> affected) {

			for (CtType<?> CtType : affected) {
				if(!CtType.getSimpleName().toLowerCase().startsWith("test")
						&& !CtType.getSimpleName().toLowerCase().endsWith("test")){
					this.createFixSpaceFromAClass(CtType, CtType);
				}
			}
				
	}

	@Override
	protected String getType(CtCodeElement element) {
		
		return element.getClass().getSimpleName();
	}

	@Override
	public IngredientSpaceStrategy strategy() {
		return IngredientSpaceStrategy.LOCAL;
	}

	@Override
	protected String convertKey(CtElement original) {
		
		if(original instanceof CtType<?>)
			return ((CtType) original).getQualifiedName();
		return original.getParent(CtType.class).getQualifiedName();
		//return original.getSignature();
	}
	

}
