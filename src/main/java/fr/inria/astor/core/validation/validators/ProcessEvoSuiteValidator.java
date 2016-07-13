package fr.inria.astor.core.validation.validators;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.JUnitExecutorProcess;
import fr.inria.astor.core.validation.executors.JUnitIndirectExecutorProcess;
import fr.inria.astor.util.Converters;
import fr.inria.astor.util.EvoSuiteFacade;
import spoon.reflect.declaration.CtClass;

/**
 * 
 ** Process-based program variant validation, used for executing EvoSuite test
 * cases
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class ProcessEvoSuiteValidator extends ProgramValidator {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	protected List<CtClass> evoTestClasses = new ArrayList<>();

	//TODO: improve this cache.
	protected List<String> generatedTest = new ArrayList<String>();

	public ProcessEvoSuiteValidator() {
		evoTestClasses = new ArrayList<>();
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
			boolean executeAlloriginalValidation = true;
			ProgramVariantValidationResult resultOriginal = validator.validate(currentVariant, projectFacade,
					executeAlloriginalValidation);
			if (resultOriginal == null || !resultOriginal.wasSuccessful()) {
				// It's not a solution, we discard this.
				return resultOriginal;
			}
			ProgramVariantValidationResult resultEvoExecution = runTestFromEvoSuite(currentVariant, projectFacade);
			log.info("Evo Result " + resultEvoExecution.toString());

			EvoSuiteValidationResult evoResult = new EvoSuiteValidationResult();
			evoResult.setManualTestValidation(resultOriginal);
			evoResult.setEvoValidation(resultEvoExecution);
			return evoResult;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}



	/** Generates and runs evosuite test cases **/
	public ProgramVariantValidationResult runTestFromEvoSuite(ProgramVariant currentVariant,
			ProjectRepairFacade projectFacade) throws Exception {
		log.info("Running Evosuite for variant " + currentVariant.currentMutatorIdentifier());

		EvoSuiteFacade fev = new EvoSuiteFacade();
		String testEScodepath = projectFacade
				.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		File esPath = new File(testEScodepath);
		log.info("Evo output: " + esPath);
		
		
		//Set up dirs
		String classpathForCompile = "";
		classpathForCompile = projectFacade.getProperties().getDependenciesString() + File.pathSeparator
				+ projectFacade.getOutDirWithPrefix(currentVariant.currentMutatorIdentifier()) + File.pathSeparator//
				+ new File("./lib/evosuite-1.0.3.jar").getAbsolutePath() + File.pathSeparator
				+ projectFacade.getOutDirWithPrefix(currentVariant.DEFAULT_ORIGINAL_VARIANT);

		String outPutTest = projectFacade
				.getOutDirWithPrefix("/evosuite/evosuite-tests/");
	

		if (generatedTest.isEmpty()) {
			log.debug("Generating test for the first time");
			boolean executed = fev.runEvosuite(currentVariant, projectFacade);

			// we collect the files generated

			Collection<File> files = FileUtils.listFiles(esPath, new RegexFileFilter("^(.*?)"),
					DirectoryFileFilter.DIRECTORY);
			for (File file : files) {
				generatedTest.add(file.getAbsolutePath());
			}
			//Now, Compilation
		
			// WE COMPILE EVO TEST
			log.info("Classpath " + classpathForCompile);

			String javaPath = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
			List<String> command = new ArrayList<String>();
			command.add(javaPath + File.separator + "javac");
			command.add("-classpath");
			command.add(classpathForCompile);
			command.add("-d");

			//// Save compiled
			File fout = new File(outPutTest);
			fout.mkdirs();
			command.add(outPutTest);

			// Adding the files
			for (String testPath : generatedTest) {
				command.add(testPath);
			}

			fev.runProcess(command.toArray(new String[command.size()]));
			///
			
			
		}else{
			log.debug("Tests were already generated "+generatedTest);
		}

		
		List<String> changed = currentVariant.computeAffectedClassesByOperatos().stream().
				map(e -> e.getQualifiedName()).collect(Collectors.toList());

		log.debug("Generated tests: " + generatedTest);
		log.debug("Classes Changed: "+changed);
		
		List<String> testToExecute = new ArrayList<>();
		for (String f : generatedTest) {
			String qualifiedTestName = f.replace(".java", "").replace(esPath.toString(), "").replace("/evosuite-tests/", "")
					.replace(File.separator, ".");
			if (!qualifiedTestName.endsWith(EvoSuiteFacade.EVOSUITE_scaffolding_SUFFIX) 
					&& changed.contains(qualifiedTestName.replace(EvoSuiteFacade.EVOSUITE_SUFFIX, ""))) {
				testToExecute.add(qualifiedTestName);
			}
		}

		String classpathForRunTest = classpathForCompile + (File.pathSeparator) + outPutTest;
		log.info("Process classpath " + classpathForRunTest);

		ProcessEvoSuiteValidator evoProcess = new ProcessEvoSuiteValidator();
		ProgramVariantValidationResult evoResult = evoProcess.executeRegressionTesting(
				Converters.toURLArray(classpathForRunTest.split(File.pathSeparator)), testToExecute);

		return evoResult;

	}


	public TestCasesProgramValidationResult executeRegressionTesting(URL[] processClasspath,
			List<String> testCasesRegression) {
		log.debug("Executing EvosuiteTest :" + testCasesRegression);
		long t1 = System.currentTimeMillis();

		boolean avoidInterrupt = true;
		JUnitExecutorProcess process = new JUnitIndirectExecutorProcess(avoidInterrupt);

		int time = 60000;
		String jvmPath = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
		TestResult trregression = process.execute(jvmPath, processClasspath, testCasesRegression, time);

		long t2 = System.currentTimeMillis();
		currentStats.time2Validation.add((t2 - t1));

		if (trregression == null) {
			currentStats.unfinishValidation++;
			return null;
		} else {
			log.debug(trregression);
			currentStats.numberOfTestcasesExecutedval2 += trregression.casesExecuted;
			currentStats.numberOfRegressionTestCases = trregression.casesExecuted;
			return new TestCasesProgramValidationResult(trregression, trregression.wasSuccessful(),
					(trregression != null));

		}
	}

	
	
}
