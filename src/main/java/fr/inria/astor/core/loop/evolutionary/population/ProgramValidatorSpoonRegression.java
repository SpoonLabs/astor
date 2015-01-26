package fr.inria.astor.core.loop.evolutionary.population;

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
import fr.inria.astor.core.entities.ProgramVariantValidation;
import fr.inria.astor.core.validation.junit.JUnitTestExecutor;

/**
 * Validates a program instance using different criterials i.e. test unit
 * results. TODO: Assertion Slice validation in future!
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProgramValidatorSpoonRegression extends ProgramValidator {

	private Logger logger = Logger.getLogger(Thread.currentThread().getName());
	
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
	public ProgramVariantValidation validateVariantTwoPhases(List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException, InitializationError {

		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();

		ProgramVariantValidation validation = new ProgramVariantValidation();

		boolean failing = false;
		long in1 = System.currentTimeMillis();
		// First, failing test cases
		if (failingCases != null) {
			//Stats.getCurrentStats().numberOfFailingTestCaseExecution++;its counted in the previous
			for (String failingcase : failingCases) {
				Result result = muTestEx.execute(failingcase);
				if (!result.wasSuccessful()) {
					failing = true;
				}
				validation.getFailingResults().add(result);
			}

		}
		long fin1 = System.currentTimeMillis();
	//	Stats.getCurrentStats().time1Validation.add(fin1 - in1);
		logger.info("Result first validation: " + !failing);

		// Then, Regression test
		if (!failing) {
			failing = executeRegression(muTestEx, validation);
			muTestEx.logger.setLevel(Level.INFO);
		}
		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		
		return validation;
	}

	protected boolean executeRegression(JUnitTestExecutor muTestEx, ProgramVariantValidation validation)
			throws FileNotFoundException, ClassNotFoundException, InitializationError {
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
			if(failingcaseReg.equals("org.apache.commons.math.ode.nonstiff.GillStepInterpolatorTest")
					|| failingcaseReg.equals("org.apache.commons.math.ode.nonstiff.GillIntegratorTest")
					|| failingcaseReg.equals("org.apache.commons.math.ode.nonstiff.EulerStepInterpolatorTest")
					|| failingcaseReg.equals("org.apache.commons.math.ode.nonstiff.EulerIntegratorTest")
					|| failingcaseReg.startsWith("org.apache.commons.math.ode.nonstiff")
					){
				continue;
			}
			
			Result result = muTestEx.execute(failingcaseReg);
			if (!result.wasSuccessful()) {
				failing = true;
			}
			 logger.info("Executing Regression " + (sizeReg) + "/" +
			 regressionCases.size() +" "+ failingcaseReg+" r:"+result.wasSuccessful());
			
			validation.getFailingResults().add(result);
		}
	//	Stats.getCurrentStats().numberOfRegressionTestExecution++;
		fin1 = System.currentTimeMillis();
	//	Stats.getCurrentStats().time2Validation.add(fin1 - in1);
		logger.info("End Regression (t=" + (fin1 - in1) + ") " + (sizeReg) + "/" + regressionCases.size() + ", result "
				+ (!failing));
		return failing;
	}

	/**
	 * Feed the list of test cases according to the definition POM/build.xml
	 */
	public void retrieveRegressionTestCases() {
		if (regressionCases == null) {
			regressionCases = new ArrayList<String>();
			for (CtSimpleType<?> type : FactoryImpl.getLauchingFactory().Type().getAll()) {
				String name = type.getQualifiedName();
				if ((name.endsWith("Test") || name.endsWith("TestBinary") || name.endsWith("TestPermutations"))
						&& (!name.endsWith("AbstractTest")) && !(type instanceof CtInterface)) {
					regressionCases.add(type.getQualifiedName());
				}

			}
		}
	}

	public ProgramVariantValidation validateVariantRegressionPhases(List<String> failingCases, String testSuiteClassName)
			throws FileNotFoundException, ClassNotFoundException, InitializationError {
		//
		LogManager.getRootLogger().setLevel(Level.INFO);// OFF
		JUnitTestExecutor muTestEx = new JUnitTestExecutor();
		ProgramVariantValidation validation = new ProgramVariantValidation();
		boolean failing = false;
		failing = executeRegression(muTestEx, validation);
		muTestEx.logger.setLevel(Level.INFO);
		validation.setResult(!failing);
		LogManager.getRootLogger().setLevel(Level.INFO);
		return validation;
	}
}
