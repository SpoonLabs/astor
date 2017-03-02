package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
/**
 * 
 * @author Matias Martinez
 *
 */
public class InsertBeforeOp extends InsertOp {

	
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
		remove(parentBlock, fix, position);
		return true;

	}

}
