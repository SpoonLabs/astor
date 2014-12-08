package fr.inria.astor.core.loop.mutation.repairSpace;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.loop.mutation.mutants.core.IMutator;
import fr.inria.astor.core.loop.mutation.mutants.core.IProbabilityItem;

public class RandomRepairSpace extends MutantRepairSpace {

	List<IProbabilityItem> mutators = new ArrayList<IProbabilityItem>();
	//Logger logger = Logger.getLogger(RepairWithProbabilisticRepairSpaceTest.class.getName());
	Logger logger = Logger.getLogger(Thread.currentThread().getName());
	

	@Override
	public IMutator getNextMutator() {
		
		initMutations();
		
		IProbabilityItem toreturn = null;
		if (index_mutant < mutators.size()) {
			toreturn = mutators.get(index_mutant);
			logger.info("Selecting ("+index_mutant+") "+toreturn);
			index_mutant++;
		}
		return toreturn;
	
	}
	
	public boolean initMutations(){
	
		return false;
	}
	
	public boolean validateProbabilities(){
		double d = 0;
		for (IProbabilityItem mut : mutators) {
			d=+mut.getProbability();
		}
		return (Math.round(d)  == 1);
	}
	
	public void addMutator(IProbabilityItem mut){
		mutators.add(mut);
	}
	
	

}
