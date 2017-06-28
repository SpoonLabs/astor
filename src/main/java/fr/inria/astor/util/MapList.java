package fr.inria.astor.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MapList<K, V> extends HashMap<K, List<V>> {

	public void add(K key, V value) {
		List<V> listV = null;
		if (!this.containsKey(key)) {
			listV = new ArrayList<V>();
			this.put(key, listV);
		} else {
			listV = get(key);
		}
		listV.add(value);
	}

}
