package fr.inria.astor.approaches.jmutrepair;

import fr.inria.astor.approaches.jmutrepair.operators.IfExpresionMutOp;
import fr.inria.astor.approaches.jmutrepair.operators.ReturnExpresionMutOp;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;

/**
 * Operators used by jMutRepair
 * 
 * @author Matias Martinez
 *
 */
public class jMutRepairSpace extends OperatorSpace {

	public jMutRepairSpace() {
		super.register(new IfExpresionMutOp());
		super.register(new ReturnExpresionMutOp());

	}

}
