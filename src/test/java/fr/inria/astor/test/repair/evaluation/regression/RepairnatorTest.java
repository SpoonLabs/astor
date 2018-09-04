package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import junit.framework.Assert;

/**
 * failing projects sent by Simon
 * 
 * @author Matias Martinez
 *
 */
public class RepairnatorTest {

	@Test
	@Ignore
	public void testjsoup285353482() throws Exception {
		String m2path = System.getenv("HOME") + "/.m2/repository/";
		File fm2 = new File(m2path);
		if (!fm2.exists()) {
			throw new Exception(m2path + "does not exit");
		}

		String[] command = new String[] { "-dependencies",
				m2path + "/junit/junit/4.12/junit-4.12.jar:" + m2path
						+ "/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:" + m2path
						+ "/com/google/code/gson/gson/2.7/gson-2.7.jar",
				"-mode", "jgenprog", "-location",
				new File("./examples/librepair-experiments-jhy-jsoup-285353482-20171009-062400_bugonly")
						.getAbsolutePath(),
				"-srcjavafolder", "/src/main/java", // Modified by M.M, original
													// path was absolute.
				"-stopfirst", "true", "-population", "1", "-maxgen", "50", // Added
																			// by
																			// M.M.,
																			// not
																			// present
																			// in
																			// command
																			// sent
																			// by
																			// S.U.
				"-parameters", "timezone:Europe/Paris:maxnumbersolutions:3:limitbysuspicious:false:" + "loglevel:DEBUG",
				"-maxtime", "100", "-seed", "1" };
		CommandSummary cs = new CommandSummary(command);
		AstorMain main1 = new AstorMain();
		main1.execute(cs.flat());

		assertTrue(main1.getEngine().getVariants().size() > 0);
		assertTrue(main1.getEngine().getVariants().get(0).getModificationPoints().size() > 0);

	}

	@Test
	@Ignore
	public void testjsoup285353482WithPackageInfo() throws Exception {
		String m2path = System.getenv("HOME") + "/.m2/repository/";
		File fm2 = new File(m2path);
		if (!fm2.exists()) {
			throw new Exception(m2path + "does not exit");
		}

		String[] command = new String[] { "-dependencies",
				m2path + "/junit/junit/4.12/junit-4.12.jar:" + m2path
						+ "/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:" + m2path
						+ "/com/google/code/gson/gson/2.7/gson-2.7.jar",
				"-mode", "jgenprog", "-location",
				new File(
						"./examples/librepair-experiments-jhy-jsoup-285353482-20171009-062400_bugonly_with_package_info")
								.getAbsolutePath(),
				"-srcjavafolder", "/src/main/java", // Modified by M.M, original
													// path was absolute.
				"-stopfirst", "true", "-population", "1", "-maxgen", "50", // Added
																			// by
																			// M.M.,
																			// not
																			// present
																			// in
																			// command
																			// sent
																			// by
																			// S.U.
				"-parameters", "timezone:Europe/Paris:maxnumbersolutions:3:limitbysuspicious:false:" + "loglevel:DEBUG",
				"-maxtime", "100", "-seed", "1" };
		CommandSummary cs = new CommandSummary(command);
		AstorMain main1 = new AstorMain();
		main1.execute(cs.flat());

		assertTrue(main1.getEngine().getVariants().size() > 0);
		assertTrue(main1.getEngine().getVariants().get(0).getModificationPoints().size() > 0);

	}

	@Test
	public void testMath70AbsolutesPaths() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;
		// Changing the location of source folders
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-location",
				new File("./examples/math_20").getAbsolutePath(), "-package", "org.apache.commons",
				// Absolute path
				"-srcjavafolder", new File("./examples/math_70").getAbsolutePath() + "/src/main/java/", //
				"-srctestfolder", new File("./examples/math_70").getAbsolutePath() + "/src/test/java/", //
				"-binjavafolder", new File("./examples/math_70").getAbsolutePath() + "/target/classes",
				"-bintestfolder", new File("./examples/math_70").getAbsolutePath() + "/target/test-classes", //
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "10", "-maxgen", Integer.toString(generations), "-stopfirst", "true", "-maxtime", "100",
				"-loglevel", "INFO", "-parameters", "disablelog:false"

		};

		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");

		System.out.println(Arrays.toString(cs.flat()));
		try {
			main1.execute(cs.flat());
			Assert.fail("The absolute path must not work");
		} catch (Exception e) {
			// we expect an exception
		}

	}

	@Test
	public void testMath70WrongPaths() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons",
				// Abstracts (refering to another folder)
				"-srcjavafolder", new File("./examples/math_70").getAbsolutePath() + "/src/main/java/", //
				"-srctestfolder", new File("./examples/math_70").getAbsolutePath() + "/src/test/java/", //
				"-binjavafolder", new File("./examples/math_70").getAbsolutePath() + "/target/classes",
				"-bintestfolder", new File("./examples/math_70").getAbsolutePath() + "/target/test-classes", //
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "10", "-maxgen", Integer.toString(generations), "-stopfirst", "true", "-maxtime", "100",
				"-loglevel", "INFO", "-parameters", "disablelog:false"

		};

		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");

		//
		cs.command.put("-binjavafolder", "blabla");

		System.out.println(Arrays.toString(cs.flat()));
		try {

			main1.execute(cs.flat());
			fail("expected ex wrong bin folder");
		} catch (Exception e) {
			// expected
		}
		cs.command.put("-binjavafolder", "/target/classes");
		cs.command.put("-bintestfolder", "/target/test-classes");
		// wrong path but astor resolves it
		cs.command.put("-srcjavafolder", new File("./examples/math_70").getAbsolutePath() + "/blabla1");

		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientPoolScope.LOCAL, mi.getIngredientScope());

		assertEquals("return solve(f, min, max)", mi.getModified().toString());

	}

	@Test
	@Ignore
	public void testAsc() throws Exception {
		// TODO.
		String[] command = new String[] { "-dependencies", "X", "-mode", "jgenprog", "-location",
				"/root/workspace/AsyncHttpClient/async-http-client/285409364/client", "-srcjavafolder",
				"/root/workspace/AsyncHttpClient/async-http-client/285409364/client/src/main/java", "-stopfirst",
				"true", "-population", "1", "-parameters",
				"timezone:Europe/Paris:maxnumbersolutions:3:limitbysuspicious:false:maxmodificationpoints:1000:javacompliancelevel:8:logfilepath:./workspace/AsyncHttpClient/async-http-client/285409364/repairnator.astor.log",
				"-maxtime", "100", "-seed", "1" };
		CommandSummary cs = new CommandSummary(command);
		AstorMain main1 = new AstorMain();
		main1.execute(cs.flat());

	}

}
