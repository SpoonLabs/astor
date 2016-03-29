package fr.inria.astor.approaches.mutRepair.operators.ctmutators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.MutantCtElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.factory.Factory;

/**
 * Negates or undo a negation for CtExpression of boolean type.
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class NegationUnaryOperatorConditionMutator extends SpoonMutator {

	public NegationUnaryOperatorConditionMutator(Factory factory) {
		super(factory);
	}

	@Override
	public List<MutantCtElement> execute(CtElement toMutate) {
		//List<CtElement> result = new ArrayList<CtElement>();
		List<MutantCtElement> result = new ArrayList<MutantCtElement>();

		if (toMutate instanceof CtUnaryOperator<?>) {
			CtUnaryOperator<?> unary = (CtUnaryOperator<?>) toMutate;
			if (unary.getKind() == UnaryOperatorKind.NOT) {
				CtExpression expIF = factory.Core().clone(unary.getOperand());
				MutantCtElement mutatn = new MutantCtElement(expIF,0.3);
				//result.add(expIF);
				result.add(mutatn);
			}
		} else {
			if (toMutate instanceof CtTypedElement<?>) {
				CtExpression<?> inv = (CtExpression<?>) toMutate;
				if (inv.getType()!= null && inv.getType().getSimpleName().equals(boolean.class.getSimpleName())) {
					CtExpression<?> invClone = factory.Core().clone(inv);
					CtUnaryOperator unary = factory.Core().createUnaryOperator();
					unary.setOperand(invClone);
					unary.setKind(UnaryOperatorKind.NOT);
					//result.add(unary);
					MutantCtElement mutatn = new MutantCtElement(unary,3);
					//result.add(expIF);
					result.add(mutatn);
				}
				
			}
		}
		return result;
	}

	@Override
	public String key() {
		return "unaryOperator";
	}

	@Override
	public void setup() {
	}

	@Override
	public int levelMutation() {
		return 1;
	}

}
