package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtExpression;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SingleLogicRedOperator extends OperatorInstance {

	CtExpression previousOriginal = null;
	CtExpression newExpression = null;

	public SingleLogicRedOperator(ModificationPoint modificationPoint, CtExpression previousOriginal,
			CtExpression newExpression, AstorOperator astoroperator) {
		super();
		this.previousOriginal = previousOriginal;
		this.newExpression = newExpression;

		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);
	}

	@Override
	public boolean applyModification() {

		super.setOriginal(previousOriginal);
		super.setModified(newExpression);

		return super.applyModification();
	}

}
