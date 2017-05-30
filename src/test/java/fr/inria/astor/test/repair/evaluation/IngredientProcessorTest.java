package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.MethodInvocationFixSpaceProcessor;
import fr.inria.astor.test.repair.evaluation.regression.MathTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class IngredientProcessorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testM70DefaultStatement() throws Exception {

		CommandSummary command = MathTests.getMath70Command();
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

		ProgramVariant pv = solutions.get(0);
		for (ModificationPoint mp : pv.getModificationPoints()) {
			CtElement elementFromPoint = mp.getCodeElement();
			assertTrue(elementFromPoint instanceof CtStatement);
		}

	}
	
	@Test
	public void testM70MethodInvocation() throws Exception {

		CommandSummary command = MathTests.getMath70Command();
		command.command.put("-parameters",ExtensionPoints.INGREDIENT_PROCESSOR.identifier+File.pathSeparator+MethodInvocationFixSpaceProcessor.class.getCanonicalName());
		command.command.put("-maxgen","0");

		AstorMain main1 = new AstorMain();

		main1.execute(command.flat());
		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		ProgramVariant pv = variantss.get(0);
		for (ModificationPoint mp : pv.getModificationPoints()) {
			CtElement elementFromPoint = mp.getCodeElement();
			assertTrue(elementFromPoint instanceof CtInvocation);
		}

	}
	

}
