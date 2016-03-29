package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.support.RuntimeProcessingManager;

/**
 *
 * @author Matias Martinez,  matias.martinez@inria.fr
 * @param <T>
 *
 */
public class IngredientProcessor<K, T extends CtCodeElement> 
	extends Launcher {
	
	
	private Logger logger = Logger.getLogger(IngredientProcessor.class.getName());
	
	private ProcessingManager processing = null;
			
	public IngredientProcessor() throws JSAPException {
		super();
		this.processing = new RuntimeProcessingManager(MutationSupporter.getFactory());
		
	}
	/**
	 * 
	 * @param processor processor such as  @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public IngredientProcessor(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		this();
		processing.addProcessor(processor.getClass().getName());

	}
	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public IngredientProcessor(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		this();
		for (AbstractFixSpaceProcessor<?> abstractFixSpaceProcessor : processors) {
			processing.addProcessor(abstractFixSpaceProcessor.getClass().getName() );
		}
	}

	protected void process(CtElement element)  {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<T> createFixSpace(CtElement ctelement, boolean mustClone ) {
		AbstractFixSpaceProcessor.mustClone = mustClone;
		AbstractFixSpaceProcessor.spaceElements.clear();
		this.process(ctelement);
		List<T> returnList =  new ArrayList(AbstractFixSpaceProcessor.spaceElements);
		AbstractFixSpaceProcessor.spaceElements.clear();
		
		return returnList;
	}
	
	
	
}
