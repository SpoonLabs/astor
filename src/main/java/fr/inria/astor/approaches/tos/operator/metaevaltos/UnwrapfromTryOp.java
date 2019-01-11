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

/**
 * 
 * @author Matias Martinez
 *
 */
public class UnwrapfromTryOp extends ReplaceOp {
	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return point.getCodeElement() instanceof CtTry;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {

		log.debug("Unwrap Try:");

		CtTry trytoremove = (CtTry) modificationPoint.getCodeElement();

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

}
