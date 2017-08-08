package fr.inria.astor.approaches.cardumen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtVariable;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class CardumenExhaustiveEngine extends CardumenApproach {

	public CardumenExhaustiveEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	public long totalIngredients = 0;
	public long totalBases = 0;
	public long totalAttempts = 0;
	public long totalIngredientsCutted = 0;
	public long attemptsCutted = 0;

	@Override
	public void startEvolution() throws Exception {

		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;

		int v = 0;

		totalIngredients = 0;
		totalBases = 0;
		for (ProgramVariant parentVariant : variants) {

			log.debug("\n****\nanalyzing variant #" + (++v) + " out of " + variants.size());
			// We analyze each modifpoint of the variant i.e. suspicious
			// statement
			for (ModificationPoint modifPoint : parentVariant.getModificationPoints()) {
				// We create all operators to apply in the modifpoint

				AstorOperator pointOperation = this.getOperatorSpace().getOperators().get(0);

				try {
					log.info("exa: mod_point " + modifPoint);
				} catch (Exception e) {
				}
				EfficientIngredientStrategy estrategy = (EfficientIngredientStrategy) this.getIngredientStrategy();

				List<CtCodeElement> elements = estrategy.getNotExhaustedBaseElements(modifPoint, pointOperation);

				if (elements == null) {
					continue;
				}

				totalBases += elements.size();
				for (CtCodeElement baseIngredient : elements) {

					long nrIngredients[] = getNrIngredients(modifPoint, baseIngredient);

					if ((long) nrIngredients[0] != nrIngredients[1]) {
						// log.debug("Different " + maxValues[0] + " " +
						// countedLimited);
						// log.debug("");
						attemptsCutted++;
					}

					totalIngredients += nrIngredients[0];
					totalIngredientsCutted += nrIngredients[1];

					log.debug("-nrIng-" + Arrays.toString(nrIngredients));
					// Commented due to ingredients are cutted
					/*
					 * List<Ingredient> ingredientsAfterTransformation =
					 * estrategy.getInstancesFromBase(modifPoint,
					 * pointOperation, new Ingredient(baseIngredient)); /* if
					 * (ingredientsAfterTransformation != null) { int
					 * conmbinationOfBaseIngredient =
					 * ingredientsAfterTransformation.size(); totalIngredients
					 * += conmbinationOfBaseIngredient; }
					 */

					totalAttempts += 1;
				}
			}
		}
		log.info("totalmp: " + getVariants().get(0).getModificationPoints().size());
		log.info("totalBases: " + totalBases);
		log.info("totalAttempts: " + totalAttempts);
		log.info("totalCutsAttempts: " + attemptsCutted);
		log.info("totalIngredients: " + totalIngredients);
		log.info("totalCutIngredients: " + totalIngredientsCutted);

	}

	private long[] getNrIngredients(ModificationPoint modificationPoint, CtCodeElement baseIngredient) {
		CtCodeElement codeElementToModifyFromBase = (CtCodeElement) baseIngredient;

		if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
			return new long[] { 0, 0 };
		}

		VarMapping mapping = VariableResolver.mapVariablesFromContext(modificationPoint.getContextOfModificationPoint(),
				codeElementToModifyFromBase);

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
			long maxVarAnalyzed = (mapped.size() > maxPerVarLimit) ? (long) maxPerVarLimit : mapped.size();
			log.debug("-sizes--" + mapped.size() + " " + maxPerVarLimit);
			allCombinationl = allCombinationl * (int) maxVarAnalyzed;

		}
		log.debug("-allComb--" + allCombinationl);

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
				List<OperatorInstance> instances = astorOperator.createOperatorInstance(modificationPoint);
				if (instances != null && instances.size() > 0) {
					ops.addAll(instances);
				}
			}
		}

		return ops;

	}

	@Override
	public void showResults() {
		super.showResults();
		log.info("\ntotalmp: " + getVariants().get(0).getModificationPoints().size());
		log.info("\ntotalBases: " + totalBases);
		log.info("\ntotalAttempts: " + totalAttempts);
		log.info("totalCutsAttempts: " + attemptsCutted);
		log.info("\ntotalIngredients: " + totalIngredients);
		log.info("\ntotalCutsIngredients: " + totalIngredientsCutted);
	}

}
