package org.apache.commons.math.genetics;


public class ChromosomePair {
	private final org.apache.commons.math.genetics.Chromosome first;

	private final org.apache.commons.math.genetics.Chromosome second;

	public ChromosomePair(final org.apache.commons.math.genetics.Chromosome c1 ,final org.apache.commons.math.genetics.Chromosome c2) {
		super();
		first = c1;
		second = c2;
	}

	public org.apache.commons.math.genetics.Chromosome getFirst() {
		return first;
	}

	public org.apache.commons.math.genetics.Chromosome getSecond() {
		return second;
	}

	@java.lang.Override
	public java.lang.String toString() {
		return java.lang.String.format("(%s,%s)", getFirst(), getSecond());
	}
}

