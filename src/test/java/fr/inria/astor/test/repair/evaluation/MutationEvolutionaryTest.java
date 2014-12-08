package fr.inria.astor.test.repair.evaluation;

import java.util.List;

import fr.inria.astor.core.faultlocalization.SuspiciousCode;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.MainIFMutation;
/**
 *  This class executes the experiment from our paper if-conditional-dataset-2014.
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MutationEvolutionaryTest extends BaseEvolutionaryTest{
	
	static List<SuspiciousCode> filtercandidates = null;
	
/*	private void setUpFilters(MutationalEvolutionEngine gploop)
			throws JSAPException {
		List<AbstractFixSpaceProcessor> proc = new ArrayList<AbstractFixSpaceProcessor>();
		proc.add(new LoopExpressionFixSpaceProcessor());
		proc.add(new IFExpressionFixSpaceProcessor());
		gploop.setFixspace(new UniformRandomFixSpace(proc));
		gploop.setRepairSpace(new UniformRandomRepairOperatorSpace());
		gploop.setPopulationControler(new FitnessPopulationController());
	}*/
	
	
	//public void generic(String fixid,String regression,String failing, String dependencies, double thfl, Stats currentStat) throws Exception {
	@Override
	public void generic(
			String location,
			String folder, 
			String regression, 
			String failing,
			String dependenciespath, 
			String packageToInstrument, double thfl,
			Stats currentStat) throws Exception {
	
		getMain().run(location, folder, dependenciespath, currentStat, packageToInstrument,thfl,failing );

	}

	@Override
	public AbstractMain createMain() {
		if(main == null)
			return new MainIFMutation();
		return main;
	}


		
	
}
