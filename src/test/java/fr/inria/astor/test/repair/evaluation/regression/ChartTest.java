package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ChartTest {

	@Test
	public void testChart1() throws Exception {

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
				//
				"-scope", CtPackageIngredientScope.class.getCanonicalName(),
				//
				"-loglevel", Level.INFO.toString(),

				//

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() > 0);

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
