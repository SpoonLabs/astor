package fr.inria.astor.core.loop.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SpecialStatementFixSpaceProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.MapCounter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ProbabilisticTransformationStrategy implements IngredientTransformationStrategy {

	protected Logger logger = Logger.getLogger(ProbabilisticTransformationStrategy.class.getName());

	protected Map<String, NGrams> ngramsSplitted = null;
	protected NGrams ngglobal = null;

	public ProbabilisticTransformationStrategy() {
		super();

	}

	public ProbabilisticTransformationStrategy(Map<String, NGrams> ngramsSplitted, NGrams ngramsGlobal) {
		super();
		this.ngramsSplitted = ngramsSplitted;
		this.ngglobal = ngramsGlobal;
	}

	public void calculateGramsProbs() throws JSAPException {
		logger.debug("Calculating N-grams");
		this.ngglobal = new NGrams();
		this.ngramsSplitted = new HashMap<>();

		AbstractFixSpaceProcessor<?> elementProcessor = new SpecialStatementFixSpaceProcessor();
		Boolean mustCloneOriginalValue = ConfigurationProperties.getPropertyBool("duplicateingredientsinspace");
		// Forcing to duplicate
		ConfigurationProperties.setProperty("duplicateingredientsinspace", "true");

		List<CtType<?>> all = MutationSupporter.getFactory().Type().getAll();

		GramProcessor pt = new GramProcessor(elementProcessor);
		for (CtType<?> ctType : all) {
			NGrams ng = pt.calculateGrams4Class(ctType);
			ngramsSplitted.put(ctType.getQualifiedName(), ng);

		}

		ngglobal = pt.calculateGlobal(all);

		// reset property clone
		ConfigurationProperties.setProperty("duplicateingredientsinspace", Boolean.toString(mustCloneOriginalValue));

	}

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient baseIngredient) {

		if (ngramsSplitted == null || this.ngglobal == null) {
			logger.debug("Initializing probabilistics");
			try {
				calculateGramsProbs();
			} catch (JSAPException e) {
				logger.error(e);
				return null;
			}
		}

		List<Ingredient> result = new ArrayList<>();

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
				result.add(new Ingredient(codeElementToModifyFromBase));

			} else {// We have mappings between variables
				logger.debug("Ingredient before transformation: " + baseIngredient);

				List<VarCombinationForIngredient> allCombinations = findAllVarMappingCombinationUsingProbab(
						mapping.getMappedVariables(), modificationPoint);
				
				//logger.debug("--mp "+modificationPoint);
				//logger.debug("--mpe "+modificationPoint.getCodeElement());
				//logger.debug("--baseIng "+baseIngredient);
				//logger.debug("== "+allCombinations);
				
				if (allCombinations.size() > 0) {

					for (VarCombinationForIngredient varCombinationForIngredient : allCombinations) {

						DynamicIngredient ding = new DynamicIngredient(varCombinationForIngredient, mapping,
								codeElementToModifyFromBase);
						result.add(ding);
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

		return result;
	}

	public List<VarCombinationForIngredient> findAllVarMappingCombinationUsingProbab(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars, ModificationPoint mpoint) {

		NGrams ngrams = this.ngramsSplitted.get(mpoint.getCtClass().getQualifiedName());

		sortPotentialVarsByProb(mappedVars, ngrams);

		List<Map<String, CtVariable>> allWithoutOrder = VariableResolver.findAllVarMappingCombination(mappedVars,
				false);

		List<VarCombinationForIngredient> allCom = new ArrayList<>();
		for (Map<String, CtVariable> varMapping : allWithoutOrder) {

			VarCombinationForIngredient varCombinationWrapper = new VarCombinationForIngredient(varMapping);

			int sizeCombination = varCombinationWrapper.getSize();
			MapCounter gramCounterSize = ngrams.ngrams[sizeCombination];

			MapCounter gramCounterSizeGlobal = this.ngglobal.ngrams[sizeCombination];
			gramCounterSizeGlobal.getProbabilies();
			if (gramCounterSize == null) { // Ingredient size bigger than all
											// statements from the class
				// logger.debug("Map is null for " + gramCounterSize);
				// logger.debug("Using global");
				gramCounterSize = gramCounterSizeGlobal;

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
			if (probability == null) {
				// logger.debug("Error null: " +
				// varCombinationWrapper.getCombinationString());
			}
			varCombinationWrapper.setProbality((probability != null) ? probability : 0);
			allCom.add(varCombinationWrapper);

		}

		allCom.sort((e1, e2) -> Double.compare(e2.getProbality(), e1.getProbality()));
		logger.debug("Number combination sorted : " + allCom.size() + " over " + allWithoutOrder.size());
		if (allCom.size() > 0)
			logger.debug("---Max prob: " + allCom.get(0).getProbality());
		return allCom;
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

}
