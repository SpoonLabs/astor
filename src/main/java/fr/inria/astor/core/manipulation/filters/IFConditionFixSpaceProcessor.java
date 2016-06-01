package fr.inria.astor.core.manipulation.filters;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtIf;
/**
 * Retrieves the if conditions
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class IFConditionFixSpaceProcessor extends AbstractFixSpaceProcessor<CtIf>  {

	private Logger logger = Logger.getLogger(IFConditionFixSpaceProcessor.class.getName());

	public  IFConditionFixSpaceProcessor(){
		super();
	
	}
	
	
	@Override
	public void process(CtIf element) {
			super.add(element);
	}
	
}
