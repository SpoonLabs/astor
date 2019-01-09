package fr.inria.astor.approaches.tos.operator;

import java.util.List;

import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import spoon.reflect.reference.CtTypeReference;

/**
 * Operator that uses ingredients from Dynamoth
 * 
 * @author Matias Martinez
 *
 */
public interface DynaIngredientOperator {

	public CtTypeReference retrieveTargetTypeReference();

	public abstract List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint,
			List<IngredientFromDyna> ingredients);

}
