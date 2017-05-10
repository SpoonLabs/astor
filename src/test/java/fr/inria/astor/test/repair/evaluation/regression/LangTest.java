package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.EvoSuiteValidationResult;
import fr.inria.astor.core.validation.validators.RegressionValidation;
import fr.inria.astor.core.validation.validators.TestCasesProgramValidationResult;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtExecutable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LangTest {

	@Test
	public void testLang1RegressionFailing() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = false;
		String[] args = commandLang1(out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		Assert.assertFalse(main1.getEngine().getMutatorSupporter().getFactory().Type().getAll().isEmpty());
		
	}
	
	@Test
	public void testLang63OneSingle() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = true;
		String[] args = commandLang63(dep, out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution
				.getValidationResult();

		assertNotNull("Without validation", validationResult);
		
	}

	@Test
	public void testLang8Clone() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = getLangCommonLibs();//new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = true;
		CommandSummary cs  = commandLang8(dep, out, stepbystep);
		String[] args = cs.flat();
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		/*assertTrue(main1.getEngine().getSolutions().size() > 0);

		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution
				.getValidationResult();

		assertNotNull("Without validation", validationResult);
		*/
		
	}
	
	@Test
	//@Ignore
	public void testLang63RegressionFailing() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = true;
		String[] args = commandLang63(dep, out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution
				.getValidationResult();

		assertNotNull("Without validation", validationResult);
		// As we execute jgp in evosuite validation mode, we expect
		// eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		// The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		// Failing due to the Regression bug
		assertFalse(esvalidationresult.getManualTestValidation().isSuccessful());

		// assertEquals(,
		// esvalidationresult.getManualTestValidation().getFailureCount());
		// Now, the extended validation must fail
		// assertFalse(esvalidationresult.getEvoValidation().wasSuccessful());

		// now step by step
		stepbystep = true;
		args = commandLang63(dep, out, stepbystep);
		System.out.println("stepbystep\n" + Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() > 0);

		variantSolution = main1.getEngine().getSolutions().get(0);
		validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();

		assertNotNull("Without validation", validationResult);
		// As we execute jgp in evosuite validation mode, we expect
		// eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		esvalidationresult = (EvoSuiteValidationResult) validationResult;

		// The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());

		System.out
				.println("failings " + ((TestCasesProgramValidationResult) esvalidationresult.getManualTestValidation())
						.getTestResult().failTest);
		assertTrue(25 > esvalidationresult.getManualTestValidation().getFailureCount());

		// Failing due to the Regression bug
		assertTrue(esvalidationresult.getManualTestValidation().isSuccessful());

	}

	@Test
	@Ignore //I have to fix the maven compilation, which fails using our script
	public void testLang55RegressionFailing() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = false;
		String[] args = commandLang55(dep, out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() > 0);
	}

	public String[] commandLang63(String dep, File out, boolean step) {
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.lang.time.DurationFormatUtilsTest", "-location",
				new File("./examples/lang_63/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/main/test/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "4", "-flthreshold", "0.1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "60", "-maxgen", "200",
				//
				//"-stopfirst", "true", 
				"-maxtime", "10", (step) ? "-testbystep" : "",
				//		"-validation",	RegressionValidation.class.getName(),
				//
			"-ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",
				"-timezone"
				, "America/New_York"

		};
		return args;
	}

	public CommandSummary commandLang8(String dep, File out, boolean step) {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learninglang8").getFile());
		Class cloneGranularityClass = CtExecutable.class;
		String scope = CtClassIngredientSpace.class.getCanonicalName();
		
		
		String[] args = new String[] { 
				"-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.lang3.time.FastDateFormat_PrinterTest"+File.pathSeparator
				+"org.apache.commons.lang3.time.FastDatePrinterTest", 
				//
				"-location",
				new File("./examples/lang_8/").getAbsolutePath(),
				//
				//
				//"-package", "org.apache.commons", 
				"-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes/",//
				"-bintestfolder", "/target/tests/",
				"-javacompliancelevel", "6", 
				"-flthreshold", "0.1",
				//
				"-out", out.getAbsolutePath(), 
				"-scope", "package", 
				"-seed", "60", 
				"-maxgen", "200",
				//
				"-stopfirst", "true", 
				"-maxtime", "10", (step) ? "-testbystep" : "",
				//		"-validation",	RegressionValidation.class.getName(),
				//
		//	"-ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",
				"-timezone"
				, "America/New_York",
				//
				"-scope", scope,
				//
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-clonegranularity", cloneGranularityClass.getCanonicalName(),
				//
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(),
				//
				"-transformingredient",

		};
		return new CommandSummary(args);
	}
	
	public String[] commandLang55(String dep, File out, boolean step) {
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.lang.time.StopWatchTest", //
				"-location", new File("./examples/lang_55/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/main/test/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "4", "-flthreshold", "0.1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "6320", "-maxgen", "50",
				//
				"-stopfirst", "true", "-maxtime", "30", (step) ? "-testbystep" : "", "-validation",
				RegressionValidation.class.getName(),
				//
				"ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",

		};
		return args;
	}

	public String[] commandLang1(File out, boolean step) {
		String dep = getLangCommonLibs();
		String[] args = new String[] {
				///
				"-dependencies", dep, "-mode", "statement", // "-failing", "org.apache.commons.lang3.math.NumberUtilsTest", //
				"-location", new File("./examples/lang_1/").getAbsolutePath(),
				//
				"-package", "org.apache.commons",
				//
				"-srcjavafolder", "/src/main/java/",
				//
				"-srctestfolder", "/src/main/test/",
				//
				"-binjavafolder", "/target/classes/",
				//
				"-bintestfolder", "/target/test-classes/",
				//
				"-javacompliancelevel", "6",
				//
				"-flthreshold", "0.1",
				//
				"-population", "1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "6320", "-maxgen", "50",
				//
				"-stopfirst", "true", "-maxtime", "5", (step) ? "-testbystep" : "",

		};
		return args;
	}

	private String getLangCommonLibs() {
		String libsdir = new File("./examples/libs/lang_common_lib").getAbsolutePath();
		String dep = libsdir + File.separator + "cglib.jar"+File.pathSeparator //
				+ libsdir + File.separator + "commons-io.jar"+File.pathSeparator //
				+ File.separator + libsdir + File.separator  + "asm.jar"+File.pathSeparator  //
				+ File.separator + libsdir + File.separator  + "easymock.jar";//
		return dep;
	}

}
