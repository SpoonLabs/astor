package fr.inria.astor.core.manipulation.filters;

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
public class MethodInvocationFixSpaceProcessor extends AbstractFixSpaceProcessor<CtInvocation> {

	private Logger logger = Logger.getLogger(MethodInvocationFixSpaceProcessor.class.getName());
	@Override
	public void process(CtInvocation element) {
		
		if(element.getParent() instanceof CtBlock){
			if(!element.toString().equals("super()"))
				super.add((CtStatement)element);
			
		}
	}

}
