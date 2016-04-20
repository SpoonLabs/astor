package org.apache.commons.math.genetics;


public class FixedGenerationCount implements org.apache.commons.math.genetics.StoppingCondition {
	private int numGenerations = 0;

	private final int maxGenerations;

	public FixedGenerationCount(int maxGenerations) {
		if (maxGenerations <= 0)
			throw new java.lang.IllegalArgumentException("The number of generations has to be >= 0");
		
		this.maxGenerations = maxGenerations;
	}

	public boolean isSatisfied(org.apache.commons.math.genetics.Population population) {
		if ((this.numGenerations) < (this.maxGenerations)) {
			(numGenerations)++;
			return false;
		} 
		return true;
	}

	public int getNumGenerations() {
		return numGenerations;
	}
}

