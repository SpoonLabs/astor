package fr.inria.astor.core.solutionsearch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.antipattern.AntiPattern;
import org.apache.commons.collections.map.HashedMap;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.util.StringUtil;
import fr.inria.main.AstorOutputStatus;

/**
 * Evolutionary program transformation Loop
 * 
 * @author Matias Martinez
 *
 */
public class EvolutionarySearchEngine extends AstorCoreEngine {

	public EvolutionarySearchEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	public void startEvolution() throws Exception {

		log.info("----Starting Solution Search");

		generationsExecuted = 0;
		nrGenerationWithoutModificatedVariant = 0;
		boolean stopSearch = false;

		dateInitEvolution = new Date();

		int maxMinutes = ConfigurationProperties.getPropertyInt("maxtime");

		while (!stopSearch) {

			if (!(generationsExecuted < ConfigurationProperties.getPropertyInt("maxGeneration"))) {
				log.debug("\n Max generation reached " + generationsExecuted);
				this.outputStatus = AstorOutputStatus.MAX_GENERATION;
				break;
			}

			if (!(belowMaxTime(dateInitEvolution, maxMinutes))) {
				log.debug("\n Max time reached " + generationsExecuted);
				this.outputStatus = AstorOutputStatus.TIME_OUT;
				break;
			}

			generationsExecuted++;
			// warning level to keep Travis CI log aliive
			log.warn("----------Running generation: " + generationsExecuted + ", population size: "
					+ this.variants.size());
			try {
				boolean solutionFound = processGenerations(generationsExecuted);

				if (solutionFound) {
					stopSearch =
							// one solution
							(ConfigurationProperties.getPropertyBool("stopfirst")
									// or nr solutions are greater than max allowed
									|| (this.solutions.size() >= ConfigurationProperties
											.getPropertyInt("maxnumbersolutions")));

					if (stopSearch) {
						log.debug("Max Solution found " + this.solutions.size());
						this.outputStatus = AstorOutputStatus.STOP_BY_PATCH_FOUND;
					}
				}
			} catch (Throwable e) {
				log.error("Error at generation " + generationsExecuted + "\n" + e);
				e.printStackTrace();
				this.outputStatus = AstorOutputStatus.ERROR;
				break;
			}

			if (this.nrGenerationWithoutModificatedVariant >= ConfigurationProperties
					.getPropertyInt("nomodificationconvergence")) {
				log.error(String.format("Stopping main loop at %d generation", generationsExecuted));
				this.outputStatus = AstorOutputStatus.CONVERGED;
				break;
			}
		}

	}

	public OperatorInstance createOperatorInstanceForPoint(ModificationPoint modificationPoint)
			throws IllegalAccessException {
		SuspiciousModificationPoint suspModificationPoint = (SuspiciousModificationPoint) modificationPoint;

		AstorOperator operatorSelected = operatorSelectionStrategy.getNextOperator(suspModificationPoint);

		if (operatorSelected == null) {
			log.debug("Operation Null");
			return null;
		}

		List<OperatorInstance> operatorInstances = operatorSelected.createOperatorInstances(modificationPoint);

		return selectRandomly(operatorInstances);
	}

	protected OperatorInstance selectRandomly(List<OperatorInstance> operatorInstances) {
		if (operatorInstances != null && operatorInstances.size() > 0) {

			return operatorInstances.get(RandomManager.nextInt(operatorInstances.size()));
		}

		return null;
	}

	/**
	 * Process a generation i: loops over all instances
	 * 
	 * @param generation
	 * @return
	 * @throws Exception
	 */
	private boolean processGenerations(int generation) throws Exception {

		log.debug("\n***** Generation " + generation + " : " + this.nrGenerationWithoutModificatedVariant);
		boolean foundSolution = false, foundOneVariant = false;

		List<ProgramVariant> temporalInstances = new ArrayList<ProgramVariant>();

		currentStat.increment(GeneralStatEnum.NR_GENERATIONS);

		for (ProgramVariant parentVariant : variants) {

			log.debug("**Parent Variant: " + parentVariant);

			this.saveOriginalVariant(parentVariant);
			ProgramVariant newVariant = createNewProgramVariant(parentVariant, generation);
			this.saveModifVariant(parentVariant);

			if (newVariant == null) {
				continue;
			}

			boolean solution = false;

			if(ConfigurationProperties.getPropertyBool("antipattern")){
			  if(!AntiPattern.isAntiPattern(newVariant, generation)){
          temporalInstances.add(newVariant);
          solution = processCreatedVariant(newVariant, generation);
        }
      }else{
        temporalInstances.add(newVariant);
        solution = processCreatedVariant(newVariant, generation);
      }

			if (solution) {
				foundSolution = true;
				newVariant.setBornDate(new Date());
			}
			foundOneVariant = true;
			// Finally, reverse the changes done by the child
			reverseOperationInModel(newVariant, generation);
			boolean validation = this.validateReversedOriginalVariant(newVariant);
			if (foundSolution && ConfigurationProperties.getPropertyBool("stopfirst")) {
				break;
			}

		}
		prepareNextGeneration(temporalInstances, generation);

		if (!foundOneVariant)
			this.nrGenerationWithoutModificatedVariant++;
		else {
			this.nrGenerationWithoutModificatedVariant = 0;
		}

		return foundSolution;
	}

