package fr.inria.astor.approaches.cardumen;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ExpressionReplaceOperator;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.ProbabilisticIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionClassTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.ProbabilisticTransformationStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.RandomTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenApproach extends JGenProg {

	public CardumenApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);

	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();

		List<TargetElementProcessor<?>> ingredientProcessors = new ArrayList<TargetElementProcessor<?>>();

		// Ingredient processor By default: ExpressionIngredientSpaceProcessor
		// ------------
		ExtensionPoints epoint = ExtensionPoints.INGREDIENT_PROCESSOR;
		if (!ConfigurationProperties.hasProperty(epoint.identifier)) {
			// By default, we use statements as granularity level.
			ingredientProcessors.add(new ExpressionIngredientSpaceProcessor());
			// ingredientProcessors.add(new
			// ExpressionBooleanIngredientSpaceProcessor());

		} else {
			// We load custom processors
			String ingrProcessors = ConfigurationProperties.getProperty(epoint.identifier);
			String[] in = ingrProcessors.split("_");
			for (String processor : in) {
				TargetElementProcessor proc_i = (TargetElementProcessor) PlugInLoader.loadPlugin(processor,
						epoint._class);
				ingredientProcessors.add(proc_i);
			}
		}
		//// -----------------------
		// Replace operator
		OperatorSpace roperatorSpace = new OperatorSpace();
		roperatorSpace.register(new ExpressionReplaceOperator());
		this.setOperatorSpace(roperatorSpace);

		///

		// OPeration SelectionStrategy //Note: we have only one operator..
		String opStrategyClassName = ConfigurationProperties.properties.getProperty("opselectionstrategy");
		if (opStrategyClassName != null) {
			OperatorSelectionStrategy strategy = createOperationSelectionStrategy(opStrategyClassName, roperatorSpace);
			this.setOperatorSelectionStrategy(strategy);
		} else {// By default, uniform strategy
			this.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(roperatorSpace));
		}

		ExpressionTypeIngredientSpace ingredientspace = ((ConfigurationProperties.getPropertyBool("uniformreplacement"))
				? new ExpressionClassTypeIngredientSpace(ingredientProcessors)
				: new ExpressionTypeIngredientSpace(ingredientProcessors));
		String scope = ConfigurationProperties.getProperty(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE.identifier);
		if (scope != null) {
			ingredientspace.scope = IngredientSpaceScope.valueOf(scope.toUpperCase());
		}
		//
		ExtensionPoints ep = ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY;
		String ingredientTransformationStrategyClassName = ConfigurationProperties.properties
				.getProperty(ep.identifier);
		if (ingredientTransformationStrategyClassName == null) {
			if (ConfigurationProperties.getPropertyBool("probabilistictransformation"))
				this.ingredientTransformationStrategy = new ProbabilisticTransformationStrategy();
			else
				this.ingredientTransformationStrategy = new RandomTransformationStrategy();
			ConfigurationProperties.properties.setProperty(ep.identifier,
					this.ingredientTransformationStrategy.getClass().getCanonicalName());
		} else {
			this.ingredientTransformationStrategy = (IngredientTransformationStrategy) PlugInLoader
					.loadPlugin(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY);
		}

		// Ingredient search Strategy

		IngredientSearchStrategy ingStrategy = (IngredientSearchStrategy) PlugInLoader.loadPlugin(
				ExtensionPoints.INGREDIENT_SEARCH_STRATEGY, new Class[] { IngredientSpace.class },
				new Object[] { ingredientspace });

		if (ingStrategy == null) {
			ingStrategy = new ProbabilisticIngredientStrategy(ingredientspace);
		}

		this.setIngredientStrategy(ingStrategy);
		this.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));

		// Transformation strategy:
		// ----
		ConfigurationProperties.setProperty("cleantemplates", "true");

	}

}
