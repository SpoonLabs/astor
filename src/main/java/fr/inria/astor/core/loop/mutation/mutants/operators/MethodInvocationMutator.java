package fr.inria.astor.core.loop.mutation.mutants.operators;

import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import fr.inria.astor.core.loop.mutation.mutants.core.SpoonMutator;
import fr.inria.astor.core.loop.mutation.mutants.processor.MethodCallParameterProcessor;
import fr.inria.astor.core.manipulation.code.VariableResolver;

public class MethodInvocationMutator extends SpoonMutator{

	Logger logger = Logger.getLogger(MethodInvocationMutator.class.getName());

	
	public MethodInvocationMutator(Factory factory) {
		super(factory);
		
	}

	public void execute(Class toMutate) {
		// TODO Auto-generated method stub
		
	}

	public void execute(String toMutate) {
		// TODO Auto-generated method stub
		
	}

	public List<CtElement> execute(CtElement ctStatement ) {
				
		MethodCallParameterProcessor processor = new MethodCallParameterProcessor();
		
		if(ctStatement instanceof CtInvocation){
			CtInvocation cinv = (CtInvocation) ctStatement;
			logger.info(" "+cinv);
			//List<CtInvocation> mutants = getMutationRemoveParameter(cinv);
			//@TODO FINISH
		///	List<CtVariable> fv =  parent.getFields();
			List<CtVariable> lv =  VariableResolver.getVariablesFromBlockInScope(cinv);
			
			List<CtInvocation> mutants = processor.getMutationVarReplacement(cinv, lv );
			//mutants.addAll(processor.getMutationVarReplacement(cinv, fv ));
			
			//result[index] = mutants;
			
		}
		return null;
	}

	public String key() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setup() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int levelMutation() {
		return 1;
	}
}
