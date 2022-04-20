package fr.inria.astor.core.faultlocalization.gzoltar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Facade of Fault Localization techniques like GZoltar or own implementations
 * (package {@link org.inria.sacha.faultlocalization}.).
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class NovelGZoltarFaultLocalization implements FaultLocalizationStrategy {

	public static final String PACKAGE_SEPARATOR = "-";
	Logger logger = Logger.getLogger(NovelGZoltarFaultLocalization.class.getName());

	@Override
	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade project,
			List<String> regressionTestForFaultLocalization) throws Exception {

		return this.calculateSuspicious(
				ConfigurationProperties.getProperty("location") + File.separator
						+ ConfigurationProperties.getProperty("srcjavafolder"),
				project.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				ConfigurationProperties.getProperty("packageToInstrument"), ProgramVariant.DEFAULT_ORIGINAL_VARIANT,
				project.getProperties().getFailingTestCases(), regressionTestForFaultLocalization, project);

	}

	private FaultLocalizationResult calculateSuspicious(String locationSrc, String locationBytecode,
			String packageToInst, String mutatorIdentifier, List<String> failingTest, List<String> allTest,
			ProjectRepairFacade project) throws Exception {

		System.out.println("Calculating suspicious");

		String output = project.getProperties().getWorkingDirRoot();

		String noout = (ConfigurationProperties.hasProperty("outfl") ? ConfigurationProperties.getProperty("outfl")
				: output);

		// Let's take the default timeout in seconds
		Integer timeOutSeconds = 10;
		if (ConfigurationProperties.getProperty("maxtime") != null)
			timeOutSeconds = new Integer(ConfigurationProperties.getProperty("maxtime"));

		Integer timeoutMiliseconds = timeOutSeconds * 60000;

		String maxmemory = "-XX:MaxPermSize=4096M";
		if (ConfigurationProperties.properties.containsKey("maxmemory")) {
			maxmemory = (ConfigurationProperties.properties.get("maxmemory").toString());
		}

		// Outs
		String outputdirGzoltar = noout + File.separator + "outputgzoltar";
		File fileOutGzoltar = (new File(outputdirGzoltar));
		if (!fileOutGzoltar.exists()) {
			fileOutGzoltar.mkdirs();
		}
		String serfile = outputdirGzoltar + File.separator + "gzoltar_mm.ser";

		String pathTestsFiles = noout + File.separator + "outTest.txt";

		// Info related to project
		String src_classes_dir = String.join(File.pathSeparator, project.getProperties().getOriginalAppBinDir());
		String testClassPath = String.join(File.pathSeparator, project.getProperties().getOriginalTestBinDir());

		// Junit path
		String junitpath = retrieveJUnitLibPath();

		String gzoltarversion = "1.7.4-SNAPSHOT";
		if (ConfigurationProperties.properties.containsKey("gzoltarversion")) {
			gzoltarversion = ConfigurationProperties.properties.getProperty("gzoltarversion");
		}

		// GZoltar path
		String gzoltar_cli_jar = getGZoltarCLIPath(gzoltarversion);
		String gzoltar_agent_jar = getGzoltarAgentRtPath(gzoltarversion);

		retrieveTestCases(timeoutMiliseconds, pathTestsFiles, src_classes_dir, testClassPath, junitpath,
				gzoltar_cli_jar);

		// Refine the test cases
		boolean isMethodName = (!allTest.isEmpty() && allTest.get(0).split("#").length >= 2);
		Path pathTestFile = new File(pathTestsFiles).toPath();
		try (Stream<String> lines = Files.lines(pathTestFile)) {
			List<String> replaced = lines
					.filter(e -> allTest.isEmpty() || (isMethodName && (allTest.contains(e.split(",")[1])))
							|| (!isMethodName && allTest.contains(e.split(",")[1].split("#")[0])))
					.collect(Collectors.toList());
			System.out.println("Filtered " + replaced);
			Files.write(pathTestFile, replaced);
		}

		String commandRunTestMethods = "java " + maxmemory + " -javaagent:" + gzoltar_agent_jar + "=destfile=" + serfile
				+ ",buildlocation=" + src_classes_dir + ",inclnolocationclasses=false,output=FILE" + "        -cp "
				+ src_classes_dir + ":" + junitpath + ":" + testClassPath + ":" + gzoltar_cli_jar
				+ "  com.gzoltar.cli.Main runTestMethods " + "   --testMethods " + pathTestsFiles
				+ "  --collectCoverage";

		System.out.println(commandRunTestMethods);
		final Process p2 = Runtime.getRuntime().exec(commandRunTestMethods);

		new Thread(new Runnable() {
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(p2.getInputStream()));
				String line = null;

				try {
					while ((line = input.readLine()) != null)
						System.out.println(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		p2.waitFor(timeoutMiliseconds, TimeUnit.MILLISECONDS);

		System.out.println("Report: ");

		String runRapportCommand = "java -XX:MaxPermSize=4096M " + "-cp "

				+ src_classes_dir + ":"

				+ junitpath + ":"

				+ testClassPath + ":"

				+ gzoltar_cli_jar + "      com.gzoltar.cli.Main faultLocalizationReport " + "        --buildLocation "

				+ src_classes_dir + "        --granularity line " + "        --inclPublicMethods "
				+ "        --inclStaticConstructors " + "        --inclDeprecatedMethods " + "        --dataFile "
				+ serfile + "        --outputDirectory " + outputdirGzoltar + "        --family sfl "
				+ "        --formula ochiai " + "        --metric entropy " + "        --formatter txt";

		System.out.println(runRapportCommand);
		final Process processRunReport = Runtime.getRuntime().exec(runRapportCommand);

		new Thread(new Runnable() {
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(processRunReport.getInputStream()));
				String line = null;

				try {
					while ((line = input.readLine()) != null)
						System.out.println(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		processRunReport.waitFor(timeoutMiliseconds, TimeUnit.MILLISECONDS);

		Double thr = ConfigurationProperties.getPropertyDouble("flthreshold");
		boolean includeZeros = ConfigurationProperties.getPropertyBool("includeZeros");
		File gzoltarOutFile = new File(outputdirGzoltar);
		FaultLocalizationResult result = parseOutputFile(gzoltarOutFile, thr, includeZeros);
		System.out.println(result);

		if (ConfigurationProperties.hasProperty("keepGZoltarFiles")) {
			FileUtils.deleteDirectory(gzoltarOutFile);
		}
		return result;

	}

	private void retrieveTestCases(Integer timeoutMiliseconds, String pathTestsFiles, String src_classes_dir,
			String testClassPath, String junitpath, String gzoltar_cli_jar) throws IOException, InterruptedException {
		String commandGetTest = "java -cp " + src_classes_dir + ":" + testClassPath + ":" + junitpath + ":"
				+ gzoltar_cli_jar + "  com.gzoltar.cli.Main listTestMethods " + testClassPath + "    --outputFile "
				+ pathTestsFiles;

		System.out.println(commandGetTest);
		final Process p = Runtime.getRuntime().exec(commandGetTest);

		new Thread(new Runnable() {
			public void run() {
				BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = null;

				try {
					while ((line = input.readLine()) != null)
						System.out.println(line);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();

		p.waitFor(timeoutMiliseconds, TimeUnit.MILLISECONDS);

		System.out.println("End obtaining test");
	}

	private String getGzoltarAgentRtPath(String gzoltarversion) throws IllegalAccessException {

		String jarToFind = "com.gzoltar.agent.rt-" + gzoltarversion + "-all.jar";
		return getJar(jarToFind);
	}

	private String getGZoltarCLIPath(String gzoltarversion) throws IllegalAccessException {

		String jarToFind = "com.gzoltar.cli-" + gzoltarversion + "-jar-with-dependencies.jar";

		return getJar(jarToFind);

	}

	private String getJar(String jarToFind) throws IllegalAccessException {
		String locationGzoltarJar = ConfigurationProperties.getProperty("locationGzoltarJar");

		if (locationGzoltarJar == null || locationGzoltarJar.isEmpty()) {
			locationGzoltarJar = "./lib/";

		}
		File f = new File(locationGzoltarJar + File.separator + jarToFind);

		if (f != null && f.exists()) {

			return f.getAbsolutePath();

		}

		String jarFromCP = getFromClassPath(jarToFind);

		f = new File(jarFromCP);
		if (!f.exists())
			throw new IllegalAccessException(
					"We cannot localize the jar at " + f.getAbsolutePath() + ". Please add it in the classpath");

		return f.getAbsolutePath();
	}

	private String getFromClassPath(String jarToFind) throws IllegalAccessException {
		String cp = System.getProperty("java.class.path");
		System.out.println("CP " + cp);
		String[] cps = cp.split(File.pathSeparator);
		String np = "";
		for (String aJar : cps) {

			if (aJar.contains(jarToFind)) {

				np += ((np.isEmpty()) ? "" : File.pathSeparator) + aJar;
			}

		}
		System.out.println("gzoltar path " + np);
		return np;
	}

	private String retrieveJUnitLibPath() {

		String cp = System.getProperty("java.class.path");
		String[] cps = cp.split(File.pathSeparator);
		String np = "";
		for (String aJar : cps) {

			if (aJar.contains("junit-4.12.jar") || aJar.contains("hamcrest-core-1.3.jar")) {

				np += ((np.isEmpty()) ? "" : File.pathSeparator) + aJar;
			}

		}
		System.out.println("junit path " + np);

		return np;
	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {

		List<String> testall = null;
		try {
			testall = GzoltarTestClassesFinder.findIn(projectFacade);
			System.out.println("Test all " + testall);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return testall;

	}

	public SuspiciousCode parseLine(String line) {
		try {
			if (line.equals("Component,OCHIAI"))
				return null;
			SuspiciousCode sc = new SuspiciousCode();
			//
			String[] infoLine = line.split(";");

			if ("suspiciousness_value".equals(infoLine[1])) {
				return null;
			}

			sc.setSusp(Double.parseDouble(infoLine[1]));

			infoLine = infoLine[0].split(":");
			sc.setLineNumber(Integer.valueOf(infoLine[1]));

			infoLine = infoLine[0].split("#");

			String className = infoLine[0].replace("$", ".");
			sc.setClassName(className);

			String method = "";
			if (infoLine.length > 1)
				method = infoLine[1];//
			sc.setMethodName(method);

			return sc;
		} catch (Exception e) {
			logger.error("-->" + line);
			logger.error(e);
			e.printStackTrace();
			return null;
		}
	}

	public FaultLocalizationResult parseOutputFile(File path, Double thr, boolean includeZeros) {

		List<SuspiciousCode> codes = analyzeSuspiciousValues(thr, path, includeZeros);

		FaultLocalizationResult result = new FaultLocalizationResult(codes);

		analyzeTestCaseExecution(path, result);

		return result;

	}

	private List<SuspiciousCode> analyzeSuspiciousValues(Double thr, File path, boolean includeZeros) {
		List<SuspiciousCode> codes = new ArrayList<>();
		File spectrapath = new File(path.getAbsolutePath() + File.separator + "sfl" + File.separator + "txt"
				+ File.separator + "ochiai.ranking.csv");

		try (BufferedReader br = new BufferedReader(new FileReader(spectrapath))) {

			String line;
			while ((line = br.readLine()) != null) {
				SuspiciousCode sc = parseLine(line);
				if (sc != null && sc.getSuspiciousValue() >= thr && (sc.getSuspiciousValue() > 0.0 || includeZeros))
					codes.add(sc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		return codes;
	}

	private void analyzeTestCaseExecution(File path, FaultLocalizationResult results) {
		File testpath = new File(path.getAbsolutePath() + File.separator + "sfl" + File.separator + "txt"
				+ File.separator + "tests.csv");

		List<String> failingTestCasesMethod = new ArrayList<>();
		List<String> failingTestCasesClasses = new ArrayList<>();
		List<String> allTestCasesMethod = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(testpath))) {

			String line;
			while ((line = br.readLine()) != null) {
				String[] lineS = line.split(",");
				String testName = lineS[0];

				if (testName.toLowerCase().equals("name")) {
					continue;
				}

				String name = testName.split("#")[0];
				String testResult = lineS[1];
				if (testResult.equals("FAIL")) {
					failingTestCasesMethod.add(testName);
					if (!failingTestCasesClasses.contains(name))
						failingTestCasesClasses.add(name);
				}

				if (!allTestCasesMethod.contains(testName))
					allTestCasesMethod.add(testName);

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		results.setFailingTestCasesClasses(failingTestCasesClasses);
		results.setFailingTestCasesMethods(failingTestCasesMethod);
		results.setExecutedTestCasesMethods(allTestCasesMethod);

	}

}
