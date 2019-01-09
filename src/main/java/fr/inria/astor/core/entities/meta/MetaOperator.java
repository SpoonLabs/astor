package fr.inria.astor.core.entities.meta;

import fr.inria.astor.core.entities.OperatorInstance;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface MetaOperator {

	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier);

}
