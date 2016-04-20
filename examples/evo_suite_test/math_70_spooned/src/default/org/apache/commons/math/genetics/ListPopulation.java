package org.apache.commons.math.genetics;


public abstract class ListPopulation implements org.apache.commons.math.genetics.Population {
	private java.util.List<org.apache.commons.math.genetics.Chromosome> chromosomes;

	private int populationLimit;

	public ListPopulation(java.util.List<org.apache.commons.math.genetics.Chromosome> chromosomes ,int populationLimit) {
		if ((chromosomes.size()) > populationLimit) {
			throw new java.lang.IllegalArgumentException("List of chromosomes bigger than maxPopulationSize.");
		} 
		if (populationLimit < 0) {
			throw new java.lang.IllegalArgumentException("Population limit has to be >= 0");
		} 
		this.chromosomes = chromosomes;
		this.populationLimit = populationLimit;
	}

	public ListPopulation(int populationLimit) {
		if (populationLimit < 0) {
			throw new java.lang.IllegalArgumentException("Population limit has to be >= 0");
		} 
		this.populationLimit = populationLimit;
		this.chromosomes = new java.util.ArrayList<org.apache.commons.math.genetics.Chromosome>(populationLimit);
	}

	public void setChromosomes(java.util.List<org.apache.commons.math.genetics.Chromosome> chromosomes) {
		this.chromosomes = chromosomes;
	}

	public java.util.List<org.apache.commons.math.genetics.Chromosome> getChromosomes() {
		return chromosomes;
	}

	public void addChromosome(org.apache.commons.math.genetics.Chromosome chromosome) {
		this.chromosomes.add(chromosome);
	}

	public org.apache.commons.math.genetics.Chromosome getFittestChromosome() {
		org.apache.commons.math.genetics.Chromosome bestChromosome = this.chromosomes.get(0);
		for (org.apache.commons.math.genetics.Chromosome chromosome : this.chromosomes) {
			if ((chromosome.compareTo(bestChromosome)) > 0) {
				bestChromosome = chromosome;
			} 
		}
		return bestChromosome;
	}

	public int getPopulationLimit() {
		return this.populationLimit;
	}

	public void setPopulationLimit(int populationLimit) {
		this.populationLimit = populationLimit;
	}

	public int getPopulationSize() {
		return this.chromosomes.size();
	}

	@java.lang.Override
	public java.lang.String toString() {
		return this.chromosomes.toString();
	}

	public java.util.Iterator<org.apache.commons.math.genetics.Chromosome> iterator() {
		return chromosomes.iterator();
	}
}

