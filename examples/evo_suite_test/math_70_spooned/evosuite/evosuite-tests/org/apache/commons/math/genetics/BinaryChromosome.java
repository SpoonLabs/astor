package org.apache.commons.math.genetics;


public abstract class BinaryChromosome extends org.apache.commons.math.genetics.AbstractListChromosome<java.lang.Integer> {
	public BinaryChromosome(java.util.List<java.lang.Integer> representation) {
		super(representation);
	}

	public BinaryChromosome(java.lang.Integer[] representation) {
		super(representation);
	}

	@java.lang.Override
	protected void checkValidity(java.util.List<java.lang.Integer> chromosomeRepresentation) throws org.apache.commons.math.genetics.InvalidRepresentationException {
		for (int i : chromosomeRepresentation) {
			if ((i < 0) || (i > 1)) {
				throw new org.apache.commons.math.genetics.InvalidRepresentationException("Elements can be only 0 or 1.");
			} 
		}
	}

	public static java.util.List<java.lang.Integer> randomBinaryRepresentation(int length) {
		java.util.List<java.lang.Integer> rList = new java.util.ArrayList<java.lang.Integer>(length);
		for (int j = 0 ; j < length ; j++) {
			rList.add(org.apache.commons.math.genetics.GeneticAlgorithm.getRandomGenerator().nextInt(2));
		}
		return rList;
	}

	@java.lang.Override
	protected boolean isSame(org.apache.commons.math.genetics.Chromosome another) {
		if (!(another instanceof org.apache.commons.math.genetics.BinaryChromosome)) {
			return false;
		} 
		org.apache.commons.math.genetics.BinaryChromosome anotherBc = ((org.apache.commons.math.genetics.BinaryChromosome)(another));
		if ((getLength()) != (anotherBc.getLength())) {
			return false;
		} 
		for (int i = 0 ; i < (getRepresentation().size()) ; i++) {
			if (!(getRepresentation().get(i).equals(anotherBc.getRepresentation().get(i)))) {
				return false;
			} 
		}
		return true;
	}
}

