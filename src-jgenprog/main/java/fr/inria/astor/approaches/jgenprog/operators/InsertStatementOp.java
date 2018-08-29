package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import spoon.reflect.code.CtStatement;

/**
 * Abstract insert operator
 * 
 * @author Matias Martinez
 *
 */
public abstract class InsertStatementOp extends StatatementIngredientOperator implements StatementLevelOperator {

	@Override
	public abstract boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p);

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return addPoint(p, opInstance);
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {
		return (point.getCodeElement() instanceof CtStatement);
	}

}
