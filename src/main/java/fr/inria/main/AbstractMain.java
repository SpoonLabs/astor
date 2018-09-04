package fr.inria.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.output.ReportResults;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.solutionsearch.extension.VariantCompiler;
import fr.inria.astor.core.solutionsearch.population.FitnessFunction;
import fr.inria.astor.core.solutionsearch.population.PopulationConformation;
import fr.inria.astor.core.solutionsearch.population.PopulationController;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.AstorCtIngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.astor.core.validation.ProgramVariantValidator;
import fr.inria.astor.util.TimeUtil;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder.InputType;

/**
 * Abstract entry point of the framework. It defines and manages program
 * arguments.
 * 
 * @author Matias Martinez
 * 
 */
public abstract class AbstractMain {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	public static ProjectRepairFacade projectFacade;

	static Options options = new Options();

	CommandLineParser parser = new BasicParser();

	static {
		options.addOption("id", true, "(Optional) Name/identified of the project to evaluate (Default: folder name)");
		options.addOption("mode", true, " (Optional) Mode (Default: jGenProg Mode)");
		options.addOption("location", true, "URL of the project to manipulate");
		options.addOption("dependencies", true,
				"dependencies of the application, separated by char " + File.pathSeparator);
		options.addOption("package", true, "package to instrument e.g. org.commons.math");
		options.addOption("failing", true,
				"failing test cases, separated by Path separator char (: in linux/mac  and ; in windows)");
		options.addOption("out", true,
				"(Optional) Out dir: place were solutions and intermediate program variants are stored. (Default: ./outputMutation/)");
		options.addOption("help", false, "print help and usage");
		options.addOption("bug280", false, "Run the bug 280 from Apache Commons Math");
		options.addOption("bug288", false, "Run the bug 288 from Apache Commons Math");
		options.addOption("bug309", false, "Run the bug 309 from Apache Commons Math");
		options.addOption("bug428", false, "Run the bug 428 from Apache Commons Math");
		options.addOption("bug340", false, "Run the bug 340 from Apache Commons Math");

		// Optional parameters
		options.addOption("jvm4testexecution", true,
				"(Optional) location of JVM that executes the mutated version of a program (Folder that contains java script, such as /bin/ ).");
		options.addOption("jvm4evosuitetestexecution", true,
				"(Optional) location of JVM that executes the EvoSuite test cases. If it is not specified, Astor uses that one from property 'jvm4testexecution'");
		options.addOption("maxgen", true, "(Optional) max number of generation a program variant is evolved");
		options.addOption("population", true,
				"(Optional) number of population (program variants) that the approach evolves");

		options.addOption("maxtime", true, "(Optional) maximum time (in minutes) to execute the whole experiment");

		options.addOption("validation", true,
				"(Optional) type of validation: process|evosuite. Default:"
						+ ConfigurationProperties.properties.getProperty("validation")
						+ "It accepts custormize validation prodedures, which must extend from "
						+ ProgramVariantValidator.class.getCanonicalName());
		options.addOption("flthreshold", true, "(Optional) threshold for Fault locatication. Default:"
				+ ConfigurationProperties.properties.getProperty("flthreshold"));

		options.addOption("maxsuspcandidates", true,
				"(Optional) Maximun number of suspicious statement considered. Default: "
						+ ConfigurationProperties.properties.getProperty("maxsuspcandidates"));

		options.addOption("reintroduce", true,
				"(Optional) indicates whether it reintroduces the original program in each generation (value: "
						+ PopulationConformation.ORIGINAL.toString() + "), "
						+ " reintroduces parent variant in next generation (value: "
						+ PopulationConformation.PARENTS.toString() + "), "
						+ " reintroduce the solution in the next generation (value: "
						+ PopulationConformation.SOLUTIONS.toString() + ") "
						+ " reintroduces origina and parents (value: original-parents) "
						+ "or do not reintroduce nothing (value: none).  More than one option can be written, separated by: "
						+ File.pathSeparator + "Default: "
						+ ConfigurationProperties.properties.getProperty("reintroduce"));

		options.addOption("tmax1", true,
				"(Optional) maximum time (in miliseconds) for validating the failing test case ");
		options.addOption("tmax2", true,
				"(Optional) maximum time (in miliseconds) for validating the regression test cases ");
		options.addOption("stopfirst", true,
				"(Optional) Indicates whether it stops when it finds the first solution (default: true)");
		options.addOption("allpoints", true,
				"(Optional) True if analyze all points of a program validation during a generation. False for analyzing only one gen per generation.");

		options.addOption("savesolution", false,
				"(Optional) Save on disk intermediate program variants (even those that do not compile)");
		options.addOption("saveall", false, "(Optional) Save on disk all program variants generated");

		options.addOption("testbystep", false, "(Optional) Executes each test cases in a separate process.");

		options.addOption("modificationpointnavigation", true,
				"(Optional) Method to navigate the modification point space of a variant: inorder, random, weight random (according to the gen's suspicous value)");

		options.addOption("mutationrate", true,
				"(Optional) Value between 0 and 1 that indicates the probability of modify one gen (default: 1)");

		options.addOption("probagenmutation", false,
				"(Optional) Mutates a gen according to its suspicious value. Default: always mutates gen.");

		options.addOption("srcjavafolder", true,
				"(Optional) folder with the application source code files (default: /src/java/main/)");

		options.addOption("srctestfolder", true,
				"(Optional) folder with the test cases source code files (default: /src/test/main/)");

		options.addOption("binjavafolder", true,
				"(Optional) folder with the application binaries (default: /target/classes/)");

		options.addOption("bintestfolder", true,
				"(Optional) folder with the test cases binaries (default: /target/test-classes/)");

		options.addOption("multipointmodification", false,
				"(Optional) An element of a program variant (i.e., gen) can be modified several times in different generation");

		options.addOption("uniqueoptogen", true,
				"(Optional) An operation can be applied only once to a gen, even this operation is in other variant.");

		options.addOption("resetoperations", false,
				"(Optional) The program variants do not pass the operators throughs the generations");

		options.addOption("regressionforfaultlocalization", true,
				"(Optional) Use the regression for fault localization.Otherwise, failing test cases. Default: "
						+ ConfigurationProperties.properties.getProperty("regressionforfaultlocalization"));

		options.addOption("javacompliancelevel", true,
				"(Optional) Compliance level (e.g., 7 for java 1.7, 6 for java 1.6). Default Java 1.7");

		options.addOption("alternativecompliancelevel", true,
				"(Optional) Alternative compliance level. Default Java 1.4. Used after Astor tries to compile to the complicance level and fails.");

		options.addOption("seed", true,
				"(Optional) Random seed, for reproducible runs.  Default is whatever java.util.Random uses when not explicitly initialized.");

		options.addOption("scope", true,
				"(Optional) Scope of the ingredient seach space: Local (same class), package (classes from the same package) or global (all classes from the application under analysis). Default: local."
						+ " It accepts customize scopes, which must implement from "
						+ AstorCtIngredientPool.class.getCanonicalName());

		options.addOption("skipfaultlocalization", false,
				"The fault localization is skipped and all statements are considered");

		options.addOption("maxdate", true,
				"(Optional) Indicates the hour Astor has to stop processing. it must have the format: HH:mm");

		options.addOption(ExtensionPoints.REPAIR_OPERATORS.identifier, true,
				"(Optional) Indicates the class name of the operators used by the selected execution mode. They must extend from "
						+ AstorOperator.class.getName() + ". Operator names must be separated by char "
						+ File.pathSeparator + ". The classes must be included in the classpath.");

		options.addOption("ingredientstrategy", true,
				"(Optional) Indicates the name of the class that astor calls for retrieving ingredients. They must extend from "
						+ IngredientSearchStrategy.class.getName() + " The classes must be included in the classpath.");

		options.addOption("opselectionstrategy", true,
				"(Optional) Indicates the name of the class that astor calls for selecting an operator from the operator space. They must extend from "
						+ OperatorSelectionStrategy.class.getName()
						+ " The classes must be included in the classpath.");

		options.addOption("customengine", true,
				"(Optional) Indicates the class name of the execution mode. It must extend from "
						+ AstorCoreEngine.class.getName());

		options.addOption(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, true,
				"(Optional) Indicates the class name of the process that selects target elements. They must extend from "
						+ ExtensionPoints.TARGET_CODE_PROCESSOR._class.getName()
						+ " The classes must be included in the classpath.");

		for (ExtensionPoints epoint : ExtensionPoints.values()) {
			if (!options.hasOption(epoint.identifier)) {
				options.addOption(epoint.identifier, true, String
						.format("Extension point %s. It must extend/implement from %s", epoint.name(), epoint._class));
			}
		}

		options.addOption("excludeRegression", false, "Exclude test regression execution");

		options.addOption("ignoredtestcases", true, "Test cases to ignore");

		options.addOption("dse", false, "Apply DSE into Evosuite");

		options.addOption("esoverpatched", false,
				"Apply ES over the patched version. By default it applies over the buggy version.");

		options.addOption("evosuitetimeout", true, "ES global timeout (in seconds)");

		options.addOption("ESParameters", true,
				"(Optional) Parameters to pass to ES. Separe parameters name and values using " + File.pathSeparator);

		options.addOption("learningdir", true, "Learning Dir");

		options.addOption("clonegranularity", true, "Clone granularity");

		options.addOption("patchprioritization", true,
				"(Optional) Indicates the class name of the class that orders patches. It must extend from "
						+ SolutionVariantSortCriterion.class.getName());

		options.addOption("transformingredient", false, "indicates if Astor transforms ingredients.");

		options.addOption("loglevel", true,
				"Indicates the log level. Values: " + Arrays.toString(Level.getAllPossiblePriorities()));

		options.addOption("timezone", true, "Timezone to be used in the process that Astor creates. Default: "
				+ ConfigurationProperties.getProperty("timezone"));

		options.addOption("faultlocalization", true, "Class name of Fault locatication Strategy. Default:"
				+ ConfigurationProperties.properties.getProperty("faultlocalization"));

		options.addOption("fitnessfunction", true,
				"(Optional) Class name of Fitness function for evaluating a variant. It must extend from "
						+ FitnessFunction.class.getCanonicalName() + " The classes must be included in the classpath.");

		options.addOption("outputresult", true,
				"(Optional) Class name for manipulating the output. It must extend from "
						+ ReportResults.class.getCanonicalName() + " The classes must be included in the classpath.");

		options.addOption("populationcontroller", true,
				"(Optional) class name that controls the population evolution. It must extend from  "
						+ PopulationController.class.getCanonicalName()
						+ " The classes must be included in the classpath.");

		options.addOption("filterfaultlocalization", true, "Indicates whether Astor filters the FL output. Default:"
				+ ConfigurationProperties.properties.getProperty("filterfaultlocalization"));

		options.addOption("operatorspace", true,
				"Operator Space contains the operators. It must extends from " + OperatorSpace.class.getName());

		options.addOption("compiler", true,
				"Class used for compile a Program variant.  It must extends from " + VariantCompiler.class.getName());

		options.addOption("regressiontestcases4fl", true,
				"Classes names of test cases used in the regression, separated by '" + File.pathSeparator
						+ "' . If the argument it is not specified, Astor automatically calculates them.");

		options.addOption("manipulatesuper", false, "Allows to manipulate 'super' statements. Disable by default.");

		options.addOption("classestoinstrument", true, "List of classes names that Astor instrument, separated by '"
				+ File.pathSeparator
				+ "' . If the argument it is not specified, Astor uses all classes from the program under repair.");
		options.addOption("maxVarCombination", true, "Max number of combinations per variable out-of-scope. Default: "
				+ ConfigurationProperties.getPropertyInt("maxVarCombination"));

		options.addOption("parameters", true, "Parameters, divided by " + File.pathSeparator);

		options.addOption("autocompile", true, "wheteher auto compile");

	}

