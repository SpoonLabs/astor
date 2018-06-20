package fr.inria.astor.approaches.cardumholes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.ingredients.TOSIngredientPool;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientRandomSearchStrategy;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientTransformationStrategy;
import fr.inria.astor.core.ingredientbased.ExhaustiveIngredientBasedEngine;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtExpression;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Cardumen1HApproach extends ExhaustiveIngredientBasedEngine {

	public Cardumen1HApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {

		super(mutatorExecutor, projFacade);

		ConfigurationProperties.setProperty(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "expression");
		ConfigurationProperties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "r-expression");

		ConfigurationProperties.setProperty("nrPlaceholders", "1");
		ConfigurationProperties.setProperty("excludevariableplaceholder", "false");
		ConfigurationProperties.setProperty("excludeliteralplaceholder", "true");
		ConfigurationProperties.setProperty("excludeinvocationplaceholder", "true");
		ConfigurationProperties.setProperty("excludevarliteralplaceholder", "true");

	}

	@Override
	protected void loadIngredientPool() throws JSAPException, Exception {

		List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetElementProcessors();
		TOSIngredientPool<CtExpression<?>> ingredientPool = new TOSIngredientPool<CtExpression<?>>(
				ingredientProcessors);
		String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
		if (scope != null) {
			ingredientPool.scope = IngredientPoolScope.valueOf(scope.toUpperCase());
		}
		this.setIngredientPool(ingredientPool);

	}

	@Override
	protected void loadIngredientSearchStrategy() throws Exception {

		TOSIngredientRandomSearchStrategy ingredientStrategy = new TOSIngredientRandomSearchStrategy(
				this.getIngredientPool());
		this.setIngredientSearchStrategy(ingredientStrategy);

	}

	@Override
	protected void loadIngredientTransformationStrategy() throws Exception {
		TOSIngredientTransformationStrategy ingtransf = new TOSIngredientTransformationStrategy();
		this.setIngredientTransformationStrategy(ingtransf);
	}

}

//