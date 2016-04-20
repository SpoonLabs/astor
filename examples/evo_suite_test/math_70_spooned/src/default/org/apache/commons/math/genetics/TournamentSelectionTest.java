package org.apache.commons.math.genetics;


public class TournamentSelectionTest {
	private static int counter = 0;

	@org.junit.Test
	public void testSelect() {
		org.apache.commons.math.genetics.TournamentSelection ts = new org.apache.commons.math.genetics.TournamentSelection(2);
		org.apache.commons.math.genetics.ElitisticListPopulation pop = new org.apache.commons.math.genetics.ElitisticListPopulation(100 , 0.203);
		for (int i = 0 ; i < (pop.getPopulationLimit()) ; i++) {
			pop.addChromosome(new org.apache.commons.math.genetics.TournamentSelectionTest.DummyChromosome());
		}
		for (int i = 0 ; i < 20 ; i++) {
			org.apache.commons.math.genetics.ChromosomePair pair = ts.select(pop);
			org.junit.Assert.assertTrue(((pair.getFirst().getFitness()) > 0));
			org.junit.Assert.assertTrue(((pair.getSecond().getFitness()) > 0));
		}
	}

	private static class DummyChromosome extends org.apache.commons.math.genetics.Chromosome {
		private final int fitness;

		public DummyChromosome() {
			this.fitness = org.apache.commons.math.genetics.TournamentSelectionTest.counter;
			(org.apache.commons.math.genetics.TournamentSelectionTest.counter)++;
		}

		public double fitness() {
			return this.fitness;
		}
	}
}

