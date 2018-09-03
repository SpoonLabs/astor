package fr.inria.astor.approaches.scaffold.scaffoldsynthesis;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request.AbstractRequest;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request.ArithmeticRequest;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request.ConditionRequest;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request.ExpressionRequest;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request.OperatorRequest;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request.RequestType;

public class ScaffoldSynthesisEntry {
	
	private static Map<RequestType, Map<Integer, AbstractRequest>> allHoles = new TreeMap<RequestType, Map<Integer, AbstractRequest>>();

	public ScaffoldSynthesisEntry() {
	}

	public static AbstractRequest COND(Object[] obj, int id, String[] str, Class<?> target) {
		return  createRequest(RequestType.CONDITION, new ConditionRequest(obj, str, target), id);
	}
	
	/**
	 * AOP: +, -, *, /, %
	 */
	public static AbstractRequest AOP(Object[] obj, int id, String[] str, Class<?> target) {
		return createRequest(RequestType.AOP, new ArithmeticRequest(obj, str, target), id);
	}

	public static AbstractRequest EXP(Object[] obj, int id, String[] str, Class<?> target) {
		return createRequest(RequestType.EXP, new ExpressionRequest(obj, str, target), id);
	}

	/**
	 * ROP: ==, !=, >,<,>=,<=
	 */	
	public static AbstractRequest ROP(Object[] obj, int id, String[] str, Class<?> target) {
		return createRequest(RequestType.ROP, new OperatorRequest(obj, str, target), id);
	}

	private static AbstractRequest createRequest(RequestType type, AbstractRequest request, int id) {
		Map<Integer, AbstractRequest> list = allHoles.getOrDefault(type, new TreeMap<Integer, AbstractRequest>());
		if (list.containsKey(id)) {
			 return list.get(id);
		}
		list.put(id, request);
		allHoles.put(type, list);
		return list.get(id);
	}

	public static void reset() {
		for (Map<Integer, AbstractRequest> sketch : allHoles.values()) {
			for (AbstractRequest r : sketch.values())
				r.reset();
		}
	}

	public static String getString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<RequestType, Map<Integer, AbstractRequest>> type : allHoles.entrySet()) {
			for (Entry<Integer, AbstractRequest> hole : type.getValue().entrySet())
				sb.append(type.getKey() + "-" + hole.getKey() + ":  " + hole.getValue());
		}
		return sb.toString();
	}
}