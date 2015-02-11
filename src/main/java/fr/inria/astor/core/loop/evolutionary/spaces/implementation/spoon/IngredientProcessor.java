package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.QueueProcessingManager;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;

/**
 *
 * @author Matias Martinez,  matias.martinez@inria.fr
 * @param <T>
 *
 */
public class IngredientProcessor<K, T extends CtCodeElement> 
	extends Launcher {
	
	
	private Logger logger = Logger.getLogger(IngredientProcessor.class.getName());
		
	
	public IngredientProcessor() throws JSAPException {
		super();
		
	}
	/**
	 * 
	 * @param processor processor such as  @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public IngredientProcessor(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super();
		this.addProcessor(processor.getClass().getName());
	}
	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public IngredientProcessor(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super();
		for (AbstractFixSpaceProcessor<?> abstractFixSpaceProcessor : processors) {
			this.addProcessor(abstractFixSpaceProcessor.getClass().getName() );
		}
	}

	protected void process(CtElement element)  {
	
		ProcessingManager processing = new QueueProcessingManager(FactoryImpl.getLauchingFactory());
		for (String processorName : getProcessorTypes()) {
			processing.addProcessor(processorName);
			logger.debug("Loaded processor " + processorName + ".");
			
		}

		processing.process(element);
	}
	

	public List<T> createFixSpace(CtElement ctelement) {
		return createFixSpace(ctelement, true);
	}
	/**
	 * It run the processor for the ctElement passed as parameter. It clones the element according to the argument <b>mustClone</b>
	 * @param ctelement
	 * @param mustClone
	 * @return
	 */
	public List<T> createFixSpace(CtElement ctelement, boolean mustClone ) {
		AbstractFixSpaceProcessor.mustClone = mustClone;
		AbstractFixSpaceProcessor.spaceElements.clear();
		this.process(ctelement);
		List<T> returnList =  new ArrayList(AbstractFixSpaceProcessor.spaceElements);
		AbstractFixSpaceProcessor.spaceElements.clear();
		
		return returnList;
	}
	
	
	
}
