package fr.inria.astor.core.solutionsearch.spaces.operators.demo;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.StatementLevelOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.IngredientBasedOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class NullPreconditionWithExpressionOperator extends IngredientBasedOperator implements StatementLevelOperator {
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint, Ingredient ingredient,
			IngredientTransformationStrategy transformationStrategy) {

		List<OperatorInstance> instances = new ArrayList<>();

		List<Ingredient> ingredientsTransformed = transformationStrategy.transform(modificationPoint, ingredient);

		for (Ingredient iIngredient : ingredientsTransformed) {

			CtExpression condition = (CtExpression) iIngredient.getCode();
			CtIf precondition = MutationSupporter.factory.createIf();
			precondition.setCondition(condition);
			precondition.setThenStatement((CtStatement) modificationPoint.getCodeElement().clone());

			OperatorInstance operatorInstance = new StatementOperatorInstance(modificationPoint, this, precondition);

			instances.add(operatorInstance);

		}

		return instances;

	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		if (!(point.getCodeElement() instanceof CtStatement))
			return false;

		// do not insert after a return
		if (point.getCodeElement() instanceof CtConstructorCall || point.getCodeElement() instanceof CtReturn) {
			return false;
		}

		// Otherwise, accept the element
		return true;
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
