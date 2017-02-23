package fr.inria.main;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.loop.population.FitnessFunction;
import fr.inria.astor.core.loop.population.PopulationController;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.AstorCtIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.validation.validators.ProgramValidator;
import fr.inria.astor.util.TimeUtil;
import spoon.reflect.factory.Factory;

/**
 * Abstract entry point of the framework. It defines and manages program
 * arguments.
 * 
 * @author Matias Martinez
 * 
 */
public abstract class AbstractMain {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	protected MutationSupporter mutSupporter;
	protected Factory factory;
	protected ProjectRepairFacade projectFacade;

	static Options options = new Options();

	CommandLineParser parser = new BasicParser();

	static {
		options.addOption("id", true, "(Optional) Name/identified of the project to evaluate (Default: folder name)");
		options.addOption("mode", true, " (Optional) Mode (Default: Statement Mode)");
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

		options.addOption("validation", true, "(Optional) type of validation: process|evosuite. Default:"
				+ ConfigurationProperties.properties.getProperty("validation")
				+ "It accepts custormize validation prodedures, which must extend from "+ProgramValidator.class.getCanonicalName());
		options.addOption("flthreshold", true, "(Optional) threshold for Fault locatication. Default:"
				+ ConfigurationProperties.properties.getProperty("flthreshold"));

		options.addOption("maxsuspcandidates", true,
				"(Optional) Maximun number of suspicious statement considered. Default: "
						+ ConfigurationProperties.properties.getProperty("maxsuspcandidates"));

		options.addOption("reintroduce", true,
				"(Optional) indicates whether it reintroduces the original program in each generation (value: original), "
						+ " introduces parent variant in next generation (value: parents), "
						+ "introduces origina and parents (value: original-parents) or none (value: none). (default: original-parents)");

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
				+ " It accepts customize scopes, which must implement from "+AstorCtIngredientSpace.class.getCanonicalName());

		options.addOption("skipfaultlocalization", false,
				"The fault localization is skipped and all statements are considered");

		options.addOption("maxdate", true,
				"(Optional) Indicates the hour Astor has to stop processing. it must have the format: HH:mm");

		options.addOption("customop", true,
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
						+ FitnessFunction.class.getCanonicalName()
						+ " The classes must be included in the classpath.");
		
		options.addOption("populationcontroller", true,
				"(Optional) class name that controls the population evolution. It must extend from  "
						+ PopulationController.class.getCanonicalName()
						+ " The classes must be included in the classpath.");


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
		log.info("command line arguments: " + Arrays.toString(args).replace(",", " "));

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
			if((failing != null))
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
				double thfl = Double.valueOf(cmd.getOptionValue("flthreshold"));
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

		if (cmd.hasOption("customop"))
			ConfigurationProperties.properties.setProperty("customop", cmd.getOptionValue("customop"));

		if (cmd.hasOption("ingredientstrategy"))
			ConfigurationProperties.properties.setProperty("ingredientstrategy",
					cmd.getOptionValue("ingredientstrategy"));

		if (cmd.hasOption("opselectionstrategy"))
			ConfigurationProperties.properties.setProperty("opselectionstrategy",
					cmd.getOptionValue("opselectionstrategy"));

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
			LogManager.getRootLogger().setLevel(Level.toLevel(loglevelSelected));
		}
		if (cmd.hasOption("timezone")) {
			ConfigurationProperties.properties.setProperty("timezone", cmd.getOptionValue("timezone"));
		}
		
		if (cmd.hasOption("faultlocalization")) {
			ConfigurationProperties.properties.setProperty("faultlocalization", cmd.getOptionValue("faultlocalization"));
		}
		
		if (cmd.hasOption("fitnessfunction")) {
			ConfigurationProperties.properties.setProperty("fitnessfunction", cmd.getOptionValue("faultlocalization"));
		}
		
		if (cmd.hasOption("populationcontroller")) {
			ConfigurationProperties.properties.setProperty("populationcontroller", cmd.getOptionValue("faultlocalization"));
		}
		
		
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
			dependenciespath = "examples/math_85/libs/junit-4.4.jar";
			folder = "Math-issue-280";
			failing = "org.apache.commons.math.distribution.NormalDistributionTest";
			location = "examples/Math-issue-280/";
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

	protected ProjectRepairFacade getProject(String location, String projectIdentifier, String method,
			List<String> failingTestCases, String dependencies, boolean srcWithMain) throws Exception {

		if (projectIdentifier == null || projectIdentifier.equals("")) {
			File locFile = new File(location);
			projectIdentifier = locFile.getName();
		}

		String key = File.separator + method + "-" + projectIdentifier + File.separator;
		String workingDirForSource = ConfigurationProperties.getProperty("workingDirectory") + key + "/src/";
		String workingDirForBytecode = ConfigurationProperties.getProperty("workingDirectory") + key + "/bin/";
		String originalProjectRoot = location + File.separator;

		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setWorkingDirForSource(workingDirForSource);
		properties.setWorkingDirForBytecode(workingDirForBytecode);
		properties.setOriginalAppBinDir(
				originalProjectRoot + File.separator + ConfigurationProperties.getProperty("binjavafolder"));
		properties.setOriginalTestBinDir(
				originalProjectRoot + File.separator + ConfigurationProperties.getProperty("bintestfolder"));
		properties.setFixid(projectIdentifier);

		properties.setOriginalProjectRootDir(originalProjectRoot);

		List<String> src = determineMavenFolders(srcWithMain, originalProjectRoot);
		properties.setOriginalDirSrc(src);

		if (dependencies != null) {
			properties.setDependencies(dependencies);
		}

		properties.setFailingTestCases(failingTestCases);

		properties.setPackageToInstrument(ConfigurationProperties.getProperty("packageToInstrument"));

		properties.setDataFolder(ConfigurationProperties.getProperty("resourcesfolder"));

		ProjectRepairFacade ce = new ProjectRepairFacade(properties);

		return ce;
	}

	private List<String> determineMavenFolders(boolean srcWithMain, String originalProjectRoot) {

		File srcdefault = new File(
				originalProjectRoot + File.separator + ConfigurationProperties.getProperty("srcjavafolder"));

		File testdefault = new File(
				originalProjectRoot + File.separator + ConfigurationProperties.getProperty("srctestfolder"));

		if (srcdefault.exists() && testdefault.exists())
			return Arrays.asList(new String[] { ConfigurationProperties.getProperty("srcjavafolder"),
					ConfigurationProperties.getProperty("srctestfolder") });

		File src = new File(originalProjectRoot + File.separator + "src/main/java");
		if (src.exists())
			return Arrays.asList(new String[] { "src/main/java", "src/test/java" });

		src = new File(originalProjectRoot + File.separator + "src/java");
		if (src.exists())
			return Arrays.asList(new String[] { "src/java", "src/test" });

		return Arrays.asList(new String[] { "src", "test" });

	}

}
