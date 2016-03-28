package fr.inria.astor.core.loop.mutants.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;

public class MethodCallParameterProcessor extends AbstractProcessor<CtMethod<?>> {

	Logger logger = Logger.getLogger(MethodCallParameterProcessor.class.getName());

	@SuppressWarnings("rawtypes")
	public void process(CtMethod<?> method) {

		System.out.println("Parameters data:");
		for (CtParameter par : method.getParameters()) {
			System.out.println(par.getType() + " " + par.getSimpleName());
		}
		System.out.println(method.getType() + " " + method.getType().getSimpleName());
		CtExecutableReference ref = method.getReference();

		CtClass parent = (CtClass) method.getParent();
		List<CtField> fields = parent.getFields();

		List<CtStatement> statements = method.getBody().getStatements();
		List[] result = new List[statements.size() + 1];
		int index = 0;
		for (CtStatement ctStatement : statements) {
			index++;

			if (ctStatement instanceof CtInvocation) {
				CtInvocation cinv = (CtInvocation) ctStatement;
				logger.info(" " + cinv);
				// List<CtInvocation> mutants =
				// getMutationRemoveParameter(cinv);
				List<CtVariable> fv = parent.getFields();
				List<CtVariable> lv = VariableResolver.getVariablesFromBlockInScope(cinv);

				List<CtInvocation> mutants = getMutationVarReplacement(cinv, lv);
				mutants.addAll(getMutationVarReplacement(cinv, fv));

				result[index] = mutants;
				//putInSwitch(mutants, index,result);
			}
		}
		// In reverse order to insert correctly
		for (int i = result.length - 1; i >= 0; i--) {
			List ri = result[i];
			if (ri != null) {
				logger.info("Inserting at line " + i + ": " + ri);
				statements.addAll(i, ri);
			}

		}

		/*
		 * CtClass classCreated =
		 * Factory.getLauchingFactory().Core().createClass();
		 * classCreated.setSimpleName("org.testMethod.CreatedClass");
		 * Factory.getLauchingFactory().Class().create(classCreated,
		 * "org.testMethod.CreatedClass");
		 */
	}

	public void putInSwitch(List elements, int pos, List[] result) {
		CtCodeSnippetStatement exp = MutationSupporter.getFactory().Code()
				.createCodeSnippetStatement("int s = 0");
		CtSwitch ct = MutationSupporter.getFactory().Core().createSwitch();
		ct.setSelector(MutationSupporter.getFactory().Code().createCodeSnippetExpression("s"));
		List cases = new ArrayList();
		ct.setCases(cases);
		//parent.getStatements().add(exp);
		//parent.getStatements().add(pos, ct);
		List lo = new ArrayList();
		lo.add(exp);
		lo.add(ct);
		result[pos] = lo;
		
		for (Object object : elements) {
			CtCase cas = MutationSupporter.getFactory().Core().createCase();
			cas.setCaseExpression(MutationSupporter.getFactory().Code().createCodeSnippetExpression("1"));
			//logger.info(ct.toString());
			List stms = new ArrayList();
			cas.setStatements(stms);
			stms.add(object);
			cases.add(cas);
		}

	}

	public List<CtInvocation> getMutationRemoveParameter(CtInvocation cinv) {
		List<CtInvocation> result = new ArrayList<CtInvocation>();

		for (int i = 0; i < cinv.getArguments().size(); i++) {

			CtExpression arg = (CtExpression) cinv.getArguments().get(i);
			CtInvocation cloneInv = (CtInvocation) MutationSupporter.getFactory().Core().clone(cinv);
			cloneInv.getArguments().remove(i);

			if (checkValidInvocation(cloneInv)) {
				result.add(cloneInv);
			}
		}
		return result;
	}

	@SuppressWarnings("rawtypes")
	public List<CtInvocation> getMutationVarReplacement(CtInvocation cinv, List<CtVariable> localVars) {
		List<CtInvocation> result = new ArrayList<CtInvocation>();

		for (int i = 0; i < cinv.getArguments().size(); i++) {

			CtExpression arg = (CtExpression) cinv.getArguments().get(i);

			for (CtVariable lv : localVars)
				if ((lv.getType().equals(arg.getType()) || lv.getType().isSubtypeOf(arg.getType()))
						&& !lv.getSimpleName().equals(arg.getSignature())) {
					CtInvocation cloneInv = (CtInvocation) MutationSupporter.getFactory().Core().clone(cinv);
					CtExpression paricloned = (CtExpression) cloneInv.getArguments().get(i);
					logger.info("Replacing: " + lv.getSignature() + " --> " + arg.getSignature());
					//CHECK: During the spoon migration to 4.3 I change the method inv to create var read
					CtVariableAccess varacces = MutationSupporter.getFactory().Code()
							.createVariableRead(lv.getReference(), false);
					/*	CtVariableAccess varacces = MutationSupporter.getFactory().Code()
							.createVariableAccess(lv.getReference(), false);*/
					paricloned.replace(varacces);
					if (checkValidInvocation(cloneInv)) {
						result.add(cloneInv);
					}

				}

		}
		return result;
	}

	private boolean checkValidInvocation(CtInvocation cloneInv) {
		// TODO Auto-generated method stub
		return true;
	}

}
