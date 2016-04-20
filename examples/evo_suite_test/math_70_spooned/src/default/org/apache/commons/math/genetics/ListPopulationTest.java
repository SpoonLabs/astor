package org.apache.commons.math.genetics;


public class ListPopulationTest {
	@org.junit.Test
	public void testGetFittestChromosome() {
		org.apache.commons.math.genetics.Chromosome c1 = new org.apache.commons.math.genetics.Chromosome() {
			public double fitness() {
				return 0;
			}
		};
		org.apache.commons.math.genetics.Chromosome c2 = new org.apache.commons.math.genetics.Chromosome() {
			public double fitness() {
				return 10;
			}
		};
		org.apache.commons.math.genetics.Chromosome c3 = new org.apache.commons.math.genetics.Chromosome() {
			public double fitness() {
				return 15;
			}
		};
		java.util.ArrayList<org.apache.commons.math.genetics.Chromosome> chromosomes = new java.util.ArrayList<org.apache.commons.math.genetics.Chromosome>();
		chromosomes.add(c1);
		chromosomes.add(c2);
		chromosomes.add(c3);
		org.apache.commons.math.genetics.ListPopulation population = new org.apache.commons.math.genetics.ListPopulation(chromosomes, 10) {
			public org.apache.commons.math.genetics.Population nextGeneration() {
				return null;
			}
		};
		org.junit.Assert.assertEquals(c3, population.getFittestChromosome());
	}
}

