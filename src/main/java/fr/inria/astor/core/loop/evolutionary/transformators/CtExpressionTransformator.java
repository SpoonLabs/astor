package fr.inria.astor.core.loop.evolutionary.transformators;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.entities.taxonomy.MutationExpression;
import fr.inria.astor.core.entities.taxonomy.Operation;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.factory.Factory;

/**
 * Transformator of CtExpression.
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
			
			if (operation.getOperationApplied() == GenProgMutationOperation.REPLACE
				||	operation.getOperationApplied() == MutationExpression.REPLACE ) {
				ctst.replace((CtExpression) fix);
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
	
	
	@Override
	public boolean canTransform(GenOperationInstance operation) {
		return (operation.getOriginal() instanceof CtExpression);
	}
}