	public abstract void run(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception;

	// CLG notes that this slightly modifies the semantics of example execution,
	// such that it now will execute examples for non-GenProg tools as well.
	// This may not be desired, but it was easier to implement this way, for
	// several reasons, and she can't see a reason that the examples *wouldn't*
	// work for the other tools.
	private boolean isExample(CommandLine cmd) {
		String[] examples = { "bug280", "bug288", "bug340", "bug428" };
		for (String example : examples) {
			if (cmd.hasOption(example)) {
				return true;
			}
		}
		return false;
	}

	public boolean isExample(String[] args) {
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			return isExample(cmd);
		} catch (Exception e) { // generic exceptions are bad practice, but Java
								// is also the worst.
			System.out.println("Error: " + e.getMessage());
			help();
			return false;
		}
	}

	public boolean processArguments(String[] args) throws Exception {

		ConfigurationProperties.clear();

		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (UnrecognizedOptionException e) {
			System.out.println("Error: " + e.getMessage());
			help();
			return false;
		}
		if (cmd.hasOption("help")) {
			help();
			return false;
		}

		if (cmd.hasOption("jvm4testexecution")) {
			ConfigurationProperties.properties.setProperty("jvm4testexecution",
					cmd.getOptionValue("jvm4testexecution"));
		} else {
			String javahome = System.getProperty("java.home");
			File location = new File(javahome);
			if (location.getName().equals("jre")) {
				javahome = location.getParent() + File.separator + "bin";
				File javalocationbin = new File(javahome);
				if (!javalocationbin.exists()) {
					System.err.println("Problems to generate java jdk path");
					return false;
				}
			}
			ConfigurationProperties.properties.setProperty("jvm4testexecution", javahome);

		}

		if (cmd.hasOption("jvm4evosuitetestexecution")) {
			ConfigurationProperties.properties.setProperty("jvm4evosuitetestexecution",
					cmd.getOptionValue("jvm4evosuitetestexecution"));
		} else {
			ConfigurationProperties.properties.setProperty("jvm4evosuitetestexecution",
					ConfigurationProperties.properties.getProperty("jvm4testexecution"));

		}

		if (!ProjectConfiguration.validJDK()) {
			System.err.println("Error: invalid jdk folder");
			return false;
		} else {
			String jvmhome = ConfigurationProperties.properties.getProperty("jvm4testexecution");
			String jdkVersion = ProjectConfiguration.getVersionJDK(jvmhome);
			if (jdkVersion != null)
				ConfigurationProperties.properties.setProperty("jvmversion", jdkVersion);
			else
				log.equals("Error: problems to determine the version of the JDK located at path: " + jvmhome);
		}

		if (!this.isExample(cmd)) {
			String dependenciespath = cmd.getOptionValue("dependencies");
			String location = cmd.getOptionValue("location");

			// Process mandatory parameters.
			if (location == null) {
				help();
				return false;
			}
			ConfigurationProperties.properties.setProperty("location", location);

			if (dependenciespath != null) {
				ConfigurationProperties.properties.setProperty("dependenciespath", dependenciespath);
			}

			String failing = cmd.getOptionValue("failing");
			if ((failing != null))
				ConfigurationProperties.properties.setProperty("failing", failing);

		}

		if (cmd.hasOption("package"))
			ConfigurationProperties.properties.setProperty("packageToInstrument", cmd.getOptionValue("package"));

		if (cmd.hasOption("id"))
			ConfigurationProperties.properties.setProperty("projectIdentifier", cmd.getOptionValue("id"));

		if (cmd.hasOption("mode"))
			ConfigurationProperties.properties.setProperty("mode", cmd.getOptionValue("mode"));

		String outputPath = "";
		if (cmd.hasOption("out")) {
			outputPath = cmd.getOptionValue("out");
		} else {
			outputPath = ConfigurationProperties.properties.getProperty("workingDirectory");
		}
		ConfigurationProperties.properties.setProperty("workingDirectory", (new File(outputPath)).getAbsolutePath());

		// Process optional values.
		if (cmd.hasOption("maxgen"))
			ConfigurationProperties.properties.setProperty("maxGeneration", cmd.getOptionValue("maxgen"));

		if (cmd.hasOption("population"))
			ConfigurationProperties.properties.setProperty("population", cmd.getOptionValue("population"));

		if (cmd.hasOption("validation"))
			ConfigurationProperties.properties.setProperty("validation", cmd.getOptionValue("validation"));

		if (cmd.hasOption("maxsuspcandidates"))
			ConfigurationProperties.properties.setProperty("maxsuspcandidates",
					cmd.getOptionValue("maxsuspcandidates"));

		if (cmd.hasOption("flthreshold")) {
			try {
				ConfigurationProperties.properties.setProperty("flthreshold", cmd.getOptionValue("flthreshold"));
			} catch (Exception e) {
				System.out.println("Error: threshold not valid");
				help();
				return false;
			}
		}

		if (cmd.hasOption("reintroduce"))
			ConfigurationProperties.properties.setProperty("reintroduce", cmd.getOptionValue("reintroduce"));

		if (cmd.hasOption("tmax1"))
			ConfigurationProperties.properties.setProperty("tmax1", cmd.getOptionValue("tmax1"));

		if (cmd.hasOption("tmax2"))
			ConfigurationProperties.properties.setProperty("tmax2", cmd.getOptionValue("tmax2"));

		if (cmd.hasOption("stopfirst"))
			ConfigurationProperties.properties.setProperty("stopfirst", cmd.getOptionValue("stopfirst"));

		if (cmd.hasOption("allpoints"))
			ConfigurationProperties.properties.setProperty("allpoints", cmd.getOptionValue("allpoints"));

		if (cmd.hasOption("savesolution"))
			ConfigurationProperties.properties.setProperty("savesolution", "true");

		if (cmd.hasOption("saveall"))
			ConfigurationProperties.properties.setProperty("saveall", "true");

		if (cmd.hasOption("testbystep"))
			ConfigurationProperties.properties.setProperty("testbystep", "true");

		if (cmd.hasOption("modificationpointnavigation"))
			ConfigurationProperties.properties.setProperty("modificationpointnavigation",
					cmd.getOptionValue("modificationpointnavigation"));

		if (cmd.hasOption("mutationrate"))
			ConfigurationProperties.properties.setProperty("mutationrate", cmd.getOptionValue("mutationrate"));

		if (cmd.hasOption("srcjavafolder"))
			ConfigurationProperties.properties.setProperty("srcjavafolder", cmd.getOptionValue("srcjavafolder"));

		if (cmd.hasOption("srctestfolder"))
			ConfigurationProperties.properties.setProperty("srctestfolder", cmd.getOptionValue("srctestfolder"));

		if (cmd.hasOption("binjavafolder"))
			ConfigurationProperties.properties.setProperty("binjavafolder", cmd.getOptionValue("binjavafolder"));

		if (cmd.hasOption("bintestfolder"))
			ConfigurationProperties.properties.setProperty("bintestfolder", cmd.getOptionValue("bintestfolder"));

		if (cmd.hasOption("maxtime"))
			ConfigurationProperties.properties.setProperty("maxtime", cmd.getOptionValue("maxtime"));

		if (cmd.hasOption("maxdate")) {
			String hour = cmd.getOptionValue("maxdate");
			try {
				TimeUtil.tranformHours(hour);
			} catch (Exception e) {
				System.out.println("Problem when parser maxdate property, tip: HH:mm");
			}
			ConfigurationProperties.properties.setProperty("maxdate", hour);

		}

		if (cmd.hasOption("multipointmodification"))
			ConfigurationProperties.properties.setProperty("multipointmodification", "true");

		if (cmd.hasOption("javacompliancelevel"))
			ConfigurationProperties.properties.setProperty("javacompliancelevel",
					cmd.getOptionValue("javacompliancelevel"));

		if (cmd.hasOption("alternativecompliancelevel"))
			ConfigurationProperties.properties.setProperty("alternativecompliancelevel",
					cmd.getOptionValue("alternativecompliancelevel"));

		if (cmd.hasOption("resetoperations"))
			ConfigurationProperties.properties.setProperty("resetoperations", "true");

		if (cmd.hasOption("regressionforfaultlocalization"))
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization",
					cmd.getOptionValue("regressionforfaultlocalization"));

