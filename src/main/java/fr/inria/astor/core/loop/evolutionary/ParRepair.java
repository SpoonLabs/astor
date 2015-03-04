package fr.inria.astor.core.loop.evolutionary;

import java.util.List;
import java.util.Random;

import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.declaration.CtElement;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.taxonomy.ParMutationOperation;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Extension of Evolutionary loop with GenProgOperations to manage IfConditions
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ParRepair extends JGenProg {

	public ParRepair(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);

	}

//	private Logger log = Logger.getLogger(GenProgLoopPARExpressionEngine.class.getName());

	/**
	 * Create a Gen Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected GenOperationInstance createGenMutationForElement(Gen gen) throws IllegalAccessException {
		// GenSuspicious genSusp = (GenSuspicious) gen;
		Gen genSusp = gen;

		
		if (!(genSusp.getRootElement() instanceof CtIf)) {
			// logger.error(".....The pointed Element is Not a statement");
			return null;
		}
		CtIf targetIF = (CtIf) genSusp.getRootElement();

		CtElement cpar = targetIF.getParent();

		if ((cpar == null /*|| !(cpar instanceof CtBlock)*/)) {
			return null;
		}

		CtExpression condit = targetIF.getCondition();
		CtExpression originalCond = condit;
		
		ParMutationOperation operationType = (ParMutationOperation) repairActionSpace.getNextMutation();

		if (operationType.equals(ParMutationOperation.DELETE_BEFORE)
				|| operationType.equals(ParMutationOperation.DELETE_AFTER)) {
		
			condit = isBinary(condit);
			// We can not apply both elements to not binary operator.
			if (condit == null) {
				log.info("The condition is not binary, it can not be applied a binary op mutation, type:  " +operationType+", in: "+originalCond);
				return null;
			}
		}
		
		GenOperationInstance operation = new GenOperationInstance();
		operation.setOriginal(condit);
		operation.setOperationApplied(operationType);
		//operation.setParentBlock(parentBlock);
		operation.setGen(genSusp);
		
		//--
		int elementsFromFixSpace = this.fixspace.getFixSpace(genSusp.getCtClass().getQualifiedName()).size();
		CtElement fix = null;
		//
		
		int max = 0;
		boolean continueSearching = true;
		while(continueSearching && max < elementsFromFixSpace){
			max++;
			CtElement fixingredient = null;
			if (!operationType.equals(ParMutationOperation.DELETE_BEFORE)
					&& !operationType.equals(ParMutationOperation.DELETE_AFTER)) {
			
				fixingredient = this.fixspace.getElementFromSpace(gen.getCtClass().getQualifiedName());
				if(fixingredient == null){
					continue;
				}
			}
			
			fix = createFix(condit, (CtExpression) fixingredient, operationType);
			
			if(fix == null){
				return null;
			}
			
			if (fix.getSignature().equals(targetIF.getSignature()) ) {
			//	log.info("Discarting operation, replacement is the same than the replaced code");
				// Discard the operation.
			}else
			if (includeClause(condit, fix, operationType)) {
				//log.info("Discarting: Clause Included");
			}else
			continueSearching = alreadyApplied(gen.getProgramVariant().getId(),fix.toString(), targetIF.getCondition().toString());
		
		} 
		if(continueSearching ){
			log.info("All mutations were applied: no mutation left to apply");
			return null;
		}
		
		//---
		operation.setModified(fix);
		
		return operation;
	}

	/**
	 * Return if the expression already contains the fix expression 
	 * @param condit
	 * @param fix
	 * @param operationType
	 * @return
	 */
	private boolean includeClause(CtExpression condit, CtElement fix, ParMutationOperation operationType) {
		if (operationType.equals(ParMutationOperation.INSERT_AFTER)
				|| operationType.equals(ParMutationOperation.INSERT_BEFORE)) {
		return (condit.toString().contains(fix.toString()) 
					|| fix.toString().contains(condit.toString()) );
		}
		return false;
	}

	public CtBinaryOperator isBinary(CtExpression<Boolean> condition) {
		if (condition instanceof CtBinaryOperator) {
			CtBinaryOperator bi = (CtBinaryOperator) condition;
			bi.getKind();
			if (bi.getKind().equals(BinaryOperatorKind.AND) || bi.getKind().equals(BinaryOperatorKind.OR))
				return (CtBinaryOperator) condition;
			else
				return null;
		}
		if (condition instanceof CtUnaryOperator<?>) {
			return isBinary(((CtUnaryOperator) condition).getOperand());
		}
		return null;
	}

	/**
	 * Uniform random over AND and OR operators.
	 * 
	 * @return
	 */
	private BinaryOperatorKind getBinaryOperator() {
		Random r = new Random();
		double d = r.nextDouble();
		if (d > 0.5d)
			return BinaryOperatorKind.AND;
		else
			return BinaryOperatorKind.OR;

	}

	@Override
	protected void applyPreviousMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException {
		try {
			boolean successful = false;
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

	}

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("rawtypes")
	protected void applyNewMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException {
		
		this.applyPreviousMutationOperationToSpoonElement(operation);	
	}
		protected void applyNewMutationOperationToSpoonElementOLD(GenOperationInstance operation)
				throws IllegalAccessException {	
		CtExpression rightTerm = null, leftTerm = null;
		try {
			boolean successful = false;
			CtExpression ctst = (CtExpression) operation.getOriginal();
			CtExpression fix = (CtExpression) operation.getModified();
			//log.info(operation.getOperationApplied() + " bug: " + ctst + " fix: " + fix);

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
				CtExpression left = mutatorSupporter.getFactory().Core().clone(op.getLeftHandOperand());
				ctst.replace(left);
				// Now the fix is the binary operator.
				operation.setModified(left);
				operation.setSuccessfulyApplied(true);

				// return;
			} else if (operation.getOperationApplied() == ParMutationOperation.DELETE_BEFORE) {
				CtBinaryOperator op = (CtBinaryOperator) ctst;
				CtExpression right = mutatorSupporter.getFactory().Core().clone(op.getRightHandOperand());
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

				CtBinaryOperator binaryOp = mutatorSupporter
						.getFactory()
						.Code()
						.createBinaryOperator(mutatorSupporter.getFactory().Core().clone(leftTerm),
								mutatorSupporter.getFactory().Core().clone(rightTerm), getBinaryOperator());

				ctst.replace((CtExpression) binaryOp);
				// Now the fix is the binary operator.
				operation.setModified(binaryOp);

				successful = true;
				/*if (binaryOp.getParent() == null) {
					log.error("No parent setted");
					successful = false;
				}*/

				operation.setSuccessfulyApplied((successful));
			}
			log.info(" applied: " + ctst.getParent().getSignature());

		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}

	}
	
	protected CtExpression createFix(CtExpression ctst, CtExpression fix,ParMutationOperation op )
			throws IllegalAccessException {
		
		CtExpression createdFix = null;
		CtExpression rightTerm = null, leftTerm = null;
		try {
			
			//
			// 1-Case: Replace expression
			if (op  == ParMutationOperation.REPLACE) {
				//ctst.replace((CtExpression) fix);
				
				createdFix = fix;
			} else

			if (op == ParMutationOperation.DELETE_AFTER) {
				CtBinaryOperator oper = (CtBinaryOperator) ctst;
				CtExpression left = mutatorSupporter.getFactory().Core().clone(oper.getLeftHandOperand());
				createdFix =  (left);
				
				
			} else if (op  == ParMutationOperation.DELETE_BEFORE) {
				CtBinaryOperator oper = (CtBinaryOperator) ctst;
				CtExpression right = mutatorSupporter.getFactory().Core().clone(oper.getRightHandOperand());
				createdFix =  right;
			
				// return;
			}
			//CtExpression ctst = (CtExpression) operation.getOriginal();
		//	CtExpression fix = (CtExpression) operation.getModified();
			// 3-Insert term in predicate:
			else {
				if (op == ParMutationOperation.INSERT_BEFORE) {
					leftTerm = fix;
					rightTerm = ctst;
				} else {

					leftTerm = ctst;
					rightTerm = fix;
				}

				CtBinaryOperator binaryOp = mutatorSupporter
						.getFactory()
						.Code()
						.createBinaryOperator(mutatorSupporter.getFactory().Core().clone(leftTerm),
								mutatorSupporter.getFactory().Core().clone(rightTerm), getBinaryOperator());

				createdFix =  binaryOp;
			

			}
		//	log.info(" applied: " + ctst.getParent().getSignature());

		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
		}
		return createdFix;
	}

	@SuppressWarnings("rawtypes")
	public void undoOperationToSpoonElement(GenOperationInstance operation) {
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		//log.info("The modified fix is " + fix + ", the original is: " + ctst);
		fix.replace(ctst);
		//log.info("After Undoing " + fix.getParent().getSignature());
	
	}

	/**
	 * 
	 * @param variant
	 * @param operationofGen
	 */
	@Override
	protected void updateVariantGenList(ProgramVariant variant, GenOperationInstance operation) {
		List<Gen> gens = variant.getGenList();
		ParMutationOperation type = (ParMutationOperation) operation.getOperationApplied();
		if (type.equals(ParMutationOperation.DELETE_BEFORE) || type.equals(ParMutationOperation.DELETE_AFTER)
				|| type.equals(ParMutationOperation.REPLACE)) {
			boolean removed = gens.remove(operation.getGen());
			log.info("---updating gen list " + operation + " removed gen? " + removed);
		}
		if (!type.equals(ParMutationOperation.DELETE_BEFORE) && !type.equals(ParMutationOperation.DELETE_AFTER)) {
			Gen existingGen = operation.getGen();
			Gen newGen = variantFactory.cloneGen(existingGen, operation.getModified());
			log.info("---updating gen list " + operation + " adding gen: " + newGen);
			gens.add(newGen);
		}

	}

	public boolean isSuccessApplied(String previous, CtStatement sts) {
		CtBlock parent = (CtBlock) sts.getParent();
		return !parent.toString().equals(previous);

	}

	

}
