package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.util.MapCounter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class NGrams {

	//Size of grams
	public MapCounter[] ngrams = new MapCounter[30];
	
	
	public void add(List voc, int n){
		if (ngrams[n] == null )
			ngrams[n] = new MapCounter<>();
		
		for (Object object : voc) {
			ngrams[n].add(object);
				
		}
		
	}
	
	public String toString(){
		return Arrays.toString(ngrams);
	}
	
	@Deprecated
	public Map getProb(){
		Map prob = new  HashMap();
		return prob;
	}
	
	
}
