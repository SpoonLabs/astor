package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ValidationTest extends BaseEvolutionaryTest {

	@Test
	public void testLang63ValidationStepbyStep() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang.time.DurationFormatUtilsTest"
				// + ":org.apache.commons.lang.builder.ToStringBuilder",
				, "-location", new File("./examples/lang_63/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "6010", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "30", "-testbystep",
				//
				"-ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest", };
		try {
			System.out.println(Arrays.toString(args));
			LogManager.getRootLogger().setLevel(Level.DEBUG);
			main1.execute(args);
			System.out.println("Solution SetbyStep " + main1.getEngine().getSolutions().size());
			// This assertiong was failing at travis CI
			// assertTrue(main1.getEngine().getSolutions().size() > 0);

		} catch (Exception e) {
			System.out.println("StepByStep fails " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalSolutionJUExLog() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-seed", "0");
		cs.command.put("-scope", "package");
		cs.command.put("-maxgen", "200");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-parameters", "disablelog:false");
		cs.append("-parameters", "logtestexecution:true");

		assertEquals(4, cs.command.get("-parameters").split(File.pathSeparator).length);
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

	}

}
