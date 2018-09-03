package fr.inria.astor.approaches.scaffold.scaffoldgeneration.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;

public class OperatorGenerator {
	
	@SuppressWarnings("serial")
	private final static Map<String, String> primToObj = new HashMap<String, String>() {
		{
			put("int", "java.lang.Integer");
			put("double", "java.lang.Double");
			put("long", "java.lang.Long");
			put("float", "java.lang.Float");
			put("boolean", "java.lang.Boolean");
//			put("char", "Character");
		}
	};
	
	@SuppressWarnings({ "rawtypes", "static-access" })
	public static CtExpression fetchROP(CtBinaryOperator operator, MutationSupporter mutSupporter, 
			ModificationPoint modificationPoint, String type, String querytype) {
		if (type == null || type.equals(""))
			return null;
		
		CtCodeSnippetExpression typeexper = mutSupporter.getFactory().Code().createCodeSnippetExpression(type+".class");

		ArrayList<CtExpression> param = getParameter(mutSupporter.getFactory(), operator);
		param.add(1, mutSupporter.getFactory().Code().createCodeSnippetExpression(Integer.toString(0)));

		param.add(3, typeexper);
		
        CtExpression[] arr = param.toArray(new CtExpression[param.size()]);
		
		CtExecutableReference<Object> ref = mutSupporter.getFactory().Core().createExecutableReference();
		ref.setSimpleName(querytype);
		
		CtInvocation methodcall=mutSupporter.getFactory().Code().createInvocation(mutSupporter.getFactory().Code().
				createCodeSnippetExpression("fr.inria.astor.approaches.scaffold.scaffoldsynthesis.ScaffoldSynthesisEntry"),
				ref, 
				arr);
		
		CtCodeSnippetExpression invokemethod = mutSupporter.getFactory().Code().createCodeSnippetExpression(methodcall.toString()
				+".invoke()".toString());	
		
		return invokemethod;
	}

	@SuppressWarnings("rawtypes")
	private static ArrayList<CtExpression> getParameter(Factory factory, CtBinaryOperator operator) {
		
		ArrayList<CtExpression> names = new ArrayList<CtExpression>();
		names.add(factory.Code().createCodeSnippetExpression("\"" + operator.getLeftHandOperand().toString() + "\""));
		names.add(factory.Code().createCodeSnippetExpression("\"" + operator.getRightHandOperand().toString() + "\""));
		
		ArrayList<CtExpression> values = new ArrayList<CtExpression>();
		values.add(factory.Code().createCodeSnippetExpression(operator.getLeftHandOperand().toString()));
		values.add(factory.Code().createCodeSnippetExpression(operator.getRightHandOperand().toString()));
		
		ArrayList<CtExpression> paramList = new ArrayList<CtExpression>();
		CtNewArray<?> arrname = factory.Core().createNewArray().setType(factory.Type().
				createArrayReference(String.class.getTypeName()));
		
		for (CtExpression expr : names) {
			arrname.addElement(expr);
        }
		
		CtNewArray<?> arrnvalues = factory.Core().createNewArray().setType(factory.Type().
				createArrayReference(Object.class.getTypeName()));
		
		for (CtExpression expr : values) {
			arrnvalues.addElement(expr);
        }
		
		paramList.add(arrnvalues);
		paramList.add(arrname);
	
		return paramList;
	}
}
