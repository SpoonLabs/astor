package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.value.Value;

/**
 * Created by Thomas Durieux on 06/03/15.
 * 
 * @author Modified by Matias Martinez
 *
 */
public class DynamothSynthesizerWOracle extends DynamothSynthesizer {

	private final class DataCombinatorListener implements DataCombinerModified.CombineListener {
		private final Candidates allCombinedNotEvaluatedExpressions;

		private DataCombinatorListener(Candidates allCombinedNotEvaluatedExpressions) {
			this.allCombinedNotEvaluatedExpressions = allCombinedNotEvaluatedExpressions;
		}

		@Override
		public boolean check(Expression expression) {

			if (!allCombinedNotEvaluatedExpressions.contains(expression)) {
				allCombinedNotEvaluatedExpressions.add(expression);
				return true;
			}
			return false;
		}
	}

	private final class NumberExpressionsByTestComparator implements Comparator<String> {
		@Override
		public int compare(String s, String t1) {
			if (values.get(t1).isEmpty()) {
				return -1;
			}
			if (values.get(s).isEmpty()) {
				return 1;
			}
			return values.get(t1).get(0).size() - values.get(s).get(0).size();
		}
	}

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public DynamothSynthesizerWOracle(DynamothSynthesisContext data) {
		super();
		this.values = data.getValues();
		this.nopolContext = data.getNopolContext();
		// Explicitly NULL
		this.oracle = null;
	}

	@Override
	public Candidates combineValues() {

		try {

			final Candidates result = new Candidates();
			List<String> collectedTests = new ArrayList<>(values.keySet());

			Collections.sort(collectedTests, new NumberExpressionsByTestComparator());
			for (int i = 0; i < collectedTests.size(); i++) {
				final String key = collectedTests.get(i);
				List<Candidates> listValue = values.get(key);
				for (Candidates expressions : listValue) {
					for (Expression expression : expressions) {
						expression.getValue().setConstant(isConstant(expression));
					}
				}
			}

			// STEP 1:
			// Recollection of Single values
			Candidates lastCollectedValues = null;
			// Key: test name
			Map<String, EvaluatedExpression> singleEvalExpression = new HashMap<>();

			// Here, we collect the single values and we create an evaluation for
			// each iteration.
			for (int i_test = 0; i_test < collectedTests.size(); i_test++) {
				final String i_test_name = collectedTests.get(i_test);
				List<Candidates> listValue = values.get(i_test_name);
				// Executions
				for (int i_execution = 0; i_execution < listValue.size(); i_execution++) {
					Candidates i_eexps = listValue.get(i_execution);
					if (i_eexps == null) {
						continue;
					}
					if (lastCollectedValues != null
							&& lastCollectedValues.intersection(i_eexps, false).size() == i_eexps.size()) {
						continue;
					}
					lastCollectedValues = i_eexps;
					if (nopolContext.isSortExpressions()) {
						Collections.sort(i_eexps, Collections.reverseOrder());
					}

					// check if one of the collected value can be a patch
					for (int j_expresionVariable = 0; j_expresionVariable < i_eexps.size(); j_expresionVariable++) {
						Expression expression = i_eexps.get(j_expresionVariable);
						if (expression == null || expression.getValue() == null) {
							continue;
						}

						EvaluatedExpression eval = null;
						if (singleEvalExpression.containsKey(expression.asPatch())) {
							eval = singleEvalExpression.get(expression.asPatch());
						} else {
							eval = new EvaluatedExpression(expression, new HashMap<String, List<Value>>());
							singleEvalExpression.put(expression.asPatch(), eval);
						}

						Map<String, List<Value>> valuesExpression = eval.getEvaluations();
						List<Value> valuePerIteration = null;
						if (valuesExpression.containsKey(i_test_name)) {
							valuePerIteration = valuesExpression.get(i_test_name);
						} else {
							valuePerIteration = new ArrayList<>();
							valuesExpression.put(i_test_name, valuePerIteration);
						}
						Value originaValue = expression.getValue();
						Value valueFromEvaluation = expression.evaluate(i_eexps);

						log.debug("---> Patch from single expression: " + expression.asPatch() + ", test " + i_test_name
								+ ", it " + i_execution + ", value at " + i_execution + ": " + valueFromEvaluation
								+ " value from getValue " + originaValue);
						valuePerIteration.add(valueFromEvaluation);
					}
				}
			}

			result.addAll(singleEvalExpression.values());

			// STEP 1: Evaluating single expression e.g., variables
			Candidates allSingleExpressions = new Candidates();
			for (int i_test = 0; i_test < collectedTests.size(); i_test++) {
				final String i_test_name = collectedTests.get(i_test);
				for (Expression combinedExpression : singleEvalExpression.values()) {
					EvaluatedExpression ev = collectEvaluationForAllExecutions(i_test_name, combinedExpression);
					allSingleExpressions.add(ev);
				}
			}

			log.debug("All single expression: " + allSingleExpressions.size());

			// STEP 2 : COMBINATION of Expressions

			Candidates allCombinedNotEvaluatedExpressions = new Candidates();

			DataCombinerModified combiner = new DataCombinerModified();

			combiner.addCombineListener(new DataCombinatorListener(allCombinedNotEvaluatedExpressions));

			Candidates allCombinedEvaluatedExpressions = new Candidates();

			long maxCombinerTime = TimeUnit.SECONDS.toMillis(10);
			combiner.combine(allSingleExpressions, null, maxCombinerTime, nopolContext);

			log.debug("All combined expressions: " + allCombinedEvaluatedExpressions.size());

			for (int i_test = 0; i_test < collectedTests.size(); i_test++) {
				final String i_test_name = collectedTests.get(i_test);
				for (Expression combinedExpression : allCombinedNotEvaluatedExpressions) {
					EvaluatedExpression ev = collectEvaluationForAllExecutions(i_test_name, combinedExpression);
					allCombinedEvaluatedExpressions.add(ev);
				}

			}
			Candidates resultAll = new Candidates();
			resultAll.addAll(allSingleExpressions);
			resultAll.addAll(allCombinedEvaluatedExpressions);
			return resultAll;
		} catch (Exception e) {
			log.error("Problems when combining expressions: " + e);
			e.printStackTrace();
			log.error(e);
			Candidates resultAll = new Candidates();
			return resultAll;
		}
	}

