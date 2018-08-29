package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.main.FileLauncher;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class FileLauncherTest {

	String exampleRoot = "./examples/";
	ClassLoader classLoader = null;
	File dataDir = null;

	@Before
	public void setUp() {
		classLoader = getClass().getClassLoader();
		dataDir = new File(classLoader.getResource("data_projects").getPath());
	}

	@Test
	public void testMath70() throws Exception {

		FileLauncher l = new FileLauncher();

		String location = new File(exampleRoot + "/math_70").getAbsolutePath();
		String[] args = new String[] { "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location", location, "-flthreshold",
				"0.5", "-scope", "local",
				//
				"-population", "1", "-seed", "10", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "100",
				"-loglevel", Level.DEBUG.toString(),

		};

		String[] command = l.getCommand(args, new File(dataDir.getAbsolutePath() + "/math.json"), 70, location);

		// System.out.println(Arrays.toString(args));

		AstorMain main1 = new AstorMain();

		main1.execute(command);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

	}

	@Test
	public void testTime2() throws Exception {

		FileLauncher l = new FileLauncher();
		String location = new File(exampleRoot + "/time_2").getAbsolutePath();

		File learningDir = new File(classLoader.getResource("learningtime2").getPath());

		String[] args = new String[] { "-mode", "jgenprog",
				//
				"-location", location, "-flthreshold", "0.5",
				//
				"-seed", "1", "-maxgen", "500", "-stopfirst", "true", "-maxtime", "100",
				//
				ExtensionPoints.REPAIR_OPERATORS.argument(),
				"fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp",
				"-scope", "fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope",
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(), "-clonegranularity",
				"spoon.reflect.declaration.CtType", "-transformingredient",
				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(), "-loglevel", Level.INFO.toString(),
				// "
		};
		String timeLibs = "/Users/matias/develop/defects4j/framework/projects/Time/lib/";
		String[] command = l.getCommand(args, new File(dataDir.getAbsolutePath() + "/time.json"), 2, timeLibs);
		// System.out.println(Arrays.toString(args));

		AstorMain main1 = new AstorMain();

		main1.execute(command);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

	}

	@Test
	public void testClosure1() throws Exception {

		FileLauncher l = new FileLauncher();

		String location = new File("/Users/matias/develop/extractedbug/closure/" + "/closure_1").getAbsolutePath();
		String[] args = new String[] { "-mode", "jgenprog", "-location", location, "-flthreshold", "0.5", "-scope",
				"local",
				//
				"-population", "1", "-seed", "10", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "100",
				"-loglevel", Level.DEBUG.toString(),

		};

		String libsLocation = location + "/lib/";
		// List<String> dd = Arrays.asList(new String[]{location});
		String[] command = l.getCommand(args, new File(dataDir.getAbsolutePath() + "/closure.json"), 1, libsLocation,
				location, new ArrayList<>());

		// System.out.println(Arrays.toString(args));

		AstorMain main1 = new AstorMain();

		main1.execute(command);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

	}

}
