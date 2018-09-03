package fr.inria.astor.approaches.scaffold.scaffoldgeneration.generator;

import java.util.Set;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ExpressionGenerator {
	
	private final static Set<String> primitive = new HashSet<String>(Arrays.asList("int", "Integer", "String", 
			"double", "Double", "byte", "Byte", "short", "Short", "long", "Long", "boolean", "Boolean","char"));
	
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

	@SuppressWarnings("unused")
	private static boolean isPrimitive = false;

	@SuppressWarnings({ "static-access", "rawtypes" })
	public static CtExpression fetchEXP(MutationSupporter mutSupporter, ModificationPoint modificationPoint, String type, String queryname) {
		isPrimitive = primitive.contains(type);
		return fetchExpressionOnType(mutSupporter.getFactory(), modificationPoint.getContextOfModificationPoint(), type, queryname, false);
	}
	
	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	public static CtExpression fetchENUM(CtEnum enumread, MutationSupporter mutSupporter, String type, String queryname) {
		List<CtVariable> values =(List<CtVariable>) enumread.getEnumValues();
		return fetchExpressionOnType(mutSupporter.getFactory(), values, type, queryname, true);
	}

	@SuppressWarnings("rawtypes")
	private static CtExpression fetchExpressionOnType(Factory factory, List<CtVariable> visiablevars, String type, String query, Boolean whetherEnum) {
		if (type == null || type.equals(""))
			return null;
		CtCodeSnippetExpression typeexper = factory.Code().createCodeSnippetExpression(type+".class");
		ArrayList<CtExpression> param = getParameter(factory, visiablevars, type, whetherEnum);
		
		param.add(1, factory.Code().createCodeSnippetExpression(Integer.toString(0)));

		param.add(3, typeexper);
		
		CtExpression[] arr = param.toArray(new CtExpression[param.size()]);
		
		CtExecutableReference<Object> ref = factory.Core().createExecutableReference();
		ref.setSimpleName(query);
		
		CtInvocation methodcall=factory.Code().createInvocation(factory.Code().createCodeSnippetExpression("fr.inria.astor.approaches.scaffold.scaffoldsynthesis.ScaffoldSynthesisEntry"),
				ref, 
				arr);
				
		String castType = primToObj.containsKey(type) ? primToObj.get(type) : type;  
		CtCodeSnippetExpression castedexp = factory.Code().createCodeSnippetExpression("(" + castType+ ")"+"("+methodcall.toString()+
				".invoke()".toString()+")");
		return castedexp;
	}

	@SuppressWarnings("rawtypes")
	public static ArrayList<CtExpression> getParameter(Factory factory, List<CtVariable> visiablevars, String type, Boolean whetherEnum) {
		
		ArrayList<CtExpression> paramList = new ArrayList<CtExpression>();
		ArrayList<CtExpression> names = new ArrayList<CtExpression>();
		ArrayList<CtExpression> values = new ArrayList<CtExpression>();

		if(whetherEnum) {
		   for (CtVariable var: visiablevars) {
			  names.add(factory.Code().createCodeSnippetExpression("\"" + var.getSimpleName() + "\""));
			  values.add(factory.Code().createCodeSnippetExpression(type+"."+var.getSimpleName()));
		   }
		} else {
			for (CtVariable var: visiablevars) {
			   names.add(factory.Code().createCodeSnippetExpression("\"" + var.getSimpleName() + "\""));
			   values.add(factory.Code().createCodeSnippetExpression(var.getSimpleName()));
			}
		}
		
		CtNewArray<?> arrnames = factory.Core().createNewArray().setType(factory.Type().
				createArrayReference(String.class.getTypeName()));
		
		for (CtExpression expr : names) {
			arrnames.addElement(expr);
        }
		
		CtNewArray<?> arrnvalues = factory.Core().createNewArray().setType(factory.Type().
				createArrayReference(Object.class.getTypeName()));
		
		for (CtExpression expr : values) {
			arrnvalues.addElement(expr);
        }
		
		paramList.add(arrnvalues);
		paramList.add(arrnames);
	
		return paramList;
	}
}
