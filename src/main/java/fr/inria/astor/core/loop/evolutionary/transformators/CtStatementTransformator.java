package fr.inria.astor.core.loop.evolutionary.transformators;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class CtStatementTransformator implements ModelTransformator {

	private Logger log = Logger.getLogger(CtStatementTransformator.class.getName());

	public void revert(GenOperationInstance operation) {
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock parentBlock = operation.getParentBlock();
		if (parentBlock != null) {
			GenProgMutationOperation operator = (GenProgMutationOperation) operation.getOperationApplied();

			switch (operator) {
			case DELETE:
				if ((parentBlock.getStatements().isEmpty() && operation.getLocationInParent() == 0)
						|| (parentBlock.getStatements().size() >= operation.getLocationInParent())) {
					parentBlock.getStatements().add(operation.getLocationInParent(), ctst);
				} else {
					log.error("Problems to recover, re-adding " + ctst + " at location "
							+ operation.getLocationInParent() + " from parent size "
							+ parentBlock.getStatements().size());
					throw new IllegalStateException("Undo:Not valid index");
				}
				break;
			case INSERT_BEFORE:
				parentBlock.getStatements().remove(fix);
				break;
			case INSERT_AFTER:
				parentBlock.getStatements().remove(fix);
				break;
			case REPLACE:
				fix.replace((CtStatement) ctst);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	public void transform(GenOperationInstance operation) throws IllegalAccessException {
		
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = operation.getParentBlock();

		if (parentBlock != null) {
			int locationIndex = parentBlock.getStatements().indexOf(ctst);
			if (locationIndex < 0) {
				throw new IllegalAccessException("Position in parent is not positive for " + operation);
			}
			operation.setLocationInParent(locationIndex);
			//
			GenProgMutationOperation operator = (GenProgMutationOperation) operation.getOperationApplied();
			try {
				switch (operator) {
				case DELETE:
					parentBlock.getStatements().remove(ctst);
					successful = true;
					break;
				case INSERT_BEFORE:
					ctst.insertBefore((CtStatement) fix);
					successful = true;
					break;
				case INSERT_AFTER:
					ctst.insertAfter((CtStatement) fix);
					successful = true;
					break;
				case REPLACE:
					ctst.replace((CtStatement) fix);
					successful = true;
					break;

				default:
					log.error("Unknow Mutation Type");
					break;
				}
				operation.setSuccessfulyApplied(successful);
			} catch (Exception ex) {
				log.error("Error applying an operation, exception: " + ex.getMessage());
				operation.setExceptionAtApplied(ex);
				operation.setSuccessfulyApplied(false);
			}
		}
	}

	/**
	 * This transformatios is capable of manage operation over Statements that are included (i.e., the parent) in blocks.
	 * See that, it does not manage, for instance, invocation inside expressions.
	 */
	@Override
	public boolean canTransform(GenOperationInstance operation) {
		return (operation.getOriginal() instanceof CtStatement) 
				&& operation.getOriginal().getParent() instanceof CtBlock;
	}

}
