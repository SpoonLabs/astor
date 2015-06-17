package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class PackageBasicFixSpace extends LocalFixSpace {

	
	public PackageBasicFixSpace(AbstractFixSpaceProcessor<?> processor)
			throws JSAPException {
		super(processor);

	}

	public PackageBasicFixSpace(List<AbstractFixSpaceProcessor<?>> processor)
			throws JSAPException {
		super(processor);

	}

	@Override
	protected String convertKey(CtElement original) {
		
		return original.getParent(CtPackage.class).getQualifiedName();
	}
	
}
