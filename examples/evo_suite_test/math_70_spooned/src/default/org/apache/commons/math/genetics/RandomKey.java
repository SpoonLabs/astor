package org.apache.commons.math.genetics;


public abstract class RandomKey<T> extends org.apache.commons.math.genetics.AbstractListChromosome<java.lang.Double> implements org.apache.commons.math.genetics.PermutationChromosome<T> {
	private final java.util.List<java.lang.Double> sortedRepresentation;

	private final java.util.List<java.lang.Integer> baseSeqPermutation;

	public RandomKey(java.util.List<java.lang.Double> representation) {
		super(representation);
		java.util.List<java.lang.Double> sortedRepr = new java.util.ArrayList<java.lang.Double>(getRepresentation());
		java.util.Collections.sort(sortedRepr);
		sortedRepresentation = java.util.Collections.unmodifiableList(sortedRepr);
		baseSeqPermutation = java.util.Collections.unmodifiableList(org.apache.commons.math.genetics.RandomKey.decodeGeneric(org.apache.commons.math.genetics.RandomKey.baseSequence(getLength()), getRepresentation(), sortedRepresentation));
	}

	public RandomKey(java.lang.Double[] representation) {
		this(java.util.Arrays.asList(representation));
	}

	public java.util.List<T> decode(java.util.List<T> sequence) {
		return org.apache.commons.math.genetics.RandomKey.decodeGeneric(sequence, getRepresentation(), sortedRepresentation);
	}

	private static <S>java.util.List<S> decodeGeneric(java.util.List<S> sequence, java.util.List<java.lang.Double> representation, java.util.List<java.lang.Double> sortedRepr) {
		int l = sequence.size();
		if ((representation.size()) != l) {
			throw new java.lang.IllegalArgumentException(java.lang.String.format("Length of sequence for decoding (%s) has to be equal to the length of the RandomKey (%s)", l, representation.size()));
		} 
		if ((representation.size()) != (sortedRepr.size())) {
			throw new java.lang.IllegalArgumentException(java.lang.String.format("Representation and sortedRepr must have same sizes, %d != %d", representation.size(), sortedRepr.size()));
		} 
		java.util.List<java.lang.Double> reprCopy = new java.util.ArrayList<java.lang.Double>(representation);
		java.util.List<S> res = new java.util.ArrayList<S>(l);
		for (int i = 0 ; i < l ; i++) {
			int index = reprCopy.indexOf(sortedRepr.get(i));
			res.add(sequence.get(index));
			reprCopy.set(index, null);
		}
		return res;
	}

	@java.lang.Override
	protected boolean isSame(org.apache.commons.math.genetics.Chromosome another) {
		if (!(another instanceof org.apache.commons.math.genetics.RandomKey<?>))
			return false;
		
		org.apache.commons.math.genetics.RandomKey<?> anotherRk = ((org.apache.commons.math.genetics.RandomKey<?>)(another));
		if ((getLength()) != (anotherRk.getLength()))
			return false;
		
		java.util.List<java.lang.Integer> thisPerm = this.baseSeqPermutation;
		java.util.List<java.lang.Integer> anotherPerm = anotherRk.baseSeqPermutation;
		for (int i = 0 ; i < (getLength()) ; i++) {
			if ((thisPerm.get(i)) != (anotherPerm.get(i)))
				return false;
			
		}
		return true;
	}

	@java.lang.Override
	protected void checkValidity(java.util.List<java.lang.Double> chromosomeRepresentation) throws org.apache.commons.math.genetics.InvalidRepresentationException {
		for (double val : chromosomeRepresentation) {
			if ((val < 0) || (val > 1)) {
				throw new org.apache.commons.math.genetics.InvalidRepresentationException("Values of representation must be in [0,1] interval");
			} 
		}
	}

	public static final java.util.List<java.lang.Double> randomPermutation(int l) {
		java.util.List<java.lang.Double> repr = new java.util.ArrayList<java.lang.Double>(l);
		for (int i = 0 ; i < l ; i++) {
			repr.add(org.apache.commons.math.genetics.GeneticAlgorithm.getRandomGenerator().nextDouble());
		}
		return repr;
	}

	public static final java.util.List<java.lang.Double> identityPermutation(int l) {
		java.util.List<java.lang.Double> repr = new java.util.ArrayList<java.lang.Double>(l);
		for (int i = 0 ; i < l ; i++) {
			repr.add((((double)(i)) / l));
		}
		return repr;
	}

	public static <S>java.util.List<java.lang.Double> comparatorPermutation(java.util.List<S> data, java.util.Comparator<S> comparator) {
		java.util.List<S> sortedData = new java.util.ArrayList<S>(data);
		java.util.Collections.sort(sortedData, comparator);
		return org.apache.commons.math.genetics.RandomKey.inducedPermutation(data, sortedData);
	}

	public static <S>java.util.List<java.lang.Double> inducedPermutation(java.util.List<S> originalData, java.util.List<S> permutedData) throws java.lang.IllegalArgumentException {
		if ((originalData.size()) != (permutedData.size())) {
			throw new java.lang.IllegalArgumentException("originalData and permutedData must have same length");
		} 
		int l = originalData.size();
		java.util.List<S> origDataCopy = new java.util.ArrayList<S>(originalData);
		java.lang.Double[] res = new java.lang.Double[l];
		for (int i = 0 ; i < l ; i++) {
			int index = origDataCopy.indexOf(permutedData.get(i));
			if (index == (-1)) {
				throw new java.lang.IllegalArgumentException("originalData and permutedData must contain the same objects.");
			} 
			res[index] = ((double)(i)) / l;
			origDataCopy.set(index, null);
		}
		return java.util.Arrays.asList(res);
	}

	@java.lang.Override
	public java.lang.String toString() {
		return java.lang.String.format("(f=%s pi=(%s))", getFitness(), baseSeqPermutation);
	}

	private static java.util.List<java.lang.Integer> baseSequence(int l) {
		java.util.List<java.lang.Integer> baseSequence = new java.util.ArrayList<java.lang.Integer>(l);
		for (int i = 0 ; i < l ; i++) {
			baseSequence.add(i);
		}
		return baseSequence;
	}
}

