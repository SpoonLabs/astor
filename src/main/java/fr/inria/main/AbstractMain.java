package fr.inria.main;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats;

/**
 * 
 * @author Matias Martinez
 * 
 */
public abstract class AbstractMain {

	protected MutationSupporter mutSupporter;
	protected Factory factory;
	protected ProjectRepairFacade rep;

	static Options options = new Options();

	static {
		options.addOption("l", true, "Name of the folder that contains program to transform");
		options.addOption("d", true, "dependencies of the application, separated by char " + File.pathSeparator);
		options.addOption("p", true, "package to instrument e.g. org.commons.math");
		options.addOption("t", true, "failing test case");
		options.addOption("help", false, "print help and usage");
		options.addOption("bug280", false, "Run the bug 280 from Apache Commons Math");
		options.addOption("bug288", false, "Run the bug 288 from Apache Commons Math");
		options.addOption("bug309", false, "Run the bug 309 from Apache Commons Math");
		options.addOption("bug428", false, "Run the bug 428 from Apache Commons Math");
		options.addOption("bug340", false, "Run the bug 340 from Apache Commons Math");

		// Optional parameters
		options.addOption(
				"jvm",
				true,
				"(Optional) location of JVM that executes the mutated version of a program (Folder that contains java script ). For the examples, run jdk 6");
		options.addOption("maxgen", true, "(Optional) max number of generation a program variant is evolved");
		options.addOption("population", true,
				"(Optional)number of population (program variants) that the approach evolves");
		options.addOption("validation", true, "(Optional) type of validation: process|thread|local ");
		options.addOption("flthreshold", true, "(Optional) threshold for Fault locatication. Default 0.5 ");

		options.addOption("reintroduce", true,
				"(Optional) indicates whether it reintroduces the original program in each generation (default:true)");
		options.addOption("tmax1", true,
				"(Optional) maximum time (in miliseconds) for validating the failing test case ");
		options.addOption("tmax2", true,
				"(Optional) maximum time (in miliseconds) for validating the regression test cases ");
		options.addOption("stopfirst", true,
				"(Optional) Indicates whether it stops when it finds the first solution (default: true)");
		options.addOption(
				"allgens",
				true,
				"(Optional) True if analyze all gens of a program validation during a generation. False for analyzing only one gen per generation.");

		options.addOption("savesolution", false,
				"(Optional) Save on disk intermediate program variants (even those that do not compile)");
		options.addOption("saveall", false, "(Optional) Save on disk the solution variants");

		options.addOption("testbystep", false, "(Optional) Executes each test cases in a separate process.");

		options.addOption(
				"genlistnavigation",
				true,
				"(Optional) Method to navigate the gen space of a variant: inorder, random, weight random (according to the gen's suspicous value)");

		options.addOption("mutationrate", true,
				"Value between 0 and 1 that indicates the probability of modify one gen (default: 1)");

	}

	public void createFactory() {

		if (factory == null) {
			factory = ProjectRepairFacade.createFactory();
			FactoryImpl.getLauchingFactory().getEnvironment().setDebug(true);
		}
		mutSupporter = new MutationSupporter(factory);
	}

	public abstract void run(String location, String projectName, String dependencies, String packageToInstrument)
			throws Exception;

