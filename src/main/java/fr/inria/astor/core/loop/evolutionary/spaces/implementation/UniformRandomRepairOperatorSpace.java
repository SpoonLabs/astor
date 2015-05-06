package fr.inria.astor.core.loop.evolutionary.spaces.implementation;

import java.util.Random;

import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.entities.taxonomy.MutationOperation;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Represents a Uniform Random Repair operator space.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class UniformRandomRepairOperatorSpace implements RepairOperatorSpace {

	private Random random = new Random();
		
	@Override
	public MutationOperation getNextMutation() {
		 return values()[random.nextInt(values().length)];
	}

	@Override
	public MutationOperation getNextMutation(double suspiciousValue) {
		double randomVal = random.nextDouble();
		if(	!ConfigurationProperties.getPropertyBool("probagenmutation") || ( suspiciousValue * ConfigurationProperties.getPropertyDouble("mutationrate") ) >= randomVal ){
			return this.getNextMutation();
		}
		//As we use a mutation rate to indicate the prob of mutation, if there is not mutation , return null
		return null;
	}

	@Override
	public int size() {
		return values().length;
	}
	/**
	 * By default, we use GenProgOperations
	 */
	@Override
	public MutationOperation[] values(){
		return GenProgMutationOperation.values();
	}
}
