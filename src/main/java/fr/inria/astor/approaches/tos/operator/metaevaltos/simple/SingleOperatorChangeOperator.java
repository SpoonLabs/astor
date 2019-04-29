package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
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
public class SingleOperatorChangeOperator extends OperatorInstance {

	CtExpression leftOriginal = null;
	CtExpression rightOriginal = null;
	BinaryOperatorKind operator;
	CtBinaryOperator original = null;

	public SingleOperatorChangeOperator(ModificationPoint modificationPoint, CtBinaryOperator original,
			CtExpression leftOriginal, CtExpression rightNew, BinaryOperatorKind operator,
			AstorOperator astoroperator) {
		super();
		this.leftOriginal = leftOriginal;
		this.rightOriginal = rightNew;
		this.operator = operator;
		this.original = original;
		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);
	}

	@Override
	public boolean applyModification() {
		CtBinaryOperator newBinaryOperator = new CtBinaryOperatorImpl<>();
		newBinaryOperator.setKind(operator);

		CtExpression leftOriginal2 = (CtExpression) MetaGenerator.geOriginalElement(leftOriginal);
		newBinaryOperator.setLeftHandOperand(leftOriginal2.clone());

		CtExpression rightOriginal2 = (CtExpression) MetaGenerator.geOriginalElement(rightOriginal);
		newBinaryOperator.setRightHandOperand(rightOriginal2.clone());

		//
		newBinaryOperator.setFactory(MutationSupporter.getFactory());
		newBinaryOperator.setParent(original.getParent());
		//

		super.setOriginal(original);
		super.setModified(newBinaryOperator);

		return super.applyModification();
	}

}
