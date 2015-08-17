package fr.inria.astor.core.validation.validators;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.FactoryImpl;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * Validates a program instance using different criterials i.e. test unit
 * results.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProgramVariantValidator {

	private Logger logger = Logger.getLogger(Thread.currentThread().getName());
	
	
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
			List<String> failingCases)
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
		
		//validation.setResultSuccess(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}

	

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
			List<String> TestCases)
			throws FileNotFoundException, ClassNotFoundException,
			InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidationResult validation = validateVariantFirstPhases(TestCases);

		// Then, Regression test
		if (!validation.wasSuccessful()) {
			executeRegressionOneByOne(muTestEx, validation);
		}
		//validation.setResultSuccess(!validation.isResultSuccess());
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
		
		List<String> regressionCases = retrieveRegressionTestCases();
		
		in1 = System.currentTimeMillis();
		logger.info("Starting Regression");
		int sizeReg = 0;
		for (String failingcaseReg : regressionCases) {
			sizeReg++;
		
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

	
	public List<String> retrieveRegressionTestCases() {
		String casesTest = ConfigurationProperties.properties.getProperty("testcasesregression");
		String[] cases = casesTest.split(";");
		return 	Arrays.asList(cases);
		
	
	}
	
	/**
	 * Feed the list of test cases according to the definition POM/build.xml
	 * @return 
	 */
	@Deprecated
	public List<String> retrieveRegressionTestCasesOLD() {
			List<String> regressionCases = new ArrayList<String>();
			List<String> ignoreTestcases = retriveIgnoreTestCases();
			regressionCases = new ArrayList<String>();
			for (CtSimpleType<?> type : MutationSupporter.getFactory().Type()
					.getAll()) {
				String name = type.getQualifiedName();
				
				if ((name.endsWith("Test") || name.endsWith("TestBinary") || name
						.endsWith("TestPermutations"))
						&& (!type.getModifiers().contains(ModifierKind.ABSTRACT))
						&& !(type instanceof CtInterface) 
					//	&& isValidConstructor(type)
						&& !(isIgnoredTestCase(name, ignoreTestcases)))
				{
					regressionCases.add(type.getQualifiedName());
				}

			}
	
			return regressionCases;
	}
	
	@Deprecated
	private boolean isValidConstructor(CtSimpleType<?> type) {
		if(type instanceof CtClass<?>) {
			return ((CtClass<?>)type).getConstructor() != null ||
			((CtClass<?>)type).getConstructor(type.getFactory().Class().createReference(String.class)) != null;
		}
		return false;
	}

	private List<String> retriveIgnoreTestCases() {
		String list = ConfigurationProperties.getProperty("ignoredTestCases");
		String[] cases = list.split(";");
		return 	Arrays.asList(cases);
	}

	private boolean isIgnoredTestCase(String nameTestCase, List<String> ignoredList ){
		
		for (String ignoreTC : ignoredList) {
			
			if(nameTestCase.startsWith(ignoreTC)){
				return true;
			};
		}
		return false;
	}
	
}
