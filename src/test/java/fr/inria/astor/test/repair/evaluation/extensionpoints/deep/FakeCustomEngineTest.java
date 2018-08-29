package fr.inria.astor.test.repair.evaluation.extensionpoints.deep;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.astor.test.repair.evaluation.extensionpoints.deep.support.DKeyReader;
import fr.inria.astor.test.repair.evaluation.extensionpoints.deep.support.FakeCustomEngine;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;

@Ignore
public class FakeCustomEngineTest extends BaseEvolutionaryTest {

	@Test
	public void testFakeStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath() + File.pathSeparator;
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep,
				//
				"-mode", "custom",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				/// ./examples/math_70
				// "/Users/matias/develop/workspace/math_70"
				// new File("./examples/math_70").getAbsolutePath(),
				new File("/Users/matias/develop/workspace/math_70").getAbsolutePath(),
				//
				"-package", "org.apache.commons",
				// "-srcjavafolder",
				// "/src/main/java/",
				// "-srctestfolder", "/src/test/java",
				// "-binjavafolder", "/target/classes",
				// "-bintestfolder",
				// "/target/test-classes",
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "6010", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "30", "-customengine",
				FakeCustomEngine.class.getName(), ExtensionPoints.REPAIR_OPERATORS.argument(),
				RemoveOp.class.getCanonicalName(), };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.isEmpty());
		// from example
		// CT total exect 7800, found exec 3296
		// DP total exect 4322, found exec 1704
		// from new
		// CT total exect 7800, found exec 3296
		// DP total exect 4322, found exec 1704
	}

	@Test
	public void testReadFile() throws IOException {
		String path = "/Users/matias/develop/results/deeprepair/dat/src2txt/Math/70/b/executables.key";
		DKeyReader dkr = new DKeyReader();
		dkr.readKeyFile(new File(path));

	}
}
