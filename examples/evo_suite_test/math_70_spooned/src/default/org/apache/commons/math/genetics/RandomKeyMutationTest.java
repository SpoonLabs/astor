package org.apache.commons.math.genetics;


public class RandomKeyMutationTest {
	@org.junit.Test
	public void testMutate() {
		org.apache.commons.math.genetics.MutationPolicy mutation = new org.apache.commons.math.genetics.RandomKeyMutation();
		int l = 10;
		for (int i = 0 ; i < 20 ; i++) {
			org.apache.commons.math.genetics.DummyRandomKey origRk = new org.apache.commons.math.genetics.DummyRandomKey(org.apache.commons.math.genetics.RandomKey.randomPermutation(l));
			org.apache.commons.math.genetics.Chromosome mutated = mutation.mutate(origRk);
			org.apache.commons.math.genetics.DummyRandomKey mutatedRk = ((org.apache.commons.math.genetics.DummyRandomKey)(mutated));
			int changes = 0;
			for (int j = 0 ; j < (origRk.getLength()) ; j++) {
				if ((origRk.getRepresentation().get(j)) != (mutatedRk.getRepresentation().get(j))) {
					changes++;
				} 
			}
			org.junit.Assert.assertEquals(1, changes);
		}
	}
}

