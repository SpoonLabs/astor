package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.simple.SingleTryOperator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class WrapwithTrySingleStatementOp extends ReplaceOp implements IOperatorWithTargetElement {
	private CtElement targetElement = null;

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> opInstances = new ArrayList<>();

		CtStatement statementPointed = (targetElement == null) ? (CtStatement) modificationPoint.getCodeElement()
				: (CtStatement) targetElement;

		SingleTryOperator singleTry = new SingleTryOperator(modificationPoint, statementPointed, this);

		opInstances.add(singleTry);

		return opInstances;
	}

	@Override
	public boolean needIngredient() {
		return false;
	}

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return target instanceof CtStatement;
	}
}
