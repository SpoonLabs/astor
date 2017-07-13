package fr.inria.astor.approaches.jgenprog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.IngredientBasedRepairApproach;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.DefaultIngredientTransformation;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;

/**
 * Core repair approach based on reuse of ingredients.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JGenProg extends IngredientBasedRepairApproach {

	public JGenProg(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();

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

		OperatorSpace jpgoperatorSpace = PlugInLoader.loadOperatorSpace();
		if (jpgoperatorSpace == null)
			jpgoperatorSpace = new jGenProgSpace();

		this.setOperatorSpace(jpgoperatorSpace);

		// We retrieve strategy for navigating operator space
		String opStrategyClassName = ConfigurationProperties.properties.getProperty("opselectionstrategy");
		if (opStrategyClassName != null) {
			OperatorSelectionStrategy strategy = createOperationSelectionStrategy(opStrategyClassName,
					jpgoperatorSpace);
			this.setOperatorSelectionStrategy(strategy);
		} else {// By default, uniform strategy
			this.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(jpgoperatorSpace));
		}
		IngredientSpace ingredientspace = PlugInLoader.loadIngredientSpace(ingredientProcessors);

		IngredientSearchStrategy ingStrategy = (IngredientSearchStrategy) PlugInLoader.loadPlugin(
				ExtensionPoints.INGREDIENT_SEARCH_STRATEGY, new Class[] { IngredientSpace.class },
				new Object[] { ingredientspace });

		if (ingStrategy == null) {
			ingStrategy = new EfficientIngredientStrategy(ingredientspace);
		}

		this.setIngredientStrategy(ingStrategy);
		this.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));

		ExtensionPoints ep = ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY;
		String ingredientTransformationStrategyClassName = ConfigurationProperties.properties
				.getProperty(ep.identifier);
		if (ingredientTransformationStrategyClassName == null) {
			this.ingredientTransformationStrategy = new DefaultIngredientTransformation();
		} else {
			this.ingredientTransformationStrategy = (IngredientTransformationStrategy) PlugInLoader
					.loadPlugin(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY);
		}

	}
}
