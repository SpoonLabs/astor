package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class Expression {

	private Class inputType;
	private Map<Class, List<ExpressionValueCandidate>> outputMap = new HashMap<Class, List<ExpressionValueCandidate>>();
	private List<ExpressionValueCandidate> lastQuery;

	public Expression(Class input) {
		inputType = input;
	}

	public void setInitVal(String[] names, Object[] vals) {
		outputMap.clear();
		for (int i = 0; i < vals.length; i++) {
			Object o = vals[i];
			Class type;
			type = o.getClass();
			ExpressionValueCandidate cand = new ExpressionValueCandidate(type, names[i], o);
			if (!outputMap.containsKey(type))
				outputMap.put(type, new ArrayList<ExpressionValueCandidate>());
			outputMap.get(type).add(cand);
		}
	}

	@SuppressWarnings("unchecked")
	public Collection<ExpressionValueCandidate> getSJCandidates(Class output) {
		lastQuery = new ArrayList<ExpressionValueCandidate>();
		java.util.Iterator<Class> itr = outputMap.keySet().iterator();
		while (itr.hasNext()) {
			Class e = itr.next();
			if (output.isAssignableFrom(e)) {
				lastQuery.addAll(outputMap.get(e));
			}
		}

		if (output.equals(byte.class) && outputMap.containsKey(Byte.class))
			lastQuery.addAll(outputMap.get(Byte.class));
		if (output.equals(Byte.class) && outputMap.containsKey(byte.class))
			lastQuery.addAll(outputMap.get(byte.class));
		if (output.equals(short.class) && outputMap.containsKey(Short.class))
			lastQuery.addAll(outputMap.get(Short.class));
		if (output.equals(Short.class) && outputMap.containsKey(short.class))
			lastQuery.addAll(outputMap.get(short.class));
		if (output.equals(Integer.class) && outputMap.containsKey(int.class))
			lastQuery.addAll(outputMap.get(int.class));
		if (output.equals(int.class) && outputMap.containsKey(Integer.class))
			lastQuery.addAll(outputMap.get(Integer.class));
		if (output.equals(Boolean.class) && outputMap.containsKey(boolean.class))
			lastQuery.addAll(outputMap.get(boolean.class));
		if (output.equals(boolean.class) && outputMap.containsKey(Boolean.class))
			lastQuery.addAll(outputMap.get(Boolean.class));
		if (output.equals(Double.class) && outputMap.containsKey(double.class))
			lastQuery.addAll(outputMap.get(double.class));
		if (output.equals(double.class) && outputMap.containsKey(Double.class))
			lastQuery.addAll(outputMap.get(Double.class));
	    if (output.equals(long.class) && outputMap.containsKey(Long.class))
			lastQuery.addAll(outputMap.get(Long.class));
	    if (output.equals(Long.class) && outputMap.containsKey(long.class))
			lastQuery.addAll(outputMap.get(long.class));
	    if (output.equals(float.class) && outputMap.containsKey(Float.class))
			lastQuery.addAll(outputMap.get(Float.class));
	    if (output.equals(Float.class) && outputMap.containsKey(float.class))
			lastQuery.addAll(outputMap.get(float.class));
	    if (output.equals(char.class) && outputMap.containsKey(Character.class))
			lastQuery.addAll(outputMap.get(Character.class));
	    if (output.equals(Character.class) && outputMap.containsKey(char.class))
			lastQuery.addAll(outputMap.get(char.class));
	    
		return lastQuery;
	}
}

