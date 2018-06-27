package fr.inria.astor.approaches.tos.core;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtExpression;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CtExpressionIngredientSpaceProcessor extends TargetElementProcessor<CtExpression> {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public CtExpressionIngredientSpaceProcessor() {
		super();

	}

	@Override
	public void process(CtExpression element) {

		if (element.getType() != null)
			this.add(element);

	}

}
