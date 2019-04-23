package fr.inria.astor.approaches.tos.core.evalTos;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

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
import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementArgumentRemoveOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffArgumentsOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXMethodReplacementDiffNameOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXVariableReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.SupportOperators;
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
import fr.inria.astor.core.validation.results.MetaValidationResult;
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
public class MultiMetaEvalTOSApproach extends EvalTOSClusterApproach {

	public static final String METID = "metid";
	public static final String METALL = "metid";
	public static int MAX_GENERATIONS = ConfigurationProperties.getPropertyInt("maxGeneration");
	public int modifPointsAnalyzed = 0;

	public List<ProgramVariant> evaluatedProgramVariants = new ArrayList<>();

	protected IPredictor predictor = null;

	public Map<ModificationPoint, PredictionResult> predictions = new HashMap();

	public MultiMetaEvalTOSApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
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
		// TODO: add statement
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
	DynaIngredientPool ingredientPool = null;

	@SuppressWarnings("unused")
	public boolean analyzeModificationPoint(ProgramVariant parentVariant, ModificationPoint iModifPoint)
			throws Exception {

		int generation = 1;

		boolean existSolution = false;

		PredictionResult predictionsForModifPoint = computePredictionsForModificationPoint(iModifPoint);

		log.info("Elements to modify in MP " + iModifPoint.identified + ": " + predictionsForModifPoint.size());
		this.predictions.put(iModifPoint, predictionsForModifPoint);

		// No prediction, so, we return
		if (predictionsForModifPoint.isEmpty()) {
			log.debug("No prediction");
			return false;
		}

		this.predictions.put(iModifPoint, predictionsForModifPoint);

		// Hard Reset pool
		ingredientPool = null;
		int i = 0;
		for (IPrediction i_prediction : predictionsForModifPoint) {
			log.info("Prediction nr " + (++i) + "/" + predictionsForModifPoint.size());

			existSolution = analyzePrediction(parentVariant, iModifPoint, generation, i_prediction);

			if (existSolution && ConfigurationProperties.getPropertyBool("stopfirst")) {
				return true;
			}
		}
		return existSolution;
	}

