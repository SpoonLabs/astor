package org.apache.commons.math.genetics;


public interface CrossoverPolicy {
	org.apache.commons.math.genetics.ChromosomePair crossover(org.apache.commons.math.genetics.Chromosome first, org.apache.commons.math.genetics.Chromosome second);
}

