package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate.ConditionSymmetryCandidate;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate.ExpressionValueCandidate;

public class ScaffoldConditionGenerator extends ScaffoldGenerator {

	private ConditionSymmetryCandidate predicate;
	
	public void initScaffold() {
		predicate=new ConditionSymmetryCandidate();
	}

	public Boolean next(ExpressionValueCandidate[] vals) {
		
		return predicate.next(vals);
	}

	@Override
	public void reset() {
		predicate.reset();
	}

	public String toString() {
		return predicate.toString();
	}
}
