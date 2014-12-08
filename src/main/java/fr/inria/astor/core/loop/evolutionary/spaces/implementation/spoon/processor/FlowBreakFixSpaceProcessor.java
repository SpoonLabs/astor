package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import spoon.reflect.code.CtCFlowBreak;
/**
 * Processor that Takes FlowBreak elements 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class FlowBreakFixSpaceProcessor extends AbstractFixSpaceProcessor<CtCFlowBreak>  {


	@Override
	public void process(CtCFlowBreak element) {
		
		super.add(element);
	}

}
