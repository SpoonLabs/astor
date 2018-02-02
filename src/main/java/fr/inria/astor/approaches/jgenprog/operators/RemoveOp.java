package fr.inria.astor.approaches.jgenprog.operators;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
/**
 * 
 * @author Matias Martinez
 *
 */
public class RemoveOp extends StatementLevelOperator {

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtBlock parentBlock = operation.getParentBlock();

		if (parentBlock != null) {

			try {
				parentBlock.getStatements().remove(operation.getLocationInParent());
				successful = true;
				operation.setSuccessfulyApplied(successful);
				this.updateBlockImplicitly(parentBlock, false);
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
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return removePoint(p, opInstance);
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtBlock<?> parentBlock = operation.getParentBlock();
		if (parentBlock != null) {
			if ((parentBlock.getStatements().isEmpty() && operation.getLocationInParent() == 0)
					|| (parentBlock.getStatements().size() >= operation.getLocationInParent())) {
				parentBlock.getStatements().add(operation.getLocationInParent(), ctst);
				parentBlock.setImplicit(operation.isParentBlockImplicit());
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
	public boolean canBeAppliedToPoint(ModificationPoint point) {
		if (!(point.getCodeElement() instanceof CtStatement))
			return false;
		
		if(point.getCodeElement() instanceof CtLocalVariable){
			return false;
		}

		return true;
	}
	
	@Override
	public boolean needIngredient(){
		return false;
	}
}
