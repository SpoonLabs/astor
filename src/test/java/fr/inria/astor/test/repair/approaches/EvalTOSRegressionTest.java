package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.EvalTOSBTApproach;
import fr.inria.astor.approaches.tos.core.UpdateParentDiffOrderFromJSON;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvalTOSRegressionTest {

	@Test
	public void test_Repair_Math85_Regression() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters",

				"clustercollectedvalues:true:disablelog:false:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName());

		this.executeAndAssert(command, 198, "if ((fa * fb) >= 0.0) {", null);
	}

	@Test
	public void test_Repair_Math74_Regression() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath74Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters",

				"clustercollectedvalues:true:disablelog:false:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName());

		this.executeAndAssert(command, 239, "double hNew", null);
	}

	@Test
	public void _failling_testBT_Math32_type_access() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath32Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "200");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.1");
		command.command.put("-saveall", "false");
		command.command.put("-parameters",

				"disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName() + ":skipfitnessinitialpopulation:true");
		// clustercollectedvalues:true:
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		assertEquals(1, approach.getSolutions().size());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, approach.getOutputStatus());

	}

	@Test
	public void test_repair_Math63_patch1_sametype() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath63Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "INFO");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "false");
		command.command.put("-parameters",

				"disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName() +
						// always type comparison
						":avoidtypecomparison:false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		assertEquals(1, approach.getSolutions().size());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, approach.getOutputStatus());

		assertEquals("1 == 0", approach.getSolutions().get(0).getAllOperations().get(0).getModified().toString());
		// return 1 == 0 || (x == y);
	}

	@Test
	public void testBT_Math63_repair_different_type() throws Exception {
		int maxSolutions = 10;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath63Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "INFO");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-saveall", "false");
		command.command.put("-parameters",

				"disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName() + ":avoidtypecomparison:true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		assertEquals(maxSolutions, approach.getSolutions().size());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, approach.getOutputStatus());

		// - return ((java.lang.Double.isNaN(x)) && (java.lang.Double.isNaN(y))) || (x
		// == y);
		// + return ((java.lang.Double.isNaN(0)) && (java.lang.Double.isNaN(y))) || (x
		// == y);

		boolean hasNewSolution = false;
		for (ProgramVariant solution : approach.getSolutions()) {
			hasNewSolution |= "x".equals(solution.getAllOperations().get(0).getModified().toString());
			// return 1 == 0 || (x == y);
		}
		assertTrue(hasNewSolution);
	}

	@Test
	public void testBT_Math50_CtTypeAccess() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath50Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "1000");
		command.command.put("-loglevel", "INFO");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.1");
		command.command.put("-saveall", "false");
		command.command.put("-parameters",

				"disablelog:false:maxnumbersolutions:" + maxSolutions + ":maxsolutionsperhole:1:sortholes:false"
				// + ":pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName() + ":skipfitnessinitialpopulation:true"
						+ ":regressionforfaultlocalization:true");
		// clustercollectedvalues:true:
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		assertEquals(1, approach.getSolutions().size());
		assertEquals(1, approach.getPatchInfo().size());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, approach.getOutputStatus());

		ProgramVariant pvariant = approach.getSolutions().get(0);
		assertEquals(1, pvariant.getAllOperations().size());
		OperatorInstance op1 = pvariant.getAllOperations().get(0);
		assertEquals(187, op1.getModificationPoint().getCodeElement().getPosition().getLine());
		assertEquals("BaseSecantSolver", op1.getModificationPoint().getCtClass().getSimpleName());

		PatchStat patch = approach.getPatchInfo().get(0);
		List<PatchHunkStats> hunks = (List<PatchHunkStats>) patch.getStats().get(PatchStatEnum.HUNKS);
		assertEquals(1, hunks.size());

		PatchHunkStats hunk1 = hunks.get(0);
		assertEquals("rtol", hunk1.getStats().get(HunkStatEnum.ORIGINAL_CODE));
		assertEquals("1 + ftol", hunk1.getStats().get(HunkStatEnum.PATCH_HUNK_CODE));

	}

	@Test
	// @Ignore
	// Repaired by Dynamoth (Precondition) and Cardumen
	// FL takes long..
	public void test_Not_Repair_Math28() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath28Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.01");
		command.command.put("-parameters",

				"clustercollectedvalues:true:disablelog:false:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName());

		this.executeAndAssert(command, 144, "if (i < minIndex)", null);
	}

	@Test
	// Repaired by Dynamoth (Precondition) and Cardumen
	public void test_NotRepaired_Math32() throws Exception {
		int maxSolutions = 4;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath32Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "200");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.1");
		command.command.put("-saveall", "false");
		command.command.put("-parameters",

				"disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName() + ":skipfitnessinitialpopulation:true");

		this.executeAndAssert(command, 330, "double inverse", null);

	}

	public static CommandSummary getQuixBugCommand4EvalTOS(String name) {
		int maxSolutions = 4;
		CommandSummary cs = new CommandSummary();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		cs.command.put("-javacompliancelevel", "8");
		cs.command.put("-seed", "1");
		cs.command.put("-package", "java_programs");
		cs.command.put("-dependencies", dep);
		cs.command.put("-scope", "package");
		cs.command.put("-mode", "jgenprog");
		cs.command.put("-id", name);
		cs.command.put("-population", "1");
		cs.command.put("-srcjavafolder", "/src");
		cs.command.put("-srctestfolder", "/src");
		cs.command.put("-binjavafolder", "/bin");
		cs.command.put("-bintestfolder", "/bin");
		cs.command.put("-flthreshold", "0.0");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-stopfirst", "TRUE");
		// |java_programs_test
		cs.command.put("-parameters",
				"logtestexecution:TRUE:" + "disablelog:TRUE:maxtime:120:loglevel:debug:autocompile:false"
						+ ":gzoltarpackagetonotinstrument:com.google.gson_engine:" + "maxnumbersolutions:"
						+ maxSolutions + ":maxsolutionsperhole:1:sortholes:false"
						// + ":pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName() + ":skipfitnessinitialpopulation:true"
						+ ":regressionforfaultlocalization:true");
		cs.command.put("-location", new File("./examples/quixbugscompiled/" + name).getAbsolutePath());

		return cs;
	}

	//// REPAIR BY Ex 3

	@Test // ex 3,1
	public void test_repair_QB_hanoi() throws Exception {
		this._base_QB("hanoi", 27, "steps.add", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_find_in_sorted() throws Exception {
		this._base_QB("find_in_sorted", 20, "return java_programs.FIND_IN_SORTED.binsearch", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_find_first_in_sorted() throws Exception {
		this._base_QB("find_first_in_sorted", 19, "while (lo ", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_powerset() throws Exception {
		this._base_QB("powerset", 35, "return output", "");
	}

	@Test // ex 3
	public void test_repair_QB_depth_first_search() throws Exception {
		this._base_QB("depth_first_search", 50, "this.successors", "this.successors = this.getSuccessors()");// Many
																												// patches
	}

	@Test // ex 3,1
	public void test_repair_QB_levenshtein() throws Exception {
		// See the line number of the suspicious
		this._base_QB("levenshtein", 17, "return 1 +", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_next_palindrome() throws Exception {
		// See that we have exceptions
		this._base_QB("next_palindrome", 35, "otherwise.addAll", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_get_factors() throws Exception {
		this._base_QB("get_factors", 18, "int max =", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_quicksort() throws Exception {
		// See that we have exceptions
		this._base_QB("quicksort", 26, "if (x > pivot)", "");
	}

	@Test // ex 3,1
	public void test_repair_QB_mergesort() throws Exception {
		this._base_QB("mergesort", 38, "if ((arr.size())", "");
	}

	@Test // ex 3
	public void test_repair_QB_lis() throws Exception {
		this._base_QB("lis", 31, "longest =", "longest = length + ends.size() - length");
		// longest = prefix_lengths.lastIndexOf((java.lang.Object) arr) + ends.size() +
		// 1;
	}

	//////////// results from ex 1

	@Test // ex 3 Not Repaired
	public void test_notrepair_QB_bucketsort() throws Exception {
		this._base_QB("bucketsort", 22, "for (java.lang.Integer", "");
	}

	@Test // ex 3 Not Repaired
	public void test_notrepairQB_pascal() throws Exception {
		this._base_QB("pascal", 22, "for (int c = 0", "");
	}

	@Test // ex 3 Not Repaired
	public void test_notrepairQB_shortest_path_lengths() throws Exception {
		this._base_QB("shortest_path_lengths", 41, "return length_by_path", "");
	}

	@Test // ex 3 Not Repaired
	public void test_notrepairQB_kth() throws Exception {
		this._base_QB("kth", 25, "return java_programs.KTH.kth", "");
	}

	////// Try to repair those not repaired in last execution

	@Test // ex 1 Repaired
	public void test_repairQB_kth_ex1_collectonlyusedmethod() throws Exception {

		CommandSummary command = (getQuixBugCommand4EvalTOS("kth"));
		String parameters = command.command.get("-parameters");
		command.command.put("-parameters", parameters + ":collectonlyusedmethod:true");

		executeAndAssert(command, 25, "return java_programs.KTH.kth", "");

	}

	@Test // ex 1 must be Repaired
	public void test_repairQB_shortest_path_lengths_ex1_collectonlyusedmethod() throws Exception {

		CommandSummary command = (getQuixBugCommand4EvalTOS("shortest_path_lengths"));
		String parameters = command.command.get("-parameters");
		command.command.put("-parameters", parameters + ":collectonlyusedmethod:true:avoidtypecomparison:true");

		executeAndAssert(command, 41, "return length_by_path", "");

	}

	@Test // ex 3 Repaired
	public void test_repairQB_pascal_lengths_ex1_collectonlyusedmethod() throws Exception {

		CommandSummary command = (getQuixBugCommand4EvalTOS("pascal"));
		String parameters = command.command.get("-parameters");
		command.command.put("-parameters", parameters + ":collectonlyusedmethod:true");

		executeAndAssert(command, 22, "for (int c = 0", "");

	}

	@Test // ex 3 Not Repaired
	public void test_repair_QB_bucketsort_ex1_collectonlyusedmethod() throws Exception {

		CommandSummary command = (getQuixBugCommand4EvalTOS("bucketsort"));
		String parameters = command.command.get("-parameters");
		command.command.put("-parameters", parameters + ":collectonlyusedmethod:true");

		executeAndAssert(command, 22, "for (java.lang.Integer", "");
	}

	public void _base_QB(String subject, int linesusp, String suspBegin, String patch) throws Exception {

		CommandSummary command = (getQuixBugCommand4EvalTOS(subject));
		executeAndAssert(command, linesusp, suspBegin, patch);

	}

	private void executeAndAssert(CommandSummary command, int linesusp, String suspBegin, String patch)
			throws Exception {
		AstorMain main1 = new AstorMain();
		command.command.put("-maxgen", "0");
		main1.execute(command.flat());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		ModificationPoint mp_buggy = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> ((e.getCodeElement().getPosition().getLine() == linesusp)
						&& e.getCodeElement().toString().startsWith(suspBegin)))
				.findFirst().get();

		System.out.println("Mp buggy: \n" + mp_buggy.getCodeElement().toString());
		System.out.println("Mp buggy possition: \n" + mp_buggy.identified);

		mp_buggy.getProgramVariant().getModificationPoints().clear();
		mp_buggy.getProgramVariant().getModificationPoints().add(mp_buggy);
		approach.MAX_GENERATIONS = 5000;
		approach.startEvolution();
		assertEquals(1, approach.getSolutions().size());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, approach.getOutputStatus());
		approach.atEnd();
	}

}
