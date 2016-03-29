package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import java.util.List;

import fr.inria.astor.core.entities.MutantCtElement;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.factory.Factory;

/**
 * Mutator made only for test. It replaces the return expression per NULL.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class BinaryOperatorMutator extends SpoonMutator<CtExpression> {

	public static boolean one_order = true;

	public BinaryOperatorMutator(Factory factory) {
		super(factory);

	}
	/**
	 * Creates alternatives Expressions (Binary Operations) given a list of
	 * Binary operators kind operators. The original kind (that one included in
	 * the original expression is not analyzed)
	 * 
	 * @param result
	 * @param op
	 * @param kind
	 * @param operators
	 */
	protected void addRemainings(List<CtExpression> result,
			CtBinaryOperator<?> op, BinaryOperatorKind kind,
			List<BinaryOperatorKind> operators) {
		// TODO: recursive?
		if (operators.contains(kind)) {
			for (BinaryOperatorKind binaryOperatorKind : operators) {
				if (binaryOperatorKind != kind) {
					// Cloning
					CtExpression right = factory.Core().clone(
							op.getRightHandOperand());
					CtExpression left = factory.Core().clone(
							op.getLeftHandOperand());

					CtBinaryOperator binaryOp = factory.Code()
							.createBinaryOperator(left, right,
									binaryOperatorKind);
					// Set parent
					right.setParent(binaryOp);
					left.setParent(binaryOp);

					result.add(binaryOp);
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	protected void addRemainingsAndFoward(List<MutantCtElement> result,
			CtBinaryOperator<?> op, List<BinaryOperatorKind> operators) {

		if (!operators.contains(op.getKind())) {
			return;
		}

		for (BinaryOperatorKind binaryOperatorKind : operators) {

			if (binaryOperatorKind.equals(op.getKind())) {

				CtExpression rightOriginal = factory.Core().clone(
						op.getRightHandOperand());
				List<MutantCtElement> mutationRight = parentMutator.execute(rightOriginal);
				//mutationRight.add(right);

				CtExpression leftOriginal = factory.Core().clone(
						op.getLeftHandOperand());
				List<MutantCtElement> mutationLeft = parentMutator.execute(leftOriginal);
				//mutationLeft.add(left);

				// AllCombinations:
				for (MutantCtElement left_i : mutationLeft) {
					CtBinaryOperator bop = createBinaryOp( op, binaryOperatorKind, (CtExpression) left_i.getElement(),rightOriginal);
					left_i.setElement(bop);
					result.add(left_i);
				}
				for (MutantCtElement right_i : mutationRight) {
					CtBinaryOperator bop = createBinaryOp( op, binaryOperatorKind, leftOriginal,(CtExpression) right_i.getElement());
					right_i.setElement(bop);
					result.add(right_i);
				}
				
			} else {
				//diff	
				CtExpression right_c = factory.Core().clone(
						op.getRightHandOperand());
				CtExpression left_c = factory.Core().clone(
						op.getLeftHandOperand());

				CtBinaryOperator binaryOp = factory.Code()
						.createBinaryOperator(left_c, right_c,
								binaryOperatorKind);
				// Set parent
				right_c.setParent(binaryOp);
				left_c.setParent(binaryOp);
				double prob = getProbabilityChange(op.getKind(),binaryOperatorKind);
				MutantCtElement mutant  = new MutantCtElement(binaryOp, prob);
				//result.add(binaryOp);
				result.add(mutant);
			}

		}
	}
	
	public abstract double getProbabilityChange(BinaryOperatorKind oldKind, BinaryOperatorKind modifiedKind);
	
		
	private CtBinaryOperator createBinaryOp(
			CtBinaryOperator<?> op, BinaryOperatorKind binaryOperatorKind,
			CtExpression left_i, CtExpression right_i) {
		CtExpression right_c = factory.Core().clone(right_i);
		CtExpression left_c = factory.Core().clone(left_i);

		CtBinaryOperator binaryOp = factory.Code()
				.createBinaryOperator(left_c, right_c,
						binaryOperatorKind);
		// Set parent
		right_c.setParent(binaryOp);
		left_c.setParent(binaryOp);
		return binaryOp;
	
	}


	public String key() {
		return "return-null-test";
	}

	public void setup() {

	}

	@Override
	public int levelMutation() {
		return 1;
	}
}
