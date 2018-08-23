package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InsertBeforeOp extends InsertStatementOp {

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = operation.getParentBlock();

		if (parentBlock != null) {
			ctst.insertBefore((CtStatement) fix);
			fix.setParent(parentBlock);
			successful = true;
			operation.setSuccessfulyApplied(successful);
			this.updateBlockImplicitly(parentBlock, true);
		} else {
			log.error("Operation not applied. Parent null ");
		}
		return successful;

	}

	@Override
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		int position = operation.getLocationInParent();
		boolean sucess = remove(parentBlock, fix, position);
		parentBlock.setImplicit(operation.isParentBlockImplicit());
		return sucess;

	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		boolean apply = super.canBeAppliedToPoint(point);
		if (!apply)
			return apply;

		// do not insert after a return
		if (point.getCodeElement() instanceof CtConstructorCall) {
			return false;
		}

		// Otherwise, accept the element
		return true;
	}

}
