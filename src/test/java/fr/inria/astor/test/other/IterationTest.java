package fr.inria.astor.test.other;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.XMLStat;
import fr.inria.astor.test.repair.evaluation.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.ifcondition.GenProgExpressionTest;
import fr.inria.astor.test.repair.evaluation.ifcondition.MutationEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.ifcondition.ParEvolutionaryTest;
/**
 * This class executes the experiment from our paper if-conditional-dataset-2014.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class IterationTest {

	@Test
	public void iterate() throws Exception {
		BaseEvolutionaryTest method = new MutationEvolutionaryTest();
	
		for (String[] exp : BugFixDataSet.data) {
	
			singleIssue(method,exp);
		}
		
	}

	public void iterate(String out, String fixid,  String failing, String regression, double th, String dependenciespath, int numberExecutions,
			
			BaseEvolutionaryTest test) throws Exception {
		
		List<Stats> statsRet = new ArrayList<Stats>();
		String dirExecutor = out + File.separator +fixid + File.separator;
		File dirExfile = new File(dirExecutor);
		if(dirExfile.exists()){
			FileUtils.deleteDirectory(dirExfile);
		}
		dirExfile.mkdirs();
		test.log.info("--Bug ID "+fixid);
	
		for (int i = 1; i <= numberExecutions; i++) {
			System.gc();
			Stats currentStat = new Stats();
			currentStat.id = i;
					
			File resultDestination = new File(dirExecutor + i + File.separator);
			test.createCustomFileLogger(resultDestination.getPath() + File.separator + "log.txt");
			test.log.info("--RUN "+i);
			long t1 = System.currentTimeMillis();	
			String location = ConfigurationProperties.getProperty("version-location");
			String packageToInstrument = ConfigurationProperties.getProperty("packageToInstrument");
			test.generic(location, fixid, regression,failing,dependenciespath,packageToInstrument, th);
			
			long t2 = System.currentTimeMillis();
			test.log.info("time executing " + numberExecutions + " runs: " + (t2 - t1) + " ms");
			//currentStat.timeIteraction = ((t2-t1));
			statsRet.add(currentStat);
			//statsRet.add(Stats.getCurrentStats());
			//test.factory = null;
			//TODO: I comment after refactor MAIN-IF-X
			//String srcOutput = test.rep.getProperties().getInDir();// //.getInDirWithPrefix(mutSupporter.currentMutatorIdentifier());
			FileFilter fl = new FileFilter() {
				
				@Override
				public boolean accept(File pathname) {
					if(pathname.isDirectory() ){
						boolean isdefault = pathname.getName().equals("default");
						return (!isdefault);
					}
					return true;
				}
			};
			//FileUtils.copyDirectory(new File(srcOutput), resultDestination,fl);
		
		}
		//test.log.info("--StatResult->" + statsRet.size());
		for (Stats stats : statsRet) {
			//test.log.info("---");
			stats.printStats();
		}
		
	
		XMLStat.saveResults(statsRet,dirExfile.getAbsolutePath()+File.separator+ "statistics_result.xml");
		
	}
	
	@Test
	public void singleIssue() throws NumberFormatException, Exception {
	
		BaseEvolutionaryTest methodMut = new MutationEvolutionaryTest();
		singleIssue(methodMut);
		
		BaseEvolutionaryTest methodPar = new ParEvolutionaryTest();
		//singleIssue(methodPar);
		
		BaseEvolutionaryTest methodGenProg = new GenProgExpressionTest();
		//singleIssue(methodGenProg);
		
		
	}
	
	@Test
	public void singleMutation() throws NumberFormatException, Exception {
	
		BaseEvolutionaryTest methodMut = new MutationEvolutionaryTest();
		singleIssue(methodMut);
		
	}
	@Test
	public void singlePar() throws NumberFormatException, Exception {
	
		BaseEvolutionaryTest methodPar = new ParEvolutionaryTest();
		singleIssue(methodPar);
	
	}
	@Test
	public void singleGP() throws NumberFormatException, Exception {
	
		BaseEvolutionaryTest methodGenProg = new GenProgExpressionTest();
		singleIssue(methodGenProg);
		
		
	}
	
	@Test
	public void singleIssueThread() throws NumberFormatException, Exception {
		long init = System.currentTimeMillis();
		BaseEvolutionaryTest methodMut = new MutationEvolutionaryTest();
		Worker w1 = new Worker(this, methodMut);
		//singleIssue(methodMut);
		w1.start();
		
		BaseEvolutionaryTest methodPar = new ParEvolutionaryTest();
		//singleIssue(methodPar);
		Worker w2 = new Worker(this, methodPar);
		w2.start();
		
		BaseEvolutionaryTest methodGenProg = new GenProgExpressionTest();
		//singleIssue(methodGenProg);
		Worker w3 = new Worker(this, methodGenProg);
		w3.start();
		synchronized (w1) {

			if(!w1.exit)
			{w1.wait();}	
		}
		synchronized (w2) {

			if(!w2.exit)
			{w2.wait();}	
		}
		synchronized (w3) {

			if(!w3.exit)
			{w3.wait();}	
		}
		long end = System.currentTimeMillis();
		System.out.println("time "+(end - init));
		
	}

	public void singleIssue(BaseEvolutionaryTest method) throws NumberFormatException, Exception{
		String id = ConfigurationProperties.properties.getProperty("bugId"); 
		String[] exp = BugFixDataSet.getDataForId(id);
		singleIssue(method, exp);
	}
	
	public void singleIssue(BaseEvolutionaryTest method, String id) throws NumberFormatException, Exception{
		String[] exp = BugFixDataSet.getDataForId(id);
		singleIssue(method, exp);
	}
	
	
	public void singleIssue(BaseEvolutionaryTest method,String[] exp) throws NumberFormatException, Exception {
		int numberExecutions = new Integer(ConfigurationProperties.properties.getProperty("numberExecutions"));
		TransformationProperties.maxGeneration = new Integer(ConfigurationProperties.properties.getProperty("maxGeneration"));;
		TransformationProperties.populationSize = new Integer(ConfigurationProperties.properties.getProperty("populationSize"));;
		String out = ConfigurationProperties.properties.getProperty("outputResults") + method.getClass().getSimpleName() + File.separator;

		iterate(out, exp[0], exp[1], exp[2], 
				(exp.length == 4) ? Double.valueOf(exp[3]) : 0.5, (exp.length == 5) ?exp[4]:null,numberExecutions, method);
	}
	@Test
	public void sit2() throws InterruptedException, ExecutionException{
		
		BaseEvolutionaryTest methodMut = new MutationEvolutionaryTest();
		Worker w1 = new Worker(this, methodMut);
		//singleIssue(methodMut);
	//w1.start();
		
		BaseEvolutionaryTest methodPar = new ParEvolutionaryTest();
		//singleIssue(methodPar);
		Worker w2 = new Worker(this, methodPar);
	//	w2.start();
		
		BaseEvolutionaryTest methodGenProg = new GenProgExpressionTest();
		//singleIssue(methodGenProg);
		Worker w3 = new Worker(this, methodGenProg);
	
		ExecutorService service = Executors.newFixedThreadPool(20);
	
		service.execute(w1);
		  synchronized (this) {
		         wait(30000);	
			}
		service.execute(w2);
		  synchronized (this) {
		         wait(30000);	
			}
		service.execute(w3);
		
	         
	         //shut down the executor service so that this thread can exit
	         service.shutdownNow();
	         
	         service.awaitTermination(1,TimeUnit.DAYS);
	         System.out.println("Finished all threads");
	}
	//@Test
	public void sit3() throws InterruptedException, ExecutionException{
		
		BaseEvolutionaryTest methodMut = new MutationEvolutionaryTest();
		Worker w1 = new Worker(this, methodMut);
		//singleIssue(methodMut);
	//w1.start();
		
		BaseEvolutionaryTest methodPar = new ParEvolutionaryTest();
		//singleIssue(methodPar);
		Worker w2 = new Worker(this, methodPar);
	//	w2.start();
		
		BaseEvolutionaryTest methodGenProg = new GenProgExpressionTest();
		//singleIssue(methodGenProg);
		Worker w3 = new Worker(this, methodGenProg);
	
		ExecutorService service = Executors.newFixedThreadPool(3);
	      List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();
	      Future f1 = service.submit(w1);
	         futures.add(f1);
	         synchronized (this) {
		         wait(30000);	
			}
	         Future f2 = service.submit(w2);
	         futures.add(f2);
	         synchronized (this) {
		         wait(30000);	
			}
	         Future f3 = service.submit(w3);
	         futures.add(f3);
	      
	         //service.invokeAll();
	         for (Future<Runnable> f : futures)
	         {
	           Runnable r = f.get();
	           System.out.println(r);
	         }
	      
	         
	         //shut down the executor service so that this thread can exit
	         service.shutdownNow();
	}
	private static class Worker extends Thread {
		//private final Process process;
		public boolean exit = false;
		IterationTest test; 
		BaseEvolutionaryTest method;
		
		private Worker(IterationTest test, BaseEvolutionaryTest method) {
			this.test = test;
			this.method = method;
		}

		public void run() {
			try {
				test.singleIssue(method);
				exit = true;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (this) {
				this.notify();	
			}
		}
	}

	

}
