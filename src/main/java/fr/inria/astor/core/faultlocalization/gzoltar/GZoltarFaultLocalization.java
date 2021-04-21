package fr.inria.astor.core.faultlocalization.gzoltar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.gzoltar.core.AgentConfigs;
import com.gzoltar.core.instr.granularity.GranularityLevel;
import com.gzoltar.core.spectrum.ISpectrum;
import com.gzoltar.fl.FaultLocalization;
import com.gzoltar.fl.FaultLocalizationFamily;
import com.gzoltar.report.fl.config.ConfigFaultLocalizationFamily;
import com.gzoltar.report.fl.config.ConfigFaultLocalizationReport;
import com.gzoltar.sfl.SFLFormulas;
import org.apache.log4j.Logger;

import com.gzoltar.report.fl.FaultLocalizationReportBuilder;
import com.gzoltar.core.test.TestResult;

//import com.gzoltar.core.GZoltar;
//import com.gzoltar.core.components.Statement;
//import com.gzoltar.core.instr.testing.TestResult;


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
	 * Filtering, such as for suspiciousness or un-interesting classes, is done further upstream.
     *
     * The primary part of the method maps the properties given in parameters and the Astor-Configuration
     * to the attributes required by Gzoltar.
     * The Astor-ConfigurationProperties are particularly important for this method.
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
     *
     */
	protected FaultLocalizationResult searchSuspicious(String locationBytecode, List<String> testsToExecute,
			List<String> toInstrument, List<String> cp, String srcFolder) throws Exception {

		// The 4 dependencies on Gzoltar
		// TODO: De-hardcode these into system configuration
		final String GZOLTARCLIJAR = System.getProperty("user.home")+"/Code/astor/lib/moderngzoltar/gzoltarcli.jar";
		final String GZOLTARAGENTJAR = System.getProperty("user.home")+"/Code/astor/lib/moderngzoltar/gzoltaragent.jar";
		final String JUNITJAR = System.getProperty("user.home")+"/Code/astor/lib/moderngzoltar/junit.jar";
		final String HAMCRESTJAR = System.getProperty("user.home")+"/Code/astor/lib/moderngzoltar/hamcrest-core.jar";
		//Check for their existence
		if (!(new File(GZOLTARCLIJAR)).exists()||!(new File(GZOLTARAGENTJAR)).exists()
			|| !(new File(HAMCRESTJAR)).exists() || !(new File(JUNITJAR)).exists())
		{
			throw new UnsupportedOperationException(
					"Atleast one of the Gzoltar Dependencies (CLI,Agent,Hamcrest or JUnit) is not found under their given path");
		}

		// The projectLocationDirectory is the Directory containing the .class files of the un-instrumented code
		File projLocationFile = new File(locationBytecode + File.separator);
		if (!projLocationFile.exists()||!projLocationFile.isDirectory()){
			throw new UnsupportedOperationException("The found project location directory does not exist or is not a folder");
		}
		logger.debug("Gzoltar run over: " + projLocationFile.getAbsolutePath());

		// The agent configs would be required for a newly created gzoltar-agent,
		// but we run it from console, so this one can be (mostly) empty.
        AgentConfigs agentConfigs = new AgentConfigs();
		// This is the default-finest granularity, others are method or class
		// A statement/expression granularity is not possible by default Gzoltar
        agentConfigs.setGranularity(GranularityLevel.LINE);

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

        // Run the Version first (just to have another sanity check)
		new ProcessBuilder()
				//The Command MUST be as varargs or a list, putting the command in a single string doesn't work
				.command("java", "-jar",GZOLTARCLIJAR,"version")
				.redirectOutput(new File(tmpDir.getAbsolutePath()+File.separator+"gzoltar_version.txt"))
				.directory(tmpDir)
				.start();

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
		File ochiaiFile = new File(tmpDir+File.separator+"reports"
				+File.separator+"sfl"+File.separator+"txt"+File.separator+"ochiai.ranking.csv");
		if(!ochiaiFile.exists() || !ochiaiFile.isFile()){
			throw new UnsupportedOperationException("Reports were not created/found");
		}

		FaultLocalizationResult result = parseOutputFile(ochiaiFile,0.1);
		// From hereon we have two options
		// a) run gzoltar up to the point of reporting, reading in the spectrum and make the report here
		// b) run gzoltar including the reporting, and read in the reports here
		// The biggest issue is to re-match the found issues to the code here either way I guess.


		/*
		// For a)
		// We need to get a fault-localization family (technique(s)+ranking-metric) first
        List<ConfigFaultLocalizationFamily> flFamilies = ConfigFaultLocalizationReport.getDefaults();
        ConfigFaultLocalizationFamily firstFam = flFamilies.get(0);

		FaultLocalization fl = new FaultLocalization(
				firstFam.getFaultLocalizationFamily(),firstFam.getFormulas()
		);
		// Then we read in the spectrum from the .ser file and the .class files
		String original_location = ConfigurationProperties.getProperty("location");

		String serFilePath = original_location + File.separator + "gzoltar.ser";
		File serFile = new File(serFilePath);
		if (!serFile.exists() || !serFile.isFile()){
			throw new UnsupportedOperationException(".Ser-File for Gzoltar does not exist @"+serFile.getAbsolutePath());
		}
		// Using the spectrum, we can create our own report in-memory
		// With the current (empty) Datafile it will just create empty reports (but it makes them).
		FaultLocalizationReportBuilder.build(
				projLocationFile.getAbsolutePath(),
				agentConfigs,
				tmpDir,
				serFile,
				null // This will go to defaults
		);
		// First attempt: Use Proj LocationPath, which is ./astor_output
		//ISpectrum spec = fl.diagnose(projLocationPath,agentConfigs,tmpReport);
		// Second attempt: Use LocationByteCode
		//File byteLocation = new File(locationBytecode.replace("//","/"));
		// Other options to look at are binjavafolder;bintestfolder;srcjavafolder;srctestfolder
		//String original_bytecode = original_location + File.separator + ConfigurationProperties.getProperty("srctestfolder");
		String original_target = original_location + File.separator + "target" + File.separator;
		// Third Attempt: read .ser file created from console in

		//ISpectrum spec2 = fl.diagnose(projLocationPath,agentConfigs,serFile);


*/
		String aa = "reached?";
        // Commented out for now
/*
		GZoltar gz = new GZoltar(System.getProperty("user.dir") + File.separator);

		// 2. Add Package/Class names to instrument
		// 3. Add Package/Test Case/Test Suite names to execute
		// Example: gz.addPackageToInstrument("org.test1.Person");
		for (String to : toInstrument) {
			gz.addPackageToInstrument(to);
		}
		if (cp != null || !cp.isEmpty()) {
			logger.info("-gz-Adding classpath: " + cp);
			gz.getClasspaths().addAll(cp);
		}
		for (String test : testsToExecute) {
			gz.addTestToExecute(test);
			if (!ConfigurationProperties.getPropertyBool("includeTestInSusp")) {
				gz.addClassNotToInstrument(test);
			}
		}

		String testToAvoid = ConfigurationProperties.getProperty("gzoltartestpackagetoexclude");
		if (testToAvoid != null) {
			String[] testtoavoidarray = testToAvoid.split(PACKAGE_SEPARATOR);
			for (String test : testtoavoidarray) {
				gz.addTestPackageNotToExecute(test);
			}
		}

		String packagetonotinstrument = ConfigurationProperties.getProperty("gzoltarpackagetonotinstrument");

		if (packagetonotinstrument != null) {
			String[] packages = packagetonotinstrument.split(PACKAGE_SEPARATOR);
			for (String p : packages) {
				gz.addPackageNotToInstrument(p);
			}
		}

		gz.run();
		int[] sum = new int[2];
		List<TestResult> testResults = gz.getTestResults();

        // This Block checks & sorts the gz test-results
        // and for all Junit Tests checks for failing and passing, logging a short summary
		for (TestResult tr : testResults) {
			String testName = tr.getName().split("#")[0];
			if (testName.startsWith("junit")) {
				continue;
			}
			sum[0]++;
			sum[1] += tr.wasSuccessful() ? 0 : 1;
			if (!tr.wasSuccessful()) {
				logger.info("Test failt: " + tr.getName());

				String testCaseName = testName.split("\\#")[0];
				if (!failingTestCases.contains(testCaseName)) {
					failingTestCases.add(testCaseName);
				}
			}
		}

		int gzPositives = gz.getSuspiciousStatements().stream().filter(x -> x.getSuspiciousness() > 0)
				.collect(Collectors.toList()).size();

		logger.info("Gzoltar Test Result Total:" + sum[0] + ", fails: " + sum[1] + ", GZoltar suspicious "
				+ gz.getSuspiciousStatements().size() + ", with positive susp " + gzPositives);

		DecimalFormat df = new DecimalFormat("#.###");
		int maxSuspCandidates = ConfigurationProperties.getPropertyInt("maxsuspcandidates");

		List<Statement> gzCandidates = new ArrayList();


		logger.info("nr test results " + testResults.size());
		for (Statement gzoltarStatement : gz.getSuspiciousStatements()) {
			String compName = gzoltarStatement.getMethod().getParent().getLabel();
			if (// Kind of files to include in analysis
			(ConfigurationProperties.getPropertyBool("includeTestInSusp") || isSource(compName, srcFolder))
					// Analyze suspiciousness
					&& (!ConfigurationProperties.getPropertyBool("limitbysuspicious")
							|| (gzoltarStatement.getSuspiciousness() >= thr))
					//
					&& (!ConfigurationProperties.getPropertyBool("onlympcovered")
							|| !gzoltarStatement.getCoverage().isEmpty())
					//
					&& (!ConfigurationProperties.getPropertyBool("onlympfromtest")
							|| (testsToExecute.contains(gzoltarStatement.getMethod().getParent().getLabel())))) {

				gzCandidates.add(gzoltarStatement);

			}

		}
		// If we do not have candidate due the threshold is to high, we add all as suspicious
		if (gzCandidates.isEmpty()) {
			gzCandidates.addAll(gz.getSuspiciousStatements());
		}
        // If we do not accept zero-values by configuration, we do remove all 0 elements.
        // Otherwise they are kept.
		if (!ConfigurationProperties.getPropertyBool("considerzerovaluesusp")) {
			gzCandidates.removeIf(susp -> (susp.getSuspiciousness() == 0));
		}

		// we order the suspicious DESC
		Collections.sort(gzCandidates, (o1, o2) -> Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()));

		// We select the best X candidates.
		int max = (gzCandidates.size() < maxSuspCandidates) ? gzCandidates.size() : maxSuspCandidates;

		List<SuspiciousCode> candidates = new ArrayList<SuspiciousCode>();

		for (int i = 0; i < max; i++) {
			Statement gzoltarStatement = gzCandidates.get(i);
			String compName = gzoltarStatement.getMethod().getParent().getLabel();

			logger.debug("Suspicious: line " + compName + " l: " + gzoltarStatement.getLineNumber() + ", susp "
					+ df.format(gzoltarStatement.getSuspiciousness()));
			SuspiciousCode suspcode = new SuspiciousCode(compName, gzoltarStatement.getMethod().getName(),
					gzoltarStatement.getLineNumber(), gzoltarStatement.getSuspiciousness(),
					gzoltarStatement.getCountMap());
			candidates.add(suspcode);

			List<TestCaseResult> test = getTestCaseResults(testResults, gzoltarStatement);
			suspcode.setCoveredByTests(test);

		}

		logger.info("Gzoltar found: " + candidates.size() + " with susp > " + thr + ", we consider: " + max);

		return new FaultLocalizationResult(candidates, failingTestCases);
		*/
        return null;
	}
