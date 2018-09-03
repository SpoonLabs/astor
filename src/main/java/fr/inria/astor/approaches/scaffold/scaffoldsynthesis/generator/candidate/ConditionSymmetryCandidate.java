package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.ScaffoldExecutor;

public class ConditionSymmetryCandidate  {
	
	private int constant = -1;
	private int lhs = -1;
	private int rhs = -1;
	private int relation = -1;
	private String[] ops = { " == ", " != ", ">", "<", "<=", ">=" };
	private ExpressionValueCandidate[] candidates = null;
	@SuppressWarnings("rawtypes")
	private Set<Class> set = new HashSet<Class>(Arrays.asList(byte.class, Byte.class, short.class, Short.class, 
			int.class, Integer.class, float.class, Float.class, double.class, Double.class,
			Long.class, long.class, Character.class, char.class));

	public ConditionSymmetryCandidate() {
	}

	public Boolean next(ExpressionValueCandidate[] vals) {
		
		candidates = vals;
		if (candidates == null || candidates.length == 0)
			return false;
		if (constant == -1) {
			constant = ScaffoldExecutor.choose(2);
		}
		if (constant == 1)
			return false;
		else if (constant == 2)
			return true;
		else return construct();
	}

	private boolean construct() {
		if (relation == -1) {
			lhs = ScaffoldExecutor.choose(candidates.length - 1);
			rhs = ScaffoldExecutor.choose(candidates.length - 1);
			if (set.contains(candidates[0].getCandClass()))
				relation = ScaffoldExecutor.choose(ops.length - 1);
			else
				relation = ScaffoldExecutor.choose(1);
		}
		boolean res = fetchBoolVal();

		return res;
	}
	
	private boolean fetchBoolVal() {
		if (lhs >= rhs)
			ScaffoldExecutor.backtrack();

		if (set.contains(candidates[0].getCandClass())) {
			try {
			    boolean res  = fetchDoubleVal();
			    return res;
			} catch (Exception e) {
				return relation == 0 ? candidates[lhs].getValue() == candidates[rhs].getValue()
						: candidates[lhs].getValue() != candidates[rhs].getValue();
			}
		} else {
			return relation == 0 ? candidates[lhs].getValue() == candidates[rhs].getValue()
					: candidates[lhs].getValue() != candidates[rhs].getValue();
		}
	}

	private boolean fetchDoubleVal() {
		
		double lhs_v = 0, rhs_v = 0;
		Object o1 = candidates[lhs].getValue();
		Object o2 = candidates[rhs].getValue();
		
		if (o1.getClass().equals(Character.class)||o1.getClass().equals(char.class))
			lhs_v = (double) ((char) o1);
		else
			lhs_v = (double) o1;
		
		if (o2.getClass().equals(Character.class)||o2.getClass().equals(char.class))
			rhs_v = (double) ((char) o2);
		else
			rhs_v = (double) o2;
		
		switch (relation) {
		case 0:
			return lhs_v == rhs_v;
		case 1:
			return lhs_v != rhs_v;
		case 2:
			return lhs_v > rhs_v;
		case 3:
			return lhs_v < rhs_v;
		case 4:
			return lhs_v <= rhs_v;
		case 5:
			return lhs_v >= rhs_v;
		}
		return false;
	}

	public String toString() {
		return lhs == -1 ? (constant == -1 ? "" : (constant == 2 ? "true" : "false"))
				: candidates[lhs].getName() + ops[relation] + candidates[rhs].getName();
	}

	public void reset() {
		constant = -1;
		lhs = -1;
		rhs = -1;
		relation = -1;
	}
}