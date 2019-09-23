package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class AddAssignmentOp extends InsertBeforeOp implements IOperatorWithTargetElement {
	private CtElement targetElement = null;

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> opInstances = new ArrayList<>();

		CtStatement statementPointed = (targetElement == null) ? (CtStatement) modificationPoint.getCodeElement()
				: (CtStatement) targetElement;

		List<CtVariable> varsInScope = modificationPoint.getContextOfModificationPoint().stream()
				.filter(e -> e.getType().isPrimitive()).collect(Collectors.toList());

		for (CtVariable aVarInScope : varsInScope) {

			CtAssignment assigment = MutationSupporter.getFactory().Code().createVariableAssignment(
					aVarInScope.getReference(), aVarInScope.isStatic(),
					// TO replace
					MutationSupporter.getFactory().createCodeSnippetExpression("0"));

			OperatorInstance opI = new OperatorInstance(modificationPoint, this, statementPointed, assigment);
			opInstances.add(opI);

		}

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
