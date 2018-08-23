package fr.inria.astor.approaches.cardumen;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.RandomSelectionTransformedIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * Exhaustive Search Engine For statistics
 * 
 * @author Matias Martinez
 * 
 */
public class CardumenExhaustiveEngine4Stats extends CardumenApproach {

	public CardumenExhaustiveEngine4Stats(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	public long totalIngredients = 0;
	public long totalmp = 0;
	public long totalBases = 0;
	public long totalAttempts = 0;
	public long totalIngredientsCutted = 0;
	public long attemptsCutted = 0;
	public long totalBasesWithZeros = 0;

	@Override
	public void startEvolution() throws Exception {

		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;

		int v = 0;

		totalIngredients = 0;
		totalBases = 0;
		totalBasesWithZeros = 0;
		for (ProgramVariant parentVariant : variants) {

			log.debug("\n****\nanalyzing variant #" + (++v) + " out of " + variants.size());
			// We analyze each modifpoint of the variant i.e. suspicious
			// statement

			totalmp = parentVariant.getModificationPoints().size();
			int mp = 0;
			int total = parentVariant.getModificationPoints().size();
			for (ModificationPoint modifPoint : parentVariant.getModificationPoints()) {
				// We create all operators to apply in the modifpoint

				AstorOperator pointOperation = this.getOperatorSpace().getOperators().get(0);

				RandomSelectionTransformedIngredientStrategy estrategy = (RandomSelectionTransformedIngredientStrategy) this
						.getIngredientSearchStrategy();

				List<Ingredient> baseElements = estrategy.getNotExhaustedBaseElements(modifPoint, pointOperation);

				if (baseElements == null) {
					continue;
				}
				mp++;
				totalBases += baseElements.size();
				Stats.currentStat.getIngredientsStats()
						.addSize(Stats.currentStat.getIngredientsStats().ingredientSpaceSize, baseElements.size());
				int base = 0;

				log.info("###\nMP  " + modifPoint + "| code: " + modifPoint.getCodeElement().toString() + "| ("
						+ ((CtExpression) modifPoint.getCodeElement()).getType() + ") " + ": " + baseElements.size());

				for (Ingredient ingredient : baseElements) {
					base++;
					CtElement baseIngredient = ingredient.getCode();
					log.debug("\nMP:  (" + (mp) + "/" + total + ") " + modifPoint + "|| code: "
							+ modifPoint.getCodeElement().toString() + "|| "
							+ modifPoint.getCodeElement().getClass().getCanonicalName() + " ("
							+ ((CtExpression) modifPoint.getCodeElement()).getType().getQualifiedName() + ") \nBase: ("
							+ base + "/" + baseElements.size() + ") " + baseIngredient.getClass().getCanonicalName()
							+ " (" + ((CtExpression) baseIngredient).getType().getQualifiedName() + ")" + "*-*-*"
							+ baseIngredient

					);
					long[] nrIngredients = getNrIngredients(modifPoint, baseIngredient);

					long spacesize = nrIngredients[0];
					long cuttedspacesize = nrIngredients[1];
					Stats.currentStat.getIngredientsStats().addSize(
							Stats.currentStat.getIngredientsStats().combinationByIngredientSize, (cuttedspacesize));
					if (nrIngredients[0] == 0) {
						totalBasesWithZeros++;
					}

					if ((long) nrIngredients[0] != nrIngredients[1]) {
						attemptsCutted++;
					}

					totalIngredients += spacesize;
					totalIngredientsCutted += cuttedspacesize;

					log.debug("-nrIng-" + Arrays.toString(nrIngredients));

					totalAttempts += 1;
				}
			}
		}
		/*
		 * log.info("totalmp: " + this.totalmp); log.info("totalBases: " +
		 * totalBases); log.info("totalAttempts: " + totalAttempts);
		 * log.info("totalCutsAttempts: " + attemptsCutted);
		 * log.info("totalIngredients: " + totalIngredients);
		 * log.info("totalCutIngredients: " + totalIngredientsCutted);
		 * log.info("totalBasesWithZeros: " + totalBasesWithZeros);
		 */
	}

	public static long[] getNrIngredients(ModificationPoint modificationPoint, CtElement codeElementToModifyFromBase) {

		VarMapping mapping = VariableResolver.mapVariablesFromContext(modificationPoint.getContextOfModificationPoint(),
				codeElementToModifyFromBase);
		return getNrIngredients(modificationPoint, mapping);
	}

	public static long[] getNrIngredients(ModificationPoint modificationPoint, VarMapping mapping) {

		if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
			return new long[] { 0, 0 };
		}

		log.debug("Mapping results " + mapping);

		// if we map all variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				return new long[] { 1, 1 };

			} else {// We have mappings between variables

				Map<VarAccessWrapper, List<CtVariable>> mappedVars = mapping.getMappedVariables();
				List<VarAccessWrapper> varsNamesToCombine = new ArrayList<>(mappedVars.keySet());

				Number[] maxValues = VariableResolver.getMaxCombination(mappedVars, varsNamesToCombine);

				long countedLimited = countLimited(mappedVars, (double) maxValues[1]);

				return new long[] { (long) maxValues[0], countedLimited };
			}

		} else {
			// log.debug("not mapped vars: "+ mapping.getNotMappedVariables());
		}
		return new long[] { 0, 0 };
	}

	public static long countLimited(Map<VarAccessWrapper, List<CtVariable>> mappedVars,
			double maxPerVarLimit /* = (double) maxValues[1]; */) {

		if (mappedVars.isEmpty()) {
			return 0;
		}

		List<VarAccessWrapper> varsNamesToCombine = new ArrayList<>(mappedVars.keySet());

		long allCombinationl = 1;

		Set<String> mappedV = new HashSet<>();

		for (VarAccessWrapper currentVar : varsNamesToCombine) {

			if (mappedV.contains(currentVar.getVar().getVariable().getSimpleName())) {
				continue;
			}
			mappedV.add(currentVar.getVar().getVariable().getSimpleName());

			List<CtVariable> mapped = mappedVars.get(currentVar);
			if (mapped.isEmpty()) {
				log.debug("===empty");
				continue;
			}
			double maxVarRounded = Math.ceil(maxPerVarLimit);
			long maxVarAnalyzed = (mapped.size() > maxVarRounded) ? (long) maxVarRounded : mapped.size();
			log.debug("-sizes--" + mapped.size() + " " + maxPerVarLimit);
			allCombinationl = allCombinationl * (int) maxVarAnalyzed;

		}
		log.debug("-allComb all--" + allCombinationl);

		int maxNumberCombinations = ConfigurationProperties.getPropertyInt("maxVarCombination");

		if (allCombinationl > maxNumberCombinations) {
			return maxNumberCombinations;
		}
		log.debug("-allComb cutted--" + allCombinationl);

		return allCombinationl;
	}

	/**
	 * @param modificationPoint
	 * @return
	 */
	protected List<OperatorInstance> createInstancesOfOperators(SuspiciousModificationPoint modificationPoint) {
		List<OperatorInstance> ops = new ArrayList<>();
		AstorOperator[] operators = getOperatorSpace().values();
		for (AstorOperator astorOperator : operators) {
			if (astorOperator.canBeAppliedToPoint(modificationPoint)) {
				List<OperatorInstance> instances = astorOperator.createOperatorInstances(modificationPoint);
				if (instances != null && instances.size() > 0) {
					ops.addAll(instances);
				}
			}
		}

		return ops;

	}

	public void showCardumenStats() {
		log.info("\ntotalmp: " + getVariants().get(0).getModificationPoints().size());
		log.info("\ntotalBases: " + totalBases);
		log.info("\ntotalAttempts: " + totalAttempts);
		log.info("\ntotalCutsAttempts: " + attemptsCutted);
		log.info("\ntotalIngredients: " + totalIngredients);
		log.info("\ntotalCutsIngredients: " + totalIngredientsCutted);
		log.info("\ntotalBasesWithZeros: " + totalBasesWithZeros);
		log.info("\ning:" + Stats.currentStat.getIngredientsStats()
				.getJsonObject(Stats.currentStat.getIngredientsStats().ingredientSpaceSize));
		log.info("\ncomb:" + Stats.currentStat.getIngredientsStats()
				.getJsonObject(Stats.currentStat.getIngredientsStats().combinationByIngredientSize));

	}

	@SuppressWarnings("unchecked")
	public JSONObject getOutputJSON() {

		JSONObject ob = new JSONObject();

		ob.put("totalmp", totalmp);
		ob.put("totalBases", totalBases);
		ob.put("totalAttempts", totalAttempts);
		ob.put("totalCutsAttempts", attemptsCutted);
		ob.put("totalIngredients", totalIngredients);
		ob.put("totalCutsIngredients", totalIngredientsCutted);
		ob.put("totalBasesWithZeros", totalBasesWithZeros);
		ob.put("ingredients", Stats.currentStat.getIngredientsStats()
				.getJsonObject(Stats.currentStat.getIngredientsStats().ingredientSpaceSize));
		ob.put("combinations", Stats.currentStat.getIngredientsStats()
				.getJsonObject(Stats.currentStat.getIngredientsStats().combinationByIngredientSize));

		return ob;
	}

	@Override
	public void atEnd() {
		super.atEnd();
		this.showCardumenStats();
		JSONObject jsonob = getOutputJSON();
		outputToJSon(jsonob);
	}

	private void outputToJSon(JSONObject jsonob) {
		System.out.println("\njsonoutput=" + jsonob);
		String output = this.getProjectFacade().getProperties().getWorkingDirRoot();
		String filename = "exastats";
		String absoluteFileName = output + "/" + filename + ".json";
		try (FileWriter file = new FileWriter(absoluteFileName)) {

			file.write(jsonob.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + absoluteFileName);
			log.info(filename + ":" + jsonob.toJSONString());

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}
	}

	public void saveJSON() {

	}

}
