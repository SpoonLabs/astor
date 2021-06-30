package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jkali.operators.ReplaceIfBooleanOp;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;

/**
 * Execution of multiples kali scenarios.
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class jKaliTest extends BaseEvolutionaryTest {

	File out = null;

	public jKaliTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));

	}

	protected List<ProgramVariant> testSeedExampleKali(String projectpath) throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies",
				new File(projectpath + File.separator + "lib/").getAbsolutePath(), "-mode", "jkali", "-failing",
				"mooctest.TestSuite_all", "-location", new File(projectpath).getAbsolutePath(), "-package", "mooctest",
				"-srcjavafolder", "/src/", "-srctestfolder", "/junit/", "-binjavafolder", "bin/", "-bintestfolder",
				"bin/", "-flthreshold", "0.1", "-out", out.getAbsolutePath() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		assertTrue(main1.getEngine().getSolutions().size() > 0);
		return main1.getEngine().getSolutions();
	}

	@Test
	public void testSeedExampleKaliRemove() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliRemoveStatement");
		checkOperator(solutions, RemoveOp.class);
	}

	private void checkOperator(List<ProgramVariant> solutions, Class _class) {
		boolean present = false;
		for (ProgramVariant programVariant : solutions) {
			present = present || programVariant.getAllOperations().stream()
					.anyMatch(e -> _class.isInstance(e.getOperationApplied()));
		}
		assertTrue(present);
	}

	@Test
	public void testSeedExampleKaliIfFalse() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliIfFalse");
		checkOperator(solutions, ReplaceIfBooleanOp.class);

	}

	@Test
	public void testSeedExampleKaliAddReturnInt() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliAddReturnPrimitive");
		// checkOperator(solutions, ReplaceReturnOp.class);
	}

	@Test
	public void testSeedExampleKaliAddReturnVoid() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliAddReturnVoid");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory") + File.separator
				+ "AstorMain-testKaliAddReturnVoid/");

		// checkOperator(solutions, ReplaceReturnOp.class);

	}

	@Test
	public void testjKaliFindingMoreThanOneSolution() throws Exception {
		
		File targetolder = new File("./examples/test-jkali-multiple-solutions/target/");
		if (!targetolder.exists()){
			targetolder.mkdirs();
		}
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		int compilerResult = compiler.run(null, null, null,
				"./examples/test-jkali-multiple-solutions/src/main/java/com/example/jkali_multiple_solutions/App.java",
				"./examples/test-jkali-multiple-solutions/src/test/java/com/example/jkali_multiple_solutions/AppTest.java",
				"-d", "./examples/test-jkali-multiple-solutions/target/");

		assertEquals(0, compilerResult);

		File binJavaFolder = new File("./examples/test-jkali-multiple-solutions/target/classes/com/example/jkali_multiple_solutions");
		if (!binJavaFolder.exists()){
			binJavaFolder.mkdirs();
		}

		File compiledSource = new File("./examples/test-jkali-multiple-solutions/target/com/example/jkali_multiple_solutions/App.class");
		compiledSource.renameTo(new File(binJavaFolder, compiledSource.getName()));

		File binTestFolder = new File("./examples/test-jkali-multiple-solutions/target/test-classes/com/example/jkali_multiple_solutions");
		if (!binTestFolder.exists()){
			binTestFolder.mkdirs();
		}

		File compiledTest = new File("./examples/test-jkali-multiple-solutions/target/com/example/jkali_multiple_solutions/AppTest.class");
		compiledTest.renameTo(new File(binTestFolder, compiledTest.getName()));

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", ExecutionMode.jKali.toString().toLowerCase(),
				"-failing", "com.example.jkali_multiple_solutions.AppTest", "-location",
				new File("./examples/test-jkali-multiple-solutions").getAbsolutePath(), "-package", "com.example.jkali_multiple_solutions", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-out",
				out.getAbsolutePath(),
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false",
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		assertTrue(solutions.size() > 1);

		List<PatchStat> patches = main1.getEngine().getPatchInfo();

		Assert.assertTrue(patches.size() > 1);

		Assert.assertEquals(3, patches.size());
	}

	@Test
	public void testjKaliStopAfterFindingFirstSolution() throws Exception {

		File targetolder = new File("./examples/test-jkali-multiple-solutions/target/");
		if (!targetolder.exists()){
			targetolder.mkdirs();
		}
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		int compilerResult = compiler.run(null, null, null,
				"./examples/test-jkali-multiple-solutions/src/main/java/com/example/jkali_multiple_solutions/App.java",
				"./examples/test-jkali-multiple-solutions/src/test/java/com/example/jkali_multiple_solutions/AppTest.java",
				"-d", "./examples/test-jkali-multiple-solutions/target/");

		assertEquals(0, compilerResult);

		File binJavaFolder = new File("./examples/test-jkali-multiple-solutions/target/classes/com/example/jkali_multiple_solutions");
		if (!binJavaFolder.exists()){
			binJavaFolder.mkdirs();
		}

		File compiledSource = new File("./examples/test-jkali-multiple-solutions/target/com/example/jkali_multiple_solutions/App.class");
		compiledSource.renameTo(new File(binJavaFolder, compiledSource.getName()));

		File binTestFolder = new File("./examples/test-jkali-multiple-solutions/target/test-classes/com/example/jkali_multiple_solutions");
		if (!binTestFolder.exists()){
			binTestFolder.mkdirs();
		}

		File compiledTest = new File("./examples/test-jkali-multiple-solutions/target/com/example/jkali_multiple_solutions/AppTest.class");
		compiledTest.renameTo(new File(binTestFolder, compiledTest.getName()));

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", ExecutionMode.jKali.toString().toLowerCase(),
				"-failing", "com.example.jkali_multiple_solutions.AppTest", "-location",
				new File("./examples/test-jkali-multiple-solutions").getAbsolutePath(), "-package", "com.example.jkali_multiple_solutions", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-out",
				out.getAbsolutePath(),
				"-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "true",
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();

		assertTrue(solutions.size() == 1);

		List<PatchStat> patches = main1.getEngine().getPatchInfo();

		Assert.assertTrue(patches.size() == 1);
	}

	@SuppressWarnings("static-access")
	@Test
	@Ignore // We ignore this test, Gzoltar works different with Java 8
	public void testMath2ExampleRemoveModeOneSolution() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { "-dependencies", "./examples/math_2/libmvn/", "-mode", "jkali", "-failing",
				"org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				"./examples/math_2/", "-package", "org.apache.commons", "-stopfirst", "true", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-stopfirst", "true", });
		main1.execute(args);
		// As 'stopfirst' is true, kali must return 1 solution
		assertEquals(1, main1.getEngine().getSolutions().size());
	}

	@SuppressWarnings("static-access")
	@Test
	@Ignore
	public void testMath2ExampleRemoveModeManySolutions() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { "-dependencies", "./examples/math_2/libmvn/", "-mode", "jkali", "-failing",
				"org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				new File("./examples/math_2/").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-stopfirst", "false", });
		main1.execute(args);
		int numberSolution = main1.getEngine().getSolutions().size();
		// As 'stopfirst' is false, kali must return more than 1 solution
		assertTrue(numberSolution > 1);
	}

}
