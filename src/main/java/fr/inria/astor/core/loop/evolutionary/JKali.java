package fr.inria.astor.core.loop.evolutionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

/**
 * jKali: implementation of Kali approach
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JKali extends ExhaustiveSearchEngine {

	final Set<String> prim = new HashSet<String>(Arrays.asList("byte","Byte", "long","Long", "int","Integer", "float","Float",  "double","Double", "short","Short", "char", "Character"));

	
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

		ModificationInstance opRemove = new ModificationInstance(modificationPoint, GenProgMutationOperation.DELETE,
				modificationPoint.getCodeElement(), null);
		setParentToGenOperator(opRemove, modificationPoint);
		ops.add(opRemove);

		ModificationInstance opInsertReturn = new ModificationInstance(modificationPoint, GenProgMutationOperation.INSERT_BEFORE,
				modificationPoint.getCodeElement(), createReturn(modificationPoint.getCodeElement()));
		setParentToGenOperator(opInsertReturn, modificationPoint);
		ops.add(opInsertReturn);

		if (modificationPoint.getCodeElement() instanceof CtIf) {
			ModificationInstance opChangeIftrue = new ModificationInstance(modificationPoint, GenProgMutationOperation.REPLACE,
					modificationPoint.getCodeElement(), createIf((CtIf) modificationPoint.getCodeElement(), true));
			setParentToGenOperator(opChangeIftrue, modificationPoint);
			ops.add(opChangeIftrue);

			ModificationInstance opChangeIffalse = new ModificationInstance(modificationPoint, GenProgMutationOperation.REPLACE,
					modificationPoint.getCodeElement(), createIf((CtIf) modificationPoint.getCodeElement(), false));
			setParentToGenOperator(opChangeIffalse, modificationPoint);
			ops.add(opChangeIffalse);
		}
		return ops;
	}

	/**
	 * Creates a new if from that one passed as parammeter. The next if has a
	 * condition expression expression true or false according to the variable
	 * <b>thenBranch</b>
	 * 
	 * @param ifElement
	 * @param thenBranch
	 * @return
	 */
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked", })
	private CtIf createIf(CtIf ifElement, boolean thenBranch) {

		CtIf clonedIf = this.mutatorSupporter.getFactory().Core().clone(ifElement);
		CtExpression ifExpression = this.mutatorSupporter.getFactory().Code()
				.createCodeSnippetExpression(Boolean.toString(thenBranch));

		clonedIf.setCondition(ifExpression);

		return clonedIf;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private CtElement createReturn(CtElement rootElement) {
		CtMethod method = rootElement.getParent(CtMethod.class);

		if (method == null) {
			log.info("Element without method parent");
			return null;
		}
		// We create the "if(true)"{}
		CtIf ifReturn = this.mutatorSupporter.getFactory().Core().createIf();
		CtExpression ifTrueExpression = this.mutatorSupporter.getFactory().Code()
				.createCodeSnippetExpression("true");
		ifReturn.setCondition(ifTrueExpression);

		// Now we create the return statement
		CtReturn<?> returnStatement = null;
		CtTypeReference typeR = method.getType();
		if (typeR == null || "void".equals(typeR.getSimpleName())) {
			returnStatement = this.mutatorSupporter.getFactory().Core().createReturn();
		} else {
			String codeExpression = "";
			if (prim.contains(typeR.getSimpleName())) {
				codeExpression = getZeroValue(typeR.getSimpleName().toLowerCase());
			} else if(typeR.getSimpleName().toLowerCase().equals("boolean")){
				codeExpression = "false";
			} else {
				codeExpression = "null";
			}
			CtExpression returnExpression = this.mutatorSupporter.getFactory().Code()
					.createCodeSnippetExpression(codeExpression);
			returnStatement = this.mutatorSupporter.getFactory().Core().createReturn();
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

}
