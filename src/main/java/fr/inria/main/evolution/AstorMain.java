package fr.inria.main.evolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import fr.inria.astor.approaches.exhaustive.ExhaustiveAstorEngine;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.approaches.jkali.JKaliEngine;
import fr.inria.astor.approaches.mutRepair.MutRepairSpace;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.GlobalBasicIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.LocalIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.PackageBasicFixSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.AbstractMain;
import fr.inria.main.ExecutionMode;

/**
 * Astor main
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class AstorMain extends AbstractMain {

	protected Logger log = Logger.getLogger(AstorMain.class.getName());

	AstorCoreEngine astorCore = null;

	public void initProject(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception {

		List<String> failingList = (failing != null) ? Arrays.asList(failing.split(File.pathSeparator))
				: new ArrayList<>();
		String method = this.getClass().getSimpleName();
		projectFacade = getProject(location, projectName, method, failingList, dependencies, true);
		projectFacade.getProperties().setExperimentName(this.getClass().getSimpleName());

		projectFacade.setupWorkingDirectories(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		List<String> tr = FinderTestCases.findTestCasesForRegression(
				projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT), projectFacade);

		projectFacade.getProperties().setRegressionCases(tr);
	}

	/**
	 * It creates a repair engine according to an execution mode.
	 * 
	 * 
	 * @param removeMode
	 * @return
	 * @throws Exception
	 */

	public AstorCoreEngine createEngine(ExecutionMode mode) throws Exception {
		astorCore = null;
		MutationSupporter mutSupporter = new MutationSupporter();
		List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
		// Fix Space
		ingredientProcessors.add(new SingleStatementFixSpaceProcessor());

		// We check if the user defines the operators to include in the operator
		// space
		OperatorSpace operatorSpace = null;
		String customOp = ConfigurationProperties.getProperty("customop");
		if (customOp != null && !customOp.isEmpty()) {
			operatorSpace = createCustomOperatorSpace(customOp);
		} else {
			customOp = ConfigurationProperties.getProperty("operatorspace");
			if (customOp != null && !customOp.isEmpty())
				operatorSpace = (OperatorSpace) PlugInLoader.loadPlugin(ExtensionPoints.OPERATORS_SPACE);
		}

		if (ExecutionMode.jKali.equals(mode)) {
			astorCore = new JKaliEngine(mutSupporter, projectFacade);
			//
		} else if (ExecutionMode.jGenProg.equals(mode)) {
			astorCore = new JGenProg(mutSupporter, projectFacade);
			if (operatorSpace == null)
				operatorSpace = new jGenProgSpace();
			// We retrieve strategy for navigating operator space
			String opStrategyClassName = ConfigurationProperties.properties.getProperty("opselectionstrategy");
			if (opStrategyClassName != null) {
				OperatorSelectionStrategy strategy = createOperationSelectionStrategy(opStrategyClassName,
						operatorSpace);
				astorCore.setOperatorSelectionStrategy(strategy);
			} else {// By default, uniform strategy
				astorCore.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(operatorSpace));
			}
			// The ingredients for build the patches
			String scope = ConfigurationProperties.properties.getProperty("scope");
			IngredientSpace ingredientspace = null;
			if ("global".equals(scope)) {
				ingredientspace = (new GlobalBasicIngredientSpace(ingredientProcessors));
			} else if ("package".equals(scope)) {
				ingredientspace = (new PackageBasicFixSpace(ingredientProcessors));
			} else if ("local".equals(scope)) {
				ingredientspace = (new LocalIngredientSpace(ingredientProcessors));
			} else {
				// todo duplicate?
				ingredientspace = (IngredientSpace) PlugInLoader.loadPlugin(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE,
						new Class[] { List.class }, new Object[] { ingredientProcessors });

			}

			IngredientSearchStrategy ingStrategy = retrieveIngredientStrategy(ingredientspace);

			((JGenProg) astorCore).setIngredientStrategy(ingStrategy);

		} else if (ExecutionMode.MutRepair.equals(mode)) {
			astorCore = new ExhaustiveSearchEngine(mutSupporter, projectFacade);
			if (operatorSpace == null)
				operatorSpace = new MutRepairSpace();
			// ConfigurationProperties.properties.setProperty("stopfirst",
			// "false");
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
			ConfigurationProperties.properties.setProperty("population", "1");
			ingredientProcessors.clear();
			ingredientProcessors.add(new IFConditionFixSpaceProcessor());
		} else if (ExecutionMode.EXASTOR.equals(mode)) {
			astorCore = new ExhaustiveAstorEngine(mutSupporter, projectFacade);
			if (operatorSpace == null)
				operatorSpace = new jGenProgSpace();

			// The ingredients for build the patches
			String scope = ConfigurationProperties.properties.getProperty("scope");
			IngredientSpace ingredientspace = null;
			if ("global".equals(scope)) {
				ingredientspace = (new GlobalBasicIngredientSpace(ingredientProcessors));
			} else if ("package".equals(scope)) {
				ingredientspace = (new PackageBasicFixSpace(ingredientProcessors));
			} else if ("local".equals(scope)) {
				ingredientspace = (new LocalIngredientSpace(ingredientProcessors));
			} else {
				ingredientspace = (IngredientSpace) PlugInLoader.loadPlugin(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE,
						new Class[] { List.class }, new Object[] { ingredientProcessors });
			}

			// TODO: put in a strategy
			IngredientSearchStrategy ingStrategy = null;// retrieveIngredientStrategy(ingredientspace);

			((ExhaustiveAstorEngine) astorCore).setIngredientSpace(ingredientspace);

		} else {
			// If the execution mode is any of the predefined, Astor
			// interpretates as
			// a custom engine, where the value corresponds to the class name of
			// the engine class
			String customengine = ConfigurationProperties.getProperty("customengine");
			astorCore = createEngineFromArgument(customengine, mutSupporter, projectFacade);

		}
		
		// Now we define the commons properties

		if (operatorSpace != null) {
			astorCore.setOperatorSpace(operatorSpace);
		} else {
			//throw new Exception("The operator Space cannot be null");
		}
		
		astorCore.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));
		

		//Loading extension Points
		astorCore.loadExtensionPoints();
		
		
		return astorCore;

	}

	/**
	 * We create an instance of the Engine which name is passed as argument.
	 * 
	 * @param customEngine
	 * @param mutSupporter
	 * @param projectFacade
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private AstorCoreEngine createEngineFromArgument(String customEngine, MutationSupporter mutSupporter,
			ProjectRepairFacade projectFacade) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(customEngine);
			object = classDefinition.getConstructor(mutSupporter.getClass(), projectFacade.getClass())
					.newInstance(mutSupporter, projectFacade);
		} catch (Exception e) {
			log.error("Loading custom engine: " + customEngine + " --" + e);
			throw new Exception("Error Loading Engine: " + e);
		}
		if (object instanceof AstorCoreEngine)
			return (AstorCoreEngine) object;
		else
			throw new Exception(
					"The strategy " + customEngine + " does not extend from " + AstorCoreEngine.class.getName());

	}

	private IngredientSearchStrategy retrieveIngredientStrategy(IngredientSpace ingredientspace) throws Exception {
		String strategy = ConfigurationProperties.properties.getProperty("ingredientstrategy");
		IngredientSearchStrategy st = null;
		if (strategy == null || strategy.trim().isEmpty()) {
			st = new EfficientIngredientStrategy(ingredientspace);
		} else {
			st = createCustomIngredientStrategy(strategy, ingredientspace);
		}
		return st;
	}

	private OperatorSpace createCustomOperatorSpace(String customOp) throws Exception {
		OperatorSpace customSpace = new OperatorSpace();
		String[] operators = customOp.split(File.pathSeparator);
		for (String op : operators) {
			AstorOperator aop = (AstorOperator) PlugInLoader.loadPlugin(op, ExtensionPoints.CUSTOM_OPERATOR._class);
			if (aop != null)
				customSpace.register(aop);
		}
		if (customSpace.getOperators().isEmpty()) {
			log.error("Empty custom operator space");
			throw new Exception("Empty custom operator space");
		}
		return customSpace;
	}

	private OperatorSelectionStrategy createOperationSelectionStrategy(String opSelectionStrategyClassName,
			OperatorSpace space) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(opSelectionStrategyClassName);
			object = classDefinition.getConstructor(OperatorSpace.class).newInstance(space);
		} catch (Exception e) {
			log.error("Loading strategy " + opSelectionStrategyClassName + " --" + e);
			throw new Exception("Loading strategy: " + e);
		}
		if (object instanceof OperatorSelectionStrategy)
			return (OperatorSelectionStrategy) object;
		else
			throw new Exception("The strategy " + opSelectionStrategyClassName + " does not extend from "
					+ OperatorSelectionStrategy.class.getName());
	}

	/**
	 * Load a custom ing strategy using reflection.
	 * 
	 * @param customStrategyclassName
	 * @param ingredientSpace
	 * @return
	 * @throws Exception
	 */
	private IngredientSearchStrategy createCustomIngredientStrategy(String customStrategyclassName,
			IngredientSpace ingredientSpace) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(customStrategyclassName);
			object = classDefinition.getConstructor(IngredientSpace.class).newInstance(ingredientSpace);
		} catch (Exception e) {
			log.error("Loading strategy " + customStrategyclassName + " --" + e);
			throw new Exception("Loading strategy " + customStrategyclassName + ": " + e);
		}
		if (object instanceof IngredientSearchStrategy)
			return (IngredientSearchStrategy) object;
		else
			throw new Exception("The strategy " + customStrategyclassName + " does not extend from "
					+ IngredientSearchStrategy.class.getName());

	}

	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);

		String mode = ConfigurationProperties.getProperty("mode");

		if ("statement".equals(mode) || "jgenprog".equals(mode))
			astorCore = createEngine(ExecutionMode.jGenProg);
		else if ("statement-remove".equals(mode) || "jkali".equals(mode))
			astorCore = createEngine(ExecutionMode.jKali);
		else if ("mutation".equals(mode) || "jmutrepair".equals(mode))
			astorCore = createEngine(ExecutionMode.MutRepair);
		else if ("custom".equals(mode))
			astorCore = createEngine(ExecutionMode.custom);
		else if ("exhaustive".equals(mode) || "exastor".equals(mode))
			astorCore = createEngine(ExecutionMode.EXASTOR);
		else {
			System.err.println("Unknown mode of execution: '" + mode
					+ "', know modes are: jgenprog, jkali, jmutrepair or custom.");
			return;
		}

		loadCommonExtensionPoints(astorCore);

		ConfigurationProperties.print();

		astorCore.startEvolution();

		astorCore.atEnd();

		long endT = System.currentTimeMillis();
		log.info("Time Total(s): " + (endT - startT) / 1000d);
	}

	/**
	 * Load extensions point that are used for all approaches. For the moment it
	 * loads only the "patch priorization point""
	 * 
	 * @throws Exception
	 */
	private boolean loadCommonExtensionPoints(AstorCoreEngine astorCore) {

		String patchpriority = ConfigurationProperties.getProperty("patchprioritization");
		if (patchpriority != null && !patchpriority.trim().isEmpty()) {
			SolutionVariantSortCriterion priorizStrategy = null;
			try {
				priorizStrategy = (SolutionVariantSortCriterion) PlugInLoader
						.loadPlugin(ExtensionPoints.SOLUTION_SORT_CRITERION);
				astorCore.setPatchSortCriterion(priorizStrategy);
				return true;
			} catch (Exception e) {
				log.error(e);
			}
		}
		return false;
	}

	/**
	 * @param args
	 * @throws Exception
	 * @throws ParseException
	 */
	public static void main(String[] args) throws Exception {
		AstorMain m = new AstorMain();
		m.execute(args);
	}

	public void execute(String[] args) throws Exception {
		boolean correct = processArguments(args);
		if (!correct) {
			System.err.println("Problems with commands arguments");
			return;
		}
		if (isExample(args)) {
			executeExample(args);
			return;
		}

		String dependencies = ConfigurationProperties.getProperty("dependenciespath");
		String failing = ConfigurationProperties.getProperty("failing");
		String location = ConfigurationProperties.getProperty("location");
		String packageToInstrument = ConfigurationProperties.getProperty("packageToInstrument");
		double thfl = ConfigurationProperties.getPropertyDouble("flthreshold");
		String projectName = ConfigurationProperties.getProperty("projectIdentifier");

		run(location, projectName, dependencies, packageToInstrument, thfl, failing);

	}

	public AstorCoreEngine getEngine() {
		return astorCore;
	}

}
