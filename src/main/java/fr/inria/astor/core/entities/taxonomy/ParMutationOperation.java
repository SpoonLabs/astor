package fr.inria.astor.core.entities.taxonomy;

import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public enum ParMutationOperation /*implements AstorOperator*/ {

	
	REPLACE,

	DELETE_BEFORE,
	DELETE_AFTER,
	
	INSERT_BEFORE,
	INSERT_AFTER;

}
