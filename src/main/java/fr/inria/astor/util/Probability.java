package fr.inria.astor.util;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Probability<K> {

	private Map<K, Double> probDist = new LinkedHashMap<>();;
	private Map<K, Double> probAccumulative = new LinkedHashMap<>();

	public Map<K, Double> getProbDist() {
		return probDist;
	}

	public Map<K, Double> getProbDistSorted() {
		return sortByValue(probDist);
	}

	public Map<K, Double> getProbAccumulative() {
		return probAccumulative;
	};

	public Map<K, Double> sortByValue(Map<K, Double> prob) {
		Map<K, Double> probMap = prob.entrySet().stream().sorted(Map.Entry.<K, Double>comparingByValue().reversed())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue,
						LinkedHashMap::new));

		return probMap;
	}

}
