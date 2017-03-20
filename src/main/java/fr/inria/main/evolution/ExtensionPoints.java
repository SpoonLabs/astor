package fr.inria.main.evolution;

import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.loop.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.loop.extension.VariantCompiler;
import fr.inria.astor.core.loop.population.FitnessFunction;
import fr.inria.astor.core.loop.population.PopulationController;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.validation.validators.ProgramValidator;

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
	VALIDATION("validation",ProgramValidator.class), //
	CUSTOM_OPERATOR("customop",AstorOperator.class),//
	OPERATORS_SPACE("operatorspace",OperatorSpace.class),//
	INGREDIENT_SEARCH_STRATEGY("ingredientstrategy",IngredientSearchStrategy.class);//
	
	String identifier;
	Class<?> _class;
	ExtensionPoints(String id, Class<?> _class){
		 this.identifier = id;
		 this._class = _class;
	}
}
