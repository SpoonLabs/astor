package fr.inria.astor.approaches.tos.core.evalTos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.core.evalTos.ingredients.ClusterExpressions;
import fr.inria.astor.approaches.tos.core.evalTos.ingredients.DynaIngredientPool;
import fr.inria.astor.approaches.tos.operator.DynaIngredientOperator;
import fr.inria.astor.approaches.tos.operator.metaevaltos.VarReplacementByAnotherVarOp;
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
import fr.inria.main.AstorOutputStatus;
import spoon.reflect.reference.CtTypeReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetaEvalTOSApproach extends EvalTOSClusterApproach {

	public int MAX_GENERATIONS = ConfigurationProperties.getPropertyInt("maxGeneration");
	public int modifPointsAnalyzed = 0;
	public int operationsExecuted = 0;

	public MetaEvalTOSApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.operatorSpace = new OperatorSpace();

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void startEvolution() throws Exception {

		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		modifPointsAnalyzed = 0;

		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			if (MAX_GENERATIONS <= operationsExecuted) {

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

				boolean stop = analyzeModificationPoint(parentVariant, iModifPoint);

				if (stop) {
					return;
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
		// TODO:
		int generation = 1;

		DynaIngredientPool poolFromModifPoint = this.getClusteredEvaluatedExpression(iModifPoint);

		// Call to the extension point to get the order
		// We take each operator, in the order given by the EP
		for (AstorOperator operator : this.operatorSpace.getOperators()) {

			List<ProgramVariant> candidateProgramVariants = new ArrayList<>();
			List<OperatorInstance> instancesOfOperatorForModificationPoint = null;

			// Decide if merge interfaces DyIng and MetaOp
			if (operator instanceof DynaIngredientOperator) {
				//
				DynaIngredientOperator dynaop = (DynaIngredientOperator) operator;

				instancesOfOperatorForModificationPoint = new ArrayList<>();
				// Get Candidate expressions:

				List<IngredientFromDyna> newIngredients = synthesizeCandidatesIngredientsFromType(parentVariant,
						iModifPoint, poolFromModifPoint, dynaop.retrieveTargetTypeReference());

				List<OperatorInstance> opInstancesMetha = dynaop.createOperatorInstances(iModifPoint, newIngredients);

				MetaProgramVariant metai = new MetaProgramVariant(nrVariant++);
				metai.setParent(parentVariant);
				metai.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
				for (OperatorInstance operatorInstance : opInstancesMetha) {
					metai.putModificationInstance(generation, operatorInstance);
				}

				candidateProgramVariants.add(metai);

				//
			} else if (operator instanceof MetaOperator) {

				instancesOfOperatorForModificationPoint = new ArrayList<>();
				// Get Candidate expressions:
				List<OperatorInstance> opInstancesMetha = operator.createOperatorInstances(iModifPoint);

				MetaProgramVariant metai = new MetaProgramVariant(nrVariant++);
				metai.setParent(parentVariant);
				metai.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
				for (OperatorInstance operatorInstance : opInstancesMetha) {
					metai.putModificationInstance(generation, operatorInstance);
				}

				candidateProgramVariants.add(metai);

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

			//

			// For each candidate variant
			for (ProgramVariant iProgramVariant : candidateProgramVariants) {

				this.generationsExecuted++;

				// Apply the code transformations
				for (OperatorInstance operatorInstance : iProgramVariant.getAllOperations()) {
					operatorInstance.applyModification();

				}

				int generationEval = 0;

				if (iProgramVariant instanceof MetaProgramVariant) {
					// For the list of operator instance, we create a mutant program
					// Each program variant is a patch
					boolean resultValidation = this.processCreatedVariant(iProgramVariant, generationEval);
					if (resultValidation) {
						this.solutions.add(iProgramVariant);
					}

				} else {
					boolean resultValidation = this.processCreatedVariant(iProgramVariant, generationEval);
					if (resultValidation) {
						this.solutions.add(iProgramVariant);
					}
				}

				// Undo the code transformations
				for (OperatorInstance operatorInstance : iProgramVariant.getAllOperations()) {
					operatorInstance.undoModification();

				}
			}
		}
		return false;
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
		String i_testName = clusterEvaluatedExpressions.getClusterEvaluatedExpressions().keySet().stream().findFirst()
				.get();

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
	protected void loadOperatorSpaceDefinition() throws Exception {
		this.operatorSpace = new OperatorSpace();
		this.operatorSpace.register(new WrapwithTrySingleStatementOp());
		this.operatorSpace.register(new WrapwithIfOp());
		this.operatorSpace.register(new WrapwithIfNullCheck());
		this.operatorSpace.register(new VarReplacementByAnotherVarOp());
	}

	@Override
	public VariantValidationResult validateInstance(ProgramVariant variant) {

		if (variant instanceof MetaProgramVariant) {
			MetaValidationResult megavalidation = new MetaValidationResult();
			MetaProgramVariant megavariant = (MetaProgramVariant) variant;

			for (MetaOperatorInstance moi : megavariant.getMetaOpInstances()) {

				for (Integer idMutant : moi.getAllIngredients().keySet()) {
					ConfigurationProperties.setProperty("metid", idMutant.toString());
					VariantValidationResult validation_single = super.validateInstance(variant);
					megavalidation.addValidation(idMutant, validation_single);
					if (validation_single.isSuccessful())
						log.debug("Solution " + idMutant.toString());
				}
			}

			variant.setValidationResult(megavalidation);

			return megavalidation;

		} else
			// by default
			return super.validateInstance(variant);

	}

	@Override
	public void atEnd() {
		// Replace meta per "plain" variants
		List<ProgramVariant> previousSolutions = new ArrayList(this.solutions);

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
	}

}
