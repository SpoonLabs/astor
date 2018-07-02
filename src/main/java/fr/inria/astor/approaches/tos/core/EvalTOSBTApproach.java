package fr.inria.astor.approaches.tos.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.cardumen.FineGrainedExpressionReplaceOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.ingredientbased.ExhaustiveIngredientBasedEngine;
import fr.inria.astor.core.ingredientbased.IngredientBasedPlugInLoader;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothCollectorFacade;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesisContext;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesizerWOracle;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.EvaluatedExpression;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import fr.inria.astor.util.MapList;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.value.Value;
import fr.inria.main.AstorOutputStatus;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvalTOSBTApproach extends ExhaustiveIngredientBasedEngine {

	public int MAX_HOLES_PER_MODIFICATION_POINT = 10;
	public int MAX_GENERATIONS = ConfigurationProperties.getPropertyInt("maxGeneration");
	public int modifPointsAnalyzed = 0;
	public int operatorExecuted = 0;

	protected CodeParserLauncher ingredientProcessor = null;
	protected List<TargetElementProcessor<?>> targetElementProcessors = new ArrayList<TargetElementProcessor<?>>();

	protected DynamothCollectorFacade collectorFacade = new DynamothCollectorFacade();
	public double COMPARISON_THRESHOLD = 1;

	public EvalTOSBTApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		targetElementProcessors.add(new CtExpressionIngredientSpaceProcessor());
		ingredientProcessor = new CodeParserLauncher(targetElementProcessors);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void startEvolution() throws Exception {

		if (this.ingredientSpace == null) {
			this.ingredientSpace = IngredientBasedPlugInLoader.getIngredientPool(getTargetElementProcessors());
		}
		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		// For each variant (one is enough)

		getIngredientSpace().defineSpace(originalVariant);

		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			// Let's print the MPs
			int i = 0;
			for (ModificationPoint mp : variants.get(0).getModificationPoints()) {
				log.info("mp " + (i++) + " -->" + mp + " " + mp.getCodeElement());
			}

			// For each modification point pm
			for (ModificationPoint iModifPoint : this.getSuspiciousNavigationStrategy()
					.getSortedModificationPointsList(parentVariant)) {

				modifPointsAnalyzed++;
				log.info("\n*************\n\n MP (" + modifPointsAnalyzed + "/"
						+ parentVariant.getModificationPoints().size() + ") location to modify: " + iModifPoint);

				System.out.println("MP code: " + iModifPoint.getCodeElement());

				boolean stop = analyzeModificationPointSingleValue(parentVariant, iModifPoint);
				if (stop) {
					return;
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operatorExecuted);

	}

	public boolean analyzeModificationPointTest(ProgramVariant parentVariant, ModificationPoint iModifPoint)
			throws IllegalAccessException, Exception, IllegalAccessError {

		final boolean stop = true;
		DynamothSynthesisContext contextCollected = this.collectorFacade.collectValues(getProjectFacade(), iModifPoint);
		// Collecting values:
		// TODO: check if values are collected from a) only failing test, or b)
		// all test.
		log.debug("---> Collected Context size: " + contextCollected.getValues().size());

		// Creating combinations (do not depend on the Holes because
		// they are combination of variables in context of a
		// modification point)
		DynamothSynthesizerWOracle synthesizer = new DynamothSynthesizerWOracle(contextCollected);

		Candidates candidatesnew = synthesizer.combineValuesEvaluated();

		// System.out.println(candidatesnew);

		MapList<String, List<EvaluatedExpression>> cluster = clusterCandidatesByValue(candidatesnew);

		List<CtCodeElement> holesFromMP = calculateHoles(iModifPoint);
		log.debug("Total holes: " + holesFromMP.size());

		for (CtCodeElement iHole : holesFromMP) {
			if (!(iHole instanceof CtExpression)
					// New Workaround: the hole will not be a complete
					// statement
					|| (iHole instanceof CtStatement)) {
				continue;
			}
			// The hole to replace:
			CtExpression aholeExpression = (CtExpression) iHole;
			log.debug(
					"\n\n---hole-> `" + iHole + "`,  return type " + aholeExpression.getType().box().getQualifiedName()
							+ "--hole type: " + iHole.getClass().getCanonicalName());

		}
		return !stop;
	}

	public boolean analyzeModificationPointSingleValue(ProgramVariant parentVariant, ModificationPoint iModifPoint)
			throws IllegalAccessException, Exception, IllegalAccessError {

		final boolean stop = true;
		DynamothSynthesisContext contextCollected = this.collectorFacade.collectValues(getProjectFacade(), iModifPoint);
		// Collecting values:
		// TODO: check if values are collected from a) only failing test, or b)
		// all test.
		log.debug("---> Collected Context size: " + contextCollected.getValues().size());

		// Creating combinations (do not depend on the Holes because
		// they are combination of variables in context of a
		// modification point)
		DynamothSynthesizerWOracle synthesizer = new DynamothSynthesizerWOracle(contextCollected);
		//
		Candidates candidatesnew = synthesizer.combineValuesEvaluated();
		System.out.println(candidatesnew);
		//
		Candidates candidates = synthesizer.combineValues();

		// Store candidates in structures
		MapList<Value, Expression> clusterValues = groupedCandidatesByValue(candidates);

		printCandidatesSummary(candidates, clusterValues);

		// Get holes (CtExpression as granularity) of the Modification
		// point
		List<CtCodeElement> holesFromMP = calculateHoles(iModifPoint);
		log.debug("Total holes: " + holesFromMP.size());

		for (CtCodeElement iHole : holesFromMP) {
			if (!(iHole instanceof CtExpression)
					// New Workaround: the hole will not be a complete
					// statement
					|| (iHole instanceof CtStatement)) {
				continue;
			}
			// The hole to replace:
			CtExpression aholeExpression = (CtExpression) iHole;
			log.debug(
					"\n\n---hole-> `" + iHole + "`,  return type " + aholeExpression.getType().box().getQualifiedName()
							+ "--hole type: " + iHole.getClass().getCanonicalName());

			// Filtering evaluated values according to the type
			List<Value> valuesWithCompatibleType = clusterValues.keySet().stream()
					.filter(e -> e.getType().getName().equals(aholeExpression.getType().box().getQualifiedName()))
					.collect(Collectors.toList());

			log.debug("-----Filtered compatible values with hole (size):  " + valuesWithCompatibleType.size() + " "
					+ valuesWithCompatibleType);

			// for each compatible value:
			for (Value iCompatibleValue : valuesWithCompatibleType) {

				log.debug(String.format("\n Hole %s with value: %s", aholeExpression, iCompatibleValue.toString()));

				// Get all expressions that evaluate as
				// valueCompatible
				List<Expression> expresionWithTheValue = clusterValues.get(iCompatibleValue);
				// TODO: now get only the first one, should we take
				// more? (as all expressions are evaluated as the same
				// value, all should produce the same results).
				if (expresionWithTheValue.size() > 0) {
					// TODO (improvement?): Validation: maybe if the
					// expression produce the patch, we can
					// re-corroborate it by evaluating X expression
					// more.
					Expression expression = expresionWithTheValue.get(0);
					// Check the patch is different to the suspicious
					// element
					if (expression.asPatch().toString().equals(iHole.toString())) {
						continue;
					}
					operatorExecuted++;
					boolean isExpressionASolution = createAndEvaluatePatch(operatorExecuted, parentVariant, iModifPoint,
							aholeExpression, expression);

					if (isExpressionASolution) {
						log.info(String.format("Patch found with expresion %s evaluated as %s in modif point %s ",
								expression.asPatch(), iCompatibleValue, iModifPoint.toString()));
					}
					if (MAX_GENERATIONS <= operatorExecuted) {

						this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
						log.info("Stop-Max operator Applied " + operatorExecuted);
						return stop;
					}

					boolean stopSearch = !this.solutions.isEmpty() && (ConfigurationProperties
							.getPropertyBool("stopfirst")
							// or nr solutions are greater than max allowed
							|| (this.solutions.size() >= ConfigurationProperties.getPropertyInt("maxnumbersolutions")));

					if (stopSearch) {
						log.debug("\n Max Solution found " + this.solutions.size());
						this.outputStatus = AstorOutputStatus.STOP_BY_PATCH_FOUND;
						return stop;
					}

				}

			}
		}
		return !stop;
	}

	public boolean createAndEvaluatePatch(int operatorExecuted, ProgramVariant parentVariant,
			ModificationPoint modifPoint, CtExpression aholeExpression, Expression expressionFromDynamoth)
			throws IllegalAccessException, Exception, IllegalAccessError {

		Ingredient ingredientSynthesized = createIngredient(expressionFromDynamoth);
		boolean isSolution = false;
		// Create an operation instance with that
		// ingredient and the element pointed by the
		// hole.
		if (ingredientSynthesized != null) {

			OperatorInstance operatorInstance = new OperatorInstance(modifPoint,
					new FineGrainedExpressionReplaceOperator(), aholeExpression, ingredientSynthesized.getCode());
			log.debug("-op (" + operatorExecuted + "): " + operatorInstance);

			ProgramVariant candidateVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
					generationsExecuted);
			candidateVariant.getOperations().put(generationsExecuted, Arrays.asList(operatorInstance));

			applyNewMutationOperationToSpoonElement(operatorInstance);

			isSolution = processCreatedVariant(candidateVariant, generationsExecuted);

			log.info("Transformed Mod point:\n" + modifPoint.getCodeElement().toString());
			if (isSolution) {
				log.info("Solution found " + getSolutions().size());
				this.solutions.add(candidateVariant);
			} else {
				log.info("No solution");
			}

			if (!modifPoint.getCodeElement().toString().contains(ingredientSynthesized.getCode().toString())) {
				log.error("Error: diff code");
				// throw new IllegalAccessError("error code transformation");
			}

			// We undo the operator (for try the next
			// one)
			// Undo changes in model
			undoOperationToSpoonElement(operatorInstance);
			///
		}
		return isSolution;
	}

	private Ingredient createIngredient(Expression expression) {
		String candidateCode = expression.asPatch();
		CtCodeSnippetExpression<Boolean> snippet = MutationSupporter.getFactory().Core().createCodeSnippetExpression();
		snippet.setValue(candidateCode);
		Ingredient ingredient = new Ingredient(snippet);
		// take one form the cluster by type
		log.debug("Creating ingredient from Dynamoth expression: " + expression + " --result--> Spoon Ingredient: "
				+ ingredient + "| value: " + expression.getValue().getRealValue());
		return ingredient;
	}

	private MapList<Value, Expression> groupedCandidatesByValue(Candidates candidates) {
		MapList<Value, Expression> clusterValues = new MapList<>();
		for (int i = 0; i < candidates.size(); i++) {
			Expression expr = candidates.get(i);
			try {
				expr.getValue().toString();
				clusterValues.add(expr.getValue(), expr);
			} catch (Exception e) {
				log.error("Error " + e);
			}
			;
		}
		return clusterValues;
	}

	public MapList<String, List<EvaluatedExpression>> clusterCandidatesByValue(Candidates candidates) {

		System.out.println("number candidates " + candidates.size());

		// For each test:
		// test name, cluster of expressions
		MapList<String, List<EvaluatedExpression>> cluster = new MapList<>();

		for (int i = 0; i < candidates.size(); i++) {
			EvaluatedExpression i_expression = (EvaluatedExpression) candidates.get(i);

			for (String i_testName : i_expression.getEvaluations().keySet()) {

				if (!cluster.containsKey(i_testName)) {
					List<EvaluatedExpression> evacluster = new ArrayList<>();
					evacluster.add(i_expression);
					cluster.add(i_testName, evacluster);
				} else {

					List<List<EvaluatedExpression>> clusterOfTest = cluster.get(i_testName);
					boolean notClustered = true;
					for (List<EvaluatedExpression> elementsFromCluster : clusterOfTest) {

						if (elementsFromCluster != null && elementsFromCluster.size() > 0) {
							EvaluatedExpression alreadyClustered = elementsFromCluster.get(0);

							double similarity = calculateSimilarity(i_testName, alreadyClustered, i_expression);

							if (similarity >= COMPARISON_THRESHOLD) {

								elementsFromCluster.add(i_expression);
								notClustered = false;
								break;
							}
						}

					}
					if (notClustered) {
						List<EvaluatedExpression> evacluster = new ArrayList<>();
						evacluster.add(i_expression);
						clusterOfTest.add(evacluster);
					}

				}

			}

		}

		return cluster;
	}

	private double calculateSimilarity(String i_testName, EvaluatedExpression alreadyClustered,
			EvaluatedExpression i_expression) {
		List<Value> valuesofTestToCluster = i_expression.getEvaluations().get(i_testName);
		List<Value> valuesofTestAlreadyClustered = alreadyClustered.getEvaluations().get(i_testName);

		int max = valuesofTestToCluster.size() > valuesofTestAlreadyClustered.size() ? valuesofTestToCluster.size()
				: valuesofTestAlreadyClustered.size();

		int total = 0;
		for (int i = 0; i < max; i++) {

			if (i < valuesofTestToCluster.size() && i < valuesofTestAlreadyClustered.size()) {
				Object v1 = valuesofTestToCluster.get(i).getRealValue();
				Object v2 = valuesofTestAlreadyClustered.get(i).getRealValue();
				if (v1 != null && v2 != null && v1.equals(v2)) {
					total += 1;
				}
			}
		}

		return (double) total / (double) max;
	}

	private void printCandidatesSummary(Candidates candidates, MapList<Value, Expression> clusterValues) {
		log.debug("Total candidates: " + candidates.size());
		log.debug("Values retrieved (size " + clusterValues.keySet().size() + "): \n" + clusterValues.keySet());
		log.debug("Cluster (size " + clusterValues.keySet().size() + ") :\n " + clusterValues);
		String o = "";
		for (Value k : clusterValues.keySet()) {
			o += k.getRealValue() + " (" + clusterValues.get(k).size() + "), ";

		}
		log.debug("Cluster " + o);
	}

	@SuppressWarnings("unchecked")
	private List<CtCodeElement> calculateHoles(ModificationPoint modifPoint) {
		boolean mustclone = false;
		List<CtCodeElement> holes = this.ingredientProcessor.createFixSpace(modifPoint.getCodeElement(), mustclone);
		if (holes.size() > MAX_HOLES_PER_MODIFICATION_POINT) {
			return holes.subList(0, MAX_HOLES_PER_MODIFICATION_POINT);
		}

		return holes;
	}

	public void startEvolutionOLD() throws Exception {

		if (this.ingredientSpace == null) {
			this.ingredientSpace = IngredientBasedPlugInLoader.getIngredientPool(getTargetElementProcessors());
		}
		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		// For each variant (one is enough)
		int maxMinutes = ConfigurationProperties.getPropertyInt("maxtime");
		int maxGenerations = ConfigurationProperties.getPropertyInt("maxGeneration");
		// for stats
		int modifPointsAnalyzed = 0;
		int operatorExecuted = 0;

		getIngredientSpace().defineSpace(originalVariant);

		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			for (ModificationPoint modifPoint : this.getSuspiciousNavigationStrategy()
					.getSortedModificationPointsList(parentVariant)) {

				modifPointsAnalyzed++;

				log.info("\n MP (" + modifPointsAnalyzed + "/" + parentVariant.getModificationPoints().size()
						+ ") location to modify: " + modifPoint);

				// We create all operators to apply in the modifpoint
				List<OperatorInstance> operatorInstances = createInstancesOfOperators(
						(SuspiciousModificationPoint) modifPoint);

				if (operatorInstances == null || operatorInstances.isEmpty())
					continue;

				for (OperatorInstance pointOperation : operatorInstances) {

					operatorExecuted++;

					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(pointOperation));

					applyNewMutationOperationToSpoonElement(pointOperation);

					log.debug("Operator:\n " + pointOperation);
					log.debug("Modif point transformated:\n--> " + modifPoint.getCodeElement());
					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);

					if (solution) {
						log.info("Solution found " + getSolutions().size());
						this.solutions.add(solutionVariant);

					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);

					if (!this.solutions.isEmpty() && ConfigurationProperties.getPropertyBool("stopfirst")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						log.debug(" modpoint analyzed " + modifPointsAnalyzed + ", operators " + operatorExecuted);
						return;
					}

					if (!belowMaxTime(dateInitEvolution, maxMinutes)) {
						this.setOutputStatus(AstorOutputStatus.TIME_OUT);
						log.debug("Max time reached");
						return;
					}

					if (maxGenerations <= operatorExecuted) {

						this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
						log.info("Stop-Max operator Applied " + operatorExecuted);
						log.info("modpoint:" + modifPointsAnalyzed + ":all:" + totalmodfpoints + ":operators:"
								+ operatorExecuted);
						return;
					}

					if (this.getSolutions().size() >= ConfigurationProperties.getPropertyInt("maxnumbersolutions")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						log.debug("Stop-Max solutions reached " + operatorExecuted);
						log.debug("modpoint:" + modifPointsAnalyzed + ":all:" + totalmodfpoints + ":operators:"
								+ operatorExecuted);
						return;
					}
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operatorExecuted);

	}

	public DynamothCollectorFacade getCollectorFacade() {
		return collectorFacade;
	}

	public void setCollectorFacade(DynamothCollectorFacade collectorFacade) {
		this.collectorFacade = collectorFacade;
	}
}
