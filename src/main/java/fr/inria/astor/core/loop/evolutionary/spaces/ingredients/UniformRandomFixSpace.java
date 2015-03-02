package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtSimpleType;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;

/**
 * This Fix Space takes uniform randomly elements from the the search space.
 * It creates the space ON DEMAND, that is, it process a CtClass the first time it is required
 * @author Matias Martinez,  matias.martinez@inria.fr
 * @param <I>
 *
 */
public abstract class UniformRandomFixSpace<L extends Object, I extends CtCodeElement, T extends Object> 
	implements FixLocationSpace <L, I , T>{
	
	
	private Logger logger = Logger.getLogger(UniformRandomFixSpace.class.getName());
	/**
	 * Map containing statements of the fix space, 
	 *
	 * 
	 */
	//I keep this structure for optimization (to avoid creating a list )
	protected Map<L,List<I>> fixSpaceByLocation = new HashMap<L,List<I>> ();
	protected Map<L,Map<T, List<I>>> fixSpaceByLocationType = new HashMap<L,Map<T,List<I>>> ();
	protected Map<T,List<I>> fixSpaceByType = new HashMap<T,List<I>> ();
	
	
	
	private  IngredientProcessor<L, I> ingredientProcessor;
	
	private Random random = new Random();
	
	public UniformRandomFixSpace() throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<L, I>();
		
	}
	/**
	 * 
	 * @param processor processor such as  @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public UniformRandomFixSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super();
		//this.addProcessor();
		ingredientProcessor = new IngredientProcessor<L, I>(processor);
	}
	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public UniformRandomFixSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super();
		/*for (AbstractFixSpaceProcessor<?> abstractFixSpaceProcessor : processors) {
			this.addProcessor(abstractFixSpaceProcessor.getClass().getName() );
		}*/
		ingredientProcessor = new IngredientProcessor<L, I>(processors);
	}

	
	protected Map<L, List<I>> getFixSpace() {
		return fixSpaceByLocation;
	}
	

		


	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void createFixSpaceFromAClass(L key, CtSimpleType root) {
		
		if (!getFixSpace().containsKey(key)) {
			List<I> ingredientsToProcess = this.ingredientProcessor.createFixSpace(root);
			AbstractFixSpaceProcessor.mustClone = true;
			//logger.debug("Fix space size " + ingredientsToProcess.size());
			getFixSpace().put(key, ingredientsToProcess);
			splitByType(key,ingredientsToProcess);
	
		}
	}
	
	
	
	private void splitByType(L location, List<I> ingredients) {
		Map<T, List<I>> typesFromLocation = this.fixSpaceByLocationType.get(location);
		if(typesFromLocation == null){
			typesFromLocation = new HashMap<>();
			this.fixSpaceByLocationType.put(location, typesFromLocation);
		}
		for (I element : ingredients) {
			T type = getType(element);
			List<I> list = typesFromLocation.get(type);
			if(list == null){
				list = new ArrayList<>();
				typesFromLocation.put(type, list);
			}
			list.add(element);
			
			List<I> listType = this.fixSpaceByType.get(type);
			if(listType == null){
				listType = new ArrayList<>();
				this.fixSpaceByType.put(type, listType);
			}
			listType.add(element);
			
		}
		//fixSpaceByType.putAll(typesFromLocation);
		
	}
	protected T getType(I element) {
		
		return (T) element;
	}
	/**
	 * 
	 * @param fixSpace
	 * @return
	 */
	protected I getRandomStatementFromSpace(List<I> fixSpace) {
		int size = fixSpace.size();
		int index = random.nextInt(size);
		return fixSpace.get(index);

	}
	/**
	 * Return a cloned CtStatement from the fix space in a randomly way
	 * @return 
	 */
	@Override
	public  I getElementFromSpace(L rootClass) {
		I originalPicked = getRandomStatementFromSpace(this.getFixSpace().get(rootClass));
		I cloned =  (I) MutationSupporter.clone(originalPicked);
		return cloned;
	}
	@Override
	public List<I> getFixSpace(L rootClass){
		return getFixSpace().get(rootClass);
	}
	
	@Override
	public List<I> getFixSpace(L rootClass, T type){
		Map<T, List<I>> types =  this.fixSpaceByLocationType.get(rootClass);
		if(types == null)
			return null;
		
		List<I> elements = types.get(type);
		return elements;
	}
	
	/*public String toString(){
		return fixSpaceByLocation.toString();
	}*/

	@Override
	public I getElementFromSpace(L rootCloned, T type) {
		List elements = getFixSpace(rootCloned, type);
		if(elements == null)
			return null;
		return getRandomStatementFromSpace(elements);
	}
	
/*	@Override
	public void defineSpace(List<CtSimpleType<?>> affected) {
		throw new IllegalArgumentException("Not Implemented");
		
	}
*/
	public String toString(){
		String s ="--Space: "+this.strategy() +"\n";
		for (L l : this.fixSpaceByLocationType.keySet()) {
			
			Map<T, List<I>> r = this.fixSpaceByLocationType.get(l);
			String s2 = "";
			int ingredients = 0;
			for (T t : r.keySet()) {
				List ing = r.get(t);
				s2+="\t "+t+": ("+ing.size()+") "+ ing.toString().replace("\n", " ") + "\n";
				ingredients+= ing.size();
			}
			s+= "--> "+l + "(kind ing:"+r.values().size()+", ingrs:"+ingredients+") \n";
			s+=s2;
			
		}
		return s;
	}
	
}
