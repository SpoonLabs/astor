package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.BaseEvolutionaryTest;
import fr.inria.main.evolution.AstorMain;
/**
 * 
 * @author Matias Martinez
 *
 */
public class EvoSuiteParticularClassesTest extends BaseEvolutionaryTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}

	//@Test
		public void testM50() throws Exception{
			String command = "-mode,statement,"
						+ "-location,"+ (new File("./examples/math_50")).getAbsolutePath()
					+ ","+ "-dependencies,"+new File("./examples/libs/junit-4.8.2.jar").getAbsolutePath()
					+ ",out,"+new File(ConfigurationProperties.getProperty("workingDirectory"))
					+ ",-failing,org.apache.commons.math.analysis.solvers.BaseSecantSolver,"
					+ "-package,org.apache.commons,"
					//+ "-jvm4testexecution,/home/mmartinez/jdk1.8.0_45/bin/"
					+ "-javacompliancelevel,7,"
					+ "-maxgen,1000000,"
					+ "-seed,6001,"
					+ "-stopfirst,true,"
					+ "-scope,package,-maxtime,10,"
					+ "-population,1,"
					+ "-srcjavafolder,src/java/,"
					+ "-srctestfolder,src/test/,-binjavafolder,target/classes/,"
					+ "-bintestfolder,target/test-classes/,"
					+ "-flthreshold,0.1,"
					//+ " -validation,fr.inria.astor.core.validation.validators.RegressionValidation,"
					;
			String[] args = command.split(",");
			AstorMain main1 = new AstorMain();
			main1.execute(args);
		}
		
		@Test
		public void testM70() throws Exception{
			String command = "-mode,statement,"
					//+ "-location,"+ (new File(".")).getAbsolutePath()
					
						+ "-location,"+ (new File("./examples/math_70")).getAbsolutePath()
					+ ","+ "-dependencies,"+new File("./examples/libs/junit-4.11.jar").getAbsolutePath()
					//+ ","
					+ ",out,"+new File(ConfigurationProperties.getProperty("workingDirectory"))
					//+ "-dependencies,"+(new File("./examples/libs/")).getAbsolutePath()
					+ ",-failing,org.apache.commons.math.analysis.solvers.BisectionSolverTest,"
					+ "-package,org.apache.commons,"
					//+ "-jvm4testexecution,/home/mmartinez/jdk1.8.0_45/bin/"
					+ "-javacompliancelevel,7,"
					+ "-maxgen,1000000,"
					+ "-seed,6001,"
					+ "-stopfirst,true,"
					+ "-scope,package,-maxtime,10,"
					+ "-population,1,"
					+ "-srcjavafolder,src/java/,"
					+ "-srctestfolder,src/test/,-binjavafolder,target/classes/,"
					+ "-bintestfolder,target/test-classes/,"
					+ "-flthreshold,0.1,"
					//+ " -validation,fr.inria.astor.core.validation.validators.RegressionValidation,"
					;
			String[] args = command.split(",");
			AstorMain main1 = new AstorMain();
			main1.execute(args);
		}
	
}
