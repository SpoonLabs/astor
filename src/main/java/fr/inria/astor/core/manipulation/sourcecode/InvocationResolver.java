package fr.inria.astor.core.manipulation.sourcecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InvocationResolver {

	static protected Logger log = Logger.getLogger(InvocationResolver.class.getName());

	/**
	 * Return all variables related to the element passed as argument
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<CtAbstractInvocation> collectInvocation(CtElement element, boolean duplicates) {
		List<CtAbstractInvocation> varaccess = new ArrayList<>();

		CtScanner sc = new CtScanner() {

			public void add(CtAbstractInvocation e) {
				if (duplicates || !varaccess.contains(e))
					varaccess.add(e);
			}

			@Override
			public <T> void visitCtInvocation(CtInvocation<T> invocation) {
				super.visitCtInvocation(invocation);
				add(invocation);
			}

			@Override
			public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
				super.visitCtConstructorCall(ctConstructorCall);
				add(ctConstructorCall);
			}

		};

		sc.scan(element);

		return varaccess;

	}

	public enum InvocationMatching {

		TARGET_IS_VARIABLE(true), TARGET_SAME_TYPE(true), SAME_SIGNATURE_FROM_DIFF_TYPE(true), TARGET_INCOMPATIBLE(
				false), CONTRUCTOR(true), NO_MATCH(false), OTHER(true);

		boolean correctness;

		public boolean isCorrect() {
			return correctness;
		}

		InvocationMatching(boolean correctness) {
			this.correctness = correctness;
		}

	}

	public static boolean fitImplicitInvocation(CtClass ctClassMP, CtAbstractInvocation inv0) {

		InvocationMatching matching = mapImplicitInvocation(ctClassMP, inv0);
		return matching.isCorrect();
	}

	@SuppressWarnings("rawtypes")
	public static InvocationMatching mapImplicitInvocation(CtClass ctClassMP, CtAbstractInvocation inv0) {
		if (inv0 instanceof CtInvocation) {
			CtInvocation invocation0 = (CtInvocation) inv0;

			CtExpression tpr = invocation0.getTarget();
			if (tpr instanceof CtThisAccess) {
				CtThisAccess<?> targetthis = (CtThisAccess) tpr;
				CtTypeReference tpref = targetthis.getType();
				if (ctClassMP.isSubtypeOf(tpref))
					return InvocationMatching.TARGET_SAME_TYPE;
				else if (chechSignatures(ctClassMP.getAllExecutables(), invocation0.getExecutable(), false)) {
					return InvocationMatching.SAME_SIGNATURE_FROM_DIFF_TYPE;
				} else {
					log.debug("Signature " + invocation0.getExecutable().getSignature());
					log.debug(
							"Not compatible: " + ctClassMP.getQualifiedName() + " with " + (tpref.getQualifiedName()));
					return InvocationMatching.TARGET_INCOMPATIBLE;
				}
			} else {
				log.debug("Explicit target " + tpr);
				return InvocationMatching.TARGET_IS_VARIABLE;
			}
		} else {

			if (inv0 instanceof CtConstructorCall) {
				return InvocationMatching.CONTRUCTOR;

			}

			return InvocationMatching.OTHER;
		}

	}

	public static boolean chechSignatures(Collection<CtExecutableReference<?>> allExecutables,
			CtExecutableReference executable, boolean constructor) {

		String signatureTarget = executable.getSignature();

		for (CtExecutableReference<?> ctExecutableReferenceOfMethod : allExecutables) {

			if (constructor && !ctExecutableReferenceOfMethod.isConstructor())
				continue;

			if (ctExecutableReferenceOfMethod.getSignature().equals(signatureTarget))
				return true;
		}

		return false;
	}

}
