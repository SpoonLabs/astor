package fr.inria.astor.core.manipulation.synthesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.lille.repair.common.Candidates;

/**
 * Encapsulates the collected data.
 * 
 * @author Matias Martinez
 *
 */
public class DynamicCollectedValues {
	// Key: test name
	// Value: list of executions
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

	public List<Candidates> getDataValuesFromTest(String testname) {
		return values.get(testname);
	}

	public String toString() {
		String out = "";
		int nrtest = 0;
		for (String key : values.keySet()) {
			out += ("test " + nrtest++ + " :" + key) + "\n";
			List<Candidates> executions = values.get(key);
			out += ("Total nr executions done by test: " + executions.size()) + "\n";
			int i = 0;
			for (Candidates candidates2 : executions) {
				out += ("--Total nr of vars " + (i++) + ": " + candidates2.size()) + "\n";
				int j = 0;
				for (fr.inria.lille.repair.expression.Expression expression : candidates2) {

					out += ("--*-->" + i + " " + (j++) + " " + expression.asPatch() + " " + expression.getValue())
							+ "\n";

				}
			}

		}
		return out;
	}

}
