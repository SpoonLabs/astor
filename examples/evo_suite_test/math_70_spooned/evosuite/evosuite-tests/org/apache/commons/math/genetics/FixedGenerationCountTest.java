package org.apache.commons.math.genetics;


public class FixedGenerationCountTest {
	@org.junit.Test
	public void testIsSatisfied() {
		org.apache.commons.math.genetics.FixedGenerationCount fgc = new org.apache.commons.math.genetics.FixedGenerationCount(20);
		int cnt = 0;
		org.apache.commons.math.genetics.Population pop = new org.apache.commons.math.genetics.Population() {
			public void addChromosome(org.apache.commons.math.genetics.Chromosome chromosome) {
			}

			public org.apache.commons.math.genetics.Chromosome getFittestChromosome() {
				return null;
			}

			public int getPopulationLimit() {
				return 0;
			}

			public int getPopulationSize() {
				return 0;
			}

			public org.apache.commons.math.genetics.Population nextGeneration() {
				return null;
			}

			public java.util.Iterator<org.apache.commons.math.genetics.Chromosome> iterator() {
				return null;
			}
		};
		while (!(fgc.isSatisfied(pop)))
			cnt++;
		org.junit.Assert.assertEquals(20, cnt);
	}
}

