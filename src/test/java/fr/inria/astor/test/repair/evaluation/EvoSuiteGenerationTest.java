package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.EvoSuiteValidationResult;
import fr.inria.astor.core.validation.validators.ProcessEvoSuiteValidator;
import fr.inria.astor.util.Converters;
import fr.inria.astor.util.EvoSuiteFacade;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvoSuiteGenerationTest extends BaseEvolutionaryTest {

	@Before
	public void setUp() throws Exception{
		super.setUp();
		// For running the test cases, We use the JVM that runs Astor
		String javahome = System.getProperty("java.home");
		File location = new File(javahome);
		javahome = location.getParent() + File.separator + "bin";
		// We set up the JVM that runs test
		ConfigurationProperties.properties.setProperty("jvm4testexecution", javahome);
		ConfigurationProperties.properties.setProperty("jvm4evosuitetestexecution", javahome);
	}
	
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testGenerateEvosuiteTestsStepByStep() throws Exception {
		AstorMain main1 = new AstorMain();

		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				// We force to not execute the evolution, so, we run it for zero
				// generation
				"-maxgen", "0", "-population", "1", "-stopfirst", "true", "-maxtime", "100"

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() == 0);

		assertEquals(1, main1.getEngine().getVariants().size());

		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		log.info("Executing evosuite");
		EvoSuiteFacade fev = new EvoSuiteFacade();
		String outES = main1.getEngine().getProjectFacade().getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		
		List<String> affectedTypes = variant.getAffectedClasses().stream().
				map(e -> e.getQualifiedName()).collect(Collectors.toList());
		
		boolean executed = fev.runEvosuite(variant, affectedTypes,main1.getEngine().getProjectFacade(), outES, true);//fev.runEvosuite(variant, main1.getEngine().getProjectFacade(), outES);
		assertTrue(executed);

		// CHECKING EVO OUTPUT
		String testEScodepath = main1.getEngine().getProjectFacade()
				.getInDirWithPrefix(ConfigurationProperties.getProperty("evosuiteresultfolder"));
		File esPath = new File(testEScodepath);
		assertTrue("Source code files from EvoSuite do not exist", esPath.exists());
		log.info("Evo output: " + esPath);

		log.info("Loading EvoTest model");
		// CREATING CLASSPATH FOR EXECUTING EVO TESTS
		String bytecodeSUTLocation = main1.getEngine().getProjectFacade()
				.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		String classpathForModelizeEvoSuite = "";

		classpathForModelizeEvoSuite += (new File(ConfigurationProperties.getProperty("evosuitejar"))
				.getAbsolutePath());

		classpathForModelizeEvoSuite += (File.pathSeparator
				+ main1.getEngine().getProjectFacade().getProperties().getDependenciesString());
		classpathForModelizeEvoSuite += (File.pathSeparator + bytecodeSUTLocation);

		String[] classpathForModelizeEvoSuiteTest = classpathForModelizeEvoSuite.split(File.pathSeparator);

		// We create the Spoon model for the evosuite test generated
		List<CtClass> classes = fev.reificateEvoSuiteTest(testEScodepath, classpathForModelizeEvoSuiteTest);
		// Two classes: EvoTest + EvoScaffolding
		//assertEquals("We do not have 2 spoon classes generated", 2, classes.size());

		classes.stream().forEach(e -> log.info("Classes generated "+e.getQualifiedName()));
	
		assertTrue("We do not have 2 spoon classes generated", classes.size() >= 2);

		
		assertFalse(main1.getEngine().getMutatorSupporter().getTestClasses().contains(classes.get(0)));

		// We save model, first Spoon
		String classpathForCompileSpoon = "";
		// ----------
		classpathForCompileSpoon = main1.getEngine().getProjectFacade().getProperties().getDependenciesString()
				+ File.pathSeparator + bytecodeSUTLocation + File.pathSeparator
				+ new File(ConfigurationProperties.getProperty("evosuitejar")).getAbsolutePath();

		String[] classpathForCreateModel = classpathForCompileSpoon.split(File.pathSeparator);

		// Compile
		CompilationResult compilation = MutationSupporter.currentSupporter.getSpoonClassCompiler()
				.compileOnMemory(classes, Converters.toURLArray(classpathForCreateModel));
		assertFalse("Any bytecode", compilation.getByteCodes().values().isEmpty());

		//// Save compiled
		String outPutTest = main1.getEngine().getProjectFacade()
				.getOutDirWithPrefix("/evosuite/evosuite-tests_" + variant.currentMutatorIdentifier());
		File fbyteEvo = new File(outPutTest);
		log.info("Saving evotest bytecode at " + fbyteEvo);
		MutationSupporter.currentSupporter.getSpoonClassCompiler().saveByteCode(compilation, fbyteEvo);
		assertTrue(fbyteEvo.exists());
		assertTrue(fbyteEvo.list().length > 0);

		List<String> testToExecute = new ArrayList<>();
		for (CtClass evoTest : classes) {
			if (!evoTest.getQualifiedName().endsWith("ESTest_scaffolding"))
				testToExecute.add(evoTest.getQualifiedName());
		}

		String classpathForRunTest = classpathForCompileSpoon + (File.pathSeparator) + outPutTest;
		log.info("Process classpath " + classpathForRunTest);

		ProcessEvoSuiteValidator evoProcess = new ProcessEvoSuiteValidator();
		TestCaseVariantValidationResult evoResult = evoProcess
				.executeRegressionTesting(Converters.toURLArray(classpathForRunTest.split(File.pathSeparator)), testToExecute);

		assertNotNull("Test execution null", evoResult);
		assertEquals("have a failure", evoResult.getFailureCount(), 0);
		assertTrue("Not successfull", evoResult.isSuccessful());
		assertTrue("Not exec", evoResult.getPassingTestCases() > 0);

	}
	@Test
	public void testCompleteEvosuiteTests() throws Exception {
		AstorMain main1 = new AstorMain();

		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-package", "10",
				// We force to not execute the evolution, so, we run it for zero
				// generation
				"-maxgen", "0", "-population", "1", "-stopfirst", "true", "-maxtime", "100"

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() == 0);

		assertEquals(1, main1.getEngine().getVariants().size());

		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		main1.getEngine().processCreatedVariant(variant, 1);
		
		log.info("Executing evosuite");
		EvoSuiteFacade fev = new EvoSuiteFacade();
		
		List<CtClass> classes = fev.createEvoTestModel(main1.getEngine().getProjectFacade(), variant);
	
		//Failing
		// Two classes: EvoTest + EvoScaffolding
		assertEquals("We do not have 2 spoon classes generated", 2, classes.size());

		assertFalse(main1.getEngine().getMutatorSupporter().getTestClasses().contains(classes.get(0)));

		TestCaseVariantValidationResult result = fev.saveAndExecuteEvoSuite(main1.getEngine().getProjectFacade(), variant, classes);
	
		assertNotNull(result);
		
		assertTrue(result.isSuccessful());
		
	}
	
	
	@Test
	public void testjGenProgWithEvoSuiteTests() throws Exception {
		AstorMain main1 = new AstorMain();
		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10",
				"-stopfirst", "true",
				"-population", 
				"1", "-stopfirst", "true", "-maxtime", "100",
				"-validation","evosuite","-maxgen","250"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		//One solution
		assertEquals(1,main1.getEngine().getSolutions().size());
	
		
	}
	

	/**
	 * We take the output of evosuite, we generate the spoon model, then we
	 * compile it, and finally we run the tests.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testCompileSaveAndRunEvoSuiteTestStepByStep() throws Exception {

		MutationSupporter.currentSupporter = new MutationSupporter();
	
		// Classpath to build a Spoon Model
		String classpath4BuildModel = new File("./examples/libs/junit-4.4.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/evo_suite_test/math_70_spooned//bin//default").getAbsolutePath()
				+ File.pathSeparator + new File(ConfigurationProperties.getProperty("evosuitejar")).getAbsolutePath();

		
		
		EvoSuiteFacade fev = new EvoSuiteFacade();

		List<CtClass> classes = fev.reificateEvoSuiteTest(
				// We have all project code files... we should put only those
				new File("./examples/evo_suite_test/math_70_spooned/evosuite/evosuite-tests")
						.getAbsolutePath(),
				classpath4BuildModel.split(File.pathSeparator));
		
		// Two classes: EvoTest + EvoScaffolding
		//assertEquals("We do not have 2 classes generated", 2, classes.size());
		//System.out.println("classes generated \n"+classes.stream().d);
		classes.forEach(e->System.out.println("ES class generated: "+e.getSimpleName()));
		assertTrue("We do not have 2 classes generated", classes.size() >=2);

		String classpathForCompileModel = new File("./examples/libs/junit-4.4.jar").getAbsolutePath()
				+ File.pathSeparator
				+ new File("./examples/evo_suite_test/math_70_spooned/bin/default").getAbsolutePath()
				+ File.pathSeparator + new File(ConfigurationProperties.getProperty("evosuitejar")).getAbsolutePath();

		log.info("Classpath "+classpathForCompileModel);
		
		
		String[] classpathForEvoSuiteTest2 = classpathForCompileModel.split(File.pathSeparator);

		// Compile (generation of evosuite bytecode) on memory
		CompilationResult compilation = MutationSupporter.currentSupporter.getSpoonClassCompiler()
				.compileOnMemory(classes, Converters.toURLArray(classpathForEvoSuiteTest2));
		assertFalse("Any bytecode", compilation.getByteCodes().values().isEmpty());

		assertEquals("Other size", 2, compilation.getByteCodes().values().size());
		// Create the directory for writing bytecode
		String outPutTest = new File(ConfigurationProperties.getProperty("workingDirectory")
				+ "/math_70_spooned/bin/evosuite/evosuite-tests").getAbsolutePath();

		File fbyteEvo = new File(outPutTest);
		if (fbyteEvo.exists()) {
			FileUtils.deleteDirectory(fbyteEvo);
		}
		boolean created = fbyteEvo.mkdirs();

		assertTrue(created);
		assertTrue(fbyteEvo.exists());
		assertEquals(0, fbyteEvo.list().length);

		// save the compilation of evosuitetest on disk
		MutationSupporter.currentSupporter.getSpoonClassCompiler().saveByteCode(compilation, fbyteEvo);
		assertTrue(fbyteEvo.exists());
		assertTrue(fbyteEvo.list().length > 0);

		/// Running junit for executing evosuite test:
		String classpathForRunTest = classpathForCompileModel + (File.pathSeparator) + outPutTest;
		// Asserting Running
		URL[] classpath4Process = Converters.toURLArray(classpathForRunTest.split(File.pathSeparator));

		List<String> testToExecute = new ArrayList<>();
		for (CtClass evoTest : classes) {
			if (!evoTest.getQualifiedName().endsWith("ESTest_scaffolding"))
				testToExecute.add(evoTest.getQualifiedName());
		}

		ProcessEvoSuiteValidator evoProcess = new ProcessEvoSuiteValidator();
		TestCaseVariantValidationResult evoResult = evoProcess.executeRegressionTesting(classpath4Process,
				testToExecute);

		assertNotNull("Null result", evoResult);
		assertEquals("have a failure", evoResult.getFailureCount(), 0);
		assertTrue("Not successfull", evoResult.isSuccessful());
		assertTrue("Not exec", evoResult.getPassingTestCases() > 0);
	}
	
	

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70WithEvosuiteTests() throws Exception {
		AstorMain main1 = new AstorMain();

		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "400", "-population", "1", 
				"-stopfirst", "true", 
				"-maxtime", "20",
				//PARAMETER TO TEST
				"-validation", "evosuite"
				

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresult.getEvoValidation().isSuccessful());
		
		assertTrue(esvalidationresult.getEvoValidation().getFailureCount() > 0);
		
		assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
		
		
	}
	
	
	
	
	@Test
	public void testM70Regression() throws Exception{
		String command = "-mode,statement,"
				+ "-location,"+ (new File("./examples/math_70")).getAbsolutePath()
				+ ","+ "-dependencies,"+new File("./examples/libs/junit-4.11.jar").getAbsolutePath()
				+ ",out,"+new File(ConfigurationProperties.getProperty("workingDirectory"))
			
				+ ",-failing,org.apache.commons.math.analysis.solvers.BisectionSolverTest,"
				+ "-package,org.apache.commons,"
				+ "-javacompliancelevel,7,"
				+ "-maxgen,1000000,"
				+ "-seed,1,"
				+ "-stopfirst,true,"
				+ "-scope,package,-maxtime,10,"
				+ "-population,1,"
				+ "-srcjavafolder,src/java/,"
				+ "-srctestfolder,src/test/,-binjavafolder,target/classes/,"
				+ "-bintestfolder,target/test-classes/,"
				+ "-flthreshold,0.1,"
				+ "-validation,"+fr.inria.astor.core.validation.validators.RegressionValidation.class.getCanonicalName()
				+",-testbystep"
				;
		String[] args = command.split(",");
		AstorMain main1 = new AstorMain();
		main1.execute(args);
	
		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresult.getEvoValidation().isSuccessful());
		
		assertTrue(esvalidationresult.getEvoValidation().getFailureCount() > 0);
		
		assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
	}

	/**
	 * This test reproduce the bug of calling a regression using a process.
	 * After calling it, it does not return.
	 * 
	 * @throws Exception
	 */
	// @Test
	public void testBugProcessDoesNotReturn() throws Exception {

		String command =
		// Java location
		"/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/bin//java "
				// memory
				+ "-Xmx2048m " + "-cp "
				// Classpath
				+ "/Users/matias/develop/code/astor/target/test-classes:/Users/matias/develop/code/astor/target/classes:/Users/matias/.m2/repository/fr/inria/gforge/spoon/spoon-core/4.4.1/spoon-core-4.4.1.jar:/Users/matias/.m2/repository/org/eclipse/jdt/org.eclipse.jdt.core/3.12.0.v20150913-1717/org.eclipse.jdt.core-3.12.0.v20150913-1717.jar:/Users/matias/.m2/repository/com/martiansoftware/jsap/2.1/jsap-2.1.jar:/Users/matias/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar:/Users/matias/.m2/repository/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar:/Users/matias/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar:/Users/matias/.m2/repository/com/gzoltar/gzoltar/0.1.1/gzoltar-0.1.1.jar:/Users/matias/.m2/repository/junit/junit/4.11/junit-4.11.jar:/Users/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar:/Users/matias/.m2/repository/commons-cli/commons-cli/1.2/commons-cli-1.2.jar:/Users/matias/develop/Eclipse.app/Contents/Eclipse/configuration/org.eclipse.osgi/214/0/.cp/:/Users/matias/develop/Eclipse.app/Contents/Eclipse/configuration/org.eclipse.osgi/213/0/.cp/:/Users/matias/develop/code/astor/./examples/libs/junit-4.4.jar:/Users/matias/develop/code/astor/./outputMutation/math_70_spooned/bin/default/:/Users/matias/develop/wsastor/testES/libs/evosuite-1.0.3.jar:"
				// TestGenerated by Evosuite
				+ "/Users/matias/develop/code/astor/outputMutation/math_70_spooned/bin/evosuite/evosuite-tests/: "
				// jUnit and arguments
				+ "fr.inria.astor.junitexec.JUnitTestExecutor org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest";
		System.out.println("Running ");

		Process process = new ProcessBuilder(command.split(" ")).start();
		// process.waitFor(30, TimeUnit.SECONDS);
		// Bug: it does not return
		process.waitFor();

		System.out.println("End process");
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;

		// System.out.printf("Output of running %s is:", Arrays.toString(args));

		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}

	
	

	@Test
	public void testValidationWrongArgumentValue() throws Exception {
		AstorMain main1 = new AstorMain();
		try{
		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "200", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				//PARAMETER TO TEST
				"-validation", "wrongargument"

		};
		
		main1.execute(args);
		fail();
		}catch(Exception e){}
	}

	@Test
	public void testM70() throws Exception{
		String command = "-mode,statement,"
				//+ "-location,"+ (new File(".")).getAbsolutePath()
				
					+ "-location,"+ (new File("./examples/math_70")).getAbsolutePath()
				+ ","+ "-dependencies,"+new File("./examples/libs/junit-4.11.jar").getAbsolutePath()
				//+ ","
				+ ",out,"+new File(ConfigurationProperties.getProperty("workingDirectory"))
				//+ "-dependencies,"+(new File("./examples/libs/")).getAbsolutePath()
				+ ",-failing,org.apache.commons.math.analysis.solvers.BisectionSolverTest,"
				+ "-package,org.apache.commons,"
				//+ "-jvm4testexecution,/home/mmartinez/jdk1.8.0_45/bin/"
				+ "-javacompliancelevel,7,"
				+ "-maxgen,1000000,"
				+ "-seed,6001,"
				+ "-stopfirst,true,"
				+ "-scope,package,-maxtime,10,"
				+ "-population,1,"
				+ "-srcjavafolder,src/java/,"
				+ "-srctestfolder,src/test/,-binjavafolder,target/classes/,"
				+ "-bintestfolder,target/test-classes/,"
				+ "-flthreshold,0.1,"
				//+ " -validation,fr.inria.astor.core.validation.validators.RegressionValidation,"
				;
		String[] args = command.split(",");
		AstorMain main1 = new AstorMain();
		main1.execute(args);
	}
	
	
	
	
	

	
	@SuppressWarnings("rawtypes")
	//@Test
	public void testNonDeterministimsMath70WithEvosuiteTests() throws Exception {
	

		int numberRuns = 3,inum = 0;
	
		//For storying the results
		int executed = -1, failing = -1; 
	
		
		while (inum < numberRuns){
		
			AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite"

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
	
		///
		if(executed == -1){
			executed = esvalidationresult.getEvoValidation().getCasesExecuted();
			failing = esvalidationresult.getEvoValidation().getFailureCount();
		}else{
			
			assertEquals(esvalidationresult.getEvoValidation().getFailureCount(), failing);
			
			assertEquals(esvalidationresult.getEvoValidation().getCasesExecuted(), executed);
		
		}
	
		
		inum ++;
		
		}
	}
	
	
	
	
	
}
