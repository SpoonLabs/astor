package fr.inria.astor.core.loop.evolutionary.spaces.implementation;

import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.entities.taxonomy.Operation;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
/**
 * The Operator space is composed by only one Operator: Delete/Remove
 * @author matias
 *
 */
public class RemoveRepairOperatorSpace implements RepairOperatorSpace {

	Operation[] values = new Operation[]{GenProgMutationOperation.DELETE};
	@Override
	public Operation getNextMutation() {
		
		return GenProgMutationOperation.DELETE;
	}

	@Override
	public Operation getNextMutation(double suspiciousValue) {
	
		return GenProgMutationOperation.DELETE;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Operation[] values() {
		return values;
	}

}