		if (cmd.hasOption("probagenmutation"))
			ConfigurationProperties.properties.setProperty("probagenmutation", "true");

		if (cmd.hasOption("skipfaultlocalization"))
			ConfigurationProperties.properties.setProperty("skipfaultlocalization", "true");

		if (cmd.hasOption("uniqueoptogen"))
			ConfigurationProperties.properties.setProperty("uniqueoptogen", cmd.getOptionValue("uniqueoptogen"));

		if (cmd.hasOption("seed"))
			ConfigurationProperties.properties.setProperty("seed", cmd.getOptionValue("seed"));

		if (cmd.hasOption("scope"))
			ConfigurationProperties.properties.setProperty("scope", cmd.getOptionValue("scope"));

		if (cmd.hasOption(ExtensionPoints.REPAIR_OPERATORS.identifier))
			ConfigurationProperties.properties.setProperty(ExtensionPoints.REPAIR_OPERATORS.identifier,
					cmd.getOptionValue(ExtensionPoints.REPAIR_OPERATORS.identifier));

		if (cmd.hasOption("ingredientstrategy"))
			ConfigurationProperties.properties.setProperty("ingredientstrategy",
					cmd.getOptionValue("ingredientstrategy"));

		if (cmd.hasOption("opselectionstrategy"))
			ConfigurationProperties.properties.setProperty(ExtensionPoints.OPERATOR_SELECTION_STRATEGY.identifier,
					cmd.getOptionValue("opselectionstrategy"));

