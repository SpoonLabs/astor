package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ExpressionCreator {
	
	private List<Expression> expressions = new ArrayList<Expression>();
	private List<ExpressionValueCandidate> lastQuery;
	private int varLen;

	public void addTypeVals(Class type, String[] names, Object[] vals) {
		expressions.clear();
		Expression exp = new Expression(type);
		exp.setInitVal(names, vals);
		expressions.add(exp);
		varLen = vals.length;
	}

	public ExpressionValueCandidate[] getSJCandidates(Class output, boolean useDefaultValues) {
		
		lastQuery = new ArrayList<ExpressionValueCandidate>();
		for (Expression exp : expressions) {
			if (exp != null) {
				lastQuery.addAll(exp.getSJCandidates(output));
			}
		}
		
		if(useDefaultValues) {
			
			if (output.equals(Integer.class) || output.equals(int.class) ||
					output.equals(Double.class) || output.equals(double.class) ||
					output.equals(Long.class) || output.equals(long.class)||
					output.equals(Float.class) || output.equals(float.class)||
					output.equals(short.class) || output.equals(Short.class)||
					output.equals(byte.class) || output.equals(Byte.class)) {
				if(!lastQuery.contains(new ExpressionValueCandidate(output, "0", 0)))
				     lastQuery.add(new ExpressionValueCandidate(output, "0", 0));
				if(!lastQuery.contains(new ExpressionValueCandidate(output, "1", 1)))
			         lastQuery.add(new ExpressionValueCandidate(output, "1", 1));
				if(!lastQuery.contains(new ExpressionValueCandidate(output, "-1", -1)))
			         lastQuery.add(new ExpressionValueCandidate(output, "-1", -1));
			} else if (output.equals(Boolean.class) || output.equals(boolean.class)) {
				if(!lastQuery.contains(new ExpressionValueCandidate(Boolean.class, "false", false)))
				    lastQuery.add(new ExpressionValueCandidate(Boolean.class, "false", false));
				if(!lastQuery.contains(new ExpressionValueCandidate(Boolean.class, "true", true)))
				    lastQuery.add(new ExpressionValueCandidate(Boolean.class, "true", true));
			} else {
				if(!lastQuery.contains(new ExpressionValueCandidate(output, "null", null)))
				    lastQuery.add(new ExpressionValueCandidate(output, "null", null));
			}
		}
		
		return lastQuery.toArray(new ExpressionValueCandidate[lastQuery.size()]);
	}

	public int getVarLen() {
		return varLen;
	}
}

