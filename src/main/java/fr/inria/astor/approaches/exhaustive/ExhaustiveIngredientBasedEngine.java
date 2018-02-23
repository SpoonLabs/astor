package fr.inria.astor.approaches.exhaustive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.approaches.ingredientbased.IngredientBasedPlugInLoader;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.navigation.SuspiciousNavigationValues;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtCodeElement;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ExhaustiveIngredientBasedEngine extends ExhaustiveSearchEngine implements IngredientBasedApproach {

	protected IngredientSpace ingredientSpace = null;

	protected IngredientTransformationStrategy ingredientTransformationStrategy;

	public ExhaustiveIngredientBasedEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.pluginLoaded = new IngredientBasedPlugInLoader();
		ConfigurationProperties.properties.setProperty(ExtensionPoints.INGREDIENT_PROCESSOR.identifier, "statements");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "irr-statements");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier,
				SuspiciousNavigationValues.INORDER.toString());
	}

	@Override
	public void startEvolution() throws Exception {

		if (this.ingredientSpace == null) {
			this.ingredientSpace = IngredientBasedPlugInLoader.getIngredientPool(getTargetElementProcessors());
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
					.getSortedModificationPointsList(parentVariant)) {

				modifPointsAnalyzed++;

				log.info("\n MP (" + modifPointsAnalyzed + "/" + parentVariant.getModificationPoints().size()
						+ ") location to modify: " + modifPoint);

				// We create all operators to apply in the modifpoint
				List<OperatorInstance> operatorInstances = createInstancesOfOperators(
						(SuspiciousModificationPoint) modifPoint);

				//log.info("--- List of operators (" + operatorInstances.size() + ") : " + operatorInstances);

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
						if (ConfigurationProperties.getPropertyBool("stopfirst")) {
							this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
							log.debug(" modpoint analyzed " + modifPointsAnalyzed + ", operators " + operatorExecuted);
							return;
						}
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);

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
	protected List<OperatorInstance> createInstancesOfOperators(SuspiciousModificationPoint modificationPoint) {

		log.debug("Creating instance of MP: " + modificationPoint.getCodeElement().getShortRepresentation());

		List<OperatorInstance> ops = new ArrayList<>();
		AstorOperator[] operators = getOperatorSpace().values();
		for (AstorOperator astorOperator : operators) {

			if (astorOperator.canBeAppliedToPoint(modificationPoint)) {
				log.debug("Applying operator " + astorOperator + " from " + Arrays.toString(operators));
				List<OperatorInstance> operatorInstances = null;
				if (astorOperator.needIngredient()) {
					operatorInstances = createInstance(modificationPoint, astorOperator);

				} else {// if does not need ingredients
					operatorInstances = astorOperator.createOperatorInstance(modificationPoint);

				}
				if (operatorInstances != null)
					ops.addAll(operatorInstances);

			}
		}

		log.debug("\nNumber modififications to apply: " + ops.size());
		return ops;

	}

	public List<OperatorInstance> createInstance(SuspiciousModificationPoint modificationPoint,
			AstorOperator astorOperator) {

		List<OperatorInstance> ops = new ArrayList<>();
		List<CtCodeElement> ingredients = new ArrayList<>();

		if (astorOperator.needIngredient()) {
			if (astorOperator instanceof ReplaceOp) {
				String type = modificationPoint.getCodeElement().getClass().getSimpleName();
				ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);

			} else {
				ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement());

			}
			if (ingredients == null) {
				log.error("Zero ingredients mp: " + modificationPoint + ", op " + astorOperator);
				return ops;
			}
			log.debug("Number of ingredients " + ingredients.size());
			for (CtCodeElement ingredient : ingredients) {

				List<OperatorInstance> instances = astorOperator.createOperatorInstance(modificationPoint);

				List<Ingredient> ingredientsAfterTransformation = this.getIngredientTransformationStrategy()
						.transform(modificationPoint, new Ingredient(ingredient));

				// if
				// (!VariableResolver.fitInPlace(modificationPoint.getContextOfModificationPoint(),
				// ingredient))
				// continue;

				if (instances != null && instances.size() > 0) {

					for (Ingredient ingredientTransformed : ingredientsAfterTransformation) {

						OperatorInstance operatorInstance = createOperatorInstance(modificationPoint, astorOperator);
						operatorInstance.setModified(ingredientTransformed.getCode());
						operatorInstance.setIngredient(ingredientTransformed);
						ops.add(operatorInstance);
					}
				}
			}
		} else {

			OperatorInstance operatorInstance = createOperatorInstance(modificationPoint, astorOperator);

			ops.add(operatorInstance);
		}
		return ops;
	}

	public IngredientSpace getIngredientSpace() {
		return ingredientSpace;
	}

	public void setIngredientSpace(IngredientSpace ingredientSpace) {
		this.ingredientSpace = ingredientSpace;
	}

	@Override
	public IngredientSpace getIngredientPool() {
		return this.ingredientSpace;
	}

	@Override
	public void setIngredientPool(IngredientSpace ingredientPool) {
		this.ingredientSpace = ingredientPool;

	}

	@Override
	public IngredientTransformationStrategy getIngredientTransformationStrategy() {
		return this.ingredientTransformationStrategy;
	}

	@Override
	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
		this.ingredientTransformationStrategy = ingredientTransformationStrategy;
	}

	@Override
	public IngredientSearchStrategy getIngredientSearchStrategy() {
		return null;
	}

	@Override
	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy) {

	}

	public OperatorInstance createOperatorInstance(ModificationPoint mp, AstorOperator operator) {
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(mp.getCodeElement());
		operation.setOperationApplied(operator);
		operation.setModificationPoint(mp);
		operation.defineParentInformation(mp);

		return operation;
	}
}
