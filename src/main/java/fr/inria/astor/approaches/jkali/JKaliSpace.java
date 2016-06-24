package fr.inria.astor.approaches.jkali;

import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jkali.operators.ReplaceIfBooleanOp;
import fr.inria.astor.approaches.jkali.operators.ReplaceReturnOp;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
/**
 * Operators used by jKali
 * @author Matias Martinez
 *
 */
public class JKaliSpace extends OperatorSpace {

	public JKaliSpace(){
		super.register(new ReplaceIfBooleanOp());
		super.register(new ReplaceReturnOp());
		super.register(new RemoveOp());
	}
	
}
