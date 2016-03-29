package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class JGenProgTest extends BaseEvolutionaryTest {

	File out = null;
	
	public JGenProgTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}
	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {

		getMain().run(location, folder, dependenciespath, packageToInstrument, thfl, failing);

	}

	@Override
	public AbstractMain createMain() {
		if (main == null) {
			return new AstorMain();
		}
		return main;
	}


	@Test
	public void testExample280CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug280"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-Math-issue-280/");
	}
	
	//@Test
	public void testExample288CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug288"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-Math-issue-288/");
	}
	
	//@Test
	public void testExample340CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug340"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "Math-issue-340/");
	}
	
	//@Test
	public void testExample309CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug309"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "Math-issue-309/");
	}
	
	/**
	 * The fix is a replacement of an return statement
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed","10" };
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
	}
	
	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return statement. 
	 * + return solve(f, min, max); 
	 * - return solve(min, max);
	 * One solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	//@Ignore
	public void testMath70TwoSolutions() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package",
				"org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope" , "package",
				"-seed", "10",
				"-maxgen", "50",
			//	"-maxdate","11:00"
				};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size()>0);
		ProgramVariant variant = solutions.get(0);
		
		//Program variant ref to 
		Collection<CtType<?>> affected = variant.getAffectedClasses();
		List<CtClass> progVariant = variant.getModifiedClasses();
		assertFalse(progVariant.isEmpty());
		
		
		for (CtType aff : affected) {
			CtType ctcProgVariant = returnByName(progVariant,(CtClass) aff);
			assertNotNull(ctcProgVariant);
			assertFalse(ctcProgVariant == aff);

			//Classes from affected set must be not equals to the program variant cloned ctclasses, 
			//due to these have include the changes applied for repairing the bug.
			assertNotEquals(ctcProgVariant, aff);
			
			//Classes from affected set must be equals to the spoon model
			CtType ctspoon = returnByName(MutationSupporter.getFactory().Type().getAll(),(CtClass) aff);
			assertNotNull(ctcProgVariant);
			assertEquals(ctspoon, aff);
		}
		
	}
	
	private CtType returnByName(Collection<?> classes, CtClass target){
		
		for (Object ctClass : classes) {
			if(((CtType)ctClass).getSimpleName().equals(target.getSimpleName())){
				return (CtType) ctClass;
			}
		}
		return null;
	}
	

	
	@Test
	public void testArguments() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package",
				"org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope" , "package",
				"-seed", "10",
				 "-stopfirst","true",
				"-maxgen", "50", 
				"-saveall","false"};
		boolean correct = main1.processArguments(args);
		assertTrue(correct);
		
		String javahome = ConfigurationProperties.properties
		.getProperty("jvm4testexecution");
	
		assertNotNull(javahome);
		
		assertTrue(javahome.endsWith("bin"));
	}
	
	/**
	 * We pass as custom operator one that was already included in astor (it is included in the classpath).
	 * @throws Exception
	 */
	@Test
	public void testMath85CustomOperator() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed","10" ,
				"-customop", RemoveOp.class.getCanonicalName()
				};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size()>0);
		//The space must have only ONE operator
		assertEquals(1, main1.getEngine().getRepairActionSpace().size());
		assertEquals( RemoveOp.class.getName(),main1.getEngine().getRepairActionSpace().values()[0]);
		
	}

	/**
	 * We pass as custom operator one that was already included in astor (it is included in the classpath).
	 * @throws Exception
	 */
	@Test
	public void testMath85AnyCustomOperator() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed","10" ,
				"-customop", "MyoPeratorInvented1"
				};
		System.out.println(Arrays.toString(args));
		try{
		main1.execute(args);
		fail("Astor cannot work without operators");
		}
		catch(Exception e){//Expected
			}
		}
	
	/**
	 * We pass as custom operator one that was already included in astor (it is included in the classpath).
	 * @throws Exception
	 */
	@Test
	public void testMath85OtherOp() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package", "-seed","10" ,
				"-customop", ReplaceOp.class.getCanonicalName()
				};
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		//The space must have only ONE operator
		assertEquals(1, main1.getEngine().getRepairActionSpace().size());
		//assertEquals( ReplaceOp.class.getName(),main1.getEngine().getRepairActionSpace().values()[0]);
	
		validatePatchExistence(out + File.separator + "AstorMain-math_85/",0);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() == 0);
	
		
	}


}
