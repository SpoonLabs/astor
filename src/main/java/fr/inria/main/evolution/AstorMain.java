package fr.inria.main.evolution;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.evolutionary.JGenProg;
import fr.inria.astor.core.loop.evolutionary.JKali;
import fr.inria.astor.core.loop.evolutionary.MutRepair;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.RemoveRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.GlobalBasicFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.LocalFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.PackageBasicFixSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.ProcessValidator;
import fr.inria.main.AbstractMain;
import fr.inria.main.ExecutionMode;

/**
 * Main for full version of jGenProg
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class AstorMain extends AbstractMain {

	public void initProject(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception {

		List<String> failingList = Arrays.asList(failing.split(File.pathSeparator));
		String method = this.getClass().getSimpleName();
		projectFacade = getProject(location, projectName, method, failingList, dependencies, true);
		projectFacade.getProperties().setExperimentName(this.getClass().getSimpleName());

		projectFacade.setupTempDirectories(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		projectFacade.calculateRegression(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

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

		MutationSupporter mutSupporter = new MutationSupporter(getFactory());
		List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
		// Fix Space
		ingredientProcessors.add(new SingleStatementFixSpaceProcessor());
		
		JGenProg gploop = null;

		if (ExecutionMode.jKali.equals(mode)) {
			gploop = new JKali(mutSupporter, projectFacade);
			gploop.setRepairActionSpace(new RemoveRepairOperatorSpace());
			ConfigurationProperties.properties.setProperty("stopfirst", "false");
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
			ConfigurationProperties.properties.setProperty("population", "1");

		} else 	if (ExecutionMode.JGenProg.equals(mode)) {
			gploop = new JGenProg(mutSupporter, projectFacade);
			gploop.setRepairActionSpace(new UniformRandomRepairOperatorSpace());

			// The ingredients for build the patches
			String scope = ConfigurationProperties.properties.getProperty("scope").toLowerCase();
			if ("global".equals(scope)) {
				gploop.setFixspace(new GlobalBasicFixSpace(ingredientProcessors));
			} else if ("package".equals(scope)) {
				gploop.setFixspace(new PackageBasicFixSpace(ingredientProcessors));
			} else {//Default
				gploop.setFixspace(new LocalFixSpace(ingredientProcessors));
			}

		}else
		if (ExecutionMode.MutRepair.equals(mode)) {
			gploop = new MutRepair(mutSupporter, projectFacade);
			ConfigurationProperties.properties.setProperty("stopfirst", "false");
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
			ConfigurationProperties.properties.setProperty("population", "1");
			ingredientProcessors.clear();
			ingredientProcessors.add(new IFConditionFixSpaceProcessor());
		}
		//Now we define the commons properties
		
		// Pop controller
		gploop.setPopulationControler(new FitnessPopulationController());
		//
		gploop.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));

		gploop.setProgramValidator(new ProcessValidator());

		return gploop;

	}
	
	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);
		JGenProg gploop = null;
		String mode = ConfigurationProperties.getProperty("mode");

		if ("statement".equals(mode))
			gploop = createEngine(ExecutionMode.JGenProg);
		else if ("statement-remove".equals(mode))
			gploop = createEngine(ExecutionMode.jKali);
		else if ("mutation".equals(mode))
			gploop = createEngine(ExecutionMode.MutRepair);
		else {
			System.err.println("Unknown mode of execution (neither JKali nor JGenProg)");
			return;
		}
		gploop.createInitialPopulation();
		ConfigurationProperties.print();

		gploop.startEvolution();
		
		long endT = System.currentTimeMillis();
		log.info("Time Total(s): " + (endT - startT)/1000d);
	}

	/**
	 * @param args
	 * @throws Exception
	 * @throws ParseException
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("Arguments: " + Arrays.toString(args).replace(",", " "));
		AstorMain m = new AstorMain();
		boolean correct = m.processArguments(args);
		if (!correct) {
			System.err.println("Problems with commands arguments");
			return;
		}
		if (m.isExample(args)) {
			m.executeExample(args);
			return;
		}

		String dependencies = ConfigurationProperties.getProperty("dependenciespath");
		String failing = ConfigurationProperties.getProperty("failing");
		String location = ConfigurationProperties.getProperty("location");
		String packageToInstrument = ConfigurationProperties.getProperty("packageToInstrument");
		double thfl = ConfigurationProperties.getPropertyDouble("flthreshold");
		String projectName = ConfigurationProperties.getProperty("projectIdentifier");

		m.run(location, projectName, dependencies, packageToInstrument, thfl, failing);

	}

	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument)
			throws Exception {


	}

}