	public abstract void run(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception;

	protected void execute(String[] args) throws Exception {

		CommandLineParser parser = new BasicParser();

		CommandLine cmd = parser.parse(options, args);
		if (cmd.hasOption("help")) {
			help();
			return;
		}

		if (cmd.hasOption("jvm")) {
			ConfigurationProperties.properties.setProperty("jvm4testexecution", cmd.getOptionValue("jvm"));

		}

		if (!ProjectConfiguration.validJDK())
			throw new IllegalArgumentException("jdk folder not found");

		boolean isExample = executeExample(cmd);
		if (isExample)
			return;

		String dependenciespath = cmd.getOptionValue("d");
		String failing = cmd.getOptionValue("t");
		Stats currentStat = new Stats();
		String location = cmd.getOptionValue("l");
		String packageToInstrument = cmd.getOptionValue("p");

		ConfigurationProperties.properties.setProperty("dependenciespath", dependenciespath);
		ConfigurationProperties.properties.setProperty("failing", failing);
		ConfigurationProperties.properties.setProperty("location", location);
		ConfigurationProperties.properties.setProperty(packageToInstrument, packageToInstrument);

		// Process mandatory parameters.
		if (dependenciespath == null || failing == null || location == null || packageToInstrument == null) {
			help();
			return;
		}

		// Process optional values.
		if (cmd.hasOption("maxgen"))
			ConfigurationProperties.properties.setProperty("maxGeneration", cmd.getOptionValue("maxgen"));

		if (cmd.hasOption("population"))
			ConfigurationProperties.properties.setProperty("population", cmd.getOptionValue("population"));

		if (cmd.hasOption("validation"))
			ConfigurationProperties.properties.setProperty("validation", cmd.getOptionValue("validation"));

		double thfl = 0.5;
		if (cmd.hasOption("flthreshold")) {
			try {
				thfl = Double.valueOf(cmd.getOptionValue("flthreshold"));
				ConfigurationProperties.properties.setProperty("flthreshold", cmd.getOptionValue("flthreshold"));

			} catch (Exception e) {
				System.out.println("Error: threshold not valid");
				help();
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

		if (cmd.hasOption("allgens"))
			ConfigurationProperties.properties.setProperty("allgens", cmd.getOptionValue("allgens"));

		if (cmd.hasOption("savesolution"))
			ConfigurationProperties.properties.setProperty("savesolution", "true");

		if (cmd.hasOption("saveall"))
			ConfigurationProperties.properties.setProperty("saveall", "true");

		if (cmd.hasOption("testbystep"))
			ConfigurationProperties.properties.setProperty("testbystep", "true");

		if (cmd.hasOption("genlistnavigation"))
			ConfigurationProperties.properties
					.setProperty("genlistnavigation", cmd.getOptionValue("genlistnavigation"));

		if (cmd.hasOption("mutationrate"))
			ConfigurationProperties.properties.setProperty("mutationrate", cmd.getOptionValue("mutationrate"));

		this.run(location, null, dependenciespath, packageToInstrument, thfl, failing);
	}

	/**
	 * Finds an example to test in the command line
	 * 
	 * @param cmd
	 * @return
	 * @throws Exception
	 */
	private boolean executeExample(CommandLine cmd) throws Exception {

		if (cmd.hasOption("bug280")) {
			String dependenciespath = "examples/Math-issue-280/lib/junit-4.4.jar";
			String folder = "Math-issue-280";
			String failing = "org.apache.commons.math.distribution.NormalDistributionTest";
			Stats currentStat = new Stats();
			File f = new File("examples/Math-issue-280/");
			String location = f.getParent();
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.5;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}

		if (cmd.hasOption("bug288")) {
			String dependenciespath = "examples/Math-issue-288/lib/junit-4.4.jar";
			String folder = "Math-issue-288";
			String failing = "org.apache.commons.math.optimization.linear.SimplexSolverTest";
			File f = new File("examples/Math-issue-288/");
			String location = f.getParent();
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.2;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}
		if (cmd.hasOption("bug309")) {
			String dependenciespath = "examples/Math-issue-309/lib/junit-4.4.jar";
			String folder = "Math-issue-309";
			String failing = "org.apache.commons.math.random.RandomDataTest";
			File f = new File("examples/Math-issue-309/");
			String location = f.getParent();
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.5;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}
		if (cmd.hasOption("bug340")) {
			String dependenciespath = "examples/Math-issue-340/lib/junit-4.4.jar";
			String folder = "Math-issue-340";
			String failing = "org.apache.commons.math.fraction.BigFractionTest";
			File f = new File("examples/Math-issue-340/");
			String location = f.getParent();
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.5;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}
		if (cmd.hasOption("bug428")) {
			String dependenciespath = "examples/Lang-issue-428/lib/easymock-2.5.2.jar" + File.pathSeparator
					+ "examples/Lang-issue-428/lib/junit-4.7.jar";
			String folder = "Lang-issue-428";
			String failing = "org.apache.commons.lang3.StringUtilsIsTest";
			File f = new File("examples/Lang-issue-428/");
			String location = f.getParent();
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.5;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
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

	protected Factory getFactory() {
		Factory facade = FactoryImpl.getLauchingFactory();

		if (facade == null) {
			facade = rep.createFactory();
			FactoryImpl.getLauchingFactory().getEnvironment().setDebug(true);
		}
		return facade;
	}

	protected ProjectRepairFacade getProject(String location, String method, String regressiontest,
			List<String> failingTestCases, String dependencies, boolean srcWithMain) throws Exception {
		File locFile = new File(location);
		String projectname = locFile.getName();

		return getProject(location, projectname, method, regressiontest, failingTestCases, dependencies, srcWithMain);
	}

	protected ProjectRepairFacade getProject(String location, String projectIdentifier, String method,
			String regressionTest, List<String> failingTestCases, String dependencies, boolean srcWithMain)
			throws Exception {

		if (projectIdentifier == null) {
			File locFile = new File(location);
			projectIdentifier = locFile.getName();
		}

		String key = File.separator + method + "-" + projectIdentifier + File.separator;
		String inResult = ConfigurationProperties.getProperty("workingDirectory") + key + "/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory") + key + "/bin/";
		String originalProjectRoot = location + File.separator + projectIdentifier + File.separator;

		List<String> src = determineMavenFolders(srcWithMain, originalProjectRoot);

		String libdir = dependencies;

		String mainClassTest = regressionTest;

		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		properties.setOriginalAppBinDir(originalProjectRoot + "/target/classes");
		properties.setOriginalTestBinDir(originalProjectRoot + "/target/test-classes");
		properties.setFixid(projectIdentifier);

		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		properties.setFailingTestCases(failingTestCases);

		properties.setPackageToInstrument(ConfigurationProperties.getProperty("packageToInstrument"));

		ProjectRepairFacade ce = new ProjectRepairFacade(properties);

		return ce;
	}

	private List<String> determineMavenFolders(boolean srcWithMain, String originalProjectRoot) {
		File src = new File(originalProjectRoot + File.separator + "src/main/java");
		if (src.exists())
			return Arrays.asList(new String[] { "src/main/java", "src/test/java" });
		else {
			return Arrays.asList(new String[] { "src/java", "src/test" });
		}
	}

}
