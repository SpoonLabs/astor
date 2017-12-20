package fr.inria.main.evolution;

import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.loop.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.loop.extension.VariantCompiler;
import fr.inria.astor.core.loop.navigation.SuspiciousNavigationStrategy;
import fr.inria.astor.core.loop.population.FitnessFunction;
import fr.inria.astor.core.loop.population.PopulationController;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.output.OutputResults;
import fr.inria.astor.core.validation.ProgramVariantValidator;

/**
 * Enum with all extension points
 * @author Matias Martinez
 *
 */
public enum ExtensionPoints {

	FAULT_LOCALIZATION("faultlocalization", FaultLocalizationStrategy.class), 
	FITNESS_FUNCTION("fitnessfunction",FitnessFunction.class), //
	COMPILER("compiler",VariantCompiler.class),//
	POPULATION_CONTROLLER("populationcontroller",PopulationController.class),//
	INGREDIENT_STRATEGY_SCOPE("scope",IngredientSpace.class),//
	SOLUTION_SORT_CRITERION("patchprioritization",SolutionVariantSortCriterion.class), 
	VALIDATION("validation",ProgramVariantValidator.class), //
	CUSTOM_OPERATOR("customop",AstorOperator.class),//
	OPERATORS_SPACE("operatorspace",OperatorSpace.class),//
	INGREDIENT_SEARCH_STRATEGY("ingredientstrategy",IngredientSearchStrategy.class),//
	INGREDIENT_TRANSFORM_STRATEGY("ingredienttransformstrategy", IngredientTransformationStrategy.class),//
	INGREDIENT_PROCESSOR("ingredientprocessor",AbstractFixSpaceProcessor.class),
	CLONE_GRANULARITY("clonegranularity",Class.class),
	OUTPUT_RESULTS("outputresult",OutputResults.class),
	SUSPICIOUS_NAVIGATION("modificationpointnavigation", SuspiciousNavigationStrategy.class);
	
	public String identifier;
	public Class<?> _class;
	
	ExtensionPoints(String id, Class<?> _class){
		 this.identifier = id;
		 this._class = _class;
	}
}
