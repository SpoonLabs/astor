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
import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.factory.Factory;

/**
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
		options.addOption("dependencies", true, "dependencies of the application, separated by char "
				+ File.pathSeparator);
		options.addOption("package", true, "package to instrument e.g. org.commons.math");
		options.addOption("failing", true, "failing test cases, separated by Path separator char (: in linux/mac  and ; in windows)");
		options.addOption("out", true,
				"(Optional) Out dir: place were solutions and intermediate program variants are stored. (Default: ./outputMutation/)");
		options.addOption("help", false, "print help and usage");
		options.addOption("bug280", false, "Run the bug 280 from Apache Commons Math");
		options.addOption("bug288", false, "Run the bug 288 from Apache Commons Math");
		options.addOption("bug309", false, "Run the bug 309 from Apache Commons Math");
		options.addOption("bug428", false, "Run the bug 428 from Apache Commons Math");
		options.addOption("bug340", false, "Run the bug 340 from Apache Commons Math");

		// Optional parameters
		options.addOption(
				"jvm4testexecution",
				true,
				"(Optional) location of JVM that executes the mutated version of a program (Folder that contains java script, such as /bin/ ). For the examples, run jdk 6");
		options.addOption("maxgen", true, "(Optional) max number of generation a program variant is evolved");
		options.addOption("population", true,
				"(Optional)number of population (program variants) that the approach evolves");
		
		options.addOption("maxtime", true, "(Optional) maximum time (in minutes) to execute the whole experiment");

		options.addOption("validation", true, "(Optional) type of validation: process|thread|local ");
		options.addOption("flthreshold", true, "(Optional) threshold for Fault locatication. Default:"+ConfigurationProperties.properties.getProperty("flthreshold"));
		
		options.addOption("maxsuspcandidates", true, "(Optional) Maximun number of suspicious statement considered. Default: "+ConfigurationProperties.properties.getProperty("maxsuspcandidates"));

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
		options.addOption(
				"allgens",
				true,
				"(Optional) True if analyze all gens of a program validation during a generation. False for analyzing only one gen per generation.");

		options.addOption("savesolution", false,
				"(Optional) Save on disk intermediate program variants (even those that do not compile)");
		options.addOption("saveall", false, "(Optional) Save on disk all program variants generated");

		options.addOption("testbystep", false, "(Optional) Executes each test cases in a separate process.");

		options.addOption(
				"genlistnavigation",
				true,
				"(Optional) Method to navigate the gen space of a variant: inorder, random, weight random (according to the gen's suspicous value)");

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
	
		options.addOption("multigenmodif", false,
				"(Optional) An element of a program variant (i.e., gen) can be modified several times in different generation");
	
		options.addOption("uniqueoptogen", true,
				"(Optional) An operation can be applied only once to a gen, even this operation is in other variant.");
		
		options.addOption("resetoperations", false,
				"(Optional) The program variants do not pass the operators throughs the generations");
	
		
		options.addOption("regressionforfaultlocalization", true,
				"(Optional) Use the regression for fault localization.Otherwise, failing test cases. Default: "+ConfigurationProperties.properties.getProperty("regressionforfaultlocalization"));
		
		options.addOption("javacompliancelevel", true,
				"(Optional) Compliance level (e.g., 7 for java 1.7, 6 for java 1.6). Default Java 1.7");
	
		options.addOption("alternativecompliancelevel", true,
				"(Optional) Alternative compliance level. Default Java 1.4. Used after Astor tries to compile to the complicance level and fails.");

		options.addOption("seed", true,
                          "(Optional) Random seed, for reproducible runs.  Default is whatever java.util.Random uses when not explicitly initialized.");
	
		options.addOption("scope", true,
                "(Optional) Scope of the ingredient seach space: Local (same class), package (classes from the same package) or global (all classes from the application under analysis). Default: local.");

		options.addOption("skipfaultlocalization", false,
				"The fault localization is skipped and all statements are considered");
	
		
	}


	public abstract void run(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception;

    // CLG notes that this slightly modifies the semantics of example execution,
    // such that it now will execute examples for non-GenProg tools as well.
    // This may not be desired, but it was easier to implement this way, for
    // several reasons, and she can't see a reason that the examples *wouldn't*
    // work for the other tools. 
    private boolean isExample(CommandLine cmd) {
        String[] examples = { "bug280", "bug288", "bug340", "bug428" } ; 
        for (String example : examples ) {
            if(cmd.hasOption(example)) {
                return true;
            }
        }
        return false;
    }

    public boolean isExample(String [] args) {
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
			ConfigurationProperties.properties
					.setProperty("jvm4testexecution", cmd.getOptionValue("jvm4testexecution"));
		}else{
			String javahome = System.getProperty("java.home");
			File location = new File(javahome);
			if(location.getName().equals("jre")){
				javahome = location.getParent() + File.separator + "bin";
				File javalocationbin = new File(javahome);
				if(!javalocationbin.exists()){
					System.err.println("Problems to generate java jdk path");
					return false;
				}
			}
			ConfigurationProperties.properties
			.setProperty("jvm4testexecution", javahome);

		}
	
		if (!ProjectConfiguration.validJDK()){
			System.out.println("Error: invalid jdk folder");
			return false;
		}
         
        if(!this.isExample(cmd)) {   
            String dependenciespath = cmd.getOptionValue("dependencies");
            String failing = cmd.getOptionValue("failing");
            String location = cmd.getOptionValue("location");
          
            
            // Process mandatory parameters.
            if (failing == null || location == null) {
                help();
                return false;
            }

            ConfigurationProperties.properties.setProperty("dependenciespath", dependenciespath);
            ConfigurationProperties.properties.setProperty("failing", failing);
            ConfigurationProperties.properties.setProperty("location", location);
        }
      
        if (cmd.hasOption("package"))
			ConfigurationProperties.properties.setProperty("packageToInstrument", cmd.getOptionValue("package"));
        
        
		if (cmd.hasOption("id"))
			ConfigurationProperties.properties.setProperty("projectIdentifier", cmd.getOptionValue("id"));

		if (cmd.hasOption("mode"))
			ConfigurationProperties.properties.setProperty("mode", cmd.getOptionValue("mode"));

		String outputPath = "";
		if (cmd.hasOption("out")){
			outputPath =  cmd.getOptionValue("out");
		}else{
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
			ConfigurationProperties.properties.setProperty("maxsuspcandidates", cmd.getOptionValue("maxsuspcandidates"));
			
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

		if (cmd.hasOption("multigenmodif"))
			ConfigurationProperties.properties.setProperty("multigenmodif", "true");
		
		if (cmd.hasOption("javacompliancelevel"))
			ConfigurationProperties.properties.setProperty("javacompliancelevel", cmd.getOptionValue("javacompliancelevel"));
	
		if (cmd.hasOption("alternativecompliancelevel"))
			ConfigurationProperties.properties.setProperty("alternativecompliancelevel", cmd.getOptionValue("alternativecompliancelevel"));
	
		if (cmd.hasOption("resetoperations"))
			ConfigurationProperties.properties.setProperty("resetoperations", "true");
	
		if (cmd.hasOption("regressionforfaultlocalization"))
			ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", cmd.getOptionValue("regressionforfaultlocalization"));
		
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
		if (cmd.hasOption("bug280")) {
			String dependenciespath = "examples/math_85/libs/junit-4.4.jar";
			String folder = "Math-issue-280";
			String failing = "org.apache.commons.math.distribution.NormalDistributionTest";
			String location = "examples/Math-issue-280/";
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.5;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}

		if (cmd.hasOption("bug288")) {
			String dependenciespath = "examples/Math-issue-288/lib/junit-4.4.jar";
			String folder = "Math-issue-288";
			String failing = "org.apache.commons.math.optimization.linear.SimplexSolverTest";
			String location = "examples/Math-issue-288/";
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.2;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}
		if (cmd.hasOption("bug309")) {
			String dependenciespath = "examples/Math-issue-309/lib/junit-4.4.jar";
			String folder = "Math-issue-309";
			String failing = "org.apache.commons.math.random.RandomDataTest";
			String location =("examples/Math-issue-309/");
			String packageToInstrument = "org.apache.commons";
			double thfl = 0.5;
			this.run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
			return true;
		}
		if (cmd.hasOption("bug340")) {
			String dependenciespath = "examples/Math-issue-340/lib/junit-4.4.jar";
			String folder = "Math-issue-340";
			String failing = "org.apache.commons.math.fraction.BigFractionTest";
			String location =("examples/Math-issue-340/");
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
			String location =("examples/Lang-issue-428/");
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

	
	

	protected ProjectRepairFacade getProject(String location, String projectIdentifier, String method,
			List<String> failingTestCases, String dependencies, boolean srcWithMain)
			throws Exception {

		if (projectIdentifier == null || projectIdentifier.equals("")) {
			File locFile = new File(location);
			projectIdentifier = locFile.getName();
		}

		String key = File.separator + method + "-" + projectIdentifier + File.separator;
		String inResult = ConfigurationProperties.getProperty("workingDirectory") + key + "/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory") + key + "/bin/";
		String originalProjectRoot = location + File.separator;
		// location + File.separator + projectIdentifier + File.separator;

		List<String> src = determineMavenFolders(srcWithMain, originalProjectRoot);

		String libdir = dependencies;

		//String mainClassTest = regressionTest;

		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setOriginalAppBinDir(originalProjectRoot + File.separator
				+ ConfigurationProperties.getProperty("binjavafolder"));
		properties.setOriginalTestBinDir(originalProjectRoot + File.separator
				+ ConfigurationProperties.getProperty("bintestfolder"));
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

		File srcdefault = new File(originalProjectRoot + File.separator
				+ ConfigurationProperties.getProperty("srcjavafolder"));
		
		File testdefault = new File(originalProjectRoot + File.separator
				+ ConfigurationProperties.getProperty("srctestfolder"));
		
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
