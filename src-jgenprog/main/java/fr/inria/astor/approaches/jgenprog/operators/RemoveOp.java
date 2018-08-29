package fr.inria.astor.approaches.jgenprog.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RemoveOp extends StatatementIngredientOperator implements StatementLevelOperator {

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtBlock parentBlock = stmtoperator.getParentBlock();

		if (parentBlock != null) {

			try {
				parentBlock.getStatements().remove(stmtoperator.getLocationInParent());
				successful = true;
				operation.setSuccessfulyApplied(successful);
				StatementSupporter.updateBlockImplicitly(parentBlock, false);
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
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtBlock<?> parentBlock = stmtoperator.getParentBlock();
		if (parentBlock != null) {
			if ((parentBlock.getStatements().isEmpty() && stmtoperator.getLocationInParent() == 0)
					|| (parentBlock.getStatements().size() >= stmtoperator.getLocationInParent())) {
				parentBlock.getStatements().add(stmtoperator.getLocationInParent(), ctst);
				parentBlock.setImplicit(stmtoperator.isParentBlockImplicit());
				return true;
			} else {
				log.error(
						"Problems to recover, re-adding " + ctst + " at location " + stmtoperator.getLocationInParent()
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
		// Do not remove local declaration
		if (point.getCodeElement() instanceof CtLocalVariable) {
			CtLocalVariable lv = (CtLocalVariable) point.getCodeElement();
			boolean shadow = false;
			CtClass parentC = point.getCodeElement().getParent(CtClass.class);
			List<CtField> ff = parentC.getFields();
			for (CtField<?> f : ff) {
				if (f.getSimpleName().equals(lv.getSimpleName()))
					shadow = true;
			}
			if (!shadow)
				return false;
		}
		// do not remove the last statement
		CtMethod parentMethd = point.getCodeElement().getParent(CtMethod.class);
		if (point.getCodeElement() instanceof CtReturn
				&& parentMethd.getBody().getLastStatement().equals(point.getCodeElement())) {
			return false;
		}

		// Otherwise, accept the element
		return true;
	}

	@Override
	public final boolean needIngredient() {
		return false;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> operatorIntances = new ArrayList<>();
		operatorIntances.add(this.createOperatorInstance(modificationPoint));

		return operatorIntances;
	}
}
