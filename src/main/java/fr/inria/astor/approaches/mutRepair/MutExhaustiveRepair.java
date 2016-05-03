package fr.inria.astor.approaches.mutRepair;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * New version of MutRepair, extending from the ExhaustiveSearchEngine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class MutExhaustiveRepair extends ExhaustiveSearchEngine {

	@SuppressWarnings({ "static-access", "unchecked" })
	public MutExhaustiveRepair(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}


	/**
	 *
	 * @param gen
	 * @return
	 * @throws IllegalAccessException
	 */
	@Override
	protected List<ModificationInstance> createOperators(SuspiciousModificationPoint modificationPoint) {
		log.debug("Creating operation");
		List<ModificationInstance> ops = new ArrayList<>();
		AstorOperator[] operators = repairActionSpace.values();
		for (AstorOperator astorOperator : operators) {
			if (astorOperator.canBeAppliedToPoint(modificationPoint)) {
				List<ModificationInstance> instances = astorOperator.createModificationInstance(modificationPoint);
				if (instances.size() > 0) {
					ops.addAll(instances);
				}
			}
		}

		return ops;
	}

	

}
