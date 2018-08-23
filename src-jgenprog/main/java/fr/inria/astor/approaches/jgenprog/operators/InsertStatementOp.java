package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.operators.IngredientBasedOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

/**
 * Abstract insert operator
 * 
 * @author Matias Martinez
 *
 */
public abstract class InsertStatementOp extends IngredientBasedOperator implements StatementLevelOperator {

	public boolean remove(CtBlock parentBlock, CtStatement fixStatement, int pos) {

		CtStatement s = parentBlock.getStatement(pos);
		// To be sure that the position has the element we
		// want to remove
		if (fixStatement.equals(s)) {
			parentBlock.getStatements().remove(pos);
			return true;
		} else {
			System.out.println("\n fx: " + fixStatement + "\n" + (s));
			throw new IllegalStateException("Undo: Not valid fix position");
		}
	}

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
