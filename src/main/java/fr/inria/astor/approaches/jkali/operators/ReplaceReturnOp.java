package fr.inria.astor.approaches.jkali.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ReplaceReturnOp extends InsertBeforeOp {
	
	final Set<String> prim = new HashSet<String>(Arrays.asList("byte","Byte", "long","Long", "int","Integer", "float","Float",  "double","Double", "short","Short", "char", "Character"));

	public List<OperatorInstance> createOperatorInstance(SuspiciousModificationPoint modificationPoint){
		List<OperatorInstance> instances = new ArrayList<>();
		
		OperatorInstance opInsertReturn = new OperatorInstance(modificationPoint, this,
				modificationPoint.getCodeElement(), createReturn(modificationPoint.getCodeElement()));
		instances.add(opInsertReturn);
		return instances;
	}
	

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private CtElement createReturn(CtElement rootElement) {
		CtMethod method = rootElement.getParent(CtMethod.class);

		if (method == null) {
			log.info("Element without method parent");
			return null;
		}
		// We create the "if(true)"{}
		CtIf ifReturn = MutationSupporter.getFactory().Core().createIf();
		CtExpression ifTrueExpression = MutationSupporter.getFactory().Code()
				.createCodeSnippetExpression("true");
		ifReturn.setCondition(ifTrueExpression);

		// Now we create the return statement
		CtReturn<?> returnStatement = null;
		CtTypeReference typeR = method.getType();
		if (typeR == null || "void".equals(typeR.getSimpleName())) {
			returnStatement = MutationSupporter.getFactory().Core().createReturn();
		} else {
			String codeExpression = "";
			if (prim.contains(typeR.getSimpleName())) {
				codeExpression = getZeroValue(typeR.getSimpleName().toLowerCase());
			} else if(typeR.getSimpleName().toLowerCase().equals("boolean")){
				codeExpression = "false";
			} else {
				codeExpression = "null";
			}
			CtExpression returnExpression = MutationSupporter.getFactory().Code()
					.createCodeSnippetExpression(codeExpression);
			returnStatement = MutationSupporter.getFactory().Core().createReturn();
			returnStatement.setReturnedExpression(returnExpression);
		}
		// Now, we associate if(true){return [...]}
		ifReturn.setThenStatement(returnStatement);
		return ifReturn;

	}

	private String getZeroValue(String simpleName) {
		if("float".equals(simpleName))
			return "0f";
		if("long".equals(simpleName))
			return "0l";
		if("double".equals(simpleName))
			return "0d";
		return "0";
	}
	

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {
		return false;
	}

	@Override
	public boolean applyChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		return false;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		return false;
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return false;
	}


}
