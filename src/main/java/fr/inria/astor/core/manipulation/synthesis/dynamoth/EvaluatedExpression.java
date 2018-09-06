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

	/**
	 * TheExpression under evaluation
	 */
	Expression expressionUnderEvaluation = null;
	/**
	 * The evaluation results by test cases (key of map), and by execution (each
	 * position in the List)
	 */
	Map<String, List<Value>> evaluations = new HashMap<>();

	public EvaluatedExpression(Expression wrap, Map<String, List<Value>> evaluations) {
		this.expressionUnderEvaluation = wrap;
		this.evaluations = evaluations;
	}

	@Override
	public int compareTo(Expression o) {
		return expressionUnderEvaluation.compareTo(o);
	}

	@Override
	public Value getValue() {
		return expressionUnderEvaluation.getValue();
	}

	@Override
	public void setValue(Value value) {
		expressionUnderEvaluation.setValue(value);

	}

	@Override
	public boolean sameExpression(Expression exp2) {

		return expressionUnderEvaluation.sameExpression(exp2);
	}

	@Override
	public double getWeight() {
		return expressionUnderEvaluation.getWeight();
	}

	@Override
	public double getPriority() {
		return expressionUnderEvaluation.getPriority();
	}

	@Override
	public Value evaluate(Candidates values) {
		return expressionUnderEvaluation.evaluate(values);
	}

	@Override
	public String asPatch() {
		return expressionUnderEvaluation.asPatch();
	}

	public Map<String, List<Value>> getEvaluations() {
		return evaluations;
	}

	public void setEvaluations(Map<String, List<Value>> evaluations) {
		this.evaluations = evaluations;
	}

	public Expression getExpressionUnderEvaluation() {
		return expressionUnderEvaluation;
	}

	public void setExpressionUnderEvaluation(Expression wrap) {
		this.expressionUnderEvaluation = wrap;
	}

	@Override
	public String toString() {
		return expressionUnderEvaluation.toString();
	}
}
