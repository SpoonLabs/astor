package fr.inria.astor.core.manipulation.filters;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtReturn;

/**
 * Retrieves the if conditions
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class ReturnFixSpaceProcessor extends TargetElementProcessor<CtReturn> {

	private Logger logger = Logger.getLogger(ReturnFixSpaceProcessor.class.getName());

	public ReturnFixSpaceProcessor() {
		super();

	}

	@Override
	public void process(CtReturn element) {
		super.add(element);
	}

}
