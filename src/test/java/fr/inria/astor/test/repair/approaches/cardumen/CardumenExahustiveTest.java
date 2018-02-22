package fr.inria.astor.test.repair.approaches.cardumen;

import java.util.List;

import org.apache.log4j.Level;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.CardumenExhaApproach;
import fr.inria.astor.approaches.cardumen.CardumenExhaustiveEngine4Stats;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
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
@Ignore
public class CardumenExahustiveTest extends BaseEvolutionaryTest {
	@Test
	public void testCardumentM70Exhausitve() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		getExhausConfig(command);

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		runExa(cardumen.getVariants().get(0), cardumen);

	}

	@Test
	public void testMath85Exha() throws Exception {
		CommandSummary command = MathCommandsTests.getMath85Command();

		getExhausConfig(command);

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		runExa(cardumen.getVariants().get(0), cardumen);
	}

	@Test
	public void testMath42Exha() throws Exception {
		CommandSummary command = MathCommandsTests.getMath42Command();

		getExhausConfig(command);
		command.append("-parameters", "maxnumvariablesperingredient:20");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		runExa(cardumen.getVariants().get(0), cardumen);
	}

	private void getExhausConfig(CommandSummary command) {
		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"limitbysuspicious:false:" + "disablelog:false:uniformreplacement:false:frequenttemplate:false");
		command.command.put("-loglevel", Level.DEBUG.toString());
		command.command.put("-maxVarCombination", "1000");
	}

	private void runExa(ProgramVariant pa, CardumenExhaustiveEngine4Stats engine) {

		for (ModificationPoint modifPoint : pa.getModificationPoints()) {
			// We create all operators to apply in the modifpoint

			AstorOperator pointOperation = engine.getOperatorSpace().getOperators().get(0);

			EfficientIngredientStrategy estrategy = (EfficientIngredientStrategy) engine.getIngredientSearchStrategy();

			List<CtCodeElement> baseElements = estrategy.getNotExhaustedBaseElements(modifPoint, pointOperation);

			if (baseElements == null) {
				continue;
			}
			System.out.println("###MP  "+modifPoint +" "+modifPoint.getCodeElement().getShortRepresentation() + ", "+((CtExpression)modifPoint.getCodeElement()).getType() +" "  + ": "+baseElements.size());
			
			for (CtCodeElement baseIngredient : baseElements) {

				long[] nrIngredients = engine.getNrIngredients(modifPoint, baseIngredient);

				log.debug("teo com size " + nrIngredients[0] + " cutted " + nrIngredients[1]);

				
				List<Ingredient> ingredients = engine.getIngredientTransformationStrategy().transform(modifPoint,
						new Ingredient(baseIngredient));

				long spacesize = nrIngredients[0];
				long cuttedspacesize = nrIngredients[1];

				if (ingredients != null) {
					log.debug("---> from all combin " + ingredients.size() + " vs from teo" + cuttedspacesize);
				//	Assert.assertEquals(ingredients.size(), cuttedspacesize);
				}

				if (nrIngredients[0] == 0) {
				//	Assert.assertTrue(ingredients == null || ingredients.isEmpty());

				}

			}
		}
	}

	@Test
	public void testCardumentM20Exhausitve() throws Exception {
		CommandSummary command = MathCommandsTests.getMath20Command();
		
		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"nomodificationconvergence:500:limitbysuspicious:false:frequenttemplate:false:tmax2:1960000:disablelog:true:uniformreplacement:false");
		command.command.put("-loglevel", Level.INFO.toString());
		command.command.put("-seed", "20003");
		//command.command.put("-maxVarCombination", "1000");
		
		//-parameters  nomodificationconvergence:500:limitbysuspicious:false:frequenttemplate:false:tmax2:1960000:disablelog:true:uniformreplacement:false
		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
	//	Stats.createStat();
	//	CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();
	//	runExa(cardumen.getVariants().get(0), cardumen);
	}
	
	@Test
	public void testCardumentM70ExhausitveComplete() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaApproach.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"limitbysuspicious:false:" + "disablelog:false:uniformreplacement:false:frequenttemplate:false");
		command.command.put("-loglevel", Level.DEBUG.toString());
		command.command.put("-maxVarCombination", "1000");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

	

	}
}
