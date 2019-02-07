package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

/**
 * Methor wrong reference Case 1: Argument removement. Case 2: Different method
 * name. Case 3: Different number arguments.
 * 
 * @author Matias Martinez
 *
 */
public class MethodXMethodReplacementDiffNameOp extends MethodXMethodReplacementOp {

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		Ingredient ingredient = operatorInstance.getAllIngredients().get(metaIdentifier);

		ModificationPoint modificationPoint = operatorInstance.getModificationPoint();

		CtExpression expressionSource = (CtExpression) ingredient.getDerivedFrom();
		CtExpression expressionTarget = (CtExpression) ingredient.getCode();

		MutationSupporter.clearPosition(expressionTarget);

		List<OperatorInstance> opsOfVariant = new ArrayList();

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, expressionSource,
				expressionTarget);
		opsOfVariant.add(opInstace);

		return opInstace;
	}

	/**
	 * For case 2
	 * 
	 * @param suspiciousElement
	 * @param context
	 * @return
	 */
	@Override
	public MapList<CtInvocation, Ingredient> retrieveInvocationIngredient(CtElement suspiciousElement) {

		CtClass classUnderAnalysis = suspiciousElement.getParent(CtClass.class);

		MapList<CtInvocation, Ingredient> similarInvocationResult = new MapList<>();

		List<CtInvocation> invocations = suspiciousElement.getElements(e -> (e instanceof CtInvocation));
		for (CtInvocation invocationToReplace : invocations) {

			CtExecutable minvokedInAffected = invocationToReplace.getExecutable().getDeclaration();

			if (minvokedInAffected == null || !(minvokedInAffected instanceof CtMethod))
				continue;

			CtMethod affectedMethod = (CtMethod) minvokedInAffected;

			CtType typeOfTarget = invocationToReplace.getTarget().getType().getTypeDeclaration();

			if (!(typeOfTarget instanceof CtClass))
				continue;

			CtClass targetOfInvocation = (CtClass) typeOfTarget;

			List allMethods = SupportOperators.getAllMethodsFromClass(targetOfInvocation);

			for (Object omethod : allMethods) {

				if (!(omethod instanceof CtMethod))
					continue;

				CtMethod anotherMethod = (CtMethod) omethod;

				if (anotherMethod.getSignature().equals(affectedMethod.getSignature()))
					// It's the same, we discard it.
					continue;

				if (anotherMethod.getSimpleName().startsWith(VarReplacementByMethodCallOp.META_METHOD_LABEL))
					// It's a meta-method, discard
					continue;

				// Only if the target is the class we can call to non public methods
				if (!targetOfInvocation.equals(classUnderAnalysis) && !anotherMethod.isPublic())
					continue;

				if (anotherMethod.getSimpleName().equals(affectedMethod.getSimpleName())) {
					// It's override
					// TODO:
					// similarInvocationResult.add(affectedMethod, anotherMethod);

				}

				if (anotherMethod.getType() != null && minvokedInAffected.getType() != null) {

					boolean compatibleReturnTypes = SupportOperators.checkIsSubtype(anotherMethod.getType(),
							minvokedInAffected.getType());
					if (compatibleReturnTypes) {

						// CASE 2: Different method name
						if (anotherMethod.getParameters().size() == affectedMethod.getParameters().size()
								&& anotherMethod.getParameters().equals(affectedMethod.getParameters())) {

							CtInvocation newInvocation = invocationToReplace.clone();
							// newInvocation.setLabel(anotherMethod.getSimpleName());
							newInvocation.setExecutable(anotherMethod.getReference());
							Ingredient newIngredient = new Ingredient(newInvocation);
							newIngredient.setDerivedFrom(invocationToReplace);

							similarInvocationResult.add(invocationToReplace, newIngredient);
						}
					}

				}

			}

		}
		return similarInvocationResult;

	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		// See that the modification points are statements
		return (point.getCodeElement() instanceof CtStatement);
	}
}
