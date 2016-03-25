package fr.inria.astor.approaches.jkali;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.evolutionary.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * jKali: implementation of Kali approach
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JKali extends ExhaustiveSearchEngine {

	public JKali(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	/**
	 * Creates the Kali operators 1) Delete statement 2) Insert return 3) change
	 * if condition to true/false.
	 * 
	 * @param modificationPoint
	 * @return
	 */
	protected List<ModificationInstance> createOperators(SuspiciousModificationPoint modificationPoint) {
		List<ModificationInstance> ops = new ArrayList<>();
		AstorOperator[] operators = repairActionSpace.values();
		for (AstorOperator astorOperator : operators) {
			if (astorOperator.applyToPoint(modificationPoint)) {
				List<ModificationInstance> instances = astorOperator.createModificationInstance(modificationPoint);
				if (instances.size() > 0) {
					ops.addAll(instances);
				}
			}
		}

		return ops;

	}

}
