package fr.inria.astor.core.solutionsearch.spaces.operators.demo;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.StatementLevelOperator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AutonomousOperator;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;

/**
 * 
 * @author Matias Martinez
 *
 */
public class NullPreconditionOperatorMI extends AutonomousOperator implements StatementLevelOperator {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> instances = new ArrayList<>();

		CtInvocation invocation = (CtInvocation) modificationPoint.getCodeElement();

		CtExpression targetExpresionOfMI = invocation.getTarget();

		if (targetExpresionOfMI instanceof CtVariableAccess) {

			CtStatement parentStatement = modificationPoint.getCodeElement().getParent(CtStatement.class);

			CtExpression ifcondition = MutationSupporter.factory.createBinaryOperator(targetExpresionOfMI,
					MutationSupporter.factory.createCodeSnippetExpression("null").compile(), BinaryOperatorKind.NE);

			CtIf precodition = MutationSupporter.factory.createIf();
			precodition.setCondition(ifcondition);
			precodition.setThenStatement(parentStatement);

			OperatorInstance operatorInstance = new StatementOperatorInstance(modificationPoint, this, parentStatement,
					precodition);

			instances.add(operatorInstance);
		}
		return instances;

	};

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtInvocation);
	}

	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		boolean successful = false;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();

		CtBlock parentBlock = stmtoperator.getParentBlock();

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
	public boolean undoChangesInModel(OperatorInstance operation, ProgramVariant p) {
		StatementOperatorInstance stmtoperator = (StatementOperatorInstance) operation;
		CtStatement ctst = (CtStatement) operation.getOriginal();
		CtStatement fix = (CtStatement) operation.getModified();
		CtBlock<?> parentBlock = stmtoperator.getParentBlock();
		if (parentBlock != null) {
			fix.replace((CtStatement) ctst);
			return true;

		}
		return false;
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {

		return false;
	}

}
