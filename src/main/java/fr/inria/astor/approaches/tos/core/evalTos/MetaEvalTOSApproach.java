package fr.inria.astor.approaches.tos.core.evalTos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.core.evalTos.ingredients.ClusterExpressions;
import fr.inria.astor.approaches.tos.core.evalTos.ingredients.DynaIngredientPool;
import fr.inria.astor.approaches.tos.operator.DynaIngredientOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.ConstReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.IOperatorWithTargetElement;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicExpOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicRedOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementArgumentRemoveOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffArgumentsOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffNameOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXVariableReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromIfOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromMethodCallOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromTryOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByMethodCallOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithIfNullCheck;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithIfOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithTrySingleStatementOp;
import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.VariantValidationResult;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.EvaluatedExpression;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.astor.util.MapList;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.PlugInLoader;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtTypeReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetaEvalTOSApproach extends EvalTOSClusterApproach {

	public static int MAX_GENERATIONS = ConfigurationProperties.getPropertyInt("maxGeneration");
	public int modifPointsAnalyzed = 0;

	public List<ProgramVariant> evaluatedProgramVariants = new ArrayList<>();

	protected IPredictor predictor = null;

	public Map<ModificationPoint, IPrediction> predictions = new HashMap();

	public MetaEvalTOSApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.operatorSpace = new OperatorSpace();
		loadPredictor();
	}

	public void loadPredictor() {

		if (ConfigurationProperties.hasProperty("pred")) {
			log.debug("Loading predictor");
			String predclass = ConfigurationProperties.getProperty("pred");

			try {
				this.predictor = (IPredictor) PlugInLoader.loadPlugin(predclass, IPredictor.class);

			} catch (Exception e) {
				log.error("error loading stratety");
				e.printStackTrace();
				log.error(e);

			}

		}
	}

	@Override
	protected void loadOperatorSpaceDefinition() throws Exception {
		this.operatorSpace = new OperatorSpace();
		this.operatorSpace.register(new WrapwithTrySingleStatementOp());
		this.operatorSpace.register(new WrapwithIfOp());
		this.operatorSpace.register(new WrapwithIfNullCheck());
		this.operatorSpace.register(new VarReplacementByAnotherVarOp());
		this.operatorSpace.register(new LogicExpOperator());
		this.operatorSpace.register(new MethodXMethodReplacementDiffNameOp());
		this.operatorSpace.register(new MethodXMethodReplacementDiffArgumentsOp());
		this.operatorSpace.register(new MethodXMethodReplacementArgumentRemoveOp());
		this.operatorSpace.register(new MethodXVariableReplacementOp());
		this.operatorSpace.register(new UnwrapfromMethodCallOp());
		this.operatorSpace.register(new UnwrapfromTryOp());
		this.operatorSpace.register(new UnwrapfromIfOp());
		this.operatorSpace.register(new LogicRedOperator());
		this.operatorSpace.register(new VarReplacementByMethodCallOp());
		this.operatorSpace.register(new OperatorReplacementOp());
		this.operatorSpace.register(new ConstReplacementOp());
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void startEvolution() throws Exception {

		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		modifPointsAnalyzed = 0;
		evaluatedProgramVariants.clear();

		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			if (MAX_GENERATIONS <= generationsExecuted) {

				this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
				return;
			}

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
						+ parentVariant.getModificationPoints().size() + ") "
						+ +Long.valueOf(((new Date().getTime() - this.dateEngineCreation.getTime()) / 60000))
						+ " minutes passed,  location to modify: " + iModifPoint);

				System.out.println("MP code: " + iModifPoint.getCodeElement());

				boolean existsSolution = analyzeModificationPoint(parentVariant, iModifPoint);

				if (existsSolution) {

					if (ConfigurationProperties.getPropertyBool("stopfirst")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						return;
					}
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operationsExecuted);

	}

	int nrVariant = 1;

	@SuppressWarnings("unused")
	public boolean analyzeModificationPoint(ProgramVariant parentVariant, ModificationPoint iModifPoint)
			throws Exception {

		int generation = 1;

		boolean existSolution = false;

		IPrediction predictionsForModifPoint = computePredictionsForModificationPoint(iModifPoint);

		log.info("Elements to modify in MP " + iModifPoint.identified + ": "
				+ predictionsForModifPoint.getElementsWithPrediction().size());
		this.predictions.put(iModifPoint, predictionsForModifPoint);

		// No prediction, so, we return
		if (predictionsForModifPoint.getElementsWithPrediction().isEmpty())
			return false;

		this.predictions.put(iModifPoint, predictionsForModifPoint);

		DynaIngredientPool ingredientPool = null;

		List<AstorOperator> allOperationsPredicted = predictionsForModifPoint.getAllOperationsPredicted();

		log.info("Predicted operators for " + iModifPoint.identified + " : " + allOperationsPredicted);

		if (oneOperatorNeedsDynamicIngredients(allOperationsPredicted)) {
			ingredientPool = this.getClusteredEvaluatedExpression(iModifPoint);

			log.info("Dyna Ingredients of modify in MP " + iModifPoint.identified + ": "
					+ ((ingredientPool.getClusterEvaluatedExpressions() != null)
							? ingredientPool.getClusterEvaluatedExpressions().size()
							: "Dynamoth null"));
		} else {
			log.debug("Any operator needs ingredient");
		}

		// Call to the extension point to get the order
		// We take each operator, in the order given by the EP
		for (PredictionElement predictionElement : predictionsForModifPoint.getElementsWithPrediction()) {

			CtElement targetElement = predictionElement.getElement();
			List<AstorOperator> candidateOperators = predictionsForModifPoint.getPrediction(predictionElement);

			for (AstorOperator operator : candidateOperators) {

				log.info("Target " + targetElement + " operator " + operator);

				if (operator == null) {
					log.error("No operator to apply");
					continue;
				}
				// Set the target element in the operator//This needs refactor
				if (this.predictor != null) {
					// Here we have a predictor used for predict the opertor
					// If we are interested in applying ops only when we have target
					// ConfigurationProperties.hasProperty("onlywithtarget") &&
					if (targetElement == null) {
						continue;
					}
					IOperatorWithTargetElement targetOp = (IOperatorWithTargetElement) operator;
					if (targetOp.checkTargetCompatibility(targetElement)) {
						targetOp.setTargetElement(targetElement);
					} else {
						// Target not compatible with operator, we continue
						targetOp.setTargetElement(null);
						continue;
					}
				}

				try {
					List<ProgramVariant> candidateProgramVariants = new ArrayList<>();
					List<OperatorInstance> instancesOfOperatorForModificationPoint = null;

					log.debug("***MP " + iModifPoint.identified + " operator " + operator);

					if (!operator.canBeAppliedToPoint(iModifPoint))
						continue;

					// Decide if merge interfaces DyIng and MetaOp

					if (operator instanceof MetaOperator) {

						instancesOfOperatorForModificationPoint = new ArrayList<>();
						// Get Candidate expressions:

						List<MetaOperatorInstance> opInstancesMeta = null;

						// if the operator needs ingredients
						if (operator instanceof DynaIngredientOperator) {
							DynaIngredientOperator dynaop = (DynaIngredientOperator) operator;

							List<IngredientFromDyna> newIngredients = synthesizeCandidatesIngredientsFromType(
									parentVariant, iModifPoint, ingredientPool, dynaop.retrieveTargetTypeReference());

							opInstancesMeta = dynaop.createMetaOperatorInstances(iModifPoint, newIngredients);

						} else {
							// no ingredient needed
							opInstancesMeta = ((MetaOperator) operator).createMetaOperatorInstances(iModifPoint);
						}
						// We create one MetaProgram Variant per metaOperator
						for (MetaOperatorInstance metaPperatorInstance : opInstancesMeta) {

							MetaProgramVariant metai = new MetaProgramVariant(nrVariant++);
							metai.setParent(parentVariant);
							metai.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
							metai.putModificationInstance(generation, metaPperatorInstance);
							candidateProgramVariants.add(metai);
						}

					} else {
						// It's a "conventional" operator
						instancesOfOperatorForModificationPoint = operator.createOperatorInstances(iModifPoint);

						for (OperatorInstance operatorInstance : instancesOfOperatorForModificationPoint) {
							ProgramVariant newProgramVariant = new ProgramVariant(nrVariant++);
							newProgramVariant.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
							newProgramVariant.putModificationInstance(generation, operatorInstance);

							candidateProgramVariants.add(newProgramVariant);
						}
					}

					// For each candidate variant
					for (ProgramVariant iProgramVariant : candidateProgramVariants) {

						this.generationsExecuted++;

						// Apply the code transformations
						for (OperatorInstance operatorInstance : iProgramVariant.getAllOperations()) {
							operatorInstance.applyModification();
						}

						int generationEval = 0;

						// For the list of operator instance, we create a mutant program
						// Each program variant is a patch
						boolean resultValidation = this.processCreatedVariant(iProgramVariant, generationEval);
						if (resultValidation) {
							log.info("Solution found with Target " + targetElement + " operator " + operator);

							this.solutions.add(iProgramVariant);
							existSolution = true;
							// storing pred
							predictedCtElementWithSol.add(predictionsForModifPoint, predictionElement);
						}
						if (ConfigurationProperties.getPropertyBool("saveallevaluatedvariants")) {
							this.evaluatedProgramVariants.add(iProgramVariant);
						}

						// Undo the code transformations
						for (OperatorInstance operatorInstance : iProgramVariant.getAllOperations()) {
							operatorInstance.undoModification();

						}

						if (existSolution && ConfigurationProperties.getPropertyBool("stopfirst")) {
							return true;
						}
					}
				} catch (Exception e) {
					log.error("Error with operator " + operator.getClass().getSimpleName());
					log.error(e);
					e.printStackTrace();
				}
			}
		}
		return existSolution;
	}

	MapList<IPrediction, PredictionElement> predictedCtElementWithSol = new MapList<>();

	/**
	 * Returns true if there is an operator that needs ingredient
	 * 
	 * @param allOperationsPredicted
	 * @return
	 */
	private boolean oneOperatorNeedsDynamicIngredients(List<AstorOperator> allOperationsPredicted) {

		for (AstorOperator astorOperator : allOperationsPredicted) {
			if (astorOperator instanceof DynaIngredientOperator)
				return true;
		}

		return false;
	}

	public void setTargetElement(CtElement targetElement, AstorOperator operator) {

	}

	public IPrediction computePredictionsForModificationPoint(ModificationPoint iModifPoint) {

		if (this.predictor != null) {
			try {
				PredictionResult rp = predictor.computePredictionsForModificationPoint(iModifPoint);
				if (rp.size() > 0) {
					return rp.get(0);
				} else
					return null;
			} catch (Exception e) {
				log.error("#rror when calling predictor");
				log.error(e);
				return null;
			}

		} else {
			// No predictor, so we put all the operations available
			List<AstorOperator> ops = this.operatorSpace.getOperators();
			Prediction optoapply = new Prediction();
			optoapply.put(new PredictionElement(iModifPoint.getCodeElement()), ops);

			return optoapply;
		}
	}

	/**
	 * 
	 * Creates ingredients according to the TypeReference of an element. The
	 * ingredients are valid in the modification point location. TODO: Move to the
	 * operator?
	 * 
	 * @param parentVariant
	 * @param iModifPoint
	 * @param targetType
	 * @return
	 */
	public List<IngredientFromDyna> synthesizeCandidatesIngredientsFromType(ProgramVariant parentVariant,
			ModificationPoint iModifPoint, DynaIngredientPool clusterEvaluatedExpressions, CtTypeReference targetType) {

		List<IngredientFromDyna> newIngredientsResult = new ArrayList<>();

		// for (String i_testName : clusterEvaluatedExpressions.keySet()) {
		// TODO: see how to modify this

		if (clusterEvaluatedExpressions == null
				|| clusterEvaluatedExpressions.getClusterEvaluatedExpressions() == null) {
			return newIngredientsResult;
		}
		String i_testName = null;

		if (this.projectFacade.getProperties().getFailingTestCases().size() > 0
				&& clusterEvaluatedExpressions.getClusterEvaluatedExpressions()
						.containsKey(this.projectFacade.getProperties().getFailingTestCases().get(0))) {
			i_testName = this.projectFacade.getProperties().getFailingTestCases().get(0);
		} else {
			// The expression from one cluster
			Optional<String> findFirst = clusterEvaluatedExpressions.getClusterEvaluatedExpressions().keySet().stream()
					.findFirst();
			if (!findFirst.isPresent())
				return newIngredientsResult;
			i_testName = findFirst.get();
		}
		List<ClusterExpressions> clustersOfTest = clusterEvaluatedExpressions.getClusterEvaluatedExpressions()
				.get(i_testName);

		for (ClusterExpressions i_cluster : clustersOfTest) {
			// valuefromtesti++;
			if (i_cluster.size() > 0) {
				EvaluatedExpression firstExpressionOfCluster = i_cluster.get(0);

				operationsExecuted++;

				// let's check the types
				String classofExpression = i_cluster.getClusterType();

				String classofiHole = targetType.box().getQualifiedName();

				if (!ConfigurationProperties.getPropertyBool("avoidtypecomparison")// In case that we dont want
																					// to compare hole types
						&& !classofiHole.equals(classofExpression)) {
					continue;
				}

				IngredientFromDyna ingredientSynthesized = createIngredient(firstExpressionOfCluster);
				newIngredientsResult.add(ingredientSynthesized);

			}
			// }
		}
		return newIngredientsResult;
	}

	@Override
	public VariantValidationResult validateInstance(ProgramVariant variant) {
//to refactor...
		MultiMetaEvalTOSApproach multi;
		try {
			multi = new MultiMetaEvalTOSApproach(mutatorSupporter, projectFacade);
			multi.setProgramValidator(this.getProgramValidator());
			return multi.validateInstance(variant);
		} catch (JSAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void atEnd() {

		// Now, predictions
		// outPredictions();

		// Replace meta per "plain" variants
		List<ProgramVariant> previousSolutions = new ArrayList(this.solutions);

		//
		// Let's remove all solutions
		this.solutions.clear();
		// For each solution found
		for (ProgramVariant originalVariantSolution : previousSolutions) {

			if (originalVariantSolution instanceof MetaProgramVariant) {
				MetaProgramVariant metap = (MetaProgramVariant) originalVariantSolution;
				List<ProgramVariant> allPlainProgramVariant = metap.getAllPlainProgramVariant();
				// Let's verify that every solution in the meta passes all the test cases
				for (ProgramVariant plainNewVariantSolution : allPlainProgramVariant) {

					plainNewVariantSolution.setId(this.generationsExecuted++);
					// Apply the changes
					plainNewVariantSolution.getAllOperations().stream().forEach(e -> e.applyModification());
					try {
						// Check if solution
						boolean isSolution = this.processCreatedVariant(plainNewVariantSolution, 1);
						log.info("Ckecking solution of " + plainNewVariantSolution.getId() + " : " + isSolution);
						if ((isSolution))
							this.solutions.add(plainNewVariantSolution);
						else
							log.error("Failing Verification of " + plainNewVariantSolution.getId());

					} catch (Exception e1) {
						log.error(e1);
						e1.printStackTrace();
					}
					// Undo the changes
					plainNewVariantSolution.getAllOperations().stream().forEach(e -> e.undoModification());

				}

			} else {
				// We add the "plain" solutions
				this.solutions.add(originalVariantSolution);
			}

		}

		// We proceed with the analysis results
		super.atEnd();

		outPredictions();

	}

	/**
	 * Let's export the predictions
	 */
	public void outPredictions() {
		// Let's save the prediction info:
		List<ModificationPoint> mpKey = new ArrayList<>(this.predictions.keySet());
		// Sort the mp
		JsonObject root = new JsonObject();
		JsonArray mpoints = new JsonArray();
		root.add("mod_points", mpoints);
		root.addProperty("project_id", this.projectFacade.getProperties().getFixid());

		Collections.sort(mpKey);
		for (ModificationPoint imp : mpKey) {
			IPrediction ipred = this.predictions.get(imp);
			JsonElement jsonprediction = ipred.toJson();
			JsonObject mpobj = new JsonObject();
			mpobj.addProperty("id", imp.identified);
			mpobj.addProperty("line", imp.getCodeElement().getPosition().getLine());
			mpobj.addProperty("file", imp.getCodeElement().getPosition().getFile().getName());

			JsonObject predictionroot = new JsonObject();
			predictionroot.add("modif_point", mpobj);
			predictionroot.add("prediction", jsonprediction);

			JsonArray solut = new JsonArray();
			if (this.predictedCtElementWithSol.keySet().contains(ipred)) {
				List<PredictionElement> pred = this.predictedCtElementWithSol.get(ipred);
				for (PredictionElement psol : pred) {
					solut.add(psol.getIndex());
				}
			}

			predictionroot.add("solutions", solut);
			mpoints.add(predictionroot);

		}

		System.out.println("predout=" + root);
	}

	public List<ProgramVariant> getEvaluatedProgramVariants() {
		return evaluatedProgramVariants;
	}

	public IPredictor getPredictor() {
		return predictor;
	}

}
