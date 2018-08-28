package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InsertAfterOp extends InsertStatementOp {

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {

		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;

		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = stmtoperator.getParentBlock();

		if (parentBlock != null) {
			ctst.insertAfter((CtStatement) fix);
			fix.setParent(parentBlock);
			successful = true;
			operation.setSuccessfulyApplied(successful);
			StatementSupporter.updateBlockImplicitly(parentBlock, true);
		} else {
			log.error("Operation not applied. Parent null");
		}
		return successful;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = stmtoperator.getParentBlock();
		int position = stmtoperator.getLocationInParent();
		position += 1;
		boolean sucessful = StatementSupporter.remove(parentBlock, fix, position);
		parentBlock.setImplicit(stmtoperator.isParentBlockImplicit());
		return sucessful;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		boolean apply = super.canBeAppliedToPoint(point);
		if (!apply)
			return apply;

		// do not insert after a return
		if (point.getCodeElement() instanceof CtReturn) {
			return false;
		}

		// Otherwise, accept the element
		return true;
	}
}
