package fr.inria.astor.test.repair.evaluation;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.CardumenExhaustiveEngine4Stats;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.validation.validators.ProgramValidator;
import fr.inria.astor.test.repair.evaluation.regression.MathTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenExahustiveTest {
	@Test
	public void testCardumentM70Exhausitve() throws Exception {
		CommandSummary command = MathTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"limitbysuspicious:false:" + "disablelog:true:uniformreplacement:false:frequenttemplate:true");
		command.command.put("-loglevel", Level.DEBUG.toString());
		command.command.put("-maxVarCombination", "1000");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		runExa(cardumen.getVariants().get(0), cardumen);

	}

	private void runExa(ProgramVariant pa, CardumenExhaustiveEngine4Stats engine) {

		for (ModificationPoint modifPoint : pa.getModificationPoints()) {
			// We create all operators to apply in the modifpoint

			AstorOperator pointOperation = engine.getOperatorSpace().getOperators().get(0);

			EfficientIngredientStrategy estrategy = (EfficientIngredientStrategy) engine.getIngredientStrategy();

			List<CtCodeElement> baseElements = estrategy.getNotExhaustedBaseElements(modifPoint, pointOperation);

			if (baseElements == null) {
				continue;
			}

			for (CtCodeElement baseIngredient : baseElements) {

				System.out.println("ddd");
				long[] nrIngredients = engine.getNrIngredients(modifPoint, baseIngredient);

				List<Ingredient> ingredients = engine.getIngredientTransformationStrategy().transform(modifPoint,
						new Ingredient(baseIngredient));

				long spacesize = nrIngredients[0];
				long cuttedspacesize = nrIngredients[1];

				if (ingredients != null) {
					System.out.println("--->" + ingredients.size() + " vs " + cuttedspacesize);
					Assert.assertEquals(ingredients.size(), cuttedspacesize);
				}

				if (nrIngredients[0] == 0) {
					Assert.assertTrue(ingredients == null || ingredients.isEmpty());

				}

			}
		}
	}

}