		for (ExtensionPoints epoint : ExtensionPoints.values()) {
			if (cmd.hasOption(epoint.identifier))
				ConfigurationProperties.properties.setProperty(epoint.identifier,
						cmd.getOptionValue(epoint.identifier));
		}

		if (cmd.hasOption("customengine"))
			ConfigurationProperties.properties.setProperty("customengine", cmd.getOptionValue("customengine"));

		if (cmd.hasOption("excludeRegression"))
			ConfigurationProperties.properties.setProperty("executeRegression", "false");

		if (cmd.hasOption("ignoredtestcases"))
			ConfigurationProperties.properties.setProperty("ignoredTestCases", cmd.getOptionValue("ignoredtestcases"));

		if (cmd.hasOption("evosuitetimeout"))
			ConfigurationProperties.properties.setProperty("evosuitetimeout", cmd.getOptionValue("evosuitetimeout"));

		ConfigurationProperties.properties.setProperty("evoDSE", Boolean.toString(cmd.hasOption("dse")));

		ConfigurationProperties.properties.setProperty("evoRunOnBuggyClass",
				Boolean.toString(!(cmd.hasOption("esoverpatched"))));

		if (cmd.hasOption("ESParameters"))
			ConfigurationProperties.properties.setProperty("ESParameters", cmd.getOptionValue("ESParameters"));

