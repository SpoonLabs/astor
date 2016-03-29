package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.core.entities.MutantCtElement;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

/**
 * Mutator made only for test. It replaces the return expression per NULL.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class LogicalBinaryOperatorMutator extends BinaryOperatorMutator {
	List<BinaryOperatorKind> operators1 = null;

	public LogicalBinaryOperatorMutator(Factory factory) {
		super(factory);
		operators1 = Arrays.asList(BinaryOperatorKind.AND, BinaryOperatorKind.OR);

	}
	double[][] probs = {
			//AND
			{0,0.25}
			,//OR
			{0.25,0}}; 
	public List<MutantCtElement> execute(CtElement toMutate) {

		// List<CtExpression> result = new ArrayList<CtExpression>();
		List<MutantCtElement> result = new ArrayList<MutantCtElement>();

		if (toMutate instanceof CtBinaryOperator<?>) {
			CtBinaryOperator<?> op = (CtBinaryOperator<?>) toMutate;
			BinaryOperatorKind kind = op.getKind();

			addRemainingsAndFoward(result, op, operators1);

		}
		return result;
	}
	@Override
	public double getProbabilityChange(BinaryOperatorKind oldKind, BinaryOperatorKind modifiedKind) {
		int oldI= operators1.indexOf(oldKind);
		int newI= operators1.indexOf(modifiedKind);
		return probs[oldI][newI];
	}

	
}
