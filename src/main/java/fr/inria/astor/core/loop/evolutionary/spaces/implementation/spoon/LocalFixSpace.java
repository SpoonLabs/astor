package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtSimpleType;
/**
 * 
 * @author Matias Martinez
 *
 */
public class LocalFixSpace extends UniformRandomFixSpace<String, CtCodeElement>{

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
	public void defineSpace(List<CtSimpleType<?>> affected,
			List<CtSimpleType<?>> all) {

			for (CtSimpleType<?> ctSimpleType : affected) {
				this.createFixSpaceFromAClass(ctSimpleType.getQualifiedName(), ctSimpleType);
			}
				
	}

}
