package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

/**
 * 
 * Case 3: Different number arguments.
 * 
 * @author Matias Martinez
 *
 */
public class MethodXMethodReplacementDiffArgumentsOp extends MethodXMethodReplacementOp {

	/**
	 *
	 * 
	 * @param suspiciousElement
	 * @param context
	 * @return
	 */
	@Override
	public MapList<CtInvocation, Ingredient> retrieveInvocationIngredient(ModificationPoint point) {
		CtElement suspiciousElement = point.getCodeElement();

		CtClass classUnderAnalysis = suspiciousElement.getParent(CtClass.class);

		MapList<CtInvocation, Ingredient> similarInvocationResult = new MapList<>();

		List<CtInvocation> invocations = getInvocations(suspiciousElement);
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

				if (anotherMethod.getSimpleName().startsWith(VarReplacementByMethodCallOp.META_METHOD_LABEL))
					// It's a meta-method, discard
					continue;

				if (anotherMethod.getSignature().equals(affectedMethod.getSignature()))
					// It's the same, we discard it.
					continue;

				// Only if the target is the class we can call to non public methods
				if (!targetOfInvocation.equals(classUnderAnalysis) && !anotherMethod.isPublic())
					continue;

				// The name must be the same
				if (anotherMethod.getSimpleName().equals(affectedMethod.getSimpleName())) {

					if (anotherMethod.getType() != null && minvokedInAffected.getType() != null) {

						boolean compatibleReturnTypes = SupportOperators.checkIsSubtype(anotherMethod.getType(),
								minvokedInAffected.getType());
						// must return the same object
						if (compatibleReturnTypes) {

							// Case 3: Different number argument
							if (anotherMethod.getParameters().size() != affectedMethod.getParameters().size()
									// check the types
									|| (!anotherMethod.getParameters().isEmpty()
											&& !affectedMethod.getParameters().isEmpty()
											&& !anotherMethod.getParameters().equals(affectedMethod.getParameters()))) {

								List<CtInvocation> newInvToMethods = SupportOperators.createRealInvocations(point,
										anotherMethod, invocationToReplace.getTarget());

								for (CtInvocation ctInvocation : newInvToMethods) {
									CtInvocation newInvocation = ctInvocation.clone();
									newInvocation.setExecutable(anotherMethod.getReference());
									Ingredient newIngredient = new Ingredient(newInvocation);
									newIngredient.setDerivedFrom(invocationToReplace);
									similarInvocationResult.add(invocationToReplace, newIngredient);

								}

							}
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

	@Override
	public String identifier() {
		// TODO Auto-generated method stub
		return "Method_RW_Method_DARG";
	}
}
