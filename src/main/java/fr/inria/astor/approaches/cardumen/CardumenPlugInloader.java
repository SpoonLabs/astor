package fr.inria.astor.approaches.cardumen;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.approaches.ingredientbased.IngredientBasedPlugInLoader;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionClassTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.ExtensionPoints;
/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenPlugInloader extends IngredientBasedPlugInLoader {

	@Override
	protected void loadIngredientPool(AstorCoreEngine approach) throws JSAPException, Exception {
		IngredientBasedApproach ibra = (IngredientBasedApproach) approach;
		
		List<TargetElementProcessor<?>> ingredientProcessors= approach.getTargetElementProcessors();
		ExpressionTypeIngredientSpace ingredientspace = ((ConfigurationProperties.getPropertyBool("uniformreplacement"))
				? new ExpressionClassTypeIngredientSpace(ingredientProcessors)
				: new ExpressionTypeIngredientSpace(ingredientProcessors));
		String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
		if (scope != null) {
			ingredientspace.scope = IngredientSpaceScope.valueOf(scope.toUpperCase());
		}
		ibra.setIngredientPool(ingredientspace);
	}

}
