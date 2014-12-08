package fr.inria.astor.core.loop.evolutionary.population;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.validation.TestResult;
import fr.inria.astor.core.validation.TestRunnerEngine;

/**
 * Process-based program variant validation
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProgramValidatorProcess {

	Stats currentStat;
	
	public ProgramValidatorProcess(Stats currentStat) {
		super();
		this.currentStat = currentStat;
	}

	private Logger log = Logger.getLogger(Thread.currentThread().getName());

	private void registerExecutionTime(long time,List<Long> times){
		if(times !=null)
			times.add(time);
	}
	
	protected String urlArrayToString(URL[] urls){
		String s= "";
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			s+=url.getPath()+File.pathSeparator;
		}
		return s;
	}
	
	public TestResult execute(URL[] path, List<String> classesToExecute, int waitTime) {
		 return execute(urlArrayToString(path), classesToExecute,waitTime);
	}
	
	public TestResult execute(String path, List<String> classesToExecute, int waitTime) {
		Process p = null;
		String javaPath = ConfigurationProperties.getProperty("jvm4testexecution");
	
		String systemcp = System.getProperty("java.class.path");

		path = systemcp + File.pathSeparator + path;
		
		List<String> cls = new ArrayList<>(classesToExecute);

		try {
		
			List<String> command = new ArrayList<String>();
			command.add(javaPath);
			command.add("-cp");
			command.add(path);
			command.add(TestRunnerEngine.class.getName());
			
			command.addAll(cls);

			ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]));
		
			//pb.directory(new File("/home/matmarti/develop/workspacePurification/shindig-gadgets-4mut") );
			pb.redirectOutput();
			pb.redirectErrorStream(true);
			long t_start = System.currentTimeMillis();
			p = pb.start();
			
			String cm2 = command.toString().replace("[", "").replace("]", "").replace(",", " ");
			System.out.println(cm2);
				
			Worker worker = new Worker(p);
			worker.start();
			worker.join(waitTime);
			long t_end = System.currentTimeMillis();
			//worker.interrupt();
			// ---
			int exitvalue = p.exitValue();
			TestResult tr = getTestResult(p);
			p.destroy();
			
			System.out.println("Execution time "+((t_end-t_start)/1000)+ " seconds");
			
			return tr;
		} catch (Exception ex) {
			System.out.println("The validation thread continues working " + ex.getMessage());
			if (p != null)
				p.destroy();
			return null;
		}
		
	}

	
	
	private void analyzeoutput(Process p) {
		try {

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				//log.info(line);
				System.out.println(line);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private TestResult getTestResult(Process p) {
		TestResult tr = new TestResult();
		boolean success = false;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				//log.info(line);
				System.out.println(line);
				if(line.startsWith(TestRunnerEngine.OUTSEP)){
					//System.out.println(line);
					String[] s = line.split(TestRunnerEngine.OUTSEP);
					int nrtc = Integer.valueOf(s[1]);
					tr.casesExecuted = nrtc;
					int failing = Integer.valueOf(s[2]);
					tr.failures = failing;
					if(!"".equals(s[3])){
					String[] falinglist = s[3].replace("[", "").replace("]", "").split(",");
					for (String string : falinglist) {
						if(!string.trim().isEmpty())
							tr.failTest.add(string.trim());
					}
					}
					success = true;
				}
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(success)
			return tr;
		else
			return null;
	}
	

	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

}