	/**
	 * Create a child mutated. Return null if not mutation is produced by the engine
	 * (i.e. the child is equal to the parent)
	 * 
	 * @param parentVariant
	 * @param generation
	 * @param idsChild
	 * @return
	 * @throws Exception
	 */
	protected ProgramVariant createNewProgramVariant(ProgramVariant parentVariant, int generation) throws Exception {
		// This is the copy of the original program
		ProgramVariant childVariant = variantFactory.createProgramVariantFromAnother(parentVariant, generation);
		log.debug("\n--Child created id: " + childVariant.getId());

		// Apply previous operations (i.e., from previous operators)
		applyPreviousOperationsToVariantModel(childVariant, generation);

		boolean isChildMutatedInThisGeneration = modifyProgramVariant(childVariant, generation);

		if (!isChildMutatedInThisGeneration) {
			log.debug("--Not Operation generated in child variant: " + childVariant);
			reverseOperationInModel(childVariant, generation);
			return null;
		}

		boolean appliedOperations = applyNewOperationsToVariantModel(childVariant, generation);

		if (!appliedOperations) {
			log.debug("---Not Operation applied in child variant:" + childVariant);
			reverseOperationInModel(childVariant, generation);
			return null;
		}

		return childVariant;
	}

	Map<Integer, List<OperatorInstance>> operationGenerated = new HashedMap();

	protected boolean alreadyApplied(OperatorInstance operationNew) {

		List<OperatorInstance> ops = operationGenerated.get(operationNew.getModificationPoint().identified);
		if (ops == null) {
			ops = new ArrayList<>();
			operationGenerated.put(operationNew.getModificationPoint().identified, ops);
			ops.add(operationNew);
			return false;
		}
		return ops.contains(operationNew);
	}

	/**
	 * Given a program variant, the method generates operations for modifying that
	 * variants. Each operation is related to one gen of the program variant.
	 * 
	 * @param variant
	 * @param generation
	 * @return
	 * @throws Exception
	 */
	private boolean modifyProgramVariant(ProgramVariant variant, int generation) throws Exception {

		log.debug("--Creating new operations for variant " + variant);
		boolean oneOperationCreated = false;
		int genMutated = 0, notmut = 0, notapplied = 0;
		int nroGen = 0;

		this.currentStat.getIngredientsStats().sizeSpaceOfVariant.clear();

		// We retrieve the list of modification point ready to be navigated
		// sorted a criterion
		List<ModificationPoint> modificationPointsToProcess = this.suspiciousNavigationStrategy
				.getSortedModificationPointsList(variant.getModificationPoints());

		for (ModificationPoint modificationPoint : modificationPointsToProcess) {

			log.debug("---analyzing modificationPoint position: " + modificationPoint.identified);

			// A point can be modified several time in the evolution
			boolean multiPointMutation = ConfigurationProperties.getPropertyBool("multipointmodification");
			if (!multiPointMutation && alreadyModified(modificationPoint, variant.getOperations(), generation))
				continue;

			modificationPoint.setProgramVariant(variant);
			OperatorInstance modificationInstance = createOperatorInstanceForPoint(modificationPoint);

			if (modificationInstance != null) {

				modificationInstance.setModificationPoint(modificationPoint);

				if (ConfigurationProperties.getPropertyBool("uniqueoptogen") && alreadyApplied(modificationInstance)) {
					log.debug("---Operation already applied to the gen " + modificationInstance);
					currentStat.getIngredientsStats().setAlreadyApplied(variant.getId());
					continue;
				}
				log.debug("location: " + modificationPoint.getCodeElement().getPosition().getFile().getName()
						+ modificationPoint.getCodeElement().getPosition().getLine());
				log.debug("operation: " + modificationInstance);
				variant.putModificationInstance(generation, modificationInstance);

				oneOperationCreated = true;
				genMutated++;
				// We analyze all gens
				if (!ConfigurationProperties.getPropertyBool("allpoints")) {
					break;
				}

			} else {// Not gen created
				log.debug("---modifPoint " + (nroGen++) + " not mutation generated in  "
						+ StringUtil.trunc(modificationPoint.getCodeElement().toString()));
				notmut++;
			}
		}

		if (oneOperationCreated && !ConfigurationProperties.getPropertyBool("resetoperations")) {
			updateVariantGenList(variant, generation);
		}
		log.debug("\n--Summary Creation: for variant " + variant + " gen mutated: " + genMutated + " , gen not mut: "
				+ notmut + ", gen not applied  " + notapplied);

		currentStat.getIngredientsStats().commitStatsOfTrial();

		return oneOperationCreated;
	}

