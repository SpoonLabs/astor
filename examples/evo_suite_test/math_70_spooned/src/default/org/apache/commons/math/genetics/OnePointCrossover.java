package org.apache.commons.math.genetics;


public class OnePointCrossover<T> implements org.apache.commons.math.genetics.CrossoverPolicy {
	@java.lang.SuppressWarnings(value = "unchecked")
	public org.apache.commons.math.genetics.ChromosomePair crossover(org.apache.commons.math.genetics.Chromosome first, org.apache.commons.math.genetics.Chromosome second) {
		if (!((first instanceof org.apache.commons.math.genetics.AbstractListChromosome<?>) && (second instanceof org.apache.commons.math.genetics.AbstractListChromosome<?>))) {
			throw new java.lang.IllegalArgumentException("One point crossover works on FixedLengthChromosomes only.");
		} 
		return crossover(((org.apache.commons.math.genetics.AbstractListChromosome<T>)(first)), ((org.apache.commons.math.genetics.AbstractListChromosome<T>)(second)));
	}

	private org.apache.commons.math.genetics.ChromosomePair crossover(org.apache.commons.math.genetics.AbstractListChromosome<T> first, org.apache.commons.math.genetics.AbstractListChromosome<T> second) {
		int length = first.getLength();
		if (length != (second.getLength()))
			throw new java.lang.IllegalArgumentException("Both chromosomes must have same lengths.");
		
		java.util.List<T> parent1Rep = first.getRepresentation();
		java.util.List<T> parent2Rep = second.getRepresentation();
		java.util.ArrayList<T> child1Rep = new java.util.ArrayList<T>(first.getLength());
		java.util.ArrayList<T> child2Rep = new java.util.ArrayList<T>(second.getLength());
		int crossoverIndex = 1 + (org.apache.commons.math.genetics.GeneticAlgorithm.getRandomGenerator().nextInt((length - 2)));
		for (int i = 0 ; i < crossoverIndex ; i++) {
			child1Rep.add(parent1Rep.get(i));
			child2Rep.add(parent2Rep.get(i));
		}
		for (int i = crossoverIndex ; i < length ; i++) {
			child1Rep.add(parent2Rep.get(i));
			child2Rep.add(parent1Rep.get(i));
		}
		return new org.apache.commons.math.genetics.ChromosomePair(first.newFixedLengthChromosome(child1Rep) , second.newFixedLengthChromosome(child2Rep));
	}
}

