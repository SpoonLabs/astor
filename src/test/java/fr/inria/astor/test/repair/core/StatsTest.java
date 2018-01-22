package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;

/**
 * Test of ingredient counter
 * 
 * @author matias martinez
 *
 */
public class StatsTest {

	@Test
	public void testIngredientCounter() {
		Stats cs = Stats.createStat();
		assertNotNull(cs);
		assertEquals(cs, Stats.getCurrentStat());

		int variantid = 1;

		assertEquals(1, cs.getIngredientsStats().incrementIngCounter(variantid));

		assertEquals(2, cs.getIngredientsStats().incrementIngCounter(variantid));

		assertEquals(2, cs.getIngredientsStats().getIngCounter(variantid));

		// Now, we variant
		int variantid2 = 2;
		assertEquals(1, cs.getIngredientsStats().incrementIngCounter(variantid2));
		assertEquals(1, cs.getIngredientsStats().getIngCounter(variantid2));
		assertEquals(2, cs.getIngredientsStats().getIngCounter(variantid));
		assertEquals(1, cs.getIngredientsStats().getIngCounter(variantid2));
		assertEquals(3, cs.getIngredientsStats().incrementIngCounter(variantid));
		//
		cs.getIngredientsStats().storeIngCounterFromSuccessPatch(variantid);
		int counter_1 = cs.getIngredientsStats().ingAttemptsSuccessfulPatches.get(0).getAttempts();// first
																									// patch
																									// stored
		assertEquals(3, counter_1);

		cs.getIngredientsStats().storeIngCounterFromSuccessPatch(variantid2);
		int counter_2 = cs.getIngredientsStats().ingAttemptsSuccessfulPatches.get(1).getAttempts();// second
																									// patch
																									// stored
		assertEquals(1, counter_2);

		// two counters
		assertEquals(2, cs.getIngredientsStats().ingAttemptsSuccessfulPatches.size());
		// three attempts for the first variant (1st position)
		assertEquals(3, (int) cs.getIngredientsStats().ingAttemptsSuccessfulPatches.get(0).getAttempts());
		// one attempt for the second variant
		assertEquals(1, (int) cs.getIngredientsStats().ingAttemptsSuccessfulPatches.get(1).getAttempts());

		cs.getIngredientsStats().resetIngCounter();
		assertTrue(cs.getIngredientsStats().temporalIngCounterByPatch.isEmpty());

		assertTrue(cs.getIngredientsStats().ingAttemptsSuccessfulPatches.isEmpty());
	}
	
	@Test
	public void testIncrement(){
		Stats st = Stats.createStat();
		
		assertFalse(st.getGeneralStats().containsKey(GeneralStatEnum.NR_GENERATIONS));
		
		st.increment(GeneralStatEnum.NR_GENERATIONS);
		assertEquals(st.getGeneralStats().get(GeneralStatEnum.NR_GENERATIONS), 1);
		
		assertTrue(st.getGeneralStats().containsKey(GeneralStatEnum.NR_GENERATIONS));
		assertFalse(st.getGeneralStats().containsKey(GeneralStatEnum.NR_FAILING_VALIDATION_PROCESS));
		
		st.increment(GeneralStatEnum.NR_FAILING_VALIDATION_PROCESS);
		assertEquals(st.getGeneralStats().get(GeneralStatEnum.NR_FAILING_VALIDATION_PROCESS), 1);
		
		st.increment(GeneralStatEnum.NR_GENERATIONS);
		assertEquals(st.getGeneralStats().get(GeneralStatEnum.NR_GENERATIONS), 2);
		
		
		st.increment(GeneralStatEnum.NR_GENERATIONS);
		assertEquals(st.getGeneralStats().get(GeneralStatEnum.NR_GENERATIONS), 3);
		
	}

}