		if (cmd.hasOption("learningdir"))
			ConfigurationProperties.properties.setProperty("learningdir", cmd.getOptionValue("learningdir"));

		if (cmd.hasOption("clonegranularity"))
			ConfigurationProperties.properties.setProperty("clonegranularity", cmd.getOptionValue("clonegranularity"));

		if (cmd.hasOption("patchprioritization"))
			ConfigurationProperties.properties.setProperty("patchprioritization",
					cmd.getOptionValue("patchprioritization"));

		if (cmd.hasOption("transformingredient"))
			ConfigurationProperties.properties.setProperty("transformingredient", "true");

		if (cmd.hasOption("loglevel")) {
			String loglevelSelected = cmd.getOptionValue("loglevel");
			ConfigurationProperties.properties.setProperty("loglevel", loglevelSelected);
		}
		if (cmd.hasOption("timezone")) {
			ConfigurationProperties.properties.setProperty("timezone", cmd.getOptionValue("timezone"));
		}

		if (cmd.hasOption("faultlocalization")) {
			ConfigurationProperties.properties.setProperty("faultlocalization",
					cmd.getOptionValue("faultlocalization"));
		}

		if (cmd.hasOption("fitnessfunction")) {
			ConfigurationProperties.properties.setProperty("fitnessfunction", cmd.getOptionValue("fitnessfunction"));
		}

