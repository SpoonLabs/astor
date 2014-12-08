package fr.inria.astor.core.loop.mutation.muttest;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtInterface;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.FactoryImpl;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidation;
import fr.inria.astor.core.loop.evolutionary.MutationalRepair;
import fr.inria.astor.core.loop.evolutionary.population.PopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidator;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidatorProcess;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.RepairOperatorSpace;
import fr.inria.astor.core.loop.mutation.mutants.core.MutantCtElement;
import fr.inria.astor.core.manipulation.compiler.bytecode.ProgramVariantCompilationResult;
import fr.inria.astor.core.setup.MutationProperties;
import fr.inria.astor.core.setup.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.StatPatch;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.validation.TestResult;
import fr.inria.astor.core.validation.ValidationDualModeThread;

/**
 * Evolutionary program transformation Loop
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class MutTestingMutantGenerator {
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

	protected ProgramVariantFactory variantFactory = new ProgramVariantFactory();

	protected ProgramValidator programVariantValidator = new ProgramValidator();

	// INTERNAL
	protected List<ProgramVariant> variants = new ArrayList<ProgramVariant>();
	protected List<ProgramVariant> killedVariants = new ArrayList<ProgramVariant>();
	protected List<ProgramVariant> aliveVariants = new ArrayList<ProgramVariant>();

	protected ProgramVariant originalVariant = null;

	protected RepairOperatorSpace repairSpace = null;

	protected PopulationController populationControler = null;

	// CODE MANAGMENT
	protected MutationSupporter mutatorSupporter = null;
	// new property
	protected ProjectRepairFacade projectFacade = null;

	public int max_mutants = 1000;
	
	/**
	 * 
	 * @param mutatorExecutor
	 * @throws JSAPException
	 */
	public MutTestingMutantGenerator(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		this.mutatorSupporter = mutatorExecutor;
		this.projectFacade = projFacade;
	}

	public void startEvolution() throws Exception {
		// Process of Instances
		int generation = 0;

		currentStat.passFailingval1 = 0;
		currentStat.passFailingval2 = 0;

		generation++;
		log.info("");
		log.info("----------Running generation " + generation + ", population size: " + this.variants.size());
		processGenerations(generation);

		for (ProgramVariant variant : killedVariants) {
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

		int currentMutant = 0 ;
		
		for (ProgramVariant parentVariant : variants) {

			if(currentMutant == max_mutants)
				break;
					
			
			log.info("");
			log.info("-Parent Variant: " + parentVariant);

			// workarround, the class is loader when in the subtype
			URL[] originalURL = projectFacade.getURLforMutation(MutationSupporter.DEFAULT_ORIGINAL_VARIANT);
			URLClassLoader loader = new URLClassLoader(originalURL);
			Thread.currentThread().setContextClassLoader(loader);

			List<ProgramVariant> mutatedVariants = createAllMutationVariants(parentVariant, generation);

			for (ProgramVariant childVariant : mutatedVariants) {

				// First Update SpoonModel
				updateModelForMutatedChildVariant(childVariant, generation);

				ProgramVariantCompilationResult compilation = mutatorSupporter.compileOnMemoryProgramVariant(
						childVariant, originalURL);
				// New setter
				childVariant.setCompilationResult(compilation);

				boolean childCompiles = compilation.compiles();

				mutatorSupporter.setMutationId(childVariant.getId());

				String srcOutput = projectFacade.getInDirWithPrefix(mutatorSupporter.currentMutatorIdentifier());

				if (childCompiles) {
										
					log.info("-The child compiles: " + childVariant.getId());
					currentStat.numberOfRightCompilation++;

					if (MutationProperties.saveProgramVariant) {
						log.info("-Saving child on disk variant #" + childVariant.getId() + " at " + srcOutput);
						// This method should be refactored, and replace by the
						// output from memory compilation
						mutatorSupporter.saveSourceCodeOnDiskProgramVariant(childVariant, srcOutput);
						this.saveByteCode(childVariant);
					}
					
					currentMutant++;
				
				} else {
					log.info("-The child does NOT compile: " + childVariant.getId() + ", errors: "
							+ compilation.getErrors().size());
					currentStat.numberOfFailingCompilation++;
				}
				// Finally, reverse the changes done by the child
				reverseMutationInModel(childVariant, generation);

			}

		}
		// New population creation:
		variants = populationControler.selectProgramVariantsForNextGeneration(variants, temporalInstances,
				this.killedVariants, MutationProperties.populationSize);

		if (MutationProperties.reintroduceOriginalProgram) {
			// Create a new variant from the original parent
			ProgramVariant parentNew = this.variantFactory.createProgramVariantFromAnother(originalVariant, generation);
			parentNew.getOperations().clear();
			parentNew.setParent(null);
			// now replace for the "worse" child
			if(variants.size() > 0)
				variants.remove(variants.size() - 1);
			variants.add(parentNew);
		}

		return foundSolution;
	}

	private List<ProgramVariant> createAllMutationVariants(ProgramVariant parentVariant, int generation)
			throws Exception {

		List<ProgramVariant> newVariants = new ArrayList<>();

		// One for each gen... and many mutants for each
		List<GenOperationInstance> childMutatedInThisGeneration = createGenMutationForModel(parentVariant, generation);

		for (GenOperationInstance genOperationInstance : childMutatedInThisGeneration) {

			// This is the copy of the original program
			ProgramVariant childVariant = variantFactory.createProgramVariantFromAnother(parentVariant, generation);
			log.info("--------Child id: " + childVariant.getId());
			newVariants.add(childVariant);
			//
			boolean linked = linkOperationWithGenCloned(childVariant, genOperationInstance, generation);
			

		}
		return newVariants;
	}

	protected boolean linkOperationWithGenCloned(ProgramVariant childVariant,
			GenOperationInstance genOperationInstance, int generation) {

		for (Gen gen : childVariant.getGenList()) {
			if (gen.getRootElement().getSignature().equals(
			// genOperationInstance.getOriginal().getSignature()
					genOperationInstance.getGen().getRootElement().getSignature())) {
				genOperationInstance.setGen(gen);
				genOperationInstance.setOriginal(getExpressionFromElement(gen.getRootElement()));
				// childVariant.getOperations(generation).add(genOperationInstance);
				childVariant.putGenOperation(generation, genOperationInstance);
				return true;
			}
		}
		return false;
		/*
		 * childVariant.putGenOperation(generation, genOperationInstance);
		 * genOperationInstance.setGen(childVariant);
		 */
	}

	protected void saveStaticSucessful(int generation) {
		currentStat.patches++;
		currentStat.genPatches.add(new StatPatch(generation, currentStat.passFailingval1, currentStat.passFailingval2));
		log.info("-->" + currentStat.passFailingval1 + " - " + currentStat.passFailingval2);
		currentStat.passFailingval1 = 0;
		currentStat.passFailingval2 = 0;
	}

	/**
	 * 
	 * 
	 * @param parentVariant
	 * @param generation
	 * @param idsChild
	 * @return
	 * @throws Exception
	 */
	public void updateModelForMutatedChildVariant(ProgramVariant childVariant, int generation) throws Exception {

		// Apply previous mutation
		applyPreviousMutationsToSpoonModel(childVariant, generation);

		/*
		 * log.info("--Not Operation generated in child variant: " +
		 * childVariant); reverseMutationInModel(childVariant, generation);
		 * return null;
		 */

		boolean appliedOperations = applyNewMutationsToSpoonModel(childVariant, generation);

		if (!appliedOperations) {
			log.info("---Not Operation applied in child variant:" + childVariant);
			reverseMutationInModel(childVariant, generation);
		}

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

	public MutationalRepair mutEngine;

	private List<GenOperationInstance> createGenMutationForModel(ProgramVariant variant, int generation)
			throws Exception {
		log.info("--Creating new mutations operations for variant " + variant);
		// boolean created = false;
		int mut = 0, notmut = 0, notapplied = 0;
		int nroGen = 0;
		List<Gen> gensToProcess = variant.getGenList();
		List<GenOperationInstance> operations = new ArrayList<>();

		for (Gen genProgInstance : gensToProcess) {

			List<MutantCtElement> mutations = mutEngine.getMutants(genProgInstance.getRootElement());

			// Discart the previous one
			for (MutantCtElement mutantCtElement : mutations) {

				GenOperationInstance operationInGen = createGenOperationInstance(genProgInstance,
						mutantCtElement.getElement());
				if (operationInGen != null) {
					// Verifies if there are compatible variables (not names!)
				//	if (VariableResolver.canBeApplied(operationInGen)) {
						currentStat.numberOfAppliedOp++;
						log.info("---gen "
								+ (nroGen++)
								+ " created in "
								+ ((genProgInstance.getRootElement() instanceof CtIf) ? ((CtIf) genProgInstance
										.getRootElement()).getCondition() : genProgInstance.getRootElement()
										.getSignature()));
						mut++;
						operations.add(operationInGen);

					/*} else {// Not applied
						currentStat.numberOfNotAppliedOp++;
						log.info("---gen "
								+ (nroGen++)
								+ " not scope for the mutation in  "
								+ ((genProgInstance.getRootElement() instanceof CtIf) ? ((CtIf) genProgInstance
										.getRootElement()).getCondition() : genProgInstance.getRootElement()
										.getSignature()) + "/ fix: " + operationInGen.getModified());
						// Not Applied
						notapplied++;
					}*/
				} else {// Not gen created
					currentStat.numberOfGenInmutated++;
					log.info("---gen "
							+ (nroGen++)
							+ " not mutation generated in  "
							+ ((genProgInstance.getRootElement() instanceof CtIf) ? ((CtIf) genProgInstance
									.getRootElement()).getCondition() : genProgInstance.getRootElement().getSignature()));
					notmut++;
				}
			}
		}

		return operations;
	}

	/**
	 * 
	 * @param genList
	 * @return
	 */
	protected List<Gen> getGenList(List<Gen> genList) {
		return genList;

	}

	/*
	 * private void updateVariantGenList(ProgramVariant variant, int generation)
	 * { List<GenOperationInstance> operations =
	 * variant.getOperations().get(generation); for (GenOperationInstance
	 * genOperationInstance : operations) { updateVariantGenList(variant,
	 * genOperationInstance); } }
	 *//**
	 * 
	 * @param variant
	 * @param operationofGen
	 */
	/*
	 * protected abstract void updateVariantGenList(ProgramVariant variant,
	 * GenOperationInstance operation);
	 */
	/**
	 * Create a Gen Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */

	protected GenOperationInstance createGenOperationInstance(Gen gen, CtElement mutant) throws IllegalAccessException {

		GenOperationInstance operation = new GenOperationInstance();
		operation.setOriginal(getExpressionFromElement(gen.getRootElement()));
		// operation.setOperationApplied();
		operation.setGen(gen);

		operation.setModified(mutant);

		return operation;
	}

	public CtExpression getExpressionFromElement(CtElement element) {
		if (element instanceof CtExpression)
			return (CtExpression) element;

		if (element instanceof CtIf) {
			return ((CtIf) element).getCondition();
		}
		return null;
	}

	public void undoOperationToSpoonElement(GenOperationInstance operation) {
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		try {
			fix.replace(ctst);
		} catch (Throwable tr) {
			operation.setExceptionAtApplied((Exception) tr);
		}
	}

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
		for (int generation_i = MutationProperties.firstgenerationIndex; generation_i < currentGeneration; generation_i++) {

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

	protected void applyPreviousMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException {
		this.applyNewMutationOperationToSpoonElement(operation);

	}

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	protected void applyNewMutationOperationToSpoonElement(GenOperationInstance operation)
			throws IllegalAccessException {

		boolean successful = false;
		CtExpression ctst = (CtExpression) operation.getOriginal();
		CtExpression fix = (CtExpression) operation.getModified();
		//
		try {
			ctst.replace((CtExpression) fix);
			successful = true;
			operation.setSuccessfulyApplied((successful));
		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}

	}

	public void saveByteCode(ProgramVariant mutatedVariant){
		String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatorSupporter.currentMutatorIdentifier());
		File foutgen = new File(bytecodeOutput);
		mutatorSupporter.getSpoonClassCompiler().saveByteCode(
				mutatedVariant.getCompilationResult().getCompilationList(), foutgen);

	}
	
	/**
	 * Process-based validation Advantage: stability, memory consumption, CG
	 * activity Disadvantage: time.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	protected boolean validateInstanceProcess(ProgramVariant mutatedVariant) {
		try {
			String bytecodeOutput = projectFacade.getOutDirWithPrefix(mutatorSupporter.currentMutatorIdentifier());
			File foutgen = new File(bytecodeOutput);
			URL[] bc = null;
			URL[] originalURL = projectFacade.getURLforMutation(MutationSupporter.DEFAULT_ORIGINAL_VARIANT);

			if (mutatedVariant.getCompilationResult() != null) {
				mutatorSupporter.getSpoonClassCompiler().saveByteCode(
						mutatedVariant.getCompilationResult().getCompilationList(), foutgen);

				bc = redefineURL(foutgen, originalURL);
			} else {
				bc = originalURL;
			}
			ProgramValidatorProcess p = new ProgramValidatorProcess(currentStat);
			// First validation: failing test case
			String localPrefix = projectFacade.getProperties().getExperimentName() + File.separator
					+ projectFacade.getProperties().getFixid();

			TestResult trfailing = p.execute(bc, projectFacade.getProperties().getFailingTestCases(),
					MutationProperties.validationSingleTimeLimit * 5/* false, localPrefix, currentStat.time1Validation*/);
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
					if (MutationProperties.executeCompleteRegression)
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

		return (URL[]) urls.toArray(originalURL);// new URL[] { foutgen.toURL(),
													// originalURL[0],
													// originalURL[1] };
	}

	protected boolean executeRegressionTesting(ProgramVariant mutatedVariant, URL[] bc, ProgramValidatorProcess p,
			String localPrefix) {
		log.info("Test Failing is passing, Executing regression");
		TestResult trregression = p.execute(bc, retrieveRegressionTestCases(),
				MutationProperties.validationRegressionTimeLimit * 2/*, localPrefix*/);
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
			ProgramValidatorProcess p, String localPrefix) {
		log.info("Test Failing is passing, Executing regression");
		TestResult trregressionall = new TestResult();
		long t1 = System.currentTimeMillis();
		for (String tc : retrieveRegressionTestCases()) {
			List<String> parcial = new ArrayList<String>();
			parcial.add(tc);
			TestResult trregression = p.execute(bc, parcial, MutationProperties.validationRegressionTimeLimit * 2
					/*true, localPrefix, null*/);
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

	/**
	 * Thread-based validation. Advantage: execution time Disadvantage: use of
	 * memory, many classes and instances loaded in the heap/permMem of the
	 * application.
	 * 
	 * @param mutatedVariant
	 * @return
	 */
	protected boolean validateInstance(ProgramVariant mutatedVariant) {

		try {
			ValidationDualModeThread thread = new ValidationDualModeThread(projectFacade, this.programVariantValidator,
					populationControler, mutatedVariant, true);

			// First validation
			synchronized (thread) {
				try {
					log.info("Waiting for validation...");
					thread.start();
					log.info("Thread 1 id " + thread.getId() + " " + thread.getName());
					thread.wait(MutationProperties.validationSingleTimeLimit);

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
						threadRegression.wait(MutationProperties.validationRegressionTimeLimit);
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

			/*
			 * URL[] originalURL =
			 * projectFacade.getURLforMutation(MutationSupporter
			 * .DEFAULT_ORIGINAL_VARIANT); SpoonURLClassLoader loader = new
			 * SpoonURLClassLoader(originalURL, mutatedVariant,
			 * mutatorSupporter.getSpoonClassCompiler());
			 * Thread.currentThread().setContextClassLoader(loader);
			 */
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
		return CtIf.class;// CtElement.class;
	}

	/**
	 * 
	 * @param suspicious
	 * @throws Exception
	 */
	public void start(List<CtElement> elements, boolean buildSpoonModel) throws Exception {

		if (buildSpoonModel && !(this.mutatorSupporter.getFactory().Type().getAll().size() > 0)) {
			initModel();

		}
		
		log.info("----Starting Mutation: Initial suspicious size: " + elements.size());
		long startT = System.currentTimeMillis();

		initializePopulation(elements);
		// --
		if (originalVariant.getGenList().isEmpty()) {
			log.info("No gen to analyze");
			return;
		}

		// --
		/*URL[] originalURL = projectFacade.getURLforMutation(MutationSupporter.DEFAULT_ORIGINAL_VARIANT);
		URLClassLoader loader = new URLClassLoader(originalURL);
		Thread.currentThread().setContextClassLoader(loader);

		/*boolean validInstance = validateInstance(originalVariant);
		assert (validInstance);

		for (ProgramVariant initvariant : variants) {
			initvariant.setFitness(originalVariant.getFitness());
		}*/

		startEvolution();

		long endT = System.currentTimeMillis();
		log.info("Time (ms): " + (endT - startT));
		currentStat.timeIteraction = ((endT - startT));

	}

	public void initModel() {
		String codeLocation = projectFacade.getInDirWithPrefix(mutatorSupporter.DEFAULT_ORIGINAL_VARIANT);
		String outLocation = projectFacade.getOutDirWithPrefix(mutatorSupporter.DEFAULT_ORIGINAL_VARIANT/*+"_1"*/);
			
		String classpath = projectFacade.getProperties().getDependenciesString();
		mutatorSupporter.buildModel(codeLocation,classpath);
	
	}
	

	/*
	 * @Deprecated public void initializePopulation(List<SuspiciousCode>
	 * suspicious) throws Exception {
	 * variantFactory.setMutatorExecutor(getMutatorSupporter());
	 * //variantFactory.setFixspace(getFixspace());
	 * variantFactory.setAcceptedCtElement(getClassToManage()); this.variants =
	 * variantFactory.createInitialPopulation(suspicious,
	 * MutationProperties.populationSize, programVariantValidator,
	 * populationControler, projectFacade);
	 * 
	 * // We save the first variant this.originalVariant = variants.get(0);
	 * currentStat.fl_gens_size = this.originalVariant.getGenList().size(); }
	 */

	public void initializePopulation(List<CtElement> elements) throws Exception {
		variantFactory.setMutatorExecutor(getMutatorSupporter());
		// variantFactory.setFixspace(getFixspace());
	//	variantFactory.setAcceptedCtElement(getClassToManage());
		this.variants = variantFactory.createInitialPopulation(elements, programVariantValidator, populationControler,
				projectFacade);

		// We save the first variant
		this.originalVariant = variants.get(0);
		currentStat.fl_gens_size = this.originalVariant.getGenList().size();
	}

}
