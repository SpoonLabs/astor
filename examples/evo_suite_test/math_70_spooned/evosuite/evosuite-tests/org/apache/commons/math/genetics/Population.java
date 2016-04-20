package org.apache.commons.math.genetics;


public interface Population extends java.lang.Iterable<org.apache.commons.math.genetics.Chromosome> {
	int getPopulationSize();

	int getPopulationLimit();

	org.apache.commons.math.genetics.Population nextGeneration();

	void addChromosome(org.apache.commons.math.genetics.Chromosome chromosome);

	org.apache.commons.math.genetics.Chromosome getFittestChromosome();
}

