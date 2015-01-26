package fr.inria.astor.core.loop.evolutionary;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.FactoryImpl;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidation;
import fr.inria.astor.core.loop.evolutionary.population.PopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidator;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.FixLocationSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.RepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.WeightCtElement;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.classloader.BytecodeClassLoader;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.StatPatch;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.core.validation.executors.ValidatorProcess;
import fr.inria.astor.core.validation.executors.ValidationDualModeThread;

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
	protected Stats currentStat;

	public Stats getCurrentStat() {
		return currentStat;
	}

	public void setCurrentStat(Stats currentStat) {
		this.currentStat = currentStat;
	}

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	// Factories and controls

	public Logger getLog() {
		return log;
	}

	public void setLog(Logger log) {
		this.log = log;
	}

	protected ProgramVariantFactory variantFactory;

	protected ProgramValidator programVariantValidator = new ProgramValidator();

	// INTERNAL
	protected List<ProgramVariant> variants = new ArrayList<ProgramVariant>();
	protected List<ProgramVariant> solutions = new ArrayList<ProgramVariant>();
	protected ProgramVariant originalVariant = null;

	// SPACES
	protected FixLocationSpace<String, CtCodeElement> fixspace = null;

	protected RepairOperatorSpace repairSpace = null;

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

	public void startEvolution() throws Exception {
		// Process of Instances
		int generation = 0;
		boolean foundsolution = false;

		log.info("FIXSPACE:" + this.getFixspace());
	
		currentStat.passFailingval1 = 0;
		currentStat.passFailingval2 = 0;

		while ((!foundsolution || !TransformationProperties.stopAtFirstSolutionFound)
				&& generation < TransformationProperties.maxGeneration) {
			generation++;
			log.info("----------Running generation " + generation + ", population size: " + this.variants.size());
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

		log.info("***** Generation " + generation);
		boolean foundSolution = false;
		List<ProgramVariant> temporalInstances = new ArrayList<ProgramVariant>();

		currentStat.numberGenerations++;

		for (ProgramVariant parentVariant : variants) {

			if (TransformationProperties.stopAtFirstSolutionFound && foundSolution) {
				break;
			}

			log.info("");
			log.info("-Parent Variant: " + parentVariant);

			// workarround, the class is loader when in the subtype
			URL[] originalURL = projectFacade.getURLforMutation(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
			URLClassLoader loader = new URLClassLoader(originalURL);
			Thread.currentThread().setContextClassLoader(loader);

			ProgramVariant childVariant = createMutatedChild(parentVariant, generation);

			if (childVariant != null) {

				CompilationResult compilation = mutatorSupporter.compileOnMemoryProgramVariant(childVariant,
						originalURL);
			
				boolean childCompiles = compilation.compiles();

				//mutatorSupporter.setMutationId(childVariant.getId());

				String srcOutput = projectFacade.getInDirWithPrefix(childVariant.currentMutatorIdentifier()/*mutatorSupporter.currentMutatorIdentifier()*/);

				if (TransformationProperties.saveProgramVariant) {
					log.info("-Saving child on disk variant #" + childVariant.getId() + " at " + srcOutput);
					// This method should be refactored, and replace by the
					// output from memory compilation
					mutatorSupporter.saveSourceCodeOnDiskProgramVariant(childVariant, srcOutput);
				}

				if (childCompiles) {

					log.info("-The child id "+childVariant.getId()+" compiles: ");
					currentStat.numberOfRightCompilation++;
					childVariant.setCompilation(compilation);
					
					BytecodeClassLoader bcclassloader = new BytecodeClassLoader(originalURL);
					bcclassloader.setBytecodes(compilation.getByteCodes());
					Thread.currentThread().setContextClassLoader(bcclassloader);

					boolean validInstance = validateInstance(childVariant);
					log.info("-Valid: " + validInstance + ", fitness " + childVariant.getFitness());
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
					log.info("-The child does NOT compile: " + childVariant.getId() + ", errors: " + "");
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
		log.info("-->" + currentStat.passFailingval1 + " - " + currentStat.passFailingval2);
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
		log.info("--------Child id: " + childVariant.getId());
		// Apply previous mutation
		applyPreviousMutationsToSpoonModel(childVariant, generation);

		boolean isChildMutatedInThisGeneration = createGenMutationForModel(childVariant, generation);

		if (!isChildMutatedInThisGeneration) {
			log.info("--Not Operation generated in child variant: " + childVariant);
			reverseMutationInModel(childVariant, generation);
			return null;
		}

		boolean appliedOperations = applyNewMutationsToSpoonModel(childVariant, generation);

		if (!appliedOperations) {
			log.info("---Not Operation applied in child variant:" + childVariant);
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
		log.info("--Undoing op of child " + instance.getId() + " " + instance.getOperations().values());
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
		log.info("--Undoing #operations: " + operations.size() + " of generation " + genI);

		for (int i = operations.size() - 1; i >= 0; i--) {
			GenOperationInstance genOperation = operations.get(i);
			log.info("---Undoing: " + genOperation);
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
		log.info("--Creating new mutations operations for variant " + variant);
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
					log.info("---gen "+ (nroGen++)+ " created in "+ (genProgInstance.getRootElement().getSignature()));
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
		log.info("--Summary Creation: for variant " + variant + " gen mutated: " + mut + " , gen not mut: " + notmut
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
		log.info("--Apply previous mutations to the model of variant " + variant);
		// We do not include the current generation (should be empty)
		for (int generation_i = TransformationProperties.firstgenerationIndex; generation_i < currentGeneration; generation_i++) {

			List<GenOperationInstance> operations = variant.getOperations().get(generation_i);
			if (operations == null || operations.isEmpty()) {
				continue;
			}
			for (GenOperationInstance genOperation : operations) {
				// TODO: if the gen was generated after, do nothing
				applyPreviousMutationOperationToSpoonElement(genOperation);
				log.info("----apply op of " + generation_i + " generation, applied `"
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

		log.info("---Apply New mutations to the model of variant " + variant);

		List<GenOperationInstance> operations = variant.getOperations().get(currentGeneration);
		if (operations == null || operations.isEmpty()) {
			return false;
		}

		for (GenOperationInstance genOperation : operations) {

			applyNewMutationOperationToSpoonElement(genOperation);
			log.info("----op of " + currentGeneration + " generation, applied `" + genOperation);

		}

		// For the last generation,remove operation with exceptions
		// Clean Operations not applied:
		int size = operations.size();
		for (int i = 0; i < size; i++) {
			GenOperationInstance genOperationInstance = operations.get(i);
			if (genOperationInstance.getExceptionAtApplied() != null || !genOperationInstance.isSuccessfulyApplied()) {
				log.info("---Error! Deleting " + genOperationInstance + " failed by a "
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

	/**
	 * Process-based validation Advantage: stability, memory consumption, CG
	 * activity Disadvantage: time.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	protected boolean validateInstanceProcess(ProgramVariant mutatedVariant) {
		try {
			String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatedVariant.currentMutatorIdentifier());
			File variantOutputFile = new File(bytecodeOutput);
			URL[] bc = null;
			URL[] originalURL = projectFacade.getURLforMutation(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

			if (mutatedVariant.getCompilation() != null) {
				mutatorSupporter.getSpoonClassCompiler().saveByteCode(
						mutatedVariant.getCompilation(), variantOutputFile);

				bc = redefineURL(variantOutputFile, originalURL);
			} else {
				bc = originalURL;
			}
			ValidatorProcess p = new ValidatorProcess(currentStat);
			// First validation: failing test case
			String localPrefix = projectFacade.getProperties().getExperimentName() + File.separator
					+ projectFacade.getProperties().getFixid();

		
			
			TestResult trfailing = p.execute(bc, projectFacade.getProperties().getFailingTestCases(),
					TransformationProperties.validationSingleTimeLimit * 5);
			currentStat.passFailingval1++;
			if (trfailing == null) {
				log.info("The validation 1 have not finished well");
				mutatedVariant.setFitness(Double.MAX_VALUE);
				return false;
			} else {
				// If it is successful, execute regression
				log.info(trfailing);
				if (trfailing.wasSuccessful()) {
					currentStat.passFailingval2++;
					if (TransformationProperties.executeCompleteRegression)
						return executeRegressionTesting(mutatedVariant, bc, p, localPrefix);
					else
						return executeRegressionTestingOneByOne(mutatedVariant, bc, p, localPrefix);

				} else {
					mutatedVariant.setFitness(trfailing.getFailures().size());
					return trfailing.wasSuccessful();// false
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	
	protected URL[] redefineURL(File foutgen, URL[] originalURL) throws MalformedURLException {
		List<URL> urls = new ArrayList<URL>();
		urls.add(foutgen.toURL());
		for (int i = 0; (originalURL != null) && i < originalURL.length; i++) {
			urls.add(originalURL[i]);
		}

		return (URL[]) urls.toArray(originalURL);
	}

	protected boolean executeRegressionTesting(ProgramVariant mutatedVariant, URL[] bc, ValidatorProcess p,
			String localPrefix) {
		log.info("Test Failing is passing, Executing regression");
		TestResult trregression = p.execute(bc, retrieveRegressionTestCases(),
				TransformationProperties.validationRegressionTimeLimit * 2);
		if (trregression == null) {
			mutatedVariant.setFitness(Double.MAX_VALUE);
			return false;
		} else {
			log.info(trregression);
			mutatedVariant.setFitness(trregression.getFailures().size());
			return trregression.wasSuccessful();
		}
	}

	protected boolean executeRegressionTestingOneByOne(ProgramVariant mutatedVariant, URL[] bc,
			ValidatorProcess p, String localPrefix) {
		log.info("Test Failing is passing, Executing regression");
		TestResult trregressionall = new TestResult();
		long t1 = System.currentTimeMillis();
		for (String tc : retrieveRegressionTestCases()) {
			List<String> parcial = new ArrayList<String>();
			parcial.add(tc);
			TestResult trregression = p.execute(bc, parcial, TransformationProperties.validationRegressionTimeLimit * 2);
			if (trregression == null) {
				log.info("The validation 2 have not finished well");
				mutatedVariant.setFitness(Double.MAX_VALUE);
				return false;
			} else {
				mutatedVariant.setFitness(trregression.getFailures().size());
				// return trregression.wasSuccessful();
				trregressionall.getFailures().addAll(trregression.getFailures());
				trregressionall.getSuccessTest().addAll(trregression.getSuccessTest());
			}
		}
		long t2 = System.currentTimeMillis();
		currentStat.time2Validation.add((t2 - t1));
		log.info(trregressionall);
		return trregressionall.wasSuccessful();

	}

	List<String> regressionCases = null;

	/**
	 * Feed the list of test cases according to the definition POM/build.xml
	 */
	public List<String> retrieveRegressionTestCases() {
		if (regressionCases == null) {
			regressionCases = new ArrayList<String>();
			for (CtSimpleType<?> type : FactoryImpl.getLauchingFactory().Type().getAll()) {
				String name = type.getQualifiedName();
				if ((name.endsWith("Test") || name.endsWith("TestBinary") || name.endsWith("TestPermutations"))
						&& (!name.endsWith("AbstractTest")) && !(type instanceof CtInterface)
						&& !(name.equals("junit.framework.TestSuite"))) {
					regressionCases.add(type.getQualifiedName());
				}

			}
		}
		return regressionCases;
	}


	
	
	protected boolean validateInstance(ProgramVariant mutatedVariant) {
		String validation = ConfigurationProperties.getProperty("validation");
		if("process".equals(validation)){
			return validateInstanceProcess(mutatedVariant);
		}
		if("thread".equals(validation)){
			return validateInstanceThread(mutatedVariant);
		}
		if("local".equals(validation))
			return validateInstanceLocal(mutatedVariant);
		
		throw new IllegalArgumentException("Validation not specified");
	}
	
	/**
	 * Thread-based validation. Advantage: execution time Disadvantage: use of
	 * memory, many classes and instances loaded in the heap/permMem of the
	 * application.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	protected boolean validateInstanceThread(ProgramVariant mutatedVariant) {

		try {

			ValidationDualModeThread thread = new ValidationDualModeThread(projectFacade, this.programVariantValidator,
					populationControler, mutatedVariant, true);

			// First validation
			synchronized (thread) {
				try {
					log.info("Waiting for validation...");
					thread.start();
					thread.wait(TransformationProperties.validationSingleTimeLimit);

				} catch (InterruptedException e) {
					log.info("stop validation thread");
					e.printStackTrace();
				}
			}
			boolean interrumped1 = !thread.finish;
			thread.stop();
			/*
			 * if (interrumped1)
			 * currentStat.time1Validation.add(Long.MAX_VALUE);
			 */log.info("Thread 1 live" + thread.isAlive() + ", interrumped " + interrumped1);
			// If the first validation is ok, we call the regression
			if (thread.sucessfull) {
				ValidationDualModeThread threadRegression = new ValidationDualModeThread(projectFacade,
						this.programVariantValidator, populationControler, mutatedVariant, false);
				synchronized (threadRegression) {
					try {
						log.info("First validation ok, start with the second one");
						threadRegression.modeFirst = false;
						threadRegression.start();
						log.info("Thread 2 id " + threadRegression.getId() + " " + threadRegression.getName());
						threadRegression.wait(TransformationProperties.validationRegressionTimeLimit);
					} catch (InterruptedException e) {
						log.info("stop validation thread");
						e.printStackTrace();
					}
					boolean interrumped2 = !threadRegression.finish;
					if (interrumped2)
						currentStat.time2Validation.add(Long.MAX_VALUE);
					threadRegression.stop();
					log.info("Thread 2 " + threadRegression.isAlive() + ", interrumped " + interrumped2);
				}

				return threadRegression.sucessfull;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Validation that does not use neither thread nor process.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	protected boolean validateInstanceLocal(ProgramVariant mutatedVariant) {

		try {
			// Load version (variant) in new thread to execute it
			// Get test cases to execute.
			List<String> failingCases = projectFacade.getProperties().getFailingTestCases();
			String testSuiteClassName = projectFacade.getProperties().getTestSuiteClassName();

			ProgramVariantValidation result = this.programVariantValidator.validateVariantTwoPhases(failingCases,
					testSuiteClassName);

			// putting fitness into program variant
			double fitness = populationControler.getFitnessValue(mutatedVariant, result);
			mutatedVariant.setFitness(fitness);

			// TODO: result has ignore count
			log.info("Fitness of instance #" + mutatedVariant.getId() + ": " + fitness + " (Totals: "
					+ result.getRunCount() + ", failed: " + result.getFailureCount() + ")");

			return result.wasSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public RepairOperatorSpace getRepairSpace() {
		return repairSpace;
	}

	public void setRepairSpace(RepairOperatorSpace repairSpace) {
		this.repairSpace = repairSpace;
	}

	public List<ProgramVariant> getVariants() {
		return variants;
	}

	public FixLocationSpace getFixspace() {
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

	public ProgramValidator getProgramVariantValidator() {
		return programVariantValidator;
	}

	public void setProgramVariantValidator(ProgramValidator programVariantValidator) {
		this.programVariantValidator = programVariantValidator;
	}

	public ProjectRepairFacade getProjectFacade() {
		return projectFacade;
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
}
