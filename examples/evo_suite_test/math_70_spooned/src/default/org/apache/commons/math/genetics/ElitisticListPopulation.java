package org.apache.commons.math.genetics;


public class ElitisticListPopulation extends org.apache.commons.math.genetics.ListPopulation {
	private double elitismRate = 0.9;

	public ElitisticListPopulation(java.util.List<org.apache.commons.math.genetics.Chromosome> chromosomes ,int populationLimit ,double elitismRate) {
		super(chromosomes, populationLimit);
		this.elitismRate = elitismRate;
	}

	public ElitisticListPopulation(int populationLimit ,double elitismRate) {
		super(populationLimit);
		this.elitismRate = elitismRate;
	}

	public org.apache.commons.math.genetics.Population nextGeneration() {
		org.apache.commons.math.genetics.ElitisticListPopulation nextGeneration = new org.apache.commons.math.genetics.ElitisticListPopulation(getPopulationLimit() , getElitismRate());
		java.util.List<org.apache.commons.math.genetics.Chromosome> oldChromosomes = getChromosomes();
		java.util.Collections.sort(oldChromosomes);
		int boundIndex = ((int)(java.lang.Math.ceil(((1.0 - (getElitismRate())) * (oldChromosomes.size())))));
		for (int i = boundIndex ; i < (oldChromosomes.size()) ; i++) {
			nextGeneration.addChromosome(oldChromosomes.get(i));
		}
		return nextGeneration;
	}

	public void setElitismRate(double elitismRate) {
		if ((elitismRate < 0) || (elitismRate > 1))
			throw new java.lang.IllegalArgumentException("Elitism rate has to be in [0,1]");
		
		this.elitismRate = elitismRate;
	}

	public double getElitismRate() {
		return this.elitismRate;
	}
}

