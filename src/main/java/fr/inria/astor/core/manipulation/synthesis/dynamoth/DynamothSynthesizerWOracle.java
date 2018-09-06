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
			// Candidates lastCollectedValues = null;
			// Key: code of the expression, value the expression object

			Map<String, Expression> singleExpression = new HashMap<>();

			// Here, we collect the single values and we create an evaluation for
			// each iteration.
			for (int i_test = 0; i_test < collectedTests.size(); i_test++) {
				final String i_test_name = collectedTests.get(i_test);
				List<Candidates> listValue = values.get(i_test_name);

				// For each Execution
				for (int i_execution = 0; i_execution < listValue.size(); i_execution++) {

					Candidates expresion_execution_i = listValue.get(i_execution);
					if (expresion_execution_i == null) {
						continue;
					}

					// For each variable in the execution
					for (int j_expresionVariable = 0; j_expresionVariable < expresion_execution_i
							.size(); j_expresionVariable++) {
						Expression expression = expresion_execution_i.get(j_expresionVariable);
						if (expression == null || expression.getValue() == null) {
							continue;
						}

						if (!singleExpression.containsKey(expression.asPatch())) {
							singleExpression.put(expression.asPatch(), expression);
						}

					}
				}
			}

			// STEP 1B: Evaluating single expression i.e., variables.
			List<EvaluatedExpression> allSingleEvaluatedExpressions = new ArrayList<EvaluatedExpression>();

			for (Expression combinedExpression : singleExpression.values()) {
				EvaluatedExpression ev = collectEvaluationForAllExecutions(combinedExpression);
				allSingleEvaluatedExpressions.add(ev);
			}

			log.debug("All single expression: " + allSingleEvaluatedExpressions.size());

			// STEP 2 : COMBINATION of Expressions

			// We create a list where we put all combinations
			// Note that the new combination has only one evaluation.
			Candidates allCombinedNotEvaluatedExpressions = new Candidates();

			DataCombinerModified combiner = new DataCombinerModified();

			combiner.addCombineListener(new DataCombinatorListener(allCombinedNotEvaluatedExpressions));

			long maxCombinerTime = TimeUnit.SECONDS.toMillis(10);
			// Passing expression from Collection to Candidates...
			Candidates allSingleExpressionsCandidates = new Candidates();
			allSingleExpressionsCandidates.addAll(singleExpression.values());
			combiner.combine(allSingleExpressionsCandidates, null, maxCombinerTime, nopolContext);

			log.debug("All combined expressions: " + allCombinedNotEvaluatedExpressions.size());

			// Here, in this list, we will store the expression After evaluating it for each
			// test and
			// execution.
			List<EvaluatedExpression> allCombinedEvaluatedExpressions = new ArrayList<EvaluatedExpression>();

			for (Expression combinedExpression : allCombinedNotEvaluatedExpressions) {
				EvaluatedExpression ev = collectEvaluationForAllExecutions(combinedExpression);
				allCombinedEvaluatedExpressions.add(ev);
			}

			// resultAll is a list of Evaluated expression.
			Candidates resultAll = new Candidates();
			resultAll.addAll(allSingleEvaluatedExpressions);
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
	protected EvaluatedExpression collectEvaluationForAllExecutions(Expression expression) {
		nbExpressionEvaluated++;

		// key is the test, value is a list of Values, each position is the evaluation
		// of the expression in a given execution.
		Map<String, List<Value>> valuesByTest = new HashMap<>();
		checkedExpression.add(expression.toString());

		// For each test
		for (String i_test : values.keySet()) {
			// Executions of the test
			List<Candidates> listCandidates = values.get(i_test);

			List<Value> valuesTest = new ArrayList<>();

			for (int i = 0; i < listCandidates.size(); i++) {
				// One execution
				Candidates valuesExecution_i = listCandidates.get(i);
				try {
					// Evaluation of the expression given the variables values given in execution i.
					fr.inria.lille.repair.expression.value.Value expressionValue = expression
							.evaluate(valuesExecution_i);
					valuesTest.add(i, expressionValue);

				} catch (RuntimeException e) {
					System.err.println("Failing eval  expression " + expression + " with values: " + valuesExecution_i);
				}
			}
			valuesByTest.put(i_test, valuesTest);

		}
		EvaluatedExpression evalExpression = new EvaluatedExpression(expression, valuesByTest);

		return evalExpression;
	}

	/**
	 * New
	 * 
	 * @param testName
	 * @param expression
	 * @return
	 */
	protected EvaluatedExpression simpleEval(String testName, Expression expression) {
		nbExpressionEvaluated++;

		Map<String, List<Value>> valuesByTest = new HashMap<>();
		checkedExpression.add(expression.toString());

		// Executions of the test
		List<Candidates> listCandidates = values.get(testName);

		List<Value> valuesTest = new ArrayList<>();

		for (int i = 0; i < listCandidates.size(); i++) {
			// One execution
			Candidates valueOtherTest = listCandidates.get(i);
			try {
				// The value of the expression (parameter) in test.
				fr.inria.lille.repair.expression.value.Value expressionValue = expression.evaluate(valueOtherTest);
				valuesTest.add(i, expressionValue);

			} catch (RuntimeException e) {
				System.err.println("Failing eval  expression " + expression + " with values: " + valueOtherTest);
				// return false;
			}
		}
		valuesByTest.put(testName, valuesTest);

		// EvaluatedExpression evalExpression = new EvaluatedExpression(expression,
		// valuesByTest);

		return null;
	}

}
