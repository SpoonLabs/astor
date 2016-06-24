package fr.inria.astor.approaches.mutRepair;

import fr.inria.astor.approaches.mutRepair.operators.IfExpresionMutOp;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
/**
 * Operators used by jMutRepair
 * @author Matias Martinez
 *
 */
public class MutRepairSpace extends OperatorSpace {

	public MutRepairSpace(){
		super.register(new IfExpresionMutOp());
	}
	
	
	
}