	public boolean analyzePrediction(ProgramVariant parentVariant, ModificationPoint iModifPoint, int generation,
			IPrediction i_prediction) {

		boolean existSolution = false;
		List<AstorOperator> allOperationsPredicted = i_prediction.getAllOperationsPredicted();

		log.info("Predicted operators for " + iModifPoint.identified + " : " + allOperationsPredicted);

		initDynaPool(iModifPoint, allOperationsPredicted);

		/// Here will be the resulting variants:
		List<ProgramVariant> allProgramVariants = new ArrayList<>();

		MapList<PredictionElement, OperatorInstance> opToInstances = new MapList<>();

		for (PredictionElement predictionElement : i_prediction.getElementsWithPrediction()) {

			CtElement targetElement = predictionElement.getElement();

			if (targetElement == null) {
				continue;
			}

			List<AstorOperator> candidateOperators = i_prediction.getPrediction(predictionElement);

			AstorOperator operator = getSingleOperator(candidateOperators);

			log.info("Target " + targetElement + " operator " + operator);

			if (operator == null) {
				log.error("No operator to apply");
				continue;
			}

			// Set the target element in the operator//This needs refactor
			if (!configureTarget(targetElement, operator)) {
				continue;
			}

			try {
				List<OperatorInstance> instancesOfOperatorForModificationPoint = null;

				//

				log.debug("***MP " + iModifPoint.identified + " operator " + operator);

				if (!operator.canBeAppliedToPoint(iModifPoint)) {
					log.debug("***Error: MP " + iModifPoint.identified + " cannot be applied to operator " + operator);
					continue;
				}

				if (operator instanceof MetaOperator) {

					instancesOfOperatorForModificationPoint = new ArrayList<>();
					// Get Candidate expressions:

					List<MetaOperatorInstance> opInstancesMeta = null;

					// if the operator needs ingredients
					if (operator instanceof DynaIngredientOperator) {
						DynaIngredientOperator dynaop = (DynaIngredientOperator) operator;

						List<IngredientFromDyna> newIngredients = synthesizeCandidatesIngredientsFromType(parentVariant,
								iModifPoint, ingredientPool, dynaop.retrieveTargetTypeReference());

						opInstancesMeta = dynaop.createMetaOperatorInstances(iModifPoint, newIngredients);

					} else {
						// no ingredient needed
						opInstancesMeta = ((MetaOperator) operator).createMetaOperatorInstances(iModifPoint);
					}
					// We create one MetaProgram Variant per metaOperator
					for (MetaOperatorInstance metaPperatorInstance : opInstancesMeta) {
						//
						metaPperatorInstance.applyModification();
						//
						opToInstances.add(predictionElement, metaPperatorInstance);
					}

				} else {
					// It's a "conventional" operator
					instancesOfOperatorForModificationPoint = operator.createOperatorInstances(iModifPoint);

					for (OperatorInstance operatorInstance : instancesOfOperatorForModificationPoint) {
						// opToInstances.add(operator, operatorInstance);
						opToInstances.add(predictionElement, operatorInstance);

					}
				}

			} catch (Exception e) {
				log.error("Error with operator " + operator.getClass().getSimpleName());
				log.error(e);
				e.printStackTrace();
			}

		}
		// Create the Program variant
		List<ProgramVariant> programVariantsOfPrediction = createVariants(opToInstances, parentVariant);
		allProgramVariants.addAll(programVariantsOfPrediction);

		// EVALUATION

		// For each candidate variant
		for (ProgramVariant iProgramVariant : allProgramVariants) {

			this.generationsExecuted++;

			// Apply the code transformations
			for (OperatorInstance operatorInstance : iProgramVariant.getAllOperations()) {
				operatorInstance.applyModification(); // MM 23/4/19
			}

			int generationEval = 0;

			try {
				// For the list of operator instance, we create a mutant program
				// Each program variant is a patch
				boolean resultValidation = this.processCreatedVariant(iProgramVariant, generationEval);
				if (resultValidation) {
					// TODO
					// log.info("Solution found with Target " + targetElement + " operator " +
					// operator);

					this.solutions.add(iProgramVariant);
					existSolution = true;
					// storing pred
					// predictedCtElementWithSol.add(i_prediction, predictionElement);
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
			} catch (Exception e) {
				log.error("Error validating variant " + iProgramVariant.getId());
				log.error(e);
				e.printStackTrace();
			}
		}

		// end prediction
		return existSolution;

	}

	/**
	 * This method should disappear
	 * 
	 * @param targetElement
	 * @param operator
	 * @return
	 */
	public boolean configureTarget(CtElement targetElement, AstorOperator operator) {
		if (this.predictor != null) {

			IOperatorWithTargetElement targetOp = (IOperatorWithTargetElement) operator;
			if (targetOp.checkTargetCompatibility(targetElement)) {
				targetOp.setTargetElement(targetElement);
				return true;
			} else {
				// Target not compatible with operator, we continue
				targetOp.setTargetElement(null);
				// continue;
				return false;
			}
		}
		return true;
	}

	public List<ProgramVariant> createVariants(MapList<PredictionElement, OperatorInstance> map,
			ProgramVariant parentVariant) {

		List<ProgramVariant> allProgramVariants = new ArrayList<>();

		doCombination(map, new LinkedList<PredictionElement>(map.keySet()).listIterator(),
				new HashMap<PredictionElement, OperatorInstance>(), allProgramVariants, parentVariant);

		return allProgramVariants;
	}

	private void doCombination(MapList<PredictionElement, OperatorInstance> opInstances,
			ListIterator<PredictionElement> operators, Map<PredictionElement, OperatorInstance> current,
			List<ProgramVariant> allProgramVariants, ProgramVariant parentVariant) {

		if (!operators.hasNext()) {

			boolean hasMetaOp = current.values().stream().filter(e -> e instanceof MetaOperatorInstance).findFirst()
					.isPresent();
			ProgramVariant newProgramVariant = null;

			if (hasMetaOp) {
				newProgramVariant = new MetaProgramVariant(nrVariant++);

			} else {
				newProgramVariant = new ProgramVariant(nrVariant++);
			}

			int generation = 1;
			for (OperatorInstance operatorInstance : current.values()) {
				newProgramVariant.setParent(parentVariant);
				newProgramVariant.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
				newProgramVariant.putModificationInstance(generation, operatorInstance);
				generation++;
			}
			allProgramVariants.add(newProgramVariant);

		} else {
			PredictionElement key = operators.next();
			List<OperatorInstance> set = opInstances.get(key);

			for (OperatorInstance value : set) {
				current.put(key, value);
				doCombination(opInstances, operators, current, allProgramVariants, parentVariant);
				current.remove(key);
			}

			operators.previous();
		}
	}

	protected void initDynaPool(ModificationPoint iModifPoint, List<AstorOperator> allOperationsPredicted) {
		// Initialize
		if (ingredientPool == null) {
			if (oneOperatorNeedsDynamicIngredients(allOperationsPredicted)) {
				ingredientPool = this.getClusteredEvaluatedExpression(iModifPoint);

				log.info("Dyna Ingredients of modify in MP " + iModifPoint.identified + ": "
						+ ((ingredientPool.getClusterEvaluatedExpressions() != null)
								? ingredientPool.getClusterEvaluatedExpressions().size()
								: "Dynamoth null"));
			} else {
				log.debug("Any operator needs ingredient");
			}
		}
	}

	protected AstorOperator getSingleOperator(List<AstorOperator> candidateOperators) {
		if (candidateOperators.size() > 1) {
			log.info("More than 1 predicted operator");
		}

		if (candidateOperators.size() > 0)
			return candidateOperators.get(0);
		return null;
	}

	// List<IPrediction> predictWithSol = new ArrayList<>();
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

	public PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint) {

		if (this.predictor != null) {
			try {
				return predictor.computePredictionsForModificationPoint(iModifPoint);
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

			PredictionResult pr = new PredictionResult();
			pr.add(optoapply);

			return pr;
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

		for (ClusterExpressions i_cluster : clusterEvaluatedExpressions.getClusterEvaluatedExpressions()) {

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

		if (variant instanceof MetaProgramVariant) {
			MetaProgramVariant megavariant = (MetaProgramVariant) variant;

			// Get all ids of moi
			String ids = "";
			Map<Integer, Set<Integer>> mutants = new HashMap<>();
			for (MetaOperatorInstance moi : megavariant.getMetaOpInstances()) {
				ids += ((!ids.isEmpty()) ? File.pathSeparator : "") + moi.getIdentifier();
				mutants.put(moi.getIdentifier(), moi.getAllIngredients().keySet());
			}
			ConfigurationProperties.setProperty(MultiMetaEvalTOSApproach.METALL, ids);

			// now, calculate all combinations of evaluations:
			List<Map<Integer, Integer>> allCandidates = SupportOperators.combinations(mutants);
			MetaValidationResult megavalidation = new MetaValidationResult(allCandidates);

			// for each candidate
			int candidateNumber = 1;
			for (Map<Integer, Integer> candidate : allCandidates) {

				log.debug("Evaluating candidate nr " + candidateNumber + " out of " + allCandidates.size());
				log.debug("Feature of candidate " + candidateNumber + ": " + candidate);
				// for each point we put a value:
				for (Integer mutid : candidate.keySet()) {

					ConfigurationProperties.setProperty(MetaGenerator.MUT_IDENTIFIER + mutid,
							candidate.get(mutid).toString());

				}
				// now we validate
				VariantValidationResult validation_single = super.validateInstance(variant);
				if (validation_single != null) {
					megavalidation.addValidation(candidateNumber, validation_single);
					if (validation_single.isSuccessful())
						log.debug("Solution found " + candidateNumber);
				} else {
					log.error("Validation Null for metaid " + candidateNumber);
				}

				candidateNumber++;
			}

			variant.setValidationResult(megavalidation);

			return megavalidation;

		} else
			// by default
			return super.validateInstance(variant);

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
			PredictionResult pr = this.predictions.get(imp);
			// TODO: for all.
			IPrediction ipred = pr.get(0);
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

	public void setPredictor(IPredictor predictor) {
		this.predictor = predictor;
	}

}
