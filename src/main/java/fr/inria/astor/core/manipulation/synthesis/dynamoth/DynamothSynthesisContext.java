package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.manipulation.synthesis.ExecutionContext;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;

/**
 * Encapsulates the collected data necessary by Dynamoth to synthesize code.
 * 
 * @author Matias Martinez
 *
 */
public class DynamothSynthesisContext extends ExecutionContext {
	// Key: test name
	// Value: list of executions
	protected Map<String, List<Candidates>> values;
	protected NopolContext nopolContext;
	protected Map<String, Object[]> oracle;

	public DynamothSynthesisContext(Map<String, List<Candidates>> values, NopolContext nopolContext,
			Map<String, Object[]> oracle) {
		super();
		this.values = values;
		this.nopolContext = nopolContext;
		this.oracle = oracle;
	}

	public DynamothSynthesisContext() {
		this.values = new HashMap<>();
	}

	public DynamothSynthesisContext(Map<String, List<Candidates>> values) {
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

	public String toString() {
		String out = "";
		int nrtest = 0;

		for (String key : values.keySet()) {
			out += ("test " + nrtest++ + " :" + key) + "\n";
			List<Candidates> executions = values.get(key);
			out += ("Total nr executions done by test: " + executions.size()) + "\n";
			int i = 0;
			for (Candidates candidates2 : executions) {
				out += ("--Total nr of vars of execution " + (i++) + ": " + candidates2.size()) + "\n";
				int j = 0;
				for (fr.inria.lille.repair.expression.Expression expression : candidates2) {
					try {
						out += ("--*-->" + i + ": var  " + (j++) + " " + expression.asPatch() + " value: "
								+ expression.getValue()) + "\n";
					} catch (Exception e) {
						System.out.println("error c:" + e);
					}

				}
			}

		}
		return out;
	}

}
