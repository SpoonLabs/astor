package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.List;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
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
	public void defineSpace(List<CtSimpleType<?>> affected) {

			for (CtSimpleType<?> ctSimpleType : affected) {
				if(!ctSimpleType.getSimpleName().toLowerCase().startsWith("test")
						&& !ctSimpleType.getSimpleName().toLowerCase().endsWith("test")){
					this.createFixSpaceFromAClass(ctSimpleType, ctSimpleType);
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
		
		if(original instanceof CtSimpleType<?>)
			return ((CtSimpleType) original).getQualifiedName();
		return original.getParent(CtSimpleType.class).getQualifiedName();
		//return original.getSignature();
	}
	

}
