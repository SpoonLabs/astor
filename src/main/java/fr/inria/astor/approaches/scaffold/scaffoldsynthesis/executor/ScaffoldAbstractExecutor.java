package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor;

public abstract class ScaffoldAbstractExecutor {

	public abstract boolean incrementCounter();

	public abstract int choose(int _max);

	public abstract void backtrack();
	
	public abstract void initialize(int id);

	public abstract int getCounterPointer();

}

