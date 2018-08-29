package fr.inria.astor.test.repair.evaluation.extensionpoints.deep;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.declaration.CtExecutable;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class D4JDeepLearningTest {

	@Test
	public void testChart1Learning() throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningchart1").getFile());

		File projectLocation = new File("./examples/chart_1");
		AstorMain main1 = new AstorMain();
		File dirLibs = new File(projectLocation.getAbsolutePath() + File.separator + "/lib/");
		String dep = getDependencies(dirLibs);
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { //
				"-dependencies", dep, //
				"-mode", "jgenprog", //
				"-location", projectLocation.getAbsolutePath(), //
				"-srcjavafolder", "/source/", //
				"-srctestfolder", "/tests/", //
				"-binjavafolder", "/build/", //
				"-bintestfolder", "/build-tests/", //
				"-javacompliancelevel", "4", //
				"-flthreshold", "0.1", //
				"-out", out.getAbsolutePath(), //
				"-seed", "1", //
				"-maxgen", "100", "-stopfirst", "false", //
				"-maxtime", "10", //
				"-package", "org.jfree", //

				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-scope", CtPackageIngredientScope.class.getCanonicalName(),

				//
				"-clonegranularity", CtExecutable.class.getCanonicalName(),
				//
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getCanonicalName(),
				//
				"-transformingredient",
				//
				"-loglevel", Level.INFO.toString(),

				//
				ExtensionPoints.REPAIR_OPERATORS.argument(),
				"fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp"
				//
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		assertTrue(main1.getEngine().getSolutions().size() > 0);

	}

	@Test
	public void testMath1() throws Exception {

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

	private String getDependencies(File dirLibs) {
		String dep = "";
		System.out.println(dirLibs);
		for (File depend : dirLibs.listFiles()) {
			if (!depend.isDirectory())
				dep += depend.getAbsolutePath() + File.pathSeparator;

		}
		return dep;
	}

}
