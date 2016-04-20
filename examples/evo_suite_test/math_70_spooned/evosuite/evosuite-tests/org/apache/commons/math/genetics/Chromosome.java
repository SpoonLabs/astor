package org.apache.commons.math.genetics;


public abstract class Chromosome implements java.lang.Comparable<org.apache.commons.math.genetics.Chromosome> , org.apache.commons.math.genetics.Fitness {
	private double fitness = java.lang.Double.MIN_VALUE;

	public double getFitness() {
		if ((this.fitness) == (java.lang.Double.MIN_VALUE)) {
			this.fitness = fitness();
		} 
		return this.fitness;
	}

	public int compareTo(org.apache.commons.math.genetics.Chromosome another) {
		return ((java.lang.Double)(getFitness())).compareTo(another.getFitness());
	}

	protected boolean isSame(org.apache.commons.math.genetics.Chromosome another) {
		return false;
	}

	protected org.apache.commons.math.genetics.Chromosome findSameChromosome(org.apache.commons.math.genetics.Population population) {
		for (org.apache.commons.math.genetics.Chromosome anotherChr : population) {
			if (isSame(anotherChr)) {
				return anotherChr;
			} 
		}
		return null;
	}

	public void searchForFitnessUpdate(org.apache.commons.math.genetics.Population population) {
		org.apache.commons.math.genetics.Chromosome sameChromosome = findSameChromosome(population);
		if (sameChromosome != null) {
			fitness = sameChromosome.getFitness();
		} 
	}
}

