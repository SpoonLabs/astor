package fr.inria.astor.approaches.mutRepair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.mutRepair.operators.ctmutators.LogicalBinaryOperatorMutator;
import fr.inria.astor.approaches.mutRepair.operators.ctmutators.MutatorComposite;
import fr.inria.astor.approaches.mutRepair.operators.ctmutators.NegationUnaryOperatorConditionMutator;
import fr.inria.astor.approaches.mutRepair.operators.ctmutators.RelationalBinaryOperatorMutator;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.MutantCtElement;
import fr.inria.astor.core.entities.WeightCtElement;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;

/**
 * Mutational evolution: Evolutionary engine that applies changes in If conditions
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class MutationalEvolutionaryRepair extends JGenProg {

	public static boolean uniformRandom = true;
	
	MutatorComposite mutatorBinary = null;
	
	Map<String, List<MutantCtElement>> mutantsCache = new HashMap<String, List<MutantCtElement>>();
	
	
	
	@SuppressWarnings("unchecked")
	public MutationalEvolutionaryRepair(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.mutatorBinary =  new MutatorComposite(mutatorExecutor.getFactory());
		this.mutatorBinary.getMutators().add(new RelationalBinaryOperatorMutator(mutatorExecutor.getFactory()));
		this.mutatorBinary.getMutators().add(new LogicalBinaryOperatorMutator(mutatorExecutor.getFactory()));
		this.mutatorBinary.getMutators().add(new NegationUnaryOperatorConditionMutator(mutatorExecutor.getFactory()));
	}

	
	/**
	 * Create a Gen Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * 
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */
	 @Override
	protected OperatorInstance createOperatorInstanceForPoint(ModificationPoint gen) throws IllegalAccessException {
		ModificationPoint genSusp =  gen;
							
		AstorOperator operationType = new ReplaceOp();
		
		if (!(genSusp.getCodeElement() instanceof CtIf)) {
			// logger.error(".....The pointed Element is Not a statement");
			return null;
		}
		CtIf targetIF = (CtIf) genSusp.getCodeElement();

		CtElement cpar = targetIF.getParent();
		

		//TODO: the parent not always is a block.. and we should manage them...
		if ((cpar == null /*|| !(cpar instanceof CtBlock)*/)) {
			return null;
		}
		
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(targetIF.getCondition());
		operation.setOperationApplied(operationType);
		operation.setModificationPoint(genSusp);
		
		List<MutantCtElement> mutations = getMutants(targetIF);
		
		//currentStat.sizeSpace.add(new StatSpaceSize(mutations.size(),0));
		
		log.debug("mutations: ("+mutations.size()+") "+mutations);
		if(mutations == null || mutations.size() == 0){
			return null;
		}
		CtElement fix = null;
		int max = 0;
		boolean continueSearching = true;
		while(continueSearching && max < mutations.size()){
			fix = getFixMutation(mutations);
			continueSearching = fix != null;//alreadyApplied(gen,fix, operationType);
			max++;
		} 
		if(continueSearching ){
			log.debug("All mutations were applied: no mutation left to apply");
			return null;
		}
		operation.setModified(fix);

		return operation;
	}

	protected CtElement getFixMutation(List<MutantCtElement> mutations) {
		CtElement fix = null;
	
		if(uniformRandom)
			fix = uniformRandom(mutations);
		else
			fix = weightRandom(mutations);
		return fix;
	}

	protected CtElement uniformRandom(List<MutantCtElement> mutations) {
		return mutations.get(RandomManager.nextInt(mutations.size())).getElement();
	}
	/**
	 * 
	 * @param mutations
	 * @return
	 */
	 private CtElement weightRandom(List<MutantCtElement> mutations) {
		 List<WeightCtElement> we = new ArrayList<WeightCtElement>();
			
		 double sum = 0; 
		 for (MutantCtElement mutantCtElement : mutations) {
			sum+=mutantCtElement.getProbabilistic();
			WeightCtElement w = new WeightCtElement(mutantCtElement, 0);
			w.weight = mutantCtElement.getProbabilistic();
			we.add(w);
		}
		if(sum == 0)
			return uniformRandom(mutations);
		
		for (WeightCtElement weightCtElement : we) {
			weightCtElement.weight = weightCtElement.weight / sum;
		}
	
		WeightCtElement.feedAccumulative(we);
		WeightCtElement selected =  WeightCtElement.selectElementWeightBalanced(we);
		MutantCtElement mutatnSelected = (MutantCtElement) selected.element;
		return	mutatnSelected.getElement();		 
	}

	/**
	  * Retrieve the mutants for a CtIf.
	  * If the condition were mutated before, we retrive them from a cache of mutants. 
	  * @param targetIF
	  * @return
	  */
	public List<MutantCtElement> getMutants(CtIf targetIF) {
		List<MutantCtElement> mutations = null;
		if(this.mutantsCache.containsKey(targetIF.getCondition().toString())){
			 mutations = clone(this.mutantsCache.get(targetIF.getCondition().toString()));
		}
		else{
			mutations = this.mutatorBinary.execute(targetIF.getCondition());
			mutantsCache.put(targetIF.getCondition().toString(), mutations);
		}
		return mutations;
	}
		

	private CtExpression getExpressionFromElement(CtElement element) {
		
		if(element instanceof CtExpression)
			return (CtExpression) element;
		
		if(element instanceof CtIf){
			return ((CtIf)element).getCondition();
		}
		//TODO: to continue			
		
		return null;
	}

	private List<MutantCtElement> clone(List<MutantCtElement> list) {
		List<MutantCtElement> clonedExpression = new ArrayList<MutantCtElement>();
		for (MutantCtElement mutation : list) {
			CtExpression cloned = (CtExpression) mutatorBinary.getFactory().Core().clone(mutation.getElement());
			clonedExpression.add(new MutantCtElement(cloned, mutation.getProbabilistic()));
		}
		
		return clonedExpression;
	}
	@SuppressWarnings("rawtypes")
	public void undoOperationToSpoonElement(OperatorInstance operation) {
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		try{
		fix.replace(ctst);
		}
		catch(Throwable tr){
			operation.setExceptionAtApplied((Exception) tr);
		}
	}
	
	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException 
	 */
	@Override
	protected void applyNewMutationOperationToSpoonElement(OperatorInstance operation) throws IllegalAccessException {

		boolean successful = false;
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		//
		try {
			ctst.replace((CtExpression) fix);
			successful = true;
			operation.setSuccessfulyApplied((successful));
		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}
			
	}
	
	

	

}
