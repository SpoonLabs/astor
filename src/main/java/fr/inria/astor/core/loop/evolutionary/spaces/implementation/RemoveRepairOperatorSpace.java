package fr.inria.astor.core.loop.evolutionary.spaces.implementation;

import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.entities.taxonomy.MutationOperation;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
/**
 * The Operator space is composed by only one Operator: Delete/Remove
 * @author matias
 *
 */
public class RemoveRepairOperatorSpace implements RepairOperatorSpace {

	MutationOperation[] values = new MutationOperation[]{GenProgMutationOperation.DELETE};
	@Override
	public MutationOperation getNextMutation() {
		
		return GenProgMutationOperation.DELETE;
	}

	@Override
	public MutationOperation getNextMutation(double suspiciousValue) {
	
		return GenProgMutationOperation.DELETE;
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public MutationOperation[] values() {
		return values;
	}

}
