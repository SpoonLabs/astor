package fr.inria.astor.approaches.ingredientbased;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats;
import spoon.reflect.declaration.CtElement;

/**
 * Ingredient based-repair approach.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class IngredientBasedRepairApproach extends AstorCoreEngine {

	protected IngredientSearchStrategy ingredientSearchStrategy;
	protected IngredientTransformationStrategy ingredientTransformationStrategy;
	protected IngredientSpace ingredientPool; 

	public IngredientBasedRepairApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		pluginLoaded = new IngredientBasedPlugInLoader();
	}

	/**
	 * By default, it initializes the spoon model. It should not be created
	 * before. Otherwise, an exception occurs.
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

		AstorOperator operationType = operatorSelectionStrategy.getNextOperator(suspModificationPoint);

		if (operationType == null) {
			log.debug("Operation Null");
			return null;
		}

		CtElement targetStmt = suspModificationPoint.getCodeElement();

		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(targetStmt);
		operation.setOperationApplied(operationType);
		operation.setModificationPoint(suspModificationPoint);
		operation.defineParentInformation(suspModificationPoint);

		if (operationType.needIngredient()) {
			Ingredient ingredient = null;
			ingredient = this.ingredientSearchStrategy.getFixIngredient(modificationPoint, operationType);
			if (ingredient == null) {
				log.debug("Any ingredient for this point, we discard it");
				return null;
			}
			operation.setModified(ingredient.getCode());
			operation.setIngredient(ingredient);
		}

		return operation;
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

	public IngredientSearchStrategy getIngredientSearchStrategy() {
		return ingredientSearchStrategy;
	}

	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientSearchStrategy) {
		this.ingredientSearchStrategy = ingredientSearchStrategy;
	}

	public IngredientTransformationStrategy getIngredientTransformationStrategy() {
		return ingredientTransformationStrategy;
	}

	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
		this.ingredientTransformationStrategy = ingredientTransformationStrategy;
	}

	public IngredientSearchStrategy getIngredientStrategy() {
		return ingredientSearchStrategy;
	}

	public void setIngredientStrategy(IngredientSearchStrategy ingredientStrategy) {
		this.ingredientSearchStrategy = ingredientStrategy;
	}


	public IngredientSpace getIngredientPool() {
		return ingredientPool;
	}

	public void setIngredientPool(IngredientSpace ingredientPool) {
		this.ingredientPool = ingredientPool;
	}

}
