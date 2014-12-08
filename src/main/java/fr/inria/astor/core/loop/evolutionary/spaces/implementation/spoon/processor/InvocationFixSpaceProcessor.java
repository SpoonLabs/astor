package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
/**
 * Processor that collects Method invocations.
 * It excludes the super() method invocations (to discuss ;) )
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class InvocationFixSpaceProcessor extends AbstractFixSpaceProcessor<CtInvocation> {

	private Logger logger = Logger.getLogger(InvocationFixSpaceProcessor.class.getName());
	@Override
	public void process(CtInvocation element) {
		
		if(element.getParent() instanceof CtBlock){
			if(!element.toString().equals("super()"))
				super.add((CtStatement)element);
			else{
				logger.info("Discarting "+ element);
			}
		}
	}

}
