package fr.inria.astor.core.manipulation.modelTransformer;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.OperatorInstance;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;

/**
 * Summarization of Model transformation.
 * 
 * @author Matias Martinez
 *
 */
public class StatamentTransformer {
	
	protected static Logger log = Logger.getLogger(StatamentTransformer.class.getName());
	
	
	public static boolean doReplaceStatement(OperatorInstance operation){
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
	
	public static boolean undoReplaceStatement(OperatorInstance operation) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = operation.getParentBlock();
		if (parentBlock != null) {
				fix.replace((CtStatement) ctst);
				return true;

		}
		return false;
	}
	
	public static boolean doRemoveStatement(OperatorInstance operation){
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
	
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
	

	public static boolean undoRemoveStatement(OperatorInstance operation) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtBlock<?> parentBlock = operation.getParentBlock();
		if (parentBlock != null) {
			if ((parentBlock.getStatements().isEmpty() && operation.getLocationInParent() == 0)
					|| (parentBlock.getStatements().size() >= operation.getLocationInParent())) {
				
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
	
}
