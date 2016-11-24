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
public class ReplaceOp extends AstorOperator {


	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
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
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		boolean sucess = true;
		sucess &= removePoint(p, opInstance);
		sucess &= addPoint(p, opInstance);

		return sucess;

	}

	@Override
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		if (parentBlock != null) {
				fix.replace((CtStatement) ctst);
				return true;

		}
		return false;
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
