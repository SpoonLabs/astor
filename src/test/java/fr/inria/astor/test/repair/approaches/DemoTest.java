package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.ingredientbased.IngredientBasedEvolutionaryRepairApproachImpl;
import fr.inria.astor.core.manipulation.filters.ExpressionBooleanIngredientSpaceProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.AstorCtIngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.InScopeVarsTransformation;
import fr.inria.astor.core.solutionsearch.spaces.operators.demo.InvocationFixSpaceProcessor;
import fr.inria.astor.core.solutionsearch.spaces.operators.demo.NullPreconditionOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.demo.NullPreconditionOperatorMI;
import fr.inria.astor.core.solutionsearch.spaces.operators.demo.NullPreconditionWithExpressionOperator;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class DemoTest {

	@Test
	public void testCase1_NullPreconditionM85() throws Exception {

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "jgenprog");
		command.command.put("-maxgen", "10");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.1");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), NullPreconditionOperator.class.getName());
		command.command.put("-out", new File("./output_astor/demo1").getAbsolutePath());
		command.command.put("-saveall", "true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertNotEquals(AstorOutputStatus.ERROR, main.getEngine().getOutputStatus());
	}

	@Test
	public void testCase1_NullPreconditionM32() throws Exception {

		CommandSummary command = MathCommandsTests.getMath32Command();
		command.command.put("-mode", "jgenprog");
		command.command.put("-maxgen", "10");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.1");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), NullPreconditionOperator.class.getName());
		command.command.put("-out", new File("./output_astor/demo1_math32").getAbsolutePath());
		command.command.put("-saveall", "true");
		command.command.put("-tmax1", "15000");

		AstorMain main = new AstorMain();
		main.execute(command.flat());
		assertEquals(1, main.getEngine().getSolutions().size());

		assertNotEquals(AstorOutputStatus.ERROR, main.getEngine().getOutputStatus());
	}

	@Test
	public void testCase2_PreconditionOnMethodInvocation() throws Exception {

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "jgenprog");
		command.command.put("-maxgen", "10");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.5");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), NullPreconditionOperatorMI.class.getName());
		command.command.put("-out", new File("./output_astor/demo2").getAbsolutePath());
		command.command.put("-saveall", "true");
		command.command.put(ExtensionPoints.TARGET_CODE_PROCESSOR.argument(),
				InvocationFixSpaceProcessor.class.getCanonicalName());
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertNotEquals(AstorOutputStatus.ERROR, main.getEngine().getOutputStatus());
	}

	@Test
	public void test1PreconditionOnMethodInvocation() throws Exception {

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "jgenprog");
		command.command.put("-maxgen", "10");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "true");
		command.command.put("-flthreshold", "0.5");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), NullPreconditionOperatorMI.class.getName());
		command.command.put("-out", new File("./output_astor/demo").getAbsolutePath());
		command.command.put("-saveall", "true");
		command.command.put(ExtensionPoints.TARGET_CODE_PROCESSOR.argument(),
				InvocationFixSpaceProcessor.class.getCanonicalName());
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertNotEquals(AstorOutputStatus.ERROR, main.getEngine().getOutputStatus());
	}

	@Test
	public void testCase3_PreconditionWithExpressionIngredientOnMethodInvocation() throws Exception {

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "jgenprog");
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.5");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), NullPreconditionOperatorMI.class.getName());
		command.command.put("-out", new File("./output_astor/demo").getAbsolutePath());
		command.command.put("-saveall", "true");
		command.command.put(ExtensionPoints.TARGET_INGREDIENT_CODE_PROCESSOR.argument(),
				ExpressionBooleanIngredientSpaceProcessor.class.getName());
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(),
				NullPreconditionWithExpressionOperator.class.getName());
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		IngredientBasedEvolutionaryRepairApproachImpl approach = (IngredientBasedEvolutionaryRepairApproachImpl) main
				.getEngine();
		List<ModificationPoint> modificationPoints = approach.getVariants().get(0).getModificationPoints();
		for (ModificationPoint modificationPoint : modificationPoints) {
			assertTrue("type: " + modificationPoint.getCodeElement(),
					modificationPoint.getCodeElement() instanceof CtStatement);
		}
		AstorCtIngredientPool pool = (AstorCtIngredientPool) approach.getIngredientPool();
		for (Ingredient ingredient : pool.getAllIngredients()) {
			assertTrue(ingredient.getCode() instanceof CtExpression);
			CtExpression expr = (CtExpression) ingredient.getCode();
			assertEquals("boolean", expr.getType().unbox().toString());
		}

		assertNotEquals(AstorOutputStatus.ERROR, main.getEngine().getOutputStatus());
	}

	@Test
	public void testCase3_PreconditionWithExpressionIngredientOnMethodInvocationTransform() throws Exception {

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "jgenprog");
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.5");
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(), NullPreconditionOperatorMI.class.getName());
		command.command.put("-out", new File("./output_astor/demo").getAbsolutePath());
		command.command.put("-saveall", "true");
		command.command.put(ExtensionPoints.TARGET_INGREDIENT_CODE_PROCESSOR.argument(),
				ExpressionBooleanIngredientSpaceProcessor.class.getName());
		command.command.put(ExtensionPoints.REPAIR_OPERATORS.argument(),
				NullPreconditionWithExpressionOperator.class.getName());
		command.command.put(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.argument(),
				InScopeVarsTransformation.class.getName());
		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertNotEquals(AstorOutputStatus.ERROR, main.getEngine().getOutputStatus());
	}
}
