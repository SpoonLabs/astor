package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.EvalTOSBTApproach;
import fr.inria.astor.approaches.tos.core.MetEngine;
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
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");

		command.command.put("-parameters",
				"disablelog:true:maxnumbersolutions:" + maxSolutions + ":logtestexecution:true" + ":");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
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

		MetEngine met = new MetEngine();

		List<CtExpression> candidates = new ArrayList();
		// for test
		CtExpression expNull = MutationSupporter.getFactory().createCodeSnippetExpression("true");

		candidates.add(expNull);

		CtExpression expReturnPatch = MutationSupporter.getFactory().createCodeSnippetExpression("(fa * fb) > 0.0");
		candidates.add(expReturnPatch);

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

}
