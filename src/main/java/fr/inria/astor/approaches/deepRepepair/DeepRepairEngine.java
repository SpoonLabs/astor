package fr.inria.astor.approaches.deepRepepair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.IngredientBasedRepairApproach;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.ClusterIngredientTransformation;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;
import spoon.reflect.declaration.CtType;

/**
 * DeepRepair engine.
 * 
 * @author Matias Martinez
 *
 */
public class DeepRepairEngine extends IngredientBasedRepairApproach {

	public DeepRepairEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();

		String learningdir = ConfigurationProperties.getProperty("learningdir");
		if (learningdir == null || learningdir.isEmpty()) {
			log.error("Error, missing argument: -learningdir ");
			throw new IllegalArgumentException("Missing argument -learningdir");
		}

		ConfigurationProperties.setProperty("transformingredient", Boolean.TRUE.toString());

		List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();

		// Fix Space
		ExtensionPoints epoint = ExtensionPoints.INGREDIENT_PROCESSOR;
		if (!ConfigurationProperties.hasProperty(epoint.identifier)) {
			// By default, we use statements as granularity level.
			ingredientProcessors.add(new SingleStatementFixSpaceProcessor());
		} else {
			// We load custom processors
			String ingrProcessors = ConfigurationProperties.getProperty(epoint.identifier);
			String[] in = ingrProcessors.split(File.pathSeparator);
			for (String processor : in) {
				AbstractFixSpaceProcessor proc_i = (AbstractFixSpaceProcessor) PlugInLoader.loadPlugin(processor,
						epoint._class);
				ingredientProcessors.add(proc_i);
			}
		}
		this.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));

		OperatorSpace jpgoperatorSpace = PlugInLoader.loadOperatorSpace();
		if (jpgoperatorSpace == null)
			jpgoperatorSpace = new jGenProgSpace();

		this.setOperatorSpace(jpgoperatorSpace);
		this.setOperatorSpace(new DeepRepairOperatorSpace());
		this.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(jpgoperatorSpace));

		IngredientSpace ingredientspace = loadIngredientSpace(ingredientProcessors);

		ExtensionPoints ep = ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY;
		String ingredientTransformationStrategyClassName = ConfigurationProperties.properties
				.getProperty(ep.identifier);
		if (ingredientTransformationStrategyClassName == null) {
			this.ingredientTransformationStrategy = new ClusterIngredientTransformation();
		} else {
			this.ingredientTransformationStrategy = (IngredientTransformationStrategy) PlugInLoader
					.loadPlugin(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY);
		}

		ExtensionPoints cloneGranularityEP = ExtensionPoints.CLONE_GRANULARITY;
		Class cloneGranularity = PlugInLoader.loadClassFromProperty(cloneGranularityEP);
		if (cloneGranularity == null) {
			cloneGranularity = CtType.class;
		}
		ConfigurationProperties.setProperty("clonegranularity", cloneGranularity.getName());

		IngredientSearchStrategy ingStrategy = new CloneIngredientSearchStrategy(ingredientspace, cloneGranularity,
				ingredientTransformationStrategy);
		this.setIngredientStrategy(ingStrategy);

	}

	public static CtLocationIngredientSpace loadIngredientSpace(List<AbstractFixSpaceProcessor<?>> ingredientProcessors)
			throws JSAPException, Exception {
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
		return ingredientspace;
	}
}
