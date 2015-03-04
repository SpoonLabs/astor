package fr.inria.astor.core.loop.evolutionary;

import java.util.List;
import java.util.Random;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.ExpressionRevolver;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Extension of Evolutionary loop with GenProgOperations to manage IfConditions
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JGenProgIfExpression extends JGenProg {

	
	Random random = new Random();
	
	public JGenProgIfExpression(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);

	}

	/**
	 * Create a Gen Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */
	@Override
	protected GenOperationInstance createGenMutationForElement(Gen gen) throws IllegalAccessException {
		Gen genSusp = gen;

		GenProgMutationOperation operationType = GenProgMutationOperation.REPLACE;

		if (!(genSusp.getRootElement() instanceof CtIf)) {
			// logger.error(".....The pointed Element is Not a statement");
			return null;
		}
		CtIf targetIF = (CtIf) genSusp.getRootElement();

		CtElement cpar = targetIF.getParent();
		
		if ((cpar == null || !(cpar instanceof CtBlock))) {
			return null;
		}
		// Should be the if.....
		CtBlock parentBlock = (CtBlock) cpar;

		GenOperationInstance operation = new GenOperationInstance();
		CtExpression fullIfCondition = targetIF.getCondition();
		CtExpression targetExpression = getExpressionToReplace(fullIfCondition);
		
		operation.setOriginal(targetExpression);
		operation.setOperationApplied(operationType);
		operation.setParentBlock(parentBlock);
		operation.setGen(genSusp);

		
		CtElement fix = getFixIngredient(gen, targetExpression);
		if (fix == null) {
			return null;
		}
		operation.setModified(fix);
	
		return operation;
	}
	

	
	private CtExpression getExpressionToReplace(CtExpression fullIfCondition) {
		List<CtExpression<Boolean>> ctExp = ExpressionRevolver.getExpressions(fullIfCondition);
		if(ctExp == null || ctExp.isEmpty())
			return fullIfCondition;
		
		int index = random.nextInt(ctExp.size());		
		return ctExp.get(index);
	}

	public void undoOperationToSpoonElement(GenOperationInstance operation) {
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		fix.replace(ctst);
	}

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	protected void applyNewMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException {

		boolean successful = false;
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		//
		try {
			ctst.replace((CtExpression) fix);
			successful = true;
			operation.setSuccessfulyApplied((successful));
		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}

	}

	public boolean isSuccessApplied(String previous, CtStatement sts) {
		CtBlock parent = (CtBlock) sts.getParent();
		return !parent.toString().equals(previous);

	}

	

}
