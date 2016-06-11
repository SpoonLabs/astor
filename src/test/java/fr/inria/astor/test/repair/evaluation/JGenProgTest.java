package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.bridgeFLSpoon.SpoonLocationPointerLauncher;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

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
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-Math-issue-280/");
	}

	// TODO: THE PARENT OF A STATEMENT IS A CASE:
	// @Test
	public void testExample288CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug288" };
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-Math-issue-288/");
	}

	// @Test
	public void testExample340CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug340" };
		main1.main(args);
		validatePatchExistence(out + File.separator + "Math-issue-340/");
	}

	// @Test
	public void testExample309CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug309" };
		main1.main(args);
		validatePatchExistence(out + File.separator + "Math-issue-309/");
	}

	/**
	 * The fix is a replacement of an return statement
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10" };
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
	}

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "100"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertTrue(variant.getValidationResult().isRegressionExecuted());
		
		validatePatchExistence(out + File.separator + "AstorMain-math_70/", solutions.size());

		ModificationInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientSpaceScope.LOCAL, mi.getIngredientScope());

		// mi.getIngredientScope()
		// Program variant ref to
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());

		for (CtType aff : affected) {
			CtType ctcProgVariant = returnByName(progVariant, (CtClass) aff);
			assertNotNull(ctcProgVariant);
			assertFalse(ctcProgVariant == aff);

			// Classes from affected set must be not equals to the program
			// variant cloned ctclasses,
			// due to these have include the changes applied for repairing the
			// bug.
			assertNotEquals(ctcProgVariant, aff);

			// Classes from affected set must be equals to the spoon model
			CtType ctspoon = returnByName(MutationSupporter.getFactory().Type().getAll(), (CtClass) aff);
			assertNotNull(ctcProgVariant);
			assertEquals(ctspoon, aff);
		}

	}

	/**
	 * Return the ct type from the collection according tho the class passed as
	 * parameter.
	 * 
	 * @param classes
	 * @param target
	 * @return
	 */
	private CtType returnByName(Collection<?> classes, CtClass target) {

		for (Object ctClass : classes) {
			if (((CtType) ctClass).getSimpleName().equals(target.getSimpleName())) {
				return (CtType) ctClass;
			}
		}
		return null;
	}

	@Test
	public void testArguments() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
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
	 * We pass as custom operator one that was already included in astor (it is
	 * included in the classpath).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85CustomOperator() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop", RemoveOp.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		// validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		// The space must have only ONE operator
		assertEquals(1, main1.getEngine().getRepairActionSpace().size());
		assertEquals(RemoveOp.class.getSimpleName(),
				main1.getEngine().getRepairActionSpace().values()[0].getClass().getSimpleName());

	}

	/**
	 * We pass as custom operator that it does not exist
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85AnyCustomOperator() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop", "MyoPeratorInvented1" };
		System.out.println(Arrays.toString(args));
		try {
			main1.execute(args);
			fail("Astor cannot work without operators");
		} catch (Exception e) {// Expected
		}
	}

	/**
	 * We pass as custom operator one that was already included in astor (it is
	 * included in the classpath) but it does not repair the bug
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85_Custom_Operator_NoFix() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop", ReplaceOp.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		// The space must have only ONE operator
		assertEquals(1, main1.getEngine().getRepairActionSpace().size());
		assertEquals(ReplaceOp.class.getSimpleName(),
				main1.getEngine().getRepairActionSpace().values()[0].getClass().getSimpleName());

		validatePatchExistence(out + File.separator + "AstorMain-math_85/", 0);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() == 0);

	}

	/**
	 * Two custom operators, one repair the bug.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85TwoCustomOperators() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop",
				(InsertAfterOp.class.getCanonicalName() + File.pathSeparator + RemoveOp.class.getCanonicalName()) };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		// The space must have Two operators
		assertEquals(2, main1.getEngine().getRepairActionSpace().size());
		assertEquals(InsertAfterOp.class.getSimpleName(),
				main1.getEngine().getRepairActionSpace().values()[0].getClass().getSimpleName());
		assertEquals(RemoveOp.class.getSimpleName(),
				main1.getEngine().getRepairActionSpace().values()[1].getClass().getSimpleName());

		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}

	@Test
	public void testMath85_CustomBasicIngredientStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				EfficientIngredientStrategy.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}
	
	
	/**
	 * Testing injected bug at CharacterReader line 118, commit version 31be24.
	 * "org.jsoup.nodes.AttributesTest"+File.pathSeparator+"org.jsoup.nodes.DocumentTypeTest"
						+File.pathSeparator+"org.jsoup.nodes.NodeTest"+File.pathSeparator+"org.jsoup.parser.HtmlParserTest"
				
	 * @throws Exception
	 */
	@Test
	@Ignore
	public void testJSoupParser31be24() throws Exception {
		String dep = new File("./examples/libs/junit-4.5.jar").getAbsolutePath();
		AstorMain main1 = new AstorMain();
		
		String[] args = new String[] { "-mode", "statement", "-location",
				new File("./examples/jsoup31be24").getAbsolutePath(), "-dependencies", dep, 
				//The injected bug produces 4 failing cases in two files
				"-failing",
				"org.jsoup.parser.CharacterReaderTest"+File.pathSeparator
				+"org.jsoup.parser.HtmlParserTest"
				, 
				//
				"-package", "org.jsoup", "-javacompliancelevel", "7", 
				"-stopfirst", "true", 
				//
				"-flthreshold", "0.8", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", 
				//
				"-scope", "local",
				"-seed", "10", 
				"-maxtime", "100",
				"-population","1",
				"-maxgen","250",
				"-saveall","true"
				};
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertEquals(1,solutions.size());
		//TODO: Problem printing CtThisAccess
		//pos += offset
		//time(sec)= 30
		//operation: ReplaceOp
		//	location= org.jsoup.parser.CharacterReader
		//	line= 118
		//	original statement= pos -= offset
		//	fixed statement= pos += offset
		//generation= 26	
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath50Remove() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.8.2.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.RegulaFalsiSolverTest", "-location",
				new File("./examples/math_50").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.1", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "100",
				"-jvm4testexecution","/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/bin/"
				

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertTrue(variant.getValidationResult().isRegressionExecuted());
		
	}
}
