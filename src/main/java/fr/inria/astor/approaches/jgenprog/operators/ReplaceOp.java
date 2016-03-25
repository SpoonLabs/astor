package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

public class ReplaceOp extends AstorOperator {


	public boolean applyChangesInModel(ModificationInstance operation, ProgramVariant p) {
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = operation.getParentBlock();

		if (parentBlock != null) {

			try {
				ctst.replace((CtStatement) fix);
				fix.setParent(parentBlock);
				successful = true;
				operation.setSuccessfulyApplied(successful);
			} catch (Exception ex) {
				log.error("Error applying an operation, exception: " + ex.getMessage());
				operation.setExceptionAtApplied(ex);
				operation.setSuccessfulyApplied(false);
			}
		} else {
			log.error("Operation not applied. Parent null ");
		}
		return successful;
	}

	@Override
	public boolean updateProgramVariant(ModificationInstance opInstance, ProgramVariant p) {
		removePoint(p, opInstance);
		addPoint(p, opInstance);

		return true;

	}

	@Override
	public boolean undoChangesInModel(ModificationInstance operation, ProgramVariant p) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		int position = operation.getLocationInParent();
		if (parentBlock != null) {
				fix.replace((CtStatement) ctst);

		}
		//TODO:
		return true;
	}

	@Override
	public boolean applyToPoint(ModificationPoint point) {
		
		return (point.getCodeElement() instanceof CtStatement);
	}
	
	@Override
	public boolean needIngredient(){
		return true;
	}
}
