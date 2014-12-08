package fr.inria.astor.core.loop.mutation.repairSpace;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.loop.mutation.mutants.core.IMutator;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MutantRepairSpace {

	//Logger logger = Logger.getLogger(MutantRepairSpace.class.getName());
	Logger logger = Logger.getLogger(Thread.currentThread().getName());

	
	List<IMutator> mutators = new ArrayList<IMutator>();

	public int index_mutant = 0;

	//MutExecutor me = new MutExecutor();

	public MutantRepairSpace() {
	}

	
	

	/**
	 * Return next Mutator. It selects the mutation using FIFO from the mutation
	 * List
	 * 
	 * @return
	 */
	public IMutator getNextMutator() {
		IMutator toreturn = null;
		if (index_mutant < mutators.size()) {
			toreturn = mutators.get(index_mutant);
			index_mutant++;
		}
		return toreturn;
	}

	public void addMutator(IMutator mut) {
		mutators.add(mut);
	}

	public int numberIteraction(List<MutResult> result) {
		int r = 0;
		for (MutResult mutResult : result) {
			r += mutResult.result.keySet().size();
		}
		return r;
	}

}
