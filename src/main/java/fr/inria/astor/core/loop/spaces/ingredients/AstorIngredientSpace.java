package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * 
 * This class defines an implementation of IngredientSpace, which groups
 * ingredients according to the content of the ingredient.
 * 
 * @author Matias Martinez
 */
public abstract class AstorIngredientSpace<Q extends Object, K extends Object, I extends CtCodeElement, T extends Object>
		implements IngredientSpace<Q,K, I, T> {
	/**
	 * Maps that represent the ingredient space. We define different structures
	 * to optimize the search.
	 */
	protected Map<K, List<I>> fixSpaceByLocation = new HashMap<K, List<I>>();
	protected Map<K, Map<T, List<I>>> fixSpaceByLocationType = new HashMap<K, Map<T, List<I>>>();
	protected Map<T, List<I>> fixSpaceByType = new HashMap<T, List<I>>();
	protected Map<Q, K> keysLocation = new HashMap<Q, K>();

	protected IngredientProcessor<Q, I> ingredientProcessor;

	public AstorIngredientSpace() throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<Q, I>();

	}

	/**
	 * 
	 * @param processor
	 *            processor such as @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public AstorIngredientSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<Q, I>(processor);
	}

	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public AstorIngredientSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<Q, I>(processors);
	}

	protected Map<K, List<I>> getFixSpace() {
		return fixSpaceByLocation;
	}

	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void createFixSpaceFromAClass2(Q element, CtType root) {
		K key = mapKey(element);
		List<I> ingredientsToProcess = this.ingredientProcessor.createFixSpace(root);
		AbstractFixSpaceProcessor.mustClone = true;
		if (getFixSpace().containsKey(key)) {
			getFixSpace().get(key).addAll(ingredientsToProcess);
		} else {
			getFixSpace().put(key, ingredientsToProcess);
		}
		splitByType(key, ingredientsToProcess);

	}
	
	
	
	/**
	 * The space maps an element to the location
	 *  @param element
	 * @return
	 */
	protected K mapKey(Q element) {
		
		K key = calculateLocation(element);
		
		if (key == null)
			return null;
		
		K keyByLoc = this.keysLocation.get(element);
		if (keyByLoc == null) {
			this.keysLocation.put(element, keyByLoc);
		}
		
		return key;
	}

	

	protected void recreateTypesStructures() {

		this.fixSpaceByLocationType.clear();
		this.fixSpaceByType.clear();

		for (K key : fixSpaceByLocation.keySet()) {
			List<I> ingredientsToProcess = fixSpaceByLocation.get(key);
			splitByType(key, ingredientsToProcess);
		}
	}

	private void splitByType(K keyLocation, List<I> ingredients) {

		Map<T, List<I>> typesFromLocation = this.fixSpaceByLocationType.get(keyLocation);
		if (typesFromLocation == null) {
			typesFromLocation = new HashMap<>();
			this.fixSpaceByLocationType.put(keyLocation, typesFromLocation);
		}
		for (I element : ingredients) {
			T type = getType(element);
			List<I> list = typesFromLocation.get(type);
			if (list == null) {
				list = new ArrayList<>();
				typesFromLocation.put(type, list);
			}
			list.add(element);

			List<I> listType = this.fixSpaceByType.get(type);
			if (listType == null) {
				listType = new ArrayList<>();
				this.fixSpaceByType.put(type, listType);
			}
			listType.add(element);

		}

	}

	protected abstract T getType(I element);

	@Override
	public List<I> getIngredients(Q location) {
		K key = calculateLocation(location);
		return getFixSpace().get(key);
	}

	@Override
	public void setIngredients(Q location, List<I> ingredients) {
		K key = mapKey(location);
		if (getFixSpace().containsKey(key)) {
			getFixSpace().get(key).clear();
			getFixSpace().get(key).addAll(ingredients);
		} else {
			getFixSpace().put(key, ingredients);
		}
		recreateTypesStructures();
	}

	@Override
	public List<I> getIngredients(Q element, T type) {
		K rootClass = calculateLocation(element);
		Map<T, List<I>> types = this.fixSpaceByLocationType.get(rootClass);
		if (types == null)
			return null;

		List<I> elements = types.get(type);
		return elements;
	}

	
	@Override
	public List<K> getLocations() {
		return new ArrayList<K>(this.fixSpaceByLocation.keySet());
	}

	public String toString() {
		String s = "--Space: " + this.spaceScope() + "\n";
		for (K l : this.fixSpaceByLocationType.keySet()) {

			Map<T, List<I>> r = this.fixSpaceByLocationType.get(l);
			String s2 = "";
			int ingredients = 0;
			for (T t : r.keySet()) {
				List ing = r.get(t);
				s2 += "\t " + t + ": (" + ing.size() + ") "
						+ /* ing.toString().replace("\n", " ") + */ "\n";
				ingredients += ing.size();
			}
			s += "--> " + l + "(kind ing:" + r.values().size() + ", ingrs:" + ingredients + ") \n";
			s += s2;

		}
		s += "----------";

		for (T t : fixSpaceByType.keySet()) {
			List<I> ing = fixSpaceByType.get(t);

			s += t + " ing: " + ing.size() + " \n";
		}

		s += "----------";
		return s;
	}

}
