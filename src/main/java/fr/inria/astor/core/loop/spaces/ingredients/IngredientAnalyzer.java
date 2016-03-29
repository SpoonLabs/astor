package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.reflect.code.CtCodeElement;
/**
 * 
 * @author Matias Martinez
 *
 */
public class IngredientAnalyzer {

	
	public static Map<String,Integer> spaceDensity(List<CtCodeElement> elements){
		Map<String,Integer> frequencymap = new HashMap<String,Integer>();
		for (CtCodeElement iterable_element : elements) {
			String k = iterable_element.getSignature();
		  if(frequencymap.containsKey(k)) {
			  frequencymap.put(k, frequencymap.get(k)+1);
		  }
		  else{ frequencymap.put(k, 1); }
		}
		return frequencymap;
	}
	
}
