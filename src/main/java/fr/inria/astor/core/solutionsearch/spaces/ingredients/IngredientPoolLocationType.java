package fr.inria.astor.core.solutionsearch.spaces.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.CacheList;

/**
 * 
 * This class defines an implementation of IngredientPool, which groups
 * ingredients according to the content of the ingredient.
 * 
 * @author Matias Martinez
 */
public abstract class IngredientPoolLocationType<Q, K, I, T, P> implements IngredientPool<Q, K, I, T> {
	/**
	 * Maps that represent the ingredient space. We define different structures
	 * to optimize the search.
	 */
	protected Map<K, List<I>> fixSpaceByLocation = new HashMap<>();
	protected Map<K, Map<T, List<I>>> fixSpaceByLocationType = new HashMap<>();
	protected Map<T, List<I>> fixSpaceByType = new HashMap<>();
	protected Map<Q, K> keysLocation = new HashMap<>();

	protected CodeParserLauncher<Q, P> ingredientProcessor;

	public IngredientPoolLocationType() throws JSAPException {
		super();
		ingredientProcessor = new CodeParserLauncher<>();

	}

	/**
	 * 
	 * @param processor
	 *            processor such as @link{InvocationFixSpaceProcessor}
	 * @throws JSAPException
	 */
	public IngredientPoolLocationType(TargetElementProcessor<?> processor) throws JSAPException {
		super();
		ingredientProcessor = new CodeParserLauncher<>(processor);
	}

	/**
	 * 
	 * @param processors
	 * @throws JSAPException
	 */
	public IngredientPoolLocationType(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super();
		ingredientProcessor = new CodeParserLauncher<>(processors);
	}

	protected Map<K, List<I>> getFixSpace() {
		return fixSpaceByLocation;
	}

	/**
	 * The space maps an element to the location
	 * 
	 * @param element
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

		Map<T, List<I>> typesFromLocation = this.fixSpaceByLocationType.computeIfAbsent(keyLocation, k -> new HashMap<>());
		for (I element : ingredients) {
			T type = getType(element);
			List<I> list = typesFromLocation.computeIfAbsent(type, k -> new ArrayList<>());
			list.add(element);

			List<I> listType = this.fixSpaceByType.computeIfAbsent(type, k -> new ArrayList<>());
			listType.add(element);

		}

	}


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
		return new ArrayList<>(this.fixSpaceByLocation.keySet());
	}

	public List<I> getAllIngredients() {
		List<I> ingredients = new ArrayList<>();
		for (List<I> listIng : this.fixSpaceByLocation.values()) {
			ingredients.addAll(listIng);
		}
		return ingredients;
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

	public CodeParserLauncher<Q, P> getIngredientProcessor() {
		return ingredientProcessor;
	}

	public void setIngredientProcessor(CodeParserLauncher<Q, P> ingredientProcessor) {
		this.ingredientProcessor = ingredientProcessor;
	}
	public List<I> retrieveIngredients(K key) {

		List<I> ingredientsKey = getFixSpace().get(key);
		if (!getFixSpace().containsKey(key)) {
			ingredientsKey = new CacheList<>();
			getFixSpace().put(key, ingredientsKey);
		}
		return ingredientsKey;
	}
}
