package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.entities.validation.TestCaseVariantValidationResult;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.population.PopulationConformation;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

/**
 * Test of Astor in mode jgenprog
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class JGenProgTest extends BaseEvolutionaryTest {

	File out = null;

	public JGenProgTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}

	@Test
	public void testExample280CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug280" };
		main1.execute(args);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
	}

	@Test
	public void testExample280Arguments() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = new CommandSummary();
		cs.command.put("-mode", "jGenProg");
		cs.command.put("-srcjavafolder", "/src/java/");
		cs.command.put("-srctestfolder", "/src/test/");
		cs.command.put("-binjavafolder", "/target/classes");
		cs.command.put("-bintestfolder", "/target/test-classes/");
		cs.command.put("-location", new File("./examples/Math-issue-280/").getAbsolutePath());
		cs.command.put("-dependencies", new File("./examples/Math-issue-280/lib").getAbsolutePath());
		cs.command.put("-maxgen", "0");

		main1.execute(cs.flat());
		List<ProgramVariant> variants = main1.getEngine().getVariants();
		assertTrue(variants.size() > 0);
	}

	// @Test
	public void testExample288CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug288" };
		main1.execute(args);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
	}

	// @Test
	public void testExample340CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug340" };
		main1.execute(args);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
	}

	// @Test
	public void testExample309CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug309" };
		main1.execute(args);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}

	/**
	 * The fix is a replacement of an return statement
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testMath85() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "true",
				"-maxgen", "400", "-scope", "package", "-seed", "10", "-loglevel", "INFO" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70ThisKeyword() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 0;
		String[] args = commandMath70(dep, out, generations);
		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-saveall", "true");
		cs.append("-parameters", ("logtestexecution:true"));

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		ReplaceOp rop = new ReplaceOp();
		CtElement elMP1 = variant.getModificationPoints().get(0).getCodeElement();

		assertEquals(elMP1.toString(), "return solve(min, max)");
		System.out.println(elMP1);

		List<Ingredient> ingredients = jgp.getIngredientSearchStrategy().getIngredientSpace().getIngredients(elMP1,
				elMP1.getClass().getSimpleName());
		System.out.println(ingredients);
		Optional<Ingredient> patchOptional = ingredients.stream()
				.filter(e -> e.getCode().toString().equals("return solve(f, min, max)")).findAny();
		assertTrue(patchOptional.isPresent());
		CtElement patch = patchOptional.get().getCode();
		assertEquals(patch.toString(), "return solve(f, min, max)");

		StatementOperatorInstance operation = new StatementOperatorInstance();
		operation.setOriginal(elMP1);
		operation.setOperationApplied(rop);
		operation.setModificationPoint(variant.getModificationPoints().get(0));
		operation.defineParentInformation(variant.getModificationPoints().get(0));
		operation.setModified(patch);

		variant.putModificationInstance(0, operation);

		boolean changed = VariableResolver.changeShadowedVars(elMP1, patch);
		assertTrue(changed);
		System.out.println("Pach code before: " + patch);

		CtMethod mep = (CtMethod) elMP1.getParent(spoon.reflect.declaration.CtMethod.class);
		System.out.println("Parent before:\n" + mep);
		elMP1.replace(patch);
		System.out.println("Parent after:\n" + mep);
		System.out.println("Pach code after: " + patch);
		// assertEquals(patch.toString(),"return solve(this.f, min, max)");
		assertEquals(patch.toString(), "return solve(f, min, max)");

		patch.setImplicit(false);
		System.out.println("Pach code after impl: " + patch);

	}

	public static String[] commandMath70(String dep, File out, int generations) {
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "5", "-loglevel", "INFO", "-parameters", "disablelog:false"

		};
		return args;
	}

	@Test
	public void testArguments() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10", "-stopfirst", "true", "-maxgen", "50",
				"-saveall", "false" };
		boolean correct = main1.processArguments(args);
		assertTrue(correct);

		String javahome = ConfigurationProperties.properties.getProperty("jvm4testexecution");

		assertNotNull(javahome);

		assertTrue(javahome.endsWith("bin"));
	}

	/**
	 * Testing injected bug at CharacterReader line 118, commit version 31be24.
	 * "org.jsoup.nodes.AttributesTest"+File.pathSeparator+"org.jsoup.nodes.DocumentTypeTest"
	 * +File.pathSeparator+"org.jsoup.nodes.NodeTest"+File.pathSeparator+"org.jsoup.parser.HtmlParserTest"
	 * 
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testJSoupParser31be24() throws Exception {
		String dep = new File("./examples/libs/junit-4.5.jar").getAbsolutePath();
		AstorMain main1 = new AstorMain();

		String[] args = new String[] { "-mode", "jgenprog", "-location",
				new File("./examples/jsoup31be24").getAbsolutePath(), "-dependencies", dep,
				// The injected bug produces 4 failing cases in two files
				"-failing",
				"org.jsoup.parser.CharacterReaderTest" + File.pathSeparator + "org.jsoup.parser.HtmlParserTest",
				//
				"-package", "org.jsoup", "-javacompliancelevel", "7", "-stopfirst", "true",
				//
				"-flthreshold", "0.8", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				//
				"-scope", "local", "-seed", "10", "-maxtime", "100", "-population", "1", "-maxgen", "250", "-saveall",
				"true" };
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertEquals(1, solutions.size());
		// TODO: Problem printing CtThisAccess
		// pos += offset
		// time(sec)= 30
		// operation: ReplaceOp
		// location= org.jsoup.parser.CharacterReader
		// line= 118
		// original statement= pos -= offset
		// fixed statement= pos += offset
		// generation= 26
	}

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void testMath50Remove() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.8.2.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		CommandSummary commandsummath50 = MathCommandsTests.getMath50Command();
		System.out.println(Arrays.toString(commandsummath50.flat()));
		main1.execute(commandsummath50.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variant
				.getValidationResult();

		assertTrue(validationResult.isRegressionExecuted());

	}

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
	public void testMath76() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.linear.SingularValueSolverTest", "-location",
				new File("./examples/math_76").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "6010", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "2",

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore // triggers some infinite loops and JVMs don't get killed
	public void testMath74() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegratorTest", "-location",
				new File("./examples/math_74").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "2",

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore // takes too long time on Travis CI
	public void testMath5_buggy() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String dep = new File("./examples/libs/junit-4.10.jar").getAbsolutePath();
		String[] args = new String[] { "-mode", "jgenprog", "-location",
				new File("./examples/math_5").getAbsolutePath(),
				//
				"-dependencies", dep,
				//
				"-flthreshold", "0.5", "-out", out.getAbsolutePath(),
				//
				//
				"-scope", "local", "-seed", "0",
				//
				"-maxgen", "5000",
				//
				"-stopfirst", "true", "-maxtime", "200",
				//
				"-loglevel", "INFO",
				//
				"-stopfirst", "true",
				//
				"-srcjavafolder", "/src/main/java/",
				//
				"-srctestfolder", "/src/test/java/",
				//
				"-binjavafolder", "/target/classes",
				//
				"-bintestfolder", "/target/test-classes",
				//
				"-javacompliancelevel", "6",

				"-parameters", "maxmodificationpoints:1"

		};
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath106UndoException() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/commons-discovery-0.2.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/libs/commons-logging-1.0.4.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/libs/junit-3.8.2.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.fraction.FractionFormatTest", "-location",
				new File("./examples/math_106").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "6010", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "5",

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70NotFailingAsArg() throws Exception {

		String originalFailing = "org.apache.commons.math.analysis.solvers.BisectionSolverTest";
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", //

				"-location", new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-maxgen", "0",// Forced

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		List<String> deducedFailingTest = main1.getEngine().getProjectFacade().getProperties().getFailingTestCases();
		assertNotNull(deducedFailingTest);
		assertEquals(1, deducedFailingTest.size());
		log.debug("deduced: " + deducedFailingTest);
		assertTrue(deducedFailingTest.contains(originalFailing));
	}

	@Test
	public void testMath70DiffOfSolution() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertFalse(variant.getPatchDiff().getFormattedDiff().isEmpty());
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());

		String diff = variant.getPatchDiff().getFormattedDiff();
		log.debug("Patch: " + diff);

	}

	@Test
	public void testMath70ChangeModifPoints() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-maxgen", "0");
		cs.command.put("-population", "1");

		// We execute astor for creating the model and run FL
		main1.execute(cs.flat());

		// Let's change the number of generations (it was 0 in the original command)
		ConfigurationProperties.setProperty("maxGeneration", "10000");
		AstorCoreEngine engine = main1.getEngine();

		assertEquals(1, engine.getVariants().size());
		engine.getVariants().get(0).getModificationPoints()
				.removeIf(e -> !(e.getCodeElement().toString().equals("return solve(min, max)")));

		assertEquals(1, engine.getVariants().get(0).getModificationPoints().size());
		// let's start the evolution again (the model was already created on that
		// engine, so we directly call start)
		engine.startEvolution();

		// we should call end after the startevol
		engine.atEnd();

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertFalse(variant.getPatchDiff().getFormattedDiff().isEmpty());

		assertTrue(existPatchWithCode(solutions, "return solve(f, min, max)"));

		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());

		String diff = variant.getPatchDiff().getFormattedDiff();
		log.debug("Patch: " + diff);

	}

	@Test
	public void testMath70StopAtXVariantsSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(),
				//
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false", "-maxtime", "10",
				"-population", "1", "-reintroduce", PopulationConformation.PARENTS.toString(), "-parameters",
				"maxnumbersolutions:2" };
		System.out.println(Arrays.toString(args));
		CommandSummary command = new CommandSummary(args);

		command.command.put("-parameters", "maxnumbersolutions:2");
		main1.execute(command.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() >= 1);

		assertTrue(existPatchWithCode(solutions, "return solve(f, min, max)") || existPatchWithCode(solutions, "return solve(f, initial, max)"));

		command.command.put("-parameters", "maxnumbersolutions:1");
		main1.execute(command.flat());

		solutions = main1.getEngine().getSolutions();
		assertEquals(1, solutions.size());

		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());

		assertTrue(existPatchWithCode(solutions, "return solve(f, min, max)"));
		assertFalse(existPatchWithCode(solutions, "return solve(f, initial, max)"));

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testIssue196_notrepaired_jgenprog() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		// File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", //

				"-location", new File("./examples/issues/LeapYearIssue196/").getAbsolutePath(), "-package",
				"LeapYear.bug1", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"7", "-flthreshold", "0.5", "-stopfirst", "true"// Forced

		};

		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		assertTrue(variants.size() > 0);

		ProgramVariant oneVariant = variants.get(0);
		assertTrue(oneVariant.getModificationPoints().size() > 0);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertEquals(0, solutions.size());

	}

}
