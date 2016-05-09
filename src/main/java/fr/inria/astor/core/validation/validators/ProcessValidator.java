package fr.inria.astor.core.validation.validators;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.JUnitExecutorProcess;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ProcessValidator extends ProgramValidator {

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
			String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
			File variantOutputFile = new File(bytecodeOutput);
			
			URL[] defaultSUTClasspath = projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
			List<URL> originalURL = new ArrayList(Arrays.asList(defaultSUTClasspath));

			String classpath = System.getProperty("java.class.path");
			
			for (String path:  classpath.split(File.pathSeparator)) {
				
					File f = new File(path);
				//	if(f.getName().toLowerCase().startsWith("junit")){
					originalURL.add(new URL("file://"+ f.getAbsolutePath()));
				//}
			}
			
			URL[] bc = null;
			if (mutatedVariant.getCompilation() != null) {
				MutationSupporter.currentSupporter.getSpoonClassCompiler().saveByteCode(mutatedVariant.getCompilation(),
						variantOutputFile);

				bc = redefineURL(variantOutputFile, originalURL.toArray(new URL[0]));
			} else {
				bc = originalURL.toArray(new URL[0]);
			}
			
			JUnitExecutorProcess p = new JUnitExecutorProcess();
		
			log.debug("-Running first validation");

			currentStats.numberOfFailingTestCaseExecution++;

			long t1 = System.currentTimeMillis();
			TestResult trfailing = p.execute(bc, projectFacade.getProperties().getFailingTestCases(),
					ConfigurationProperties.getPropertyInt("tmax1"));
			long t2 = System.currentTimeMillis();
			currentStats.time1Validation.add((t2 - t1));

			currentStats.passFailingval1++;
			if (trfailing == null) {
				log.debug("**The validation 1 have not finished well**");
				return null;
			} else {
				// If it is successful, execute regression
				currentStats.numberOfTestcasesExecutedval1 += trfailing.casesExecuted;
				currentStats.numberOfFailingTestCase = trfailing.casesExecuted;
				log.debug(trfailing);
				if (trfailing.wasSuccessful()) {
					currentStats.numberOfRegressionTestExecution++;
					currentStats.passFailingval2++;
					if (ConfigurationProperties.getPropertyBool("testbystep"))
						return executeRegressionTestingOneByOne(mutatedVariant, bc, p, projectFacade);
					else
						return executeRegressionTesting(mutatedVariant, bc, p,  projectFacade);

				} else {
					ProgramVariantValidationResult r = new ProgramVariantValidationResult(trfailing, false, false);
					return r;
				}

			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected URL[] redefineURL(File foutgen, URL[] originalURL) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		urls.add(foutgen.toURL());
		for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
			urls.add(originalURL[i]);
		}

		return (URL[]) urls.toArray(originalURL);
	}

	protected ProgramVariantValidationResult executeRegressionTesting(ProgramVariant mutatedVariant, URL[] bc,
			JUnitExecutorProcess p,  ProjectRepairFacade projectFacade) {
		log.debug("-Test Failing is passing, Executing regression");
		long t1 = System.currentTimeMillis();
		List<String> testCasesRegression = projectFacade.getProperties().getRegressionTestCases();

		TestResult trregression = p.execute(bc, testCasesRegression, ConfigurationProperties.getPropertyInt("tmax2"));

		if(testCasesRegression == null || testCasesRegression.isEmpty()){
			log.error("Any test case for regression testing");
			return null;
		}
		
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

	protected ProgramVariantValidationResult executeRegressionTestingOneByOne(ProgramVariant mutatedVariant, URL[] bc,
			JUnitExecutorProcess p,  ProjectRepairFacade projectFacade) {
		
		log.debug("-Test Failing is passing, Executing regression");
		TestResult trregressionall = new TestResult();
		long t1 = System.currentTimeMillis();

		for (String tc : projectFacade.getProperties().getRegressionTestCases()) {

			List<String> parcial = new ArrayList<String>();
			parcial.add(tc);
			TestResult trregression = p.execute(bc, parcial, ConfigurationProperties.getPropertyInt("tmax2"));
			if (trregression == null) {
				log.debug("The validation 2 have not finished well");
				return null;
			} else {
				trregressionall.getFailures().addAll(trregression.getFailures());
				trregressionall.getSuccessTest().addAll(trregression.getSuccessTest());
				trregressionall.failures += trregression.getFailures().size();
				trregressionall.casesExecuted += trregression.getFailures().size()
						+ trregression.getSuccessTest().size();
			}
		}
		long t2 = System.currentTimeMillis();
		currentStats.time2Validation.add((t2 - t1));
		currentStats.numberOfTestcasesExecutedval2 += trregressionall.casesExecuted;
		currentStats.numberOfRegressionTestCases = trregressionall.casesExecuted;
		log.debug(trregressionall);
		return new ProgramVariantValidationResult(trregressionall, true, trregressionall.wasSuccessful());

	}

}
