package fr.inria.astor.core.loop.evolutionary.transformators;

import fr.inria.astor.core.entities.GenOperationInstance;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface ModelTransformator {

	public void revert(GenOperationInstance operation);
	
	public void transform(GenOperationInstance operation) throws Exception;
	
	public boolean canTransform(GenOperationInstance operation);
	
}
