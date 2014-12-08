package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.QueueProcessingManager;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.FixLocationSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.setup.MutationSupporter;

/**
 * This Fix Space takes uniform randomly elements from the the search space.
 * It creates the space ON DEMAND, that is, it process a CtClass the first time it is required
 * @author Matias Martinez,  matias.martinez@inria.fr
 * @param <T>
 *
 */
public class UniformRandomFixSpace<K extends Object, T extends CtCodeElement> 
	extends Launcher implements FixLocationSpace <K, T >{
	
	
	private Logger logger = Logger.getLogger(UniformRandomFixSpace.class.getName());
	/**
	 * Map containing statements of the fix space, 
	 *
	 * 
	 */
	//to remove
	private Map<K,List<T>> fixSpace = new HashMap<K,List<T>> ();
	private Map<K,Map<String, List<T>>> fixSpaceByType = new HashMap<K,Map<String,List<T>>> ();
	
	
	private Random random = new Random();
	
	public UniformRandomFixSpace() throws JSAPException {
		super();
		
	}
	/**
	 * 
	 * @param processor processor such as  @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public UniformRandomFixSpace(AbstractFixSpaceProcessor processor) throws JSAPException {
		super();
		this.addProcessor(processor.getClass().getName());
	}
	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public UniformRandomFixSpace(List<AbstractFixSpaceProcessor> processors) throws JSAPException {
		super();
		for (AbstractFixSpaceProcessor abstractFixSpaceProcessor : processors) {
			this.addProcessor(abstractFixSpaceProcessor.getClass().getName() );
		}
	}

	protected void process(CtElement element)  {
	
		ProcessingManager processing = new QueueProcessingManager(FactoryImpl.getLauchingFactory());
		for (String processorName : getProcessorTypes()) {
			processing.addProcessor(processorName);
			FactoryImpl.getLauchingFactory().getEnvironment().debugMessage("Loaded processor " + processorName + ".");
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
		
	protected Map<K, List<T>> getFixSpace() {
		return fixSpace;
	}

	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void createFixSpaceFromAClass(K key, CtClass root) {
		// --FIX SPACE
		if (!getFixSpace().containsKey(key)) {
			List<T> fixspace = createFixSpace(root);
			AbstractFixSpaceProcessor.mustClone = true;
			logger.debug("Fix space size " + fixspace.size());
			getFixSpace().put(key, fixspace);
			splitByType(key,fixspace);
	
		}
	}
	
	
	
	private void splitByType(K location, List<T> fixspace2) {
		Map<String, List<T>> typesFromLocation = this.fixSpaceByType.get(location);
		if(typesFromLocation == null){
			typesFromLocation = new HashMap<>();
			this.fixSpaceByType.put(location, typesFromLocation);
		}
		for (T element : fixspace2) {
			String type = element.getClass().getName();
			List<T> list = typesFromLocation.get(type);
			if(list == null){
				list = new ArrayList<>();
				typesFromLocation.put(type, list);
			}
			list.add(element);
			
		}
		
	}
	/**
	 * 
	 * @param fixSpace
	 * @return
	 */
	protected T getRandomStatementFromSpace(List<T> fixSpace) {
		int size = fixSpace.size();
		int index = random.nextInt(size);
		return fixSpace.get(index);

	}
	/**
	 * Return a cloned CtStatement from the fix space in a randomly way
	 * @return 
	 */
	@Override
	public  T getElementFromSpace(K rootClass) {
		T originalPicked = getRandomStatementFromSpace(this.getFixSpace().get(rootClass));
		T cloned =  (T) MutationSupporter.clone(originalPicked);
		return cloned;
	}
	@Override
	public List<T> getFixSpace(K rootClass){
		return getFixSpace().get(rootClass);
	}
	@Override
	public List<T> getFixSpace(K rootClass, String type){
		Map<String, List<T>> types =  this.fixSpaceByType.get(rootClass);
		if(types == null)
			return null;
		
		List<T> elements = types.get(type);
		return elements;
	}
	
	public String toString(){
		return fixSpace.toString();
	}

	@Override
	public T getElementFromSpace(K rootCloned, String type) {
		List elements = getFixSpace(rootCloned, type);
		if(elements == null)
			return null;
		return getRandomStatementFromSpace(elements);
	}
	
	
}
