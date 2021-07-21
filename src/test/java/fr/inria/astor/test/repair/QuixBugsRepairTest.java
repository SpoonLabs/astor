package fr.inria.astor.test.repair;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.CardumenApproach;
import fr.inria.astor.approaches.cardumen.CardumenExhaustiveApproach;
import fr.inria.astor.approaches.cardumen.ExpressionReplaceOperator;
import fr.inria.astor.approaches.cardumholes.Cardumen1HApproach;
import fr.inria.astor.approaches.jgenprog.extension.TibraApproach;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.NoIngredientTransformation;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * Testing all repaired bugs by Astor from Quixbugs from our experiment
 * 
 * @author Matias Martinez
 *
 */
public class QuixBugsRepairTest {

	public static CommandSummary getQuixBugsCommand(String name) {

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
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-stopfirst", "TRUE");
		cs.command.put("-parameters", "logtestexecution:TRUE:"
				+ "disablelog:FALSE:maxtime:120:autocompile:false:gzoltarpackagetonotinstrument:com.google.gson_engine"
				+ GZoltarFaultLocalization.PACKAGE_SEPARATOR + "java_programs_test");
		cs.command.put("-location", new File("./examples/quixbugscompiled/" + name).getAbsolutePath());

		return cs;
	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_shortest_path_lengthsRepair_statement() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("shortest_path_lengths"));
		command.command.put("-maxgen", "500");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_depth_first_searchRepair_statement() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("depth_first_search"));
		command.command.put("-maxgen", "500");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_depth_first_searchRepair_cardumen() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("depth_first_search"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "cardumen");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_depth_first_searchRepair_jkali() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("depth_first_search"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "jkali");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_next_permutationRepair_mutation() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("next_permutation"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "mutation");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_next_knapsackRepair_mutation() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("knapsack"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "mutation");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_quicksortRepair_mutation() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("quicksort"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "mutation");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_quicksortRepair_jkali() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("quicksort"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "jkali");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_rpn_evalRepair_Cardumen() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("rpn_eval"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "cardumen");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_levenshteinRepair_cardumen() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("levenshtein"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "cardumen");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void test_get_factorsRepair_cardumen() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("get_factors"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "cardumen");
		command.command.put("-saveall", "true");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_get_factorsRepair_cardumen_exha() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("get_factors"));
		command.command.put("-maxgen", "500");
		command.command.put("-mode", "custom");
		command.command.put("-customengine", CardumenExhaustiveApproach.class.getName());
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	@Test
	public void test_get_factorsRepair_cardumen_Debug_mode() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("get_factors"));
		command.command.put("-maxgen", "0");
		command.command.put("-mode", "cardumen");
		command.append("-parameters", "packageToInstrument:java_programs");

		main1.execute(command.flat());

		ModificationPoint mpl20 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("i < max")).findFirst().get();
		assertNotNull(mpl20);
		System.out.println("MP 20 " + mpl20 + " code " + mpl20.getCodeElement());

		// assertTrue("No solution",main1.getEngine().getSolutions().size() > 0);

		CardumenApproach cardumentapproach = (CardumenApproach) main1.getEngine();
		// cardumentapproach.createOperatorInstanceForPoint(mpl20);
		cardumentapproach.getVariants().get(0).getModificationPoints().clear();
		cardumentapproach.getVariants().get(0).getModificationPoints().add(mpl20);

		ConfigurationProperties.setProperty("maxGeneration", "100");
		cardumentapproach.startEvolution();
		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_mergesortRepair_cardumen_debug() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("mergesort"));
		command.command.put("-maxgen", "0");// do not evolve right now
		command.command.put("-mode", "cardumen");
		command.command.put("-saveall", "true");
		main1.execute(command.flat());

		// - if ((arr.size()) == 0) {
		// + if (((arr.size()) / 2) == 0) {

		String susp = "arr.size()";

		ModificationPoint buggyModifPoint = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals(susp)
						&& e.getCodeElement().getPosition().getLine() == 38)
				.findFirst().get();
		assertNotNull(buggyModifPoint);
		System.out.println("MP 27 " + buggyModifPoint + " code " + buggyModifPoint.getCodeElement());

		// Remove other modification points
		AstorCoreEngine approach = (AstorCoreEngine) main1.getEngine();
		approach.getVariants().get(0).getModificationPoints().clear();
		approach.getVariants().get(0).getModificationPoints().add(buggyModifPoint);

		ConfigurationProperties.setProperty("maxGeneration", "1000");
		approach.startEvolution();
		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);
		approach.atEnd();

	}

	@Test
	public void test_mergesortRepair_cardumen() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("mergesort"));
		command.command.put("-maxgen", "1500");
		command.command.put("-mode", "cardumen");
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);
		// - if ((arr.size()) == 0) {
		// + if (((arr.size()) / 2) == 0) {
		String susp = "arr.size()";
		assertTrue(checkPatch(main1.getEngine(), susp, "(arr.size() / 2)"));
	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_powersetRepair_tibra() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("powerset"));
		command.command.put("-maxgen", "1000");
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TibraApproach.class.getCanonicalName());

		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void test_lisRepair_tibra() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("lis"));
		command.command.put("-maxgen", "1000");
		command.command.put("-mode", "custom");
		command.command.put("-customengine", TibraApproach.class.getCanonicalName());

		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */

	@Test
	public void testLISRepair_statement() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("lis"));
		command.command.put("-maxgen", "1000");
		// The suspicious is a var declaration used in the patch, so, the default
		// transformation ignore it
		command.append("-parameters", ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + ":"
				+ NoIngredientTransformation.class.getName());
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);

		assertTrue(checkPatch(main1.getEngine(),
				"int length = (!prefix_lengths.isEmpty()) ? java.util.Collections.max(prefix_lengths) : 0",
				"ends.put(length + 1, i)"));

	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */

	@Test
	public void testLISRepair_statement_debug() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("lis"));
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "ERROR");
		command.append("-parameters", ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier + ":"
				+ NoIngredientTransformation.class.getName());

		main1.execute(command.flat());

		String susp = "int length = (!prefix_lengths.isEmpty()) ? java.util.Collections.max(prefix_lengths) : 0";

		ModificationPoint mpl20 = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals(susp)).findFirst().get();
		assertNotNull(mpl20);
		System.out.println("MP 27 " + mpl20 + " code " + mpl20.getCodeElement());

		AstorCoreEngine approach = (AstorCoreEngine) main1.getEngine();
		approach.getVariants().get(0).getModificationPoints().clear();
		approach.getVariants().get(0).getModificationPoints().add(mpl20);
		approach.getOperatorSpace().getOperators().removeIf(e -> !e.name().startsWith("Insert"));

		ConfigurationProperties.setProperty("maxGeneration", "1000");
		approach.startEvolution();
		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);
		approach.atEnd();

		assertTrue(checkPatch(approach,
				"int length = (!prefix_lengths.isEmpty()) ? java.util.Collections.max(prefix_lengths) : 0",
				"ends.put(length + 1, i)"));
	}

	public boolean checkPatch(AstorCoreEngine approach, String original, String patched) {
		for (PatchStat patch : approach.getPatchInfo()) {
			List<PatchHunkStats> hunks = (List<PatchHunkStats>) patch.getStats().get(PatchStatEnum.HUNKS);
			assertEquals(1, hunks.size());

			PatchHunkStats hunk1 = hunks.get(0);
			boolean matchoriginal = (original.equals(hunk1.getStats().get(HunkStatEnum.ORIGINAL_CODE)));
			boolean matchpatched = (patched.equals(hunk1.getStats().get(HunkStatEnum.PATCH_HUNK_CODE)));
			if (matchoriginal && matchpatched)
				return true;
		}
		return false;
	}

	/**
	 * Repaired in paper
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLISSpaces() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("lis"));
		command.command.put("-maxgen", "0");

		main1.execute(command.flat());

		IngredientPoolLocationType pool = (IngredientPoolLocationType) ((IngredientBasedApproach) main1.getEngine())
				.getIngredientPool();

		for (ModificationPoint mp : main1.getEngine().getVariants().get(0).getModificationPoints()) {
			System.out.println("mp: " + mp.getCodeElement());
		}

		List<Ingredient> allIngredients = pool.getAllIngredients();
		for (Ingredient ingredient : allIngredients) {
			System.out.println("-ing->" + ingredient);
		}

		Optional<Ingredient> opIng1 = allIngredients.stream()
				.filter(e -> e.getCode().toString().equals("ends.put(length + 1, i)")).findAny();
		assertTrue(opIng1.isPresent());
		Ingredient ing1 = opIng1.get();

		Optional<ModificationPoint> opMP = main1.getEngine().getVariants().get(0).getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().startsWith("int length")).findAny();
		assertTrue(opMP.isPresent());

	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void ne_test_depth_first_searchRepair_cardumen1h() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("depth_first_search"));
		command.command.put("-maxgen", "2000");
		command.command.put("-scope", "global");
		command.command.put("-stopfirst", "false");
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		main1.execute(command.flat());

		assertTrue(main1.getEngine().getSolutions().size() >= 2);
		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, main1.getEngine().getOutputStatus());

	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void ne_test_levenshteinRepair_cardumen1h() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("levenshtein"));
		command.command.put("-maxgen", "2000");
		command.command.put("-scope", "global");
		command.command.put("-stopfirst", "false");
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		main1.execute(command.flat());

		assertTrue("No solution", main1.getEngine().getSolutions().size() > 0);
		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, main1.getEngine().getOutputStatus());
	}

	/**
	 * With Flacoco, there are less modification points in this test case and it fails due to out of bounds exception
	 */
	@Test
	@Ignore
	public void ne_test_rpn_evalRepair_cardumen1h() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("rpn_eval"));
		command.command.put("-maxgen", "20000");
		command.command.put("-scope", "global");
		command.command.put("-stopfirst", "false");
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		main1.execute(command.flat());

		Cardumen1HApproach engine = (Cardumen1HApproach) main1.getEngine();
		printSpaces(engine);

		ModificationPoint mp23 = engine.getVariants().get(0).getModificationPoints().get(22);
		List<OperatorInstance> instances = engine.createIngredientOpInstance((SuspiciousModificationPoint) mp23,
				new ExpressionReplaceOperator());
		for (OperatorInstance operatorInstance : instances) {
			System.out.println("op instance: " + operatorInstance);
		}
		assertEquals(0, engine.getSolutions().size());
		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, engine.getOutputStatus());

	}

	private void printSpaces(AstorCoreEngine engine) {
		IngredientPoolLocationType pool = (IngredientPoolLocationType) ((IngredientBasedApproach) engine)
				.getIngredientPool();
		int i = 0;
		for (ModificationPoint mp : engine.getVariants().get(0).getModificationPoints()) {
			System.out.println("mp: " + (i++) + " " + mp.getCodeElement());
		}

		List<Ingredient> allIngredients = pool.getAllIngredients();
		i = 0;
		for (Ingredient ingredient : allIngredients) {
			System.out.println("-ing->" + (i++) + " " + ingredient);
		}
	}

	/**
	 * 
	 * 
	 * @throws Exception
	 */
	@Test
	public void ne_test_hanoiRepair_cardumen() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("hanoi"));
		command.command.put("-maxgen", "1000");
		command.command.put("-mode", "cardumen");
		main1.execute(command.flat());

		assertFalse(main1.getEngine().getSolutions().isEmpty());

	}

	@Test
	@Ignore
	public void ne_test_hanoiRepair_cardumen1h() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary command = (getQuixBugsCommand("hanoi"));
		command.command.put("-maxgen", "1000");
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		command.command.put("-scope", "package");
		main1.execute(command.flat());

		assertTrue(main1.getEngine().getSolutions().size() == 0);

	}
}
