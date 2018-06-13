package fr.inria.astor.core.manipulation.synthesis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;

/**
 * Encapsulates the collected data.
 * 
 * @author Matias Martinez
 *
 */
public class DynamicCollectedValues {
	// Key: test name
	// Value: list of executions
	private Map<String, List<Candidates>> values;
	private NopolContext nopolContext;
	private Map<String, Object[]> oracle;

	public DynamicCollectedValues(Map<String, List<Candidates>> values, NopolContext nopolContext,
			Map<String, Object[]> oracle) {
		super();
		this.values = values;
		this.nopolContext = nopolContext;
		this.oracle = oracle;
	}

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

	public NopolContext getNopolContext() {
		return nopolContext;
	}

	public void setNopolContext(NopolContext nopolContext) {
		this.nopolContext = nopolContext;
	}

	public Map<String, Object[]> getOracle() {
		return oracle;
	}

	public void setOracle(Map<String, Object[]> oracle) {
		this.oracle = oracle;
	}

}
