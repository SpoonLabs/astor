package fr.inria.astor.core.loop.spaces.ingredients.scopes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
/**
 * List that caches the toString values of the elements stored on it. 
 * @author Matias
 *
 */
public class CacheList<E> extends ArrayList<E> {

	private Set<String> cache = new HashSet<String>();
	
	
	@Override
	public boolean contains(Object o) {
		if(cache.contains(o.toString()))
			return true;

		return super.contains(o);
	}

	@Override
	public boolean add(E e) {
		cache.add(e.toString());
		return super.add(e);
	}

	@Override
	public void add(int index, E element) {
		cache.add(element.toString());
		super.add(index, element);
	}

	
	
}
