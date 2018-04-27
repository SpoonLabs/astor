package fr.inria.astor.approaches.deeprepair;

import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;

/**
 * Operators included in DeepRepair mode
 * @author Matias Martinez
 *
 */
public class DeepRepairOperatorSpace extends OperatorSpace{

	public DeepRepairOperatorSpace(){
		super.register(new ReplaceOp());
		super.register(new InsertAfterOp());
		super.register(new InsertBeforeOp());	
	}
	
}
