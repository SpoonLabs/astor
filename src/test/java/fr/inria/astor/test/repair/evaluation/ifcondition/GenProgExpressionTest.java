package fr.inria.astor.test.repair.evaluation.ifcondition;

import org.junit.Test;

import fr.inria.astor.test.repair.evaluation.BaseEvolutionaryTest;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.MainIFMutation;
import fr.inria.main.evolution.MainIFjGenProg;
/**
 *  This class executes the experiment from our paper if-conditional-dataset-2014.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class GenProgExpressionTest  extends BaseEvolutionaryTest{
		
	
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
			return new MainIFjGenProg();
		}
		return main;
	}
	
	@Test
	public void testExampleMath280() throws Exception{
		
		/*String dependenciespath= "examples/Math-issue-280/lib/junit-4.4.jar";
		String folder= "Math-issue-280";
		String failing= "org.apache.commons.math.distribution.NormalDistributionTest";
		File f = new File("examples/Math-issue-280/");
		String location = f.getParent();
		String regression= "org.apache.commons.math.distribution.NormalDistributionTest";
		String packageToInstrument= "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, regression, failing, dependenciespath, packageToInstrument, thfl);
		*/
		 MainIFjGenProg.main(new String[]{"-bug280"});;
		
	}
	
	@Test
	public void testExampleMath288() throws Exception{
				
		 MainIFjGenProg.main(new String[]{"-bug288"});;
		
	}
	
	@Test
	public void testExampleMath288Mutation() throws Exception{
				
		 MainIFMutation.main(new String[]{"-bug288"});;
		
	}
	
	@Test
	public void testExampleMath280Mutation() throws Exception{
				
		 MainIFMutation.main(new String[]{"-bug280"});;
		
	}
	
	
	@Test
	public void testExampleMath309() throws Exception{
		
	
		 MainIFjGenProg.main(new String[]{"-bug309"});;
		
	}
	
	@Test
	public void testExampleMath340() throws Exception{
		
	
		 MainIFjGenProg.main(new String[]{"-bug340"});;
		
	}
		
	@Test
	public void testExampleMath428() throws Exception{
		
	
		 MainIFjGenProg.main(new String[]{"-bug428"});;
		
	}
	
}
