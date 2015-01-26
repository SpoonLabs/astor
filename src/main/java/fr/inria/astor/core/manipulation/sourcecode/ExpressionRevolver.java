package fr.inria.astor.core.manipulation.sourcecode;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtUnaryOperator;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ExpressionRevolver {

	public static List<CtExpression<Boolean>> getExpressions(CtExpression<Boolean> element) {
		List<CtExpression<Boolean>> expsRetrieved = new ArrayList<CtExpression<Boolean>>();

		if (element instanceof CtUnaryOperator) {
			expsRetrieved.add(element);
			element = ((CtUnaryOperator) element).getOperand();
		}

		if (element instanceof CtBinaryOperator) {
			expsRetrieved.add(element);
			CtBinaryOperator bin = (CtBinaryOperator) element;
			if (bin.getKind().equals(BinaryOperatorKind.AND) || bin.getKind().equals(BinaryOperatorKind.OR)) {
				expsRetrieved.addAll(getExpressions(bin.getLeftHandOperand()));
				expsRetrieved.addAll(getExpressions(bin.getRightHandOperand()));
			}
		} else {
			if (element instanceof CtInvocation
					&& element.getType().getSimpleName().equals(boolean.class.getSimpleName())) {
				expsRetrieved.add(element);
			}
		}
		return expsRetrieved;
	}

}
