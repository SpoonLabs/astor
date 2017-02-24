package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.apache.log4j.Level;
import org.junit.Test;

import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtExecutable;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ChartTest {

	@Test
	public void testChart1Learning() throws Exception {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningchart1").getFile());

		File projectLocation = new File("./examples/chart_1");
		AstorMain main1 = new AstorMain();
		File dirLibs = new File(projectLocation.getAbsolutePath() + File.separator + "/lib/");
		String dep = getDependencies( dirLibs);
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { //
				"-dependencies", dep, //
				"-mode", "statement", //
				"-location", projectLocation.getAbsolutePath(), //
				"-srcjavafolder", "/source/", //
				"-srctestfolder", "/tests/", //
				"-binjavafolder", "/build/", //
				"-bintestfolder", "/build-tests/", //
				"-javacompliancelevel", "4", //
				"-flthreshold", "0.1", //
				"-out", out.getAbsolutePath(), //
				"-seed", "1", //
				"-maxgen", "100", 
				"-stopfirst", "false", //
				"-maxtime", "10",//
				"-package","org.jfree",//

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
				"-customop","fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp"
				//
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		assertTrue(main1.getEngine().getSolutions().size() > 0);

	}

	private String getDependencies( File dirLibs) {
		String dep = "";
		System.out.println(dirLibs);
		for (File depend : dirLibs.listFiles()) {
			if(!depend.isDirectory())
				dep+=depend.getAbsolutePath() + File.pathSeparator;
			
		}
		return dep;
	}

}
