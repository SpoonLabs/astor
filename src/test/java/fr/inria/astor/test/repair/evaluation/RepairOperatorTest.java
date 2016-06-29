package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.other.FakeOperatorSelectionStrategy;
import fr.inria.main.evolution.AstorMain;

/**
 * Test of operators plugged to Astor in mode jgenprog
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class RepairOperatorTest extends BaseEvolutionaryTest {

	File out = null;

	public RepairOperatorTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}


	
	/**
	 * We pass as custom operator one that was already included in astor (it is
	 * included in the classpath).
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85CustomOperatorSpace() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop", RemoveOp.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		// validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		// The space must have only ONE operator
		assertEquals(1, main1.getEngine().getOperatorSpace().size());
		assertEquals(RemoveOp.class.getSimpleName(),
				main1.getEngine().getOperatorSpace().values()[0].getClass().getSimpleName());

	}

	@Test
	public void testMath70CustomOperatorNavigationStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), 
				//Forced to not run 
				"-scope", "local", "-seed", "10", "-maxgen", "0", 
				"-stopfirst", "true",
				"-maxtime", "100",
				"-opselectionstrategy", FakeOperatorSelectionStrategy.class.getName(),
				

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		List<ProgramVariant> variants = main1.getEngine().getVariants();
	
		OperatorSpace opSpace = ((JGenProg)main1.getEngine()).getOperatorSpace();
		assertTrue(((JGenProg)main1.getEngine()).getOperatorSelectionStrategy() instanceof FakeOperatorSelectionStrategy);
		
		for(ModificationPoint mp : variants.get(0).getModificationPoints()){
			
			SuspiciousModificationPoint smp = (SuspiciousModificationPoint) mp;
			int line = smp.getSuspicious().getLineNumber();
			if(line % 10 == 0){
				assertEquals(opSpace.getOperators().get(0), 
						((JGenProg)main1.getEngine()).getOperatorSelectionStrategy().getNextOperator(smp)					);
			}else
				assertEquals(opSpace.getOperators().get(1), 
						((JGenProg)main1.getEngine()).getOperatorSelectionStrategy().getNextOperator(smp)					);
			
		}
		
		 
		
	}
	
	
	/**
	 * We pass as custom operator that it does not exist
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85AnyCustomOperator() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop", "MyoPeratorInvented1" };
		System.out.println(Arrays.toString(args));
		try {
			main1.execute(args);
			fail("Astor cannot work without operators");
		} catch (Exception e) {// Expected
		}
	}

	/**
	 * We pass as custom operator one that was already included in astor (it is
	 * included in the classpath) but it does not repair the bug
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85_Custom_Operator_NoFix() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "1", "-scope", "package", "-seed", "10", "-customop", ReplaceOp.class.getCanonicalName() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		// The space must have only ONE operator
		assertEquals(1, main1.getEngine().getOperatorSpace().size());
		assertEquals(ReplaceOp.class.getSimpleName(),
				main1.getEngine().getOperatorSpace().values()[0].getClass().getSimpleName());

	}

	/**
	 * Two custom operators, one repair the bug.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85TwoCustomOperators() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-customop",
				(InsertAfterOp.class.getCanonicalName() + File.pathSeparator + RemoveOp.class.getCanonicalName()) };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		// The space must have Two operators
		assertEquals(2, main1.getEngine().getOperatorSpace().size());
		assertEquals(InsertAfterOp.class.getSimpleName(),
				main1.getEngine().getOperatorSpace().values()[0].getClass().getSimpleName());
		assertEquals(RemoveOp.class.getSimpleName(),
				main1.getEngine().getOperatorSpace().values()[1].getClass().getSimpleName());

		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}

	@Test
	public void testMath85_CustomBasicIngredientStrategy() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed", "10", "-ingredientstrategy",
				EfficientIngredientStrategy.class.getCanonicalName()
				};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

	}
	
	
	
}
