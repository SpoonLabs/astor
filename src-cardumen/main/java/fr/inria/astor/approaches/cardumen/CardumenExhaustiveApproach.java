package fr.inria.astor.approaches.cardumen;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.ingredientbased.ExhaustiveIngredientBasedEngine;
import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionClassTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenExhaustiveApproach extends ExhaustiveIngredientBasedEngine implements IngredientBasedApproach {

	public CardumenExhaustiveApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		// Default configuration of Cardumen:
		ConfigurationProperties.setProperty("cleantemplates", "true");

		if (ConfigurationProperties.getPropertyBool("probabilistictransformation")) {
			ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
					"name-probability-based");
		}

		ConfigurationProperties.setProperty(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "expression");
		ConfigurationProperties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "r-expression");
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier, "name-probability-based");

	}

	@Override
	protected void loadIngredientPool() throws JSAPException, Exception {
		List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetElementProcessors();
		ExpressionTypeIngredientSpace ingredientspace = ((ConfigurationProperties.getPropertyBool("uniformreplacement"))
				? new ExpressionClassTypeIngredientSpace(ingredientProcessors)
				: new ExpressionTypeIngredientSpace(ingredientProcessors));
		String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
		if (scope != null) {
			ingredientspace.scope = IngredientPoolScope.valueOf(scope.toUpperCase());
		}
		this.setIngredientPool(ingredientspace);
	}

}
