package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.lille.repair.common.config.NopolContext;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtInvocationImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class StaSynthBuilder {

	@SuppressWarnings("rawtypes")
	public List synthesizer(List<CtExpression> singleExpressions, List<CtVariableRead> cteVarReadList) {
		NopolContext fakeContext = fakeContext();

		List<CtInvocation> invocations = this.createtinvocations(cteVarReadList);

		List<CtExpression> notnull = createNotNullExpr(cteVarReadList);

		for (CtExpression notNullExpression : notnull) {
			// System.out.println("not null: " + notNullExpression);
		}

		singleExpressions.addAll(invocations);

		long maxCombinerTime = TimeUnit.SECONDS.toMillis(10);
		// Passing expression from Collection to Candidates...

		for (CtExpression expr : singleExpressions) {
			// System.out.println(expr + " type " + expr.getType());
		}
		System.out.println("Starting calculating expressions");
		List<CtExpression> allCombinedExpression = new ArrayList<>();
		DataCombinerSpoon combiner = new DataCombinerSpoon();

		combiner.addCombineListener(new DataCombinatorListenerSpoon(allCombinedExpression));

		combiner.combine(singleExpressions, maxCombinerTime, fakeContext);

		List<CtExpression> result = new ArrayList<>();

		result.addAll(singleExpressions);
		result.addAll(notnull);
		result.addAll(allCombinedExpression);

		System.out.println("End calculating space " + result.size());
		return result;

	}

	private NopolContext fakeContext() {
		NopolContext nopolContext = new NopolContext();
		// nopolContext.setCollectOnlyUsedMethod(ConfigurationProperties.getPropertyBool("collectonlyusedmethod"));
		nopolContext.setCollectOnlyUsedMethod(false);

		nopolContext.setDataCollectionTimeoutInSecondForSynthesis(5);
		nopolContext.setOnlyOneSynthesisResult(false);
		return nopolContext;
	}

	static CtExpression nullExp = null;

	static {
		nullExp = MutationSupporter.getFactory().createCodeSnippetExpression("null");
	}

	public List<CtExpression> createNotNullExpr(List<CtVariableRead> cteVarReadList) {

		List<CtExpression> result = new ArrayList<>();

		for (CtVariableRead varr : cteVarReadList) {

			if (!varr.getType().isPrimitive()) {
				CtBinaryOperatorImpl binex = new CtBinaryOperatorImpl<>();
				binex.setKind(BinaryOperatorKind.NE);
				binex.setLeftHandOperand(varr);
				binex.setRightHandOperand(nullExp);
				result.add(binex);
			}
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<CtInvocation> createtinvocations(List<CtVariableRead> variables) {

		List<CtInvocation> invocations = new ArrayList();

		for (CtVariableRead aVar : variables) {

			for (CtExecutableReference<?> executable : aVar.getType().getAllExecutables()) {

				if (executable.getExecutableDeclaration() instanceof CtMethod) {

					CtMethod method = (CtMethod) executable.getExecutableDeclaration();
					// FOR the moment, only public
					if ((method.getVisibility() == null || method.getVisibility().equals(ModifierKind.PUBLIC))
							&& !"Object".equals(method.getDeclaringType().getSimpleName())) {

						if (method.getParameters().size() == 0) {
							CtInvocationImpl inv = new CtInvocationImpl<>();
							inv.setTarget(aVar);
							inv.setExecutable(executable);
							invocations.add(inv);
							// System.out.println("--izeroarg->" + inv);
						} else {
							try {
								List<List<CtExpression>> args = createAllPossibleArgsListForMethod(method, variables);

								if (args != null && args.size() > 0) {

									List<List<CtExpression>> allComb = combine(args);
									// System.out.println("--comb--args->" + allComb);

									for (List<CtExpression> list : allComb) {
										CtInvocationImpl inv = new CtInvocationImpl<>();
										inv.setTarget(aVar);
										inv.setExecutable(executable);
										inv.setArguments(list);
										// System.out.println("--iplusarg->" + inv);
										invocations.add(inv);
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}

				}
			}
		}

		return invocations;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private List<List<CtExpression>> createAllPossibleArgsListForMethod(CtMethod method,
			List<CtVariableRead> variables) {
		try {

			List<CtParameter> argumentTypes = method.getParameters();
			List<List<CtExpression>> argumentCandidates = new ArrayList<>();
			for (int j = 0; j < argumentTypes.size(); j++) {
				CtParameter par = argumentTypes.get(j);

				List<CtExpression> compatiblePar = new ArrayList<>();

				for (CtVariableRead ctVariableRead : variables) {

					if (ctVariableRead.getType().isSubtypeOf(par.getType())) {
						compatiblePar.add(ctVariableRead);
					}

				}
				argumentCandidates.add(compatiblePar);
			}
			return argumentCandidates;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<List<CtExpression>> combine(List<List<CtExpression>> expressions) {
		if (expressions.size() == 0) {
			return expressions;
		}
		if (expressions.size() == 2) {
			return combine(expressions.get(0), expressions.get(1));
		}
		List<List<CtExpression>> lists = new ArrayList<>();
		if (expressions.size() == 1) {
			for (int i = 0; i < expressions.get(0).size(); i++) {
				CtExpression expression = expressions.get(0).get(i);
				List<CtExpression> list = new ArrayList<>(1);
				list.add(expression);
				lists.add(list);
			}
			return lists;
		}
		List<CtExpression> last = expressions.get(expressions.size() - 1);
		for (int i = 0; i < last.size(); i++) {
			CtExpression a = last.get(i);
			List<List<CtExpression>> b = combine(expressions.subList(0, expressions.size() - 2));
			for (int j = 0; j < b.size(); j++) {
				List<CtExpression> expressionList = b.get(j);
				expressionList.add(a);
				lists.add(expressionList);
			}
		}
		return lists;
	}

	private List<List<CtExpression>> combine(List<CtExpression> a, List<CtExpression> b) {
		List<List<CtExpression>> lists = new ArrayList<>();
		for (int i = 0; i < a.size(); i++) {
			CtExpression expression = a.get(i);
			for (int j = 0; j < b.size(); j++) {
				CtExpression expression1 = b.get(j);
				if (expression.equals(expression1))
					continue;
				List<CtExpression> list = new ArrayList<>();
				list.add(expression);
				list.add(expression1);
				lists.add(list);
			}
		}
		return lists;
	}
}
