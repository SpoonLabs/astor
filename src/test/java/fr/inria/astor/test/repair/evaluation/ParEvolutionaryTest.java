package fr.inria.astor.test.repair.evaluation;

import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.MainIFPar;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ParEvolutionaryTest  extends BaseEvolutionaryTest{


	@Override
	public AbstractMain createMain() {
		return new MainIFPar();
	}

	@Override
	public void generic(
			String location,
			String folder, 
			String regression, 
			String failing,
			String dependenciespath, 
			String packageToInstrument, double thfl,
			Stats currentStat) throws Exception {
		
		getMain().run(
				location, 
				folder, 
				dependenciespath, 
				currentStat,
				packageToInstrument, 
				thfl, failing);
		
		
	}

	
	
	
}
