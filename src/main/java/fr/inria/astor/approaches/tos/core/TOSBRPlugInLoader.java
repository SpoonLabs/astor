package fr.inria.astor.approaches.tos.core;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientRandomSearchStrategy;
import fr.inria.astor.approaches.tos.ingredients.processors.StatementFixSpaceProcessor;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientPool;
import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * Load the plug ins that TOS approaches needs.
 * 
 * @author Matias Martinez
 *
 */
public class TOSBRPlugInLoader {


	public static void loadTargetElements(AstorCoreEngine approach) throws Exception {

		List<TargetElementProcessor<?>> targetElementProcessors = new ArrayList<TargetElementProcessor<?>>();
		targetElementProcessors.add(new StatementFixSpaceProcessor());
		approach.setTargetElementProcessors(targetElementProcessors);
	}

	
	public static void loadIngredientPool(AstorCoreEngine approach) throws JSAPException, Exception {
		IngredientBasedApproach ibra = (IngredientBasedApproach) approach;

		List<TargetElementProcessor<?>> ingredientProcessors = approach.getTargetElementProcessors();
		TOSIngredientPool ingredientspace = new TOSIngredientPool(ingredientProcessors);
		String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
		if (scope != null) {
			ingredientspace.scope = IngredientPoolScope.valueOf(scope.toUpperCase());
		}
		ibra.setIngredientPool(ingredientspace);
	
	}


	public static void loadIngredientSearchStrategy(AstorCoreEngine approach) throws Exception {

		IngredientBasedApproach ibra = (IngredientBasedApproach) approach;

		ibra.setIngredientSearchStrategy(new TOSIngredientRandomSearchStrategy(ibra.getIngredientPool()));
	}

	public static void loadOperatorSpaceDefinition(AstorCoreEngine approach) throws Exception {

		OperatorSpace opSpace = new OperatorSpace();
		opSpace.register(new ReplaceOp());
		approach.setOperatorSpace(opSpace);

	}

}
