package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.ScaffoldExecutor;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate.ExpressionValueCandidate;

public class ScaffoldExpressionGenerator extends ScaffoldGenerator {
	private int index = -1;
	private String toString = "";

	public Object next(ExpressionValueCandidate[] vals) {
		if (index == -1) {
			index = ScaffoldExecutor.choose(vals.length-1);
			toString = vals[index]==null? "null": vals[index].getName()+"\n";
		}
		return vals[index].getValue();
	}

	public void reset() {
		index = -1;
	}

	public String toString() {
		return toString;
	}

	@Override
	public void initScaffold() {
		// TODO Auto-generated method stub
	}
}
