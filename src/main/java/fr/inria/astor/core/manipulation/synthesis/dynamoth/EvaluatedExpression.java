package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.value.Value;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvaluatedExpression implements fr.inria.lille.repair.expression.Expression {

	Expression wrap = null;
	// test name
	Map<String, List<Value>> evaluations = new HashMap<>();

	public EvaluatedExpression(Expression wrap, Map<String, List<Value>> evaluations) {
		this.wrap = wrap;
		this.evaluations = evaluations;
	}

	@Override
	public int compareTo(Expression o) {
		return wrap.compareTo(o);
	}

	@Override
	public Value getValue() {
		return wrap.getValue();
	}

	@Override
	public void setValue(Value value) {
		wrap.setValue(value);

	}

	@Override
	public boolean sameExpression(Expression exp2) {

		return wrap.sameExpression(exp2);
	}

	@Override
	public double getWeight() {
		return wrap.getWeight();
	}

	@Override
	public double getPriority() {
		return wrap.getPriority();
	}

	@Override
	public Value evaluate(Candidates values) {
		return wrap.evaluate(values);
	}

	@Override
	public String asPatch() {
		return wrap.asPatch();
	}

	public Map<String, List<Value>> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(Map<String, List<Value>> evaluations) {
		this.evaluations = evaluations;
	}

	public Expression getWrap() {
		return wrap;
	}

	public void setWrap(Expression wrap) {
		this.wrap = wrap;
	}

	@Override
	public String toString() {
		return wrap.toString();
	}
}