	@Deprecated
	public Candidates combineValuesOld() {
		final Candidates result = new Candidates();
		List<String> collectedTests = new ArrayList<>(values.keySet());

		Collections.sort(collectedTests, new Comparator<String>() {
			@Override
			public int compare(String s, String t1) {
				if (values.get(t1).isEmpty()) {
					return -1;
				}
				if (values.get(s).isEmpty()) {
					return 1;
				}
				return values.get(t1).get(0).size() - values.get(s).get(0).size();
			}
		});
		for (int i = 0; i < collectedTests.size(); i++) {
			final String key = collectedTests.get(i);
			List<Candidates> listValue = values.get(key);
			for (Candidates expressions : listValue) {
				for (Expression expression : expressions) {
					expression.getValue().setConstant(isConstant(expression));
				}
			}
		}
		Candidates lastCollectedValues = null;

		//
		for (int i_test = 0; i_test < collectedTests.size(); i_test++) {
			final String key = collectedTests.get(i_test);
			List<Candidates> listValue = values.get(key);
			// Executions
			for (int i_execution = 0; i_execution < listValue.size(); i_execution++) {
				Candidates i_eexps = listValue.get(i_execution);
				if (i_eexps == null) {
					continue;
				}
				if (lastCollectedValues != null
						&& lastCollectedValues.intersection(i_eexps, false).size() == i_eexps.size()) {
					continue;
				}
				lastCollectedValues = i_eexps;
				if (nopolContext.isSortExpressions()) {
					Collections.sort(i_eexps, Collections.reverseOrder());
				}

				// check if one of the collected value can be a patch
				for (int j = 0; j < i_eexps.size(); j++) {
					Expression expression = i_eexps.get(j);
					if (expression == null || expression.getValue() == null) {
						continue;
					}
					// EvaluatedExpression expWrap =
					// collectEvaluationForAllExecutions(key, i, expression);
					// if (expWrap != null) {
					// result.add(expWrap);
					if (checkExpression(key, i_execution, expression)) {
						// result.add(expression);

						if (nopolContext.isOnlyOneSynthesisResult()) {
							return result;
						}
					}
				}
				///
				// TODO: I would group by expression and get the evaluation per
				/// iteration
				///

				DataCombinerModified combiner = new DataCombinerModified();
				final int iterationNumber = i_execution;

				combiner.addCombineListener(new DataCombinerModified.CombineListener() {
					@Override
					public boolean check(Expression expression) {

						// EvaluatedExpression expWrap =
						// collectEvaluationForAllExecutions(key,
						// iterationNumber,
						// expression);
						// if (expWrap != null) {
						// result.add(expWrap);
						if (checkExpression(key, iterationNumber, expression)) {

							result.add(expression);// MM Not modify the
							// result...
							return true;
						}
						return false;
					}
				});

				long maxCombinerTime = TimeUnit.SECONDS.toMillis(10);
				// Combine for this iteration
				combiner.combine(i_eexps, null, maxCombinerTime, nopolContext);
			}
		}

		return result;

	}

	@Override
	protected boolean checkExpression(String testName, int iterationNumber, Expression expression) {
		nbExpressionEvaluated++;
		if (checkedExpression.contains(expression.toString())) {
			return false;
		}
		checkedExpression.add(expression.toString());
		for (String test : values.keySet()) {
			List<Candidates> listCandidates = values.get(test);
			for (int i = 0; i < listCandidates.size(); i++) {
				Candidates valueOtherTest = listCandidates.get(i);
				if (test.equals(testName) && iterationNumber == i) {
					continue;
				}
			}
		}
		return true;
	}

	/**
	 * checkExpression2
	 * 
	 * @param testName
	 * @param iterationNumber
	 * @param expression
	 * @return
	 */
	protected EvaluatedExpression collectEvaluationForAllExecutions(String testName, Expression expression) {
		nbExpressionEvaluated++;
		if (checkedExpression.contains(expression.toString())) {
			// todo
			// return false;
		}
		Map<String, List<Value>> valuesByTest = new HashMap<>();
		checkedExpression.add(expression.toString());
		for (String test : values.keySet()) {
			List<Candidates> listCandidates = values.get(test);
			List<Value> valuesTest = new ArrayList<>();
			for (int i = 0; i < listCandidates.size(); i++) {

				Candidates valueOtherTest = listCandidates.get(i);
				try {
					fr.inria.lille.repair.expression.value.Value expressionValue = expression.evaluate(valueOtherTest);
					valuesTest.add(i, expressionValue);

				} catch (RuntimeException e) {
					System.err.println("Failing eval  expression " + expression + " with values: " + valueOtherTest);
					// return false;
				}
			}
			valuesByTest.put(testName, valuesTest);

		}
		EvaluatedExpression evalExpression = new EvaluatedExpression(expression, valuesByTest);

		return evalExpression;
	}

}
