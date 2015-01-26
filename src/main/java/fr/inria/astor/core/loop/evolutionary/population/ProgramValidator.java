package fr.inria.astor.core.loop.evolutionary.population;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;

import fr.inria.astor.core.entities.ProgramVariantValidation;
import fr.inria.astor.core.validation.junit.JUnitTestExecutor;

/**
 * Validates a program instance using different criterials i.e. test unit
 * results. 
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProgramValidator {

	private Logger logger = Logger.getLogger(Thread.currentThread().getName());
	/**
	 * First execute the ``originals`` failing test case, then carries out a
	 * regression testing
	 * 
	 * @param supporter
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws InitializationError
	 */
	public ProgramVariantValidation validateVariantTwoPhases(List<String> failingCases, String testSuiteClassName/*
																												 * MutationSupporter
																												 * supporter
																												 */)
			throws FileNotFoundException, ClassNotFoundException, InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidation validation = new ProgramVariantValidation();

		boolean failing = false;
		long in1 = System.currentTimeMillis();
		// First, failing test cases
		if (failingCases != null) {
			//Stats.getCurrentStats().numberOfFailingTestCaseExecution++;
			for (String failingcase : failingCases) {
				Result result = muTestEx.execute(failingcase);
				if (!result.wasSuccessful()) {
					failing = true;
				}
				validation.getFailingResults().add(result);
			}

		}
		long fin1 = System.currentTimeMillis();
		//We remove the stats, it is in the previous validation
		//Stats.getCurrentStats().time1Validation.add(fin1 - in1);

		// Then, Regression test
		if (!failing) {
			in1 = System.currentTimeMillis();
			logger.info("Executing Regression");
			Result result = muTestEx.execute(testSuiteClassName);
			validation.setRegressionResult(result);
			failing = result.wasSuccessful();
			//Stats.getCurrentStats().numberOfRegressionTestExecution++;
			fin1 = System.currentTimeMillis();
		//	Stats.getCurrentStats().time2Validation.add(fin1 - in1);
		}

		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

	/**
	 * Return if a mutation is valid or not. The classes to test as well as the
	 * test MUST be in the loader in the current class loader.
	 * 
	 * /*supporter.getProperties().getTestSuiteClassName()
	 * 
	 * @param supporter
	 * @return
	 * @throws FileNotFoundException
	 * @throws ClassNotFoundException
	 * @throws InitializationError
	 */


	public ProgramVariantValidation validateVariantFirstPhases(List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException, InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidation validation = new ProgramVariantValidation();

		boolean failing = false;
		long in1 = System.currentTimeMillis();
		// First, failing test cases
		if (failingCases != null) {
			//Stats.getCurrentStats().numberOfFailingTestCaseExecution++;
			for (String failingcase : failingCases) {
				Result result = muTestEx.execute(failingcase);
				if (!result.wasSuccessful()) {
					failing = true;
				}
				validation.getFailingResults().add(result);
			}

		}
		long fin1 = System.currentTimeMillis();
		//Stats.getCurrentStats().time1Validation.add(fin1 - in1);

		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

	public ProgramVariantValidation validateVariantRegressionPhases(List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException, InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidation validation = new ProgramVariantValidation();

		boolean failing = false;
		long fin1;
		long in1;

		in1 = System.currentTimeMillis();
		logger.info("Executing Regression");
		Result result = muTestEx.execute(testSuiteClassName);
		validation.setRegressionResult(result);
		failing = result.wasSuccessful();
		//Stats.getCurrentStats().numberOfRegressionTestExecution++;
		fin1 = System.currentTimeMillis();
		//Stats.getCurrentStats().time2Validation.add(fin1 - in1);

		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

}
