package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

public class RemoveOp extends AstorOperator {

	

	@Override
	public boolean applyChangesInModel(ModificationInstance operation, ProgramVariant p) {
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = operation.getParentBlock();

		if (parentBlock != null) {

			try {
				parentBlock.getStatements().remove(ctst);
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
		return removePoint(p, opInstance);
	}

	@Override
	public boolean undoChangesInModel(ModificationInstance operation, ProgramVariant p) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		int position = operation.getLocationInParent();
		if (parentBlock != null) {
			if ((parentBlock.getStatements().isEmpty() && operation.getLocationInParent() == 0)
					|| (parentBlock.getStatements().size() >= operation.getLocationInParent())) {
				if(operation.getLocationInParent() < 0 )
					System.out.println();;
				parentBlock.getStatements().add(operation.getLocationInParent(), ctst);
				return true;
			} else {
				log.error("Problems to recover, re-adding " + ctst + " at location " + operation.getLocationInParent()
						+ " from parent size " + parentBlock.getStatements().size());
				throw new IllegalStateException("Undo:Not valid index");
			}

		}
		return false;
	}

	@Override
	public boolean applyToPoint(ModificationPoint point) {
		
		return (point.getCodeElement() instanceof CtStatement);
	}
	
	@Override
	public boolean needIngredient(){
		return false;
	}
}
