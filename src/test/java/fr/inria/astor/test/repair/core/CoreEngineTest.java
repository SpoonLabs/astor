package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CoreEngineTest {

	@Test
	public void testReset() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-maxgen", "28");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-population", "1");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> variants = main1.getEngine().getSolutions();
		ProgramVariant vfin1 = variants.get(0);
		assertTrue(vfin1.getId() > 1);
		AstorCoreEngine engine = main1.getEngine();
		System.out.println(engine.getCurrentStat().getGeneralStats().get(GeneralStatEnum.NR_GENERATIONS));
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND,
				engine.getCurrentStat().getGeneralStats().get(GeneralStatEnum.OUTPUT_STATUS));

		engine.reset(vfin1);
		//We do not evolve
		ConfigurationProperties.setProperty("maxGeneration", "0");

		engine.startEvolution();
		engine.atEnd();

		ProgramVariant vfin2 = engine.getVariants().get(0);
		// Same object
		assertTrue(vfin1 == vfin2);

		assertEquals(AstorOutputStatus.MAX_GENERATION,
				engine.getCurrentStat().getGeneralStats().get(GeneralStatEnum.OUTPUT_STATUS));

	}
	
	@Test
	public void testResetUntilSol() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-maxgen", "10");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-population", "1");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		ProgramVariant vfin1 = variants.get(0);
		assertTrue(vfin1.getId() > 1);
		AstorCoreEngine engine = main1.getEngine();
		System.out.println(engine.getCurrentStat().getGeneralStats().get(GeneralStatEnum.NR_GENERATIONS));
		assertEquals(AstorOutputStatus.MAX_GENERATION,
				engine.getCurrentStat().getGeneralStats().get(GeneralStatEnum.OUTPUT_STATUS));

		engine.reset(vfin1);
		//We do not evolve
		ConfigurationProperties.setProperty("maxGeneration", "20");

		engine.startEvolution();
		engine.atEnd();

		

		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND,
				engine.getCurrentStat().getGeneralStats().get(GeneralStatEnum.OUTPUT_STATUS));

	}

}
