package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate.ExpressionValueCandidate;

public abstract class ScaffoldGenerator {
	
	protected ExpressionValueCandidate[] candidates;

	public abstract Object next(ExpressionValueCandidate[] candidates);

	public abstract void reset();
	
	public abstract void initScaffold();

	public void setInitVals(ExpressionValueCandidate[] candidates) {
		this.candidates = candidates;
	}
}
