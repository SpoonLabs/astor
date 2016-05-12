package fr.inria.astor.core.manipulation.modelTransformer;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ModificationInstance;
import spoon.reflect.code.CtExpression;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionTransformer {

	private static Logger log = Logger.getLogger(ExpressionTransformer.class.getName());

	public static boolean undoReplace(ModificationInstance operation) {
		try {
			CtExpression ctst = (CtExpression) operation.getOriginal();
			CtExpression fix = (CtExpression) operation.getModified();
			fix.replace(ctst);
			return true;

		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			return false;
		}

	}

	public static boolean doReplace(ModificationInstance operation) {
		boolean successful = false;

		try {

			CtExpression ctst = (CtExpression) operation.getOriginal();
			CtExpression fix = (CtExpression) operation.getModified();

			ctst.replace((CtExpression) fix);
			successful = true;
			operation.setSuccessfulyApplied((successful));

		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}
		return successful;
	}

}
