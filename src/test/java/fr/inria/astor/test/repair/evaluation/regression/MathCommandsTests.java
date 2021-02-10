package fr.inria.astor.test.repair.evaluation.regression;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MathCommandsTests {

	public static CommandSummary getMath70Command() {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "100",

		};
		return new CommandSummary(args);

	}

	public static CommandSummary getMath74Command() {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_74").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "100",

		};
		return new CommandSummary(args);

	}

	public static CommandSummary getMath28Command() {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog",
				"-location", new File("./examples/math_28").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder",
				"/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7",
				"-flthreshold", "0.1", "-out", out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen",
				Integer.toString(generations), "-stopfirst", "true", "-maxtime", "100",

		};
		return new CommandSummary(args);

	}

	public static CommandSummary getMath57Command() {
		String dep = new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 50;

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.stat.clustering.KMeansPlusPlusClustererTest", "-location",
				new File("./examples/math_57").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/ ", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "100", "-loglevel", "DEBUG",

		};
		return new CommandSummary(args);

	}

	public static CommandSummary getMath85Command() {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "200", "-scope", "package", "-seed", "10" };
		return new CommandSummary(args);
	}

	public static CommandSummary getMath63Command() {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-location",
				new File("./examples/math_63").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5",
				"-stopfirst", "false", "-maxgen", "200", "-scope", "package", "-seed", "10" };
		return new CommandSummary(args);
	}

	public static CommandSummary getMath32Command() {
		String dep = new File("./examples/libs/junit-4.10.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog","-failing",
				"org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSetTest", "-location",
				new File("./examples/math_32").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5",
				"-stopfirst", "false", "-maxgen", "200", "-scope", "package", "-seed", "10" };
		return new CommandSummary(args);
	}

	public static CommandSummary getMath42Command() {
		String dep = new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 50;

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.optimization.linear.SimplexSolverTestt", "-location",
				new File("/Users/matias/develop/extractedbug/math/math_42").getAbsolutePath(), "-package",
				"org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"5", "-flthreshold", "0.1", "-out", out.getAbsolutePath(), "-scope", "local", "-seed", "410", "-maxgen",
				Integer.toString(generations), "-stopfirst", "true", "-maxtime", "100",
				// "-loglevel","DEBUG",

		};
		return new CommandSummary(args);

	}

	public static CommandSummary getMath20Command() {
		String dep = new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 50;

		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-location",
				new File("./examples/math_20").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.1", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "100",
				// "-loglevel","DEBUG",

		};
		return new CommandSummary(args);

	}

	public static CommandSummary getMath50Command() {
		String dep = new File("./examples/libs/junit-4.8.2.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",

				"org.apache.commons.math.analysis.solvers.RegulaFalsiSolverTest", "-location",
				new File("./examples/math_50").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.1", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "5", "-ignoredtestcases", "org.apache.commons.math.util.FastMathTest" };
		return new CommandSummary(args);
	}

	@Test
	public void testMath1() throws Exception {

		// [-out
		// /local/scr/mtufano/Math/1/b/similar-global-executables-embeddings/1
		// -jvm4testexecution /usr/local/jdk1.7.0_80/bin/ -maxgen 1000000
		// -population 1 -maxtime 180 -timezone America/New_York
		// -seed 1 -javacompliancelevel 5 -package org.apache.commons
		// -binjavafolder target/classes/ -bintestfolder target/test-classes/
		// -srcjavafolder src/main/java/ -srctestfolder src/test/java/
		// -learningdir ../../out/learningdir/Math/1/b/ -location
		// ../../dat/defects4j/Math/1/b/ -failing
		// org.apache.commons.math3.fraction.FractionTest:org.apache.commons.math3.fraction.BigFractionTest
		// -dependencies ../../dat/libs/Math/commons-discovery-0.5.jar ]
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm1").getFile());
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog",
				//
				"-failing",
				"org.apache.commons.math3.fraction.FractionTest:org.apache.commons.math3.fraction.BigFractionTest",
				"-location", new File("./examples/math_1").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/main/java/ ", "-srctestfolder", "/src/test/java/", "-binjavafolder",
				"/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel", "5",
				"-flthreshold", "0.1", "-out", out.getAbsolutePath(), //
				"-seed", "11", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "100", //
				ExtensionPoints.REPAIR_OPERATORS.argument(),
				"fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp",
				"-scope", "fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope",
				"-ingredientstrategy",
				"fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy",
				"-clonegranularity", "spoon.reflect.declaration.CtExecutable"//
				, "-transformingredient", "-population", "1", "-learningdir", learningDir.getAbsolutePath(),
				"-loglevel", Level.INFO.toString()//
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		// assertTrue(solutions.size() > 0);
	}

}
