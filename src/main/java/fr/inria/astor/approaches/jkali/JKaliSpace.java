package fr.inria.astor.approaches.jkali;

import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jkali.operators.ReplaceIfBooleanOp;
import fr.inria.astor.approaches.jkali.operators.ReplaceReturnOp;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
/**
 * 
 * @author Matias Martinez
 *
 */
public class JKaliSpace extends OperatorSpace implements RepairOperatorSpace{

	public JKaliSpace(){
		super.register(new ReplaceIfBooleanOp());
		super.register(new ReplaceReturnOp());
		super.register(new RemoveOp());
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
