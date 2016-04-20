package org.apache.commons.math.genetics;


public interface MutationPolicy {
	org.apache.commons.math.genetics.Chromosome mutate(org.apache.commons.math.genetics.Chromosome original);
}

