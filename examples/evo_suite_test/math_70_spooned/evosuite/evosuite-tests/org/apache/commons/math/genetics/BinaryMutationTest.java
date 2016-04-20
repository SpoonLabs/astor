package org.apache.commons.math.genetics;


public class BinaryMutationTest {
	@org.junit.Test
	public void testMutate() {
		org.apache.commons.math.genetics.BinaryMutation mutation = new org.apache.commons.math.genetics.BinaryMutation();
		for (int i = 0 ; i < 20 ; i++) {
			org.apache.commons.math.genetics.DummyBinaryChromosome original = new org.apache.commons.math.genetics.DummyBinaryChromosome(org.apache.commons.math.genetics.BinaryChromosome.randomBinaryRepresentation(10));
			org.apache.commons.math.genetics.DummyBinaryChromosome mutated = ((org.apache.commons.math.genetics.DummyBinaryChromosome)(mutation.mutate(original)));
			int numDifferent = 0;
			for (int j = 0 ; j < (original.getRepresentation().size()) ; j++) {
				if ((original.getRepresentation().get(j)) != (mutated.getRepresentation().get(j))) {
					numDifferent++;
				} 
			}
			org.junit.Assert.assertEquals(1, numDifferent);
		}
	}
}

