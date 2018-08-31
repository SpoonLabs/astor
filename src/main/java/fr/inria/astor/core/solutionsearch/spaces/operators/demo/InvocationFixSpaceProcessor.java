package fr.inria.astor.core.solutionsearch.spaces.operators.demo;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;

/**
 * Processor that collects Method invocations. It excludes the super() method
 * invocations (to discuss ;) )
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class InvocationFixSpaceProcessor extends TargetElementProcessor<CtInvocation> {

	private Logger logger = Logger.getLogger(InvocationFixSpaceProcessor.class.getName());

	@Override
	public void process(CtInvocation element) {

		super.add((CtStatement) element);

	}

}
