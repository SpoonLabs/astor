package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author matias
 *
 */
public class IntroClassTest {

	public static Logger log = Logger.getLogger(IntroClassTest.class.getName());
	
	
	@Test
	public void test_3b2376_003() throws Exception {
		String dep = new File("./examples/libs/junit-4.11.jar").getAbsolutePath();
		String[] command = new String[]{
		"-location", 
		new File("./examples/introclass/3b2376/003/").getAbsolutePath(),
		"-failing","introclassJava.median_3b2376ab_003BlackboxTest:introclassJava.median_3b2376ab_003WhiteboxTest", 
		"-dependencies", dep,
		"-seed","10",
		"-skipfaultlocalization",
		};
		AstorMain.main(command);
	}

	@Test
	public void test_3b2376_003FaultLocalization() throws Exception {
	
		
		log.debug("\nInit Skip fault localization test");
		double thfl = 0.001;
		int gensLowThr = genNumberGensForIntroClass3b2376_003(thfl,false);
		assertTrue(gensLowThr > 0);
		
		thfl=0.7d;
		int gensHighThr = genNumberGensForIntroClass3b2376_003(thfl,false);
		assertTrue(gensHighThr > 0);
		assertTrue(gensLowThr > gensHighThr);
		
		
		thfl=0.0d;
		int gensAll = genNumberGensForIntroClass3b2376_003(thfl,true);
		assertTrue(gensAll > 0);
		assertTrue(gensAll > gensLowThr);
		assertTrue(gensAll > gensHighThr);
		
		
	}

	private int genNumberGensForIntroClass3b2376_003(double thfl, boolean skipFL) throws Exception {
		String dependenciespath = new File("./examples/libs/junit-4.11.jar").getAbsolutePath() 
				+ File.pathSeparator
				+ new File("./examples/Math-0c1ef/lib/hamcrest-core-1.3.jar").getAbsolutePath();
		String projectId = "IntroClass003";
		String failing = "introclassJava.median_3b2376ab_003BlackboxTest:introclassJava.median_3b2376ab_003WhiteboxTest";
		File exampleLocation = new File("./examples/introclass/3b2376/003/");
		String location = exampleLocation.getAbsolutePath();
		String packageToInstrument = "";
		
		
		String[] command = new String[]{"-dependencies",dependenciespath,"-location",location,
				"-flthreshold",Double.toString(thfl),
				"-package",packageToInstrument,
				"-failing", failing,
				"-id",projectId,
				"-population","1",
				"-seed", "10", ((skipFL)?"-skipfaultlocalization":"")
				};
		

		AstorMain main = new AstorMain();
		
		boolean correctArguments = main.processArguments(command);
		assertTrue(correctArguments);
		
		main.initProject(location, projectId, dependenciespath, packageToInstrument, thfl, failing);

		AstorCoreEngine jgp = main.createEngine(ExecutionMode.jGenProg);
		
		Assert.assertEquals(1, jgp.getVariants().size());

		ProgramVariant variant = jgp.getVariants().get(0);
		int nroGen = variant.getModificationPoints().size();
		return nroGen;
	}
	
}
