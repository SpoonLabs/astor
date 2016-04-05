package fr.inria.main.evolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.approaches.jkali.JKali;
import fr.inria.astor.approaches.jkali.JKaliSpace;
import fr.inria.astor.approaches.mutRepair.MutExhaustiveRepair;
import fr.inria.astor.approaches.mutRepair.MutRepairSpace;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.population.FitnessPopulationController;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.ingredients.BasicIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.FixIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.GlobalBasicFixSpace;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.LocalFixSpace;
import fr.inria.astor.core.loop.spaces.ingredients.PackageBasicFixSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.validation.validators.ProcessValidator;
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

	
	JGenProg astorCore = null;

	public void initProject(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception {

		List<String> failingList = Arrays.asList(failing.split(File.pathSeparator));
		String method = this.getClass().getSimpleName();
		projectFacade = getProject(location, projectName, method, failingList, dependencies, true);
		projectFacade.getProperties().setExperimentName(this.getClass().getSimpleName());

		projectFacade.setupTempDirectories(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		FinderTestCases.findTestCasesForRegression(
				projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT), projectFacade);

	}

	/**
	 * It creates an repair engine according to an execution mode.
	 * 
	 * 
	 * @param removeMode
	 * @return
	 * @throws Exception
	 */

	public JGenProg createEngine(ExecutionMode mode) throws Exception {
		astorCore = null;
		MutationSupporter mutSupporter = new MutationSupporter();
		List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
		// Fix Space
		ingredientProcessors.add(new SingleStatementFixSpaceProcessor());

		if (ExecutionMode.jKali.equals(mode)) {
			astorCore = new JKali(mutSupporter, projectFacade);
			astorCore.setRepairActionSpace(new JKaliSpace());
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
			ConfigurationProperties.properties.setProperty("population", "1");

		} else if (ExecutionMode.JGenProg.equals(mode)) {
			astorCore = new JGenProg(mutSupporter, projectFacade);
			astorCore.setRepairActionSpace(new UniformRandomRepairOperatorSpace(new jGenProgSpace()));

			// The ingredients for build the patches
			String scope = ConfigurationProperties.properties.getProperty("scope").toLowerCase();
			FixIngredientSpace fixspace = null;
			if ("global".equals(scope)) {
				fixspace = (new GlobalBasicFixSpace(ingredientProcessors));
			} else if ("package".equals(scope)) {
				fixspace = (new PackageBasicFixSpace(ingredientProcessors));
			} else {// Default
				fixspace = (new LocalFixSpace(ingredientProcessors));
			}
			IngredientStrategy ingStrategy = getIngredientStrategy(fixspace);
			astorCore.setIngredientStrategy(ingStrategy);

		} else if (ExecutionMode.MutRepair.equals(mode)) {
			astorCore = new MutExhaustiveRepair(mutSupporter, projectFacade);
			astorCore.setRepairActionSpace(new MutRepairSpace());
			//ConfigurationProperties.properties.setProperty("stopfirst", "false");
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
			ConfigurationProperties.properties.setProperty("population", "1");
			ingredientProcessors.clear();
			ingredientProcessors.add(new IFConditionFixSpaceProcessor());
		} 

		//We check if the user define their own operators
		String customOp = ConfigurationProperties.getProperty("customop");
		if(customOp != null && !customOp.isEmpty()){
			createCustomSpace(customOp);
		}
		// Now we define the commons properties

		// Pop controller
		astorCore.setPopulationControler(new FitnessPopulationController());
		//
		astorCore.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));

		astorCore.setProgramValidator(new ProcessValidator());

		return astorCore;

	}

	private IngredientStrategy getIngredientStrategy(FixIngredientSpace fixspace) throws Exception {
		String strategy = ConfigurationProperties.properties.getProperty("ingredientstrategy");
		IngredientStrategy st = null;
		if(strategy == null || strategy.trim().isEmpty())
			st = new BasicIngredientStrategy();
		else
			st = createStrategy(strategy);
		
		if(st != null)
			st.setIngredientSpace(fixspace);	
	
		return st;
	}

	private void createCustomSpace(String customOp) throws Exception {
		OperatorSpace customSpace = new OperatorSpace();
		String[] operators = customOp.split(File.pathSeparator);
		for (String op : operators) {
			AstorOperator aop = createOperator(op);
			if(aop != null)
				customSpace.register(aop);
		}
		if(customSpace.getOperators().isEmpty()){
			log.error("Empty custom operator space");
			throw new Exception("Empty custom operator space");
		}
		
		astorCore.setRepairActionSpace(new UniformRandomRepairOperatorSpace(customSpace));
	}
	
	AstorOperator createOperator(String className) {
	      Object object = null;
	      try {
	          Class classDefinition = Class.forName(className);
	          object = classDefinition.newInstance();
	      } catch (Exception e) {
	          log.error(e);
	      } 
	      if(object instanceof AstorOperator)
	    	  return (AstorOperator) object;
	      else
	    	  log.error("The operator "+className+" does not extend from "+ AstorOperator.class.getName());
		  return null;
	 }
	
	public IngredientStrategy createStrategy(String className) throws Exception {
	      Object object = null;
	      try {
	          Class classDefinition = Class.forName(className);
	          object = classDefinition.newInstance();
	      } catch (Exception e) {
	          log.error("Loading strategy "+ className + " --"+ e);
	          throw new Exception("Loading strategy: "+e);
	      } 
	      if(object instanceof  IngredientStrategy)
	    	  return ( IngredientStrategy) object;
	      else
	    	  throw new Exception("The strategy "+className+" does not extend from "+ IngredientStrategy.class.getName());
	
	 }
	
	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);

		String mode = ConfigurationProperties.getProperty("mode");

		if ("statement".equals(mode) ||"jgenprog".equals(mode) )
			astorCore = createEngine(ExecutionMode.JGenProg);
		else if ("statement-remove".equals(mode) || "jkali".equals(mode))
			astorCore = createEngine(ExecutionMode.jKali);
		else if ("mutation".equals(mode) || "jmutrepair".equals(mode))
			astorCore = createEngine(ExecutionMode.MutRepair);
		else {
			System.err.println("Unknown mode of execution: '"+mode+ "', know modes are: jgenprog, jkali, jmutrepair.");
			return;
		}
		astorCore.createInitialPopulation();
		ConfigurationProperties.print();

		astorCore.startEvolution();

		astorCore.showResults();

		long endT = System.currentTimeMillis();
		log.info("Time Total(s): " + (endT - startT) / 1000d);
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

	public JGenProg getEngine() {
		return astorCore;
	}

}
