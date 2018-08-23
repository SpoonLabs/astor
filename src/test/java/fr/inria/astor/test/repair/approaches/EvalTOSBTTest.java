package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.EvalTOSBTApproach;
import fr.inria.astor.approaches.tos.core.UpdateParentDiffOrderFromJSON;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesisContext;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesizerWOracle;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.EvaluatedExpression;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.MapList;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvalTOSBTTest {

	@Test
	public void testBT_Math85_SingleValue() throws Exception {
		// We want to find 4 solutions at most.
		int maxSolutions = 4;

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters",
				"clustercollectedvalues:false:disablelog:true:maxnumbersolutions:" + maxSolutions);

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);

		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		approach.analyzeModificationPointSingleValue(approach.getVariants().get(0), mp198);

		assertTrue(approach.getSolutions().size() > 0);
		assertEquals(maxSolutions, approach.getSolutions().size());
		// Call atEnd to print the solutions.
		approach.atEnd();

	}

	@Test
	public void testBT_Math85_1_ClusterMultipleExecution() throws Exception {
		// We want to find 4 solutions at most.
		int maxSolutions = 4;

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters", "disablelog:true:maxnumbersolutions:" + maxSolutions);

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);

		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		// approach.analyzeModificationPointTest(approach.getVariants().get(0),
		// mp198);

		final boolean stop = true;
		DynamothSynthesisContext contextCollected = approach.getCollectorFacade()
				.collectValues(approach.getProjectFacade(), mp198);

		// Creating combinations (do not depend on the Holes because
		// they are combination of variables in context of a
		// modification point)
		DynamothSynthesizerWOracle synthesizer = new DynamothSynthesizerWOracle(contextCollected);

		Candidates candidatesnew = synthesizer.combineValuesEvaluated();

		// System.out.println(candidatesnew);

		MapList<String, List<EvaluatedExpression>> cluster = approach.clusterCandidatesByValue(candidatesnew);

		///
		System.out.println("END clustering: Visualization cluster: ");

		for (String i_testName : cluster.keySet()) {

			List<List<EvaluatedExpression>> clusterOfTest = cluster.get(i_testName);

			System.out.println("--Test " + i_testName + " # clustersL " + clusterOfTest.size());
			int i = 0;
			for (List<EvaluatedExpression> aCluster : clusterOfTest) {

				System.out.println(String.format("c %d: nr evals %d pach first %s size %s, evaluations: %s ", i++,
						aCluster.get(0).getEvaluations().get(i_testName).size(), aCluster.get(0).asPatch(),
						aCluster.size(), aCluster.get(0).getEvaluations().get(i_testName)));
			}

		}

		// assertTrue(approach.getSolutions().size() > 0);
		// assertEquals(maxSolutions, approach.getSolutions().size());
		// Call atEnd to print the solutions.
		approach.atEnd();

	}

	@Test
	public void testBT_Math85_1_Evolve_Cluster_MultiExecution() throws Exception {
		// We want to find maxSolutions solutions at most.
		int maxSolutions = 100;

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters", "disablelog:true:maxnumbersolutions:" + maxSolutions);

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);
		approach.getSolutions().clear();
		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		approach.analyzeModificationPointMultipleExecutions(approach.getVariants().get(0), mp198);
		assertTrue(approach.getSolutions().size() > 0);
		// assertEquals(maxSolutions, approach.getSolutions().size());
		// Call atEnd to print the solutions.
		approach.atEnd();
	}

	@Test
	public void testBT_Math_70_1() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.5");
		command.command.put("-parameters", "disablelog:true:clustercollectedvalues:false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		assertTrue(approach.getSolutions().isEmpty());
	}

	@Test
	public void testBT_Math85_1_Sort() throws Exception {
		// We want to find maxSolutions solutions at most.
		int maxSolutions = 100;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");

		command.command.put("-parameters",
				"disablelog:true:maxnumbersolutions:" + maxSolutions + ":pathjsonfrequency:" + filef.getAbsolutePath());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);

		ConfigurationProperties.setProperty("sortholes", "false");
		List<CtCodeElement> holes = approach.calculateAllHoles(mp198);
		List<CtCodeElement> holesSorted = approach.orderHoleElements(holes);

		assertEquals(holes.size(), holesSorted.size());
		assertEquals(holes, holesSorted);

		ConfigurationProperties.setProperty("sortholes", "true");
		holes = approach.calculateAllHoles(mp198);
		holesSorted = approach.orderHoleElements(holes);

		assertEquals(holes.size(), holesSorted.size());
		assertTrue(!holes.equals(holesSorted));

	}

	@Test
	public void testBT_Math85_SingleValue_SingleChange() throws Exception {
		// One per each term in the if
		int maxSolutions = 5;
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters", "clustercollectedvalues:false:disablelog:true:maxnumbersolutions:"
				+ maxSolutions + ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);

		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		approach.analyzeModificationPointSingleValue(approach.getVariants().get(0), mp198);
		// Call atEnd to print the solutions.
		approach.atEnd();
		assertTrue(approach.getSolutions().size() > 0);
		assertEquals(maxSolutions, approach.getSolutions().size());

	}

	@Test
	public void testBT_Math85_SingleValue_UpdateParent() throws Exception {
		// One per each term in the if
		int maxSolutions = 4;// TODO:check
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters",
				"clustercollectedvalues:false:disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		assertTrue(approach.getHoleOrderEngine() instanceof UpdateParentDiffOrderFromJSON);

		UpdateParentDiffOrderFromJSON reader = (UpdateParentDiffOrderFromJSON) approach.getHoleOrderEngine();

		assertTrue(reader.accept("UPD_d_s"));
		assertFalse(reader.accept("INS_d_s"));

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);

		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		approach.analyzeModificationPointSingleValue(approach.getVariants().get(0), mp198);
		// Call atEnd to print the solutions.
		approach.atEnd();
		assertTrue(approach.getSolutions().size() > 0);
		assertEquals(maxSolutions, approach.getSolutions().size());

	}

	@Test
	public void testBT_Math85_Cluster_UpdateParent() throws Exception {
		// One per each term in the if
		int maxSolutions = 4;// TODO:check
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		// command.command.put("-customengine",
		// EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters",

				"clustercollectedvalues:true:disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		assertTrue(approach.getHoleOrderEngine() instanceof UpdateParentDiffOrderFromJSON);

		UpdateParentDiffOrderFromJSON reader = (UpdateParentDiffOrderFromJSON) approach.getHoleOrderEngine();

		assertTrue(reader.accept("UPD_d_s"));
		assertFalse(reader.accept("INS_d_s"));

		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertEquals(40, mp198.identified);

		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		approach.analyzeModificationPointMultipleExecutions(approach.getVariants().get(0), mp198);
		// Call atEnd to print the solutions.
		approach.atEnd();
		assertTrue(approach.getSolutions().size() > 0);
		assertEquals(maxSolutions, approach.getSolutions().size());

	}

	@Test
	public void testBT_Math85_Cluster_UpdateParent_EvolveRemovingMP() throws Exception {
		// One per each term in the if
		int maxSolutions = 4;// TODO:check
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

				"clustercollectedvalues:true:disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

		approach.getVariants().get(0).getModificationPoints()
				.removeIf(e -> !(e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")));

		assertEquals(1, approach.getVariants().get(0).getModificationPoints().size());

		approach.MAX_GENERATIONS = 1000;
		approach.startEvolution();
		approach.atEnd();
		assertTrue(main.getEngine().getSolutions().size() > 0);

	}

	@Test
	public void testBT_Math85_Cluster_UpdateParent_Evolve() throws Exception {
		// One per each term in the if
		int maxSolutions = 4;// TODO:check
		File filef = new File("src/test/resources/changes_analisis_frequency.json");
		assertTrue(filef.exists());

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters",

				"clustercollectedvalues:true:disablelog:true:maxnumbersolutions:" + maxSolutions
						+ ":maxsolutionsperhole:1:sortholes:true:pathjsonfrequency:" + filef.getAbsolutePath()
						+ ":holeorder:" + UpdateParentDiffOrderFromJSON.class.getName() + ":customengine:"
						+ EvalTOSBTApproach.class.getCanonicalName());

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();

	}

}
