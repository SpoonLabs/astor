package fr.inria.astor.core.manipulation.filters;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.sourcecode.ExpressionRevolver;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
/**
 * Retrieves the expressions of a if conditions
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class IFExpressionFixSpaceProcessor extends AbstractFixSpaceProcessor<CtIf>  {

	private Logger logger = Logger.getLogger(IFExpressionFixSpaceProcessor.class.getName());

	public  IFExpressionFixSpaceProcessor(){
		super();
		
	}
	
	
	@Override
	public void process(CtIf element) {
		List<CtExpression<Boolean>> ctExp = ExpressionRevolver.getExpressions(element.getCondition());
		for (CtExpression ctExpression : ctExp) {
			super.add(ctExpression);
		}
	
	}
	
}
