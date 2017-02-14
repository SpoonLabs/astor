package fr.inria.astor.test.repair.evaluation.other;

import static org.junit.Assert.*;

import org.junit.Test;

import fr.inria.astor.core.stats.Stats;
/**
 * Test 
 * @author matias
 *
 */
public class StatsTest {

	@Test
	public void testIngredientCounter() {
		Stats cs = Stats.createStat();
		assertNotNull(cs);
		assertEquals(cs, Stats.getCurrentStats());
	
		int variantid = 1;
		
		assertEquals(1,cs.incrementIngCounter(variantid));
		
		assertEquals(2,cs.incrementIngCounter(variantid));
		
		assertEquals(2,cs.getIngCounter(variantid));
	
		//Now, we variant
		int variantid2 = 2;
		assertEquals(1,cs.incrementIngCounter(variantid2));
		assertEquals(1,cs.getIngCounter(variantid2));
		assertEquals(2,cs.getIngCounter(variantid));
		assertEquals(1,cs.getIngCounter(variantid2));
		assertEquals(3,cs.incrementIngCounter(variantid));
		//
		cs.storeIngCounterFromSuccessPatch(variantid);
		int counter_1 =  cs.ingAttemptsSuccessfulPatches.get(0);//first patch stored
		assertEquals(3, counter_1);
		
		cs.storeIngCounterFromSuccessPatch(variantid2);
		int counter_2 =  cs.ingAttemptsSuccessfulPatches.get(1);//second patch stored
		assertEquals(1, counter_2);

		//two counters
		assertEquals(2,cs.ingAttemptsSuccessfulPatches.size());
		//three attempts for the first variant (1st position)
		assertEquals(3,(int)cs.ingAttemptsSuccessfulPatches.get(0));
		//one attempt for the second variant
		assertEquals(1,(int)cs.ingAttemptsSuccessfulPatches.get(1));
		
		cs.resetIngCounter();
		assertTrue(cs.temporalIngCounter.isEmpty());

		assertTrue(cs.ingAttemptsSuccessfulPatches.isEmpty());
	}

}
