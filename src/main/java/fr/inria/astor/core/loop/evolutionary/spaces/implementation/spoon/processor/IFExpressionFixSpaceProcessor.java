package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import fr.inria.astor.core.loop.evolutionary.transformators.CtExpressionTransformator;
import fr.inria.astor.core.manipulation.code.ExpressionRevolver;
/**
 * Retrieves the expressions of a if conditions
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class IFExpressionFixSpaceProcessor extends AbstractFixSpaceProcessor<CtIf>  {

	private Logger logger = Logger.getLogger(IFExpressionFixSpaceProcessor.class.getName());

	public  IFExpressionFixSpaceProcessor(){
		super();
		this.transformator = new CtExpressionTransformator();
	
	}
	
	
	@Override
	public void process(CtIf element) {
		//super.add(element.getCondition());
		List<CtExpression<Boolean>> ctExp = ExpressionRevolver.getExpressions(element.getCondition());
		for (CtExpression ctExpression : ctExp) {
			super.add(ctExpression);
		}
	
	}
	
}
