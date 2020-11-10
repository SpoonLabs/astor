package fr.inria.astor.core.manipulation.sourcecode;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.NGramManager;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.PotentialVariableDeclarationFunction;

/**
 * Variable manipulations: methods to analyze variables and scope
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
@SuppressWarnings("rawtypes")
public class VariableResolver {

	public static ClusteringParser cluster = new ClusteringParser();

	private static Logger logger = Logger.getLogger(VariableResolver.class.getName());

	/**
	 * Return a list of variables that match with the variable access passed as
	 * parameter. The last argument indicate if we map also the vars name
	 * 
	 * @param varContext
	 * @param vartofind
	 * @param mapName
	 * @return
	 */
	protected static List<CtVariable> matchVariable(List<CtVariable> varContext, CtVariableAccess vartofind,
			boolean mapName) {
		List<CtVariable> varMatched = new ArrayList<>();
		try {
			CtTypeReference typeToFind = vartofind.getType();

			// First we search for compatible variables according to the type
			List<CtVariable> types = compatiblesSubType(varContext, typeToFind);
			if (types.isEmpty()) {
				return varMatched;
			}
			// Then, we search
			for (CtVariable ctVariableWithTypes : types) {
				// comparing name is optional, according to argument.
				boolean matchName = !mapName
						|| ctVariableWithTypes.getSimpleName().equals(vartofind.getVariable().getSimpleName());
				if (matchName) {
					varMatched.add(ctVariableWithTypes);
				}
			}

		} catch (Exception ex) {
			logger.error("Variable verification error", ex);
		}

		return varMatched;
	}

	/**
	 * For a given VariableAccess, we search the list of Variables contains
	 * compatible types (i.e. sub types)
	 * 
	 * @param varContext
	 * @param vartofind
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static List<CtVariable> compatiblesSubType(List<CtVariable> varContext, CtTypeReference typeToFind) {

		List<CtVariable> result = new ArrayList<CtVariable>();

		for (CtVariable ctVariable_i : varContext) {

			CtTypeReference typeref_i = ctVariable_i.getType();
			try {
				if (typeref_i.isSubtypeOf(typeToFind)) {
					result.add(ctVariable_i);
				}
			} catch (Exception e) {
				result.add(ctVariable_i);
			}

		}
		return result;
	}

	/**
	 * Maps a variable access with a variable declaration.
	 * 
	 * @param varContext
	 * @param varacc
	 * @return
	 */
	public static Map<CtVariableAccess, List<CtVariable>> matchVars(List<CtVariable> varContext,
			List<CtVariableAccess> varacc, boolean mapName) {

		Map<CtVariableAccess, List<CtVariable>> mapping = new HashMap<>();

		for (CtVariableAccess ctVariableAccess : varacc) {
			List<CtVariable> matched = matchVariable(varContext, ctVariableAccess, mapName);
			mapping.put(ctVariableAccess, matched);
		}

		return mapping;
	}

	public static List<CtVariableAccess> collectVariableAccess(CtElement element) {
		return collectVariableAccess(element, false);
	}

	public static List<CtVariableAccess> collectVariableRead(CtElement element) {
		List<CtVariableAccess> varaccess = new ArrayList<>();
		List<String> varaccessCacheNames = new ArrayList<>();
		CtScanner sc = new CtScanner() {

			public void add(CtVariableAccess e) {
				if (!varaccessCacheNames.contains(e.getVariable().getSimpleName()))
					varaccess.add(e);
				varaccessCacheNames.add(e.getVariable().getSimpleName());
			}

			@Override
			public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
				super.visitCtVariableRead(variableRead);
				add(variableRead);
			}

			@Override
			public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
				super.visitCtTypeAccess(typeAccess);
				// varaccess.add(typeAccess);
			}

			@Override
			public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
				super.visitCtFieldRead(fieldRead);
				add(fieldRead);
			}

		};

		sc.scan(element);

		return varaccess;

	}

	public static List<CtVariableAccess> collectVariableReadIgnoringBlocks(CtElement element) {

		if (element instanceof CtIf) {
			return collectVariableRead(((CtIf) element).getCondition());
		}
		if (element instanceof CtWhile) {
			return collectVariableRead(((CtWhile) element).getLoopingExpression());
		}

		if (element instanceof CtFor) {
			return collectVariableRead(((CtFor) element).getExpression());
		}

		return collectVariableRead(element);

	}

	/**
	 * Return all variables related to the element passed as argument
	 * 
	 * @param element
	 * @return
	 */
	public static List<CtVariableAccess> collectVariableAccess(CtElement element, boolean duplicates) {
		List<CtVariableAccess> varaccess = new ArrayList<>();
		List<String> varaccessCacheNames = new ArrayList<>();
		CtScanner sc = new CtScanner() {

			public void add(CtVariableAccess e) {
				if (duplicates || !varaccessCacheNames.contains(e.getVariable().getSimpleName()))
					varaccess.add(e);
				varaccessCacheNames.add(e.getVariable().getSimpleName());
			}

			@Override
			public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
				super.visitCtVariableRead(variableRead);
				add(variableRead);
			}

			@Override
			public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
				super.visitCtVariableWrite(variableWrite);
				add(variableWrite);
			}

			@Override
			public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
				super.visitCtTypeAccess(typeAccess);
				// varaccess.add(typeAccess);
			}

			@Override
			public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
				super.visitCtFieldRead(fieldRead);
				add(fieldRead);
			}

			@Override
			public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
				super.visitCtFieldWrite(fieldWrite);
				add(fieldWrite);
			}

		};

		sc.scan(element);

		return varaccess;

	}

	public static List<CtVariableAccess> collectVariableAccessIgnoringBlocks(CtElement element) {

		if (element instanceof CtIf) {
			return collectVariableAccess(((CtIf) element).getCondition());
		}
		if (element instanceof CtWhile) {
			return collectVariableAccess(((CtWhile) element).getLoopingExpression());
		}

		if (element instanceof CtFor) {
			return collectVariableAccess(((CtFor) element).getExpression());
		}

		return collectVariableAccess(element);

	}

	public static List<CtLiteral> collectLiterals(CtElement element) {

		List<CtLiteral> literalsValues = new ArrayList<>();

		CtScanner scanner = new CtScanner() {

			@Override
			public <T> void visitCtLiteral(CtLiteral<T> literal) {

				super.visitCtLiteral(literal);
				if (!literalsValues.contains(literal))
					literalsValues.add(literal);
			}

		};

		scanner.scan(element);

		return literalsValues;
	}

	public static List<CtLiteral> collectLiteralsNoString(CtElement element) {

		List<CtLiteral> literalsValues = new ArrayList<>();

		CtScanner scanner = new CtScanner() {

			@Override
			public <T> void visitCtLiteral(CtLiteral<T> literal) {

				super.visitCtLiteral(literal);
				if (!literalsValues.contains(literal) && !"String".equals(literal.getType().getSimpleName()))
					literalsValues.add(literal);
			}

		};

		scanner.scan(element);

		return literalsValues;
	}

	/**
	 * 
	 * This methods determines whether all the variable access contained in a
	 * CtElement passes as parameter match with a variable from a set of variables
	 * given as argument. Both variable Types and Names are compared,
	 * 
	 * @param varContext List of variables to match
	 * @param element    element to extract the var access to match
	 * @return
	 */
	public static boolean fitInPlace(List<CtVariable> varContext, CtElement element) {
		return fitInContext(varContext, element, true);
	}

	/**
	 * This methods determines whether all the variable access contained in a
	 * CtElement passes as parameter match with a variable from a set of variables
	 * given as argument. The argument <code>matchName </code> indicates whether
	 * Type and Names are compared (value true), only type (false).
	 * 
	 * @param varContext          List of variables to match
	 * @param ingredientCtElement element to extract the var access to match
	 * @return
	 */
	public static boolean fitInContext(List<CtVariable> varContext, CtElement ingredientCtElement, boolean matchName) {

		Map<CtVariableAccess, List<CtVariable>> matched = getMapping(varContext, ingredientCtElement, matchName);
		if (matched == null)
			return false;

		return checkMapping(matched).isEmpty();

	}

	public static List<CtVariableAccess> checkMapping(Map<CtVariableAccess, List<CtVariable>> matched) {
		List<CtVariableAccess> notMapped = new ArrayList<>();

		if (matched == null)
			return notMapped;

		// Now, we analyze if all access were matched
		for (CtVariableAccess ctVariableAccess : matched.keySet()) {
			List<CtVariable> mapped = matched.get(ctVariableAccess);
			if (mapped.isEmpty()) {
				// One var access was not mapped
				// return false;
				notMapped.add(ctVariableAccess);
			}
		}
		// All VarAccess were mapped
		// return true;
		return notMapped;
	}

	public static Map<CtVariableAccess, List<CtVariable>> getMapping(List<CtVariable> varContext,
			CtElement ingredientCtElement, boolean matchName) throws IllegalAccessError {
		// We collect all var access from the ingredient
		List<CtVariableAccess> varAccessCollected = collectVariableAccess(ingredientCtElement);

		// Here we retrieve the induction variables, then match ONLY the name.
		List<CtVariableAccess> varInductionCollected = collectInductionVariableAccess(ingredientCtElement,
				varAccessCollected);
		// Remove all induction variables, we dont need them to the variable
		// match
		boolean removedInduction = varAccessCollected.removeAll(varInductionCollected);

		if (varInductionCollected.size() > 0 && !removedInduction)
			throw new IllegalAccessError("Var induction not removed");

		// Now, we check there is not name conflict with the induction variable.
		boolean nameConflict = nameConflict(varContext, varInductionCollected);
		if (nameConflict) {
			logger.debug("Name Conflict " + varAccessCollected);
			return null;
		}

		// Now, we search for access to public variable
		List<CtVariableAccess> varStaticAccessCollected = collectStaticVariableAccess(ingredientCtElement,
				varAccessCollected);
		// We discard those variables, we dont need to match it
		boolean removedStaticAccess = varAccessCollected.removeAll(varStaticAccessCollected);

		if (varStaticAccessCollected.size() > 0 && !removedStaticAccess)
			throw new IllegalAccessError("Var static access not removed");

		// Now, we match the remain var access.
		Map<CtVariableAccess, List<CtVariable>> matched = matchVars(varContext, varAccessCollected, matchName);
		return matched;
	}

	/**
	 * 
	 */
	public static VarMapping mapVariablesUsingCluster(List<CtVariable> varContext, CtElement ingredientCtElement) {

		// var out-of scope, list of variables compatibles
		Map<VarAccessWrapper, List<CtVariable>> varsMaps = new HashMap<>();
		List<CtVariableAccess> varsNotMapped = new ArrayList<>();

		Map<String, List<String>> clusters = cluster
				.readClusterFile(Paths.get(ConfigurationProperties.getProperty("learningdir") + File.separator
						+ ConfigurationProperties.getProperty("clusteringfilename")));

		List<CtVariableAccess> variablesOutOfScope = retriveVariablesOutOfContext(varContext, ingredientCtElement);
		logger.debug("#vars out of context: " + variablesOutOfScope.size());
		for (CtVariableAccess variableOutScope : variablesOutOfScope) {

			List<String> wcluster = clusters.get(variableOutScope.getVariable().getSimpleName());

			if (wcluster == null) {
				logger.debug("variable our of scope without context: " + variableOutScope);
				continue;
			}
			logger.debug("--var  out of context: " + variableOutScope + ", with wcluster size " + wcluster.size());

			boolean mapped = false;
			VarAccessWrapper varOutWrapper = new VarAccessWrapper(variableOutScope);
			for (String wordFromCluster : wcluster) {// In order

				List<CtVariable> varExist = existVariableWithName(varContext, wordFromCluster);
				// check compatibility between varExist and wout
				for (CtVariable varInScope : varExist) {
					boolean sameNames = variableOutScope.getVariable().getSimpleName()
							.equals(varInScope.getSimpleName());
					if (!sameNames) {
						boolean compatibleVariables = areVarsCompatible(variableOutScope, varInScope);
						if (compatibleVariables) {
							addVarMappingAsResult(varsMaps, varOutWrapper, varInScope);
							mapped = true;
						}
					}
				}

			}
			// if the var was not matched, we put in list of variables out of
			// scope not mapped.
			if (!mapped)
				varsNotMapped.add(variableOutScope);

		}
		VarMapping mappings = new VarMapping(varsMaps, varsNotMapped);
		return mappings;

	}

	public static VarMapping mapVariablesFromContext(List<CtVariable> varContext, CtElement ingredientCtElement) {

		List<CtVariableAccess> variablesOutOfScope = VariableResolver.retriveVariablesOutOfContext(varContext,
				ingredientCtElement);

		return mapVariablesFromContext(varContext, variablesOutOfScope);
	}

	public static VarMapping mapVariablesFromContext(List<CtVariable> varContext,
			List<CtVariableAccess> variablesOutOfScope) {

		// var out-of scope, list of variables compatibles
		Map<VarAccessWrapper, List<CtVariable>> varsMaps = new HashMap<>();

		List<CtVariableAccess> varsNotMapped = new ArrayList<>();
		logger.debug("#vars out of context: " + variablesOutOfScope.size());
		// For each var out of scopt
		for (CtVariableAccess variableOutScope : variablesOutOfScope) {

			boolean mapped = false;
			VarAccessWrapper varOutWrapper = new VarAccessWrapper(variableOutScope);
			// For each var in context
			for (CtVariable varInScope : varContext) {

				boolean sameNames = variableOutScope.getVariable().getSimpleName().equals(varInScope.getSimpleName());
				// if (!sameNames) {
				boolean compatibleVariables = areVarsCompatible(variableOutScope, varInScope);
				if (compatibleVariables) {
					addVarMappingAsResult(varsMaps, varOutWrapper, varInScope);
					mapped = true;
				}
				// }
			}
			// if the var was not matched, we put in list of variables out of
			// scope not mapped.
			if (!mapped)
				varsNotMapped.add(variableOutScope);

		}
		VarMapping mappings = new VarMapping(varsMaps, varsNotMapped);
		return mappings;

	}

	/**
	 * Return true if the variables are compatible
	 * 
	 * @param varOutScope
	 * @param varInScope
	 * @return
	 */
	public static boolean areVarsCompatible(CtVariableAccess varOutScope, CtVariable varInScope) {
		CtTypeReference refCluster = varInScope.getType();
		CtTypeReference refOut = varOutScope.getType();

		return areTypesCompatible(refCluster, refOut);
	}

	public static boolean areTypesCompatible(CtTypeReference type1, CtTypeReference type2) {
		try {// Check if an existing variable (name taken from
				// cluster)
				// is compatible with with that one out of scope

			boolean bothArray = false;
			boolean notCompatible = false;
			do {
				// We check if types are arrays.
				boolean clusterIsArray = type1 instanceof CtArrayTypeReference;
				boolean ourIsArray = type2 instanceof CtArrayTypeReference;

				if (clusterIsArray ^ ourIsArray) {
					notCompatible = true;

				}
				// if both are arrays, we extract the component
				// type, and we compare it again
				bothArray = clusterIsArray && ourIsArray;
				if (bothArray) {
					type1 = ((CtArrayTypeReference) type1).getComponentType();
					type2 = ((CtArrayTypeReference) type2).getComponentType();
				}

			} while (bothArray);

			if (notCompatible)
				return false;

			if (type1.isSubtypeOf(type2)) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		return false;
	}

	private static void addVarMappingAsResult(Map<VarAccessWrapper, List<CtVariable>> varMaps,
			VarAccessWrapper varOutWrapper, CtVariable varInContext) {
		List<CtVariable> vars = varMaps.get(varOutWrapper);
		if (vars == null) {
			vars = new ArrayList<>();
			varMaps.put(varOutWrapper, vars);
		}
		if (!vars.stream().filter(e -> e.getSimpleName().equals(varInContext.getSimpleName())).findAny().isPresent())
			vars.add(varInContext);
	}

	/**
	 * Returns the variables that have as name the string passed as argument.
	 * 
	 * @param varContext      variables
	 * @param wordFromCluster name of a variable
	 * @return
	 */
	public static List<CtVariable> existVariableWithName(List<CtVariable> varContext, String wordFromCluster) {
		List<CtVariable> founds = new ArrayList<>();
		for (CtVariable ctVariable : varContext) {
			if (ctVariable.getSimpleName().equals(wordFromCluster))
				founds.add(ctVariable);
		}
		return founds;
	}

	/**
	 * Retrieves the variables out of scope from the element given a context.
	 */
	public static List<CtVariableAccess> retriveVariablesOutOfContext(List<CtVariable> varContext,
			CtElement ingredientCtElement) {
		boolean duplicated = true;
		List<CtVariableAccess> allVariablesFromElement = collectVariableAccess(ingredientCtElement, duplicated);
		return retriveVariablesOutOfContext(varContext, allVariablesFromElement);
	}

	/**
	 * Retrieves the variables out of scope from the element given a context.
	 */
	public static List<CtVariableAccess> retriveVariablesOutOfContext(List<CtVariable> varContext,
			List<CtVariableAccess> variablesToChech) {
		List<CtVariableAccess> variablesOutOfScope = new ArrayList<>();

		for (CtVariableAccess variableAccessFromElement : variablesToChech) {
			if (!fitInPlace(varContext, variableAccessFromElement)) {
				variablesOutOfScope.add(variableAccessFromElement);
			}
		}
		return variablesOutOfScope;
	}

	public static List<CtVariableAccess> collectStaticVariableAccess(CtElement rootElement,
			List<CtVariableAccess> varAccessCollected) {
		List<CtVariableAccess> statics = new ArrayList<>();

		for (CtVariableAccess ctVariableAccess : varAccessCollected) {
			CtVariableReference varref = ctVariableAccess.getVariable();

			if (isStatic(varref)) {
				statics.add(ctVariableAccess);
			}
		}
		return statics;
	}

	public static boolean isStatic(CtVariableReference varref) {

		if (!(varref instanceof CtFieldReference)) {
			return false;
		}

		CtFieldReference fieldRef = (CtFieldReference) varref;

		return fieldRef.isStatic();

	}

	/**
	 * Return true if there is name conflicts between the vars and the context.
	 * 
	 * @param varsFromContext
	 * @param varInductionCollected
	 * @return
	 */
	public static boolean nameConflict(List<CtVariable> varsFromContext, List<CtVariableAccess> varInductionCollected) {
		Map<CtVariableAccess, List<CtVariable>> conflics = searchVarNameConflicts(varsFromContext,
				varInductionCollected);

		return !conflics.isEmpty();
	}

	/**
	 * Returns a map between the variables with name conflicts.
	 * 
	 * @param varsFromContext
	 * @param varInductionCollected
	 * @return
	 */
	public static Map<CtVariableAccess, List<CtVariable>> searchVarNameConflicts(List<CtVariable> varsFromContext,
			List<CtVariableAccess> varInductionCollected) {

		Map<CtVariableAccess, List<CtVariable>> mappingConflicts = new HashMap<>();

		for (CtVariableAccess inductionVar : varInductionCollected) {

			List<CtVariable> varsConf = new ArrayList<>();
			String nameInduction = inductionVar.getVariable().getSimpleName();

			for (CtVariable ctVariableContext : varsFromContext) {
				String nameVarContexr = ctVariableContext.getSimpleName();
				if (nameInduction.equals(nameVarContexr)) {
					varsConf.add(ctVariableContext);
				}
			}
			if (varsConf.size() > 0) {
				mappingConflicts.put(inductionVar, varsConf);
			}

		}
		return mappingConflicts;
	}

	/**
	 * It retrieves all variables access which declarations are inside the
	 * ingredient.
	 * 
	 * @param ingredientRootElement
	 * @param varAccessCollected
	 * @return
	 */
	public static List<CtVariableAccess> collectInductionVariableAccess(CtElement ingredientRootElement,
			List<CtVariableAccess> varAccessCollected) {

		List<CtVariableAccess> induction = new ArrayList<>();

		for (CtVariableAccess ctVariableAccess : varAccessCollected) {

			CtVariableReference varref = ctVariableAccess.getVariable();

			// We are interesting in induction vars, they are modeled as
			// LocalVariables
			if (!(varref instanceof CtLocalVariableReference))
				continue;

			CtVariable var = varref.getDeclaration();

			boolean insideIngredient = checkParent(var, ingredientRootElement);
			if (insideIngredient)
				induction.add(ctVariableAccess);

		}
		return induction;
	}

	/**
	 * 
	 * @param var
	 * @param rootElement
	 * @return
	 */
	private static boolean checkParent(CtVariable var, CtElement rootElement) {

		if (rootElement == null)
			logger.error("Error! root element null");
		CtElement parent = var;
		while (parent != null
				&& !(parent instanceof CtPackage)/*
													 * && !CtPackage. TOP_LEVEL_PACKAGE_NAME. equals(parent.toString())
													 */) {
			if (parent.equals(rootElement))
				return true;
			parent = parent.getParent();
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	public static boolean changeShadowedVars(CtElement origin, CtElement destination) {
		boolean changed = false;
		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(destination);
		logger.debug("vars from patch " + varAccessCollected);
		for (CtVariableAccess ctVariableAccess : varAccessCollected) {

			logger.debug(
					"--> var from patch: " + ctVariableAccess + " " + ctVariableAccess.getClass().getCanonicalName());
			if (ctVariableAccess instanceof CtFieldAccess) {
				CtFieldAccess f = (CtFieldAccess) ctVariableAccess;

				CtField<?> field = f.getVariable().getFieldDeclaration();

				List<CtVariable<?>> vars = origin.map(new PotentialVariableDeclarationFunction(field.getSimpleName()))
						.list();
				if (vars.size() > 0) {
					for (CtVariable<?> ctVariable : vars) {

						if (ctVariable != field) {
							logger.debug("SameName: " + ctVariable);
							if (ctVariable instanceof CtParameter) {
								CtParameterReference pr = MutationSupporter.getFactory()
										.createParameterReference((CtParameter) ctVariable);
								CtVariableRead vr = (CtVariableRead) MutationSupporter.getFactory().createVariableRead(
										pr, ctVariable.getModifiers().contains(ModifierKind.STATIC));

								ctVariableAccess.replace(vr);
							} else if (ctVariable instanceof CtLocalVariable) {
								CtLocalVariableReference pr = MutationSupporter.getFactory()
										.createLocalVariableReference((CtLocalVariable) ctVariable);
								CtVariableRead vr = (CtVariableRead) MutationSupporter.getFactory().createVariableRead(
										pr, ctVariable.getModifiers().contains(ModifierKind.STATIC));
								ctVariableAccess.replace(vr);

							}
							changed = true;

						}
					}

				}
			}
		}
		return changed;
	}

	/**
	 * Returns all variables in scope, reachable from the ctelement passes as
	 * argument
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CtVariable> searchVariablesInScope(CtElement element) {
		List<CtVariable> variables = new ArrayList();

		if (element == null) {
			return variables;
		}

		if (element instanceof CtField) {
			return variables;
		}
		// We find the CtClass and returns the fields
		CtClass ctclass = element.getParent(CtClass.class);
		if (ctclass != null) {
			Collection<CtFieldReference<?>> vars = ctclass.getAllFields();
			for (CtFieldReference<?> ctFieldReference : vars) {
				// We dont add private fields from parent classes
				if ((!ctFieldReference.getModifiers().contains(ModifierKind.PRIVATE)
						|| ctclass.getFields().contains(ctFieldReference.getDeclaration()))) {

					// We ignore "serialVersionUID'
					if ((ctFieldReference.getDeclaration() != null)
							&& !"serialVersionUID".equals(ctFieldReference.getDeclaration().getSimpleName()))
						variables.add(ctFieldReference.getDeclaration());
				}
			}

		}

		// We find the parent method and we extract the parameters
		CtMethod method = element.getParent(CtMethod.class);
		if (method != null) {
			List<CtParameter> pars = method.getParameters();
			for (CtParameter ctParameter : pars) {
				variables.add(ctParameter);
			}
		}

		// We find the parent block and we extract the local variables before
		// the element under analysis
		CtBlock parentblock = element.getParent(CtBlock.class);
		CtElement currentElement = element;
		if (parentblock != null) {
			int positionEl = parentblock.getStatements().indexOf(currentElement);
			variables.addAll(VariableResolver.retrieveLocalVariables(positionEl, parentblock));
			if (ConfigurationProperties.getPropertyBool("consideryvarloops")) {
				variables.addAll(getVarsInFor(currentElement));
				variables.addAll(getVarsInForEach(currentElement));
			}

		}

		return variables;

	}

	private static List<CtLocalVariable> getVarsInFor(CtElement element) {
		List<CtLocalVariable> variables = new ArrayList<CtLocalVariable>();
		CtElement currentElement = element;
		CtFor ff = currentElement.getParent(CtFor.class);
		while (ff != null) {
			variables.addAll(ff.getForInit().stream().filter(e -> e instanceof CtLocalVariable)
					.map(CtLocalVariable.class::cast).collect(Collectors.toList()));

			ff = ff.getParent(CtFor.class);

		}
		return variables;
	}

	private static List<CtLocalVariable> getVarsInForEach(CtElement element) {
		List<CtLocalVariable> variables = new ArrayList<CtLocalVariable>();
		CtElement currentElement = element;
		CtForEach ff = currentElement.getParent(CtForEach.class);
		while (ff != null) {
			variables.add((CtLocalVariable) ff.getVariable());
			ff = ff.getParent(CtForEach.class);

		}
		return variables;
	}

	/**
	 * Return the local variables of a block from the beginning until the element
	 * located at positionEl.
	 * 
	 * @param positionEl analyze variables from the block until that position.
	 * @param pb         a block to search the local variables
	 * @return
	 */
	protected static List<CtLocalVariable> retrieveLocalVariables(int positionEl, CtBlock pb) {
		List stmt = pb.getStatements();
		List<CtLocalVariable> variables = new ArrayList<CtLocalVariable>();
		for (int i = 0; i < positionEl; i++) {
			CtElement ct = (CtElement) stmt.get(i);
			if (ct instanceof CtLocalVariable) {
				variables.add((CtLocalVariable) ct);
			}
		}
		CtElement beforei = pb;
		CtElement parenti = pb.getParent();
		boolean continueSearch = true;
		// We find the parent block
		while (continueSearch) {

			if (parenti == null) {
				continueSearch = false;
				parenti = null;
			} else if (parenti instanceof CtBlock) {
				continueSearch = false;
			} else {
				beforei = parenti;
				parenti = parenti.getParent();
			}
		}

		if (parenti != null) {
			int pos = ((CtBlock) parenti).getStatements().indexOf(beforei);
			variables.addAll(retrieveLocalVariables(pos, (CtBlock) parenti));
		}
		return variables;
	}

	/**
	 * Adapt the ingredient to the destination according to the mapping. We directly
	 * manipulate the variables from the ingredient, which are stored in VarMapping
	 * 
	 * @param varMapping
	 * @param destination
	 * @return it returns the original variable reference of each converted variable
	 */
	@SuppressWarnings("unchecked")
	public static Map<VarAccessWrapper, CtVariableAccess> convertIngredient(VarMapping varMapping,
			Map<String, CtVariable> mapToFollow) {

		Map<VarAccessWrapper, CtVariableAccess> originalMap = new HashMap<>();

		Map<VarAccessWrapper, List<CtVariable>> mappedVars = varMapping.getMappedVariables();
		for (VarAccessWrapper var : mappedVars.keySet()) {
			CtVariable varNew = mapToFollow.get(var.getVar().getVariable().getSimpleName());
			//
			CtVariableReference newVarReference = varNew.getReference();

			CtVariableAccess originalVarAccessDestination = var.getVar();
			CtVariableAccess newVarAccessDestination = null;

			// if the var to reference is a local or parameter
			if (newVarReference instanceof CtLocalVariableReference
					|| newVarReference instanceof CtParameterReference) {
				// let's check the destination Writes or Reads
				if (originalVarAccessDestination instanceof CtFieldWrite
						|| originalVarAccessDestination instanceof CtVariableWrite) {
					// We replace the Write by a Var writter
					newVarAccessDestination = MutationSupporter.getFactory().Core().createVariableWrite();
					newVarAccessDestination.setVariable(newVarReference);

				} else { // read
					newVarAccessDestination = MutationSupporter.getFactory().Code().createVariableRead(newVarReference,
							varNew.hasModifier(ModifierKind.STATIC));
				}

			} else
			// else, if we want to reference a field
			if (newVarReference instanceof CtFieldReference) {
				// let's check the destination, write or read
				if (originalVarAccessDestination instanceof CtFieldWrite<?>
						|| originalVarAccessDestination instanceof CtFieldRead<?>) {
					newVarAccessDestination = MutationSupporter.getFactory().Core().createFieldWrite();

				} else {
					newVarAccessDestination = MutationSupporter.getFactory().Core().createFieldRead();

				}
				newVarAccessDestination.setVariable(newVarReference);
			}
			// At the end, for all cases:
			if (newVarAccessDestination != null) {
				originalMap.put(new VarAccessWrapper(newVarAccessDestination), originalVarAccessDestination);
				originalVarAccessDestination.replace(newVarAccessDestination);
			} else {
				logger.error("No destination resolved");
			}

		} // end for
		return originalMap;
	}

	/**
	 * For each modified variable, it resets the variables by putting their original
	 * var reference
	 * 
	 * @param varMapping
	 * @param original
	 */

	public static void resetIngredient(Map<VarAccessWrapper, CtVariableAccess> old) {
		for (VarAccessWrapper newa : old.keySet()) {
			newa.getVar().replace(old.get(newa));

		}

	}

	public static IngredientPoolScope determineIngredientScope(CtElement ingredient, CtElement fix) {

		File ingp = ingredient.getPosition().getFile();
		File fixp = fix.getPosition().getFile();

		if (ingp == null || fixp == null)
			return null;

		if (ingp.getAbsolutePath().equals(fixp.getAbsolutePath())) {
			return IngredientPoolScope.LOCAL;
		}
		if (ingp.getParentFile().getAbsolutePath().equals(fixp.getParentFile().getAbsolutePath())) {
			return IngredientPoolScope.PACKAGE;
		}
		return IngredientPoolScope.GLOBAL;
	}

	@Deprecated
	protected IngredientPoolScope determineIngredientScope(CtElement modificationpoint, CtElement selectedFix,
			List<?> ingredients) {
		// This is the original ingredient scope
		IngredientPoolScope orig = VariableResolver.determineIngredientScope(modificationpoint, selectedFix);

		String fixStr = selectedFix.toString();

		// Now, we search for equivalent fixes with different scopes
		for (Object ing : ingredients) {
			try {
				ing.toString();
			} catch (Exception e) {
				// if we cannot print the ingredient, we return
				logger.error(e.toString());
				continue;
			}
			// if it's the same fix
			if (ing.toString().equals(fixStr)) {
				IngredientPoolScope n = VariableResolver.determineIngredientScope(modificationpoint, (CtElement) ing);
				// if the scope of the ingredient ing is narrower than the fix,
				// we keep it.
				if (n.ordinal() < orig.ordinal()) {
					orig = n;
					// if it's local, we return
					if (IngredientPoolScope.values()[0].equals(orig))
						return orig;
				}

			}
		}
		return orig;
	}

	public static List<Map<String, CtVariable>> findAllVarMappingCombination(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars) {
		NGramManager managerngram = null;
		return findAllVarMappingCombination(mappedVars, managerngram);
	}

	/**
	 * 
	 * Method that finds all combination of variables mappings Ex: if var 'a' can be
	 * mapped to a1 and a2, and var 'b' to b1 and b2, the method return all
	 * combinations (a1,b1), (a2,b1), (a1,b2), (a2,b2)
	 * 
	 * @param mappedVars map of variables (out-of-scope) and candidate replacements
	 *                   of
	 * @return
	 */
	public static List<Map<String, CtVariable>> findAllVarMappingCombination(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars, NGramManager managerngram) {

		if (mappedVars.isEmpty()) {
			return new ArrayList<Map<String, CtVariable>>();
		}

		List<VarAccessWrapper> varsNamesToCombine = new ArrayList<>(mappedVars.keySet());

		List<Map<String, CtVariable>> allCombinations = new ArrayList<>();
		allCombinations.add(new TreeMap<>());

		Number[] maxValues = getMaxCombination(mappedVars, varsNamesToCombine);

		long numberTotalComb = (long) maxValues[0];
		double maxPerVarLimit = (double) maxValues[1];
		try {
			for (VarAccessWrapper currentVar : varsNamesToCombine) {

				if (allCombinations.size() > 0
						&& allCombinations.get(0).containsKey(currentVar.getVar().getVariable().getSimpleName())) {
					logger.debug("Var already mapped: " + currentVar.getVar().getVariable().getSimpleName());
					continue;
				}

				List<Map<String, CtVariable>> generationCombinations = new ArrayList<>();

				List<CtVariable> mapped = mappedVars.get(currentVar);

				List<CtVariable> sortedVariables = new ArrayList<>(mapped);

				if (managerngram == null) {
					logger.debug("Sorting variables Randomly: " + sortedVariables.size());
					Collections.shuffle(sortedVariables, RandomManager.getRandom());
				} else {
					logger.debug("Sorting variables by 1-gram");
					Collections.sort(sortedVariables, new Comparator<CtVariable>() {

						@Override
						public int compare(CtVariable v1, CtVariable v2) {
							String s1 = v1.getSimpleName();
							String s2 = v2.getSimpleName();

							Double p1 = (Double) managerngram.getNgglobal().ngrams[1].getProbabilies().get(s1);
							Double p2 = (Double) managerngram.getNgglobal().ngrams[1].getProbabilies().get(s2);

							if (p1 == null && p2 == null) {
								return 0;
							}

							if (p1 == null) {
								logger.debug("Var not found in global ngram: " + s1);
								return 1;
							}
							if (p2 == null) {
								logger.debug("Var not found in global ngram: " + s2);
								return -1;
							}
							return Double.compare(p2, p1);
						}
					});
					// logger.debug("vars sorted "+sortedVariables);
				}

				// Now, let's create the combinations:
				int varsAnalyzed = 0;
				// for each mapping candidate
				for (CtVariable varFromMap : sortedVariables) {
					// We count the variables that can be mapped for that
					// combination.

					for (Map<String, CtVariable> previousCombination : allCombinations) {

						// we create the new var combination from the previous
						// one
						Map<String, CtVariable> newCombination = new TreeMap<>(previousCombination);
						// we add the map for the variable to the new
						// combination
						newCombination.put(currentVar.getVar().getVariable().getSimpleName(), varFromMap);
						generationCombinations.add(newCombination);
					}
					varsAnalyzed++;
					if (varsAnalyzed >= ((int) (Math.ceil(maxPerVarLimit)))) {
						break;
					}

				}
				allCombinations = generationCombinations;
			}
		} catch (Throwable e) {
			logger.error("Problems when calculating combinations, nr vars " + mappedVars.size()
					+ " theoretical combinations: " + Arrays.toString(maxValues));
			logger.error(e);
			return new ArrayList<Map<String, CtVariable>>();
		}
		// FIlter combinations that are empty
		allCombinations = allCombinations.stream().filter(e -> !e.isEmpty()).collect(Collectors.toList());

		int maxNumberCombinations = ConfigurationProperties.getPropertyInt("maxVarCombination");

		logger.debug("NrVarCombinationsConsideredBeforeCutting: " + allCombinations.size());
		if (allCombinations.size() > maxNumberCombinations) {
			allCombinations = allCombinations.subList(0, maxNumberCombinations);
		}

		logger.debug("NrVarCombinationsConsideredAfter: " + allCombinations.size());

		for (Map<String, CtVariable> map : allCombinations) {
			if (map.keySet().size() != varsNamesToCombine.size()) {
				// logger.error("Missing vars "+ map.keySet().size() +" "+
				// varsNamesToCombine);
			}
		}

		return allCombinations;
	}

	public static Number[] getMaxCombination(Map<VarAccessWrapper, List<CtVariable>> mappedVars,
			List<VarAccessWrapper> varsNamesToCombine) {

		int maxNumberCombinations = ConfigurationProperties.getPropertyInt("maxVarCombination");

		int max = -1;
		long numberTotalComb = 1;
		int nrVarsWithMorethan1Possibilities = 0;
		Set<String> vars = new HashSet<>();
		// Here the code for calculating the total number of combinations
		for (VarAccessWrapper currentVar : varsNamesToCombine) {

			if (vars.contains(currentVar.getVar().getVariable().getSimpleName())) {
				continue;
			}

			vars.add(currentVar.getVar().getVariable().getSimpleName());

			List<CtVariable> mapped = mappedVars.get(currentVar);
			int numberCompVar = mapped.size();

			if (numberCompVar > max)
				max = numberCompVar;

			if (numberCompVar > 1)
				nrVarsWithMorethan1Possibilities++;

			logger.debug(String.format("Number compatible vars of %s : %d",
					currentVar.getVar().getVariable().getSimpleName(), numberCompVar));

			if (numberTotalComb < Integer.MAX_VALUE) {
				long mult = (long) numberTotalComb * numberCompVar;
				if (mult > Integer.MAX_VALUE || mult < Integer.MIN_VALUE) {
					logger.debug("Max Combination: Overflow 32-bit. Considering nrCombinations: " + Integer.MAX_VALUE);
					numberTotalComb = Integer.MAX_VALUE;
				} else
					numberTotalComb *= numberCompVar;
			}
		}

		logger.debug("Theoreticalcombinations: " + numberTotalComb);
		double maxPerVarLimit = 0;

		if (numberTotalComb < maxNumberCombinations
				|| !ConfigurationProperties.getPropertyBool("maxCombinationVariableLimit")) {
			// We dont need to cut vars
			maxPerVarLimit = max;
		} else {
			maxPerVarLimit = Math.pow(maxNumberCombinations, 1.0 / nrVarsWithMorethan1Possibilities);
		}

		logger.debug(String.format("Max per var %f, total number comb: %d  ", maxPerVarLimit, numberTotalComb));

		return new Number[] { numberTotalComb, maxPerVarLimit };
	}

}
