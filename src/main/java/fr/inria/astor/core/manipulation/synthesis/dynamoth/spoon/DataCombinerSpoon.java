package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.combination.Operator;
import fr.inria.lille.repair.expression.combination.unary.UnaryExpression;
import fr.inria.lille.repair.expression.combination.unary.UnaryExpressionImpl;
import fr.inria.lille.repair.expression.combination.unary.UnaryOperator;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;
import spoon.support.reflect.code.CtUnaryOperatorImpl;

/**
 * Created by Thomas Durieux on 12/03/15.
 * 
 * modified by Matias Martinez
 */
public class DataCombinerSpoon {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public static int maxDepth = ConfigurationProperties.getPropertyInt("synthesis_depth");

	private final List<CombineListenerSpoon> listeners = new ArrayList<>();
	private boolean stop = false;
	private long startTime;
	private long maxTime;
	private long executionTime;
	private NopolContext nopolContext;

	private Map<Class, CtClass> reifiedClasses = new HashMap<>();

	protected int max_number_combinations;

	public List<CtExpression> combine(List<CtExpression> candidates, long maxTime, NopolContext nopolContext) {
		List<Operator> operators = new ArrayList<>();
		operators.addAll(Arrays.asList(UnaryOperatorSpoon.values()));
		operators.addAll(Arrays.asList(BinaryOperatorSpoon.values()));

		return combine(candidates, maxTime, nopolContext, operators);
	}

	public List<CtExpression> combine(List<CtExpression> candidates, long maxTime, NopolContext nopolContext,
			List<Operator> operators) {
		this.nopolContext = nopolContext;
		max_number_combinations = ConfigurationProperties.getPropertyInt("max_synthesis_step");
		maxDepth = nopolContext.getSynthesisDepth();
		this.maxTime = maxTime;
		this.startTime = System.currentTimeMillis();
		executionTime = System.currentTimeMillis() - startTime;
		logger.debug("[combine] start on " + candidates.size() + " elements");

		//
		List<CtExpression> result = new ArrayList<CtExpression>();
		result.addAll(candidates);
		List<CtExpression> lastTurn = new ArrayList<>();
		// puts all
		lastTurn.addAll(candidates);

		executionTime = System.currentTimeMillis() - startTime;

		for (int i = 0; i < maxDepth - 1 && !stop /* && executionTime <= maxTime */; i++) {
			List<CtExpression> expr = newCombiner(lastTurn, operators);
			lastTurn.addAll(expr);
			executionTime = System.currentTimeMillis() - startTime;
		}
		// result.addAll(lastTurn);
		logger.debug("[combine] end " + lastTurn.size() + " evaluated elements");
		return result;
	}

	private List<CtExpression> newCombiner(final List<CtExpression> toCombine, final List<Operator> operators) {

		final List<CtExpression> result = new ArrayList<>();

		for (Operator operator : operators) {

			int nbExpression = operator.getTypeParameters().size();
			CombinationSpoon combination = new CombinationSpoon(toCombine, operator, nbExpression, reifiedClasses);
			while (!combination.isEnd(this.stop)) {
				List<CtExpression> expressions = combination.perform(this.stop);
				CtExpression binaryExpression = create(operator, expressions, nopolContext);
				if (binaryExpression != null)
					add(result, binaryExpression);

				if (operator instanceof BinaryOperatorSpoon && !((BinaryOperatorSpoon) operator).isCommutative()) {

					// Modified:

					binaryExpression = create(operator, Arrays.asList(expressions.get(1), expressions.get(0)),
							nopolContext);
					if (binaryExpression != null)
						add(result, binaryExpression);
				}

				if (nopolContext.isOnlyOneSynthesisResult() || result.size() > max_number_combinations) {
					System.out.println("Arriving max number of combinations done.");
					this.stop = true;
					return result;
				}
			}

		}
		return result;
	}

	private void add(final List<CtExpression> result, CtExpression binaryExpression) {
		boolean makesence = true;
		try {
			if (binaryExpression instanceof CtBinaryOperator) {
				makesence = isExpressionMakeSense((CtBinaryOperator) binaryExpression);
			}

			if (makesence) {
				this.callListener(binaryExpression);
				result.add(binaryExpression);
			}
		} catch (Exception e) {
			// the hash is different, so it throws an exception
			// System.err.println("error checking");
			// System.err.println(binaryExpression);
			// e.printStackTrace();
		}
	}

