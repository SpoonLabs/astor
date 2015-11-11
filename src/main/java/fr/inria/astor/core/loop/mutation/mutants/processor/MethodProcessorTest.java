package fr.inria.astor.core.loop.mutation.mutants.processor;

import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import fr.inria.astor.core.manipulation.MutationSupporter;



public class MethodProcessorTest extends AbstractProcessor<CtMethod<?>> {

	public void process(CtMethod<?> method) {
		CtStatement newStatement = MutationSupporter.getFactory().Code().createCodeSnippetStatement("System.out.println(\"insertedF\")");
		CtStatement newStatement2 = MutationSupporter.getFactory().Core().clone(newStatement);
		
		List<CtStatement> statements = method.getBody().getStatements();	
		
		statements.add(0,newStatement);
	}

}
