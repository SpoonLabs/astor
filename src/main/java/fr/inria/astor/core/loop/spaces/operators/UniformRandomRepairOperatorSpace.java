package fr.inria.astor.core.loop.spaces.operators;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
/**
 * Represents a Uniform Random Repair operator space.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class UniformRandomRepairOperatorSpace implements RepairOperatorSpace {

	OperatorSpace space;
	
	public UniformRandomRepairOperatorSpace(OperatorSpace space) {
		super();
		this.space = space;
	}

	@Override
	public AstorOperator getNextOperator() {
		 return values()[RandomManager.nextInt(values().length)];
	}

	@Deprecated
	public AstorOperator getNextOperator(double suspiciousValue) {
		double randomVal = RandomManager.nextDouble();
		if(	!ConfigurationProperties.getPropertyBool("probagenmutation") || ( suspiciousValue * ConfigurationProperties.getPropertyDouble("mutationrate") ) >= randomVal ){
			return this.getNextOperator();
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
	public AstorOperator[] values(){
		AstorOperator[] op_arr = new AstorOperator[space.getOperators().size()]; 
		return space.getOperators().toArray(op_arr);//GenProgMutationOperation.values();
	}
}
