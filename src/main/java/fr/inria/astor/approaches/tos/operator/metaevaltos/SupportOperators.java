package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.util.MapList;
import fr.inria.lille.repair.expression.access.VariableImpl;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SupportOperators {

	/**
	 * Add all variables from the expression and candidates in a list
	 * 
	 * @param exptochange
	 * @param candidates
	 * @param modificationPoint
	 * @return
	 */
	public static List<CtVariableAccess> collectAllVarsFromDynaIngredients(List<IngredientFromDyna> candidates,
			ModificationPoint modificationPoint) {
		// First collect variables from dynamoth
		List<VariableImpl> dynaVars = new ArrayList<>();
		List<CtVariableAccess> varAccessList = new ArrayList();

		for (IngredientFromDyna candidateIngr : candidates) {
			dynaVars.addAll(candidateIngr.getVariable());
		}

		if (dynaVars.isEmpty()) {
			return varAccessList;
		}
		// Second, for each var from dynamoth, find the CtVariable access (by parsing
		// the context of the modification point)
		List<CtVariable> varAccessCandidate = modificationPoint.getContextOfModificationPoint();

		for (VariableImpl aDynaVariable : dynaVars) {

			for (CtVariable aVariableSinScope : varAccessCandidate) {

				if (aVariableSinScope.getSimpleName().equals(aDynaVariable.getVariableName())) {

					CtVariableAccess aVariableRead = MutationSupporter.getFactory()
							.createVariableRead(aVariableSinScope.getReference(), false);
					if (!varAccessList.contains(aVariableRead))
						varAccessList.add(aVariableRead);
				}
			}

		}

		return varAccessList;
	}

	/**
	 * Retrieves all variables from the target element and all ingredients
	 * 
	 * @param elementtochange
	 * @param candidates
	 * @return
	 */
	public static List<CtVariableAccess> collectAllVars(CtElement elementtochange, List<Ingredient> candidates) {
		List<CtVariableAccess> varAccess = VariableResolver.collectVariableAccess(elementtochange);

		for (Ingredient candidateIngr : candidates) {
			CtElement candidate = candidateIngr.getCode();
			List<CtVariableAccess> varAccessCandidate = VariableResolver.collectVariableAccess(candidate);
			for (CtVariableAccess varX : varAccessCandidate) {
				if (!varAccess.contains(varX)) {
					varAccess.add(varX);
				}
			}
		}

		return varAccess;
	}

	public static boolean compareTypes(CtTypeReference t1, CtTypeReference t2) {
		try {
			return t1 != null && t2 != null && (t1.toString().equals(t2.toString()) || t1.equals(t2)
					|| t1.isSubtypeOf(t2) || t2.isSubtypeOf(t1));
		} catch (Exception e) {
			System.out.println("Error comparing types");
			e.printStackTrace();
			return false;
		}
	}

	public static boolean checkIsSubtype(CtTypeReference typeCurrent, CtTypeReference typeToBeReturned) {
		try {
			return typeCurrent != null && typeToBeReturned != null
					&& (typeCurrent.toString().equals(typeToBeReturned.toString())
							|| typeCurrent.equals(typeToBeReturned) || typeCurrent.isSubtypeOf(typeToBeReturned));
		} catch (Exception e) {
			System.out.println("Error comparing types");
			e.printStackTrace();
			return false;
		}
	}

	public static List getAllMethodsFromClass(CtClass parentClass) {
		List allMethods = new ArrayList(parentClass.getAllMethods());

		if (parentClass != null && parentClass.getParent() instanceof CtClass) {
			CtClass parentParentClass = (CtClass) parentClass.getParent();
			allMethods.addAll(parentParentClass.getAllMethods());

		}
		return allMethods;
	}

	public static List<CtInvocation> retrieveInvocationsFromMethod(CtTypeReference variableToReplaceType,
			CtClass classUnderAnalysis, ModificationPoint point) {
		List<CtInvocation> newInvocations = new ArrayList<>();

		// CtClass classUnderAnalysis = point.getCodeElement().getParent(CtClass.class);

		List allMethods = SupportOperators.getAllMethodsFromClass(classUnderAnalysis);

		CtThisAccess<Object> createThisAccess = MutationSupporter.getFactory()
				.createThisAccess(MutationSupporter.getFactory().Type().objectType(), true);
		for (Object omethod : allMethods) {

			if (!(omethod instanceof CtMethod))
				continue;

			CtMethod anotherMethod = (CtMethod) omethod;

			if (anotherMethod.getSimpleName().startsWith(VarReplacementByMethodCallOp.META_METHOD_LABEL))
				// It's a meta-method, discard
				continue;

			boolean compatibleReturnTypes = SupportOperators.checkIsSubtype(anotherMethod.getType(),
					variableToReplaceType);

			if (compatibleReturnTypes) {

				List<CtInvocation> newInvToMethods = createRealInvocations(point, anotherMethod, createThisAccess);
				newInvocations.addAll(newInvToMethods);
			}
		}

		return newInvocations;
	}

	public static List<CtInvocation> retrieveInvocationsFromVar(CtTypeReference variableToReplaceType,
			CtClass classUnderAnalysis, ModificationPoint point) {
		List<CtInvocation> newInvocations = new ArrayList<>();

		List<CtVariable> variablesInScope = point.getContextOfModificationPoint();

		for (CtVariable varInScope : variablesInScope) {

			if (varInScope.getType() == null || varInScope.getType().isPrimitive()) {
				continue;
			}
			List<CtMethod> allMethods = varInScope.getType().getAllExecutables().stream()
					.filter(e -> e.getExecutableDeclaration() instanceof CtMethod)
					.map(e -> e.getExecutableDeclaration()).map(CtMethod.class::cast).collect(Collectors.toList());

			for (CtMethod anotherMethod : allMethods) {

				if (anotherMethod.getSimpleName().startsWith(VarReplacementByMethodCallOp.META_METHOD_LABEL))
					// It's a meta-method, discard
					continue;

				if (!anotherMethod.isPublic())
					continue;

				boolean compatibleReturnTypes = SupportOperators.checkIsSubtype(anotherMethod.getType(),
						variableToReplaceType);

				if (compatibleReturnTypes) {

					List<CtInvocation> newInvToMethods = createRealInvocations(point, anotherMethod, MutationSupporter
							.getFactory().createVariableRead(varInScope.getReference(), varInScope.isStatic()));
					newInvocations.addAll(newInvToMethods);

				}
			}
		}
		return newInvocations;
	}

	public static boolean isBooleanType(CtExpression e) {
		if (e.getType() == null || e.getType().unbox() == null)
			return false;

		return e.getType().unbox().getSimpleName().equals("boolean");
	}

	public static List<CtInvocation> createRealInvocations(ModificationPoint point, CtMethod anotherMethod,
			CtExpression target) {
		List<CtInvocation> newInvocations = new ArrayList<>();
		// All the possibles variables
		List<List<CtExpression<?>>> possibleArguments = computeParameters(anotherMethod, point);
		if (possibleArguments == null || possibleArguments.isEmpty())
			return newInvocations;

		for (List<CtExpression<?>> arguments : possibleArguments) {
			CtInvocation newInvocation = MutationSupporter.getFactory().createInvocation(target,
					anotherMethod.getReference(), arguments);
			// newInvocation.setLabel(anotherMethod.getSimpleName());
			newInvocation.setExecutable(anotherMethod.getReference());
			newInvocation.setArguments(arguments);
			newInvocation.setTarget(target);

			newInvocations.add(newInvocation);

			// newInvocation.setT
		}
		return newInvocations;
	}

	public static List<List<CtExpression<?>>> computeParameters(CtMethod anotherMethod, ModificationPoint point) {

		List<List<CtExpression<?>>> candidateArguments = new ArrayList();

		List<CtVariable> variablesInScope = point.getContextOfModificationPoint();

		List<CtParameter> parameterType = anotherMethod.getParameters();

		MapList<CtTypeReference, CtVariable> types = new MapList<>();
		// Groups vars according to types
		for (CtParameter ctTypeParameter : parameterType) {
			CtTypeReference parType = ctTypeParameter.getType();

			if (!types.containsKey(parType)) {
				List compatible = variablesInScope.stream().filter(e -> e.getType().isSubtypeOf(parType))
						.collect(Collectors.toList());
				// A par without a var
				if (compatible.isEmpty())
					return null;

				types.put(parType, compatible);

			}

		}
		long maxCombinations = getMaxCombinations(parameterType, types);
		// number of arguments
		for (int i = 0; i < maxCombinations; i++) {
			List<CtExpression<?>> callArguments = new ArrayList();
			for (CtParameter ctTypeParameter : parameterType) {

				List<CtVariable> compVar = types.get(ctTypeParameter.getType());
				CtVariable varSelected = compVar.get(RandomManager.nextInt(compVar.size()));
				callArguments.add(MutationSupporter.getFactory().createVariableRead(varSelected.getReference(),
						varSelected.isStatic()));

			}
			// check if the arguments are not already considered
			if (!candidateArguments.contains(callArguments)) {
				candidateArguments.add(callArguments);
			}
		}

		// let's create realParameters

		return candidateArguments;
	}

	private static long getMaxCombinations(List<CtParameter> parameterType,
			MapList<CtTypeReference, CtVariable> types) {

		long max = 1;
		for (CtParameter ctTypeParameter : parameterType) {

			max *= types.get(ctTypeParameter.getType()).size();

		}
		int maxComb = ConfigurationProperties.getPropertyInt("maxVarCombination");
		if (max > maxComb || max < 0) {
			return (int) maxComb;
		}

		return max;
	}

	public static boolean checkOcurrenceOfOtherParameters(CtMethod anotherMethod, CtMethod affectedMethod) {
		// anotherMethod.getParameters().stream().map(CtParameter.class::cast)
		// .collect(Collectors.toList()

		for (Object parameterFromAnotherM : anotherMethod.getParameters()) {
			CtParameter parAnother = (CtParameter) parameterFromAnotherM;

			// The parameter does not exist in the previous version
			if (!affectedMethod.getParameters().contains(parAnother)) {
				return false;
			}
		}
		// all parameters exist
		return true;
	}

	public static List<CtExpression> checkOcurrenceOfOtherParameters(CtMethod anotherMethod, CtMethod affectedMethod,
			List realParameters) {

		List newRealParameters = new ArrayList();

		for (Object parameterFromAnotherM : anotherMethod.getParameters()) {
			CtParameter parAnother = (CtParameter) parameterFromAnotherM;

			int indexOfParameter = affectedMethod.getParameters().indexOf(parAnother);
			// The parameter does not exist in the previous version
			if (indexOfParameter < 0) {
				return null;
			} else {
				CtExpression parameterAtI = (CtExpression) realParameters.get(indexOfParameter);
				newRealParameters.add(parameterAtI);
			}

		}
		// all parameters exist
		return newRealParameters;
	}
}
