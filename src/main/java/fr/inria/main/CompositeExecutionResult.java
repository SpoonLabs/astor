package fr.inria.main;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CompositeExecutionResult extends ExecutionResult {

	private List<ExecutionResult> results = new ArrayList<ExecutionResult>();

	public void add(ExecutionResult result) {
		results.add(result);
	}

	public List<ExecutionResult> getResults() {
		return results;
	}

	public void setResults(List<ExecutionResult> results) {
		this.results = results;
	}

}
