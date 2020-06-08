package fr.inria.astor.core.validation.junit;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.validation.TestCaseVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.core.validation.ProgramVariantValidator;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.astor.core.validation.results.TestResult;
import fr.inria.astor.util.Converters;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JUnitProcessValidator extends ProgramVariantValidator {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	/**
	 * Process-based validation Advantage: stability, memory consumption, CG
	 * activity Disadvantage: time.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	@Override
	public TestCaseVariantValidationResult validate(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade) {

		return this.validate(mutatedVariant, projectFacade,
				Boolean.valueOf(ConfigurationProperties.getProperty("forceExecuteRegression")));

	}

	/**
	 * Run the validation of the program variant in two steps: one the original
	 * failing test, the second the complete test suite (only in case the failing
	 * now passes)
	 * 
	 * @param mutatedVariant
	 * @param projectFacade
	 * @param forceExecuteRegression
	 * @return
	 */
	public TestCaseVariantValidationResult validate(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade,
			boolean forceExecuteRegression) {

		try {
			URL[] bc = createClassPath(mutatedVariant, projectFacade);

			LaucherJUnitProcess testProcessRunner = new LaucherJUnitProcess();

			log.debug("-Running first validation");

			long t1 = System.currentTimeMillis();
			String jvmPath = ConfigurationProperties.getProperty("jvm4testexecution");

			TestResult trfailing = testProcessRunner.execute(jvmPath, bc,
					projectFacade.getProperties().getFailingTestCases(),
					ConfigurationProperties.getPropertyInt("tmax1"));
			long t2 = System.currentTimeMillis();

			if (trfailing == null) {
				log.debug("**The validation 1 have not finished well**");
				return null;
			}

			log.debug(trfailing);
			if (trfailing.wasSuccessful() || forceExecuteRegression) {
				return runRegression(mutatedVariant, projectFacade, bc);
			} else {
				TestCaseVariantValidationResult r = new TestCasesProgramValidationResult(trfailing,
						trfailing.wasSuccessful(), false);
				return r;
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected TestCaseVariantValidationResult runRegression(ProgramVariant mutatedVariant,
			ProjectRepairFacade projectFacade, URL[] bc) {

		LaucherJUnitProcess testProcessRunner = new LaucherJUnitProcess();

		if (ConfigurationProperties.getPropertyBool("testbystep"))
			return executeRegressionTestingOneByOne(mutatedVariant, bc, testProcessRunner, projectFacade);
		else
			return executeRegressionTesting(mutatedVariant, bc, testProcessRunner, projectFacade);

	}

	protected URL[] createClassPath(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade)
			throws MalformedURLException {

		List<URL> originalURL = createOriginalURLs(projectFacade);
		URL[] bc;
		if (mutatedVariant.getCompilation() != null) {

			File variantOutputFile = defineLocationOfCompiledCode(mutatedVariant, projectFacade);

			bc = Converters.redefineURL(variantOutputFile, originalURL.toArray(new URL[0]));
		} else {
			bc = originalURL.toArray(new URL[0]);
		}

		boolean isGZoltarDependencyFound = false;

		for (int i = 0; i < bc.length && !isGZoltarDependencyFound; i++) {
			if (bc[i].getFile().contains("gzoltar-0.1.1")) {
				isGZoltarDependencyFound = true;
			}
		}

		if (!isGZoltarDependencyFound) {

			File libsfolder = new File("." + File.separator + "lib");

			URL[] newBc = new URL[bc.length + 1];
			newBc[0] = new URL("file://" + libsfolder.getAbsolutePath() + File.separator
					+ "com.gzoltar-0.1.1-jar-with-dependencies.jar");

			for (int i = 0; i < bc.length; i++) {
				newBc[i + 1] = bc[i];
			}

			return newBc;
		}

		return bc;
	}

	public List<URL> createOriginalURLs(ProjectRepairFacade projectFacade) throws MalformedURLException {
		URL[] defaultSUTClasspath = projectFacade
				.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		List<URL> originalURL = new ArrayList<>(Arrays.asList(defaultSUTClasspath));

		String classpath = System.getProperty("java.class.path");

		for (String path : classpath.split(File.pathSeparator)) {

			File f = new File(path);
			// originalURL.add(new URL("file://\"" + f.getAbsolutePath() + "\""));
			originalURL.add(new URL("file://" + f.getAbsolutePath()));

		}

		return originalURL;
	}

	protected File defineLocationOfCompiledCode(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade) {
		String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
		File variantOutputFile = new File(bytecodeOutput);

		MutationSupporter.currentSupporter.getOutput().saveByteCode(mutatedVariant.getCompilation(), variantOutputFile);
		return variantOutputFile;
	}

	protected TestCaseVariantValidationResult executeRegressionTesting(ProgramVariant mutatedVariant, URL[] bc,
			LaucherJUnitProcess p, ProjectRepairFacade projectFacade) {
		log.debug("-Test Failing is passing, Executing regression");

		List<String> testCasesRegression = projectFacade.getProperties().getRegressionTestCases();

		String jvmPath = ConfigurationProperties.getProperty("jvm4testexecution");

		TestResult trregression = p.execute(jvmPath, bc, testCasesRegression,
				ConfigurationProperties.getPropertyInt("tmax2"));

		if (testCasesRegression == null || testCasesRegression.isEmpty()) {
			log.error("Any test case for regression testing");
			return null;
		}

		if (trregression == null) {
			currentStats.increment(GeneralStatEnum.NR_FAILING_VALIDATION_PROCESS);
			return null;
		} else {
			log.debug(trregression);
			return new TestCasesProgramValidationResult(trregression, trregression.wasSuccessful(),
					(trregression != null));
		}
	}

	protected TestCaseVariantValidationResult executeRegressionTestingOneByOne(ProgramVariant mutatedVariant, URL[] bc,
			LaucherJUnitProcess p, ProjectRepairFacade projectFacade) {

		log.debug("-Test Failing is passing, Executing regression, One by one");
		TestResult trregressionall = new TestResult();
		long t1 = System.currentTimeMillis();

		for (String tc : projectFacade.getProperties().getRegressionTestCases()) {

			List<String> parcial = new ArrayList<String>();
			parcial.add(tc);
			String jvmPath = ConfigurationProperties.getProperty("jvm4testexecution");

			TestResult singleTestResult = p.execute(jvmPath, bc, parcial,
					ConfigurationProperties.getPropertyInt("tmax2"));
			if (singleTestResult == null) {
				log.debug("The validation 2 have not finished well");
				return null;
			} else {
				trregressionall.getFailures().addAll(singleTestResult.getFailures());
				trregressionall.getSuccessTest().addAll(singleTestResult.getSuccessTest());
				trregressionall.failures += singleTestResult.failures;
				trregressionall.casesExecuted += singleTestResult.getCasesExecuted();

			}
		}
		long t2 = System.currentTimeMillis();
		log.debug(trregressionall);
		return new TestCasesProgramValidationResult(trregressionall, true, trregressionall.wasSuccessful());

	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {
		List<String> testCasesToRun = FinderTestCases.findJUnit4XTestCasesForRegression(projectFacade);

		return testCasesToRun;
	}

}
