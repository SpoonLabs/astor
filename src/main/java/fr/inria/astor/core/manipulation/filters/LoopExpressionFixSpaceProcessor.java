package fr.inria.astor.core.manipulation.filters;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.sourcecode.ExpressionRevolver;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtWhile;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class LoopExpressionFixSpaceProcessor extends TargetElementProcessor<CtLoop> {

	private Logger logger = Logger.getLogger(LoopExpressionFixSpaceProcessor.class.getName());
	
	public LoopExpressionFixSpaceProcessor(){
		super();

	}
	@Override
	public void process(CtLoop element) {
		if(element instanceof CtFor){
			addExpressionAndSubexpressions(((CtFor)element).getExpression());
		}
		if(element instanceof CtWhile){
			addExpressionAndSubexpressions(((CtWhile)element).getLoopingExpression());
		}
		if(element instanceof CtDo){
			addExpressionAndSubexpressions(((CtDo)element).getLoopingExpression());
		}
		
	}
	
	public void addExpressionAndSubexpressions(CtExpression  ctExp1){
		List<CtExpression<Boolean>> ctExp = ExpressionRevolver.getExpressions(ctExp1);
		for (CtExpression ctExpression : ctExp) {
			super.add(ctExpression);
		}
	}
		

}
