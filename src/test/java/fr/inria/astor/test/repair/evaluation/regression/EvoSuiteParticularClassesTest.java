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
					+ ",-failing,org.apache.commons.math.analysis.solvers.BisectionSolverTest,"
					+ "-package,org.apache.commons,"
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
					+ " -validation,fr.inria.astor.core.validation.validators.RegressionValidation,"
					;
			String[] args = command.split(",");
			AstorMain main1 = new AstorMain();
			main1.execute(args);
		}
		
		//@Test
		public void testTime11() throws Exception{
			
	//		command line arguments: [-mode  statement  -location  .  -id  Time  -dependencies  lib/  -failing  org.joda.time.tz.TestCompiler:  -package  org.joda  -jvm4testexecution  /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/  -jvm4evosuitetestexecution  /home/mmartinez/jdk1.8.0_45/bin/  -javacompliancelevel  5  -maxgen  1000000  -seed  10  -stopfirst  false  -scope  local  -maxtime  120  -population  1  -srcjavafolder  src/main/java/  -srctestfolder  src/test/java/  -binjavafolder  target/classes/  -bintestfolder  target/test-classes/  -flthreshold  0.1  -validation  fr.inria.astor.core.validation.validators.RegressionValidation  -evosuitetimeout  300  -ignoredtestcases  org.apache.commons.lang.LocaleUtilsTest]

			String command = "-mode,statement,"
				
						+ "-location,"+ (new File("./examples/time_11")).getAbsolutePath()
					+ ","+ "-dependencies,"+new File("./examples/libs/junit-3.8.2.jar").getAbsolutePath()
						+File.pathSeparator+ new File("/Users/matias/.m2/repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar").getAbsolutePath()
					//+ ","
					+ ",out,"+new File(ConfigurationProperties.getProperty("workingDirectory"))
					+ ",-failing,org.joda.time.tz.TestCompiler,"
					+ "-package,org.joda,"
					+ "-javacompliancelevel,7,"
					+ "-maxgen,1000000,"
					+ "-seed,6001,"
					+ "-stopfirst,true,"
					+ "-scope,package,-maxtime,10,"
					+ "-population,1,"
					+ "-srcjavafolder,src/main/java/,"
					+ "-srctestfolder,src/test/java/,"
					+ "-binjavafolder,target/classes/,"
					+ "-bintestfolder,target/test-classes/,"
					+ "-flthreshold,0.1,"
					//+ " -validation,fr.inria.astor.core.validation.validators.RegressionValidation,"
					;
			String[] args = command.split(",");
			AstorMain main1 = new AstorMain();
			main1.execute(args);
			
		}
	
}
