package fr.inria.astor.core.manipulation.sourcecode;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtLocalVariableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.CtScanner;
import spoon.support.reflect.code.CtFieldWriteImpl;

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

	/**
	 * Return all variables related to the element passed as argument
	 * 
	 * @param element
	 * @return
	 */
	public static List<CtVariableAccess> collectVariableAccess(CtElement element, boolean duplicates) {
		List<CtVariableAccess> varaccess = new ArrayList<>();

		CtScanner sc = new CtScanner() {

			public void add(CtVariableAccess e) {
				if (duplicates || !varaccess.contains(e))
					varaccess.add(e);
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

	/**
	 * 
	 * This methods determines whether all the variable access contained in a
	 * CtElement passes as parameter match with a variable from a set of
	 * variables given as argument. Both variable Types and Names are compared,
	 * 
	 * @param varContext
	 *            List of variables to match
	 * @param element
	 *            element to extract the var access to match
	 * @return
	 */
	public static boolean fitInPlace(List<CtVariable> varContext, CtElement element) {
		return fitInContext(varContext, element, true);
	}

	/**
	 * This methods determines whether all the variable access contained in a
	 * CtElement passes as parameter match with a variable from a set of
	 * variables given as argument. The argument <code>matchName </code>
	 * indicates whether Type and Names are compared (value true), only type
	 * (false).
	 * 
	 * @param varContext
	 *            List of variables to match
	 * @param ingredientCtElement
	 *            element to extract the var access to match
	 * @return
	 */
	public static boolean fitInContext(List<CtVariable> varContext, CtElement ingredientCtElement, boolean matchName) {

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
			return false;
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
		// Now, we analyze if all access were matched
		for (CtVariableAccess ctVariableAccess : matched.keySet()) {
			List<CtVariable> mapped = matched.get(ctVariableAccess);
			if (mapped.isEmpty()) {
				// One var access was not mapped
				return false;
			}
		}
		// All VarAccess were mapped
		return true;

	}

	/**
	 * 
	 */
	public static VarMapping mapVariables(List<CtVariable> varContext, CtElement ingredientCtElement) {

		// var out-of scope, list of variables compatibles
		Map<VarAccessWrapper, List<CtVariable>> varMaps = new HashMap<>();
		List<CtVariableAccess> notMappedVariables = new ArrayList<>();

		ClassLoader classLoader = VariableResolver.class.getClassLoader();

		Map<String, List<String>> clusters = cluster
				.readClusterFile(Paths.get(ConfigurationProperties.getProperty("learningdir") + File.separator
						+ ConfigurationProperties.getProperty("clusteringfilename")));

		List<CtVariableAccess> variablesOutOfScope = retriveVariablesOutOfContext(varContext, ingredientCtElement);
		logger.debug("vars out of context: " + variablesOutOfScope);
		for (CtVariableAccess wOut : variablesOutOfScope) {

			List<String> wcluster = clusters.get(wOut.getVariable().getSimpleName());

			if (wcluster == null) {
				logger.debug("variable our of scope without context: " + wOut);
				continue;
			}
			logger.debug("--var  out of context: " + wOut + ", with wcluster " + wcluster);

			boolean mapped = false;
			VarAccessWrapper varOutWrapper = new VarAccessWrapper(wOut);
			for (String wordFromCluster : wcluster) {// In order

				List<CtVariable> varExist = existVariableWithName(varContext, wordFromCluster);
				// check compatibility between varExist and wout
				for (CtVariable varFromCluster : varExist) {
					CtTypeReference typeref_i = varFromCluster.getType();
					try {
						if (typeref_i.isSubtypeOf(wOut.getType())) {
							List<CtVariable> vars = varMaps.get(varOutWrapper);
							if (vars == null) {
								vars = new ArrayList<>();
								varMaps.put(varOutWrapper, vars);
							}
							vars.add(varFromCluster);
							mapped = true;
							// We do not break the loop, we want to find all
							// mappings
						}
					} catch (Exception e) {
						logger.error(e);
					}
				}

			}
			// if the var was not matched, we put in list of variables out of
			// scope not mapped.
			if (!mapped)
				notMappedVariables.add(wOut);

		}
		VarMapping mappings = new VarMapping(varMaps, notMappedVariables);
		return mappings;

		// : finds all variables *out of scope* from 'Ing'.
		// 2: for each var 'wout' from *out of scope* do
		// 2.a: finds line 'Lwo' corresponding to 'out' var in clustering.csv to
		// retrieve the cluster of 'wout'
		// 2.b: for each word 'wcluster' from 'Lwo' // as they sorted by
		// embedding
		// distance, it iterates from left to right
		// 2.b.1: Search if there is one variable 'wcontext' with name
		// 'wcluster' in
		// scope.// here, we invoke to the VariableResolver.
		// 2.b.2: Check compatibility of types from 'wcontext' and 'wout' vars
		// 2.b.2.1: if they are compatibles replace 'wout'' by 'wcontent' on
		// 'Ing';
		// break loop (2.b).
		// 2.b.2.2: else (vars not compatibles) continue loop.

	}

	/**
	 * Returns the variables that have as name the string passed as argument.
	 * 
	 * @param varContext
	 *            variables
	 * @param wordFromCluster
	 *            name of a variable
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
		List<CtVariableAccess> variablesOutOfScope = new ArrayList<>();
		boolean duplicated = true;
		List<CtVariableAccess> allVariables = collectVariableAccess(ingredientCtElement, duplicated);
		for (CtVariableAccess variableAccessFromElement : allVariables) {
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

			// an access to a static must be a static field.
			if (!(varref instanceof CtFieldReference)) {
				continue;
			}

			CtVariable var = varref.getDeclaration();
			if (var == null || var.getModifiers().contains(ModifierKind.STATIC)) {
				statics.add(ctVariableAccess);
			}
		}
		return statics;
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
			// logger.debug("var decl " + var + " indution " +
			// insideIngredient);
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
		while (parent != null && !CtPackage.TOP_LEVEL_PACKAGE_NAME.equals(parent.toString())) {
			if (parent.toString().equals(rootElement.toString()))
				return true;
			parent = parent.getParent();
		}

		return false;
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
		if (parentblock != null) {
			int positionEl = parentblock.getStatements().indexOf(element);
			variables.addAll(VariableResolver.retrieveLocalVariables(positionEl, parentblock));
		}

		return variables;

	}

	/**
	 * Return the local variables of a block from the beginning until the
	 * element located at positionEl.
	 * 
	 * @param positionEl
	 *            analyze variables from the block until that position.
	 * @param pb
	 *            a block to search the local variables
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
	 * Adapt the ingredient to the destination according to the mapping. We
	 * directly manipulate the variables from the ingredient, which are stored
	 * in VarMapping
	 * 
	 * @param varMapping
	 * @param destination
	 * @return it returns the original variable reference of each converted
	 *         variable
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
	 * For each modified variable, it resets the variables by putting their
	 * original var reference
	 * 
	 * @param varMapping
	 * @param original
	 */

	public static void resetIngredient(Map<VarAccessWrapper, CtVariableAccess> old) {
		for (VarAccessWrapper newa : old.keySet()) {
			newa.getVar().replace(old.get(newa));

		}

	}

	/**
	 * 
	 * Method that finds all combination of variables mappings Ex: if var 'a'
	 * can be mapped to a1 and a2, and var 'b' to b1 and b2, the method return
	 * all combinations (a1,b1), (a2,b1), (a1,b2), (a2,b2)
	 * 
	 * @param mappedVars
	 *            map of variables (out-of-scope) and candidate replacements of
	 * @param currentCombination
	 *            current combination of variables
	 * @return
	 */
	public static List<Map<String, CtVariable>> findAllVarMappingCombination(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars) {

		List<Map<String, CtVariable>> allCombinationsOne = new ArrayList<>();

		if (!mappedVars.isEmpty()) {
			List<VarAccessWrapper> varNamesOne = new ArrayList<>(mappedVars.keySet());

			VariableResolver.findAllVarMappingCombination(mappedVars, varNamesOne, 0, new TreeMap<>(),
					allCombinationsOne);
		}
		return allCombinationsOne;
	}

	/**
	 * Method that finds all combination of variables mappings Ex: if var 'a'
	 * can be mapped to a1 and a2, and var 'b' to b1 and b2, the method return
	 * all combinations (a1,b1), (a2,b1), (a1,b2), (a2,b2)
	 * 
	 * @param mappedVars
	 *            map of variables (out-of-scope) and candidate replacements of
	 * @param varsName
	 *            names of all variables
	 * @param indexVar
	 *            current variable under analysis
	 * @param currentCombination
	 *            current combination of variables
	 * @param allCombinations
	 *            list that store all variable combinations
	 */
	public static void findAllVarMappingCombination(Map<VarAccessWrapper, List<CtVariable>> mappedVars,
			List<VarAccessWrapper> varsName, int indexVar, Map<String, CtVariable> currentCombination,
			List<Map<String, CtVariable>> allCombinations) {

		// Stop condition
		// If we have analyzed all variables, we add the combination to the
		// result
		if (varsName.size() == indexVar) {
			allCombinations.add(currentCombination);
			return;
		}

		// Get the variable to change
		// CtVariableAccess currentVar = varsName.get(indexVar).getVar();
		VarAccessWrapper currentVar = varsName.get(indexVar);
		// get all possibles variables to replace
		List<CtVariable> mapped = mappedVars.get(currentVar);

		if (currentCombination.containsKey(currentVar.getVar().getVariable().getSimpleName())) {
			findAllVarMappingCombination(mappedVars, varsName, indexVar + 1, currentCombination, allCombinations);
		}

		// for each mapping candidate
		for (CtVariable varFromMap : mapped) {
			// we create the new var combination from the previous one
			Map<String, CtVariable> newCombination = new TreeMap<>(currentCombination);
			// we add the map for the variable to the new combination
			newCombination.put(currentVar.getVar().getVariable().getSimpleName(), varFromMap);
			// we call recursive to continue mapping the remaining variables
			findAllVarMappingCombination(mappedVars, varsName, indexVar + 1, newCombination, allCombinations);
		}
	}
}
