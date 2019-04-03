package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtTry;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UnwrapfromTryOp extends ReplaceOp implements IOperatorWithTargetElement {

	private CtElement targetElement = null;

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return point.getCodeElement() instanceof CtTry;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		log.debug("Unwrap Try:");

		CtTry trytoremove = (targetElement == null) ? (CtTry) modificationPoint.getCodeElement()
				: (CtTry) targetElement;

		CtBlock blockOfTry = trytoremove.getBody().clone();
		MutationSupporter.clearPosition(blockOfTry);

		List<OperatorInstance> opInstances = new ArrayList<>();

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, trytoremove, blockOfTry);
		opInstances.add(opInstace);

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

		return target instanceof CtTry;
	}
}
