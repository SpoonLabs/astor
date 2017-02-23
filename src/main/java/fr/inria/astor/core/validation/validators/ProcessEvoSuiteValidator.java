package fr.inria.astor.core.validation.validators;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.JUnitExecutorProcess;
import fr.inria.astor.core.validation.executors.JUnitIndirectExecutorProcess;
import fr.inria.astor.util.Converters;
import fr.inria.astor.util.EvoSuiteFacade;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

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

	//protected List<String> generatedTestCache = new ArrayList<String>();
	protected List<String> testAlreadyGenerated = new ArrayList<String>();
	//
	
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
	public TestCaseVariantValidationResult validate(ProgramVariant currentVariant, ProjectRepairFacade projectFacade) {

		try {
			ProcessValidator validator = new ProcessValidator();
			boolean executeAlloriginalValidation = false;
			TestCaseVariantValidationResult resultOriginal = validator.validate(currentVariant, projectFacade,
					executeAlloriginalValidation);
			if (resultOriginal == null || !resultOriginal.isSuccessful()) {
				// It's not a solution, we discard this.
				return resultOriginal;
			}
			boolean runESoverOriginalBuggyClass = ConfigurationProperties.getPropertyBool("evoRunOnBuggyClass");
			
			TestCaseVariantValidationResult resultEvoExecution = runTestFromEvoSuite(currentVariant, projectFacade, runESoverOriginalBuggyClass);
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
	public TestCaseVariantValidationResult runTestFromEvoSuite(ProgramVariant currentVariant,
			ProjectRepairFacade projectFacade, boolean runOverOriginal) throws Exception {
		log.info("Running Evosuite for variant " + currentVariant.currentMutatorIdentifier());

		log.debug("Run ES over "+((runOverOriginal)?"default":"patched"));
		
		String sufix = (runOverOriginal)?"default":("var"+currentVariant.getId()); 
		
		EvoSuiteFacade fev = new EvoSuiteFacade();
		String testEScodepath = projectFacade
				.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder")+File.separator+sufix);
		File esPath = new File(testEScodepath);
		log.info("Evo output: " + esPath);
		
		
		//Set up dirs
		String classpathForCompile = "";
		classpathForCompile = projectFacade.getProperties().getDependenciesString() + File.pathSeparator
				+ projectFacade.getOutDirWithPrefix(currentVariant.currentMutatorIdentifier()) + File.pathSeparator//
				+ new File(ConfigurationProperties.getProperty("evosuitejar")).getAbsolutePath() + File.pathSeparator
				+ projectFacade.getOutDirWithPrefix(currentVariant.DEFAULT_ORIGINAL_VARIANT);

		
		String outPutTest = projectFacade
				.getOutDirWithPrefix("/evosuite/evosuite-tests/"+sufix);
	
	
		List<String> classesToGenerateTests = new ArrayList<String>();
		List<String> testToExecute = new ArrayList<String>();

		//Classed affected by the patch
		List<CtType<?>> typesToProcess =fev.getClassesToProcess(currentVariant);

		//
		//See which we have to execute, if this runOverPatch, all are included
		for (CtType<?> ctType : typesToProcess) {
			String stype = ctType.getQualifiedName() ;
			String testName = stype + EvoSuiteFacade.EVOSUITE_SUFFIX;
			if(!runOverOriginal  || !testAlreadyGenerated.contains(testName) ){
				classesToGenerateTests.add(stype);	
			}
			//all test affected must be executed
			testToExecute.add(testName);
		}
		
		log.debug("Classes Affected: "+classesToGenerateTests);
		
		

		if ( classesToGenerateTests.size() > 0) {
		
			List<String> pathTestGenerated = new ArrayList<String>();

			log.debug("Generating test for the first time");
			boolean executed = fev.runEvosuite(currentVariant, classesToGenerateTests, projectFacade,testEScodepath, runOverOriginal);

			// we collect the files generated

			Collection<File> files = FileUtils.listFiles(esPath, new RegexFileFilter("^(.*?)"),
					DirectoryFileFilter.DIRECTORY);
			for (File file : files) {
				pathTestGenerated.add(file.getAbsolutePath());
			}
			
			List<String> testGenerated = new ArrayList<>();
			
			//Collect test generated from files generated by ES
			for (String f : pathTestGenerated) {
				String qualifiedTestName = f.replace(".java", "").replace(esPath.toString(), "").replace("/evosuite-tests/", "")
						.replace(File.separator, ".");
				if (!qualifiedTestName.endsWith(EvoSuiteFacade.EVOSUITE_scaffolding_SUFFIX) 
						&& classesToGenerateTests.contains(qualifiedTestName.replace(EvoSuiteFacade.EVOSUITE_SUFFIX, ""))) {
					testGenerated.add(qualifiedTestName);
				}
			}
			//check if number of test cases generated is the same that the number of test case we wanted to generate
			if(classesToGenerateTests.size() != testGenerated.size()){
				log.error("ES did not generate all test cases that I should do, test generated "+testGenerated+", classes under generation "+classesToGenerateTests);
			}
			log.debug("Generated tests to run: " + testGenerated);
			
			testAlreadyGenerated.addAll(testGenerated);
			
			//Now, Compilation of generated test cases.
		
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
			for (String testPath : pathTestGenerated) {
				command.add(testPath);
			}
			//Run compilation: 
			fev.runProcess(command.toArray(new String[command.size()]));
	
		}else{
			log.debug("Any test to generate, all test cases were generated before: "+testAlreadyGenerated);
		}

		String classpathForRunTest = classpathForCompile + (File.pathSeparator) + outPutTest;
		log.info("Process classpath " + classpathForRunTest);

		ProcessEvoSuiteValidator evoProcess = new ProcessEvoSuiteValidator();
		TestCaseVariantValidationResult evoResult = evoProcess.executeRegressionTesting(
				Converters.toURLArray(classpathForRunTest.split(File.pathSeparator)), testToExecute);

		return evoResult;

	}

	public TestCasesProgramValidationResult executeRegressionTesting(URL[] processClasspath,
			List<String> testCasesRegression) {
		boolean avoidInterrupt = true;
		return executeRegressionTesting(processClasspath,testCasesRegression, avoidInterrupt);
	}

	public TestCasesProgramValidationResult executeRegressionTesting(URL[] processClasspath,
			List<String> testCasesRegression, boolean avoidInterrupt) {
		log.debug("Executing EvosuiteTest :" + testCasesRegression);
		long t1 = System.currentTimeMillis();

		JUnitExecutorProcess process = new JUnitIndirectExecutorProcess(avoidInterrupt);

		int time = 60000;
		String jvmPath = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
		TestResult trregression = process.execute(jvmPath, processClasspath, testCasesRegression, time);

		long t2 = System.currentTimeMillis();
		currentStats.time2Validation.add((t2 - t1));

		if (trregression == null) {
			currentStats.unfinishValidation++;
			boolean error = true;
			return new TestCasesProgramValidationResult(error);
		
		} else {
			log.debug(trregression);
			currentStats.numberOfTestcasesExecutedval2 += trregression.casesExecuted;
			currentStats.numberOfRegressionTestCases = trregression.casesExecuted;
			return new TestCasesProgramValidationResult(trregression, trregression.wasSuccessful(),
					(trregression != null));

		}
	}
	
	
}
