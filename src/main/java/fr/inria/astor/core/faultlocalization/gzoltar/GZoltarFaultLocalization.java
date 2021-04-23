package fr.inria.astor.core.faultlocalization.gzoltar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.log4j.Logger;


import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Facade of Fault Localization techniques like GZoltar or own implementations
 * (package {@link fr.inria.astor.core.faultlocalization}.).
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class GZoltarFaultLocalization implements FaultLocalizationStrategy {

	public static final String PACKAGE_SEPARATOR = "-";
	Logger logger = Logger.getLogger(GZoltarFaultLocalization.class.getName());

	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade project,
			List<String> regressionTestForFaultLocalization) throws Exception {

		return this.calculateSuspicious(
				ConfigurationProperties.getProperty("location") + File.separator
						+ ConfigurationProperties.getProperty("srcjavafolder"),
				project.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				ConfigurationProperties.getProperty("packageToInstrument"), ProgramVariant.DEFAULT_ORIGINAL_VARIANT,
				project.getProperties().getFailingTestCases(), regressionTestForFaultLocalization,
				ConfigurationProperties.getPropertyBool("regressionforfaultlocalization"), project);

	}

    /**
     * This method is the private pardon to the
     * above given public API "calculateSuspicious(ProjectRepairFacade,List<Tests>)".
	 *
	 * It starts actual system processes invoking gzoltar and creates a folder for the generated files.
     *
     * The method does the following:
     * 1. Test & Project Setup
     * 2. Run Gzoltar on it
     * 3. Check the Gzoltar output for validity and filter/change according to parameters
     * 4. Read & Transform the Gzoltar Output to Project-Internal FaultLocalizationResult
     *
     * This method is mostly a wrapper around the "searchSuspicious(...)" below, which adds cleaning and sanity checks.
     *
     * @param locationSrc path to the location of sourcecode
     * @param locationBytecode path to the location of ByteCode
     * @param packageToInst A list of packages that will be added to the bytecode, e.g. libraries like JUnit
     * @param mutatorIdentifier
     * @param failingTest A list of the failing tests considered for the fault localization
     * @param allTest A list of all tests considered for the fault localization
     * @param mustRunAllTest whether all tests will be executed, or just
     * @param project
     * @return
     * @throws IllegalArgumentException if either there are no TestCases given / left after filtering, or if no suspicious code was found.
     */
	private FaultLocalizationResult calculateSuspicious(String locationSrc, String locationBytecode,
			String packageToInst, String mutatorIdentifier, List<String> failingTest, List<String> allTest,
			boolean mustRunAllTest, ProjectRepairFacade project) throws Exception {

	    // Step 1: Test & Project Setup
		List<String> testcasesToExecute = null;

		if (mustRunAllTest) {
			testcasesToExecute = allTest;
		} else {
			testcasesToExecute = failingTest;
		}

		locationBytecode = locationBytecode.replace("//","/");

		if (testcasesToExecute == null || testcasesToExecute.isEmpty()) {
			new IllegalArgumentException("Astor needs at least one test case for running");
		}

		logger.info("-Executing Gzoltar classpath: " + locationBytecode + " from " + +testcasesToExecute.size()
				+ " classes with test cases");

		List<String> listTOInst = new ArrayList<String>();
		listTOInst.add(packageToInst);

		List<String> classPath = new ArrayList<String>();

		if (ConfigurationProperties.getPropertyBool("runonoriginalbin")) {
			classPath.addAll(project.getProperties().getOriginalAppBinDir());
			classPath.addAll(project.getProperties().getOriginalTestBinDir());
		} else {
			classPath.add(locationBytecode);
		}

		for (URL dep : project.getProperties().getDependencies()) {
			classPath.add(dep.getPath());
		}

		// Step 2: Run Gzoltar

		FaultLocalizationResult flResult = this.searchSuspicious(locationBytecode, testcasesToExecute, listTOInst,
				classPath, locationSrc);

		List<SuspiciousCode> suspiciousStatemens = flResult.getCandidates();

		// Step 3: Check and Adjust Gzoltar Output

		if ((suspiciousStatemens == null || suspiciousStatemens.isEmpty())
				&& !ConfigurationProperties.getPropertyBool("canhavezerosusp"))
			throw new IllegalArgumentException("No suspicious gen for analyze");

		List<String> failingTestCases = flResult.getFailingTestCases();
		if (ConfigurationProperties.getPropertyBool("ignoreflakyinfl")) {
			addFlakyFailingTestToIgnoredList(failingTestCases, project);
		}

		if (project.getProperties().getFailingTestCases().isEmpty()) {
			logger.debug("Failing test cases was not pass as argument: we use failings from FL "
					+ flResult.getFailingTestCases());
			project.getProperties().setFailingTestCases(failingTestCases);
		}

		// Step 4: Adjust filtered output to interface

		if (ConfigurationProperties.getPropertyBool("filterfaultlocalization")) {
			List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

			for (SuspiciousCode suspiciousCode : suspiciousStatemens) {
				filtercandidates.add(suspiciousCode);
			}
			flResult.setCandidates(filtercandidates);

		}
		return flResult;
	}

	/**
	 * It adds to the ignore list all failing TC that were not passed as argument. \
	 * They are probably flaky test.
	 * 
	 * @param failingTestCases
	 */
	private void addFlakyFailingTestToIgnoredList(List<String> failingTestCases, ProjectRepairFacade project) {
		//
		if (project.getProperties().getFailingTestCases() == null)
			return;
		List<String> originalFailing = project.getProperties().getFailingTestCases();
		List<String> onlyFailingInFL = new ArrayList<>(failingTestCases);
		// we remove those that we already know that fail
		onlyFailingInFL.removeAll(originalFailing);
		logger.debug("failing before " + onlyFailingInFL + ", added to the ignored list");
		String ignoredTestCases = ConfigurationProperties.getProperty("ignoredTestCases");
		for (String failingFL : onlyFailingInFL) {
			ignoredTestCases += File.pathSeparator + failingFL;
		}
		ConfigurationProperties.properties.setProperty("ignoredTestCases", ignoredTestCases);
	}


    /**
     * This method initializes and runs Gzoltar, and returns unfiltered results of the process.
	 * Filtering, such as for suspiciousness or un-interesting classes, is done further downstream.
     * The primary part of the method maps the properties given in parameters and the Astor-Configuration
     * to the attributes required by Gzoltar and runs the Gzoltar-CLI.
     * The Astor-ConfigurationProperties are particularly important for this method.
	 * The properties used are
	 * 	- gzoltarpath
	 * 	- considerzerovaluesusp (downstream)
	 * 	- maxsuspcandidates (downstream)
     *
     * An important sidenote is, if results are found but none is above the threshold,
     * all results are being returned as fitting.
     *
     * At the moment, it fails "gracefully" when run above Java 8.
	 * The gzoltar version required for all elements that include the spectrum is 1.9.3-SNAPSHOT at the moment.
	 * Also, due to serialization in the .ser file by gzoltar, the used CLI and the libraries used here need to match.
     *
     * @param locationBytecode The Path to the ByteCode to be run
     * @param testsToExecute The list of tests to be executed
     * @param toInstrument
     * @param cp A classpath that can be added
     * @param srcFolder The Path to the SourceCode to be analyzed
     * @return
     * @throws Exception Any Exception produced by gz.Run()
     *
	 * When used in this method, the abbreviation "CP" is for "ClassPath".
     * Further Information on AgentConfig
     * https://github.com/GZoltar/gzoltar/blob/master/com.gzoltar.core/src/main/java/com/gzoltar/core/AgentConfigs.java
     */
	protected FaultLocalizationResult searchSuspicious(String locationBytecode, List<String> testsToExecute,
			List<String> toInstrument, List<String> cp, String srcFolder) throws Exception {

		String gzoltarBasePath = ConfigurationProperties.getProperty("gzoltarpath");
		// The 4 dependencies on Gzoltar
		// Fulfill the BasePath to the actual jars
		final String GZOLTARCLIJAR = gzoltarBasePath + File.separator + "gzoltarcli.jar";
		final String GZOLTARAGENTJAR = gzoltarBasePath + File.separator + "gzoltaragent.jar";
		final String JUNITJAR = gzoltarBasePath + File.separator + "junit.jar";
		final String HAMCRESTJAR = gzoltarBasePath + File.separator + "hamcrest-core.jar";
		//Check for their existence
		if (!(new File(GZOLTARCLIJAR)).exists()||!(new File(GZOLTARAGENTJAR)).exists()
			|| !(new File(HAMCRESTJAR)).exists() || !(new File(JUNITJAR)).exists())
		{
			throw new UnsupportedOperationException(
					"At least one of the Gzoltar Dependencies (CLI,Agent,Hamcrest or JUnit) is not found under the given path '" + gzoltarBasePath + "'");
		}

		// The projectLocationDirectory is the Directory containing the .class files of the un-instrumented code
		File projLocationFile = new File(locationBytecode + File.separator);
		if (!projLocationFile.exists()||!projLocationFile.isDirectory()){
			throw new UnsupportedOperationException("The found project location directory does not exist or is not a folder");
		}
		logger.debug("Gzoltar run over: " + projLocationFile.getAbsolutePath());

        // This temporary folder should keep
		// 	1) the instrumented class files
		// 	2) the .ser file created by gzoltar
		// 	3) (maybe) the reports created before reading them in
		// If it does not exist, make it, otherwise remove all old content
        File tmpDir = new File("/tmp/astor/");
        if (!tmpDir.exists()){
            tmpDir.mkdirs();
        } else {
        	for (File f : tmpDir.listFiles())
        		f.delete();
		}

		logger.debug("Running Gzoltar CLI 'version'");
        // Run the Version first (just to have another sanity check)
		new ProcessBuilder()
				//The Command MUST be as varargs or a list, putting the command in a single string doesn't work
				.command("java", "-jar",GZOLTARCLIJAR,"version")
				.redirectOutput(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_version.txt"))
				.directory(tmpDir)
				.start();

		logger.debug("Running Gzoltar CLI 'listTestMethods'");
        // Get the Gzoltar Tests, they are written to tests.txt
		String testMethodCp = projLocationFile.getAbsolutePath() + ":"+GZOLTARAGENTJAR+":"+GZOLTARCLIJAR;
		Process testMethodProcess = new ProcessBuilder()
				//The Command MUST be as varargs or a list, putting the command in a single string doesn't work
				.command("java", "-cp",testMethodCp,"com.gzoltar.cli.Main","listTestMethods", projLocationFile.getAbsolutePath())
				.redirectOutput(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_listTestMethods_output.txt"))
				.directory(tmpDir)
				.start();
		testMethodProcess.waitFor();
		File testsTxt = new File(tmpDir + File.separator + "tests.txt");
		if(!testsTxt.exists() || !testsTxt.isFile()){
			throw new UnsupportedOperationException("Tests.txt was not created/found");
		}

		logger.debug("Running Gzoltar CLI 'instrument'");
		String instrumentationCP = testMethodCp;
		Process instrumentationProcess = new ProcessBuilder()
				.command("java","-cp",instrumentationCP,
						"com.gzoltar.cli.Main","instrument"
						,projLocationFile.getAbsolutePath(),
						"--outputDirectory","./instrumented")
				.redirectOutput(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_instrument_output.txt"))
				.directory(tmpDir)
				.start();
		instrumentationProcess.waitFor();

		File instrumentationFile = new File(tmpDir+File.separator+"instrumented");
		if(!instrumentationFile.exists() || !instrumentationFile.isDirectory()){
			throw new UnsupportedOperationException("Instrumentation Directory was not created/found");
		}

		// Unlike the other CPs, running the test needs the instrumented classes but NOT the original ones in the classpath.
		String runTestsCP = instrumentationFile.getAbsolutePath()
				+ ":" + HAMCRESTJAR  + ":" + GZOLTARCLIJAR + ":" + GZOLTARAGENTJAR;
		logger.debug("Running Gzoltar CLI 'runTestMethods'");
		Process testProcess = new ProcessBuilder()
				.command("java","-cp",runTestsCP,
						//"-Dgzoltar-agent.output=\"file\"",
						//"-Dgzoltar-agent.destfile=\"./gzoltar.ser\"",
						"com.gzoltar.cli.Main",
						"runTestMethods",
						"--testMethods",testsTxt.getAbsolutePath(),
						"--offline","--collectCoverage")
				.redirectOutput(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_runTest_output.txt"))
				.directory(tmpDir)
				.start();
		testProcess.waitFor();


		File serFile = new File(tmpDir+File.separator+"gzoltar.ser");
		if(!serFile.exists() || !serFile.isFile()){
			throw new UnsupportedOperationException("gzoltar.ser was not created/found");
		}

		logger.debug("Running Gzoltar CLI 'faultLocalizationReport'");
		String reportCP = instrumentationCP + ":" + testMethodCp;
		Process reportProcess = new ProcessBuilder()
				.command("java","-cp",reportCP,
						"com.gzoltar.cli.Main",
						"faultLocalizationReport",
						// Configuration of the CLI
						"--buildLocation",instrumentationFile.getAbsolutePath(),
						"--dataFile", serFile.getAbsolutePath(),
						"--outputDirectory",tmpDir+File.separator+"reports",
						"--formatter","txt",
						// Configuration of the reports
						"--family","sfl",
						"--formula","ochiai",
						"--granularity","line",
						"--metric","entropy")
				.redirectOutput(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_runReport_output.txt"))
				.redirectError(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_runReport_error.txt"))
				.directory(tmpDir)
				.start();
		reportProcess.waitFor();

		// The report files as txt files are always under $givenDir/sfl/txt/xxx.csv
		// sfl is short for spectrum-based-fault-localization
		File reportsDirectory = new File(tmpDir+File.separator+"reports");
		if(!reportsDirectory.exists() || !reportsDirectory.isDirectory()){
			throw new UnsupportedOperationException("Reports were not created/found at "+reportsDirectory.getAbsolutePath());
		}

		FaultLocalizationResult result = parseOutputFile(new File(tmpDir+File.separator+"reports"
				+File.separator+"sfl"+File.separator+"txt"),0.1);

		//TODO: Do I have to run suspCode.setCoveredByTests ?

        return result;
	}

	/**
	 * This method looks up the tests.csv and ochiai.ranking.csv created by gzoltar and parses them into the system-internal
	 * objects for further use.
	 *
	 * There are two system-properties used:
	 * 	- considerzerovaluesusp: whether 0 suspiciousness is allowed to be considered a candidate
	 * 	- maxsuspcandidates: upper limit for the results
	 *
	 * If there are no elements with enough suspiciousness, all elements are seen as candidates.
	 * These candidates are still filtered by the system properties.
	 *
	 * The method will fail if the files are not found under their location.
	 * The headers & formats used by the parsing match Gzoltar 1.7.3-SNAPSHOT and might be subject to change in later versions.
	 * @param path The directory containing the reports created by gzoltar.
	 *             Must contain a tests.csv and an ochiai.ranking.csv.
	 * @param minimumSuspiciousness The minimum suspiciousness a line must have to be included in the candidates. Must be greater than the threshold.
	 * @return The suspicious lines of code found in the gzoltar reports.
	 */
	public FaultLocalizationResult parseOutputFile(File path, Double minimumSuspiciousness) {
		// Paths to the data files
		File testpath = new File(path.getAbsolutePath() + File.separator + "tests.csv");
		File ochiaiFile = new File(path.getAbsolutePath() + File.separator +"ochiai.ranking.csv");
		// Short checks for existance - exit early and hard if missing
		if(!testpath.exists() || !testpath.isFile()){
			throw new UnsupportedOperationException("Tests.csv was not created/found");
		}
		if(!ochiaiFile.exists() || !ochiaiFile.isFile()){
			throw new UnsupportedOperationException("Ochiai.csv was not created/found");
		}
		// Aggregator objects
		List<SuspiciousCode> codeCandidates = new ArrayList<>();
		List<SuspiciousCode> codes = new ArrayList<>();
		Set<String> failingTestCases = new HashSet<>();

		try (BufferedReader br = new BufferedReader(new FileReader(ochiaiFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				// At this point, just add all non-null elements to the candidates
				// Filtering is done below
				SuspiciousCode sc = parseLineOchiaiLine(line);
				if (sc != null )
					codeCandidates.add(sc);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// add the according suspicious values to the results
		for (SuspiciousCode c : codeCandidates){
			if(c.getSuspiciousValue() > minimumSuspiciousness){
				codes.add(c);
			}
		}
		// If there are none, just add all candidates (current default behaviour of astor)
		if (codes.isEmpty())
			codes.addAll(codeCandidates);
		// Limit and filter return values by the system property
		codes = codes.stream()
				// Iff zero values are allowed, filter nothing
				// Otherwise filter out 0 values
				.filter(c ->
						ConfigurationProperties.getPropertyBool("considerzerovaluesusp") && c.getSuspiciousValue() > 0
				)
				// Sort entries by their suspiciousness descending (most susp first)
				.sorted((a,b)->Double.compare(a.getSuspiciousValue(),b.getSuspiciousValue()))
				// Limit to the first n given by system properties
				.limit(ConfigurationProperties.getPropertyInt("maxsuspcandidates"))
				.collect(Collectors.toList());

		logger.debug("Reading the report.csv worked and found " + codes.size() + " lines of suspicious code");
		// A line of the tests.csv looks the following:
		// nl.tudelft.mutated_rers.Problem1_ESTest#test002,FAIL,4395022,java.lang.IllegalArgumentException:
		final String testCSVHeader = "name,outcome,runtime,stacktrace";
		try (BufferedReader br = new BufferedReader(new FileReader(testpath))) {
			String test_line;
			while ((test_line = br.readLine()) != null) {
				// Skip the header
				if (test_line.equals(testCSVHeader))
					continue;
				logger.trace(test_line);
				// Not all of these attributes are actually used,
				// But I found them useful during the debugging process
				String[] linePieces = test_line.split(",");
				String testName = linePieces[0].split("#")[1];
				String testclassName = linePieces[0].split("#")[1];
				String status = linePieces[1];
				// Stacktrace is only around for failing tests
				if(linePieces.length>2) {
					String stacktrace = linePieces[2];
				}
				if(status.equals("FAIL")){
					failingTestCases.add(testName);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debug("Reading the tests.csv worked and found " + failingTestCases.size() + " failing tests");

		codes.forEach(e -> logger.trace(e));
		failingTestCases.forEach(e -> logger.trace(e));
		FaultLocalizationResult result = new FaultLocalizationResult(codes, failingTestCases.stream().collect(Collectors.toList()));

		return result;
	}

	/**
	 * This method parses a line of the ochiai.csv created by gzoltar into this projects internal suspicious code.
	 * A line of the file looks as following:
	 *	name;suspiciousness_value
	 *	nl.tudelft.mutated_rers$Problem1_ESTest#test002():39;1.0
	 * Where the name follows the pattern
	 *	package$class#method:line
	 * The suspiciousness-value is a double between 0 and 1.
	 *
	 * There was an issue that some lines are of the format
	 * 	org.apache.commons.math.complex$ComplexTest$TestComplex#ComplexTest$TestComplex(double,double):962;0.0
	 * So they contain multiple $ signs, which represent inner classes.
	 * This is currently addressed by picking the first class as the class name and the first method as the method name.
	 * While I have not seen examples with multiple #'s, it is addressed in the same way.
	 *
	 *
	 * This format matches the output of Gzoltar Version 1.7.3-SNAPSHOT
	 * @param line a single line of the ochiai.csv
	 * @return the parsed object created from an ochiai line. Null for the header. Null in case of parsing error.
	 *
	 * This method should work for other reports as well (e.g. a "tarantula.csv" created by gzoltar).
	 * Could be made static, but has been made non-static to utilize the logger.
	 */
	public SuspiciousCode parseLineOchiaiLine(String line) {
		//Shortcut: header returns null
		if (line.equals("name;suspiciousness_value"))
			return null;
		//Shortcut: Format does not provide atleast on class and method
		if (!line.contains("$") || !line.contains("#"))
			return null;
		final String CSV_SEPARATOR = ";";

		String location = line.split(CSV_SEPARATOR)[0];
		String susp = line.split(CSV_SEPARATOR)[1];
		//For parsing the pieces, put everything into a try catch block
		try {
			// Cut the location into pieces according to the separators
			// "$" is a regex character, and must be escaped with a double \ beforehand
			String[] packagePieces = location.split("\\$");
			String packageName = packagePieces[0];

			// ClassName and MethodName are the outermost instances of these in the files, in case the file contains inner classes etc.
			String className = firstAfter(location,"$");
			String methodName = firstAfter(location,"#");

			// The pattern of taking the last piece should be irrelevant for linenumbers,
			// as unlike classes/methods linenumbers should be unique
			int lineNumber = Integer.parseInt(location.split(":")[1]);

			// Create a new suspicious code object and set the found values
			SuspiciousCode sc = new SuspiciousCode();

			sc.setSusp(Double.parseDouble(susp));

			sc.setLineNumber(Integer.valueOf(lineNumber));

			sc.setClassName(className);
			sc.setMethodName(methodName);

			// TODO: Does the classname should have package prefix?
			// TODO: Is the FileName required and where do I get it from?

			return sc;
		} catch (Exception e) {
			logger.error("Issue with parsing line: " + line);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * This method is a short helper as it turned out to be rather complex to get the classes and methods
	 * when there are private inner classes and methods in play.
	 * This method returns the first element that comes AFTER a markup sign (e.g. $)
	 * but ends the string after any other markup Sign.
	 *
	 * Returns null if there was no markup Sign in the stringToSplit. Returns null if either parameter is null.
	 * @param stringToSplit The string in which to search
	 * @param markupSign The sign to find a substring after, one of "$","#",":"
	 * @return The first sub-string between the markup sign and any other char "$","#",":". Null if there was none.
	 */
	public static String firstAfter(String stringToSplit, String markupSign){
		if (stringToSplit == null || markupSign == null) {
			return null;
		}
		String[] pieces;
		if(markupSign.equals("$"))
			pieces = stringToSplit.split("\\$");
		else
			pieces = stringToSplit.split(markupSign);

		if (pieces.length <= 1) {
			return null;
		}
		String firstPiece = pieces[1];

		String returnValue =
				firstPiece
						.split("\\$")[0] // Cut off any other classes
						.split("#")[0] // Cut off any other methods
						.split(":")[0]; // Cut off linenumber

		return returnValue;
	}

	/*
        Short Interface to compare two pieces of susipicious code.
        It accepts null-values and defaults to 0, otherwise it compares the values found.
         */
	public class ComparatorCandidates implements Comparator<SuspiciousCode> {

		@Override
		public int compare(SuspiciousCode o1, SuspiciousCode o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return Double.compare(o2.getSuspiciousValue(), o1.getSuspiciousValue());
		}

	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {
		List<String> testCasesToRun = FinderTestCases.findJUnit4XTestCasesForRegression(projectFacade);
		return testCasesToRun;
	}


}
