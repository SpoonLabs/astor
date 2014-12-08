package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtIf;
import fr.inria.astor.core.loop.evolutionary.transformators.CtExpressionTransformator;
/**
 * Retrieves the if conditions
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class IFConditionFixSpaceProcessor extends AbstractFixSpaceProcessor<CtIf>  {

	private Logger logger = Logger.getLogger(IFConditionFixSpaceProcessor.class.getName());

	public  IFConditionFixSpaceProcessor(){
		super();
		this.transformator = new CtExpressionTransformator();
	
	}
	
	
	@Override
	public void process(CtIf element) {
			super.add(element);
	}
	
}
