package fr.inria.astor.test.repair.evaluation;

import java.io.File;

import org.junit.Test;

import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.MainjGenProg;
/**
 *  This class executes the experiment from our paper if-conditional-dataset-2014.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class JGenProgTest  extends BaseEvolutionaryTest{
		
	
	@Override
	public void generic(
			String location,
			String folder, 
			String regression, 
			String failing,
			String dependenciespath, 
			String packageToInstrument, double thfl) throws Exception {
	
		getMain().run(location, folder, dependenciespath, packageToInstrument, thfl, failing);
		
	}

	@Override
	public AbstractMain createMain() {
		if(main == null){
			return new MainjGenProg();
		}
		return main;
	}
	
	@Test
	public void testExampleMath280() throws Exception{
		
		String dependenciespath= "examples/Math-issue-280/lib/junit-4.4.jar";
		String folder= "Math-issue-280";
		String failing= "org.apache.commons.math.distribution.NormalDistributionTest";
		Stats currentStat= new Stats();
		File f = new File("examples/Math-issue-280/");
		String location = f.getParent();
		String regression= "org.apache.commons.math.distribution.NormalDistributionTest";
		String packageToInstrument= "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, regression, failing, dependenciespath, packageToInstrument, thfl);
		
	}
	
		
}
