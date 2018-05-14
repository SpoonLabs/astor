package fr.inria.astor.core.solutionsearch.spaces.ingredients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtElement;
import spoon.support.RuntimeProcessingManager;

/**
 *
 * @author Matias Martinez,  matias.martinez@inria.fr
 * @param <T>
 *
 */
public class CodeParserLauncher<K, T> 
	extends Launcher {
	
	
	private Logger logger = Logger.getLogger(CodeParserLauncher.class.getName());
	
	private ProcessingManager processing = null;
			
	public CodeParserLauncher() throws JSAPException {
		super();
		this.processing = new RuntimeProcessingManager(MutationSupporter.getFactory());
		
	}
	/**
	 * 
	 * @param processor processor such as  @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public CodeParserLauncher(TargetElementProcessor<?> processor) throws JSAPException {
		this();
		processing.addProcessor(processor.getClass().getName());

	}
	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public CodeParserLauncher(List<TargetElementProcessor<?>> processors) throws JSAPException {
		this();
		for (TargetElementProcessor<?> abstractFixSpaceProcessor : processors) {
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
		TargetElementProcessor.mustClone = mustClone;
		TargetElementProcessor.spaceElements.clear();
		this.process(ctelement);
		List<T> returnList =  new ArrayList(TargetElementProcessor.spaceElements);
		TargetElementProcessor.spaceElements.clear();
		
		return returnList;
	}
	
	
	
}
