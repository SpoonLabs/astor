package org.apache.commons.math.genetics;


public class DummyRandomKey extends org.apache.commons.math.genetics.RandomKey<java.lang.String> {
	public DummyRandomKey(java.util.List<java.lang.Double> representation) {
		super(representation);
	}

	public DummyRandomKey(java.lang.Double[] representation) {
		super(representation);
	}

	@java.lang.Override
	public org.apache.commons.math.genetics.AbstractListChromosome<java.lang.Double> newFixedLengthChromosome(java.util.List<java.lang.Double> chromosomeRepresentation) {
		return new org.apache.commons.math.genetics.DummyRandomKey(chromosomeRepresentation);
	}

	public double fitness() {
		return 0;
	}
}

