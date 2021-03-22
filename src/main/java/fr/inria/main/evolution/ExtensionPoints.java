package fr.inria.main.evolution;

import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.output.ReportResults;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.solutionsearch.extension.VariantCompiler;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationStrategy;
import fr.inria.astor.core.solutionsearch.population.FitnessFunction;
import fr.inria.astor.core.solutionsearch.population.PopulationController;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.astor.core.validation.ProgramVariantValidator;

/**
 * Enum with all extension points
 * 
 * @author Matias Martinez
 *
 */
public enum ExtensionPoints {

	NAVIGATION_ENGINE("customengine", AstorCoreEngine.class), //
	FAULT_LOCALIZATION("faultlocalization", FaultLocalizationStrategy.class), //
	FITNESS_FUNCTION("fitnessfunction", FitnessFunction.class), //
	COMPILER("compiler", VariantCompiler.class), //
	POPULATION_CONTROLLER("populationcontroller", PopulationController.class), //
	INGREDIENT_STRATEGY_SCOPE("scope", IngredientPool.class), //
	SOLUTION_SORT_CRITERION("patchprioritization", SolutionVariantSortCriterion.class), //
	VALIDATION("validation", ProgramVariantValidator.class), //
	REPAIR_OPERATORS("repairoperators", AstorOperator.class), //
	OPERATORS_SPACE("operatorspace", OperatorSpace.class), //
	OPERATOR_SELECTION_STRATEGY("opselectionstrategy", OperatorSelectionStrategy.class), //
	INGREDIENT_SEARCH_STRATEGY("ingredientstrategy", IngredientSearchStrategy.class), //
	INGREDIENT_TRANSFORM_STRATEGY("ingredienttransformstrategy", IngredientTransformationStrategy.class), //
	TARGET_CODE_PROCESSOR("targetelementprocessor", TargetElementProcessor.class), //
	TARGET_INGREDIENT_CODE_PROCESSOR("targetingredientelementprocessor", TargetElementProcessor.class), //
	CLONE_GRANULARITY("clonegranularity", Class.class), //
	OUTPUT_RESULTS("outputresult", ReportResults.class), //
	SUSPICIOUS_NAVIGATION("modificationpointnavigation", SuspiciousNavigationStrategy.class), //
	// CODE_SYNTHESIS("codesynthesis", IngredientSynthesizer.class), //
	// CONTEXT_COLLECTOR("contextcollector", ExecutionContextCollector.class)
	;//

	public String identifier;
	public Class<?> _class;

	ExtensionPoints(String id, Class<?> _class) {
		this.identifier = id;
		this._class = _class;
	}

	public String argument() {
		return "-" + this.identifier;
	}
}
