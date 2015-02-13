package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import fr.inria.astor.core.loop.evolutionary.transformators.CtStatementTransformator;

/**
 * Processor that retrieves Statements, but excludes blocks, class method and class.
 * Udpate: I improved this by only adding statements with a Block parent 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class SingleStatementFixSpaceProcessor  extends AbstractFixSpaceProcessor<CtStatement>{

	
	/**
	 * The default statement transformator is CTStamemeny 
	 */
	public SingleStatementFixSpaceProcessor() {
		super();
		this.transformator = new CtStatementTransformator();
	}

	@Override
	public void process(CtStatement element) {
		if(!(element instanceof CtBlock  || element instanceof CtClass 
				|| element instanceof CtMethod || element instanceof CtTry || element instanceof CtCatch)
				&& (element.getParent() instanceof CtBlock)){
			add(element);
		}
	}

}
