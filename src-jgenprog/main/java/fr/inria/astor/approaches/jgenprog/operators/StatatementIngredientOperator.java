package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.operators.IngredientBasedOperator;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class StatatementIngredientOperator extends IngredientBasedOperator {

	@Override
	protected OperatorInstance createOperatorInstance(ModificationPoint mp) {
		OperatorInstance operation = new StatementOperatorInstance(mp, this, mp.getCodeElement(), null);
		return operation;
	}

}
