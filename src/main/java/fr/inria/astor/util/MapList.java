package fr.inria.astor.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MapList<K, V> extends LinkedHashMap<K, List<V>> {

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

	public MapList<K, V> getSorted() {
		return this.entrySet().stream()
				.sorted(Map.Entry.<K, List<V>>comparingByValue((l1, l2) -> Integer.compare(l1.size(), l2.size()))
						.reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						MapList::new));

	}

	public Probability<K> getProb() {

		Probability<K> probO = new Probability<>();
		int sum = this.values().stream().mapToInt(e -> e.size()).sum();
		double accumul = 0;
		for (K k : this.keySet()) {
			int v = this.get(k).size();
			double probability_k = (double) v / (double) sum;
			accumul = (accumul + probability_k);
			probO.getProbAccumulative().put(k, accumul);
			probO.getProbDist().put(k, probability_k);

		}
		return probO;
	}
}
