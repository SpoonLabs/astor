package fr.inria.astor.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;

/**
 * Execution of results from
 * https://github.com/Spirals-Team/defects4j-repair/tree/master/results/2015-august
 * 
 * @author Matias Martinez
 *
 */
public class D4JWorkflowTestSingle {

	private static final int TIMEOUTMIN = 60;

	public String bugidParametrized;

	@Test
	public void testMath2() throws Exception {

		CommandSummary cs = new CommandSummary();
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");
		cs.command.put("-flthreshold", "0.159");
		runComplete("Math2", "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7", "jGenProg", 120, cs);
	}

	@Test
	public void testMath5() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");
		runComplete("Math5", "", "jGenProg", 90, cs);
	}

	@Test
	public void testMath8() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");
		runComplete("Math8", "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7", "jGenProg", 90, cs);
	}

	@Test
	public void testMath28() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");
		runComplete("Math28", "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7", "jGenProg", 90, cs);

	}

	@Test
	public void testMath40() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0.2");
		cs.command.put("-parameters", "logtestexecution:true");
		cs.command.put("-ignoredtestcases",
				"org.apache.commons.math.util.FastMathTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegratorTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.GillIntegratorTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.ThreeEighthesIntegratorTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.HighamHall54IntegratorTest");
		runComplete("Math40", "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7", "jGenProg", 90, cs);
	}

	@Test
	public void testMath49() throws Exception {
		runCompleteJGenProg("Math49", "", 90);
	}

	@Test
	public void testMath50() throws Exception {
		runCompleteJGenProg("Math50", "", 90);
	}

	@Test
	public void testMath53() throws Exception {
		runCompleteJGenProg("Math53", "", 90);
	}

	@Test
	public void testMath70() throws Exception {
		runCompleteJGenProg("Math70", "");
	}

	@Test
	public void testMath71() throws Exception {

		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0.1");
		cs.command.putIfAbsent("-scope", "local");

		runComplete("Math71", "", "jGenProg", 90, cs);
	}

	@Test
	public void testMath73() throws Exception {
		runCompleteJGenProg("Math73", "");
	}

	@Test
	public void testMath78() throws Exception {

		CommandSummary cs = new CommandSummary();
		cs.command.put("-ignoredtestcases", "org.apache.commons.math.util.FastMathTest" + File.pathSeparator
				+ "org.apache.commons.math.random.RandomAdaptorTest");
		runComplete("Math78", "", "jGenProg", 90, cs);

	}

	@Test
	public void testMath80() throws Exception {
		runCompleteJGenProg("Math80", "");
	}

	@Test
	public void testMath81() throws Exception {
		runCompleteJGenProg("Math81", "");
	}

	@Test
	public void testMath82() throws Exception {
		runCompleteJGenProg("Math82", "");
	}

	@Test
	public void testMath84() throws Exception {

		CommandSummary cs = new CommandSummary();
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");
		cs.command.put("-flthreshold", "0.7");

		runComplete("Math84", "", "jGenProg", 120, cs);
	}

	@Test
	public void testMath85() throws Exception {
		runCompleteJGenProg("Math85", "");
	}

	@Test
	public void testMath95() throws Exception {
		runCompleteJGenProg("Math95", "");
	}

	@Test
	public void testChart1() throws Exception {
		runCompleteJGenProg("Chart1", "");
	}

	@Test
	public void testChart3() throws Exception {
		runCompleteJGenProg("Chart3", "");
	}

	@Test
	public void testChart5() throws Exception {
		runCompleteJGenProg("Chart5", "");
	}

	@Test
	public void testChart7() throws Exception {
		runCompleteJGenProg("Chart7", "");
	}

	@Test
	public void testChart13() throws Exception {
		runCompleteJGenProg("Chart13", "");
	}

	@Test
	public void testChart15() throws Exception {
		runCompleteJGenProg("Chart15", "");
	}

	@Test
	public void testChart25() throws Exception {
		runCompleteJGenProg("Chart25", "");
	}

	@Test
	public void testTime4() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-javacompliancelevel", "5");
		runCompleteJGenProg("Time4", "");
	}

	@Test
	public void testTime11() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-javacompliancelevel", "5");
		runCompleteJGenProg("Time11", "");
	}

	//
	@Test
	public void testMath2JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0.0");
		cs.command.putIfAbsent("-javacompliancelevel", "5");
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");

		runComplete("Math2", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testMath8JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0");
		runComplete("Math8", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testMath28JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0");
		cs.append("-parameters", "maxmemory" + File.pathSeparator + "-Xmx4G");
		runComplete("Math28", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testMath32JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0");
		runComplete("Math32", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testMath40JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0");
		cs.command.put("-parameters", "logtestexecution:true");
		cs.command.put("-ignoredtestcases",
				"org.apache.commons.math.util.FastMathTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegratorTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.GillIntegratorTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.ThreeEighthesIntegratorTest" + File.pathSeparator
						+ "org.apache.commons.math.ode.nonstiff.HighamHall54IntegratorTest");
		runComplete("Math40", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testMath40JKaliSummary() throws Exception {
		org.apache.log4j.LogManager.getRootLogger().setLevel(Level.DEBUG);
		String bugid = "Math40";

		configureBuggyProject(bugid, "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7");
		CommandSummary cs = new CommandSummary();
		createCommand(bugid, "jKali", 5, "gzoltar", cs); //

		cs.command.put("-maxtime", "0");
		cs.command.put("-jvm4testexecution", (System.getenv("J7PATH") != null) ? System.getenv("J7PATH")
				: "/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/bin/");
		assertEquals("0", cs.command.get("-maxtime"));

		AstorMain main1 = new AstorMain();

		System.out.println("command before " + cs.flat());
		main1.execute(cs.flat());

		AstorCoreEngine engine = main1.getEngine();

		assertEquals(1, engine.getVariants().size());
		ProgramVariant programVariant = engine.getVariants().get(0);
		programVariant.getModificationPoints().removeIf(e -> !(e.getCodeElement().getPosition().getFile().getName()
				.contains("BracketingNthOrderBrentSolver.java") && e.getCodeElement().getPosition().getLine() == 260));

		assertEquals(1, programVariant.getModificationPoints().size());
		System.out.println("MD selected: " + programVariant.getModificationPoints().get(0).getCodeElement().toString());
		ConfigurationProperties.setProperty("maxtime", "1000000");
		engine.startEvolution();
		engine.atEnd();

		assertEquals(1, engine.getSolutions().size());
		System.out.println("---Starting second run---");

	}

	@Test
	public void testMath49JKali() throws Exception {
		runComplete("Math49", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testMath50JKali() throws Exception {
		runComplete("Math50", "", "jKali", TIMEOUTMIN);
	}

	@Test
	@Ignore
	public void testMath78JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.put("-ignoredtestcases", "org.apache.commons.math.util.FastMathTest" + File.pathSeparator
				+ "org.apache.commons.math.random.RandomAdaptorTest");
		cs.command.putIfAbsent("-flthreshold", "0");
		runComplete("Math78", "", "jKali", 90, cs);
	}

	@Test
	public void testMath80JKali() throws Exception {
		runComplete("Math80", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testMath81JKali() throws Exception {
		runComplete("Math81", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testMath82JKali() throws Exception {
		runComplete("Math82", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testMath84JKali() throws Exception {
		runComplete("Math84", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testMath85JKali() throws Exception {
		runComplete("Math85", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testMath95JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0");
		runComplete("Math95", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testTime11JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-javacompliancelevel", "5");
		runComplete("Time11", "", "jKali", TIMEOUTMIN, cs);
	}

	@Test
	public void testChart1JKali() throws Exception {
		runComplete("Chart1", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testChart5JKali() throws Exception {
		runComplete("Chart5", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testChart13JKali() throws Exception {
		runComplete("Chart13", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testChart15JKali() throws Exception {
		CommandSummary cs = new CommandSummary();
		cs.command.putIfAbsent("-flthreshold", "0.01");
		runComplete("Chart15", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testChart25JKali() throws Exception {
		runComplete("Chart25", "", "jKali", TIMEOUTMIN);
	}

	@Test
	public void testChart26JKali() throws Exception {
		runComplete("Chart26", "", "jKali", TIMEOUTMIN);
	}

	public static void runCompleteJGenProg(String bug_id, String mvn_option) throws Exception {

		runComplete(bug_id, mvn_option, "jGenProg", TIMEOUTMIN);
	}

	public static void runCompleteJGenProg(String bug_id, String mvn_option, int timeout) throws Exception {

		runComplete(bug_id, mvn_option, "jGenProg", timeout);
	}

	public static void runComplete(String bug_id, String mvn_option, String approach, int timeout) throws Exception {
		CommandSummary cs = new CommandSummary();
		runComplete(bug_id, mvn_option, approach, timeout, cs);
	}

	public static void runComplete(String bug_id, String mvn_option, String approach, int timeout, CommandSummary cs)
			throws Exception {
		System.out.println("\n****\nRunning repair attempt for " + bug_id);

		System.out.println("Env var " + System.getenv("J7PATH"));
		File dirResults = new File("./resultsTestCases");
		if (!dirResults.exists()) {
			dirResults.mkdirs();
		}
		System.out.println("Storing results at " + dirResults.getAbsolutePath());

		configureBuggyProject(bug_id, mvn_option);

		String[] faultLocalization = new String[] { "gzoltar", "flacoco" };//

		boolean hasSolution = false;

		Map<String, Long> timePerFL = new HashMap<>();
		Map<String, Boolean> repairedPerFL = new HashMap<>();

		for (String aFL : faultLocalization) {

			System.out.println("Running on FL : " + aFL);

			createCommand(bug_id, approach, timeout,
					("flacoco".equals(aFL) ? "fr.inria.astor.core.faultlocalization.flacoco.FlacocoFaultLocalization"
							: aFL),
					cs);
			cs.command.put("-jvm4testexecution", System.getenv("J7PATH"));
			AstorMain main1 = new AstorMain();

			long init = System.currentTimeMillis();
			try {
				// org.apache.log4j.LogManager.getRootLogger().setLevel(Level.DEBUG);
				main1.execute(cs.flat());
			} catch (Exception e) {
				e.printStackTrace();
			}

			long end = System.currentTimeMillis();

			List<ProgramVariant> variantsSolutions = main1.getEngine().getSolutions();

			System.out.println("Finishing execution for " + bug_id + ": # patches: " + variantsSolutions.size());

			hasSolution = hasSolution || variantsSolutions.size() > 0;

			// Results

			// Copy the result
			File foutput = new File("./output_astor/AstorMain-" + bug_id + File.separator + "astor_output.json");
			// We rename the file and put in a result folder
			File foutputnew = new File(dirResults.getAbsolutePath() + File.separator + File.separator + "astor_output_"
					+ bug_id + "-" + approach + "_" + aFL + ".json");

			Files.copy(foutput.toPath(), foutputnew.toPath(), StandardCopyOption.REPLACE_EXISTING);

			//
			timePerFL.put(aFL, (end - init) / 1000);
			repairedPerFL.put(aFL, variantsSolutions.size() > 0);

			System.out.println("Saving execution of " + aFL + "results at " + foutputnew);

		}
		// Save results

		String fileNameResults = dirResults.getAbsolutePath() + File.separator + "results_" + bug_id + "-" + approach
				+ ".json";
		System.out.println("Saving results at " + fileNameResults);
		FileWriter fw = new FileWriter(fileNameResults);
		fw.write("{\"bugid\" :  \" " + bug_id + "\",  \"approach\": \"  " + approach + " \" , \"flacoco_sol\": \" "
				+ repairedPerFL.get("flacoco") + " \" , \"flacoco_time\" : " + timePerFL.get("flacoco")
				+ ", \"gzoltar_sol\": \" " + repairedPerFL.get("gzoltar") + " \" , \"gzoltar_time\": "
				+ timePerFL.get("gzoltar")

				+ "}");
		fw.close();

		//

		assertTrue(hasSolution);

	}

	public static void configureBuggyProject(String bug_id, String mvn_option)
			throws IOException, InterruptedException {

		// for Chart, we use ant

		/// usr/local/bin/ant
		String antpath = new File("/usr/local/bin/ant").exists() ? "/usr/local/bin/ant" : "ant";

		// String javapath = new File("/usr/bin/java").exists() ? "/usr/bin/java" :
		// "java";
		String bugid_remotename = bug_id.toLowerCase().equals("chart15") ? "Chart15Refactor" : bug_id;
		if (bug_id.startsWith("Chart") && !new File(bug_id).exists()) {
			// here we use maven to compile
			String command = "java -version;mkdir -p tempdj4/" + bug_id + ";\n cd tempdj4/" + bug_id
					+ ";\n git init;\n git fetch "
					// + "https://github.com/Spirals-Team/defects4j-repair "
					+ "https://github.com/martinezmatias/defects4j-repair " + bugid_remotename + ":" + bug_id
					+ ";\n git checkout " + bug_id + ";\n" + "sed -i -e '/delete dir/ d' ant/build.xml;\n" + antpath
					+ " -f ant/build.xml compile compile-tests;\n"
					// + "echo -n
					// `pwd`/lib/iText-2.1.4.jar:`pwd`/lib/junit.jar:`pwd`/lib/servlet.jar >
					// cp.txt;\n"
					+ "echo -n `pwd`/lib/iText-2.1.4.jar:`pwd`/lib/junit.jar:`pwd`/lib/servlet.jar > cp.txt;\n"

			;
			System.out.println(command);
			Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
			p.waitFor();
			String output = IOUtils.toString(p.getInputStream());
			String errorOutput = IOUtils.toString(p.getErrorStream());
			System.out.println(output);
			System.err.println(errorOutput);

		} else if (!new File(bug_id).exists()) {

			String mvnpath = new File("/usr/local/bin/mvn").exists() ? "/usr/local/bin/mvn" : "mvn";

			System.out.println("\nChecking out project: " + bug_id);
			// for the rest we use Maven
			String command = "java -version;mkdir -p tempdj4/" + bug_id + ";\n cd tempdj4/" + bug_id
					+ ";\n git init;\n git fetch https://github.com/Spirals-Team/defects4j-repair " + bug_id + ":"
					+ bug_id + ";\n git checkout " + bug_id + ";\n " + mvnpath + " -q test -DskipTests " + mvn_option
					+ ";\n " + mvnpath + " -q dependency:build-classpath -Dmdep.outputFile=cp.txt";
			System.out.println(command);
			Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
			p.waitFor();
			String output = IOUtils.toString(p.getInputStream());
			String errorOutput = IOUtils.toString(p.getErrorStream());
			System.out.println(output);
			System.err.println(errorOutput);
		}
	}

	public static void createCommand(String bug_id, String approach, int timeout, String faultLocalization,
			CommandSummary cs) throws IOException, FileNotFoundException {
		Properties prop = new Properties();
		String locationProject = "./tempdj4/" + bug_id;
		prop.load(new FileInputStream(locationProject + "/defects4j.build.properties"));

		String src = File.separator + prop.get("d4j.dir.src.classes").toString();
		String srcTst = File.separator + prop.get("d4j.dir.src.tests").toString();

		// String failing = bug_id + "/" + prop.get("d4j.tests.trigger");

		System.out.println("Source " + src);

		// getting the classpath from Maven
		List<String> cp = new ArrayList<>();
		for (String entry : FileUtils.readFileToString(new File(locationProject + "/cp.txt"))
				.split(new String(new char[] { File.pathSeparatorChar }))) {
			cp.add(new File(entry).getAbsolutePath());

		}
		String testBin = null, classBin = null;

		File maven_app = new File(locationProject + "/target/classes");
		if (maven_app.exists()) {
			// cp.add(maven_app.toURL());
			classBin = "/target/classes";// maven_app.getAbsolutePath();
		}
		File maven_test = new File(locationProject + "/target/test-classes");
		if (maven_test.exists()) {
			// cp.add(maven_test.toURL());
			testBin = "/target/test-classes";// maven_app.getAbsolutePath();
		}
		File ant_app = new File(locationProject + "/build");
		if (ant_app.exists()) {
			classBin = "/build";// ant_app.getAbsolutePath();
		}
		File ant_test = new File(locationProject + "/build-tests");
		if (ant_test.exists()) {
			// cp.add(ant_test.toURL());
			testBin = "/build-tests";// ant_test.getAbsolutePath();
		}

		System.out.println(cp);
		//
		String depStrings = cp.stream().map(n -> n.toString()).collect(Collectors.joining(File.pathSeparator));

		System.out.println(depStrings);

		cs.command.put("-id", bug_id);
		cs.command.put("-mode", approach);
		cs.command.put("-srcjavafolder", src);
		cs.command.put("-srctestfolder", srcTst);
		cs.command.put("-binjavafolder", classBin);
		cs.command.put("-bintestfolder", testBin);

		cs.command.put("-location", locationProject);
		cs.command.put("-dependencies", depStrings);
		cs.command.putIfAbsent("-maxgen", "10000");
		cs.command.putIfAbsent("-stopfirst", "true");
		cs.command.putIfAbsent("-loglevel", "DEBUG");
		cs.command.putIfAbsent("-maxtime", new Integer(timeout).toString());

		cs.command.put("-faultlocalization", faultLocalization);

		cs.command.putIfAbsent("-javacompliancelevel", "7");

		cs.command.put("-population", "1");
		cs.command.putIfAbsent("-flthreshold", "0.1");
		cs.command.putIfAbsent("-seed", "10");
		cs.command.putIfAbsent("-tmax1", "30000");

		if (cs.command.containsValue("-ignoredtestcases")) {

			// Already passed some test to ignore
			String tctoignore = cs.command.get("-ignoredtestcases");
			cs.command.putIfAbsent("-ignoredtestcases",
					"org.apache.commons.math.util.FastMathTest" + File.pathSeparator + tctoignore);
			//
		} else {

			cs.command.putIfAbsent("-ignoredtestcases", "org.apache.commons.math.util.FastMathTest");
		}
		System.out.println("\nConfiguration " + cs.command.toString());

		cs.append("-parameters", "logtestexecution:true:" + "maxmemory" + File.pathSeparator + "-Xmx4G");

	}

	public void run(String bug_id, String mvn_option) throws Exception {

		String command = "mkdir -p tempdj4/" + bug_id + ";\n cd tempdj4/" + bug_id
				+ ";\n git init;\n git fetch https://github.com/Spirals-Team/defects4j-repair " + bug_id + ":" + bug_id
				+ ";\n git checkout " + bug_id + ";\n /usr/local/bin/mvn -q test -DskipTests " + mvn_option
				+ ";\n /usr/local/bin/mvn -q dependency:build-classpath -Dmdep.outputFile=cp.txt";
		System.out.println(command);
		// Process p = Runtime.getRuntime().exec(new String[] { "sh", "-c", command });
		Process p = Runtime.getRuntime().exec(new String[] { "bash", "-c", command });

		p.waitFor();
		String output = IOUtils.toString(p.getInputStream());
		String errorOutput = IOUtils.toString(p.getErrorStream());
		System.out.println(output);
		System.err.println(errorOutput);

		System.out.println("End case");

	}

	@Test
	public void testFaultLocalizationFlacocoIssueMath2() throws IOException, InterruptedException {
		org.apache.log4j.LogManager.getRootLogger().setLevel(Level.DEBUG);
		configureBuggyProject("Math2", "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7");
		CommandSummary cs = new CommandSummary();
		createCommand("Math2", "jGenprog", 5, "fr.inria.astor.core.faultlocalization.flacoco.FlacocoFaultLocalization",
				cs);

		AstorMain main1 = new AstorMain();

		long init = System.currentTimeMillis();
		try {

			main1.execute(cs.flat());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Test
	public void testSynthesisjKaliIssueMath78PatchNopol() throws Exception {
		org.apache.log4j.LogManager.getRootLogger().setLevel(Level.DEBUG);
		String bugid = "Math78";

		configureBuggyProject(bugid, "-Dmaven.compiler.source=7 -Dmaven.compiler.target=7");
		CommandSummary cs = new CommandSummary();
		createCommand(bugid, "jKali", 5, "gzoltar", cs); //

		cs.command.put("-maxtime", "0");

		assertEquals("0", cs.command.get("-maxtime"));

		AstorMain main1 = new AstorMain();

		long init = System.currentTimeMillis();

		System.out.println("command before " + cs.flat());
		main1.execute(cs.flat());

		ConfigurationProperties.setProperty("maxtime", "1000000");
		AstorCoreEngine engine = main1.getEngine();

		assertEquals(1, engine.getVariants().size());
		ProgramVariant programVariant = engine.getVariants().get(0);
		programVariant.getModificationPoints()
				.removeIf(e -> !(e.getCodeElement().toString().equals("delta = 0.5 * dx")));

		assertEquals(1, programVariant.getModificationPoints().size());
		System.out.println("---Starting second run---");
		// let's start the evolution again (the model was already created on that
		// engine, so we directly call start)
		engine.startEvolution();

		// we should call end after the startevol
		engine.atEnd();

		CtCodeElement newIf = MutationSupporter.getFactory().Code()
				.createCodeSnippetStatement("if(y0 < 1){delta = 0.5 * dx;} ").partiallyEvaluate();

		System.out.println("-->> " + newIf.toString());

//		String codeNewS = newIf.toString();
//
//		assertTrue(codeNewS.contains("y0 < 1"));
//
//		assertTrue(codeNewS.contains("delta = 0.5 * dx"));

		programVariant.getModificationPoints().get(0).setCodeElement(newIf);

		System.out.println("code patch " + programVariant.getModificationPoints().get(0).getCodeElement());

		ReplaceOp rop = new ReplaceOp();

		OperatorInstance opinstance = new StatementOperatorInstance(programVariant.getModificationPoints().get(0), rop,
				programVariant.getModificationPoints().get(0).getCodeElement(), newIf);

		boolean applied = opinstance.applyModification();

		assertTrue(applied);

		programVariant.putModificationInstance(0, opinstance);

		programVariant.setId(10);

		ConfigurationProperties.setProperty("saveall", "true");

		boolean passes = engine.processCreatedVariant(programVariant, 1);

		assertNotNull(programVariant.getValidationResult());

		assertTrue(passes);

		boolean undo = opinstance.undoModification();

		assertTrue(undo);

	}

}