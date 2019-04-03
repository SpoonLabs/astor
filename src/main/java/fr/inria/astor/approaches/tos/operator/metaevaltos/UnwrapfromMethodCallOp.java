package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UnwrapfromMethodCallOp extends FineGrainedExpressionReplaceOperator implements IOperatorWithTargetElement {
	private CtElement targetElement = null;

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> opInstances = new ArrayList<>();

		CtElement elementToVisit = (this.targetElement == null) ? modificationPoint.getCodeElement()
				: this.targetElement;
		MapList<CtInvocation, Ingredient> ingredients = retrieveMethodHasCompatibleParameterAndReturnSameMethod(
				elementToVisit);

		for (CtInvocation invocationToReplace : ingredients.keySet()) {

			List<Ingredient> ingredientsOfInvocation = ingredients.get(invocationToReplace);

			for (Ingredient ingredient : ingredientsOfInvocation) {

				CtExpression expressionSource = (CtExpression) invocationToReplace;
				CtExpression expressionTarget = (CtExpression) ingredient.getCode();

				MutationSupporter.clearPosition(expressionTarget);

				OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, expressionSource,
						expressionTarget);
				opInstances.add(opInstace);

			}

		}

		return opInstances;
	}

	@Override
	public boolean needIngredient() {
		return false;
	}

	private MapList<CtInvocation, Ingredient> retrieveMethodHasCompatibleParameterAndReturnSameMethod(
			CtElement suspiciousElement) {

		MapList<CtInvocation, Ingredient> result = new MapList<CtInvocation, Ingredient>();
		List<CtInvocation> invocations = suspiciousElement.getElements(e -> (e instanceof CtInvocation)).stream()
				.map(CtInvocation.class::cast).collect(Collectors.toList());

		for (CtInvocation invocation : invocations) {

			for (Object oparameter : invocation.getArguments()) {
				CtExpression argument = (CtExpression) oparameter;

				if (SupportOperators.checkIsSubtype(argument.getType(), invocation.getType())) {

					CtExpression clonedExpressionArgument = argument.clone();
					MutationSupporter.clearPosition(clonedExpressionArgument);
					Ingredient newIngredient = new Ingredient(clonedExpressionArgument);
					result.add(invocation, newIngredient);

				}

			}
		}
		return result;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		// See that the modification points are statements
		return (point.getCodeElement() instanceof CtStatement);
	}

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	@Override
	public boolean checkTargetCompatibility(CtElement e) {

		return (e instanceof CtInvocation);
	}
}
