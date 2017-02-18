package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.main.evolution.AstorMain;
/**
 * Test for MutRepair engine. (it mutates if conditions)
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class MutRepairTest extends BaseEvolutionaryTest {
	
	
	@Test
	//@Ignore
	public void testMath85issue280() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { 
				"-dependencies", dep, "-mode", "mutation", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				 "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1", 
				"-stopfirst", "true",
				"-maxtime", "15",
				"-seed","10"};
		System.out.println(Arrays.toString(args));
		AstorMain m = new AstorMain();
		m.execute(args);
		assertTrue(m.getEngine().getSolutions().size() > 0);
		//location= org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils
		//		line= 198
		//		original statement= if ((fa * fb) >= 0.0) {
	}
	
	
	@Test
	@Ignore
	public void testMath288() throws Exception {
		File out = new File("./outputMutation/");
			String[] args = new String[] {
				"-dependencies", new File("./examples/libs/junit-4.4.jar").getAbsolutePath(),
				"-mode","mutation",
				"-failing", 
				"org.apache.commons.math.optimization.linear.SimplexSolverTest", 
				"-location",new File("./examples/Math-issue-288/").getAbsolutePath(),							
				"-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", 
				"-binjavafolder", "/target/classes/", 
				"-bintestfolder","/target/test-classes/",
				"-flthreshold","0.1",
				"-out",out.getAbsolutePath(),
				"-stopfirst", "true",
				"-seed","10"
				};
		System.out.println(Arrays.toString(args));
		AstorMain main1 = new AstorMain();
		main1.execute(args);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		log.debug("Solutions "+solutions);
		assertNotNull(solutions);
		int nrsolutions = solutions.size();
		assertEquals(1, nrsolutions);
		
	}
	



	
	

}
