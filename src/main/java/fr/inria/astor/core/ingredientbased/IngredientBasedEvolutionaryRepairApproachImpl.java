package fr.inria.astor.core.ingredientbased;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.deeprepair.CloneIngredientSearchStrategy;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.EvolutionarySearchEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.ProbabilisticIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.SimpleRandomSelectionIngredientStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.GlobalBasicIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.LocalIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.PackageBasicFixSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.ClusterIngredientTransformation;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.NoIngredientTransformationWithCheck;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.ProbabilisticTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.RandomTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.IngredientBasedOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;

/**
 * Ingredient based-repair approach.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class IngredientBasedEvolutionaryRepairApproachImpl extends EvolutionarySearchEngine
		implements IngredientBasedApproach {

	protected IngredientSearchStrategy ingredientSearchStrategy;
	protected IngredientTransformationStrategy ingredientTransformationStrategy;
	protected IngredientPool ingredientPool;

	protected List<TargetElementProcessor<?>> targetIngredientProcessors = null;

	public IngredientBasedEvolutionaryRepairApproachImpl(MutationSupporter mutatorExecutor,
			ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	/**
	 * By default, it initializes the spoon model. It should not be created before.
	 * Otherwise, an exception occurs.
	 * 
	 * @param suspicious
	 * @throws Exception
	 */
	public void initPopulation(List<SuspiciousCode> suspicious) throws Exception {

		super.initPopulation(suspicious);

		if (this.ingredientSearchStrategy != null) {
			this.ingredientSearchStrategy.getIngredientSpace().defineSpace(originalVariant);
		}

	}

	@Override
	public OperatorInstance createOperatorInstanceForPoint(ModificationPoint modificationPoint)
			throws IllegalAccessException {
		SuspiciousModificationPoint suspModificationPoint = (SuspiciousModificationPoint) modificationPoint;

		AstorOperator operatorSelected = operatorSelectionStrategy.getNextOperator(suspModificationPoint);

		if (operatorSelected == null) {
			log.debug("Operation Null");
			return null;
		}

		List<OperatorInstance> operatorInstances = null;
		if (operatorSelected.canBeAppliedToPoint(modificationPoint)) {
			if (operatorSelected.needIngredient()) {
				IngredientBasedOperator ingbasedapproach = (IngredientBasedOperator) operatorSelected;

				Ingredient ingredient = this.ingredientSearchStrategy.getFixIngredient(modificationPoint,
						operatorSelected);

				if (ingredient == null) {
					return null;
				}
				operatorInstances = ingbasedapproach.createOperatorInstances(modificationPoint, ingredient,
						this.ingredientTransformationStrategy);

			} else {
				operatorInstances = operatorSelected.createOperatorInstances(modificationPoint);
			}

			return selectRandomly(operatorInstances);
		} else {
			return null;
		}
	}

	@Override
	public void printFinalStatus() {
		super.printFinalStatus();
		if (ConfigurationProperties.getPropertyBool("logsattemps")) {

			if (this.ingredientSearchStrategy != null
					&& this.ingredientSearchStrategy.getIngredientSpace() instanceof ExpressionTypeIngredientSpace) {
				ExpressionTypeIngredientSpace space = (ExpressionTypeIngredientSpace) this.ingredientSearchStrategy
						.getIngredientSpace();
				log.info("Total mod points: " + this.variants.get(0).getModificationPoints().size());
				space.toJSON(this.getProjectFacade().getProperties().getWorkingDirForSource());
				Stats.currentStat.getIngredientsStats().toJSON(
						this.getProjectFacade().getProperties().getWorkingDirRoot(),
						Stats.currentStat.getIngredientsStats().ingredientSpaceSize, "ingredientSpaceSize");
				Stats.currentStat.getIngredientsStats().toJSON(
						this.getProjectFacade().getProperties().getWorkingDirRoot(),
						Stats.currentStat.getIngredientsStats().combinationByIngredientSize,
						"combinationsTemplatesingredientSpaceSize");
			}
		}
	}

	protected OperatorSelectionStrategy createOperationSelectionStrategy(String opSelectionStrategyClassName,
			OperatorSpace space) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(opSelectionStrategyClassName);
			object = classDefinition.getConstructor(OperatorSpace.class).newInstance(space);
		} catch (Exception e) {
			log.error("Loading strategy " + opSelectionStrategyClassName + " --" + e);
			throw new Exception("Loading strategy: " + e);
		}
		if (object instanceof OperatorSelectionStrategy)
			return (OperatorSelectionStrategy) object;
		else
			throw new Exception("The strategy " + opSelectionStrategyClassName + " does not extend from "
					+ OperatorSelectionStrategy.class.getName());
	}

	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
		this.ingredientTransformationStrategy = ingredientTransformationStrategy;
	}

	public IngredientTransformationStrategy getIngredientTransformationStrategy() throws Exception {
		return this.ingredientTransformationStrategy;
	}

	public IngredientSearchStrategy getIngredientSearchStrategy() {
		return ingredientSearchStrategy;
	}

	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy) {
		this.ingredientSearchStrategy = ingredientStrategy;
	}

	public IngredientPool getIngredientPool() {
		return ingredientPool;
	}

	public void setIngredientPool(IngredientPool ingredientPool) {
		this.ingredientPool = ingredientPool;
	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientPool() throws JSAPException, Exception {
		List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetIngredientProcessors();
		// The ingredients for build the patches
		IngredientPool ingredientspace = getIngredientPool(ingredientProcessors);

		this.setIngredientPool(ingredientspace);

	}

	public static IngredientPool getIngredientPool(List<TargetElementProcessor<?>> ingredientProcessors)
			throws JSAPException, Exception {
		String scope = ConfigurationProperties.properties.getProperty("scope");
		IngredientPool ingredientspace = null;
		if ("global".equals(scope)) {
			ingredientspace = (new GlobalBasicIngredientSpace(ingredientProcessors));
		} else if ("package".equals(scope)) {
			ingredientspace = (new PackageBasicFixSpace(ingredientProcessors));
		} else if ("local".equals(scope) || "file".equals(scope)) {
			ingredientspace = (new LocalIngredientSpace(ingredientProcessors));
		} else {
			ingredientspace = (IngredientPool) PlugInLoader.loadPlugin(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE,
					new Class[] { List.class }, new Object[] { ingredientProcessors });

		}
		return ingredientspace;
	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientSearchStrategy() throws Exception {

		IngredientPool ingredientspace = this.getIngredientPool();

		IngredientSearchStrategy ingStrategy = retrieveIngredientSearchStrategy(ingredientspace);

		this.setIngredientSearchStrategy(ingStrategy);

	}

	public static IngredientSearchStrategy retrieveIngredientSearchStrategy(IngredientPool ingredientspace)
			throws Exception {
		IngredientSearchStrategy ingStrategy = null;

		String ingStrategySt = ConfigurationProperties.properties
				.getProperty(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier);

		if (ingStrategySt != null) {

			if (ingStrategySt.equals("uniform-random")) {
				ingStrategy = new SimpleRandomSelectionIngredientStrategy(ingredientspace);
				// new RandomSelectionTransformedIngredientStrategy(ingredientspace);
			} else if (ingStrategySt.equals("name-probability-based")) {
				ingStrategy = new ProbabilisticIngredientStrategy(ingredientspace);
			} else if (ingStrategySt.equals("code-similarity-based")) {
				ingStrategy = new CloneIngredientSearchStrategy(ingredientspace);
			} else {
				ingStrategy = (IngredientSearchStrategy) PlugInLoader.loadPlugin(
						ExtensionPoints.INGREDIENT_SEARCH_STRATEGY, new Class[] { IngredientPool.class },
						new Object[] { ingredientspace });
			}
		} else {
			ingStrategy = new SimpleRandomSelectionIngredientStrategy(ingredientspace);
			// new RandomSelectionTransformedIngredientStrategy(ingredientspace);
		}
		return ingStrategy;
	}

	protected void loadIngredientTransformationStrategy() throws Exception {

		IngredientTransformationStrategy ingredientTransformationStrategyLoaded = retrieveIngredientTransformationStrategy();

		this.setIngredientTransformationStrategy(ingredientTransformationStrategyLoaded);
	}

	public static IngredientTransformationStrategy retrieveIngredientTransformationStrategy() throws Exception {
		IngredientTransformationStrategy ingredientTransformationStrategyLoaded = null;
		String ingredientTransformationStrategy = ConfigurationProperties.properties
				.getProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier);

		if (ingredientTransformationStrategy == null) {
			ingredientTransformationStrategyLoaded = (new NoIngredientTransformationWithCheck());
		} else {// there is a value
			if (ingredientTransformationStrategy.equals("no-transformation")) {
				ingredientTransformationStrategyLoaded = (new NoIngredientTransformationWithCheck());
			} else if (ingredientTransformationStrategy.equals("random-variable-replacement")) {
				ingredientTransformationStrategyLoaded = (new RandomTransformationStrategy());
			} else if (ingredientTransformationStrategy.equals("name-cluster-based")) {
				ingredientTransformationStrategyLoaded = (new ClusterIngredientTransformation());
			} else if (ingredientTransformationStrategy.equals("name-probability-based")) {
				ingredientTransformationStrategyLoaded = (new ProbabilisticTransformationStrategy());
			} else {
				ingredientTransformationStrategyLoaded = ((IngredientTransformationStrategy) PlugInLoader
						.loadPlugin(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY));
			}
		}
		return ingredientTransformationStrategyLoaded;
	}

	public void loadTargetIngredientElement() throws Exception {

		ExtensionPoints extensionPointpointTargetIngredient = null;
		if (ConfigurationProperties.hasProperty(ExtensionPoints.TARGET_INGREDIENT_CODE_PROCESSOR.identifier))
			extensionPointpointTargetIngredient = ExtensionPoints.TARGET_INGREDIENT_CODE_PROCESSOR;
		else
			extensionPointpointTargetIngredient = ExtensionPoints.TARGET_CODE_PROCESSOR;

		List<TargetElementProcessor<?>> loadedTargetElementProcessors = super.loadTargetElements(
				extensionPointpointTargetIngredient);
		this.setTargetIngredientProcessors(loadedTargetElementProcessors);

	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();
		this.loadTargetIngredientElement();
		this.loadIngredientPool();
		this.loadIngredientSearchStrategy();
		this.loadIngredientTransformationStrategy();

	}

	public List<TargetElementProcessor<?>> getTargetIngredientProcessors() {
		return targetIngredientProcessors;
	}

	public void setTargetIngredientProcessors(List<TargetElementProcessor<?>> targetIngredientProcessors) {
		this.targetIngredientProcessors = targetIngredientProcessors;
	}

	@Override
	protected void setParticularStats(PatchHunkStats hunk, OperatorInstance genOperationInstance) {

		super.setParticularStats(hunk, genOperationInstance);
		hunk.getStats().put(HunkStatEnum.INGREDIENT_SCOPE,
				((genOperationInstance.getIngredientScope() != null) ? genOperationInstance.getIngredientScope()
						: "-"));

		if (genOperationInstance.getIngredient() != null) {

			if (genOperationInstance.getIngredient().getDerivedFrom() != null)
				hunk.getStats().put(HunkStatEnum.INGREDIENT_PARENT,
						genOperationInstance.getIngredient().getDerivedFrom());
			else {
				hunk.getStats().put(HunkStatEnum.INGREDIENT_PARENT, genOperationInstance.getIngredient());
			}
		}
	}

}
