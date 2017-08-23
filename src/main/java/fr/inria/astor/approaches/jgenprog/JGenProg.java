package fr.inria.astor.approaches.jgenprog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.IngredientBasedRepairApproach;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
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
import fr.inria.astor.core.setup.RandomManager;
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

	@Override
	public void prepareNextGeneration(List<ProgramVariant> temporalInstances, int generation) {

		super.prepareNextGeneration(temporalInstances, generation);

		if (ConfigurationProperties.getPropertyBool("applyCrossover")) {
			applyCrossover(generation);
		}
	}

	private void applyCrossover(int generation) {
		int numberVariants = this.variants.size();
		if (numberVariants <= 1) {
			log.debug("CO|Not Enough variants to apply Crossover");
			return;
		}

		// We randomly choose the two variants to crossover
		ProgramVariant v1 = this.variants.get(RandomManager.nextInt(numberVariants));
		ProgramVariant v2 = this.variants.get(RandomManager.nextInt(numberVariants));
		// Same instance
		if (v1 == v2) {
			log.debug("CO|randomless chosen the same variant to apply crossover");
			return;
		}

		if (v1.getOperations().isEmpty() || v2.getOperations().isEmpty()) {
			log.debug("CO|Not Enough ops to apply Crossover");
			return;
		}
		// we randomly select the generations to apply
		int rgen1index = RandomManager.nextInt(v1.getOperations().keySet().size()) + 1;
		int rgen2index = RandomManager.nextInt(v2.getOperations().keySet().size()) + 1;

		List<OperatorInstance> ops1 = v1.getOperations((int) v1.getOperations().keySet().toArray()[rgen1index]);
		List<OperatorInstance> ops2 = v2.getOperations((int) v2.getOperations().keySet().toArray()[rgen2index]);

		OperatorInstance opinst1 = ops1.remove((int) RandomManager.nextInt(ops1.size()));
		OperatorInstance opinst2 = ops2.remove((int) RandomManager.nextInt(ops2.size()));

		if (opinst1 == null || opinst2 == null) {
			log.debug("CO|We could not retrieve a operator");
			return;
		}

		// The generation of both new operators is the Last one.
		// In the first variant we put the operator taken from the 2 one.
		v1.putModificationInstance(generation, opinst2);
		// In the second variant we put the operator taken from the 1 one.
		v2.putModificationInstance(generation, opinst1);
		//
	}

}
