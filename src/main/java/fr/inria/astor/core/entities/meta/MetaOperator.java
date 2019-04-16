package fr.inria.astor.core.entities.meta;

import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface MetaOperator {

	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier);

	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint);

	public String identifier();

}
