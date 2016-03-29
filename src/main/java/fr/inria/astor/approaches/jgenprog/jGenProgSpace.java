package fr.inria.astor.approaches.jgenprog;

import fr.inria.astor.approaches.jgenprog.operators.*;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;

/**
 * Operators included in jGenProg mode
 * @author Matias Martinez
 *
 */
public class jGenProgSpace extends OperatorSpace{

	public jGenProgSpace(){
		super.register(new RemoveOp());
		super.register(new ReplaceOp());
		super.register(new InsertAfterOp());
		super.register(new InsertBeforeOp());	
	}
	
}
