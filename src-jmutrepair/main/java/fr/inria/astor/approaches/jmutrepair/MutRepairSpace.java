package fr.inria.astor.approaches.jmutrepair;

import fr.inria.astor.approaches.jmutrepair.operators.IfExpresionMutOp;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
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
