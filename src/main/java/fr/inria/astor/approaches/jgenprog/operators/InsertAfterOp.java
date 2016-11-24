package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
/**
 * 
 * @author Matias Martinez
 *
 */
public class InsertAfterOp extends AstorOperator {

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
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
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		int position = operation.getLocationInParent();
		position += 1;
		return remove(parentBlock, fix, position);
	}

	public static boolean remove(CtBlock parent, CtStatement fix, int pos) {

		CtStatement s = parent.getStatement(pos);
		if (fix.equals(s)) {// To be sure that the position has the element we
							// want to remove
			parent.getStatements().remove(pos);
			return true;
		} else {
			throw new IllegalStateException("Undo: Not valid fix position");
		}
	}
	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return addPoint(p, opInstance);
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {
		
		return (point.getCodeElement() instanceof CtStatement);
	}
	
	@Override
	public boolean needIngredient(){
		return true;
	}
}
