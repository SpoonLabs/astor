package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SingleLogicExpOperator extends OperatorInstance {

	CtExpression leftOriginal = null;
	CtExpression rightNew = null;
	BinaryOperatorKind operator;

	public SingleLogicExpOperator(ModificationPoint modificationPoint, CtExpression leftOriginal, CtExpression rightNew,
			BinaryOperatorKind operator, AstorOperator astoroperator) {
		super();
		this.leftOriginal = leftOriginal;
		this.rightNew = rightNew;
		this.operator = operator;
		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);
	}

	@Override
	public boolean applyModification() {
		CtBinaryOperator binaryOperator = new CtBinaryOperatorImpl<>();
		binaryOperator.setKind(operator);

		binaryOperator.setLeftHandOperand(leftOriginal.clone());

		CtExpression newRightExpression = rightNew;
		binaryOperator.setRightHandOperand(newRightExpression);

		//
		binaryOperator.setFactory(MutationSupporter.getFactory());
		binaryOperator.setParent(leftOriginal.getParent());
		//

		super.setOriginal(leftOriginal);
		super.setModified(binaryOperator);

		return super.applyModification();
	}

}
