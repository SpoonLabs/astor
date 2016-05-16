package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtType;

/**
 * 
 * This class defines an implementation of IngredientSpace, which groups
 * ingredients according to the content of the ingredient.
 * 
 * @author Matias Martinez
 *
 * @param <L>
 *            Location
 * @param <K>
 *            Key for the location
 * @param <I>
 *            Content of Ingredient
 * @param <T>
 *            Type of ingredient
 */
public abstract class AstorIngredientSpace<L extends Object, K extends Object, I extends CtCodeElement, T extends Object>
		implements IngredientSpace<L, I, T> {
	/**
	 * Maps that represent the ingredient space.
	 * We define different structures to optimize the search. 
	 */
	protected Map<K, List<I>> fixSpaceByLocation = new HashMap<K, List<I>>();
	protected Map<K, Map<T, List<I>>> fixSpaceByLocationType = new HashMap<K, Map<T, List<I>>>();
	protected Map<T, List<I>> fixSpaceByType = new HashMap<T, List<I>>();

	private IngredientProcessor<L, I> ingredientProcessor;

	public AstorIngredientSpace() throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<L, I>();

	}

	/**
	 * 
	 * @param processor
	 *            processor such as @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public AstorIngredientSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<L, I>(processor);
	}

	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public AstorIngredientSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super();
		ingredientProcessor = new IngredientProcessor<L, I>(processors);
	}

	protected Map<K, List<I>> getFixSpace() {
		return fixSpaceByLocation;
	}


	/**
	 * Creation of fix space from a CtClass
	 * 
	 * @param root
	 */
	public void createFixSpaceFromAClass(L key1, CtType root) {
		K key = convertKey(key1);
		List<I> ingredientsToProcess = this.ingredientProcessor.createFixSpace(root);
		AbstractFixSpaceProcessor.mustClone = true;
		if (getFixSpace().containsKey(key)) {
			getFixSpace().get(key).addAll(ingredientsToProcess);
		} else {
			getFixSpace().put(key, ingredientsToProcess);
		}
		splitByType(key, ingredientsToProcess);

	}

	public abstract K convertKey(L original);
	
	private void recreateTypesStructures(){
		
		this.fixSpaceByLocationType.clear();
		this.fixSpaceByType.clear();
		
		for (K key : fixSpaceByLocation.keySet()){
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

	protected T getType(I element) {

		return (T) element;
	}

	@Override
	public List<I> getIngredients(L location) {
		K key = convertKey(location);
		return getFixSpace().get(key);
	}

	@Override
	public void setIngredients(L location, List<I> ingredients) {
		K key = convertKey(location);
		getFixSpace().put(key,ingredients);
		recreateTypesStructures();
	}

	
	@Override
	public List<I> getIngredients(L rootClass1, T type) {
		K rootClass = convertKey(rootClass1);
		Map<T, List<I>> types = this.fixSpaceByLocationType.get(rootClass);
		if (types == null)
			return null;

		List<I> elements = types.get(type);
		return elements;
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