	/**
	 * Return true if the gen passed as parameter was already affected by a previous
	 * operator.
	 * 
	 * @param genProgInstance
	 * @param map
	 * @param generation
	 * @return
	 */
	private boolean alreadyModified(ModificationPoint genProgInstance, Map<Integer, List<OperatorInstance>> map,
			int generation) {

		for (int i = 1; i < generation; i++) {
			List<OperatorInstance> ops = map.get(i);
			if (ops == null)
				continue;
			for (OperatorInstance genOperationInstance : ops) {
				if (genOperationInstance.getModificationPoint() == genProgInstance) {
					return true;
				}
			}
		}
		return false;
	}

	public void prepareNextGeneration(List<ProgramVariant> temporalInstances, int generation) {
		// After analyze all variant
		// New population creation:
		// show all and search solutions:

		// We filter the solution from the rest
		String solutionId = "";
		for (ProgramVariant programVariant : temporalInstances) {
			if (programVariant.isSolution()) {
				this.solutions.add(programVariant);
				solutionId += programVariant.getId() + "(SOLUTION)(f=" + programVariant.getFitness() + ")" + ", ";
			}
		}
		log.debug("End analysis generation - Solutions found:" + "--> (" + solutionId + ")");

		variants = populationControler.selectProgramVariantsForNextGeneration(variants, temporalInstances,
				ConfigurationProperties.getPropertyInt("population"), variantFactory, originalVariant, generation);

	}

	/**
	 * Apply a mutation generated in previous generation to a model
	 * 
	 * @param variant
	 * @param currentGeneration
	 * @throws IllegalAccessException
	 */
	public void applyPreviousOperationsToVariantModel(ProgramVariant variant, int currentGeneration)
			throws IllegalAccessException {

		// We do not include the current generation (should be empty)
		for (int generation_i = firstgenerationIndex; generation_i < currentGeneration; generation_i++) {

			List<OperatorInstance> operations = variant.getOperations().get(generation_i);
			if (operations == null || operations.isEmpty()) {
				continue;
			}
			for (OperatorInstance genOperation : operations) {
				applyPreviousMutationOperationToSpoonElement(genOperation);
				log.debug("----gener( " + generation_i + ") `" + genOperation.isSuccessfulyApplied() + "`, "
						+ genOperation.toString());

			}

		}
	}

	/**
	 * Apply the mutation generated in the current Generation
	 * 
	 * @param variant
	 * @param currentGeneration
	 * @throws IllegalAccessException
	 */
	public boolean applyNewOperationsToVariantModel(ProgramVariant variant, int currentGeneration)
			throws IllegalAccessException {

		List<OperatorInstance> operations = variant.getOperations().get(currentGeneration);
		if (operations == null || operations.isEmpty()) {
			return false;
		}

		for (OperatorInstance genOperation : operations) {

			applyNewMutationOperationToSpoonElement(genOperation);

		}

		// For the last generation,remove operation with exceptions
		// Clean Operations not applied:
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			OperatorInstance genOperationInstance = operations.get(i);
			if (genOperationInstance.getExceptionAtApplied() != null || !genOperationInstance.isSuccessfulyApplied()) {
				log.debug("---Error! Deleting " + genOperationInstance + " failed by a "
						+ genOperationInstance.getExceptionAtApplied());
				operations.remove(i);
				i--;
				size--;
			}
		}
		return !(operations.isEmpty());
	}
}
