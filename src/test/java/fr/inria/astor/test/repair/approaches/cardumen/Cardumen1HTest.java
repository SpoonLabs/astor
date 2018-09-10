package fr.inria.astor.test.repair.approaches.cardumen;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.cardumholes.Cardumen1HApproach;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.TOSInstance;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientPool;
import fr.inria.astor.approaches.tos.ingredients.TOSIngredientTransformationStrategy;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Cardumen1HTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testCardumenHolesTestComponents() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-seed", "1");
		command.command.put("-maxgen", "0");
		command.command.put("-population", "1");
		command.command.put("-parameters",
				"nrPlaceholders:" + 1 + File.pathSeparator + "excludevariableplaceholder" + File.pathSeparator + "false" //
						+ File.pathSeparator + "excludeliteralplaceholder" + File.pathSeparator + "true"
						+ File.pathSeparator + "excludeinvocationplaceholder" + File.pathSeparator + "true"
						+ File.pathSeparator + "excludevarliteralplaceholder" + File.pathSeparator + "true"

		);

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertTrue(main1.getEngine() instanceof Cardumen1HApproach);
		Cardumen1HApproach cardumenholes = (Cardumen1HApproach) main1.getEngine();

		ProgramVariant pv = cardumenholes.getVariants().get(0);
		for (ModificationPoint mpi : pv.getModificationPoints()) {
			System.out.println("Element pointed: " + mpi.getCodeElement());
			assertTrue(mpi.getCodeElement() instanceof CtExpression<?>);
		}

		String expressionSumOP1 = "(a + b) * 0.5";
		assertTrue(pv.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals(expressionSumOP1)).findFirst().isPresent());

		String expressionMI2 = "solve(min, max)";
		assertTrue(pv.getModificationPoints().stream().filter(e -> e.getCodeElement().toString().equals(expressionMI2))
				.findFirst().isPresent());

		String expressionNotPresent = "solve(min1, max)";
		assertFalse(pv.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals(expressionNotPresent)).findFirst().isPresent());

		// Now, let's inspect the ingredient pool:
		IngredientPool ingredientPool = cardumenholes.getIngredientPool();
		assertTrue(ingredientPool instanceof TOSIngredientPool);
		TOSIngredientPool tosIngredientPool = (TOSIngredientPool) ingredientPool;

		List<Ingredient> allIngredients = tosIngredientPool.getAllIngredients();
		int i = 0;
		for (Ingredient ingredient : allIngredients) {
			TOSEntity tosIngredient = (TOSEntity) ingredient;
			CtElement codeTOS = tosIngredient.generateCodeofTOS();
			System.out.println("-ic->" + (i++) + " " + codeTOS);
		}

		// Now, let's transform ingredients

		assertTrue(cardumenholes.getIngredientTransformationStrategy() instanceof TOSIngredientTransformationStrategy);
		TOSIngredientTransformationStrategy tranformationStrategy = (TOSIngredientTransformationStrategy) cardumenholes
				.getIngredientTransformationStrategy();
		// take one mp
		ModificationPoint mp0 = pv.getModificationPoints().stream()
				.filter(e -> e.getCodeElement().toString().equals("solve(min, max)")).findFirst().get();
		assertEquals("solve(min, max)", mp0.getCodeElement().toString());
		// take one ingredient
		Ingredient i7 = allIngredients.stream()
				.filter(e -> e.getCode().toString().equals("verifyInterval(min, _double_0)")).findFirst().get();
		assertEquals("verifyInterval(min, _double_0)", i7.getCode().toString());

		// transform the ingredient according to mp
		List<Ingredient> transformations = tranformationStrategy.transform(mp0, i7);
		assertTrue(transformations.size() > 0);

		assertTrue(transformations.stream().anyMatch(e -> e.getCode().toString().equals("verifyInterval(min, max)")));
		assertTrue(transformations.stream().anyMatch(e -> e.getCode().toString().equals("verifyInterval(min, min)")));
		assertTrue(transformations.stream().anyMatch(e -> e.getCode().toString().equals("verifyInterval(min, min)")));
		assertTrue(
				transformations.stream().anyMatch(e -> e.getCode().toString().equals("verifyInterval(min, result)")));

		i = 0;
		for (Ingredient ingredient : transformations) {
			TOSInstance tosins = (TOSInstance) ingredient;
			System.out.println("-it->" + (i++) + " " + tosins.getCode());
			assertNotNull(tosins.getCode());
		}
	}

	@Test
	public void testCardumenHolesRepairTest() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		command.command.put("-flthreshold", "0.01");
		command.command.put("-maxgen", "1000");
		command.command.put("-stopfirst", "true");
		command.command.put("-population", "1");
		command.command.put("-parameters",
				"nrPlaceholders:" + 1 + File.pathSeparator + "excludevariableplaceholder" + File.pathSeparator + "false" //
						+ File.pathSeparator + "excludeliteralplaceholder" + File.pathSeparator + "true"
						+ File.pathSeparator + "excludeinvocationplaceholder" + File.pathSeparator + "true"
						+ File.pathSeparator + "excludevarliteralplaceholder" + File.pathSeparator + "true"

		);

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertTrue(main1.getEngine() instanceof Cardumen1HApproach);
		Cardumen1HApproach cardumenholes = (Cardumen1HApproach) main1.getEngine();

		assertTrue(cardumenholes.getSolutions().size() > 0);

		assertTrue(cardumenholes.getPatchInfo().size() > 0);

		PatchStat patch = cardumenholes.getPatchInfo().get(0);

		assertNotNull(patch.getStats().get(PatchStatEnum.PATCH_DIFF_ORIG));
		assertFalse(patch.getStats().get(PatchStatEnum.PATCH_DIFF_ORIG).toString().isEmpty());
	}

	@Test
	public void testCardumenHolesRepairExha() throws Exception {

		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", ExecutionMode.custom.toString());
		command.command.put("-customengine", Cardumen1HApproach.class.getCanonicalName());
		command.command.put("-flthreshold", "0.8");
		command.command.put("-maxgen", "1000000");
		command.command.put("-stopfirst", "false");
		command.command.put("-population", "1");
		command.command.put("-parameters",
				"nrPlaceholders:" + 1 + File.pathSeparator + "excludevariableplaceholder" + File.pathSeparator + "false" //
						+ File.pathSeparator + "excludeliteralplaceholder" + File.pathSeparator + "true"
						+ File.pathSeparator + "excludeinvocationplaceholder" + File.pathSeparator + "true"
						+ File.pathSeparator + "excludevarliteralplaceholder" + File.pathSeparator + "true"

		);

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		assertTrue(main1.getEngine() instanceof Cardumen1HApproach);
		Cardumen1HApproach cardumenholes = (Cardumen1HApproach) main1.getEngine();

		assertTrue(cardumenholes.getSolutions().size() > 0);

		assertTrue(cardumenholes.getPatchInfo().size() > 0);

		PatchStat patch = cardumenholes.getPatchInfo().get(0);

		assertNotNull(patch.getStats().get(PatchStatEnum.PATCH_DIFF_ORIG));
		assertFalse(patch.getStats().get(PatchStatEnum.PATCH_DIFF_ORIG).toString().isEmpty());

		assertEquals(AstorOutputStatus.EXHAUSTIVE_NAVIGATED, cardumenholes.getOutputStatus());
	}

}
