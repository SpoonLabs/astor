package fr.inria.astor.core.validation.executors;
/**
 * Thread that execute one external process
 * @author Matias Martinez
 *
 */
public class WorkerThreadHelper extends Thread {
	private final Process process;
	private Integer exit;

	public WorkerThreadHelper(Process process) {
		this.process = process;
	}

	public void run() {
		try {
			exit = process.waitFor();
		} catch (InterruptedException ignore) {
			return;
		}
	}
	public Integer getExit(){
		return this.exit;
	}
}

