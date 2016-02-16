package fr.inria.astor.core.entities.taxonomy;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public enum ParMutationOperation implements Operator {

	
	REPLACE,

	DELETE_BEFORE,
	DELETE_AFTER,
	
	INSERT_BEFORE,
	INSERT_AFTER;

}
