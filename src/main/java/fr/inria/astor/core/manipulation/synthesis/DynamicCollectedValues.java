package fr.inria.astor.core.manipulation.synthesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.lille.repair.common.Candidates;

/**
 * Encapsulates the collected data.
 * @author Matias Martinez
 *
 */
public class DynamicCollectedValues {
	//Key: test name
	//Value: list of executions
	Map<String, List<Candidates>> values;

	public DynamicCollectedValues() {
		this.values = new HashMap<>();
	}

	public DynamicCollectedValues(Map<String, List<Candidates>> values) {
		super();
		this.values = values;
	}

	public Map<String, List<Candidates>> getValues() {
		return values;
	}

	public void setValues(Map<String, List<Candidates>> values) {
		this.values = values;
	}
	public List<Candidates> getDataValuesFromTest(String testname){
		return values.get(testname);
	}
}