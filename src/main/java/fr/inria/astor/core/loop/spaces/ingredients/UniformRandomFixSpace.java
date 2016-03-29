package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.setup.RandomManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtType;

/**
 * This Fix Space takes uniform randomly elements from the the search space.
 * It creates the space ON DEMAND, that is, it process a CtClass the first time it is required
 * @author Matias Martinez,  matias.martinez@inria.fr
 * @param <I>
 *
 */
public abstract class UniformRandomFixSpace<L extends Object, K extends Object,  I extends CtCodeElement, T extends Object> 
	implements FixLocationSpace <L, I , T>{
	
	
	private Logger logger = Logger.getLogger(UniformRandomFixSpace.class.getName());
	/**
	 * Map containing statements of the fix space, 
	 *
	 * 
	 */
	//I keep this structure for optimization (to avoid creating a list )
	protected Map<K,List<I>> fixSpaceByLocation = new HashMap<K,List<I>> ();
	protected Map<K,Map<T, List<I>>> fixSpaceByLocationType = new HashMap<K,Map<T,List<I>>> ();
	protected Map<T,List<I>> fixSpaceByType = new HashMap<T,List<I>> ();
	protected List<L> locationsConsidered = new ArrayList<>();
	
	
	private  IngredientProcessor<L, I> ingredientProcessor;
	
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
		ingredientProcessor = new IngredientProcessor<L, I>(processor);
	}
	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public UniformRandomFixSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<L, I>(processors);
	}

	
	protected Map<K, List<I>> getFixSpace() {
		return fixSpaceByLocation;
	}
	
	@Override
	public Map getSpace() {
		return fixSpaceByLocationType;
	}
		


	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void createFixSpaceFromAClass(L key1, CtType root) {
		K key = convertKey(key1);
		locationsConsidered.add(key1);
		//if (!getFixSpace().containsKey(key)) {
			List<I> ingredientsToProcess = this.ingredientProcessor.createFixSpace(root);
			AbstractFixSpaceProcessor.mustClone = true;
			//logger.debug("Fix space size " + ingredientsToProcess.size());
			if(getFixSpace().containsKey(key)){
				getFixSpace().get(key).addAll(ingredientsToProcess);
			}else
				getFixSpace().put(key, ingredientsToProcess);
			
			splitByType(key,ingredientsToProcess);
	
		//}
	}
	/**
	 * Each space decides with dimension it wants
	 * @param original
	 * @return
	 */
	protected abstract K convertKey(L original);
	
	private void splitByType(K keyLocation, List<I> ingredients) {
		
		Map<T, List<I>> typesFromLocation = this.fixSpaceByLocationType.get(keyLocation);
		if(typesFromLocation == null){
			typesFromLocation = new HashMap<>();
			this.fixSpaceByLocationType.put(keyLocation, typesFromLocation);
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
		int index = RandomManager.nextInt(size);
		return fixSpace.get(index);

	}
	/**
	 * Return a cloned CtStatement from the fix space in a randomly way
	 * @return 
	 */
	@Override
	public  I getElementFromSpace(L rootClass1) {
		K rootClass = convertKey(rootClass1);
		I originalPicked = getRandomStatementFromSpace(this.getFixSpace().get(rootClass));
		I cloned =  (I) MutationSupporter.clone(originalPicked);
		return cloned;
	}
	@Override
	public List<I> getFixSpace(L location){
		K key = convertKey(location);
		return getFixSpace().get(key);
	}
	
	@Override
	public List<I> getFixSpace(L rootClass1, T type){
		K rootClass = convertKey(rootClass1);
		Map<T, List<I>> types =  this.fixSpaceByLocationType.get(rootClass);
		if(types == null)
			return null;
		
		List<I> elements = types.get(type);
		return elements;
	}
		
	@Override
	public I getElementFromSpace(L location, T type) {
		//K key = convertKey(rootCloned);	
		List<I> elements = getFixSpace(location, type);
		if(elements == null)
			return null;
		return getRandomStatementFromSpace(elements);
	}

	public List<L> locationsConsidered(){
		return locationsConsidered;
	}
	
	public String toString(){
		String s ="--Space: "+this.strategy() +"\n";
		for (K l : this.fixSpaceByLocationType.keySet()) {
			
			Map<T, List<I>> r = this.fixSpaceByLocationType.get(l);
			String s2 = "";
			int ingredients = 0;
			for (T t : r.keySet()) {
				List ing = r.get(t);
				s2+="\t "+t+": ("+ing.size()+") "+ /*ing.toString().replace("\n", " ") +*/  "\n";
				ingredients+= ing.size();
			}
			s+= "--> "+l + "(kind ing:"+r.values().size()+", ingrs:"+ingredients+") \n";
			s+=s2;
			
		}
		s+="----------";
		
		for(T t : fixSpaceByType.keySet() )	{
			List<I> ing = fixSpaceByType.get(t);
			
			s+=t+" ing: "+ing.size()+ " \n";			
		}	
				
		s+="----------";
		return s;
	}

}
