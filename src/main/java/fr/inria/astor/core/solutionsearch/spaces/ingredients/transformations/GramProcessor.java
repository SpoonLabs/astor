package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class GramProcessor {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	public TargetElementProcessor<?> ingredientProcessor;

	public GramProcessor(TargetElementProcessor<?> ip) {
		super();
		this.ingredientProcessor = ip;
	}

	/**
	 * Return the n-gram by class from the list of types given as parameter
	 * 
	 * @param allNGrams
	 * @return
	 * @throws JSAPException
	 */
	public Map<String, NGrams> calculateByClass(List<CtType<?>> allNGrams) throws JSAPException {

		Map<String, NGrams> resultNGrams = new HashedMap();
		for (CtType<?> ctType : allNGrams) {
			resultNGrams.put(ctType.getQualifiedName(), calculateGrams4Class(ctType));
		}
		return resultNGrams;
	}

	public NGrams calculateGrams4Class(CtType type) throws JSAPException {

		Boolean mustCloneOriginalValue = ConfigurationProperties.getPropertyBool("duplicateingredientsinspace");
		// Forcing to duplicate
		ConfigurationProperties.setProperty("duplicateingredientsinspace", "true");

		
		NGrams gramsFromClass = new NGrams();

		CodeParserLauncher ingp = new CodeParserLauncher<>(ingredientProcessor);
		int allElements = 0;
		CtType typeToProcess = type;
		while (typeToProcess != null // &&
										// !ngramsStore.containsKey(typeToProcess.getQualifiedName())
		) {
			logger.debug("Extracting Ngram from " + typeToProcess.getQualifiedName());
			allElements += getNGramsFromCodeElements(typeToProcess, gramsFromClass, ingp);
			if (typeToProcess.getSuperclass() != null)
				typeToProcess = typeToProcess.getSuperclass().getDeclaration();
			else
				typeToProcess = null;

		}
		// reset property clone
		ConfigurationProperties.setProperty("duplicateingredientsinspace", Boolean.toString(mustCloneOriginalValue));


		return gramsFromClass;
	}

	/**
	 * Obtain all n-gram of the element give as parameter according to the
	 * ingredient processor also passed as argument. The results (n-grams) are
	 * stored in the ngram stored passed as argument
	 * 
	 * @param codeToProcess
	 * @param ngramStore
	 * @param ingp
	 * @return
	 */
	private int getNGramsFromCodeElements(CtElement codeToProcess, NGrams ngramStore, CodeParserLauncher ingp) {

		int allElements = 0;

		List<CtCodeElement> elementsFromSpace = ingp.createFixSpace(codeToProcess, false);
		for (CtCodeElement ctCodeElement : elementsFromSpace) {

			List<CtVariableAccess> vars = VariableResolver.collectVariableAccess(ctCodeElement);
			if (vars.isEmpty())
				continue;

			if (vars.size() > ConfigurationProperties.getPropertyDouble("maxnumvariablesperingredient")) {
				logger.debug("Attention, Large Ingredient discarted: \n" + ctCodeElement.getShortRepresentation());
				continue;
			}

			cleanParenthesis(vars);
			sortVarsByNames(vars);

			for (int i = vars.size(); i >= 1; i--) {
				List allpermutations = getPermutationsOfVarNames(vars, i);
				ngramStore.add(allpermutations, i);
			}
			
			//logger.debug("\n: "+ctCodeElement+ " ng" + ngramStore);

			allElements++;
		}
		return allElements;
	}

	/**
	 * Remove parenthesis from a var access. e.g. (a)
	 * 
	 * @param vars
	 */
	private void cleanParenthesis(List<CtVariableAccess> vars) {
		for (CtVariableAccess ctVariableAccess : vars) {
			String name = ctVariableAccess.getVariable().getSimpleName();
			if (name.startsWith("(")) {
				int l = name.length();
				String nm = name.substring(1, l - 2);
			}

		}
	}
	@Deprecated
	public Map<String, NGrams> calculateByPackage(List<CtType<?>> all) throws JSAPException {

		Map<String, NGrams> result = new HashedMap();
		CodeParserLauncher ingp = new CodeParserLauncher<>(ingredientProcessor);
		int allElements = 0;

		for (CtType<?> ctType : all) {
			NGrams ns = new NGrams();
			CtPackage parent = (CtPackage) ctType.getParent(CtPackage.class);
			if (!result.containsKey(parent.getQualifiedName())) {
				allElements += getNGramsFromCodeElements(parent, ns, ingp);
				result.put(parent.getQualifiedName(), ns);
			}
		}
		logger.debug("allElements " + allElements);
		return result;
	}
	@Deprecated
	public Map<String, NGrams> calculateByType(List<CtType<?>> all) throws JSAPException {

		Map<String, NGrams> result = new HashedMap();
		CodeParserLauncher ingp = new CodeParserLauncher<>(ingredientProcessor);
		int allElements = 0;

		for (CtType<?> ctType : all) {
			NGrams ns = new NGrams();
			allElements += getNGramsFromCodeElements(ctType, ns, ingp);
			result.put(ctType.getQualifiedName(), ns);
		}
		logger.debug("allElements " + allElements);
		return result;
	}

	public NGrams calculateGlobal(List<CtType<?>> all) throws JSAPException {

		NGrams ns = new NGrams();

		CodeParserLauncher ingp = new CodeParserLauncher<>(ingredientProcessor);
		int allElements = 0;

		for (CtType<?> ctType : all) {
			allElements += getNGramsFromCodeElements(ctType, ns, ingp);

		}
		logger.debug("allElements " + allElements);
		return ns;
	}

	/**
	 * Order the var access list by name.
	 * 
	 * @param vars
	 */
	public void sortVarsByNames(List<CtVariableAccess> vars) {

		Collections.sort(vars, new Comparator<CtVariableAccess>() {

			@Override
			public int compare(CtVariableAccess o1, CtVariableAccess o2) {
				return o1.getVariable().getSimpleName().compareTo(o2.getVariable().getSimpleName());
			}
		});

	}

	/**
	 * Get all permutation of a list of var access
	 * 
	 * @param sequence
	 * @param n
	 * @return
	 */
	private static List getPermutationsOfVarNames(List sequence, int n) {
		List allPermutationObtained = new ArrayList<>();

		int N = sequence.size();

		int[] binary = new int[(int) Math.pow(2, N)];
		for (int i = 0; i < Math.pow(2, N); i++) {
			int b = 1;
			binary[i] = 0;
			int num = i, count = 0;
			while (num > 0) {
				if (num % 2 == 1)
					count++;
				binary[i] += (num % 2) * b;
				num /= 2;
				b = b * 10;
			}
			if (count == n) {
				List subs = new ArrayList<>();
				for (int j = 0; j < N; j++) {
					if (binary[i] % 10 == 1)
						subs.add(((CtVariableAccess) sequence.get(j)).getVariable().getSimpleName());
					binary[i] /= 10;
				}
				String listflat = (String) subs.stream().map(Object::toString).collect(Collectors.joining(" "));
				allPermutationObtained.add(listflat);
			}
		}
		return allPermutationObtained;
	}

}
