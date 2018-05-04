package fr.inria.astor.test.repair.evaluation.extensionpoints.ingredients;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.MethodInvocationFixSpaceProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtExpression;
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

		CommandSummary command = MathCommandsTests.getMath70Command();
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

		ProgramVariant pv = solutions.get(0);
	
		JGenProg jgp = (JGenProg) main1.getEngine();
		AstorIngredientSpace ingSpace = (AstorIngredientSpace) jgp.getIngredientSearchStrategy().getIngredientSpace();

		List ingredients = ingSpace.getAllIngredients();
		assertTrue(ingredients.size() > 0);

		checkIngredientTypes(solutions, ingSpace, CtStatement.class);
	}

	@Test
	public void testM70MethodInvocation() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.INGREDIENT_PROCESSOR.identifier + File.pathSeparator
				+ MethodInvocationFixSpaceProcessor.class.getCanonicalName());
		command.command.put("-maxgen", "0");

		AstorMain main1 = new AstorMain();

		main1.execute(command.flat());
		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		ProgramVariant pv = variantss.get(0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		AstorIngredientSpace ingSpace = (AstorIngredientSpace) jgp.getIngredientSearchStrategy().getIngredientSpace();

		checkIngredientTypes(variantss, ingSpace, CtInvocation.class);
	}

	@Test
	public void testM70Expression() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.INGREDIENT_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionIngredientSpaceProcessor.class.getCanonicalName());
		command.command.put("-maxgen", "0");// Avoid evolution

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		AstorIngredientSpace ingSpace = (AstorIngredientSpace) jgp.getIngredientSearchStrategy().getIngredientSpace();

		checkIngredientTypes(variantss, ingSpace, CtExpression.class);

	}

	public void checkIngredientTypes(List<ProgramVariant> variantss, AstorIngredientSpace ingSpace,
			Class classToProcess) {
		ProgramVariant pv = variantss.get(0);
		for (ModificationPoint modificationPoint : pv.getModificationPoints()) {
			CtElement elementFromPoint = modificationPoint.getCodeElement();
			assertTrue(classToProcess.isInstance(elementFromPoint));
		}

		List ingredients = ingSpace.getAllIngredients();
		assertTrue(ingredients.size() > 0);

		for (Object ingredient : ingredients) {
			assertTrue(classToProcess.isInstance(ingredient));
		}
	}

}
