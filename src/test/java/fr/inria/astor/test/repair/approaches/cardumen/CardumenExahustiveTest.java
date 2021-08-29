package fr.inria.astor.test.repair.approaches.cardumen;

import fr.inria.astor.approaches.cardumen.CardumenExhaustiveApproach;
import fr.inria.astor.approaches.cardumen.CardumenExhaustiveEngine4Stats;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;
import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenExahustiveTest extends BaseEvolutionaryTest {

	@Test
	public void testCardumentM70ExhausitveComplete() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.05");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveApproach.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		int maxSol = 7;
		command.command.put("-parameters", "limitbysuspicious:false:" + "disablelog:false:uniformreplacement:false:"
				+ "frequenttemplate:false:" + "maxnumbersolutions:" + maxSol);
		command.command.put("-loglevel", Level.INFO.toString());

		command.command.put("-maxVarCombination", "1000");
		command.command.put("-stopfirst", "false");
		command.command.put("-maxgen", "1000");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		AstorCoreEngine cardumen = main1.getEngine();
		List<ProgramVariant> solutions = cardumen.getSolutions();
		Assert.assertEquals(maxSol, solutions.size());
		List<PatchStat> patches = cardumen.getPatchInfo();
		Assert.assertEquals(maxSol, patches.size());
		//Patch only found by Cardumen
		PatchHunkStats hunkSolution = getHunkSolution(patches, "solve(f, defaultFunctionValueAccuracy, max)",
				"CtInvocationImpl|CtReturnImpl");
		Assert.assertNotNull(hunkSolution);
		assertEquals("solve(_UnivariateRealFunction_0, _double_1, _double_2)", hunkSolution.getStats().get(HunkStatEnum.INGREDIENT_PARENT).toString());
	}
	@Test
	public void testCardumentM70Exhausitve() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.LOCAL;

		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.05");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-parameters", "considerzerovaluesusp:true:scope:" + scope.toString().toLowerCase()
				+ "scope:limitbysuspicious:false:" + "disablelog:true:uniformreplacement:false:frequenttemplate:true");
		command.command.put("-loglevel", Level.INFO.toString());
		command.command.put("-maxVarCombination", "100000000");

		AstorMain main1 = new AstorMain();
		log.debug("LOCAL SCOPE");
		// Local
		command.command.put("-parameters", "considerzerovaluesusp:false:scope:local" + ":limitbysuspicious:false:"
				+ "disablelog:true:uniformreplacement:false:frequenttemplate:true");

		main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		assertEquals(85, cardumen.totalBases);

		log.debug("PACKAGE SCOPE");
		// PACKAGE
		command.command.put("-parameters", "considerzerovaluesusp:false:scope:package" + ":limitbysuspicious:false:"
				+ "disablelog:true:uniformreplacement:false:frequenttemplate:true");

		main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();
		int limit = 100;
		assertTrue(cardumen.totalBases +" >= "+limit, cardumen.totalBases >= limit);
	}
	

	@Test
	public void testCardumentM70ExhausitveMaxLimited() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.05");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters",
				"limitbysuspicious:false:" + "disablelog:true:uniformreplacement:false:frequenttemplate:true");
		command.command.put("-loglevel", Level.INFO.toString());
		command.command.put("-maxVarCombination", "1000");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();
		int limit = 86000000;
		//assertTrue(cardumen.totalIngredients + " > " + Integer.toString(limit), cardumen.totalIngredients >= limit);
		//assertTrue(86299730 >= cardumen.totalIngredientsCutted);
		//assertTrue(cardumen.totalIngredientsCutted >= 26000);
		//assertTrue(cardumen.totalBases >= 160);

	}

	@Test
	public void testCardumentM70ExhausitveMaxSuspiciousLimited() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;

		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "skipfitnessinitialpopulation:true:limitbysuspicious:false:"
				+ "disablelog:false:uniformreplacement:false:frequenttemplate:false");
		command.command.put("-loglevel", Level.INFO.toString());
		command.command.put("-maxVarCombination", "100");
		command.command.put("-maxsuspcandidates", "1000");

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		assertEquals(10, cardumen.totalmp);

		command.command.put("-maxsuspcandidates", "3");
		main1.execute(command.flat());
		Stats.createStat();
		cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();
		assertEquals(3, cardumen.totalmp);

		// assertEquals(100605077,cardumen.totalIngredients);
		// assertTrue(100605077 > cardumen.totalIngredientsCutted);
		// assertEquals(38222,cardumen.totalBases);

	}
	@Test
	public void testCardumentM70ExhausitveReplacement() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientPoolScope scope = IngredientPoolScope.PACKAGE;
		boolean uniformreplacement = false;
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.05");
		command.command.put("-maxtime", "60");
		command.command.put("-population", "1");
		command.command.put("-customengine", CardumenExhaustiveEngine4Stats.class.getCanonicalName());
		command.command.put("-scope", scope.toString().toLowerCase());
		command.command.put("-parameters", "skipfitnessinitialpopulation:true:limitbysuspicious:false:"
				+ "disablelog:false:uniformreplacement:" + Boolean.toString(uniformreplacement));
		command.command.put("-loglevel", Level.INFO.toString());

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());
		Stats.createStat();
		assertFalse(ConfigurationProperties.getPropertyBool("uniformreplacement"));

		CardumenExhaustiveEngine4Stats cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();

		assertTrue(cardumen.totalmp + " >= 12", cardumen.totalmp >= 12);
		long tingNotUnif = cardumen.totalIngredientsCutted;
		long tingNotUnifall = cardumen.totalIngredients;

		// changing property

		uniformreplacement = true;

		command.command.put("-parameters", "skipfitnessinitialpopulation:true:limitbysuspicious:false:"
				+ "disablelog:false:uniformreplacement:" + Boolean.toString(uniformreplacement));

		cardumen = null;
		main1.execute(command.flat());
		Stats.createStat();
		cardumen = (CardumenExhaustiveEngine4Stats) main1.getEngine();
		assertTrue(ConfigurationProperties.getPropertyBool("uniformreplacement"));
		long tingUnif = cardumen.totalIngredientsCutted;
		long tingUnifall = cardumen.totalIngredients;
		log.debug(tingNotUnif + " > " + tingUnif);
		assertTrue(tingNotUnif + " > " + tingUnif, tingNotUnif > tingUnif);
		log.debug(tingNotUnifall + " > " + tingUnifall);
		assertTrue(tingNotUnifall + " > " + tingUnifall, tingNotUnifall > tingUnifall);
	}

}
