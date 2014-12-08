package fr.inria.astor.core.loop.mutation.repairSpace;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.loop.mutation.mutants.core.IMutator;
import fr.inria.astor.core.loop.mutation.mutants.core.IProbabilityItem;

public class ProbabilisticRepairSpace extends MutantRepairSpace {

	List<IProbabilityItem> mutators = new ArrayList<IProbabilityItem>();
	//Logger logger = Logger.getLogger(RepairWithProbabilisticRepairSpaceTest.class.getName());
	private  Logger logger = Logger.getLogger(Thread.currentThread().getName());


	@Override
	public IMutator getNextMutator() {

		initMutations();

		IProbabilityItem toreturn = null;
		if (index_mutant < mutators.size()) {
			toreturn = mutators.get(index_mutant);
			logger.info("Selecting (" + index_mutant + ") " + toreturn);
			index_mutant++;
		}
		return toreturn;

	}

	public boolean initMutations() {
		if (index_mutant == 0) {
			Collections.sort(mutators, new SortByProb());
			return true;
		}
		return false;
	}

	public boolean validateProbabilities() {
		double d = 0;
		for (IProbabilityItem mut : mutators) {
			d = +mut.getProbability();
		}
		return (Math.round(d) == 1);
	}

	public void addMutator(IProbabilityItem mut) {
		mutators.add(mut);
	}

	class SortByProb implements Comparator<IProbabilityItem> {

		public int compare(IProbabilityItem o1, IProbabilityItem o2) {
			// Desc
			return Double.compare(o2.getProbability(), o1.getProbability());
		}
	}

}
