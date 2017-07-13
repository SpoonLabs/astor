package fr.inria.astor.core.manipulation.filters;

import spoon.reflect.code.CtExpression;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionBooleanIngredientSpaceProcessor extends ExpressionIngredientSpaceProcessor {

	
	@Override
	public void process(CtExpression element) {
		if(element.getType() !=null && 
				("boolean".equals(element.getType().toString()) || "boolean".equals(element.getType().unbox().toString())))
			this.add(element);	
		
	}
	
}
