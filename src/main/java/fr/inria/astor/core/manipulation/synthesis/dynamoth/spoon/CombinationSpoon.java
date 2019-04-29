package fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.inria.lille.repair.expression.combination.Operator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

public class CombinationSpoon {
	private final List<List<CtExpression>> toCombine = new ArrayList<>();
	private final Operator operator;
	private final List<Integer> positions;
	private final int nbExpression;
	private boolean isEnd = false;

	CombinationSpoon(List<CtExpression> toCombine, Operator operator, int nbExpression,
			Map<Class, CtClass> reifiedClasses) {
		// select compatible element for each parameter
		for (int i = 0; i < operator.getTypeParameters().size(); i++) {
			Class aClass = operator.getTypeParameters().get(i);
			for (int j = 0; j < toCombine.size(); j++) {
				CtExpression expression = (CtExpression) toCombine.get(j);

				CtClass ctofClass = null;
				// Type of the expression
				if (reifiedClasses.containsKey(aClass)) {
					ctofClass = reifiedClasses.get(aClass);
				} else {
					ctofClass = (CtClass) new TypeFactory().get(aClass);
					reifiedClasses.put(aClass, ctofClass);
				}
				try {
					CtTypeReference refClass = ctofClass.getReference();
					if (expression.getType().box().isSubtypeOf(refClass.box())) {
						// if (expression.getValue().isCompatibleWith(aClass)) {
						if (this.toCombine.size() < i + 1) {
							this.toCombine.add(new ArrayList<>());
						}
						this.toCombine.get(i).add(expression);
					}
				} catch (Exception e) {

					System.out.println("Error " + expression + " class " + ctofClass.getSimpleName());
					e.printStackTrace();
				}
			}
		}
		this.operator = operator;
		this.nbExpression = nbExpression;
		this.positions = new ArrayList<>(nbExpression);
		for (int i = 0; i < nbExpression; i++) {
			positions.add(0);
		}
		if (this.toCombine.isEmpty()) {
			isEnd = true;
		}
		for (List<CtExpression> expressions : this.toCombine) {
			if (expressions.isEmpty()) {
				isEnd = true;
				return;
			}
		}
	}

	public synchronized List<CtExpression> perform(boolean stop) {
		if (isEnd || stop) {
			return null;
		}
		List<CtExpression> current = new ArrayList<>();
		for (int i = 0; i < nbExpression; i++) {
			current.add(toCombine.get(i).get(positions.get(i)));
		}
		incrementPosition();
		return current;
	}

	public boolean isEnd(boolean stop) {
		return isEnd || stop;
	}

	private synchronized void incrementPosition() {
		for (int i = positions.size() - 1; i >= 0; i--) {
			Integer position = positions.get(i);
			int size = toCombine.get(i).size();
			if (position < size - 1) {
				position++;
				positions.set(i, position);
				return;
			} else {
				positions.set(i, 0);
				if (i == positions.size() - 1 && positions.size() > 1) {
					// positions.set(i, Math.min(positions.get(i - 1) +
					// 2, size - 1));
				}
				if (i == 0) {
					isEnd = true;
					return;
				}
			}
		}
	}
}