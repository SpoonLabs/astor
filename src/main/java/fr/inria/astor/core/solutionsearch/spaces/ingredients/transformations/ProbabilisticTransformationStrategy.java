package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.util.MapCounter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ProbabilisticTransformationStrategy extends CacheTransformationStrategy
		implements IngredientTransformationStrategy {

	protected Logger logger = Logger.getLogger(ProbabilisticTransformationStrategy.class.getName());
	protected NGramManager ngramManager = new NGramManager(null, null);

	public ProbabilisticTransformationStrategy() {
		super();

	}

	public ProbabilisticTransformationStrategy(Map<String, NGrams> ngramsSplitted, NGrams ngramsGlobal) {
		super();
		ngramManager = new NGramManager(ngramsSplitted, ngramsGlobal);
	}

	public void calculateGramsProbs() throws JSAPException {
		this.ngramManager.init();
	}

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient baseIngredient) {

		if (this.alreadyTransformed(modificationPoint, baseIngredient)) {
			return getCachedTransformations(modificationPoint, baseIngredient);
		}

		if (!this.ngramManager.initialized()) {

			logger.debug("Initializing probabilistics");
			try {
				calculateGramsProbs();
			} catch (JSAPException e) {
				logger.error(e);
				return null;
			}
		}

		List<Ingredient> transformedIngredientsResults = new ArrayList<>();

		CtCodeElement codeElementToModifyFromBase = (CtCodeElement) baseIngredient.getCode();

		if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
			logger.debug("The modification point  has not any var in scope");
		}

		VarMapping mapping = VariableResolver.mapVariablesFromContext(modificationPoint.getContextOfModificationPoint(),
				codeElementToModifyFromBase);
		// if we map all variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				logger.debug("Any transf sucessful: The var Mapping is empty, we keep the ingredient");
				transformedIngredientsResults.add(baseIngredient);
			} else {// We have mappings between variables
				logger.debug("Ingredient before transformation: " + baseIngredient.getCode() + " mined from "
						+ baseIngredient.getCode().getParent(CtType.class).getQualifiedName());

				List<VarCombinationForIngredient> allCombinations = findAllVarMappingCombinationUsingProbab(
						mapping.getMappedVariables(), modificationPoint, baseIngredient);

				if (allCombinations.size() > 0) {

					for (VarCombinationForIngredient varCombinationForIngredient : allCombinations) {

						DynamicIngredient ding = new DynamicIngredient(varCombinationForIngredient, mapping,
								codeElementToModifyFromBase);
						transformedIngredientsResults.add(ding);
					}
				}
			}
		} else {
			logger.debug("Any transformation was sucessful: Vars not mapped: " + mapping.getNotMappedVariables());
			String varContext = "";
			for (CtVariable context : modificationPoint.getContextOfModificationPoint()) {
				varContext += context.getSimpleName() + " " + context.getType().getQualifiedName() + ", ";
			}
			logger.debug("context " + varContext);
			for (CtVariableAccess ingredient : mapping.getNotMappedVariables()) {
				logger.debug("---out_of_context: " + ingredient.getVariable().getSimpleName() + ": "
						+ ingredient.getVariable().getType().getQualifiedName());
			}
		}

		this.storingIngredients(modificationPoint, baseIngredient, transformedIngredientsResults);

		return transformedIngredientsResults;
	}

	/**
	 * Returns a list of var combinations, sorted by probabilities.
	 * 
	 * @param mappedVars
	 * @param mpoint
	 * @param baseIngredient
	 * @return
	 */
	public List<VarCombinationForIngredient> findAllVarMappingCombinationUsingProbab(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars, ModificationPoint mpoint, Ingredient baseIngredient) {
		String mpointClass = mpoint.getCtClass().getQualifiedName();
		logger.debug("Ngrams from " + mpointClass);
		NGrams ngrams = this.ngramManager.getNgramsSplitted().get(mpointClass);

		sortPotentialVarsByProb(mappedVars, ngrams);

		List<Map<String, CtVariable>> allWithoutOrder = VariableResolver.findAllVarMappingCombination(mappedVars,
				this.ngramManager);
		logger.debug("Var mapping " + allWithoutOrder.size());
		List<VarCombinationForIngredient> allCom = new ArrayList<>();
		for (Map<String, CtVariable> varMapping : allWithoutOrder) {

			VarCombinationForIngredient varCombinationWrapper = new VarCombinationForIngredient(varMapping);

			int sizeCombination = varCombinationWrapper.getSize();
			MapCounter gramCounterSize = ngrams.ngrams[sizeCombination];

			MapCounter gramCounterSizeGlobal = this.ngramManager.getNgglobal().ngrams[sizeCombination];

			if (gramCounterSize == null) { // Ingredient size bigger than all
											// statements from the class

				String ingredientClass = baseIngredient.getCode().getParent(CtType.class).getQualifiedName();
				if (mpointClass.equals(ingredientClass)) {
					logger.error("Map is null for " + sizeCombination + " for local n-gramm " + mpointClass
							+ ", ingredient from " + ingredientClass);
				}

				gramCounterSize = gramCounterSizeGlobal;
				if (gramCounterSizeGlobal == null) {
					logger.error("No grams for combination size " + sizeCombination);
					logger.error("--> mp " + mpointClass);
					logger.error("\n grams " + ngrams);
					return allCom;
				}

			} else {
				// logger.debug("Okey!");
			}

			Double probability = (Double) gramCounterSize.getProbabilies()
					.get(varCombinationWrapper.getCombinationString());
			if (false) {
				logger.debug("--c: " + varCombinationWrapper.getCombinationString());
				logger.debug("--L--" + gramCounterSize.sorted());
				logger.debug("--Lp--" + gramCounterSize.getProbabilies());
				logger.debug("--G--" + gramCounterSizeGlobal.sorted());
				logger.debug("--Gp--" + gramCounterSizeGlobal.getProbabilies());
			}

			varCombinationWrapper.setProbality((probability != null) ? probability : 0);
			allCom.add(varCombinationWrapper);

		}

		//Sort from probability in descending order. In case of a tie, order by their original var order
		allCom.sort(Comparator.comparing(VarCombinationForIngredient::getProbality, Comparator.reverseOrder())
				.thenComparing(sortByVarOrder()));
		logger.debug("Number combination sorted By Probability : " + allCom.size() + " over " + allWithoutOrder.size());
		if (allCom.size() > 0)
			logger.debug("---Max prob: " + allCom.get(0).getProbality());
		return allCom;
	}

	/**
	 * eg: given a VarCombinationForIngredient vc1 and a VarCombinationForIngredient vc2
	 * if vc1 has 3 vars in the following order: "f", "max", "min"
	 * and vc2 has  3 vars in the order        : "f", "min, "max"
	 * <p>
	 * then originalOrderOfVc1 == "f max min"
	 * and  originalOrderOfVc2 == "f min max"
	 * <p>
	 * In this case vc2 has priority given its alphabetical order compared to vc1
	 *
	 * @return
	 */
	private Comparator<VarCombinationForIngredient> sortByVarOrder() {
		return (vc1, vc2) -> {
			String originalOrderOfVc1 = getOriginalOrderOrVars(vc1);
			String originalOrderOfVc2 = getOriginalOrderOrVars(vc2);
			return originalOrderOfVc2.compareTo(originalOrderOfVc1);
		};
	}

	/**
	 * eg: given varCombinationForIngredient with the 3 vars "f", "min", "max"
	 * return "f min max"
	 *
	 * @param varCombinationForIngredient
	 * @return a String with the vars from 'varCombinationForIngredient' in the original order
	 */
	private String getOriginalOrderOrVars(VarCombinationForIngredient varCombinationForIngredient) {
		List<String> simpleNames = varCombinationForIngredient.getCombination()
				.values().stream().map(CtNamedElement::getSimpleName).collect(Collectors.toList());
		return String.join(" ", simpleNames);
	}

	private void sortPotentialVarsByProb(Map<VarAccessWrapper, List<CtVariable>> mappedVars, NGrams ngrams) {

		MapCounter mc1 = ngrams.ngrams[1];
		Map<?, Double> probabilities = mc1.getProbabilies();
		logger.debug("Var probabilistics: " + probabilities);
		for (VarAccessWrapper keyVar : mappedVars.keySet()) {
			List<CtVariable> vars = mappedVars.get(keyVar);
			// Collections.sort(vars, (e1, e2) ->
			// (Double.compare(probabilities.get(e2.getSimpleName()),
			// probabilities.get(e1.getSimpleName()))));
			logger.debug("All Vars " + vars);
			Collections.sort(vars, new Comparator<CtVariable>() {

				@Override
				public int compare(CtVariable e1, CtVariable e2) {
					Double d2 = probabilities.get(e2.getSimpleName());
					Double d1 = probabilities.get(e1.getSimpleName());

					if (d2 == null && d1 == null)
						return 0;

					if (d2 == null) {
						logger.debug("nf2 " + e2.getSimpleName());
						return -1;
					}
					if (d1 == null) {
						logger.debug("nf1 " + e1.getSimpleName());
						return 1;
					}
					return Double.compare(d2, d1);
				}
			});
		}

	}

	public NGramManager getNgramManager() {
		return ngramManager;
	}

	public void setNgramManager(NGramManager ngramManager) {
		this.ngramManager = ngramManager;
	}

}
