package org.apache.commons.math.genetics;


public class DummyBinaryChromosome extends org.apache.commons.math.genetics.BinaryChromosome {
	public DummyBinaryChromosome(java.util.List<java.lang.Integer> representation) {
		super(representation);
	}

	public DummyBinaryChromosome(java.lang.Integer[] representation) {
		super(representation);
	}

	@java.lang.Override
	public org.apache.commons.math.genetics.AbstractListChromosome<java.lang.Integer> newFixedLengthChromosome(java.util.List<java.lang.Integer> chromosomeRepresentation) {
		return new org.apache.commons.math.genetics.DummyBinaryChromosome(chromosomeRepresentation);
	}

	public double fitness() {
		return 0;
	}
}

