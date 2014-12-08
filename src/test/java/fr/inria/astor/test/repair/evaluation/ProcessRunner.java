package fr.inria.astor.test.repair.evaluation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class ProcessRunner {

	public static void main(String[] args) throws NumberFormatException, Exception {
		IterationTest it = new IterationTest();
		String id = args[0];
		String m = args[1];
		if(m.equals("gp"))
		it.singleIssue(new GenProgLoopExpressionProjectTest(),id);
		else
			if(m.equals("par"))
				it.singleIssue(new ParEvolutionaryTest(),id);
			else
				it.singleIssue(new MutationEvolutionaryTest(),id);
			
	}
	int[] ids = new int[]{
			//LANG
			280
	};
	@Test
	public void test() throws Exception{
		
		for (int i : ids) {
			run(Integer.toString(i),"gp");
			run(Integer.toString(i),"par");
			run(Integer.toString(i),"mut");
		}
	}
	
	@Test
	public void testGR() throws Exception{
		
		for (int i : ids) {
			run(Integer.toString(i),"gp");
		}
	}
	
	@Test
	public void testPAR() throws Exception{
		
		for (int i : ids) {
			run(Integer.toString(i),"par");
		}
	}
	
	@Test
	public void testMut() throws Exception{
		
		for (int i : ids) {
			run(Integer.toString(i),"mut");
		}
	}
	
	public void run(String id,String method) throws Exception{
		
		String javaPath = "/home/matmarti/develop/jdk1.7.0_51/bin/java";
				
		String systemcp = System.getProperty("java.class.path");

		String path = systemcp;
	
		List<String> command = new ArrayList<String>();
		command.add(javaPath);
		command.add("-Xmx2000m");
		command.add("-cp");
		command.add(path);
		command.add( "fr.inria.astor.test.repair.genprog.fixing.ProcessRunner");
		command.add(id);
		command.add(method);
		//command.addAll(classesToExecute);

		ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]));
	
		//pb.redirectOutput(new File(processOutputDir + File.separator+versionKey+File.separator + "processout.txt"));
		
		pb.redirectErrorStream(true);
		//long t_start = System.currentTimeMillis();
		Process proc = pb.start();
		StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream());
		StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream());
		errorGobbler.start();
		outputGobbler.start();
		int wait = proc.waitFor();
		System.out.println("finish " + id + " "+method + " out "+wait);
	}
	
	class StreamGobbler extends Thread {
	    InputStream is;

	    // reads everything from is until empty. 
	    StreamGobbler(InputStream is) {
	        this.is = is;
	    }

	    public void run() {
	        try {
	            InputStreamReader isr = new InputStreamReader(is);
	            BufferedReader br = new BufferedReader(isr);
	            String line=null;
	            while ( (line = br.readLine()) != null)
	                System.out.println(line);    
	        } catch (IOException ioe) {
	            ioe.printStackTrace();  
	        }
	    }
	}
	
}
