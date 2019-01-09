package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.InitialConceptMetEngine;
import fr.inria.astor.approaches.tos.core.evalTos.EvalTOSClusterApproach;
import fr.inria.astor.approaches.tos.core.evalTos.MetaEvalTOSApproach;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetEngineTest {

	@Test
	public void testBT_Math85_1_Met() throws Exception {
		// We want to find maxSolutions solutions at most.
		int maxSolutions = 100;

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSClusterApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");

		command.command.put("-parameters",
				"disablelog:true:maxnumbersolutions:" + maxSolutions + ":logtestexecution:true" + ":");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSClusterApproach);
		EvalTOSClusterApproach approach = (EvalTOSClusterApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0).getModificationPoints().stream()
				.filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e.getCodeElement().getPosition()
						.getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);

		assertTrue(mp198.getCodeElement() instanceof CtIf);
		CtIf pointedIf198 = (CtIf) mp198.getCodeElement();

		assertEquals(40, mp198.identified);

		ConfigurationProperties.setProperty("sortholes", "false");
		List<CtCodeElement> holes = approach.calculateAllHoles(mp198);

		InitialConceptMetEngine met = new InitialConceptMetEngine();

		List<Ingredient> candidates = new ArrayList();
		// for test
		CtExpression expNull = MutationSupporter.getFactory().createCodeSnippetExpression("true");

		candidates.add(new Ingredient(expNull));

		CtExpression expReturnPatch = MutationSupporter.getFactory().createCodeSnippetExpression("(fa * fb) > 0.0");
		candidates.add(new Ingredient(expReturnPatch));

		ProgramVariant variant = main.getEngine().getVariants().get(0);
		List<OperatorInstance> newOperations = met.transform(variant, mp198, pointedIf198.getCondition(), candidates);

		// for each operation, we apply

		for (OperatorInstance operatorInstance : newOperations) {
			boolean appliedOp = operatorInstance.applyModification();

		}
		// The default case:
		ConfigurationProperties.setProperty("metid", "0");
		boolean resultValidation = approach.processCreatedVariant(variant, 0);

		assertFalse(resultValidation);
		assertNotNull(variant.getCompilation());
		assertTrue(variant.getCompilation().compiles());

		assertFalse(variant.getValidationResult().isSuccessful());
		assertFalse(resultValidation);

		assertTrue(variant.getValidationResult() instanceof TestCasesProgramValidationResult);
		assertEquals(1, ((TestCasesProgramValidationResult) variant.getValidationResult()).getFailureCount());

		/// The wrong patch
		ConfigurationProperties.setProperty("metid", "1");
		resultValidation = approach.processCreatedVariant(variant, 0);

		assertFalse(resultValidation);
		assertNotNull(variant.getCompilation());
		assertTrue(variant.getCompilation().compiles());

		assertFalse(variant.getValidationResult().isSuccessful());
		assertFalse(resultValidation);

		assertTrue(variant.getValidationResult() instanceof TestCasesProgramValidationResult);
		assertEquals(2, ((TestCasesProgramValidationResult) variant.getValidationResult()).getFailureCount());

		/// The patch

		ConfigurationProperties.setProperty("metid", "2");
		resultValidation = approach.processCreatedVariant(variant, 0);

		assertTrue(resultValidation);
		assertNotNull(variant.getCompilation());
		assertTrue(variant.getCompilation().compiles());

		assertTrue(variant.getValidationResult().isSuccessful());
		assertTrue(resultValidation);

		assertTrue(variant.getValidationResult() instanceof TestCasesProgramValidationResult);
		assertEquals(0, ((TestCasesProgramValidationResult) variant.getValidationResult()).getFailureCount());

		// Undo
		for (OperatorInstance operatorInstance : newOperations) {
			boolean appliedOp = operatorInstance.undoModification();
		}

		// Again the default (failing).
		resultValidation = approach.processCreatedVariant(variant, 0);

		assertFalse(resultValidation);
		assertNotNull(variant.getCompilation());
		assertTrue(variant.getCompilation().compiles());

		assertFalse(variant.getValidationResult().isSuccessful());
		assertFalse(resultValidation);

	}

	@Test
	public void test_doomyTry_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testTrySimple1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-try");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		ProgramVariant solution1 = main1.getEngine().getSolutions().get(0);

		assertTrue(solution1.getAllOperations().stream().filter(e -> e.getModified().toString().startsWith("try {"))
				.findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			try {"));
		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff()
				.contains("catch (java.lang.Exception e) {}"));

	}

	@Test
	public void test_doomyIf_1() throws Exception {

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary command = new CommandSummary();
		command.command.put("-location", new File("./examples/testMet/testIfSimple1").getAbsolutePath());
		command.command.put("-mode", "custom");
		command.command.put("-customengine", MetaEvalTOSApproach.class.getName());
		command.command.put("-javacompliancelevel", "7");
		command.command.put("-maxtime", "120");
		command.command.put("-seed", "0");
		command.command.put("-stopfirst", "true");
		command.command.put("-maxgen", "1000000");
		command.command.put("-population", "1");
		command.command.put("-scope", "local");
		command.command.put("-srcjavafolder", "src/main/java/");
		command.command.put("-srctestfolder", "src/test/java/");
		command.command.put("-binjavafolder", "target/classes/");
		command.command.put("-bintestfolder", "target/test-classes/");
		command.command.put("-id", "test-if1");
		command.command.put("-out", out.getAbsolutePath());
		command.command.put("-dependencies", dep);
		command.command.put("-loglevel", "INFO");
		command.command.put("-flthreshold", "0.24");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		assertEquals(2, main1.getEngine().getSolutions().size());

		// Solution 1
		ProgramVariant solution1 = main1.getEngine().getSolutions().get(0);

		assertTrue(solution1.getAllOperations().stream()
				.filter(e -> e.getModified().toString().startsWith("if (1 == 0")).findFirst().isPresent());

		assertTrue(solution1.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			if (1 == 0)"));

		// Solution 2
		ProgramVariant solution2 = main1.getEngine().getSolutions().get(1);

		assertTrue(solution2.getAllOperations().stream()
				.filter(e -> e.getModified().toString().startsWith("if (i2 == 1)")).findFirst().isPresent());

		assertTrue(solution2.getPatchDiff().getOriginalStatementAlignmentDiff().contains("+			if (i2 == 1)"));

	}

}
