package fr.inria.astor.core.loop.evolutionary;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.taxonomy.MutationExpression;
import fr.inria.astor.core.entities.taxonomy.Operator;
import fr.inria.astor.core.loop.mutation.mutants.core.MutantCtElement;
import fr.inria.astor.core.loop.mutation.mutants.operators.LogicalBinaryOperatorMutator;
import fr.inria.astor.core.loop.mutation.mutants.operators.MutatorComposite;
import fr.inria.astor.core.loop.mutation.mutants.operators.NegationUnaryOperatorConditionMutator;
import fr.inria.astor.core.loop.mutation.mutants.operators.RelationalBinaryOperatorMutator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;

/**
 * New version of MutRepair, extending from the ExhaustiveSearchEngine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class MutRepair extends ExhaustiveSearchEngine {

	MutatorComposite mutatorBinary = null;

	public MutRepair(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.mutatorBinary = new MutatorComposite(mutatorExecutor.getFactory());
		this.mutatorBinary.getMutators().add(new RelationalBinaryOperatorMutator(mutatorExecutor.getFactory()));
		this.mutatorBinary.getMutators().add(new LogicalBinaryOperatorMutator(mutatorExecutor.getFactory()));
		this.mutatorBinary.getMutators().add(new NegationUnaryOperatorConditionMutator(mutatorExecutor.getFactory()));

	}


	/**
	 * Creates the Kali operators 1) Delete statement 2) Insert return 3) change
	 * if condition to true/false.
	 * 
	 * @param gen
	 * @return
	 * @throws IllegalAccessException
	 */
	protected List<GenOperationInstance> createOperators(GenSuspicious genSusp) {
		List<GenOperationInstance> ops = new ArrayList<>();

		if (!(genSusp.getCodeElement() instanceof CtIf)) {
			// logger.error(".....The pointed Element is Not a statement");
			return null;
		}
		CtIf targetIF = (CtIf) genSusp.getCodeElement();

		List<MutantCtElement> mutations = getMutants(targetIF);

		for (MutantCtElement mutantCtElement : mutations) {
			GenOperationInstance opInstance;
			try {
				opInstance = createOperationForGen(genSusp, mutantCtElement);
				if (opInstance != null)
					ops.add(opInstance);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			
		}

		return ops;
	}

	public List<MutantCtElement> getMutants(CtIf targetIF) {
		List<MutantCtElement> mutations = null;
		mutations = this.mutatorBinary.execute(targetIF.getCondition());
		return mutations;
	}

	protected GenOperationInstance createOperationForGen(Gen gen, MutantCtElement fix) throws IllegalAccessException {
		Gen genSusp = gen;

		Operator operationType = MutationExpression.REPLACE;

		if (!(genSusp.getCodeElement() instanceof CtIf)) {
			// logger.error(".....The pointed Element is Not a statement");
			return null;
		}
		CtIf targetIF = (CtIf) genSusp.getCodeElement();

		CtElement cpar = targetIF.getParent();

		if ((cpar == null)) {
			return null;
		}

		GenOperationInstance operation = new GenOperationInstance();
		operation.setOriginal(targetIF.getCondition());
		operation.setOperationApplied(operationType);
		operation.setGen(genSusp);

		List<MutantCtElement> mutations = getMutants(targetIF);

		log.debug("mutations: (" + mutations.size() + ") " + mutations);
		if (mutations == null || mutations.size() == 0) {
			return null;
		}
		operation.setModified(fix.getElement());

		return operation;
	}

}