		if (cmd.hasOption("outputresult")) {
			ConfigurationProperties.properties.setProperty("outputresult", cmd.getOptionValue("outputresult"));
		}

		if (cmd.hasOption("populationcontroller")) {
			ConfigurationProperties.properties.setProperty("populationcontroller",
					cmd.getOptionValue("populationcontroller"));
		}
		if (cmd.hasOption("filterfaultlocalization"))
			ConfigurationProperties.properties.setProperty("filterfaultlocalization",
					cmd.getOptionValue("filterfaultlocalization"));

		if (cmd.hasOption("operatorspace"))
			ConfigurationProperties.properties.setProperty("operatorspace", cmd.getOptionValue("operatorspace"));

		if (cmd.hasOption("compiler"))
			ConfigurationProperties.properties.setProperty("compiler", cmd.getOptionValue("compiler"));

		if (cmd.hasOption("regressiontestcases4fl"))
			ConfigurationProperties.properties.setProperty("regressiontestcases4fl",
					cmd.getOptionValue("regressiontestcases4fl"));

		if (cmd.hasOption("manipulatesuper"))
			ConfigurationProperties.properties.setProperty("manipulatesuper", Boolean.TRUE.toString());

		if (cmd.hasOption("classestoinstrument"))
			ConfigurationProperties.properties.setProperty("classestoinstrument",
					cmd.getOptionValue("classestoinstrument"));

		if (cmd.hasOption("maxVarCombination"))
			ConfigurationProperties.properties.setProperty("maxVarCombination",
					cmd.getOptionValue("maxVarCombination"));

		if (cmd.hasOption("parameters")) {
			String[] pars = cmd.getOptionValue("parameters").split(File.pathSeparator);
			for (int i = 0; i < pars.length; i = i + 2) {
				String key = pars[i];
				String value = pars[i + 1];
				ConfigurationProperties.properties.setProperty(key, value);

			}
		}

		if (cmd.hasOption("autocompile"))
			ConfigurationProperties.properties.setProperty("autocompile", cmd.getOptionValue("autocompile"));

		log.info("command line arguments: " + Arrays.toString(args).replace(",", " "));

