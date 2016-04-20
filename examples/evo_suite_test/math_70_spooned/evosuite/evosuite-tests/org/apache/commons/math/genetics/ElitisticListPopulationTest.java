package org.apache.commons.math.genetics;


public class ElitisticListPopulationTest {
	private static int counter = 0;

	@org.junit.Test
	public void testNextGeneration() {
		org.apache.commons.math.genetics.ElitisticListPopulation pop = new org.apache.commons.math.genetics.ElitisticListPopulation(100 , 0.203);
		for (int i = 0 ; i < (pop.getPopulationLimit()) ; i++) {
			pop.addChromosome(new org.apache.commons.math.genetics.ElitisticListPopulationTest.DummyChromosome());
		}
		org.apache.commons.math.genetics.Population nextGeneration = pop.nextGeneration();
		org.junit.Assert.assertEquals(20, nextGeneration.getPopulationSize());
	}

	private static class DummyChromosome extends org.apache.commons.math.genetics.Chromosome {
		private final int fitness;

		public DummyChromosome() {
			this.fitness = org.apache.commons.math.genetics.ElitisticListPopulationTest.counter;
			(org.apache.commons.math.genetics.ElitisticListPopulationTest.counter)++;
		}

		public double fitness() {
			return this.fitness;
		}
	}
}

