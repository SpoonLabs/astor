package fr.inria.astor.approaches.tos.core.evalTos;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.core.evalTos.ingredients.ClusterExpressions;
import fr.inria.astor.approaches.tos.core.evalTos.ingredients.DynaIngredientPool;
import fr.inria.astor.approaches.tos.operator.DynaIngredientOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.CompositeMethodXMethodReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.ConstReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.IOperatorWithTargetElement;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicExpOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.LogicRedOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.MethodXVariableReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.OperatorReplacementOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.SupportOperators;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromIfOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.UnwrapfromMethodCallOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByMethodCallOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithIfNullCheck;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithIfOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.WrapwithTrySingleStatementOp;
import fr.inria.astor.core.entities.IngredientFromDyna;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.entities.meta.MetaProgramVariant;
import fr.inria.astor.core.entities.validation.VariantValidationResult;
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
	public static int MAX_GENERATIONS = 0;
	public int modifPointsAnalyzed = 0;

	public List<ProgramVariant> evaluatedProgramVariants = new ArrayList<>();

	protected IPredictor predictor = null;

	/**
	 * Keeps the prediction obtained for each modification point
	 */
	public Map<ModificationPoint, PredictionResult> predictions = new HashMap();

	/**
	 * Stores the prediction with a solution
	 */
	MapList<IPrediction, ProgramVariant> predictedVariantWithSol = new MapList<>();

	/**
	 * keeps the relation between the meta and the concrete
	 */
	MapList<ProgramVariant, ProgramVariant> metaToConcrete = new MapList<>();

	/**
	 * Attempts
	 * 
	 * @param mutatorExecutor
	 * @param projFacade
	 * @throws JSAPException
	 */
	Map<IPrediction, Integer> attempts = new HashMap<>();

	public static MapList<String, AstorOperator> operators = new MapList<>();

	static {
		// "addassignment"
		operators.add("wrapsTryCatch", new WrapwithTrySingleStatementOp());
		operators.add("wrapsIfElse_Others", new WrapwithIfOp());//
		operators.add("wrapsIf_Others", new WrapwithIfOp());// duplicate
		operators.add("wrapsIfElse_NULL", new WrapwithIfNullCheck());
		operators.add("wrapsIf_NULL", new WrapwithIfNullCheck());// duplicate
		//

		// wrapsLoop
		operators.add("VAR_RW_VAR", new VarReplacementByAnotherVarOp());
		operators.add("expLogicExpand", new LogicExpOperator());

		operators.add("Method_RW_Method", new CompositeMethodXMethodReplacementOp());

//		operators.add("Method_RW_Method", new MethodXMethodReplacementDiffNameOp());// TODO
//		operators.add("Method_RW_Method", new MethodXMethodReplacementDiffArgumentsOp());
//		operators.add("Method_RW_Method", new MethodXMethodReplacementArgumentRemoveOp());
		operators.add("unwrapMethod", new UnwrapfromMethodCallOp());
//		// ops.add("", new UnwrapfromTryOp());//ignored
		operators.add("unwrapIfElse", new UnwrapfromIfOp());
		operators.add("expLogicReduce", new LogicRedOperator());
		operators.add("VAR_RW_Method", new VarReplacementByMethodCallOp());
		operators.add("binOperatorModif", new OperatorReplacementOp());
		operators.add("constChange", new ConstReplacementOp());

		//
		operators.add("wrapsMethod", null);// TODO
		operators.add("Method_RW_Var", new MethodXVariableReplacementOp());// TODO
		//

	}

	public MultiMetaEvalTOSApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		MAX_GENERATIONS = ConfigurationProperties.getPropertyInt("maxGeneration");
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

		for (List<AstorOperator> astorOperationsFromAbstractOperator : operators.values()) {
			// We register all operators
			for (AstorOperator astorOperator : astorOperationsFromAbstractOperator) {
				this.operatorSpace.register(astorOperator);
			}

		}

	}

	private boolean reachTimeout = false;

	@SuppressWarnings("rawtypes")
	@Override
	public void startEvolution() throws Exception {

		reachTimeout = false;
		dateInitEvolution = new Date();
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
						+ " minutes passed,  location to modify: " + iModifPoint + " sol found: "
						+ this.solutions.size());

				log.debug("MP code: " + iModifPoint.getCodeElement());

				boolean existsSolution = analyzeModificationPoint(parentVariant, iModifPoint);

				if (existsSolution) {

					if (ConfigurationProperties.getPropertyBool("stopfirst") || (this.solutions
							.size() >= ConfigurationProperties.getPropertyInt("maxnumbersolutions"))) {
						log.info("Stoping solution research");
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						return;
					}
				}
				if (this.reachTimeout || !stillTime()) {
					this.setOutputStatus(AstorOutputStatus.TIME_OUT);
					log.info("Max time reached");
					return;
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operationsExecuted);

	}

	private boolean stillTime() {
		boolean stillTime = belowMaxTime(dateInitEvolution, ConfigurationProperties.getPropertyInt("maxtime"));
		if (!stillTime)
			this.reachTimeout = true;

		return stillTime;
	}

	int nrVariant = 1;
	DynaIngredientPool ingredientPool = null;

	@SuppressWarnings("unused")
	public boolean analyzeModificationPoint(ProgramVariant parentVariant, ModificationPoint iModifPoint)
			throws Exception {

		boolean existSolution = false;

		PredictionResult predictionsForModifPoint = computePredictionsForModificationPoint(iModifPoint);

		log.info("Elements to modify in MP " + iModifPoint.identified + ": " + predictionsForModifPoint.size());

		// No prediction, so, we return
		if (predictionsForModifPoint.isEmpty()) {
			log.debug("No prediction");
			return false;
		}

		// Hard Reset pool
		ingredientPool = null;
		int iPredictionOfPoint = 0;
		for (IPrediction i_prediction : predictionsForModifPoint) {
			log.info("Prediction nr " + (++iPredictionOfPoint) + "/" + predictionsForModifPoint.size());

			// boolean foundsol = analyzePrediction(parentVariant, iModifPoint,
			// i_prediction);

			existSolution = analyzePrediction(parentVariant, iModifPoint, i_prediction);

			if (existSolution) {
				existSolution = true;

				if (ConfigurationProperties.getPropertyBool("stopfirst")) {
					return true;
				}
			}
			if (!stillTime()) {
				log.info("Timeout reach at prediction " + (iPredictionOfPoint) + "/" + predictionsForModifPoint.size()
						+ " of mod point" + iModifPoint.identified);

				this.reachTimeout = true;
				return existSolution;
			}
		}
		return existSolution;
	}

	public boolean analyzePrediction(ProgramVariant parentVariant, ModificationPoint iModifPoint,
			IPrediction i_prediction) {

		List<AstorOperator> allOperationsPredicted = i_prediction.getAllOperationsPredicted();

		log.info("Predicted operators for " + iModifPoint.identified + " : " + allOperationsPredicted);

		// if it's not initialized
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

			if (candidateOperators == null || candidateOperators.isEmpty()) {
				log.error("No predicted operator for Target element: " + targetElement
						+ ", where prediction element is: " + predictionElement);
				continue;
			}

			AstorOperator operator = getSingleOperator(candidateOperators);

			log.info("Target: " + targetElement + ", operator applied:" + operator);

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
						// new MM 24-04
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

		if (opToInstances.isEmpty()) {
			log.debug("Any op instance after the prediction of mp: " + iModifPoint.identified);
			return false;
		}

		// Create the Program variant
		List<ProgramVariant> programVariantsOfPrediction = createVariants(opToInstances, parentVariant);
		allProgramVariants.addAll(programVariantsOfPrediction);

		// EVALUATION

		return evaluateProgramVariant(i_prediction, allProgramVariants);

	}

	public boolean evaluateProgramVariant(IPrediction i_prediction, List<ProgramVariant> allProgramVariants) {
		// For each candidate variant
		boolean existSolution = false;

		for (ProgramVariant iProgramVariant : allProgramVariants) {

			// this.generationsExecuted++;

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

					this.solutions.add(iProgramVariant);
					existSolution = true;
					// storing pred
					predictedVariantWithSol.add(i_prediction, iProgramVariant);
					try {
						log.info("Meta Solution summary: \n"
								+ getSolutionString(iProgramVariant.getOperations().keySet().size(), iProgramVariant));
					} catch (Exception e) {
						log.error("Error printing the meta solution ");
					}
				}
				if (ConfigurationProperties.getPropertyBool("saveallevaluatedvariants")) {
					this.evaluatedProgramVariants.add(iProgramVariant);
				}
				saveAttempts(i_prediction, iProgramVariant);

				// Undo the code transformations
				for (OperatorInstance operatorInstance : iProgramVariant.getAllOperations()) {
					operatorInstance.undoModification();

				}

				if (existSolution && ConfigurationProperties.getPropertyBool("stopfirst")) {
					return true;
				}

				if (!stillTime()) {
					log.info("Timeout reach at variant evaluation " + iProgramVariant.getId());
					this.reachTimeout = true;
					return existSolution;
				}

			} catch (Exception e) {
				log.error("Error validating variant " + iProgramVariant.getId());
				log.error(e);
				e.printStackTrace();
			}
		}
		return existSolution;
	}

	private void saveAttempts(IPrediction i_prediction, ProgramVariant iProgramVariant) {
		int v = 0;
		VariantValidationResult validation = iProgramVariant.getValidationResult();
		if (validation != null) {

			if (validation instanceof MetaValidationResult) {
				MetaValidationResult mvalidation = (MetaValidationResult) validation;
				v = mvalidation.getAllCandidates().size();
			} else {
				// a single validation
				v = 1;
			}
		}
		Integer atpred = this.attempts.get(i_prediction);
		if (atpred == null) {
			atpred = 0;
		}
		this.attempts.put(i_prediction, (atpred + v));
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

			newProgramVariant.copyModificationPoints(parentVariant.getModificationPoints());

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
				PredictionResult pr = predictor.computePredictionsForModificationPoint(iModifPoint);
				this.predictions.put(iModifPoint, pr);
				return pr;
			} catch (Exception e) {
				log.error("#rror when calling predictor");
				log.error(e);
				this.predictions.put(iModifPoint, null);
				return null;
			}

		} else {
			PredictionResult rp = new PredictionResult();
			List<AstorOperator> ops = this.operatorSpace.getOperators();

			// No predictor, so we put all the operations available
			for (AstorOperator astorOperator : ops) {
				Prediction optoapply = new Prediction();
				optoapply.add(new PredictionElement(iModifPoint.getCodeElement()), astorOperator);
				rp.add(optoapply);
			}
			this.predictions.put(iModifPoint, rp);
			return rp;
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

				this.generationsExecuted++;
				log.info("Eval pv: " + variant.getId() + ", mut nr " + candidateNumber + " / " + allCandidates.size());
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

				if (!stillTime()) {
					log.info("Stop evaluating meta due to timeout at candidate " + candidateNumber);
					this.reachTimeout = true;
					break;
				}

				candidateNumber++;

			}

			variant.setValidationResult(megavalidation);
			variant.setIsSolution(megavalidation.isSuccessful());

			return megavalidation;

		} else {
			this.generationsExecuted++;
			// by default
			return super.validateInstance(variant);
		}
	}

	@Override
	public void atEnd() {

		// Replace meta per "plain" variants
		List<ProgramVariant> previousSolutions = new ArrayList(this.solutions);

		log.info("Meta Analysis: Nr Solutions " + previousSolutions.size());

		log.info(this.getSolutionData(previousSolutions, this.generationsExecuted) + "\n");
		//
		// Let's remove all solutions
		this.solutions.clear();
		// For each solution found
		for (ProgramVariant originalVariantSolution : previousSolutions) {

			if (originalVariantSolution instanceof MetaProgramVariant) {

				log.info("Solution found Meta pv " + originalVariantSolution);

				MetaProgramVariant metap = (MetaProgramVariant) originalVariantSolution;
				List<ProgramVariant> allPlainProgramVariant = metap.getAllPlainProgramVariant();
				// Let's verify that every solution in the meta passes all the test cases
				for (ProgramVariant plainNewVariantSolution : allPlainProgramVariant) {

					plainNewVariantSolution.setId(this.generationsExecuted++);
					// Apply the changes

					for (OperatorInstance oi : plainNewVariantSolution.getAllOperations()) {
						try {
							boolean applyied = oi.applyModification();
							log.debug("applied op " + oi.getOperationApplied().name() + ": " + applyied);

							log.debug(oi.getModified());
						} catch (Exception e) {
							log.error("Error when calculating plain variant from meta");
							log.error(e);
						}
					}

					try {
						// Check if solution
						boolean isSolution = this.processCreatedVariant(plainNewVariantSolution, 1);
						log.info("Ckecking solution of " + plainNewVariantSolution.getId() + " : " + isSolution);
						if ((isSolution)) {
							this.solutions.add(plainNewVariantSolution);
							// Save the "parent" relation between the variants
							this.metaToConcrete.add(metap, plainNewVariantSolution);

						} else
							log.error("Failing Verification of " + plainNewVariantSolution.getId());

					} catch (Exception e1) {
						log.error(e1);
						e1.printStackTrace();
					}
					// Undo the changes
					for (int i = plainNewVariantSolution.getAllOperations().size() - 1; i >= 0; i--) {
						OperatorInstance opi = plainNewVariantSolution.getAllOperations().get(i);
						opi.undoModification();
					}

				}

			} else {
				// We add the "plain" solutions
				log.info("Solution found not meta pv " + originalVariantSolution);
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

			JsonObject mpobj = new JsonObject();
			mpoints.add(mpobj);
			mpobj.addProperty("id", imp.identified);
			mpobj.addProperty("line", imp.getCodeElement().getPosition().getLine());
			mpobj.addProperty("file", imp.getCodeElement().getPosition().getFile().getName());

			JsonObject predictionroot = new JsonObject();
			predictionroot.add("modif_point", mpobj);

			JsonArray arraypred = new JsonArray();
			mpobj.add("predictions", arraypred);

			for (IPrediction iPrediction : pr) {
				if (iPrediction == null)
					continue;

				JsonObject predjson = new JsonObject();
				JsonElement jsonprediction = iPrediction.toJson();
				arraypred.add(predjson);
				predjson.add("info", jsonprediction);

				//
				Integer attemptsOfPrediction = this.attempts.get(iPrediction);
				predjson.addProperty("attempts", attemptsOfPrediction);

				//
				JsonArray solut = new JsonArray();
				predjson.add("patches", solut);
				if (this.predictedVariantWithSol.keySet().contains(iPrediction)) {

					List<ProgramVariant> pred = this.predictedVariantWithSol.get(iPrediction);
					for (ProgramVariant variantWithSolution : pred) {

						if (variantWithSolution.isSolution()) {

							if (this.metaToConcrete.containsKey(variantWithSolution)) {
								for (ProgramVariant concreteVariantForMeta : this.metaToConcrete
										.get(variantWithSolution)) {
									log.info("Getting conc diff from " + concreteVariantForMeta.getId());

									solut.add(
											concreteVariantForMeta.getPatchDiff().getOriginalStatementAlignmentDiff());
								}

							} else {
								// it's not a meta variant
								log.info("Getting normal diff from " + variantWithSolution.getId());
								if (variantWithSolution.getPatchDiff() != null) {
									solut.add(variantWithSolution.getPatchDiff().getOriginalStatementAlignmentDiff());
								} else
									log.error("variant without diff " + variantWithSolution.getId());
							}

						}
					}
				}

			}

		}

		System.out.println("predout=" + root);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String ppjson = gson.toJson(root);

		// String out = (ConfigurationProperties.getProperty("out") != null) ?
		// ConfigurationProperties.getProperty("out")
		// : ConfigurationProperties.getProperty("workingDirectory");
		String out = this.projectFacade.getProperties().getWorkingDirRoot();
		String outpath = out + File.separator + "prediction" + ".json";
		log.info("Saving json at \n" + outpath);
		try {
			FileWriter fw = new FileWriter(new File(outpath));
			fw.write(ppjson);
			fw.flush();
			fw.close();
		} catch (IOException e) {

			e.printStackTrace();
			log.error(e);
		}

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
