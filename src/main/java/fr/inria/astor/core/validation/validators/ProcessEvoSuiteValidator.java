package fr.inria.astor.core.validation.validators;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.JUnitExecutorProcessWait;
import fr.inria.astor.util.EvoSuiteFacade;
import spoon.reflect.declaration.CtClass;

/**
 * 
 * @author matias
 *
 */
public class ProcessEvoSuiteValidator extends ProgramValidator {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	protected List<CtClass> evoTestClasses = new ArrayList<>();

	public ProcessEvoSuiteValidator() {
		evoTestClasses = new ArrayList<>();
	}

	public ProcessEvoSuiteValidator(List<CtClass> evoTestClasses) {
		this.evoTestClasses = evoTestClasses;
	}

	
	public List<CtClass> getEvoTestClasses() {
		return evoTestClasses;
	}

	public void setEvoTestClasses(List<CtClass> evoTestClasses) {
		this.evoTestClasses = evoTestClasses;
	}
	/**
	 * Process-based validation Advantage: stability, memory consumption, CG
	 * activity Disadvantage: time.
	 * 
	 * @param currentVariant
	 * @return
	 */
	@Override
	public ProgramVariantValidationResult validate(ProgramVariant currentVariant, ProjectRepairFacade projectFacade) {

		try {
			ProcessValidator validator = new ProcessValidator();
			ProgramVariantValidationResult resultOriginal = validator.validate(currentVariant, projectFacade);
			if (resultOriginal == null || !resultOriginal.wasSuccessful()) {
				// It's not a solution, we discard this.
				return resultOriginal;
			}
			log.info("Running Evosuite for variant "+ currentVariant.currentMutatorIdentifier());
			
			EvoSuiteFacade fev = new EvoSuiteFacade();
			
			if (evoTestClasses.isEmpty()) {
				log.info("Evosuite classes list is empty: Generating evoTest");
				// generate evosuite clases
			
				List<CtClass> evoCtclasses = fev.createEvoTestModel(projectFacade,  
						currentVariant);
				this.setEvoTestClasses(evoCtclasses);
			}
			else{
				log.info("Evosuite classes list is already built: "+this.evoTestClasses); 
			}
			
			ProgramVariantValidationResult resultEvoExecution = fev.saveAndExecuteEvoSuite(projectFacade, currentVariant, 
					evoTestClasses);
			
			log.info("Evo Result "+ resultEvoExecution.toString());

			EvoSuiteValidationResult evoResult = new EvoSuiteValidationResult(resultOriginal.getTestResult());
			evoResult.setEvoValidation(resultEvoExecution);
			
			return evoResult;

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



	public ProgramVariantValidationResult executeRegressionTesting(URL[] processClasspath,
			List<String> testCasesRegression) {
		log.debug("Executing EvosuiteTest :"+ testCasesRegression );
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
