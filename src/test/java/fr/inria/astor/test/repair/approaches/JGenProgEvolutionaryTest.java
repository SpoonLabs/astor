package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.population.PopulationConformation;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JGenProgEvolutionaryTest extends BaseEvolutionaryTest {

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70PackageSolutionsEvolving() throws Exception {
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
				"-scope", "package", "-seed", "10", "-maxgen", "3000", "-stopfirst", "false", "-maxtime", "10",
				"-population", "1", "-reintroduce",
				PopulationConformation.PARENTS.toString() + File.pathSeparator
						+ PopulationConformation.SOLUTIONS.toString(), // Parameters:
				"-parameters", "maxnumbersolutions:3:ignoredTestCases:org.apache.commons.math.estimation.MinpackTest"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertEquals(3, solutions.size());
		boolean withMultiple = false;
		for (ProgramVariant programVariant : solutions) {
			log.info("-->" + programVariant.getOperations().values());
			withMultiple = withMultiple || programVariant.getOperations().values().size() >= 2;
		}
		assertTrue(withMultiple);
	}

}
