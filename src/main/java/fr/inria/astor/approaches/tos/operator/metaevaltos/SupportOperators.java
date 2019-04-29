package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
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
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
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

		boolean isParentMethodStatic = isParentMethodStatic(point.getCodeElement());

		List allMethods = SupportOperators.getAllMethodsFromClass(classUnderAnalysis);

		CtThisAccess<Object> createThisAccess = MutationSupporter.getFactory()
				.createThisAccess(MutationSupporter.getFactory().Type().objectType(), true);

		for (Object omethod : allMethods) {

			if (!(omethod instanceof CtMethod))
				continue;

			CtMethod anotherMethod = (CtMethod) omethod;

			if (isParentMethodStatic && //
					!anotherMethod.getModifiers().contains(ModifierKind.STATIC)) {
				// if the modification point is in a static method, the method to call must be
				// static
				continue;
			}

			if (anotherMethod.getSimpleName().startsWith(VarReplacementByMethodCallOp.META_METHOD_LABEL))
				// It's a meta-method, discard
				continue;

			boolean compatibleReturnTypes = SupportOperators.checkIsSubtype(anotherMethod.getType(),
					variableToReplaceType);

			if (compatibleReturnTypes) {

				List<CtInvocation> newInvToMethods = createRealInvocationsAllPosibilities(point, anotherMethod,
						createThisAccess);
				newInvocations.addAll(newInvToMethods);
			}
		}

		return newInvocations;
	}

	public static boolean isParentMethodStatic(CtElement codeElement) {

		CtMethod parentMethod = codeElement.getParent(CtMethod.class);
		if (parentMethod != null) {

			return parentMethod.getModifiers().contains(ModifierKind.STATIC);
		}

		return false;
	}

	public static List<CtInvocation> retrieveInvocationsFromVar(CtTypeReference variableToReplaceType,
			CtClass classUnderAnalysis, ModificationPoint point) {
		List<CtInvocation> newInvocations = new ArrayList<>();

		List<CtVariable> variablesInScope = point.getContextOfModificationPoint();

		boolean isParentMethodStatic = isParentMethodStatic(point.getCodeElement());

		for (CtVariable varInScope : variablesInScope) {

			if (varInScope.getType() == null || varInScope.getType().isPrimitive()) {
				continue;
			}

			//

			if (isParentMethodStatic && //
					!varInScope.getModifiers().contains(ModifierKind.STATIC)) {
				// if the modification point is in a static method, the variable to target must
				// be
				// static
				continue;
			}
			//
			List<CtMethod> allMethods = varInScope.getType().getAllExecutables().stream()
					.filter(e -> e.getExecutableDeclaration() instanceof CtMethod)
					.map(CtExecutableReference::getExecutableDeclaration).map(CtMethod.class::cast).collect(Collectors.toList());

			for (CtMethod anotherMethod : allMethods) {

				if (anotherMethod.getSimpleName().startsWith(VarReplacementByMethodCallOp.META_METHOD_LABEL))
					// It's a meta-method, discard
					continue;

				if (!anotherMethod.isPublic())
					continue;

				boolean compatibleReturnTypes = SupportOperators.checkIsSubtype(anotherMethod.getType(),
						variableToReplaceType);

				if (compatibleReturnTypes) {

					List<CtInvocation> newInvToMethods = createRealInvocationsAllPosibilities(point, anotherMethod,
							MutationSupporter.getFactory().createVariableRead(varInScope.getReference(),
									varInScope.isStatic()));
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

	public static List<CtInvocation> createRealInvocationsAllPosibilities(ModificationPoint point,
			CtMethod anotherMethod, CtExpression target) {

		// All the possibles variables
		List<List<CtExpression<?>>> possibleArguments = computeParameters(anotherMethod, point);
		if (possibleArguments == null || possibleArguments.isEmpty())
			return Collections.emptyList();

		List<CtInvocation> newInvocations = realInvocationsFromCombination(anotherMethod, target, possibleArguments);
		return newInvocations;
	}

	public static List<CtInvocation> realInvocationsFromCombination(CtMethod anotherMethod, CtExpression target,
			List<List<CtExpression<?>>> possibleArguments) {
		List<CtInvocation> newInvocations = new ArrayList<>();

		for (List<CtExpression<?>> arguments : possibleArguments) {
			CtInvocation newInvocation = MutationSupporter.getFactory().createInvocation(target,
					anotherMethod.getReference(), arguments);
			newInvocation.setExecutable(anotherMethod.getReference());
			newInvocation.setArguments(arguments);
			newInvocation.setTarget(target);

			newInvocations.add(newInvocation);

		}
		return newInvocations;
	}

	public static List<CtInvocation> createRealInvocationsReusingVars(ModificationPoint point, CtMethod anotherMethod,
			CtInvocation invocationToReplace) {

		List<CtParameter> parameterTypes = anotherMethod.getParameters();
		CtExpression target = invocationToReplace.getTarget();
		MapList<CtTypeReference, CtElement> candidateMappings = new MapList<>();

		List<CtVariable> variablesInScope = point.getContextOfModificationPoint();

		for (CtParameter aParameterAnotherMethod : parameterTypes) {

			CtTypeReference aTypePar = aParameterAnotherMethod.getType();

			List similarExpression = (List<CtExpression>) invocationToReplace.getArguments().stream()
					// .filter(e -> e.equals(aTypePar)).collect(Collectors.toList());
					.filter(e -> ((CtExpression) e).getType().equals(aTypePar)).collect(Collectors.toList());
			// TODO: put a configuration point here.
			if (similarExpression.size() > 0) {

				if (!candidateMappings.containsKey(aTypePar)) {

					candidateMappings.put(aTypePar, similarExpression);
				}

			} else {

				if (!candidateMappings.containsKey(aTypePar)) {
					List compatible = variablesInScope.stream().filter(e -> e.getType().isSubtypeOf(aTypePar))
							.collect(Collectors.toList());
					// A par without a var
					if (compatible.isEmpty())
						return null;

					candidateMappings.put(aTypePar, compatible);
				}
			}

		}

		List<List<CtExpression<?>>> candidateArguments = createAllParametersCombinations(parameterTypes,
				candidateMappings);

		if (candidateArguments == null || candidateArguments.isEmpty())
			return Collections.emptyList();

		List<CtInvocation> newInvocations = realInvocationsFromCombination(anotherMethod, target, candidateArguments);

		return newInvocations;
	}

	public static List<List<CtExpression<?>>> computeParameters(CtMethod anotherMethod, ModificationPoint point) {

		List<CtVariable> variablesInScope = point.getContextOfModificationPoint();

		List<CtParameter> parameterTypes = anotherMethod.getParameters();

		MapList<CtTypeReference, CtElement> candidateMappings = new MapList<>();
		// Groups vars according to types
		for (CtParameter ctTypeParameter : parameterTypes) {
			CtTypeReference parType = ctTypeParameter.getType();

			if (!candidateMappings.containsKey(parType)) {
				List compatible = variablesInScope.stream().filter(e -> e.getType().isSubtypeOf(parType))
						.collect(Collectors.toList());
				// A par without a var
				if (compatible.isEmpty())
					return null;

				candidateMappings.put(parType, compatible);

			}

		}
		List<List<CtExpression<?>>> candidateArguments = createAllParametersCombinations(parameterTypes,
				candidateMappings);

		return candidateArguments;
	}

	public static List<List<CtExpression<?>>> createAllParametersCombinations(List<CtParameter> parameterType,
			MapList<CtTypeReference, CtElement> candidateMappings) {
		List<List<CtExpression<?>>> candidateArguments = new ArrayList();

		long maxCombinations = getMaxCombinations(parameterType, candidateMappings);
		// number of arguments
		for (int i = 0; i < maxCombinations; i++) {
			List<CtExpression<?>> callArguments = new ArrayList();
			for (CtParameter ctTypeParameter : parameterType) {

				List<CtElement> compVar = candidateMappings.get(ctTypeParameter.getType());
				CtElement elSelected = compVar.get(RandomManager.nextInt(compVar.size()));
				if (elSelected instanceof CtVariable) {
					CtVariable varSelected = (CtVariable) elSelected;
					callArguments.add(MutationSupporter.getFactory().createVariableRead(varSelected.getReference(),
							varSelected.isStatic()));
				} else {
					if (elSelected instanceof CtExpression) {
						// for the moment, we dont clone
						callArguments.add((CtExpression<?>) elSelected);
					}
				}

			}
			// check if the arguments are not already considered
			if (!candidateArguments.contains(callArguments)) {
				candidateArguments.add(callArguments);
			}
		}
		return candidateArguments;
	}

	private static long getMaxCombinations(List<CtParameter> parameterType, MapList<CtTypeReference, CtElement> types) {

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

	public static void putVarsNotDuplicated(CtElement elementToAnalyze, List<CtVariableAccess> varsToBeParameters) {
		List<CtVariableAccess> varsFromExpression = elementToAnalyze.getElements(e -> e instanceof CtVariableAccess);
		for (CtVariableAccess ctVariableAccess : varsFromExpression) {
			if (!varsToBeParameters.contains(ctVariableAccess))
				varsToBeParameters.add(ctVariableAccess);

		}
	}

	// https://stackoverflow.com/questions/8173862/map-of-sets-into-list-of-all-combinations

	public static <K, V> List<Map<K, V>> combinations(Map<K, Set<V>> map) {
		List<Map<K, V>> list = new ArrayList<>();
		recurse(map, new LinkedList<>(map.keySet()).listIterator(), new HashMap<>(), list);
		return list;
	}

	private static <K, V> void recurse(Map<K, Set<V>> map, ListIterator<K> iter, Map<K, V> cur, List<Map<K, V>> list) {

		if (!iter.hasNext()) {
			Map<K, V> entry = new HashMap<>();

			for (K key : cur.keySet()) {
				entry.put(key, cur.get(key));
			}

			list.add(entry);
		} else {
			K key = iter.next();
			Set<V> set = map.get(key);

			for (V value : set) {
				cur.put(key, value);
				recurse(map, iter, cur, list);
				cur.remove(key);
			}

			iter.previous();
		}
	}

}