/*
	private List<TestCaseResult> getTestCaseResults(List<TestResult> testResults, Statement gzoltarStatement) {
		List<TestCaseResult> tcresults = new ArrayList<>();

		BitSet coverage = gzoltarStatement.getCoverage();
		int nextTest = coverage.nextSetBit(0);
		while (nextTest != -1) {
			TestResult testResult = testResults.get(nextTest);
			if (!ConfigurationProperties.getPropertyBool("savecoverbyfailtests") || !testResult.wasSuccessful()) {

				TestCaseResult tcri = new TestCaseResult(testResult.getName(), testResult.wasSuccessful());
				tcresults.add(tcri);
			}
			nextTest = coverage.nextSetBit(nextTest + 1);
		}
		return tcresults;
	}

	private boolean isSource(String compName, String srcFolder) {
		String clRoot = compName.split("\\$")[0];
		String[] segmentationName = clRoot.split("\\.");
		String simpleClassName = segmentationName[segmentationName.length - 1];

		// File root = new File(srcFolder+"/"+clRoot.replace(".", "/")+".java");

		return // root.exists()
				// &&
		!compName.toLowerCase().endsWith("test") && !compName.toLowerCase().endsWith("tests")
				&& !simpleClassName.toLowerCase().startsWith("test")
				&& !simpleClassName.toLowerCase().startsWith("validate");

	}
*/
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


	public SuspiciousCode parseLine(String line) {
		try {
			if (line.equals("Component,OCHIAI"))
				return null;
			SuspiciousCode sc = new SuspiciousCode();

			String[] infoLine = line.split("#");
			String[] linesusp = infoLine[1].split(",");
			sc.setLineNumber(Integer.valueOf(linesusp[0]));
			sc.setSusp(Double.parseDouble(linesusp[1]));

			String[] splitFile = line.split("<");
			String fileName = splitFile[0].replace("[", ".");
			sc.setFileName(fileName);
			String[] classLine = splitFile[1].split("\\{");
			String className = classLine[0];
			sc.setClassName(className);
			String method = "";
			if (classLine.length > 1)
				method = classLine[1];//
			sc.setMethodName(method);

			return sc;
		} catch (Exception e) {
			logger.error("-->" + line);
			logger.error(e);
			e.printStackTrace();
			return null;
		}
	}

	public FaultLocalizationResult parseOutputFile(File path, Double thr) {
		File spectrapath = new File(path.getAbsolutePath() + File.separator + "spectra");
		File testpath = new File(path.getAbsolutePath() + File.separator + "tests");

		List<SuspiciousCode> codes = new ArrayList<>();
		List<String> failingTestCases = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(spectrapath))) {

			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				SuspiciousCode sc = parseLine(line);
				if (sc != null && sc.getSuspiciousValue() > 0 && sc.getSuspiciousValue() >= thr)
					codes.add(sc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// org.apache.commons.lang3.AnnotationUtilsTest#testAnnotationsOfDifferingTypes,PASS,92358174
		try (BufferedReader br = new BufferedReader(new FileReader(testpath))) {

			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] lineS = line.split(",");
				if (lineS[1].equals("FAIL")) {
					String name = lineS[0].split("#")[0];
					if (!failingTestCases.contains(name))
						failingTestCases.add(name);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		codes.forEach(e -> logger.debug(e));
		failingTestCases.forEach(e -> logger.debug(e));
		FaultLocalizationResult result = new FaultLocalizationResult(codes, failingTestCases);

		return result;
	}


}
