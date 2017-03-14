package fr.inria.astor.test.repair.evaluation.dpl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Queue;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;

/**
 * Test for Clone ingredient strategy
 * 
 * @author Matias Martinez
 *
 */
public class ExecutableCloneIngredientStrategyTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Test
	public void testInputProperties() throws ClassNotFoundException, IOException {

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		assertTrue(learningDir.exists());

		assertTrue(learningDir.list().length > 0);

		ConfigurationProperties.properties.setProperty("learningdir", learningDir.getAbsolutePath());
		ConfigurationProperties.properties.setProperty("clonegranularity", CtType.class.getCanonicalName());
		IngredientSpace ingredientSpace = null;
		CloneIngredientSearchStrategy<?> cloneStrategy = new CloneIngredientSearchStrategy<>(ingredientSpace);

	}

	@Test
	public void testExecutableMath70() throws Exception {

		// Now, let's test at Executanble granularity

		Class executableCloneGranularity = CtExecutable.class;

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtExecutable.class;
		String[] args = createCommandM70(learningDir, executableCloneGranularity);

		log.debug(Arrays.toString(args));

		AstorMain main1 = new AstorMain();
		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		CloneIngredientSearchStrategy<CtExecutable> excloneStrategy = (CloneIngredientSearchStrategy<CtExecutable>) engine
				.getIngredientStrategy();

		AstorOperator op = new ReplaceOp();
		Ingredient exingredientSelected = excloneStrategy.getFixIngredient(buggyStatementModPoint, op);

		assertNotNull(exingredientSelected);

		CtExecutable<?> executParent = buggyStatementModPoint.getCodeElement().getParent(CtExecutable.class);

		Queue<CtCodeElement> execingredientsToConsider = excloneStrategy.getfixspace(buggyStatementModPoint, op,
				executParent);

		assertNotNull(execingredientsToConsider);

		assertFalse(execingredientsToConsider.isEmpty());

		log.debug(execingredientsToConsider);

	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass) {
		// HERE WE FORCE TO NOT EVOLVE
		return createCommandM70(learningDir, cloneGranularityClass, 0, false);
	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient) {
		return createCommandM70(learningDir, cloneGranularityClass, generations, transformIngredient,
				CtLocationIngredientSpace.class.getCanonicalName());
	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient, String scope) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "statement",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest",
				//
				"-location", new File("./examples/math_70").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				//
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
				//
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(),
				//
				"-scope", scope,
				//
				"-seed", "10",

				"-maxgen", Integer.toString(generations),
				//
				"-population", "1",
				//
				"-stopfirst", "true",
				//
				"-maxtime", "100",
				//
				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-clonegranularity", cloneGranularityClass.getCanonicalName(),
				//
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(),
				//
				"-transformingredient",
				//
				"-loglevel", Level.DEBUG.toString() };
		return args;
	}
