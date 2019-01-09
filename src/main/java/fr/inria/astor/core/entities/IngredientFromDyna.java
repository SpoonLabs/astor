package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.manipulation.synthesis.dynamoth.EvaluatedExpression;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.combinations.BinaryExpressionImpl;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.access.MethodImpl;
import fr.inria.lille.repair.expression.access.VariableImpl;
import fr.inria.lille.repair.expression.combination.unary.UnaryExpressionImpl;
import spoon.reflect.declaration.CtElement;

/**
 * Ingredient from Dynamoth
 * 
 * @author Matias Martinez
 *
 */
public class IngredientFromDyna extends Ingredient {

	protected Expression dynmothExpression = null;

	public IngredientFromDyna(CtElement element, Expression expression) {
		super(element);
		this.dynmothExpression = expression;

	}

	public Expression getDynmothExpression() {
		return dynmothExpression;
	}

	public void setDynmothExpression(Expression dynmothExpression) {
		this.dynmothExpression = dynmothExpression;
	}

	public List<VariableImpl> getVariable() {
		return getVariable(dynmothExpression);
	}

	/**
	 * Returns the variables present in the expression
	 * 
	 * @param expression
	 * @return
	 */
	public List<VariableImpl> getVariable(Expression expression) {
		List<VariableImpl> vars = new ArrayList<>();

		if (expression instanceof EvaluatedExpression)
			vars.addAll(getVariable(((EvaluatedExpression) expression).getExpressionUnderEvaluation()));
		else if (expression instanceof VariableImpl)
			vars.add((VariableImpl) expression);

		else if (expression instanceof UnaryExpressionImpl)
			vars.addAll(getVariable(((UnaryExpressionImpl) expression).getExpression()));
		else if (expression instanceof MethodImpl) {
			vars.addAll(getVariable(((MethodImpl) expression).getTarget()));
			MethodImpl mimp = (MethodImpl) expression;
			for (Expression parameter : mimp.getParameters())
				vars.addAll(getVariable(parameter));
		} else if (expression instanceof BinaryExpressionImpl) {
			BinaryExpressionImpl binex = (BinaryExpressionImpl) expression;
			vars.addAll(getVariable(binex.getFirstExpression()));
			vars.addAll(getVariable(binex.getSecondExpression()));
		}

		return vars;
	}

}
