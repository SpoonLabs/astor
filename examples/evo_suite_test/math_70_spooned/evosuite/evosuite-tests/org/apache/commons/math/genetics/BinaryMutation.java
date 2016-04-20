package org.apache.commons.math.genetics;


public class BinaryMutation implements org.apache.commons.math.genetics.MutationPolicy {
	public org.apache.commons.math.genetics.Chromosome mutate(org.apache.commons.math.genetics.Chromosome original) {
		if (!(original instanceof org.apache.commons.math.genetics.BinaryChromosome)) {
			throw new java.lang.IllegalArgumentException("Binary mutation works on BinaryChromosome only.");
		} 
		org.apache.commons.math.genetics.BinaryChromosome origChrom = ((org.apache.commons.math.genetics.BinaryChromosome)(original));
		java.util.List<java.lang.Integer> newRepr = new java.util.ArrayList<java.lang.Integer>(origChrom.getRepresentation());
		int geneIndex = org.apache.commons.math.genetics.GeneticAlgorithm.getRandomGenerator().nextInt(origChrom.getLength());
		newRepr.set(geneIndex, ((origChrom.getRepresentation().get(geneIndex)) == 0 ? 1 : 0));
		org.apache.commons.math.genetics.Chromosome newChrom = origChrom.newFixedLengthChromosome(newRepr);
		return newChrom;
	}
}

