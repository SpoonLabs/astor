package fr.inria.astor.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
/**
 * @author Matias Martinez
 *
 * @param <K>
 */
public class MapCounter<K> extends HashMap<K, Integer> {

	Map<K, Double> probMap = null;
	boolean changeSinceLastCreation = false;
	public void add(K key) {
		changeSinceLastCreation = true;
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

	public Map sorted(){
		return this.entrySet().stream().sorted(Map.Entry.<K, Integer>comparingByValue().reversed())
		.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
	}
	
	public Map<K, Double> getProbabilies(){
		
		if(!changeSinceLastCreation){
			return  probMap;
		}
		changeSinceLastCreation = false;
		probMap = new HashMap<K, Double>();
		
		int size = 0;
		for (Integer values : this.values()) {
			size += values;
		}
		
		for (K key : this.keySet()) {
			int vofKey = this.get(key);
			
			double probKey = (double)vofKey / (double) size;
			probMap.put(key, probKey);
		}
		
		probMap = probMap.entrySet().stream()
				.sorted(Map.Entry.<K, Double>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		
		return probMap;
	}

}
