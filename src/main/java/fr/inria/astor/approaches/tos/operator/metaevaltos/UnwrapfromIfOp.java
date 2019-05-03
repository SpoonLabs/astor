package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.simple.SingleUnWrapIfOperator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UnwrapfromIfOp extends ReplaceOp implements IOperatorWithTargetElement {
	private CtElement targetElement = null;

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return point.getCodeElement() instanceof CtIf;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		log.debug("Unwrap If:");

		CtIf ifToRemove = (targetElement == null) ? (CtIf) modificationPoint.getCodeElement() : (CtIf) targetElement;

		CtStatement stmtinsideThen = ifToRemove.getThenStatement();

		List<OperatorInstance> opInstances = new ArrayList<>();

		OperatorInstance opInstace = new SingleUnWrapIfOperator(modificationPoint, ifToRemove, stmtinsideThen, this);
		opInstances.add(opInstace);

		return opInstances;
	}

	@Override
	public boolean needIngredient() {
		return false;
	}

	int positionRemoved = -1;

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return target instanceof CtIf;
	}
}
