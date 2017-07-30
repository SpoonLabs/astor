package fr.inria.astor.approaches.cardumen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtCodeElement;

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

	int totalIngredients = 0;
	int totalBases = 0;
	int totalAttempts = 0;

	@Override
	public void startEvolution() throws Exception {

		ConfigurationProperties.setProperty("maxVarCombination", "100000000");

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
					log.info("mod_point " + modifPoint);
				} catch (Exception e) {
				}
				EfficientIngredientStrategy estrategy = (EfficientIngredientStrategy) this.getIngredientStrategy();

				List<CtCodeElement> elements = estrategy.getNotExhaustedBaseElements(modifPoint, pointOperation);

				if (elements == null) {
					continue;
				}

				totalBases += elements.size();
				for (CtCodeElement baseIngredient : elements) {
					List<Ingredient> ingredientsAfterTransformation = estrategy.getInstancesFromBase(modifPoint,
							pointOperation, new Ingredient(baseIngredient));
					if (ingredientsAfterTransformation != null) {
						int conmbinationOfBaseIngredient = ingredientsAfterTransformation.size();
						totalIngredients += conmbinationOfBaseIngredient;
					}
					totalAttempts += 1;
				}
			}
		}
		log.debug("totalmp: " + getVariants().get(0).getModificationPoints().size());
		log.debug("totalBases: " + totalBases);
		log.debug("totalAttempts: " + totalAttempts);
		log.debug("totalIngredients: " + totalIngredients);

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
		log.debug("\ntotalmp: " + getVariants().get(0).getModificationPoints().size());
		log.debug("\ntotalBases: " + totalBases);
		log.debug("\ntotalAttempts: " + totalAttempts);
		log.debug("\ntotalIngredients: " + totalIngredients);
	}

}
