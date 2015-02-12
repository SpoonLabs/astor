package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.IngredientSpaceStrategy;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtSimpleType;
/**
 * 
 * @author Matias Martinez
 *
 */
public class BasicFixSpace extends UniformRandomFixSpace<String, CtCodeElement,String>{

	public BasicFixSpace(AbstractFixSpaceProcessor<?> processor)
			throws JSAPException {
		super(processor);
	}

	public BasicFixSpace(List<AbstractFixSpaceProcessor<?>> processors)
			throws JSAPException {
		super(processors);
	}

	public BasicFixSpace() throws JSAPException {
		super();
	
	}
	

	@Override
	public void defineSpace(List<CtSimpleType<?>> affected) {

			for (CtSimpleType<?> ctSimpleType : affected) {
				this.createFixSpaceFromAClass(ctSimpleType.getQualifiedName(), ctSimpleType);
			}
				
	}

	@Override
	protected String getType(CtCodeElement element) {
		
		return element.getClass().getSimpleName();//.getName();
	}

	@Override
	public IngredientSpaceStrategy strategy() {
		return IngredientSpaceStrategy.LOCAL;
	}

}
