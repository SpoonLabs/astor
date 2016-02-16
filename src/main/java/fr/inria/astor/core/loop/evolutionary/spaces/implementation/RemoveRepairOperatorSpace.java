package fr.inria.astor.core.loop.evolutionary.spaces.implementation;

import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.entities.taxonomy.Operator;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
/**
 * The Operator space is composed by only one Operator: Delete/Remove
 * @author matias
 *
 */
public class RemoveRepairOperatorSpace implements RepairOperatorSpace {

	Operator[] values = new Operator[]{GenProgMutationOperation.DELETE};
	@Override
	public Operator getNextOperator() {
		
		return GenProgMutationOperation.DELETE;
	}

	@Override
	public Operator getNextOperator(double suspiciousValue) {
	
		return GenProgMutationOperation.DELETE;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Operator[] values() {
		return values;
	}

}
