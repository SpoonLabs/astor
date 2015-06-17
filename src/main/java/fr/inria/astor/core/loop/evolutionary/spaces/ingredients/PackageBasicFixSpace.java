package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;

public class PackageBasicFixSpace extends BasicFixSpace {

	
	public PackageBasicFixSpace(AbstractFixSpaceProcessor<?> processor)
			throws JSAPException {
		super(processor);

	}

	public PackageBasicFixSpace(List<AbstractFixSpaceProcessor<?>> processor)
			throws JSAPException {
		super(processor);

	}
	
}
