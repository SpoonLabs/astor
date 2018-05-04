package fr.inria.astor.test.repair.approaches.cardumen;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.cardumen.CardumenExhaustiveApproach;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenExahustiveTest extends BaseEvolutionaryTest {

	@Test
	public void testCardumentM70ExhausitveComplete() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();

		IngredientSpaceScope scope = IngredientSpaceScope.PACKAGE;
		// Configuration for paper experiment
		command.command.put("-mode", ExecutionMode.custom.name());
		command.command.put("-flthreshold", "0.1");
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
		assertEquals("solve(_UnivariateRealFunction_0, _double_1, _double_2)", hunkSolution.getStats().get(HunkStatEnum.INGREDIENT).toString());
	}

}
