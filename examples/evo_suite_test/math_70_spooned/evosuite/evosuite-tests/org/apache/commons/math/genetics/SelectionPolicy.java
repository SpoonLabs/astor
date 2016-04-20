package org.apache.commons.math.genetics;


public interface SelectionPolicy {
	org.apache.commons.math.genetics.ChromosomePair select(org.apache.commons.math.genetics.Population population);
}

