package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.deeprepair.CloneIngredientSearchStrategy;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.validation.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtExecutable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LangTest {

	@Test
	@Ignore
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
		CommandSummary command = new CommandSummary(args);
		System.out.println(Arrays.toString(command.flat()));
		command.command.put("-parameters", "logtestexecution:true");
		command.command.put("-population", "1");
		command.command.put("-mode", "jkali");
		main1.execute(command.flat());

		assertFalse("Solution not found", main1.getEngine().getSolutions().isEmpty());

		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution
				.getValidationResult();

		assertNotNull("Without validation", validationResult);

	}

	@Test
	public void testLang63OneSingleJUnitNologExternalExecutor() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = true;
		String[] args = commandLang63(dep, out, stepbystep);
		CommandSummary command = new CommandSummary(args);
		System.out.println(Arrays.toString(command.flat()));
		String save = "true";
		command.command.put("-parameters", "logtestexecution:" + save);
		command.command.put("-population", "1");
		command.command.put("-mode", "jkali");
		main1.execute(command.flat());

		assertEquals("Incorrectly saved Property ", save, ConfigurationProperties.getProperty("logtestexecution"));
		assertFalse("Solution not found", main1.getEngine().getSolutions().isEmpty());

		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution
				.getValidationResult();

		assertNotNull("Without validation", validationResult);

	}

	public String[] commandLang63(String dep, File out, boolean step) {
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang.time.DurationFormatUtilsTest", "-location",
				new File("./examples/lang_63/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/main/test/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "4", "-flthreshold", "0.1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "1", "-maxgen", "200",
				//
				"-stopfirst", "true", "-maxtime", "10", (step) ? "-testbystep" : "",
				// "-validation", RegressionValidation.class.getName(),
				//
				"-ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest", "-timezone", "America/New_York"

		};
		return args;
	}

	public CommandSummary commandLang8(String dep, File out, boolean step) {

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learninglang8").getFile());
		Class cloneGranularityClass = CtExecutable.class;
		String scope = CtClassIngredientSpace.class.getCanonicalName();

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang3.time.FastDateFormat_PrinterTest" + File.pathSeparator
						+ "org.apache.commons.lang3.time.FastDatePrinterTest",
				//
				"-location", new File("./examples/lang_8/").getAbsolutePath(),
				//
				//
				// "-package", "org.apache.commons",
				"-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder",
				"/target/classes/", //
				"-bintestfolder", "/target/tests/", "-javacompliancelevel", "6", "-flthreshold", "0.1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "60", "-maxgen", "200",
				//
				"-stopfirst", "true", "-maxtime", "10", (step) ? "-testbystep" : "",
				// "-validation", RegressionValidation.class.getName(),
				//
				// "-ignoredtestcases",
				// "org.apache.commons.lang.LocaleUtilsTest",
				"-timezone", "America/New_York",
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
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang.time.StopWatchTest", //
				"-location", new File("./examples/lang_55/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/main/test/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "4", "-flthreshold", "0.1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "6320", "-maxgen", "50",
				//
				"-stopfirst", "true", "-maxtime", "30", (step) ? "-testbystep" : "",
				//
				"ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",

		};
		return args;
	}

	public String[] commandLang1(File out, boolean step) {
		String dep = getLangCommonLibs();
		String[] args = new String[] {
				///
				"-dependencies", dep, "-mode", "jgenprog", // "-failing",
															// "org.apache.commons.lang3.math.NumberUtilsTest",
															// //
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
		String dep = libsdir + File.separator + "cglib.jar" + File.pathSeparator //
				+ libsdir + File.separator + "commons-io.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "asm.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "easymock.jar";//
		return dep;
	}

}
