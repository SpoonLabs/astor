package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

public class InsertAfterOp extends AstorOperator {

	@Override
	public boolean applyChangesInModel(ModificationInstance operation, ProgramVariant p) {
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = operation.getParentBlock();

		if (parentBlock != null) {

			ctst.insertAfter((CtStatement) fix);
			fix.setParent(parentBlock);
			successful = true;
			operation.setSuccessfulyApplied(successful);

		} else {
			log.error("Operation not applied. Parent null");
		}
		return successful;
	}

	@Override
	public boolean undoChangesInModel(ModificationInstance operation, ProgramVariant p) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		int position = operation.getLocationInParent();
		//TODO: to check, in the creation of ModifInstance we put +1 in InsertAfter
		position += 1;
		remove(parentBlock, fix, position);
		return true;

	}

	public static void remove(CtBlock parent, CtStatement fix, int pos) {

		CtStatement s = parent.getStatement(pos);
		if (fix.equals(s)) {// To be sure that the position has the element we
							// want to remove
			parent.getStatements().remove(pos);
		} else {
			throw new IllegalStateException("Undo: Not valid fix position");
		}
	}
	@Override
	public boolean updateProgramVariant(ModificationInstance opInstance, ProgramVariant p) {
		addPoint(p, opInstance);
		return false;
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