	//// Added by MM from Combination Factory

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CtExpression create(Operator operator, List<CtExpression> expressions, NopolContext nopolContext) {
		switch (expressions.size()) {
		case 1:
			// return create((UnaryOperator) operator, expressions.get(0), nopolContext);
			CtUnaryOperatorImpl unaryOp = new CtUnaryOperatorImpl<>();
			unaryOp.setKind(((UnaryOperatorSpoon) operator).getOp());
			unaryOp.setOperand(expressions.get(0));
			Class aClassReturnOp1 = operator.getReturnType();
			CtClass ctofClass1 = getCtClassFromClass(aClassReturnOp1);
			// Put the type
			if (ctofClass1 != null)
				unaryOp.setType(ctofClass1.getReference());
			else {
				System.out.println("We could not determine the ct class of " + aClassReturnOp1.getSimpleName());
			}
			return unaryOp;
		case 2:

			if (expressions.get(0) instanceof CtLiteral && expressions.get(1) instanceof CtLiteral)
				return null;
			// return create((BinaryOperator) operator, expressions.get(0),
			// expressions.get(1), nopolContext);

			CtBinaryOperatorImpl op = new CtBinaryOperatorImpl<>();
			op.setKind(((BinaryOperatorSpoon) operator).getOpKind());
			op.setLeftHandOperand((CtExpression) expressions.get(0));
			op.setRightHandOperand((CtExpression) expressions.get(1));

			//
			Class aClassReturnOp = operator.getReturnType();
			CtClass ctofClass = getCtClassFromClass(aClassReturnOp);
			// Put the type
			if (ctofClass != null)
				op.setType(ctofClass.getReference());
			else {
				System.out.println("We could not determine the ct class of " + aClassReturnOp.getSimpleName());
			}
			return op;
		default:
			throw new IllegalArgumentException(
					"Combination expression with " + expressions.size() + " is not supported");
		}
	}

	private CtClass getCtClassFromClass(Class aClass) {
		CtClass ctofClass = null;
		if (reifiedClasses.containsKey(aClass)) {
			ctofClass = reifiedClasses.get(aClass);
		} else {
			ctofClass = (CtClass) new TypeFactory().get(aClass);
			reifiedClasses.put(aClass, ctofClass);
		}
		return ctofClass;
	}

	public static UnaryExpression create(UnaryOperator operator, Expression first, NopolContext nopolContext) {
		return new UnaryExpressionImpl(operator, first, nopolContext);
	}

	public void addCombineListener(CombineListenerSpoon combineListener) {
		this.listeners.add(combineListener);
	}

	private boolean callListener(CtExpression expression) {
		for (CombineListenerSpoon combineListener : listeners) {
			if (combineListener.check(expression)) {
				if (nopolContext.isOnlyOneSynthesisResult()) {
					stop = true;
				}
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public boolean isExpressionMakeSense(CtBinaryOperator binop) {

		CtTypeReference param1 = binop.getLeftHandOperand().getType().box();
		CtTypeReference param2 = binop.getRightHandOperand().getType().box();

		// check the compatibility with the operator
		if (!param1.isSubtypeOf(param2) || !param2.isSubtypeOf(param1)) {
			return false;
		}

		switch (binop.getKind()) {
		case EQ:
			if (binop.getLeftHandOperand().equals(binop.getRightHandOperand())) {
				return false;
			}

			break;
		case NE:

			if (binop.getLeftHandOperand().equals(binop.getRightHandOperand())) {
				return false;
			}

			break;
		case AND:
		case OR:
			if (binop.getLeftHandOperand().equals(binop.getRightHandOperand())) {
				return false;
			}
			break;
		case LE:
		case LT:
			if (binop.getLeftHandOperand().equals(binop.getRightHandOperand())) {
				return false;
			}
			break;
		case PLUS:
			if (binop.getLeftHandOperand().toString().equals("0")
					|| (binop.getRightHandOperand().toString().equals("0"))) {
				return false;
			}
			break;
		case MINUS:
			if (binop.getLeftHandOperand().equals(binop.getRightHandOperand())
					|| (binop.getRightHandOperand().toString().equals("0"))) {
				return false;
			}
			break;
		case MUL:
			if (binop.getLeftHandOperand().toString().equals("0")
					|| (binop.getRightHandOperand().toString().equals("0"))) {
				return false;
			}

		case DIV:
			if (binop.getLeftHandOperand().equals(binop.getRightHandOperand())
					|| (binop.getRightHandOperand().toString().equals("0"))) {
				return false;
			}
			break;
		}
		return true;
	}

}
