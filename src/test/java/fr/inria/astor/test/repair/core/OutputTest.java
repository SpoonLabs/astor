package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.output.PatchJSONStandarOutput;
import fr.inria.astor.core.output.StandardOutputReport;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class OutputTest {

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LogFileInfoLevel() throws Exception {
		System.out.println("START Test log file Info");
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-seed", "0");
		cs.command.put("-scope", "package");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-maxgen", "500");
		cs.command.put("-parameters", "disablelog:false");
		File fileLog = File.createTempFile("logTest", ".log");
		cs.append("-parameters", "logfilepath:" + fileLog.getAbsolutePath());

		assertEquals(4, cs.command.get("-parameters").split(File.pathSeparator).length);
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());


	}

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package This test validates the stats
	 * via API and JSON
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalOutputs() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

		Stats stats = Stats.getCurrentStat();

		assertNotNull(stats);

		String jsonpath = main1.getEngine().getProjectFacade().getProperties().getWorkingDirRoot() + File.separator
				+ ConfigurationProperties.getProperty("jsonoutputname") + ".json";
		File filejson = new File(jsonpath);
		assertTrue(filejson.exists());

		JSONParser parser = new JSONParser();

		Object obj = parser.parse(new FileReader(filejson));

		JSONObject jsonroot = (JSONObject) obj;

		// loop array
		JSONArray msg = (JSONArray) jsonroot.get("patches");
		assertEquals(1, msg.size());
		JSONObject pob = (JSONObject) msg.get(0);

		JSONArray hunks = (JSONArray) pob.get("patchhunks");
		assertEquals(1, hunks.size());
		JSONObject hunkob = (JSONObject) hunks.get(0);
		assertEquals("return solve(f, min, max)", hunkob.get(HunkStatEnum.PATCH_HUNK_CODE.name()));
		assertEquals("return solve(min, max)", hunkob.get(HunkStatEnum.ORIGINAL_CODE.name()));

		// Test API

		assertEquals(1, main1.getEngine().getPatchInfo().size());

		PatchStat patchstats = main1.getEngine().getPatchInfo().get(0);

		List<PatchHunkStats> hunksApi = (List<PatchHunkStats>) patchstats.getStats().get(PatchStatEnum.HUNKS);

		assertNotNull(hunksApi);

		PatchHunkStats hunkStats = hunksApi.get(0);

		assertNotNull(hunkStats);

		assertEquals("return solve(f, min, max)", hunkStats.getStats().get(HunkStatEnum.PATCH_HUNK_CODE));

		assertEquals("return solve(min, max)", hunkStats.getStats().get(HunkStatEnum.ORIGINAL_CODE));

		assertNotNull(hunkStats.getStats().get(HunkStatEnum.PATH));
		assertFalse(hunkStats.getStats().get(HunkStatEnum.PATH).toString().isEmpty());

	}

	@Test
	public void testExtPoint2Outputs() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-outputresult",
				PatchJSONStandarOutput.class.getCanonicalName() + "|" + StandardOutputReport.class.getCanonicalName());
		cs.command.put("-parameters", "outputjsonresult:true");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

		assertEquals(2, main1.getEngine().getOutputResults().size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testOutputsAPI1() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

		ProgramVariant solution = main1.getEngine().getSolutions().get(0);
		assertNotNull(solution.getPatchInfo());
		assertNotNull(solution.getPatchInfo().getStats().get(PatchStatEnum.PATCH_DIFF_ORIG));
		assertNotNull(solution.getPatchInfo().getStats().get(PatchStatEnum.HUNKS));
		assertNotNull(solution.getPatchInfo().getStats().get(PatchStatEnum.FOLDER_SOLUTION_CODE));
		assertFalse(((String) solution.getPatchInfo().getStats().get(PatchStatEnum.FOLDER_SOLUTION_CODE)).isEmpty());

		List<PatchHunkStats> hunksApi = (List<PatchHunkStats>) solution.getPatchInfo().getStats()
				.get(PatchStatEnum.HUNKS);
		assertTrue(hunksApi.size() > 0);
		PatchHunkStats hunkInfo = hunksApi.get(0);
		assertNotNull(hunkInfo.getStats().get(HunkStatEnum.PATH));
		assertFalse(hunkInfo.getStats().get(HunkStatEnum.PATH).toString().isEmpty());

	}

	@Test
	public void testMath70LogFile() throws Exception {

		System.out.println("START Test log DEBUG");

		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-seed", "0");
		cs.command.put("-scope", "package");
		cs.command.put("-loglevel", Level.DEBUG.toString());

		cs.command.put("-parameters", "disablelog:false");
		File fileLog = File.createTempFile("logTest", ".log");
		cs.append("-parameters", "logfilepath:" + fileLog.getAbsolutePath());

		assertEquals(4, cs.command.get("-parameters").split(File.pathSeparator).length);
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

	}

	@Test
	public void testMath70Outputg() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", //

				"-location", new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-maxgen", "0", "-maxtime", "10", "-stopfirst", "true"

		};
		CommandSummary command = new CommandSummary(args);
		System.out.println(Arrays.toString(args));
		main1.execute(command.flat());
		AstorCoreEngine engine = main1.getEngine();

		assertEquals(AstorOutputStatus.MAX_GENERATION, engine.getOutputStatus());

		command.command.put("-maxgen", "10");
		command.command.put("-maxtime", "0");

		main1.execute(command.flat());
		engine = main1.getEngine();

		assertEquals(AstorOutputStatus.TIME_OUT, engine.getOutputStatus());

		command.command.put("-maxtime", "60");
		command.command.put("-maxgen", "1000");
		main1.execute(command.flat());
		engine = main1.getEngine();

		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, engine.getOutputStatus());

	}

	@Test
	public void testMath70Path() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-parameters", "parsesourcefromoriginal:true");
		cs.command.put("-loglevel", "DEBUG");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		PatchStat patchstats = main1.getEngine().getPatchInfo().get(0);

		Stats stats = Stats.getCurrentStat();

		assertNotNull(stats);

		assertEquals(1, main1.getEngine().getPatchInfo().size());

		List<PatchHunkStats> hunksApi = (List<PatchHunkStats>) patchstats.getStats().get(PatchStatEnum.HUNKS);

		assertNotNull(hunksApi);

		PatchHunkStats hunkStats = hunksApi.get(0);

		assertNotNull(hunkStats);

		assertEquals("return solve(f, min, max)", hunkStats.getStats().get(HunkStatEnum.PATCH_HUNK_CODE));

		assertEquals("return solve(min, max)", hunkStats.getStats().get(HunkStatEnum.ORIGINAL_CODE));

		assertNotNull(hunkStats.getStats().get(HunkStatEnum.PATH));
		assertFalse(hunkStats.getStats().get(HunkStatEnum.PATH).toString().isEmpty());

		String mpath = hunkStats.getStats().get(HunkStatEnum.MODIFIED_FILE_PATH).toString();
		assertNotNull(mpath);
		assertTrue(new File(mpath).exists());

		String rpath = hunkStats.getStats().get(HunkStatEnum.PATH).toString();
		assertNotNull(rpath);
		assertTrue(new File(rpath).exists());

		assertFalse(rpath.equals(mpath));

	}

	@Test
	public void testMath70Identifier() throws Exception {
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
		String id = "math-70";
		cs.command.put("-id", id);
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-binjavafolder", "/target/classes");
		cs.command.put("-bintestfolder", "/target/test-classes");

		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientPoolScope.LOCAL, mi.getIngredientScope());

		assertEquals("return solve(f, min, max)", mi.getModified().toString());
		Object retrievedId = main1.getEngine().getCurrentStat().getGeneralStats()
				.get(GeneralStatEnum.EXECUTION_IDENTIFIER);
		assertNotNull(retrievedId);
		assertEquals(id, retrievedId);

	}
}
