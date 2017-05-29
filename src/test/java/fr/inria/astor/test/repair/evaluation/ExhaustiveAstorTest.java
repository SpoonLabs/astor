package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.exhaustive.ExhausitiveCloneEngine;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.TestCasesProgramValidationResult;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
/**
 * 
 * @author matias
 *
 */
public class ExhaustiveAstorTest  extends BaseEvolutionaryTest{

	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testExhaustiveMath70LocalSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, 
				"-mode", "exhaustive", 
				"-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), 
				//
				"-scope", "package", 
				"-seed", "10", "-maxgen", "100",
				"-stopfirst", "false",//
				"-maxtime", "100",
				//For excluding regression
				"-excludeRegression"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		//assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant.getValidationResult();
		assertTrue(validationResult.isRegressionExecuted());
		
		validatePatchExistence(out + File.separator + "AstorMain-math_70/", solutions.size());

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		//assertEquals(IngredientSpaceScope.LOCAL, mi.getIngredientScope());

		// mi.getIngredientScope()
		// Program variant ref to
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());

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
	public void testExhaustiveCloneMath70LocalSolution() throws Exception {
		
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, 
				"-mode","custom", 
				"-customengine",
				ExhausitiveCloneEngine.class.getCanonicalName(), 
				"-customop",
				"fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp",
			
				"-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1", "-out",
				out.getAbsolutePath(), 
				//
				//"-scope", "package", 
				"-seed", "10", "-maxgen", "100000",
				"-stopfirst", "false",//
				"-maxtime", "100",
				"-scope", //
				
			//	CtClassIngredientSpace.class.getCanonicalName(),//,
				CtPackageIngredientScope.class.getCanonicalName(), //
			//	"fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope",
				"-clonegranularity", "spoon.reflect.declaration.CtExecutable"//
				, "-transformingredient",
				 "-population","1",
				"-learningdir",learningDir.getAbsolutePath()
	
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
	
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		//assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant.getValidationResult();
		assertTrue(validationResult.isRegressionExecuted());
		
		validatePatchExistence(out + File.separator + "AstorMain-math_70/", solutions.size());
	
		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		//assertEquals(IngredientSpaceScope.LOCAL, mi.getIngredientScope());
	
		// mi.getIngredientScope()
		// Program variant ref to
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());
	
	}

}
