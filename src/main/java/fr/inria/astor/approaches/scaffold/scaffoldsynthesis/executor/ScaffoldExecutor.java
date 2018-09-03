package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor;

public class ScaffoldExecutor {

	private static ScaffoldAbstractExecutor exec = new ScaffoldStatelessExecutor();
	
	@SuppressWarnings("deprecation")
	public static void setType(ExecutorType type) {
		
	}

	public static boolean incrementCounter() {
		return exec.incrementCounter();
	}

	public static int choose(int _max) {
		return exec.choose(_max);
	}

	public static void backtrack() {
		exec.backtrack();
	}

	public static void initialize(int id) {
		exec.initialize(id);
	}

	public static int getCounterPointer() {
		return exec.getCounterPointer();
	}

}
