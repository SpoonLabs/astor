package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UnwrapfromIfOp extends ReplaceOp {

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return point.getCodeElement() instanceof CtIf;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		log.debug("Unwrap If:");

		CtIf ifToRemove = (CtIf) modificationPoint.getCodeElement();

		CtStatement stmtinsideThen = ifToRemove.getThenStatement().clone();

		stmtinsideThen.setParent(null);
		MutationSupporter.clearPosition(stmtinsideThen);

		List<OperatorInstance> opInstances = new ArrayList<>();

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, ifToRemove, stmtinsideThen);
		opInstances.add(opInstace);

		return opInstances;
	}

	@Override
	public boolean needIngredient() {
		return false;
	}

	int positionRemoved = -1;

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		CtStatement statementToRemove = (CtStatement) operation.getOriginal();
		CtStatement movedElement = (CtStatement) operation.getModified();

		CtBlock parentBlock = stmtoperator.getParentBlock();
		if (parentBlock != null) {

			if (movedElement instanceof CtBlock) {
				CtBlock movedBlock = (CtBlock) movedElement;
				statementToRemove.replace(movedBlock.getStatements());
				movedBlock.setParent(parentBlock);
				for (CtStatement newStmtBlock : movedBlock.getStatements()) {
					newStmtBlock.setParent(parentBlock);
				}

			} else {
				statementToRemove.replace((CtStatement) movedElement);
				movedElement.setParent(parentBlock);

			}
		}
		return true;

	}

	@Override
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = stmtoperator.getParentBlock();
		if (parentBlock != null) {

			if (fix instanceof CtBlock) {
				CtBlock movedBlock = (CtBlock) fix;
				CtStatement firstFromBlock = movedBlock.getStatement(0);
				firstFromBlock.replace((CtStatement) ctst);

				for (CtStatement s : movedBlock.getStatements()) {
					parentBlock.removeStatement(s);
				}
				return true;
			} else {
				fix.replace((CtStatement) ctst);
				return true;
			}

		}
		return false;
	}

}
