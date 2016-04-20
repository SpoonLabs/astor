package org.apache.commons.math.genetics;


public class BinaryChromosomeTest {
	@org.junit.Test
	public void testInvalidConstructor() {
		java.lang.Integer[][] reprs = new java.lang.Integer[][]{ new java.lang.Integer[]{ 0 , 1 , 0 , 1 , 2 } , new java.lang.Integer[]{ 0 , 1 , 0 , 1 , -1 } };
		for (java.lang.Integer[] repr : reprs) {
			try {
				new org.apache.commons.math.genetics.DummyBinaryChromosome(repr);
				org.junit.Assert.fail("Exception not caught");
			} catch (java.lang.IllegalArgumentException e) {
			}
		}
	}

	@org.junit.Test
	public void testRandomConstructor() {
		for (int i = 0 ; i < 20 ; i++) {
			new org.apache.commons.math.genetics.DummyBinaryChromosome(org.apache.commons.math.genetics.BinaryChromosome.randomBinaryRepresentation(10));
		}
	}

	@org.junit.Test
	public void testIsSame() {
		org.apache.commons.math.genetics.Chromosome c1 = new org.apache.commons.math.genetics.DummyBinaryChromosome(new java.lang.Integer[]{ 0 , 1 , 0 , 1 , 0 , 1 });
		org.apache.commons.math.genetics.Chromosome c2 = new org.apache.commons.math.genetics.DummyBinaryChromosome(new java.lang.Integer[]{ 0 , 1 , 1 , 0 , 1 });
		org.apache.commons.math.genetics.Chromosome c3 = new org.apache.commons.math.genetics.DummyBinaryChromosome(new java.lang.Integer[]{ 0 , 1 , 0 , 1 , 0 , 1 , 1 });
		org.apache.commons.math.genetics.Chromosome c4 = new org.apache.commons.math.genetics.DummyBinaryChromosome(new java.lang.Integer[]{ 1 , 1 , 0 , 1 , 0 , 1 });
		org.apache.commons.math.genetics.Chromosome c5 = new org.apache.commons.math.genetics.DummyBinaryChromosome(new java.lang.Integer[]{ 0 , 1 , 0 , 1 , 0 , 0 });
		org.apache.commons.math.genetics.Chromosome c6 = new org.apache.commons.math.genetics.DummyBinaryChromosome(new java.lang.Integer[]{ 0 , 1 , 0 , 1 , 0 , 1 });
		org.junit.Assert.assertFalse(c1.isSame(c2));
		org.junit.Assert.assertFalse(c1.isSame(c3));
		org.junit.Assert.assertFalse(c1.isSame(c4));
		org.junit.Assert.assertFalse(c1.isSame(c5));
		org.junit.Assert.assertTrue(c1.isSame(c6));
	}
}

