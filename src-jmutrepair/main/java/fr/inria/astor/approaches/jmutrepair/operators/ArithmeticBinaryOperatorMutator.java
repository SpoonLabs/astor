package fr.inria.astor.approaches.jmutrepair.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.approaches.jmutrepair.MutantCtElement;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

/**
 * Comparison binary operation
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ArithmeticBinaryOperatorMutator extends BinaryOperatorMutator {

	List<BinaryOperatorKind> operators = null;

	public ArithmeticBinaryOperatorMutator(Factory factory) {
		super(factory);
		operators = Arrays.asList(BinaryOperatorKind.PLUS, BinaryOperatorKind.MINUS, BinaryOperatorKind.MUL,
				BinaryOperatorKind.DIV, BinaryOperatorKind.MOD

		);

	}

	public List<MutantCtElement> execute(CtElement toMutate) {

		List<MutantCtElement> result = new ArrayList<MutantCtElement>();

		if (toMutate instanceof CtBinaryOperator<?>) {
			CtBinaryOperator<?> op = (CtBinaryOperator<?>) toMutate;

			addRemainingsAndFoward(result, op, operators);
		}

		return result;

	}

	@Override
	public double getProbabilityChange(BinaryOperatorKind oldKind, BinaryOperatorKind modifiedKind) {

		return 1;
	}

}
