package org.apache.commons.math.genetics;


public class FitnessCachingTest {
	private static final int DIMENSION = 50;

	private static final double CROSSOVER_RATE = 1;

	private static final double MUTATION_RATE = 0.1;

	private static final int TOURNAMENT_ARITY = 5;

	private static final int POPULATION_SIZE = 10;

	private static final int NUM_GENERATIONS = 50;

	private static final double ELITISM_RATE = 0.2;

	private static int fitnessCalls = 0;

	@org.junit.Test
	public void testFitnessCaching() {
		org.apache.commons.math.genetics.GeneticAlgorithm ga = new org.apache.commons.math.genetics.GeneticAlgorithm(new org.apache.commons.math.genetics.OnePointCrossover<java.lang.Integer>() , CROSSOVER_RATE , new org.apache.commons.math.genetics.BinaryMutation() , MUTATION_RATE , new org.apache.commons.math.genetics.TournamentSelection(TOURNAMENT_ARITY));
		org.apache.commons.math.genetics.Population initial = org.apache.commons.math.genetics.FitnessCachingTest.randomPopulation();
		org.apache.commons.math.genetics.StoppingCondition stopCond = new org.apache.commons.math.genetics.FixedGenerationCount(NUM_GENERATIONS);
		ga.evolve(initial, stopCond);
		int neededCalls = (POPULATION_SIZE) + (((NUM_GENERATIONS) - 1) * ((int)(((POPULATION_SIZE) * (1.0 - (ELITISM_RATE))))));
		org.junit.Assert.assertTrue(((org.apache.commons.math.genetics.FitnessCachingTest.fitnessCalls) <= neededCalls));
	}

	private static org.apache.commons.math.genetics.ElitisticListPopulation randomPopulation() {
		java.util.List<org.apache.commons.math.genetics.Chromosome> popList = new java.util.LinkedList<org.apache.commons.math.genetics.Chromosome>();
		for (int i = 0 ; i < (POPULATION_SIZE) ; i++) {
			org.apache.commons.math.genetics.BinaryChromosome randChrom = new org.apache.commons.math.genetics.FitnessCachingTest.DummyCountingBinaryChromosome(org.apache.commons.math.genetics.BinaryChromosome.randomBinaryRepresentation(DIMENSION));
			popList.add(randChrom);
		}
		return new org.apache.commons.math.genetics.ElitisticListPopulation(popList , popList.size() , ELITISM_RATE);
	}

	private static class DummyCountingBinaryChromosome extends org.apache.commons.math.genetics.DummyBinaryChromosome {
		public DummyCountingBinaryChromosome(java.util.List<java.lang.Integer> representation) {
			super(representation);
		}

		@java.lang.Override
		public double fitness() {
			(org.apache.commons.math.genetics.FitnessCachingTest.fitnessCalls)++;
			return 0;
		}
	}
}

