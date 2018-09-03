package fr.inria.astor.test.repair.approaches.scaffold;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.ScaffoldSynthesisEntry;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.ScaffoldExecutor;

public class ScaffoldSynthesizeTest {
	
	@Test
	public void testSynthesize() {
		SimpleSketch example=new SimpleSketch();
		
		String solution="";
		
		do {
			ScaffoldSynthesisEntry.reset();
			try {
				if (example.simpleExpError() ==2) {
					solution=ScaffoldSynthesisEntry.getString();
					break;
				} 
			} catch (Exception e) {}
		} while (ScaffoldExecutor.incrementCounter());

		assertTrue(solution.trim().equals("EXP-0:  a"));
	}
	
}
