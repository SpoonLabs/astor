package fr.inria.astor.approaches.tos.operator;

import java.util.List;

import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import spoon.reflect.reference.CtTypeReference;

/**
 * Operator that uses ingredients from Dynamoth
 * 
 * @author Matias Martinez
 *
 */
public interface DynaIngredientOperator extends MetaOperator {

	public CtTypeReference retrieveTargetTypeReference();

	public abstract List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint,
			List<IngredientFromDyna> ingredients);

}
