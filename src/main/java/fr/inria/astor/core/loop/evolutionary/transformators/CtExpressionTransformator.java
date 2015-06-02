package fr.inria.astor.core.loop.evolutionary.transformators;

import org.apache.log4j.Logger;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.taxonomy.ParMutationOperation;
import fr.inria.astor.core.manipulation.MutationSupporter;

import fr.inria.astor.core.setup.RandomManager;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CtExpressionTransformator implements ModelTransformator{

	private Logger log = Logger.getLogger(CtExpressionTransformator.class.getName());
	
	
	@Override
	public void revert(GenOperationInstance operation) {
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		fix.replace(ctst);
		
		
	}

	@Override
	public void transform(GenOperationInstance operation) throws Exception {
		Factory factory = MutationSupporter.getFactory();
		
		CtExpression rightTerm = null, leftTerm = null;
		try {
			boolean successful = false;
			CtExpression ctst = (CtExpression) operation.getOriginal();
			CtExpression fix = (CtExpression) operation.getModified();
			//log.debug(operation.getOperationApplied() + " bug: " + ctst + " fix: " + fix);

			//
			// 1-Case: Replace expression
			if (operation.getOperationApplied() == ParMutationOperation.REPLACE) {
				ctst.replace((CtExpression) fix);
				successful = true;
				operation.setSuccessfulyApplied((successful));
				// return;
			} else

			if (operation.getOperationApplied() == ParMutationOperation.DELETE_AFTER) {
				CtBinaryOperator op = (CtBinaryOperator) ctst;
				CtExpression left = factory.Core().clone(op.getLeftHandOperand());
				ctst.replace(left);
				// Now the fix is the binary operator.
				operation.setModified(left);
				operation.setSuccessfulyApplied(true);

				// return;
			} else if (operation.getOperationApplied() == ParMutationOperation.DELETE_BEFORE) {
				CtBinaryOperator op = (CtBinaryOperator) ctst;
				CtExpression right = factory.Core().clone(op.getRightHandOperand());
				ctst.replace(right);
				// Now the fix is the binary operator.
				operation.setModified(right);
				operation.setSuccessfulyApplied(true);
				// return;
			}

			// 3-Insert term in predicate:
			else {
				if (operation.getOperationApplied() == ParMutationOperation.INSERT_BEFORE) {
					leftTerm = fix;
					rightTerm = ctst;
				} else {

					leftTerm = ctst;
					rightTerm = fix;
				}

				CtBinaryOperator binaryOp = factory
						.Code()
						.createBinaryOperator(factory.Core().clone(leftTerm),
								factory.Core().clone(rightTerm), getBinaryOperator());

				ctst.replace((CtExpression) binaryOp);
				// Now the fix is the binary operator.
				operation.setModified(binaryOp);

				successful = true;
			
				operation.setSuccessfulyApplied((successful));
			}
			log.debug(" applied: " + ctst.getParent().getSignature());

		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}

	}
	
	private BinaryOperatorKind getBinaryOperator() {
		double d = RandomManager.nextDouble();
		if (d > 0.5d)
			return BinaryOperatorKind.AND;
		else
			return BinaryOperatorKind.OR;

	}
	
	@Override
	public boolean canTransform(GenOperationInstance operation) {
		return (operation.getOriginal() instanceof CtExpression);
	}
}
