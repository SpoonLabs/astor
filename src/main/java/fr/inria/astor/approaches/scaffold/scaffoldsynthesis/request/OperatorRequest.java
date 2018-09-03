package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.ScaffoldExecutor;

@SuppressWarnings("rawtypes")
public class OperatorRequest extends AbstractRequest {

	public OperatorRequest() {}
	public OperatorRequest(Object[] inputValues, String[] inputNames, Class<?> targetType) {
		super(inputValues, inputNames, targetType);
	}

	private int relation = -1;
	private String[] ops = { " == ", " != ", ">", "<", "<=", ">=" };
	
	private Set<Class> set = new HashSet<Class>(Arrays.asList(byte.class, Byte.class, short.class, Short.class, 
			int.class, Integer.class, float.class, Float.class, double.class, Double.class,
			Long.class, long.class, Character.class, char.class));

	private boolean fetchVal(Object[] vals) {
		
		double lhs_v = 0, rhs_v = 0;
		
		if (vals[0].getClass().equals(Character.class)||vals[0].getClass().equals(char.class))
			lhs_v = (double) ((char) vals[0]);
		else
			lhs_v = (double) vals[0];
		
		if (vals[1].getClass().equals(Character.class)||vals[1].getClass().equals(char.class))
			rhs_v = (double) ((char) vals[1]);
		else
			rhs_v = (double) vals[1];

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
		return relation < 0 ? "" : ops[relation] + " ";
	}

	public void reset() {
		relation = -1;
	}

	@Override
	public Object invoke() {

		if (inputValues.size() != 2)
			return false;
		
		boolean isPrime = false;
		
		if (set.contains(targetType))
			isPrime = true;
		
		if (relation == -1) {
			if (isPrime)
				relation = ScaffoldExecutor.choose(ops.length - 1);
			else
				relation = ScaffoldExecutor.choose(1);
		}
		
		if (isPrime)
			return fetchVal(inputValues.toArray(new Object[inputValues.size()]));
		else
			return relation == 0 ? inputValues.get(0) == inputValues.get(1) : inputValues.get(0) != inputValues.get(1);
	}
}
