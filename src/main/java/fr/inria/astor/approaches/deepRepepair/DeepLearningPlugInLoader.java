package fr.inria.astor.approaches.deepRepepair;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.ingredientbased.IngredientBasedPlugInLoader;
import fr.inria.astor.approaches.ingredientbased.IngredientBasedRepairApproach;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;

/**
 * 
 * @author Matias Martinez
 *
 */
public class DeepLearningPlugInLoader extends IngredientBasedPlugInLoader {

	@Override
	public void loadIngredientPool(AstorCoreEngine approach) throws JSAPException, Exception {

		IngredientBasedRepairApproach ibra = (IngredientBasedRepairApproach) approach;

		List<TargetElementProcessor<?>> ingredientProcessors = approach.getTargetElementProcessors();

		// The ingredients for build the patches
		String scope = ConfigurationProperties.properties.getProperty("scope");
		CtLocationIngredientSpace ingredientspace = null;
		if ("global".equals(scope)) {
			ingredientspace = (new CtGlobalIngredientScope(ingredientProcessors));
		} else if ("package".equals(scope)) {
			ingredientspace = (new CtPackageIngredientScope(ingredientProcessors));
		} else if ("local".equals(scope)) {
			ingredientspace = (new CtClassIngredientSpace(ingredientProcessors));
		} else {
			ingredientspace = (CtLocationIngredientSpace) PlugInLoader.loadPlugin(
					ExtensionPoints.INGREDIENT_STRATEGY_SCOPE, new Class[] { List.class },
					new Object[] { ingredientProcessors });

		}
		ibra.setIngredientPool(ingredientspace);
	}
	
	@Override
	protected void loadOperatorSpaceDefinition(AstorCoreEngine approach) throws Exception {

		approach.setOperatorSpace(new DeepRepairOperatorSpace());
	}
}
