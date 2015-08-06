package fr.inria.astor.core.stats;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class StatCounter<T> {
	
	private Map<T, Integer> structure = new HashMap<T, Integer>();

	public void add(T add){
	    int count;
	    if (structure.containsKey(add)) {
	        count = structure.get(add);
	        structure.put(add, count + 1);
	    } else {
	        structure.put(add, 1);
	    }
	}

	public Map<T, Integer> getStructure() {
		return structure;
	}

		
	public String toString(){
		return structure.toString();
	}
	
}
