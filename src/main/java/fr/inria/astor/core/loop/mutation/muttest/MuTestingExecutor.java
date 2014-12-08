package fr.inria.astor.core.loop.mutation.muttest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.runner.Result;

import fr.inria.astor.core.loop.evolutionary.JUnitRunner;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidatorProcess;
import fr.inria.astor.core.validation.TestResult;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MuTestingExecutor {
	
	static int RUN_MUTANTS = 1;
	static int TIMEOUT = 180000;

	public MuTestingResult run(String prefixDir, String[] testClasses, String classpath, String spooned_class_folder,
			String original) throws InterruptedException, ExecutionException, MalformedURLException {

		MuTestingResult result = new MuTestingResult();

		int max_mutant_analyzed = 0;

	
		TestResult testResulorig  = this.runTestCasesP(testClasses, classpath, spooned_class_folder, original, null);
		System.out.println("Test Results: " + testResulorig.casesExecuted  + " f " + testResulorig.getFailureCount() + "  "+testResulorig.failTest);
					
		int failingPre = testResulorig.getFailureCount(); 
		

		System.out.println("Failing test cases before mutation " + failingPre);

		File dirSpooned = new File(spooned_class_folder);

		for (File mutFile : dirSpooned.listFiles()) {
			if (max_mutant_analyzed == RUN_MUTANTS)
				break;
		
				
			max_mutant_analyzed++;
			System.out.println("*****Analyzing mutant " + max_mutant_analyzed + " / " + dirSpooned.listFiles().length);
			System.out.println("Mutant location "+mutFile);
			if (!mutFile.getName().startsWith(prefixDir)) {
				long init = System.currentTimeMillis();
				
				TestResult testResult = this.runTestCasesP(testClasses, classpath, spooned_class_folder, original,
						mutFile.getName());
				
				if (testResult == null) {
					result.notFinishTestCases.add(mutFile.getName());
					System.err.println(mutFile.getName()+" "+"Test Interrumted");
					continue;
				}
				System.out.println(mutFile.getName()+" "+"Test Results: " + testResult.casesExecuted + " f " + testResult.getFailureCount()+ " "+testResult.getFailures());
				
				int failingPost_i = testResult.getFailureCount();

				result.failingTestCases.put(mutFile.getName(), testResult.getFailures());

				if (failingPre == failingPost_i) {
					System.out.println("r " + mutFile.getName() + " alive");
					result.alivedTestCases.add(mutFile.getName());
				} else {
					System.out.println("r " + mutFile.getName() + " killed");
					result.killedTestCases.add(mutFile.getName());
				}
				//max_mutant_analyzed++;
				System.out.println("Execution test time: " + (System.currentTimeMillis() - init) / 1000);

			}

		}
		System.out.println("Alive " + result.alivedTestCases.size() + " , Killed " + result.killedTestCases.size());
		return result;
	}
	
	PrintStream pnull = new PrintStream(new OutputStream() {
		
		@Override
		public void write(int b) throws IOException {
			
		}
	});
		
	
	public Callable<Result> createCallable(String[] testClasses, String classpath, String spooned_class_folder,
			String original, String mutant) throws InterruptedException, ExecutionException, MalformedURLException {

		URL originalBinFolder = null;
		URL mutantFolder = null;
		try {
			originalBinFolder = new File(original).toURI().toURL();

			if (mutant != null)
				mutantFolder = new File(spooned_class_folder + File.separator + mutant).toURI().toURL();

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		URLClassLoader cltc = new URLClassLoader(getURLArray(classpath, originalBinFolder, mutantFolder));
		return new JUnitRunnerMulti(testClasses, cltc, mutant);

	}

	public Result runTestCases(String[] testClasses, String classpath, String spooned_class_folder, String original,
			String mutant) throws InterruptedException, ExecutionException, MalformedURLException {

		URL originalBinFolder = null;
		URL mutantFolder = null;
		try {
			originalBinFolder = new File(original).toURI().toURL();

			if (mutant != null)
				mutantFolder = new File(spooned_class_folder + File.separator + mutant).toURI().toURL();

		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}

		URLClassLoader cltc = new URLClassLoader(getURLArray(classpath, originalBinFolder, mutantFolder));
		ExecutorService executor = Executors.newSingleThreadExecutor(new ProvidedClassLoaderThreadFactory(cltc));
		
		Result r = null;
		try {
			r = executor.submit(new JUnitRunner(testClasses)).get(1, TimeUnit.MINUTES);
			System.out.println("Test Results: " + r.getRunCount() + " f " + r.getFailureCount() + " ig "
					+ r.getIgnoreCount() + r.getFailures());
		} catch (TimeoutException e) {
			e.printStackTrace();
		}

		executor.shutdown();
		return r;

	}
	
	
	public TestResult runTestCasesP(String[] testClasses, String classpath, String spooned_class_folder, String original,
			String mutant) throws InterruptedException, ExecutionException, MalformedURLException {

		classpath =	(original) + File.pathSeparator + classpath; 
		
		if(mutant != null)
			classpath =	(spooned_class_folder + File.separator + mutant) + File.pathSeparator + classpath; 
		
		ProgramValidatorProcess vp = new ProgramValidatorProcess(null);
		return vp.execute(classpath, Arrays.asList(testClasses), TIMEOUT);
		 
	
	}

	public String[] findTestCases(URL urlBinFolder) {
		String[] testClasses = new TestClassesFinder().findIn(new URL[] { urlBinFolder }, false);

		return testClasses;
	}

	public static URL[] getURLArray(String classpath, URL binfolder, URL mutantFolder) throws MalformedURLException {
		String[] s = classpath.split(File.pathSeparator);
		int fold = 1;
		if (mutantFolder != null)
			fold = 2;

		URL[] arr = new URL[s.length + fold];
		// --
		if (mutantFolder != null) {
			arr[0] = mutantFolder;
			arr[1] = binfolder;
		} else
			arr[0] = binfolder;

		int i = fold;
		for (String si : s) {
			arr[i] = new File(si).toURL();
			i++;
		}
		// --
		return arr;
	}

	public void compareResults(MuTestingResult before, MuTestingResult after) {

		System.out.println("Killed b " + before.killedTestCases.size() + ", a " + after.killedTestCases.size());
		System.out.println("Alive b " + before.alivedTestCases.size() + ", a " + after.alivedTestCases.size());
		System.out.println("Interrupted b " + before.notFinishTestCases.size() + ", a " + after.notFinishTestCases.size());

		List<String> killedBoth = new ArrayList<>();
		List<String> killedNotExec = new ArrayList<>();
		List<String> killedAlive = new ArrayList<>();
		List<String> killedNoInfo = new ArrayList<>();
		
	
		for (String killedBefore : before.killedTestCases) {

			if (after.killedTestCases.contains(killedBefore))
				killedBoth.add(killedBefore);
			else if (!after.alivedTestCases.contains(killedBefore))
				killedNotExec.add(killedBefore);
			else
				if(after.alivedTestCases.contains(killedBefore))
					killedAlive.add(killedBefore);
					else
						killedNoInfo.add(killedBefore);
						

		}
		System.out.println("both ("+killedBoth.size()+") " + killedBoth );
		System.out.println("killed not exec ("+killedNotExec.size()+") "  + killedNotExec); 
		System.out.println("killed alive ("+killedAlive.size()+") " + killedAlive);
		System.out.println("killed not info ("+killedNoInfo.size()+") " + killedNotExec);

	}

}
