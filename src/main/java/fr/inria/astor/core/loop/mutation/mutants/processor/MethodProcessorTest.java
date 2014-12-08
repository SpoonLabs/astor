package fr.inria.astor.core.loop.mutation.mutants.processor;

import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.FactoryImpl;



public class MethodProcessorTest extends AbstractProcessor<CtMethod<?>> {

	public void process(CtMethod<?> method) {
		CtStatement newStatement = FactoryImpl.getLauchingFactory().Code().createCodeSnippetStatement("System.out.println(\"insertedF\")");
		CtStatement newStatement2 = FactoryImpl.getLauchingFactory().Core().clone(newStatement);
		
		List<CtStatement> statements = method.getBody().getStatements();	
		
		statements.add(0,newStatement);
	}

}
