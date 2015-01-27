package fr.inria.astor.core.validation;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;

import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.FactoryImpl;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
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
	public ProgramVariantValidationResult validateVariantTwoPhasesOld(
			List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException,
			InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidationResult validation = new ProgramVariantValidationResult();

		boolean failing = false;
		long in1 = System.currentTimeMillis();
		// First, failing test cases
		if (failingCases != null) {
			// Stats.getCurrentStats().numberOfFailingTestCaseExecution++;
			for (String failingcase : failingCases) {
				Result result = muTestEx.runTest(failingcase);
				if (!result.wasSuccessful()) {
					failing = true;
				}
				//validation.getTestResults().add(result);
				validation.addResultTest(result);
			}

		}
		long fin1 = System.currentTimeMillis();
		// We remove the stats, it is in the previous validation
		// Stats.getCurrentStats().time1Validation.add(fin1 - in1);

		// Then, Regression test
		if (!failing) {
			in1 = System.currentTimeMillis();
			logger.info("Executing Regression");
			Result result = muTestEx.runTest(testSuiteClassName);
			validation.addResultRegressionTest(result);
			failing = result.wasSuccessful();
			// Stats.getCurrentStats().numberOfRegressionTestExecution++;
			fin1 = System.currentTimeMillis();
			// Stats.getCurrentStats().time2Validation.add(fin1 - in1);
		}

		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

	public ProgramVariantValidationResult validateVariantSecondPhaseSingle(
			List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException,
			InitializationError {

		// LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidationResult validation = new ProgramVariantValidationResult();

	//	long fin1 = System.currentTimeMillis();

		// in1 = System.currentTimeMillis();
		logger.info("Executing Regression");
		Result result = muTestEx.runTest(testSuiteClassName);
		validation.addResultRegressionTest(result);
		validation.setRegressionExecuted(result.wasSuccessful());
		// failing = result.wasSuccessful();
		// Stats.getCurrentStats().numberOfRegressionTestExecution++;
		
		// LogManager.getRootLogger().setLevel(Level.INFO);
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

	public ProgramVariantValidationResult validateVariantFirstPhases(
			List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException,
			InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidationResult validation = new ProgramVariantValidationResult();

		boolean failing = false;
		long in1 = System.currentTimeMillis();
		// First, failing test cases
		if (failingCases != null) {
			// Stats.getCurrentStats().numberOfFailingTestCaseExecution++;
			for (String failingcase : failingCases) {
				Result result = muTestEx.runTest(failingcase);
				if (!result.wasSuccessful()) {
					failing = true;
				}
				validation.addResultTest(result);
			}

		}
		
		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

	List<String> regressionCases = null;

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
	public ProgramVariantValidationResult validateVariantTwoPhases(
			List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException,
			InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidationResult validation = validateVariantFirstPhases(failingCases, testSuiteClassName);

		// Then, Regression test
		if (!validation.isResult()) {
			executeRegressionOneByOne(muTestEx, validation);
			muTestEx.logger.setLevel(Level.INFO);
		}
		validation.setResult(!validation.isResult());
		LogManager.getRootLogger().setLevel(Level.INFO);

		return validation;
	}

	/**
	 * new
	 */
	@Deprecated
	public ProgramVariantValidationResult validateVariantRegressionPhases(
			String testSuiteClassName) throws FileNotFoundException,
			ClassNotFoundException, InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidationResult validation = new ProgramVariantValidationResult();

		boolean failing = false;
	
			logger.info("Executing Regression");
		Result result = muTestEx.runTest(testSuiteClassName);
		validation.addResultRegressionTest(result);
		failing = result.wasSuccessful();
		// Stats.getCurrentStats().numberOfRegressionTestExecution++;
		
		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

	protected boolean executeRegressionOneByOne(JUnitTestExecutor muTestEx,
			ProgramVariantValidationResult validation)
			throws FileNotFoundException, ClassNotFoundException,
			InitializationError {
		boolean failing = false;
		long in1;
		long fin1;
		
		retrieveRegressionTestCases();
		
		in1 = System.currentTimeMillis();
		logger.info("Starting Regression");
		muTestEx.logger.setLevel(Level.ERROR);
		int sizeReg = 0;
		for (String failingcaseReg : regressionCases) {
			sizeReg++;
			if (failingcaseReg
					.equals("org.apache.commons.math.ode.nonstiff.GillStepInterpolatorTest")
					|| failingcaseReg
							.equals("org.apache.commons.math.ode.nonstiff.GillIntegratorTest")
					|| failingcaseReg
							.equals("org.apache.commons.math.ode.nonstiff.EulerStepInterpolatorTest")
					|| failingcaseReg
							.equals("org.apache.commons.math.ode.nonstiff.EulerIntegratorTest")
					|| failingcaseReg
							.startsWith("org.apache.commons.math.ode.nonstiff")) {
				continue;
			}

			Result result = muTestEx.runTest(failingcaseReg);
						
			if (!result.wasSuccessful()) {
				failing = true;
			}
			logger.info("Executing Regression " + (sizeReg) + "/"
					+ regressionCases.size() + " " + failingcaseReg + " r:"
					+ result.wasSuccessful());

			//validation.getTestResults().add(result);
			validation.addResultRegressionTest(result);
		}
		// Stats.getCurrentStats().numberOfRegressionTestExecution++;
		fin1 = System.currentTimeMillis();
		// Stats.getCurrentStats().time2Validation.add(fin1 - in1);
		logger.info("End Regression (t=" + (fin1 - in1) + ") " + (sizeReg)
				+ "/" + regressionCases.size() + ", result " + (!failing));
		return failing;
	}

	/**
	 * Feed the list of test cases according to the definition POM/build.xml
	 */
	private void retrieveRegressionTestCases() {
		if (regressionCases == null) {
			regressionCases = new ArrayList<String>();
			for (CtSimpleType<?> type : FactoryImpl.getLauchingFactory().Type()
					.getAll()) {
				String name = type.getQualifiedName();
				if ((name.endsWith("Test") || name.endsWith("TestBinary") || name
						.endsWith("TestPermutations"))
						&& (!name.endsWith("AbstractTest"))
						&& !(type instanceof CtInterface)) {
					regressionCases.add(type.getQualifiedName());
				}

			}
		}
	}

}
