package fr.inria.astor.core.loop.evolutionary;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.loop.evolutionary.population.PopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.WeightCtElement;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.FixLocationSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.operators.RepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.classloader.BytecodeClassLoader;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.stats.StatPatch;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.validation.validators.IProgramValidator;

/**
 * Evolutionary program transformation Loop
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class EvolutionaryEngine {
	/**
	 * Statistic for
	 */
	protected Stats currentStat = new Stats();

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());


	protected ProgramVariantFactory variantFactory;

	protected IProgramValidator programValidator;

	// INTERNAL
	protected List<ProgramVariant> variants = new ArrayList<ProgramVariant>();
	protected List<ProgramVariant> solutions = new ArrayList<ProgramVariant>();
	protected ProgramVariant originalVariant = null;

	// SPACES
	protected FixLocationSpace<String, CtCodeElement,String> fixspace = null;

	protected RepairOperatorSpace repairActionSpace = null;

	protected PopulationController populationControler = null;

	// CODE MANAGMENT
	protected MutationSupporter mutatorSupporter = null;

	protected ProjectRepairFacade projectFacade = null;

	/**
	 * 
	 * @param mutatorExecutor
	 * @throws JSAPException
	 */
	public EvolutionaryEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		this.mutatorSupporter = mutatorExecutor;
		this.projectFacade = projFacade;
	}

	/**
	 * 
	 * @throws Exception
	 */
	public void startEvolution() throws Exception {
		
		int generation = 0;
		boolean foundsolution = false;
				
		log.debug("FIXSPACE:" + this.getFixSpace());
	
		currentStat.passFailingval1 = 0;
		currentStat.passFailingval2 = 0;

		while ((!foundsolution || !TransformationProperties.stopAtFirstSolutionFound)
				&& generation < TransformationProperties.maxGeneration) {
			generation++;
			log.debug("----------Running generation " + generation + ", population size: " + this.variants.size());
			foundsolution = processGenerations(generation);
		}
		// At the end

		if (!this.solutions.isEmpty()) {
			log.info("End Repair Loops: Found solution");
			log.info("Solution stored at: "+projectFacade.getProperties().getInDir());
			
		} else {
			log.info("End Repair Loops: NOT Found solution");
		}
		for (ProgramVariant variant : solutions) {
			log.info("f (sol) " + variant.getFitness() + ", " + variant);
		}

		for (ProgramVariant variant : variants) {
			log.info("f " + variant.getFitness() + ", " + variant);
		}
	}

	/**
	 * Process a generation i: loops over all instances
	 * 
	 * @param generation
	 * @return
	 * @throws Exception
	 */
	private boolean processGenerations(int generation) throws Exception {

		log.debug("***** Generation " + generation);
		boolean foundSolution = false;
		List<ProgramVariant> temporalInstances = new ArrayList<ProgramVariant>();

		currentStat.numberGenerations++;

		for (ProgramVariant parentVariant : variants) {

			if (TransformationProperties.stopAtFirstSolutionFound && foundSolution) {
				break;
			}

			log.debug("-Parent Variant: " + parentVariant);

			// workarround, the class is loader when in the subtype
			URL[] originalURL = projectFacade.getURLforMutation(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
			URLClassLoader loader = new URLClassLoader(originalURL);
			Thread.currentThread().setContextClassLoader(loader);

			ProgramVariant childVariant = createMutatedChild(parentVariant, generation);

			if (childVariant != null) {

				CompilationResult compilation = mutatorSupporter.compileOnMemoryProgramVariant(childVariant,
						originalURL);
			
				boolean childCompiles = compilation.compiles();
				
				String srcOutput = projectFacade.getInDirWithPrefix(childVariant.currentMutatorIdentifier());

				if (TransformationProperties.saveProgramVariant) {
					log.debug("-Saving child on disk variant #" + childVariant.getId() + " at " + srcOutput);
					// This method should be refactored, and replace by the
					// output from memory compilation
					mutatorSupporter.saveSourceCodeOnDiskProgramVariant(childVariant, srcOutput);
				}

				if (childCompiles) {

					log.debug("-The child id "+childVariant.getId()+" compiles: ");
					currentStat.numberOfRightCompilation++;
					childVariant.setCompilation(compilation);
					
					BytecodeClassLoader bcclassloader = new BytecodeClassLoader(originalURL);
					bcclassloader.setBytecodes(compilation.getByteCodes());
					Thread.currentThread().setContextClassLoader(bcclassloader);

					boolean validInstance = validateInstance(childVariant);
					log.debug("-Valid?: " + validInstance + ", fitness " + childVariant.getFitness());
					if (validInstance) {
						log.info("-Found Solution, child variant #" + childVariant.getId());
						saveStaticSucessful(generation);
						// if the mode is not save, we only save the solution
						if (!TransformationProperties.saveProgramVariant) {
							mutatorSupporter.saveSourceCodeOnDiskProgramVariant(childVariant, srcOutput);

						}
						foundSolution = true;
						temporalInstances.add(childVariant);
						mutatorSupporter.saveSolution(childVariant, srcOutput, generation);

					} else {
						// If compiles and it is not solution...
						temporalInstances.add(childVariant);
					}
				} else {
					log.debug("-The child does NOT compile: " + childVariant.getId() + ", errors: " + "");
					currentStat.numberOfFailingCompilation++;
				}
				// Finally, reverse the changes done by the child
				reverseMutationInModel(childVariant, generation);

			}

		}
		// New population creation:
		variants = populationControler.selectProgramVariantsForNextGeneration(variants, temporalInstances,
				this.solutions, TransformationProperties.populationSize);

		if (TransformationProperties.reintroduceOriginalProgram) {
			// Create a new variant from the original parent
			ProgramVariant parentNew = this.variantFactory.createProgramVariantFromAnother(originalVariant, generation);
			parentNew.getOperations().clear();
			parentNew.setParent(null);
			if(variants.size() != 0){
				// now replace for the "worse" child
				variants.remove(variants.size() - 1);
			}
			variants.add(parentNew);
		}

		return foundSolution;
	}

	

	protected void saveStaticSucessful(int generation) {
		currentStat.patches++;
		currentStat.genPatches.add(new StatPatch(generation, currentStat.passFailingval1, currentStat.passFailingval2));
		log.debug("-->" + currentStat.passFailingval1 + " - " + currentStat.passFailingval2);
		currentStat.passFailingval1 = 0;
		currentStat.passFailingval2 = 0;
	}

	/**
	 * Create a child mutated. Return null if not mutation is produced by the
	 * engine (i.e. the child is equal to the parent)
	 * 
	 * @param parentVariant
	 * @param generation
	 * @param idsChild
	 * @return
	 * @throws Exception
	 */
	public ProgramVariant createMutatedChild(ProgramVariant parentVariant, int generation) throws Exception {
		// This is the copy of the original program
		ProgramVariant childVariant = variantFactory.createProgramVariantFromAnother(parentVariant, generation);
		log.debug("--------Child id: " + childVariant.getId());
		// Apply previous mutation
		applyPreviousMutationsToSpoonModel(childVariant, generation);

		boolean isChildMutatedInThisGeneration = createGenMutationForModel(childVariant, generation);

		if (!isChildMutatedInThisGeneration) {
			log.debug("--Not Operation generated in child variant: " + childVariant);
			reverseMutationInModel(childVariant, generation);
			return null;
		}

		boolean appliedOperations = applyNewMutationsToSpoonModel(childVariant, generation);

		if (!appliedOperations) {
			log.debug("---Not Operation applied in child variant:" + childVariant);
			reverseMutationInModel(childVariant, generation);
			return null;
		}

		return childVariant;
	}

	/**
	 * Undo in reverse order that the mutation were applied.
	 * 
	 * @param variant
	 * @param generation
	 */
	public void reverseMutationInModel(ProgramVariant variant, int generation) {

		if (variant.getOperations() == null || variant.getOperations().isEmpty()) {
			return;
		}
		// For each generation, in reverse order they are generated.

		for (int genI = generation; genI >= 1; genI--) {

			undoSingleGeneration(variant, genI);
		}
	}

	public void reverseMutationInModel(ProgramVariant instance) {

		if (instance.getOperations() == null || instance.getOperations().isEmpty()) {
			return;
		}
		log.debug("--Undoing op of child " + instance.getId() + " " + instance.getOperations().values());
		for (Integer generation : instance.getOperations().keySet()) {
			// For each generation, in reverse order they are generated.
			undoSingleGeneration(instance, generation);
		}
	}

	public void undoSingleGeneration(ProgramVariant instance, int genI) {
		List<GenOperationInstance> operations = instance.getOperations().get(genI);
		if (operations == null || operations.isEmpty()) {
			return;
		}
		log.debug("--Undoing #operations: " + operations.size() + " of generation " + genI);

		for (int i = operations.size() - 1; i >= 0; i--) {
			GenOperationInstance genOperation = operations.get(i);
			log.debug("---Undoing: " + genOperation);
			undoOperationToSpoonElement(genOperation);
		}
	}

	/**
	 * For each gen of the variant, create a mutation. These are stored in the
	 * program variant.
	 * 
	 * @param variant
	 * @param generation
	 * @return if at least one gen mutation is created
	 * @throws Exception
	 */
	Random random = new Random();

	private boolean createGenMutationForModel(ProgramVariant variant, int generation) throws Exception {
		log.debug("--Creating new mutations operations for variant " + variant);
		boolean created = false;
		int mut = 0, notmut = 0, notapplied = 0;
		int nroGen = 0;
		List<Gen> gensToProcess = getGenList(variant.getGenList());
		for (Gen genProgInstance : gensToProcess) {

			genProgInstance.setProgramVariant(variant);
			GenOperationInstance operationInGen = createGenMutationForElement(genProgInstance);
			if (operationInGen != null) {
				// Verifies if there are compatible variables (not names!)
				//TODO:
				//if (VariableResolver.canBeApplied(operationInGen)) 
				if(true){
					currentStat.numberOfAppliedOp++;
					log.debug("---gen "+ (nroGen++)+ " created in "+ (genProgInstance.getRootElement().getSignature()));
					variant.putGenOperation(generation, operationInGen);
					operationInGen.setGen(genProgInstance);
					created = true;
					mut++;
					if (TransformationProperties.mutateOnlyOneGenPerGeneration) {
						break;
					}
				} else {// Not applied
					currentStat.numberOfNotAppliedOp++;
					log.debug("---gen "
							+ (nroGen++)
							+ " not scope for the mutation in  "
							+ (genProgInstance.getRootElement().getSignature())
							+ "/ fix: " + operationInGen.getModified());
					// Not Applied
					notapplied++;
				}
			} else {// Not gen created
				currentStat.numberOfGenInmutated++;
				log.debug("---gen "
						+ (nroGen++)
						+ " not mutation generated in  "
						+ (genProgInstance.getRootElement().getSignature()));
				notmut++;
			}
		}

		if (created) {
			updateVariantGenList(variant, generation);
		}
		log.debug("--Summary Creation: for variant " + variant + " gen mutated: " + mut + " , gen not mut: " + notmut
				+ ", gen not applied  " + notapplied);
		return created;
	}

	/**
	 * 
	 * @param genList
	 * @return
	 */
	protected List<Gen> getGenList(List<Gen> genList) {
		if (TransformationProperties.mutateGenInOrder) {
			return genList;
		} else if (TransformationProperties.mutateGenRandomlySuspicious) {
			return getWeightGenList(genList);
		}
		List<Gen> shuffList = new ArrayList<Gen>(genList);
		Collections.shuffle(shuffList);
		return shuffList;

	}

	public List<Gen> getWeightGenList(List<Gen> genList) {
		List<Gen> remaining = new ArrayList<Gen>(genList);
		List<Gen> solution = new ArrayList<Gen>();

		for (int i = 0; i < genList.size(); i++) {
			List<WeightCtElement> we = new ArrayList<WeightCtElement>();
			double sum = 0;
			for (Gen gen : remaining) {
				double susp = ((GenSuspicious) gen).getSuspicious().getSuspiciousValue();
				sum += susp;
				WeightCtElement w = new WeightCtElement(gen, 0);
				w.weight = susp;
				we.add(w);
			}

			for (WeightCtElement weightCtElement : we) {
				weightCtElement.weight = weightCtElement.weight / sum;
			}

			WeightCtElement.feedAccumulative(we);
			WeightCtElement selected = WeightCtElement.selectElementWeightBalanced(we);

			Gen selectedg = (Gen) selected.element;
			remaining.remove(selectedg);
			solution.add(selectedg);
		}
		return solution;

	}

	private void updateVariantGenList(ProgramVariant variant, int generation) {
		List<GenOperationInstance> operations = variant.getOperations().get(generation);
		for (GenOperationInstance genOperationInstance : operations) {
			updateVariantGenList(variant, genOperationInstance);
		}
	}

	/**
	 * This method updates gens of a variant according to a created GenOperationInstance
	 * @param variant
	 * @param operationofGen
	 */
	protected abstract void updateVariantGenList(ProgramVariant variant, GenOperationInstance operation);

	/**
	 * Create a Gen Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */
	protected abstract GenOperationInstance createGenMutationForElement(Gen genProgInstance)
			throws IllegalAccessException;

	protected abstract void undoOperationToSpoonElement(GenOperationInstance operation);

	/**
	 * Apply a mutation generated in previous generation to a model
	 * 
	 * @param variant
	 * @param currentGeneration
	 * @throws IllegalAccessException
	 */
	protected void applyPreviousMutationsToSpoonModel(ProgramVariant variant, int currentGeneration)
			throws IllegalAccessException {
		// New: for the operation of each generation
		log.debug("--Apply previous mutations to the model of variant " + variant);
		// We do not include the current generation (should be empty)
		for (int generation_i = TransformationProperties.firstgenerationIndex; generation_i < currentGeneration; generation_i++) {

			List<GenOperationInstance> operations = variant.getOperations().get(generation_i);
			if (operations == null || operations.isEmpty()) {
				continue;
			}
			for (GenOperationInstance genOperation : operations) {
				applyPreviousMutationOperationToSpoonElement(genOperation);
				log.debug("----apply op of " + generation_i + " generation, applied `"
						+ genOperation.isSuccessfulyApplied() + "`, " + genOperation.toString());

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
	protected boolean applyNewMutationsToSpoonModel(ProgramVariant variant, int currentGeneration)
			throws IllegalAccessException {
		// New: for the operation of each generation

		log.debug("---Apply New mutations to the model of variant " + variant);

		List<GenOperationInstance> operations = variant.getOperations().get(currentGeneration);
		if (operations == null || operations.isEmpty()) {
			return false;
		}

		for (GenOperationInstance genOperation : operations) {

			applyNewMutationOperationToSpoonElement(genOperation);
			log.debug("----op of " + currentGeneration + " generation, applied `" + genOperation);

		}

		// For the last generation,remove operation with exceptions
		// Clean Operations not applied:
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			GenOperationInstance genOperationInstance = operations.get(i);
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

	protected abstract void applyPreviousMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException;

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	protected abstract void applyNewMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException;

	
	
	protected boolean validateInstance(ProgramVariant mutatedVariant) {
		ProgramVariantValidationResult result;
		if( (result = programValidator.validate(mutatedVariant, projectFacade)) != null){
			//
			double fitness = this.populationControler.getFitnessValue(mutatedVariant, result);
			mutatedVariant.setFitness(fitness);
			return result.isResult();
		}
		return false;
	}
		
	public RepairOperatorSpace getRepairActionSpace() {
		return repairActionSpace;
	}

	public void setRepairActionSpace(RepairOperatorSpace repairSpace) {
		this.repairActionSpace = repairSpace;
	}

	public List<ProgramVariant> getVariants() {
		return variants;
	}

	public FixLocationSpace getFixSpace() {
		return fixspace;
	}

	public void setFixspace(FixLocationSpace fixspace) {
		this.fixspace = fixspace;
	}

	public ProgramVariantFactory getVariantFactory() {
		return variantFactory;
	}

	public MutationSupporter getMutatorSupporter() {
		return mutatorSupporter;
	}

	public void setMutatorSupporter(MutationSupporter mutatorSupporter) {
		this.mutatorSupporter = mutatorSupporter;
	}

	public PopulationController getPopulationControler() {
		return populationControler;
	}

	public void setPopulationControler(PopulationController populationControler) {
		this.populationControler = populationControler;
	}


	public void setProjectFacade(ProjectRepairFacade projectFacade) {
		this.projectFacade = projectFacade;
	}

	public Class getClassToManage() {
		return CtElement.class;
	}

	public void setVariantFactory(ProgramVariantFactory variantFactory) {
		this.variantFactory = variantFactory;
	}
	
	
	public IProgramValidator getProgramValidator() {
		return programValidator;
	}

	public void setProgramValidator(IProgramValidator programValidator) {
		this.programValidator = programValidator;
	}

	public Stats getCurrentStat() {
		return currentStat;
	}

	public void setCurrentStat(Stats currentStat) {
		this.currentStat = currentStat;
	}
}
