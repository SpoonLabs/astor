package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;
import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;

/**
 * This Fix Space takes uniform randomly elements from the the search space. It
 * requires that all classes to parser (i.e., that provide material to the
 * space) be processed to the use of the space.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class GlobalBasicFixSpace 
	extends BasicFixSpace{
	
	private Logger logger = Logger.getLogger(GlobalBasicFixSpace.class.getName());

	
	public GlobalBasicFixSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super(processor);
		
	}
	
	public GlobalBasicFixSpace(List<AbstractFixSpaceProcessor<?>> processor) throws JSAPException {
		super(processor);
		
	}


	@Override
	public CtCodeElement getElementFromSpace(String rootClass) {
		CtCodeElement originalPicked = getRandomStatementFromSpace( getFixSpace(null));
		return  MutationSupporter.clone(originalPicked);
	
	}

	/**
	 * Ignored the class
	 */
	@Override
	public List<CtCodeElement> getFixSpace(String rootClass) {
		return new ArrayList(this.fixSpaceByType.values());
	}

	@Override
	public List<CtCodeElement> getFixSpace(String rootClass, String type) {
		
		return this.fixSpaceByType.get(type);

	}
	@Override
	public CtCodeElement getElementFromSpace(String rootCloned, String type) {
		CtCodeElement originalPicked = getRandomStatementFromSpace( getFixSpace(null, type));
		return  MutationSupporter.clone(originalPicked);
	}
	
	
	
}
