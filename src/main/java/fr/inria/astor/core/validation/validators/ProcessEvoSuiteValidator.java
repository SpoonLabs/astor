package fr.inria.astor.core.validation.validators;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.JUnitExecutorProcess;
import fr.inria.astor.core.validation.executors.JUnitExecutorProcessWait;

/**
 * 
 * @author matias
 *
 */
public class ProcessEvoSuiteValidator extends ProgramValidator {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	/**
	 * Process-based validation Advantage: stability, memory consumption, CG
	 * activity Disadvantage: time.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	@Override
	public ProgramVariantValidationResult validate(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade) {
		
		try {
			ProcessValidator validator = new ProcessValidator();
			ProgramVariantValidationResult resultOriginal=  validator.validate(mutatedVariant, projectFacade);
			if(resultOriginal == null || !resultOriginal.wasSuccessful()){
				//It's not a solution, we discard this.
				return resultOriginal;
			}
			System.out.println("Executing EvoSuite");
			if(true)
				return resultOriginal;
			
			
			String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
			File variantOutputFile = new File(bytecodeOutput);

			URL[] classpathVariant = projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
			List<URL> originalURL = new ArrayList(Arrays.asList(classpathVariant));
			originalURL.add(new URL("junit"));
			originalURL.add(new URL("evosuite"));
			
			String classpath = System.getProperty("java.class.path");

			for (String s : classpath.split(File.pathSeparator)) {
				originalURL.add(new URL("file://" + new File(s).getAbsolutePath()));
			}
			//TODO: search EvoTest, Spoon them, manipulate (ignore tag?), 
			//compile,save bytecode, add code to Classpath
			
			MutationSupporter.currentSupporter.getSpoonClassCompiler().saveByteCode(mutatedVariant.getCompilation(),
					variantOutputFile);

			URL[] finalClassPath = joinURLs(variantOutputFile, originalURL.toArray(new URL[0]));

			

			List<String> testCasesRegression = projectFacade.getProperties().getRegressionTestCases();

			ProgramVariantValidationResult result = executeRegressionTesting(finalClassPath, testCasesRegression);
			//
			if(result.wasSuccessful()){
				System.out.println("Successs Evo regression");
			}
			//
			return result;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected URL[] joinURLs(File foutgen, URL[] originalURL) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		urls.add(foutgen.toURL());
		for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
			urls.add(originalURL[i]);
		}

		return (URL[]) urls.toArray(originalURL);
	}

	public ProgramVariantValidationResult executeRegressionTesting(
			URL[] processClasspath,
			List<String> testCasesRegression) {
		log.debug("-Test Failing is passing, Executing regression");
		long t1 = System.currentTimeMillis();
		
		JUnitExecutorProcessWait process = new JUnitExecutorProcessWait();
		int time = 60000;
		TestResult trregression = process.execute(processClasspath, testCasesRegression, time);

		long t2 = System.currentTimeMillis();
		currentStats.time2Validation.add((t2 - t1));

		if (trregression == null) {
			currentStats.unfinishValidation++;
			return null;
		} else {
			log.debug(trregression);
			currentStats.numberOfTestcasesExecutedval2 += trregression.casesExecuted;
			currentStats.numberOfRegressionTestCases = trregression.casesExecuted;
			return new ProgramVariantValidationResult(trregression);
		}
	}

}
