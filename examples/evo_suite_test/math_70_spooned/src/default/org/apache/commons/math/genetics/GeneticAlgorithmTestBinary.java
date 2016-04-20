package org.apache.commons.math.genetics;


public class GeneticAlgorithmTestBinary {
	private static final int DIMENSION = 50;

	private static final int POPULATION_SIZE = 50;

	private static final int NUM_GENERATIONS = 50;

	private static final double ELITISM_RATE = 0.2;

	private static final double CROSSOVER_RATE = 1;

	private static final double MUTATION_RATE = 0.1;

	private static final int TOURNAMENT_ARITY = 2;

	@org.junit.Test
	public void test() {
		org.apache.commons.math.genetics.GeneticAlgorithm ga = new org.apache.commons.math.genetics.GeneticAlgorithm(new org.apache.commons.math.genetics.OnePointCrossover<java.lang.Integer>() , CROSSOVER_RATE , new org.apache.commons.math.genetics.BinaryMutation() , MUTATION_RATE , new org.apache.commons.math.genetics.TournamentSelection(TOURNAMENT_ARITY));
		org.junit.Assert.assertEquals(0, ga.getGenerationsEvolved());
		org.apache.commons.math.genetics.Population initial = org.apache.commons.math.genetics.GeneticAlgorithmTestBinary.randomPopulation();
		org.apache.commons.math.genetics.StoppingCondition stopCond = new org.apache.commons.math.genetics.FixedGenerationCount(NUM_GENERATIONS);
		org.apache.commons.math.genetics.Chromosome bestInitial = initial.getFittestChromosome();
		org.apache.commons.math.genetics.Population finalPopulation = ga.evolve(initial, stopCond);
		org.apache.commons.math.genetics.Chromosome bestFinal = finalPopulation.getFittestChromosome();
		org.junit.Assert.assertTrue(((bestFinal.compareTo(bestInitial)) > 0));
		org.junit.Assert.assertEquals(NUM_GENERATIONS, ga.getGenerationsEvolved());
	}

	private static org.apache.commons.math.genetics.ElitisticListPopulation randomPopulation() {
		java.util.List<org.apache.commons.math.genetics.Chromosome> popList = new java.util.LinkedList<org.apache.commons.math.genetics.Chromosome>();
		for (int i = 0 ; i < (POPULATION_SIZE) ; i++) {
			org.apache.commons.math.genetics.BinaryChromosome randChrom = new org.apache.commons.math.genetics.GeneticAlgorithmTestBinary.FindOnes(org.apache.commons.math.genetics.BinaryChromosome.randomBinaryRepresentation(DIMENSION));
			popList.add(randChrom);
		}
		return new org.apache.commons.math.genetics.ElitisticListPopulation(popList , popList.size() , ELITISM_RATE);
	}

	private static class FindOnes extends org.apache.commons.math.genetics.BinaryChromosome {
		public FindOnes(java.util.List<java.lang.Integer> representation) {
			super(representation);
		}

		public double fitness() {
			int num = 0;
			for (int val : getRepresentation()) {
				if (val != 0)
					num++;
				
			}
			return num;
		}

		@java.lang.Override
		public org.apache.commons.math.genetics.AbstractListChromosome<java.lang.Integer> newFixedLengthChromosome(java.util.List<java.lang.Integer> chromosomeRepresentation) {
			return new org.apache.commons.math.genetics.GeneticAlgorithmTestBinary.FindOnes(chromosomeRepresentation);
		}
	}
}

