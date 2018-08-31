package fr.inria.astor.core.ingredientbased;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.ExhaustiveSearchEngine;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.IngredientBasedOperator;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ExhaustiveIngredientBasedEngine extends ExhaustiveSearchEngine implements IngredientBasedApproach {

	protected IngredientPool ingredientSpace = null;

	protected IngredientTransformationStrategy ingredientTransformationStrategy;

	public ExhaustiveIngredientBasedEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		ConfigurationProperties.properties.setProperty(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "statements");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "irr-statements");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier,
				SuspiciousNavigationValues.INORDER.toString());
	}

	@Override
	public void startEvolution() throws Exception {

		if (this.ingredientSpace == null) {
			this.ingredientSpace = IngredientBasedEvolutionaryRepairApproachImpl
					.getIngredientPool(getTargetElementProcessors());
		}
		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		// For each variant (one is enough)
		int maxMinutes = ConfigurationProperties.getPropertyInt("maxtime");
		int maxGenerations = ConfigurationProperties.getPropertyInt("maxGeneration");
		// for stats
		int modifPointsAnalyzed = 0;
		int operatorExecuted = 0;

		getIngredientSpace().defineSpace(originalVariant);

		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			for (ModificationPoint modifPoint : this.getSuspiciousNavigationStrategy()
					.getSortedModificationPointsList(parentVariant.getModificationPoints())) {

				modifPointsAnalyzed++;

				log.info("\n MP (" + modifPointsAnalyzed + "/" + parentVariant.getModificationPoints().size()
						+ ") location to modify: " + modifPoint);

				// We create all operators to apply in the modifpoint
				List<OperatorInstance> operatorInstances = createInstancesOfOperators(
						(SuspiciousModificationPoint) modifPoint);

				// log.info("--- List of operators (" + operatorInstances.size()
				// + ") : " + operatorInstances);

				if (operatorInstances == null || operatorInstances.isEmpty())
					continue;

				for (OperatorInstance pointOperation : operatorInstances) {

					operatorExecuted++;

					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(pointOperation));

					applyNewMutationOperationToSpoonElement(pointOperation);

					log.debug("Operator:\n " + pointOperation);
					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);

					if (solution) {
						log.info("Solution found " + getSolutions().size());
						this.solutions.add(solutionVariant);

					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);

					if (!this.solutions.isEmpty() && ConfigurationProperties.getPropertyBool("stopfirst")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						log.debug(" modpoint analyzed " + modifPointsAnalyzed + ", operators " + operatorExecuted);
						return;
					}

					if (!belowMaxTime(dateInitEvolution, maxMinutes)) {
						this.setOutputStatus(AstorOutputStatus.TIME_OUT);
						log.debug("Max time reached");
						return;
					}

					if (maxGenerations <= operatorExecuted) {

						this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
						log.info("Stop-Max operator Applied " + operatorExecuted);
						log.info("modpoint:" + modifPointsAnalyzed + ":all:" + totalmodfpoints + ":operators:"
								+ operatorExecuted);
						return;
					}

					if (this.getSolutions().size() >= ConfigurationProperties.getPropertyInt("maxnumbersolutions")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						log.debug("Stop-Max solutions reached " + operatorExecuted);
						log.debug("modpoint:" + modifPointsAnalyzed + ":all:" + totalmodfpoints + ":operators:"
								+ operatorExecuted);
						return;
					}
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operatorExecuted);

	}

	/**
	 * @param modificationPoint
	 * @return
	 */
	@Override
	protected List<OperatorInstance> createInstancesOfOperators(SuspiciousModificationPoint modificationPoint) {

		log.debug("Creating instance of MP: " + modificationPoint.getCodeElement().getShortRepresentation());

		List<OperatorInstance> ops = new ArrayList<>();
		AstorOperator[] operators = getOperatorSpace().values();
		for (AstorOperator astorOperator : operators) {

			if (astorOperator.canBeAppliedToPoint(modificationPoint)) {
				log.debug("Applying operator " + astorOperator + " from " + Arrays.toString(operators));
				List<OperatorInstance> operatorInstances = null;
				if (astorOperator.needIngredient()) {
					IngredientBasedOperator ingbasedoperator = (IngredientBasedOperator) astorOperator;
					try {
						operatorInstances = createIngredientOpInstance(modificationPoint, ingbasedoperator);
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Error creating op instances: \n" + e);
					}

				} else {// if does not need ingredients
					operatorInstances = astorOperator.createOperatorInstances(modificationPoint);

				}
				if (operatorInstances != null)
					ops.addAll(operatorInstances);
			}
		}

		log.debug("\nNumber modififications to apply: " + ops.size());
		return ops;

	}

	@SuppressWarnings("unchecked")
	public List<OperatorInstance> createIngredientOpInstance(SuspiciousModificationPoint modificationPoint,
			IngredientBasedOperator astorOperator) throws Exception {

		List<OperatorInstance> ops = new ArrayList<>();
		List<Ingredient> ingredients = new ArrayList<>();

		if (astorOperator instanceof ReplaceOp) {// TODO
			String type = ingredientSpace.getType(new Ingredient(modificationPoint.getCodeElement())).toString();

			ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);

		} else {
			ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement());

		}

		if (ingredients == null) {
			log.error("Zero ingredients mp: " + modificationPoint + ", op " + astorOperator);
			return ops;
		}
		log.debug("Number of ingredients " + ingredients.size());
		for (Ingredient ingredient : ingredients) {

			List<OperatorInstance> operatorInstances = null;
			operatorInstances = astorOperator.createOperatorInstances(modificationPoint, ingredient,
					this.ingredientTransformationStrategy);

			ops.addAll(operatorInstances);

		}

		return ops;

	}

	public IngredientPool getIngredientSpace() {
		return ingredientSpace;
	}

	public void setIngredientSpace(IngredientPool ingredientSpace) {
		this.ingredientSpace = ingredientSpace;
	}

	@Override
	public IngredientPool getIngredientPool() {
		return this.ingredientSpace;
	}

	@Override
	public void setIngredientPool(IngredientPool ingredientPool) {
		this.ingredientSpace = ingredientPool;

	}

	@Override
	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
		this.ingredientTransformationStrategy = ingredientTransformationStrategy;
	}

	@Override
	public IngredientTransformationStrategy getIngredientTransformationStrategy() {
		return ingredientTransformationStrategy;
	}

	@Override
	public IngredientSearchStrategy getIngredientSearchStrategy() {
		return null;
	}

	@Override
	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy) {

	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientPool() throws JSAPException, Exception {
		List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetElementProcessors();
		// The ingredients for build the patches
		IngredientPool ingredientspace = IngredientBasedEvolutionaryRepairApproachImpl
				.getIngredientPool(ingredientProcessors);

		this.setIngredientPool(ingredientspace);

	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientSearchStrategy() throws Exception {
		this.setIngredientSearchStrategy(
				IngredientBasedEvolutionaryRepairApproachImpl.retrieveIngredientSearchStrategy(getIngredientPool()));
	}

	protected void loadIngredientTransformationStrategy() throws Exception {

		IngredientTransformationStrategy ingredientTransformationStrategyLoaded = IngredientBasedEvolutionaryRepairApproachImpl
				.retrieveIngredientTransformationStrategy();
		this.setIngredientTransformationStrategy(ingredientTransformationStrategyLoaded);
	}

	@Override
	protected void setParticularStats(PatchHunkStats hunk, OperatorInstance genOperationInstance) {
		// TODO Auto-generated method stub
		super.setParticularStats(hunk, genOperationInstance);
		hunk.getStats().put(HunkStatEnum.INGREDIENT_SCOPE,
				((genOperationInstance.getIngredientScope() != null) ? genOperationInstance.getIngredientScope()
						: "-"));

		if (genOperationInstance.getIngredient() != null
				&& genOperationInstance.getIngredient().getDerivedFrom() != null)
			hunk.getStats().put(HunkStatEnum.INGREDIENT_PARENT, genOperationInstance.getIngredient().getDerivedFrom());

	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();
		this.loadIngredientPool();
		this.loadIngredientSearchStrategy();
		this.loadIngredientTransformationStrategy();
	}

}
