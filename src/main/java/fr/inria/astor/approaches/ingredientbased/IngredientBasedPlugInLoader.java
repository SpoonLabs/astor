package fr.inria.astor.approaches.ingredientbased;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.deepRepepair.DeepRepairOperatorSpace;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.ProbabilisticIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.GlobalBasicIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.LocalIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.PackageBasicFixSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.ClusterIngredientTransformation;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.DefaultIngredientTransformation;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.ProbabilisticTransformationStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.RandomTransformationStrategy;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;
import fr.inria.main.evolution.PlugInVisitor;

/**
 * 
 * @author Matias Martinez
 *
 */
public class IngredientBasedPlugInLoader extends PlugInVisitor {

	public IngredientBasedPlugInLoader() {
		super();
	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientPool(AstorCoreEngine approach) throws JSAPException, Exception {
		IngredientBasedRepairApproach ibra = (IngredientBasedRepairApproach) approach;
		List<TargetElementProcessor<?>> ingredientProcessors = approach.getTargetElementProcessors();
		// The ingredients for build the patches
		IngredientSpace ingredientspace = getIngredientPool(ingredientProcessors);

		ibra.setIngredientPool(ingredientspace);

	}

	public static IngredientSpace getIngredientPool(List<TargetElementProcessor<?>> ingredientProcessors)
			throws JSAPException, Exception {
		String scope = ConfigurationProperties.properties.getProperty("scope");
		IngredientSpace ingredientspace = null;
		if ("global".equals(scope)) {
			ingredientspace = (new GlobalBasicIngredientSpace(ingredientProcessors));
		} else if ("package".equals(scope)) {
			ingredientspace = (new PackageBasicFixSpace(ingredientProcessors));
		} else if ("local".equals(scope) || "file".equals(scope)) {
			ingredientspace = (new LocalIngredientSpace(ingredientProcessors));
		} else {
			ingredientspace = (IngredientSpace) PlugInLoader.loadPlugin(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE,
					new Class[] { List.class }, new Object[] { ingredientProcessors });

		}
		return ingredientspace;
	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientSearchStrategy(AstorCoreEngine approach) throws Exception {

		IngredientBasedRepairApproach ibra = (IngredientBasedRepairApproach) approach;

		IngredientSpace ingredientspace = ibra.getIngredientPool();

		IngredientSearchStrategy ingStrategy = null;

		String ingStrategySt = ConfigurationProperties.properties
				.getProperty(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier);

		if (ingStrategySt != null) {

			if (ingStrategySt.equals("uniform-random")) {
				ingStrategy = new EfficientIngredientStrategy(ingredientspace);
			} else if (ingStrategySt.equals("name-probability-based")) {
				ingStrategy = new ProbabilisticIngredientStrategy(ingredientspace);
			} else if (ingStrategySt.equals("code-similarity-based")) {
				ingStrategy = new CloneIngredientSearchStrategy(ingredientspace);
			} else {
				ingStrategy = (IngredientSearchStrategy) PlugInLoader.loadPlugin(
						ExtensionPoints.INGREDIENT_SEARCH_STRATEGY, new Class[] { IngredientSpace.class },
						new Object[] { ingredientspace });
			}
		} else {
			ingStrategy = new EfficientIngredientStrategy(ingredientspace);
		}

		ibra.setIngredientStrategy(ingStrategy);

	}

	protected void loadIngredientTransformationStrategy(AstorCoreEngine approach) throws Exception {
		IngredientBasedRepairApproach ibra = (IngredientBasedRepairApproach) approach;

		String ingredientTransformationStrategy = ConfigurationProperties.properties
				.getProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier);

		if (ingredientTransformationStrategy == null) {
			ibra.setIngredientTransformationStrategy(new DefaultIngredientTransformation());
		} else {// there is a value
			if (ingredientTransformationStrategy.equals("no-transformation")) {
				ibra.setIngredientTransformationStrategy(new DefaultIngredientTransformation());
			} else if (ingredientTransformationStrategy.equals("random-variable-replacement")) {
				ibra.setIngredientTransformationStrategy(new RandomTransformationStrategy());
			} else if (ingredientTransformationStrategy.equals("name-cluster-based")) {
				ibra.setIngredientTransformationStrategy(new ClusterIngredientTransformation());
			} else if (ingredientTransformationStrategy.equals("name-probability-based")) {
				ibra.setIngredientTransformationStrategy(new ProbabilisticTransformationStrategy());
			} else {
				ibra.setIngredientTransformationStrategy((IngredientTransformationStrategy) PlugInLoader
						.loadPlugin(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY));
			}
		}

	}



	@Override
	public void load(AstorCoreEngine approach) throws Exception {
		super.load(approach);
		this.loadIngredientPool(approach);
		this.loadIngredientSearchStrategy(approach);
		this.loadIngredientTransformationStrategy(approach);
	}
}