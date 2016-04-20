package org.apache.commons.math.genetics;


public class GeneticAlgorithmTestPermutations {
	private static final int DIMENSION = 20;

	private static final int POPULATION_SIZE = 80;

	private static final int NUM_GENERATIONS = 200;

	private static final double ELITISM_RATE = 0.2;

	private static final double CROSSOVER_RATE = 1;

	private static final double MUTATION_RATE = 0.08;

	private static final int TOURNAMENT_ARITY = 2;

	private static final java.util.List<java.lang.Integer> sequence = new java.util.ArrayList<java.lang.Integer>();

	static {
		for (int i = 0 ; i < (DIMENSION) ; i++) {
			sequence.add(i);
		}
	}

	@org.junit.Test
	public void test() {
		org.apache.commons.math.genetics.GeneticAlgorithm ga = new org.apache.commons.math.genetics.GeneticAlgorithm(new org.apache.commons.math.genetics.OnePointCrossover<java.lang.Integer>() , CROSSOVER_RATE , new org.apache.commons.math.genetics.RandomKeyMutation() , MUTATION_RATE , new org.apache.commons.math.genetics.TournamentSelection(TOURNAMENT_ARITY));
		org.apache.commons.math.genetics.Population initial = org.apache.commons.math.genetics.GeneticAlgorithmTestPermutations.randomPopulation();
		org.apache.commons.math.genetics.StoppingCondition stopCond = new org.apache.commons.math.genetics.FixedGenerationCount(NUM_GENERATIONS);
		org.apache.commons.math.genetics.Chromosome bestInitial = initial.getFittestChromosome();
		org.apache.commons.math.genetics.Population finalPopulation = ga.evolve(initial, stopCond);
		org.apache.commons.math.genetics.Chromosome bestFinal = finalPopulation.getFittestChromosome();
		org.junit.Assert.assertTrue(((bestFinal.compareTo(bestInitial)) > 0));
	}

	private static org.apache.commons.math.genetics.ElitisticListPopulation randomPopulation() {
		java.util.List<org.apache.commons.math.genetics.Chromosome> popList = new java.util.ArrayList<org.apache.commons.math.genetics.Chromosome>();
		for (int i = 0 ; i < (POPULATION_SIZE) ; i++) {
			org.apache.commons.math.genetics.Chromosome randChrom = new org.apache.commons.math.genetics.GeneticAlgorithmTestPermutations.MinPermutations(org.apache.commons.math.genetics.RandomKey.randomPermutation(DIMENSION));
			popList.add(randChrom);
		}
		return new org.apache.commons.math.genetics.ElitisticListPopulation(popList , popList.size() , ELITISM_RATE);
	}

	private static class MinPermutations extends org.apache.commons.math.genetics.RandomKey<java.lang.Integer> {
		public MinPermutations(java.util.List<java.lang.Double> representation) {
			super(representation);
		}

		public double fitness() {
			int res = 0;
			java.util.List<java.lang.Integer> decoded = decode(org.apache.commons.math.genetics.GeneticAlgorithmTestPermutations.sequence);
			for (int i = 0 ; i < (decoded.size()) ; i++) {
				int value = decoded.get(i);
				if (value != i) {
					res += java.lang.Math.abs((value - i));
				} 
			}
			return -res;
		}

		@java.lang.Override
		public org.apache.commons.math.genetics.AbstractListChromosome<java.lang.Double> newFixedLengthChromosome(java.util.List<java.lang.Double> chromosomeRepresentation) {
			return new org.apache.commons.math.genetics.GeneticAlgorithmTestPermutations.MinPermutations(chromosomeRepresentation);
		}
	}
}