		// CLG believes, but is not totally confident in her belief, that this
		// is a reasonable place to initialize the random number generator.
		RandomManager.initialize();
		return true;
	}

	/**
	 * Finds an example to test in the command line
	 * 
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	public boolean executeExample(String[] args) throws Exception {

		CommandLine cmd = parser.parse(options, args);
		ConfigurationProperties.properties.setProperty("stopfirst", "true");
		String dependenciespath = null, folder = null, failing = null, location = null, packageToInstrument = null;
		double faultLocalizationThreshold = 0;
		if (cmd.hasOption("bug280")) {
			dependenciespath = new File("./examples/math_85/libs/junit-4.4.jar").getAbsolutePath();
			folder = "Math-issue-280";
			failing = "org.apache.commons.math.distribution.NormalDistributionTest";
			location = new File("./examples/Math-issue-280/").getAbsolutePath();
			packageToInstrument = "org.apache.commons";
			faultLocalizationThreshold = 0.2;
		}

		if (cmd.hasOption("bug288")) {
			dependenciespath = "examples/Math-issue-288/lib/junit-4.4.jar";
			folder = "Math-issue-288";
			failing = "org.apache.commons.math.optimization.linear.SimplexSolverTest";
			location = "examples/Math-issue-288/";
			packageToInstrument = "org.apache.commons";
			faultLocalizationThreshold = 0.2;
		}
		if (cmd.hasOption("bug309")) {
			dependenciespath = "examples/Math-issue-309/lib/junit-4.4.jar";
			folder = "Math-issue-309";
			failing = "org.apache.commons.math.random.RandomDataTest";
			location = ("examples/Math-issue-309/");
			packageToInstrument = "org.apache.commons";
			faultLocalizationThreshold = 0.5;
		}
		if (cmd.hasOption("bug340")) {
			dependenciespath = "examples/Math-issue-340/lib/junit-4.4.jar";
			folder = "Math-issue-340";
			failing = "org.apache.commons.math.fraction.BigFractionTest";
			location = ("examples/Math-issue-340/");
			packageToInstrument = "org.apache.commons";
			faultLocalizationThreshold = 0.5;
		}
		if (cmd.hasOption("bug428")) {
			dependenciespath = "examples/Lang-issue-428/lib/easymock-2.5.2.jar" + File.pathSeparator
					+ "examples/Lang-issue-428/lib/junit-4.7.jar";
			folder = "Lang-issue-428";
			failing = "org.apache.commons.lang3.StringUtilsIsTest";
			location = ("examples/Lang-issue-428/");
			packageToInstrument = "org.apache.commons";
			faultLocalizationThreshold = 0.5;
		}
		if (location != null) {
			ConfigurationProperties.properties.setProperty("flthreshold",
					new Double(faultLocalizationThreshold).toString());
			this.run(location, folder, dependenciespath, packageToInstrument, faultLocalizationThreshold, failing);
			return true;
		}

		return false;
	}

	private static void help() {

		HelpFormatter formater = new HelpFormatter();
		formater.printHelp("Main", options);
		System.out.println("More options and default values at 'configuration.properties' file");

		System.exit(0);

	}

	/**
	 * Compile the original code
	 * 
	 * @param properties
	 */
	protected void compileProject(ProjectConfiguration properties) {
		final Launcher launcher = new Launcher();
		for (String path_src : properties.getOriginalDirSrc()) {
			log.debug("Add folder to compile: " + path_src);
			launcher.addInputResource(path_src);
		}

		for (String path_test : properties.getTestDirSrc()) {
			log.debug("Add folder to compile: " + path_test);
			launcher.addInputResource(path_test);
		}

		String binoutput = properties.getWorkingDirForBytecode() + File.separator
				+ (ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		launcher.setBinaryOutputDirectory(binoutput);

		log.info("Compiling original code from " + launcher.getModelBuilder().getInputSources()
				+ "\n bytecode saved in " + launcher.getModelBuilder().getBinaryOutputDirectory());

		launcher.getEnvironment()
				.setPreserveLineNumbers(ConfigurationProperties.getPropertyBool("preservelinenumbers"));
		launcher.getEnvironment().setComplianceLevel(ConfigurationProperties.getPropertyInt("javacompliancelevel"));
		launcher.getEnvironment().setShouldCompile(true);
		launcher.getEnvironment().setSourceClasspath(properties.getDependenciesString().split(File.pathSeparator));
		launcher.buildModel();
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
		launcher.getModelBuilder().compile(InputType.FILES);
		// launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);

	}

	protected ProjectRepairFacade getProjectConfiguration(String location, String projectIdentifier, String method,
			List<String> failingTestCases, String dependencies, boolean srcWithMain) throws Exception {

		if (projectIdentifier == null || projectIdentifier.equals("")) {
			File locFile = new File(location);
			projectIdentifier = locFile.getName();
		}

		String projectUnderRepairKeyFolder = File.separator + method + "-" + projectIdentifier + File.separator;
		String workingdir = ConfigurationProperties.getProperty("workingDirectory");
		String workingDirForSource = workingdir + projectUnderRepairKeyFolder + "/src/";
		String workingDirForBytecode = workingdir + projectUnderRepairKeyFolder + "/bin/";
		String originalProjectRoot = location + File.separator;
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setWorkingDirRoot(workingdir + projectUnderRepairKeyFolder);
		properties.setWorkingDirForSource(workingDirForSource);
		properties.setWorkingDirForBytecode(workingDirForBytecode);

		properties.setFixid(projectIdentifier);

		properties.setOriginalProjectRootDir(originalProjectRoot);

		determineSourceFolders(properties, srcWithMain, originalProjectRoot);

		if (dependencies != null) {
			properties.setDependencies(dependencies);
		}

		if (!ConfigurationProperties.getPropertyBool("autocompile")) {
			// compileProject(properties);
			// } else {
			String originalBin = determineBinFolder(originalProjectRoot,
					ConfigurationProperties.getProperty("binjavafolder"));
			properties.setOriginalAppBinDir(originalBin);

			String originalBinTest = determineBinFolder(originalProjectRoot,
					ConfigurationProperties.getProperty("bintestfolder"));
			properties.setOriginalTestBinDir(originalBinTest);
		}

		properties.setFailingTestCases(failingTestCases);

		properties.setPackageToInstrument(ConfigurationProperties.getProperty("packageToInstrument"));

		properties.setDataFolder(ConfigurationProperties.getProperty("resourcesfolder"));

		ProjectRepairFacade ce = new ProjectRepairFacade(properties);

		return ce;
	}

	private String determineBinFolder(String originalProjectRoot, String paramBinFolder) {

		File fBin = new File(originalProjectRoot + File.separator + paramBinFolder).getAbsoluteFile();
		if (Files.exists(fBin.toPath())) {
			return fBin.getAbsolutePath();
		}

		throw new IllegalArgumentException("The bin folder  " + fBin + " does not exist.");
	}

	private List<String> determineSourceFolders(ProjectConfiguration properties, boolean srcWithMain,
			String originalProjectRoot) throws IOException {

		final boolean onlyOneFolder = true;

		List<String> sourceFolders = new ArrayList<>();

		String paramSrc = ConfigurationProperties.getProperty("srcjavafolder");

		String[] srcs = paramSrc.split(File.pathSeparator);
		// adding src from parameter
		addToFolder(sourceFolders, srcs, originalProjectRoot, !onlyOneFolder);
		if (sourceFolders.isEmpty()) {
			// Adding src folders by guessing potential folders
			String[] possibleSrcFolders = new String[] { (originalProjectRoot + File.separator + "src/main/java"),
					(originalProjectRoot + File.separator + "src/java"),
					(originalProjectRoot + File.separator + "src"), };

			addToFolder(sourceFolders, possibleSrcFolders, originalProjectRoot, onlyOneFolder);
		}
		log.info("Source folders: " + sourceFolders);
		properties.setOriginalDirSrc(sourceFolders);

		// Now test folder
		String paramTestSrc = ConfigurationProperties.getProperty("srctestfolder");

		List<String> sourceTestFolders = new ArrayList<>();

		// adding test folder from the argument
		String[] srcTs = paramTestSrc.split(File.pathSeparator);
		addToFolder(sourceTestFolders, srcTs, originalProjectRoot, !onlyOneFolder);
		if (sourceTestFolders.isEmpty()) {
			// Adding src test folders by guessing potential folders
			String[] possibleTestSrcFolders = new String[] { (originalProjectRoot + File.separator + "src/test/java"),
					(originalProjectRoot + File.separator + "src/test"),
					(originalProjectRoot + File.separator + "test"), };

			addToFolder(sourceTestFolders, possibleTestSrcFolders, originalProjectRoot, onlyOneFolder);
		}
		log.info("Source Test folders: " + sourceTestFolders);
		properties.setTestDirSrc(sourceTestFolders);

		return sourceFolders;

	}

	private void addToFolder(List<String> pathResults, String[] possibleTestSrcFolders, String originalProjectRoot,
			boolean onlyOne) throws IOException {
		boolean added = false;
		for (String possibleSrc : possibleTestSrcFolders) {
			File fSrc = new File(File.separator + possibleSrc).getAbsoluteFile();
			if (Files.exists(fSrc.toPath())) {
				if (!pathResults.contains(fSrc.getAbsolutePath())) {
					pathResults.add(fSrc.getAbsolutePath());
					added = true;
				}

			} else {
				File fSrcRelative = new File(originalProjectRoot + File.separator + possibleSrc);
				if (Files.isDirectory(fSrcRelative.toPath())) {
					if (!pathResults.contains(fSrcRelative.getAbsolutePath())) {
						pathResults.add(fSrcRelative.getAbsolutePath());
						added = true;
					}
				}

			}
			if (onlyOne && added)
				break;
		}
	}

}
