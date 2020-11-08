package fr.inria.main.evolution;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.WriterAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import fr.inria.astor.approaches.cardumen.CardumenApproach;
import fr.inria.astor.approaches.deeprepair.DeepRepairEngine;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jkali.JKaliEngine;
import fr.inria.astor.approaches.jmutrepair.jMutRepairExhaustive;
import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.ingredientbased.ExhaustiveIngredientBasedEngine;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
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

	protected AstorCoreEngine core = null;

	public void initProject(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception {

		List<String> failingList = (failing != null) ? Arrays.asList(failing.split(File.pathSeparator))
				: new ArrayList<>();
		String method = this.getClass().getSimpleName();

		projectFacade = getProjectConfiguration(location, projectName, method, failingList, dependencies, true);

		projectFacade.getProperties().setExperimentName(this.getClass().getSimpleName());

		projectFacade.setupWorkingDirectories(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		if (ConfigurationProperties.getPropertyBool("autocompile")) {
			compileProject(projectFacade.getProperties());
		}

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
		core = null;
		MutationSupporter mutSupporter = new MutationSupporter();

		if (ExecutionMode.DeepRepair.equals(mode)) {
			core = new DeepRepairEngine(mutSupporter, projectFacade);

		} else if (ExecutionMode.CARDUMEN.equals(mode)) {
			core = new CardumenApproach(mutSupporter, projectFacade);

		} else if (ExecutionMode.jKali.equals(mode)) {
			core = new JKaliEngine(mutSupporter, projectFacade);

		} else if (ExecutionMode.jGenProg.equals(mode)) {
			core = new JGenProg(mutSupporter, projectFacade);

		} else if (ExecutionMode.MutRepair.equals(mode)) {
			core = new jMutRepairExhaustive(mutSupporter, projectFacade);

		} else if (ExecutionMode.EXASTOR.equals(mode)) {
			core = new ExhaustiveIngredientBasedEngine(mutSupporter, projectFacade);

		} else if (ExecutionMode.SCAFFOLD.equals(mode)) {
			core = new ScaffoldRepairEngine(mutSupporter, projectFacade);

		} else {
			// If the execution mode is any of the predefined, Astor
			// interpretes as
			// a custom engine, where the value corresponds to the class name of
			// the engine class
			String customengine = ConfigurationProperties.getProperty(ExtensionPoints.NAVIGATION_ENGINE.identifier);
			core = createEngineFromArgument(customengine, mutSupporter, projectFacade);

		}

		// Loading extension Points
		core.loadExtensionPoints();

		core.initModel();

		if (ConfigurationProperties.getPropertyBool("skipfaultlocalization")) {
			// We dont use FL, so at this point the do not have suspicious
			core.initPopulation(new ArrayList<SuspiciousCode>());
		} else {
			List<SuspiciousCode> suspicious = core.calculateSuspicious();

			core.initPopulation(suspicious);
		}

		return core;

	}

	/**
	 * We create an instance of the Engine which name is passed as argument.
	 * 
	 * @param customEngineValue
	 * @param mutSupporter
	 * @param projectFacade
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AstorCoreEngine createEngineFromArgument(String customEngineValue, MutationSupporter mutSupporter,
			ProjectRepairFacade projectFacade) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(customEngineValue);
			object = classDefinition.getConstructor(mutSupporter.getClass(), projectFacade.getClass())
					.newInstance(mutSupporter, projectFacade);
		} catch (Exception e) {
			log.error("Loading custom engine: " + customEngineValue + " --" + e);
			throw new Exception("Error Loading Engine: " + e);
		}
		if (object instanceof AstorCoreEngine)
			return (AstorCoreEngine) object;
		else
			throw new Exception(
					"The strategy " + customEngineValue + " does not extend from " + AstorCoreEngine.class.getName());

	}

	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);

		String mode = ConfigurationProperties.getProperty("mode").toLowerCase();
		String customEngine = ConfigurationProperties.getProperty(ExtensionPoints.NAVIGATION_ENGINE.identifier);

		if (customEngine != null && !customEngine.isEmpty())
			core = createEngine(ExecutionMode.custom);
		else {
			for (ExecutionMode executionMode : ExecutionMode.values()) {
				for (String acceptedName : executionMode.getAcceptedNames()) {
					if (acceptedName.equals(mode)) {
						core = createEngine(executionMode);
						break;
					}
				}
			}

			if (core == null) {
				System.err.println("Unknown mode of execution: '" + mode + "',  modes are: "
						+ Arrays.toString(ExecutionMode.values()));
				return;
			}

		}

		ConfigurationProperties.print();

		core.startEvolution();

		core.atEnd();

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

		log.info("Running Astor on a JDK at " + System.getProperty("java.home"));

		if (!correct) {
			System.err.println("Problems with commands arguments");
			return;
		}
		if (isExample(args)) {
			executeExample(args);
			return;
		}

		String dependencies = ConfigurationProperties.getProperty("dependenciespath");
		dependencies += (ConfigurationProperties.hasProperty("extendeddependencies"))
				? (File.pathSeparator + ConfigurationProperties.hasProperty("extendeddependencies"))
				: "";
		String failing = ConfigurationProperties.getProperty("failing");
		String location = ConfigurationProperties.getProperty("location");
		String packageToInstrument = ConfigurationProperties.getProperty("packageToInstrument");
		double thfl = ConfigurationProperties.getPropertyDouble("flthreshold");
		String projectName = ConfigurationProperties.getProperty("projectIdentifier");

		setupLogging();

		run(location, projectName, dependencies, packageToInstrument, thfl, failing);

	}

	public AstorCoreEngine getEngine() {
		return core;
	}

	public void setupLogging() throws IOException {
		// done with log4j2.xml
	}

}
