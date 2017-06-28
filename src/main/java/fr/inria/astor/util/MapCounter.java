package fr.inria.astor.util;

import java.util.HashMap;
import java.util.Map;
/**
 * @author Matias Martinez
 *
 * @param <K>
 */
public class MapCounter<K> extends HashMap<K, Integer> {

	public void add(K key) {
		
		if (!containsKey(key))
			this.put(key, new Integer(1));
		else {
			Integer i = this.get(key);
			this.put(key, i + 1);
		}
	}
	
	public void printSort(){
		this.entrySet().stream().sorted(Map.Entry.<K, Integer>comparingByValue().reversed())
		.forEach(System.out::println);
	}

	
	public Map<K, Double> getProbabilies(){
		
		Map<K, Double> h = new HashMap<K, Double>();
		
		int size = 0;
		for (Integer values : this.values()) {
			size += values;
		}
		
		for (K key : this.keySet()) {
			int vofKey = this.get(key);
			
			double probKey = (double)vofKey / (double) size;
			h.put(key, probKey);
		}
		
		return h;
	}
	
}
