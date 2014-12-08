package fr.inria.astor.core.loop.evolutionary.spaces.implementation;

import fr.inria.astor.core.entities.taxonomy.MutationOperation;
import fr.inria.astor.core.entities.taxonomy.ParMutationOperation;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ParUniformRandomRepairOperatorSpace extends UniformRandomRepairOperatorSpace {

	
	@Override
	public MutationOperation[] values(){
		return ParMutationOperation.values();
	}
	
}
