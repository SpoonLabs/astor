package fr.inria.astor.core.loop.evolutionary.transformators;

import fr.inria.astor.core.entities.ModificationInstance;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public interface ModelTransformator {

	public void revert(ModificationInstance operation);
	
	public void transform(ModificationInstance operation) throws Exception;
	
	public boolean canTransform(ModificationInstance operation);
	
}
