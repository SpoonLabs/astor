package fr.inria.astor.approaches.mutRepair;

import fr.inria.astor.approaches.mutRepair.operators.IfExpresionMutOp;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
/**
 * 
 * @author Matias Martinez
 *
 */
public class MutRepairSpace extends OperatorSpace implements RepairOperatorSpace{

	public MutRepairSpace(){
		super.register(new IfExpresionMutOp());
	}
	
	@Override
	public AstorOperator getNextOperator() {
		return null;
	}

	@Override
	public AstorOperator getNextOperator(double suspiciousValue) {
		return null;
	}

	@Override
	public int size() {
		return super.getOperators().size();
	}

	@Override
	public AstorOperator[] values(){
		AstorOperator[] op_arr = new AstorOperator[this.getOperators().size()]; 
		return this.getOperators().toArray(op_arr);
	}
	
}
