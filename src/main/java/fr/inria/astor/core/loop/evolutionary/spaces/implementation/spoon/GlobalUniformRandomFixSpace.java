package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.setup.MutationSupporter;

/**
 * This Fix Space takes uniform randomly elements from the the search space. It
 * requires that all classes to parser (i.e., that provide material to the
 * space) be processed to the use of the space.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class GlobalUniformRandomFixSpace<K,T extends CtCodeElement> extends UniformRandomFixSpace<K,T> {
	


	protected List<T> globalSpace = new ArrayList<>();


	private Logger logger = Logger.getLogger(GlobalUniformRandomFixSpace.class.getName());

	
	public GlobalUniformRandomFixSpace(AbstractFixSpaceProcessor processor) throws JSAPException {
		super(processor);
		
	}
	
	public GlobalUniformRandomFixSpace(List<AbstractFixSpaceProcessor> processor) throws JSAPException {
		super(processor);
		
	}
	
	/**
	 * Return a cloned CtStatement from the fix space
	 * 
	 * @return
	 */
	@Override
	public T getElementFromSpace(K rootClass) {
		T originalPicked = getRandomStatementFromSpace(getGlobalFixSpace());
		T cloned = (T) MutationSupporter.clone(originalPicked);
		return cloned;
	}

	@Override
	public List<T> getFixSpace(K rootClass) {
		//we ignore the parameter: the space is always the same (global) for all classes!!
		return getGlobalFixSpace();
	}

	public List<T> getGlobalFixSpace() {
		if (globalSpace.isEmpty()) {
			Collection<List<T>> globalValues = super.getFixSpace().values();
			for (List<T> local : globalValues) {
				globalSpace.addAll(local);
			}
		}
		return globalSpace;
	}

}