@Test
	public void testM1() throws Exception{
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm1").getFile());
		
	String c = "-jvm4testexecution /Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/bin"
			+ " -maxgen 1000000 -population 1 -maxtime 180 -timezone America/New_York -customop fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp -seed 1 -javacompliancelevel 5 -package org.apache.commons -binjavafolder target/classes/ -bintestfolder target/test-classes/ -srcjavafolder src/main/java/ -srctestfolder src/test/java/ -learningdir "
			+ learningDir.getAbsolutePath()
			+ " -location " + new File("./examples/math_1").getAbsolutePath()
			+ " -failing org.apache.commons.math3.fraction.FractionTest:org.apache.commons.math3.fraction.BigFractionTest -dependencies "
			+ new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath()
			+ " -loglevel INFO"
			+ " -classestoinstrument org.apache.commons.math3.exception.ConvergenceException:org.apache.commons.math3.Field:org.apache.commons.math3.exception.MathIllegalArgumentException:org.apache.commons.math3.exception.NumberIsTooSmallException:org.apache.commons.math3.exception.util.Localizable:org.apache.commons.math3.exception.MathIllegalNumberException:org.apache.commons.math3.exception.NullArgumentException:org.apache.commons.math3.fraction.BigFraction:org.apache.commons.math3.util.ArithmeticUtils:org.apache.commons.math3.exception.MathArithmeticException:org.apache.commons.math3.exception.util.ExceptionContextProvider:org.apache.commons.math3.exception.NotPositiveException:org.apache.commons.math3.exception.MathIllegalStateException:org.apache.commons.math3.exception.util.LocalizedFormats:org.apache.commons.math3.util.FastMath:org.apache.commons.math3.util.MathUtils:org.apache.commons.math3.exception.NotFiniteNumberException:org.apache.commons.math3.exception.ZeroException:org.apache.commons.math3.FieldElement:org.apache.commons.math3.fraction.FractionConversionException:org.apache.commons.math3.fraction.Fraction -regressiontestcases4fl org.apache.commons.math3.analysis.interpolation.FieldHermiteInterpolatorTest:org.apache.commons.math3.analysis.polynomials.PolynomialsUtilsTest:org.apache.commons.math3.distribution.KolmogorovSmirnovDistributionTest:org.apache.commons.math3.fraction.BigFractionFieldTest:org.apache.commons.math3.fraction.BigFractionFormatTest:org.apache.commons.math3.fraction.BigFractionTest:org.apache.commons.math3.fraction.FractionFieldTest:org.apache.commons.math3.fraction.FractionFormatTest:org.apache.commons.math3.fraction.FractionTest:org.apache.commons.math3.linear.ArrayFieldVectorTest:org.apache.commons.math3.linear.BlockFieldMatrixTest:org.apache.commons.math3.linear.FieldLUDecompositionTest:org.apache.commons.math3.linear.FieldLUSolverTest:org.apache.commons.math3.linear.FieldMatrixImplTest:org.apache.commons.math3.linear.MatrixUtilsTest:org.apache.commons.math3.linear.SparseFieldMatrixTest:org.apache.commons.math3.linear.SparseFieldVectorTest:org.apache.commons.math3.ode.nonstiff.AdamsBashforthIntegratorTest:org.apache.commons.math3.ode.nonstiff.AdamsMoultonIntegratorTest:org.apache.commons.math3.ode.sampling.NordsieckStepInterpolatorTest:org.apache.commons.math3.util.OpenIntToFieldTest -scope fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope -ingredientstrategy fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy -transformingredient";
	System.out.println(c);
	//String[] cm = c.split(" ");
	//AstorMain.main(cm);
	}
	@Test
	public void testExecutableMath70TransformationStrategy() throws Exception {

		// Now, let's test at Executable granularity

		Class executableCloneGranularity = CtExecutable.class;

		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtExecutable.class;
		int generations = 100;
		boolean transformIngredient = true;
		String[] args = createCommandM70(learningDir, executableCloneGranularity, generations, transformIngredient);

		log.debug(Arrays.toString(args));

		AstorMain main1 = new AstorMain();
		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		ProgramVariant pvariant = engine.getVariants().get(0);

	}

	
	public String[] commandLang1Clone(File out, boolean step) {
		String libsdir = new File("./examples/libs/lang_common_lib").getAbsolutePath();
		String dep = libsdir + File.separator + "cglib.jar"+File.pathSeparator //
				+ libsdir + File.separator + "commons-io.jar"+File.pathSeparator //
				+ File.separator + libsdir +File.separator + "asm.jar"+File.pathSeparator //
				+ File.separator + libsdir + File.separator +"easymock.jar";//

	  //target/classes/ -bintestfolder target/tests/ -srcjavafolder src/main/java/ -srctestfolder src/test/java/ 
	  
	//	-learningdir ../../out/learningdir/Lang/1/b/ -location ../../dat/defects4j/Lang/1/b/ 
		
	//	-dependencies ../../dat/libs/Lang/cglib.jar:../../dat/libs/Lang/commons-io.jar:../../dat/libs/Lang/asm.jar:../../dat/libs/Lang/easymock.jar 

		String[] args = new String[] {
				///
				"-dependencies", dep, "-mode", "statement", 
				// "-failing", "org.apache.commons.lang3.math.NumberUtilsTest", //
				"-location", new File("./examples/lang_1/").getAbsolutePath(),
				//
				"-package", "org.apache.commons",
				//
				"-srcjavafolder", "/src/main/java/",
				//
				"-srctestfolder", "/src/main/test/",
				//
				"-binjavafolder", "/target/classes/",
				//
				"-bintestfolder", "/target/test-classes/",
				//
				"-javacompliancelevel", "6",
				//
				"-flthreshold", "0.1",
				//
				"-population", "1",
				//
				"-out", out.getAbsolutePath(), 
				"-scope", "package", 
				"-seed", "1", 
				"-maxgen", "50",
				//
				//
				 "-ingredientstrategy", "fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy",
				"-scope", "fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope",
				"-learningdir", "",
				"-timezone", "America/New_York",
				"-stopfirst", "true", 
				"-maxtime", "5", (step) ? "-testbystep" : "",
				"-customop", "fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp",
				"-loglevel", "DEBUG",
				
				//	"-clonegranularity", cloneGranularityClass.getCanonicalName(),
				//
			//	"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(),
				//
			//	"-transformingredient",
		};
		return args;
	}
	
	@Test
	public void testCloneLang1RegressionFailing() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = false;
		String[] args = commandLang1Clone(out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		Assert.assertFalse(main1.getEngine().getMutatorSupporter().getFactory().Type().getAll().isEmpty());
		
	}
	
}
