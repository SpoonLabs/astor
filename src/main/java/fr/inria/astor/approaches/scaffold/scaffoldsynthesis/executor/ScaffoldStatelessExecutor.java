package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.statelessexplore.Explorer;

public class ScaffoldStatelessExecutor extends ScaffoldAbstractExecutor {

	public ScaffoldStatelessExecutor() {
		Explorer.initialize();
	}

	@Override
	public boolean incrementCounter() {
		return Explorer.incrementCounter();
	}

	@Override
	public int choose(int _max) {
		return Explorer.choose(_max);
	}

	@Override
	public void backtrack() {
		Explorer.backtrack();
	}

	@Override
	public void initialize(int id) {
		Explorer.initialize();
		
	}

	public int getCounterPointer() {
		return Explorer.getCounterPointer();
	}
}
