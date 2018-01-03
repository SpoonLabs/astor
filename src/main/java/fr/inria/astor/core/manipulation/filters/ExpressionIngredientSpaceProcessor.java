package fr.inria.astor.core.manipulation.filters;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionIngredientSpaceProcessor extends TargetElementProcessor<CtExpression> {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public ExpressionIngredientSpaceProcessor() {
		super();

	}

	@Override
	public void process(CtExpression element) {

		if (element instanceof CtAssignment || element instanceof CtNewArray || element instanceof CtTypeAccess
				|| element instanceof CtVariableAccess || element instanceof CtLiteral)
			return;
		if (element.getType() != null)
			this.add(element);

	}

}
