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
import fr.inria.astor.core.ingredientbased.ExhaustiveIngredientBasedEngine;
import fr.inria.astor.core.ingredientbased.IngredientBasedEvolutionaryRepairApproachImpl;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothCollectorFacade;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesisContext;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesizerWOracle;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.EvaluatedExpression;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.util.MapList;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.expression.Expression;
import fr.inria.lille.repair.expression.value.Value;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.PlugInLoader;
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

	public int MAX_HOLES_PER_MODIFICATION_POINT;
	public int MAX_GENERATIONS = ConfigurationProperties.getPropertyInt("maxGeneration");
	public int modifPointsAnalyzed = 0;
	public int operationsExecuted = 0;

	protected CodeParserLauncher ingredientProcessor = null;
	protected List<TargetElementProcessor<?>> targetElementProcessors = new ArrayList<TargetElementProcessor<?>>();

	protected DynamothCollectorFacade collectorFacade = new DynamothCollectorFacade();
	public double COMPARISON_THRESHOLD = 1;

	protected HoleOrder holeOrderEngine = null;

	public EvalTOSBTApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		targetElementProcessors.add(new CtExpressionIngredientSpaceProcessor());
		ingredientProcessor = new CodeParserLauncher(targetElementProcessors);

		MAX_HOLES_PER_MODIFICATION_POINT = (ConfigurationProperties.hasProperty("maxholespermp"))
				? ConfigurationProperties.getPropertyInt("maxholespermp")
				: 10;

	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();
		if (this.ingredientSpace == null) {
			this.ingredientSpace = IngredientBasedEvolutionaryRepairApproachImpl
					.getIngredientPool(getTargetElementProcessors());
		}

		String holeorder = ConfigurationProperties.properties.getProperty("holeorder");
		if (holeorder == null) {
			this.holeOrderEngine = new SimpleDiffOrderFromJSON();
		} else {
			holeOrderEngine = (HoleOrder) PlugInLoader.loadPlugin(holeorder, HoleOrder.class);
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void startEvolution() throws Exception {

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
					.getSortedModificationPointsList(parentVariant.getModificationPoints())) {

				modifPointsAnalyzed++;
				log.info("\n*************\n\n MP (" + modifPointsAnalyzed + "/"
						+ parentVariant.getModificationPoints().size() + ") location to modify: " + iModifPoint);

				System.out.println("MP code: " + iModifPoint.getCodeElement());

				boolean stop = false;

				if (ConfigurationProperties.getPropertyBool("clustercollectedvalues"))
					stop = analyzeModificationPointMultipleExecutions(parentVariant, iModifPoint);
				else
					stop = analyzeModificationPointSingleValue(parentVariant, iModifPoint);

				if (stop) {
					return;
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operationsExecuted);

	}

	public boolean analyzeModificationPointMultipleExecutions(ProgramVariant parentVariant,
			ModificationPoint iModifPoint) throws IllegalAccessException, Exception, IllegalAccessError {

		final boolean stop = true;
		DynamothSynthesisContext contextCollected = null;
		try {
			contextCollected = this.collectorFacade.collectValues(getProjectFacade(), iModifPoint);
		} catch (Exception e) {
			log.error("Error calling Dynamoth value recolection MP id: " + iModifPoint.identified);
			log.error(e);
			currentStat.increment(GeneralStatEnum.NR_ERRONEOUS_VARIANCES);
			return false;
		}
		// Collecting values:
		// values are collected from all test.
		log.debug("---> Collected Context size: " + contextCollected.getValues().size());

		// Creating combinations (do not depend on the Holes because
		// they are combination of variables in context of a
		// modification point)
		DynamothSynthesizerWOracle synthesizer = new DynamothSynthesizerWOracle(contextCollected);

		Candidates candidatesnew = synthesizer.combineValuesEvaluated();

		// Key: test name, value list of clusters, each cluster is a list of
		// evaluated expressions
		MapList<String, List<EvaluatedExpression>> cluster = clusterCandidatesByValue(candidatesnew);

		List<CtCodeElement> holesFromMP = calculateHolesSorted(iModifPoint);
		log.debug("Total holes: " + holesFromMP.size());

		int nrholefrommpi = 0;
		for (CtCodeElement iHole : holesFromMP) {
			nrholefrommpi++;
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

			// Simplification
			int nrtestfromholei = 0;
			for (String i_testName : cluster.keySet()) {
				List<List<EvaluatedExpression>> clustersOfTest = cluster.get(i_testName);
				nrtestfromholei++;
				log.debug(String.format("Nr clusters of test %s: %d/%d", i_testName, nrtestfromholei,
						clustersOfTest.size()));

				int valuefromtesti = 0;
				for (List<EvaluatedExpression> i_cluster : clustersOfTest) {
					valuefromtesti++;
					if (i_cluster.size() > 0) {
						EvaluatedExpression firstExpressionOfCluster = i_cluster.get(0);

						if (firstExpressionOfCluster.asPatch().toString().equals(iHole.toString())) {
							continue;
						}

						log.debug(String.format("Analyzing value %d/%d of test %d from hole %d", valuefromtesti,
								clustersOfTest.size(), nrtestfromholei, nrholefrommpi));
						operationsExecuted++;
						currentStat.increment(GeneralStatEnum.NR_GENERATIONS);
						boolean isExpressionASolution = createAndEvaluatePatch(operationsExecuted, parentVariant,
								iModifPoint, aholeExpression, firstExpressionOfCluster);

						if (isExpressionASolution) {
							log.info(String.format("Patch found with expresion %s evaluated as %s in modif point %s ",
									firstExpressionOfCluster.asPatch(),
									firstExpressionOfCluster.getEvaluations().get(i_testName), iModifPoint.toString()));
						}
						if (MAX_GENERATIONS <= operationsExecuted) {

							this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
							log.info("Stop-Max operator Applied " + operationsExecuted);
							return stop;
						}

						boolean stopSearch = !this.solutions.isEmpty()
								&& (ConfigurationProperties.getPropertyBool("stopfirst")
										// or nr solutions are greater than max
										// allowed
										|| (this.solutions.size() >= ConfigurationProperties
												.getPropertyInt("maxnumbersolutions")));

						if (stopSearch) {
							log.debug("\n Max Solution found " + this.solutions.size());
							this.outputStatus = AstorOutputStatus.STOP_BY_PATCH_FOUND;
							return stop;
						}

					}
				}

			}

		}
		return !stop;
	}

	public boolean analyzeModificationPointSingleValue(ProgramVariant parentVariant, ModificationPoint iModifPoint)
			throws IllegalAccessException, Exception, IllegalAccessError {

		log.debug("-Step A--> Analyzing extension point: \n" + iModifPoint.getCodeElement());
		final boolean stop = true;
		DynamothSynthesisContext contextCollected = this.collectorFacade.collectValues(getProjectFacade(), iModifPoint);

		log.debug("-Step B--> Collected Context size: " + contextCollected.getValues().size());

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
		List<CtCodeElement> holesFromMP = calculateHolesSorted(iModifPoint);
		log.debug("Total holes: " + holesFromMP.size() + " " + holesFromMP);

		for (CtCodeElement iHole : holesFromMP) {
			if (!(iHole instanceof CtExpression)
					// New Workaround: the hole will not be a complete
					// statement
					|| (iHole instanceof CtStatement)) {
				continue;
			}
			int maxsolutionsiHole = 0;

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
					operationsExecuted++;
					boolean isExpressionASolution = createAndEvaluatePatch(operationsExecuted, parentVariant, iModifPoint,
							aholeExpression, expression);

					if (isExpressionASolution) {
						log.info(String.format(
								"Patch found with expresion %s evaluated as %s in hole %s at modif point %s ",
								expression.asPatch(), iCompatibleValue, iHole.toString(), iModifPoint.toString()));
						maxsolutionsiHole++;
					}

					if (maxsolutionsiHole > 0
							&& maxsolutionsiHole >= ConfigurationProperties.getPropertyInt("maxsolutionsperhole")) {
						log.debug("Max number of sol per hole");
						break;
					}

					if (MAX_GENERATIONS <= operationsExecuted) {

						this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
						log.info("Stop-Max operator Applied " + operationsExecuted);
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

			log.debug("Transformed Mod point:\n" + modifPoint.getCodeElement().toString());
			if (isSolution) {
				log.debug("Solution found " + getSolutions().size());
				this.solutions.add(candidateVariant);
				currentStat.increment(GeneralStatEnum.NR_RIGHT_COMPILATIONS);
			} else {
				currentStat.increment(GeneralStatEnum.NR_FAILING_VALIDATION_PROCESS);
			}

			if (!modifPoint.getCodeElement().toString().contains(ingredientSynthesized.getCode().toString())) {
				log.debug(
						" Error: not well transformed? Type of hole " + aholeExpression.getClass().getCanonicalName());
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

	/**
	 * Key: test name, value list of clusters, each cluster is a list of evaluated
	 * expressions
	 * 
	 * @param candidates
	 * @return
	 */
	public MapList<String, List<EvaluatedExpression>> clusterCandidatesByValue(Candidates candidates) {

		log.debug("number candidates " + candidates.size());

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
	public List<CtCodeElement> calculateAllHoles(ModificationPoint modifPoint) {
		boolean mustclone = false;
		List<CtCodeElement> holes = this.ingredientProcessor.createFixSpace(modifPoint.getCodeElement(), mustclone);

		return holes;
	}

	public List<CtCodeElement> reduceHoles(List<CtCodeElement> holes) {

		if (holes.size() > MAX_HOLES_PER_MODIFICATION_POINT) {
			log.debug("Cutting nr of holes: " + holes.size() + " to " + MAX_HOLES_PER_MODIFICATION_POINT);
			return holes.subList(0, MAX_HOLES_PER_MODIFICATION_POINT);
		}
		return holes;
	}

	public List<CtCodeElement> calculateHolesSorted(ModificationPoint modifPoint) {
		List<CtCodeElement> holes = calculateAllHoles(modifPoint);
		List<CtCodeElement> holesSorted = orderHoleElements(holes);
		return reduceHoles(holesSorted);
	}

	public List<CtCodeElement> orderHoleElements(List<CtCodeElement> holes) {

		if (!ConfigurationProperties.getPropertyBool("sortholes")) {
			log.debug("---Not Sorting holes ");
			return holes;
		} else {

			log.debug("---Sorting holes ");
			List<CtCodeElement> sorted = holeOrderEngine.orderHoleElements(holes);
			return sorted;
		}

	}

	public DynamothCollectorFacade getCollectorFacade() {
		return collectorFacade;
	}

	public void setCollectorFacade(DynamothCollectorFacade collectorFacade) {
		this.collectorFacade = collectorFacade;
	}

	public HoleOrder getHoleOrderEngine() {
		return holeOrderEngine;
	}

	public void setHoleOrderEngine(HoleOrder holeOrderEngine) {
		this.holeOrderEngine = holeOrderEngine;
	}
}
