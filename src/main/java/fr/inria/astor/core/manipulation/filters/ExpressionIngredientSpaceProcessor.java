package fr.inria.astor.core.manipulation.filters;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtExpression;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionIngredientSpaceProcessor extends AbstractFixSpaceProcessor<CtExpression> {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public  ExpressionIngredientSpaceProcessor(){
		super();
		
	}
	
	@Override
	public void process(CtExpression element) {
		this.add(element);
		
	}

}
