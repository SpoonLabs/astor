package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.ScaffoldExecutor;

@SuppressWarnings("rawtypes")
public class ArithmeticRequest extends AbstractRequest {

public ArithmeticRequest() {}
	public ArithmeticRequest(Object[] inputValues, String[] inputNames, Class<?> targetType) {
		super(inputValues, inputNames, targetType);
	}

	private int relation = -1;
	private String[] ops = { "+ ", " - ", " * ", " / ", " % " };
	
	private Set<Class> set = new HashSet<Class>(Arrays.asList(byte.class, Byte.class, short.class, Short.class, 
			int.class, Integer.class, float.class, Float.class, double.class, Double.class,
			Long.class, long.class, Character.class, char.class));

	private double fetchVal(Object[] vals) {
		double lhs_v = 0, rhs_v = 0;
		
		if (vals[0].getClass().equals(Character.class) ||vals[0].getClass().equals(char.class))
			lhs_v = (double) ((char) vals[0]);
		else
			lhs_v = (double) vals[0];
		
		if (vals[1].getClass().equals(Character.class) || vals[1].getClass().equals(char.class))
			rhs_v = (double) ((char) vals[1]);
		else
			rhs_v = (double) vals[1];
		
		switch (relation) {
		case 0:
			return lhs_v + rhs_v;
		case 1:
			return lhs_v - rhs_v;
		case 2:
			return lhs_v * rhs_v;
		case 3:
			return lhs_v / rhs_v;
		case 4:
			return lhs_v % rhs_v;
		}
		
		return 0;
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
			return 0;
		if (!set.contains(targetType))
			return 0;
		if (relation == -1)
			relation = ScaffoldExecutor.choose(ops.length - 1);
		
		double res = fetchVal(inputValues.toArray(new Object[inputValues.size()]));
		
		if (targetType.equals(int.class) || targetType.equals(Integer.class))
			return (int) (res);
		if (targetType.equals(byte.class) || targetType.equals(Byte.class))
			return (byte) (res);
		if (targetType.equals(short.class) || targetType.equals(Short.class))
			return (short) (res);
		if (targetType.equals(float.class) || targetType.equals(Float.class))
			return (float) (res);
		if (targetType.equals(long.class) || targetType.equals(Long.class))
			return (long) (res);
		if (targetType.equals(char.class) || targetType.equals(Character.class))
			return (char) (res);
		
		return res;
	}
}
