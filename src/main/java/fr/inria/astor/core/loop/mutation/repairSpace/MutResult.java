package fr.inria.astor.core.loop.mutation.repairSpace;

import java.util.HashMap;
import java.util.Map;

import spoon.reflect.declaration.CtElement;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
/**
 * Return the result of a mutation of a Candidate
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MutResult {
	/**
	 * Reference to the suspicious line
	 */
	SuspiciousCode suspicious;
	
	CtElement toMutate;
	/**
	 * The result is formed by the mutation (transformation) id,  plus the boolean result that indicates if the modification produce a valid fix. 
	 */
	Map<String, Boolean> result = new HashMap<String, Boolean>();

	int iterations = 0;
	
	public MutResult(CtElement toMutate) {
		this.toMutate = toMutate;
	}

	public void addMutInstance(String postfix, boolean hasRepair, CtElement mutatedElement) {
		result.put(postfix, hasRepair);		
	}

	public Map<String, Boolean> getResult() {
		return result;
	}

	public SuspiciousCode getCandidate() {
		return suspicious;
	}

	public void setCandidate(SuspiciousCode candidate) {
		this.suspicious = candidate;
	}
	
	public void getIterations(){
		result.keySet().size();
	};
}
