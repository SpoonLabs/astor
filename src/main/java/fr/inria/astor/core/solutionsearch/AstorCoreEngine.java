package fr.inria.astor.core.solutionsearch;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.map.HashedMap;
import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.cardumen.CardumenOperatorSpace;
import fr.inria.astor.approaches.extensions.minimpact.validator.ProcessEvoSuiteValidator;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.approaches.jkali.JKaliSpace;
import fr.inria.astor.approaches.jmutrepair.MutRepairSpace;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.PatchDiff;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.VariantValidationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.cocospoon.CocoFaultLocalization;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.sourcecode.BlockReificationScanner;
import fr.inria.astor.core.output.PatchJSONStandarOutput;
import fr.inria.astor.core.output.ReportResults;
import fr.inria.astor.core.output.StandardOutputReport;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import fr.inria.astor.core.solutionsearch.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.solutionsearch.extension.VariantCompiler;
import fr.inria.astor.core.solutionsearch.navigation.InOrderSuspiciousNavigation;
import fr.inria.astor.core.solutionsearch.navigation.SequenceSuspiciousNavigationStrategy;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationStrategy;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.core.solutionsearch.navigation.UniformRandomSuspiciousNavigation;
import fr.inria.astor.core.solutionsearch.navigation.WeightRandomSuspiciousNavitation;
import fr.inria.astor.core.solutionsearch.population.FitnessFunction;
import fr.inria.astor.core.solutionsearch.population.PopulationController;
import fr.inria.astor.core.solutionsearch.population.ProgramVariantFactory;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.astor.core.solutionsearch.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.solutionsearch.spaces.operators.WeightedRandomOperatorSelection;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.core.validation.ProgramVariantValidator;
import fr.inria.astor.core.validation.processbased.ProcessValidator;
import fr.inria.astor.util.PatchDiffCalculator;
import fr.inria.astor.util.StringUtil;
import fr.inria.astor.util.TimeUtil;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

/**
 * Evolutionary program transformation Loop
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class AstorCoreEngine implements AstorExtensionPoint {

	/**
	 * Initial identifier.
	 */
	public static int firstgenerationIndex = 1;

	/**
	 * Statistic
	 */
	protected Stats currentStat = null;

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	protected ProgramVariantFactory variantFactory;

	protected ProgramVariantValidator programValidator;

	// SPACES

	protected SuspiciousNavigationStrategy suspiciousNavigationStrategy = null;

	protected OperatorSelectionStrategy operatorSelectionStrategy = null;

	protected OperatorSpace operatorSpace = null;

	protected PopulationController populationControler = null;

	// CODE MANAGMENT
	protected MutationSupporter mutatorSupporter = null;

	protected ProjectRepairFacade projectFacade = null;

	//
	protected FaultLocalizationStrategy faultLocalization = null;

	protected SolutionVariantSortCriterion patchSortCriterion = null;

	protected FitnessFunction fitnessFunction = null;

	protected VariantCompiler compiler = null;

	protected List<TargetElementProcessor<?>> targetElementProcessors = null;

	// protected PlugInVisitor pluginLoaded = null;

	// Output
	protected List<ReportResults> outputResults = null;

	/// // Flag, output status, results
	protected List<ProgramVariant> variants = new ArrayList<ProgramVariant>();

	protected List<ProgramVariant> solutions = new ArrayList<ProgramVariant>();

	protected ProgramVariant originalVariant = null;

	protected Date dateInitEvolution = new Date();

	protected int generationsExecuted = 0;

	private int nrGenerationWithoutModificatedVariant = 0;

	protected AstorOutputStatus outputStatus = null;

	protected List<PatchStat> patchInfo = new ArrayList<>();

	/**
	 * 
	 * @param mutatorExecutor
	 * @throws JSAPException
	 */
	public AstorCoreEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		this.mutatorSupporter = mutatorExecutor;
		this.projectFacade = projFacade;

		this.currentStat = Stats.createStat();

	}

	public void startEvolution() throws Exception {

		log.info("\n----Starting Solution Search");

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

			if (!(belowMaxTime(dateInitEvolution, maxMinutes))) {// &&
																	// limitDate()

				log.debug("\n Max time reached " + generationsExecuted);
				this.outputStatus = AstorOutputStatus.TIME_OUT;
				break;
			}

			generationsExecuted++;
			log.debug("\n----------Running generation/iteraction " + generationsExecuted + ", population size: "
					+ this.variants.size());
			try {
				boolean solutionFound = processGenerations(generationsExecuted);

				stopSearch = solutionFound &&
				// one solution
						(ConfigurationProperties.getPropertyBool("stopfirst")
								// or nr solutions are greater than max allowed
								|| (this.solutions.size() >= ConfigurationProperties
										.getPropertyInt("maxnumbersolutions")));

				if (stopSearch) {
					log.debug("\n Max Solution found " + this.solutions.size());
					this.outputStatus = AstorOutputStatus.STOP_BY_PATCH_FOUND;
				}

			} catch (Throwable e) {
				log.error("Error at generation " + generationsExecuted + "\n" + e);
				// log.equals(Arrays.toString(e.getStackTrace()));
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

	public void atEnd() {

		long startT = dateInitEvolution.getTime();
		long endT = System.currentTimeMillis();
		log.info("Time Repair Loop (s): " + (endT - startT) / 1000d);
		currentStat.getGeneralStats().put(GeneralStatEnum.TOTAL_TIME, ((endT - startT)) / 1000d);
		log.info("generationsexecuted: " + this.generationsExecuted);

		currentStat.getGeneralStats().put(GeneralStatEnum.OUTPUT_STATUS, this.getOutputStatus());

		try {
			this.computePatchDiff(this.solutions);
		} catch (Exception e) {
			log.error("Problem at computing diff" + e);
		}
		this.sortPatches();
		this.printFinalStatus();

		log.info(this.getSolutionData(this.solutions, this.generationsExecuted) + "\n");

		// Recreate statistiques of patches
		if (!solutions.isEmpty()) {
			patchInfo = this.currentStat.createStatsForPatches(solutions, generationsExecuted, dateInitEvolution);
		}

		String output = this.projectFacade.getProperties().getWorkingDirRoot();

		// Reporting results
		for (ReportResults out : this.getOutputResults()) {
			out.produceOutput(patchInfo, this.currentStat.getGeneralStats(), output);
		}
	}

	protected void computePatchDiff(List<ProgramVariant> solutions) throws Exception {

		PatchDiffCalculator cdiff = new PatchDiffCalculator();
		for (ProgramVariant solutionVariant : solutions) {
			PatchDiff pdiff = new PatchDiff();
			boolean format = true;

			String diffPatchFormated = cdiff.getDiff(getProjectFacade(), this.originalVariant, solutionVariant, format,
					this.mutatorSupporter);

			pdiff.setFormattedDiff(diffPatchFormated);

			format = false;

			String diffPatchOriginalAlign = cdiff.getDiff(getProjectFacade(), this.originalVariant, solutionVariant,
					format, this.mutatorSupporter);

			pdiff.setOriginalStatementAlignmentDiff(diffPatchOriginalAlign);

			solutionVariant.setPatchDiff(pdiff);
		}

	}

	private String getDiff(List<ProgramVariant> solutions, boolean format) {

		return null;
	}

	/**
	 * Sorts patches according to a criterion passed via Extension point.
	 */
	public void sortPatches() {
		if (!getSolutions().isEmpty() && this.getPatchSortCriterion() != null) {
			this.solutions = this.getPatchSortCriterion().priorize(getSolutions());
		}
	}

	public void printFinalStatus() {
		log.info("\n----SUMMARY_EXECUTION---");
		if (!this.solutions.isEmpty()) {
			log.debug("End Repair Loops: Found solution");
			log.debug("Solution stored at: " + projectFacade.getProperties().getWorkingDirForSource());

		} else {
			log.debug("End Repair Loops: NOT Found solution");
		}
		log.debug("\nNumber solutions:" + this.solutions.size());
		for (ProgramVariant variant : solutions) {
			log.debug("f (sol): " + variant.getFitness() + ", " + variant);
		}
		log.debug("\nAll variants:");
		for (ProgramVariant variant : variants) {
			log.debug("f " + variant.getFitness() + ", " + variant);
		}
		log.debug("\nNumber suspicious:" + this.variants.size());

	}

	/**
	 * Check whether the program has passed the maximum time for operating
	 * 
	 * @param dateInit
	 *            start date of execution
	 * @param maxMinutes
	 *            max minutes for operating
	 * @return
	 */
	protected boolean belowMaxTime(Date dateInit, int maxMinutes) {
		if (TimeUtil.deltaInMinutes(dateInit) < maxMinutes) {
			return true;
		} else {
			log.info("\n No more time for operating");
			return false;
		}
	}

	public boolean limitDate() {
		String limit = ConfigurationProperties.properties.getProperty("maxdate");
		if (limit == null) {
			return true;
		}

		try {
			Date d = TimeUtil.tranformHours(limit);

			Date dc = new Date();
			Date tr = TimeUtil.tranformHours(dc.getHours() + ":" + dc.getMinutes());
			if (tr.getHours() >= 12) {
				return true;
			}
			boolean continueProc = tr.before(d);
			if (!continueProc) {
				log.info("Astor reaches the hour limit, we stop here");
			}
			return continueProc;
		} catch (ParseException e) {
			log.error("Parsing time", e);
			return false;
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
			temporalInstances.add(newVariant);

			boolean solution = processCreatedVariant(newVariant, generation);

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
	 * Store in the program variant passed as parameter a clone of each ctclass
	 * involved in the variant.
	 * 
	 * @param variant
	 */
	protected void storeModifiedModel(ProgramVariant variant) {
		variant.getModifiedClasses().clear();
		for (CtClass modifiedClass : variant.getBuiltClasses().values()) {
			CtClass cloneModifClass = (CtClass) MutationSupporter.clone(modifiedClass);
			cloneModifClass.setParent(modifiedClass.getParent());
			variant.getModifiedClasses().add(cloneModifClass);
		}

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

	Map<String, String> originalModel = new HashedMap();
	Map<String, String> modifModel = new HashedMap();

	private void saveOriginalVariant(ProgramVariant variant) {
		originalModel.clear();
		for (CtType st : variant.getAffectedClasses()) {
			try {
				originalModel.put(st.getQualifiedName(), st.toString());
			} catch (Exception e) {
				log.error("Problems saving cttype: " + st.getQualifiedName());
			}
		}

	}

	private void saveModifVariant(ProgramVariant variant) {
		modifModel.clear();
		for (CtType st : variant.getAffectedClasses()) {
			try {
				modifModel.put(st.getQualifiedName(), st.toString());
			} catch (Exception e) {
				log.error("Problems saving cttype: " + st.getQualifiedName());
			}

		}

	}

	private boolean validateReversedOriginalVariant(ProgramVariant variant) {

		for (CtType st : variant.getAffectedClasses()) {
			String original = originalModel.get(st.getQualifiedName());
			if (original != null) {
				boolean idem = original.equals(st.toString());
				if (!idem) {
					log.error("Error variant :" + variant.getId()
							+ "the model was not the same from the original after this generation");
					// log.error("Undo Error: original: \n" + original);
					// log.error("Undo Error: modified: \n" + st.toString());
					try {
						File forig = File.createTempFile("torig", "java");
						FileWriter fr = new FileWriter(forig);
						fr.write(original);
						fr.close();

						File fmod = File.createTempFile("torig", "java");
						FileWriter fm = new FileWriter(fmod);
						fm.write(st.toString());
						fm.close();

						PatchDiffCalculator pdc = new PatchDiffCalculator();
						String diff = pdc.getDiff(forig, fmod, st.getQualifiedName());

						log.error("Undo Error: diff: \n" + diff);
					} catch (Exception e) {
						log.error(e);
					}

					return false;
				}
			}
		}
		return true;
	}

	public void saveVariant(ProgramVariant programVariant) throws Exception {
		final boolean codeFormated = true;
		save(programVariant, !codeFormated);
		save(programVariant, codeFormated);

	}

	private void save(ProgramVariant programVariant, boolean format) throws Exception {

		boolean originalValue = ConfigurationProperties.getPropertyBool("preservelinenumbers");

		final String suffix = format ? PatchDiffCalculator.DIFF_SUFFIX : "";
		String srcOutput = projectFacade.getInDirWithPrefix(programVariant.currentMutatorIdentifier()) + suffix;
		log.debug("\n-Saving child on disk variant #" + programVariant.getId() + " at " + srcOutput);
		ConfigurationProperties.setProperty("preservelinenumbers", Boolean.toString(!format));
		mutatorSupporter.saveSourceCodeOnDiskProgramVariant(programVariant, srcOutput);

		ConfigurationProperties.setProperty("preservelinenumbers", Boolean.toString(originalValue));
	}

	/**
	 * 
	 * Compiles and validates a created variant.
	 * 
	 * @param parentVariant
	 * @param generation
	 * @return true if the variant is a solution. False otherwise.
	 * @throws Exception
	 */
	public boolean processCreatedVariant(ProgramVariant programVariant, int generation) throws Exception {

		URL[] originalURL = projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		CompilationResult compilation = compiler.compile(programVariant, originalURL);

		boolean childCompiles = compilation.compiles();
		programVariant.setCompilation(compilation);

		storeModifiedModel(programVariant);

		if (ConfigurationProperties.getPropertyBool("saveall")) {
			this.saveVariant(programVariant);
		}

		if (childCompiles) {

			log.debug("-The child compiles: id " + programVariant.getId());
			currentStat.increment(GeneralStatEnum.NR_RIGHT_COMPILATIONS);

			VariantValidationResult validationResult = validateInstance(programVariant);
			double fitness = this.fitnessFunction.calculateFitnessValue(validationResult);
			programVariant.setFitness(fitness);

			log.debug("-Valid?: " + validationResult + ", fitness " + programVariant.getFitness());
			if (validationResult != null && validationResult.isSuccessful()) {
				log.info("-Found Solution, child variant #" + programVariant.getId());
				saveStaticSucessful(programVariant.getId(), generation);
				saveVariant(programVariant);

				return true;
			}
		} else {
			log.debug("-The child does NOT compile: " + programVariant.getId() + ", errors: "
					+ compilation.getErrorList());
			currentStat.increment(GeneralStatEnum.NR_FAILLING_COMPILATIONS);
			programVariant.setFitness(this.fitnessFunction.getWorstMaxFitnessValue());
		}
		// In case that the variant a) does not compile; b) compiles but it's
		// not adequate
		Stats.currentStat.getIngredientsStats().storeIngCounterFromFailingPatch(programVariant.getId());
		return false;

	}

	protected void saveStaticSucessful(int variant_id, int generation) {
		Stats.currentStat.getIngredientsStats().storeIngCounterFromSuccessPatch(variant_id);
		Stats.currentStat.getIngredientsStats().storePatchAttempts(variant_id);
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

	/**
	 * Undo in reverse order that the mutation were applied.
	 * 
	 * @param variant
	 * @param generation
	 */
	protected void reverseOperationInModel(ProgramVariant variant, int generation) {

		if (variant.getOperations() == null || variant.getOperations().isEmpty()) {
			return;
		}
		// For each generation, in reverse order they are generated.

		for (int genI = generation; genI >= 1; genI--) {

			undoSingleGeneration(variant, genI);
		}
	}

	protected void undoSingleGeneration(ProgramVariant instance, int genI) {
		List<OperatorInstance> operations = instance.getOperations().get(genI);
		if (operations == null || operations.isEmpty()) {
			return;
		}

		for (int i = operations.size() - 1; i >= 0; i--) {
			OperatorInstance genOperation = operations.get(i);
			log.debug("---Undoing: gnrtn(" + genI + "): " + genOperation);
			undoOperationToSpoonElement(genOperation);
		}
	}

	/**
	 * Given a program variant, the method generates operations for modifying
	 * that variants. Each operation is related to one gen of the program
	 * variant.
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
				.getSortedModificationPointsList(variant);

		// log.debug("modifPointsToProcess " + modificationPointsToProcess);
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

				log.debug("operation " + modificationInstance);
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

	Map<ModificationPoint, List<OperatorInstance>> operationGenerated = new HashedMap();

	private boolean alreadyApplied(OperatorInstance operationNew) {

		List<OperatorInstance> ops = operationGenerated.get(operationNew.getModificationPoint());
		if (ops == null) {
			ops = new ArrayList<>();
			operationGenerated.put(operationNew.getModificationPoint(), ops);
			ops.add(operationNew);
			return false;
		}
		return ops.contains(operationNew);
	}

	/**
	 * Return true if the gen passed as parameter was already affected by a
	 * previous operator.
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

	/**
	 * This method updates gens of a variant according to a created
	 * GenOperationInstance
	 * 
	 * @param variant
	 * @param operationofGen
	 */
	private void updateVariantGenList(ProgramVariant variant, int generation) {
		List<OperatorInstance> operations = variant.getOperations().get(generation);

		for (OperatorInstance genOperationInstance : operations) {
			updateVariantGenList(variant, genOperationInstance);
		}
	}

	public OperatorInstance createOperatorInstanceForPoint(ModificationPoint modificationPoint)
			throws IllegalAccessException {
		SuspiciousModificationPoint suspModificationPoint = (SuspiciousModificationPoint) modificationPoint;

		AstorOperator operationType = operatorSelectionStrategy.getNextOperator(suspModificationPoint);

		if (operationType == null) {
			log.debug("Operation Null");
			return null;
		}

		CtElement targetStmt = suspModificationPoint.getCodeElement();

		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(targetStmt);
		operation.setOperationApplied(operationType);
		operation.setModificationPoint(suspModificationPoint);
		operation.defineParentInformation(suspModificationPoint);

		return operation;
	}

	/**
	 * Apply a mutation generated in previous generation to a model
	 * 
	 * @param variant
	 * @param currentGeneration
	 * @throws IllegalAccessException
	 */
	protected void applyPreviousOperationsToVariantModel(ProgramVariant variant, int currentGeneration)
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

	public VariantValidationResult validateInstance(ProgramVariant variant) {

		VariantValidationResult validationResult = programValidator.validate(variant, projectFacade);
		if (validationResult != null) {
			variant.setIsSolution(validationResult.isSuccessful());
			variant.setValidationResult(validationResult);
		}
		return validationResult;
	}

	public OperatorSelectionStrategy getOperatorSelectionStrategy() {
		return operatorSelectionStrategy;
	}

	public void setOperatorSelectionStrategy(OperatorSelectionStrategy operatorSelectionStrategy) {
		this.operatorSelectionStrategy = operatorSelectionStrategy;
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

	public void setProjectFacade(ProjectRepairFacade projectFacade) {
		this.projectFacade = projectFacade;
	}

	public void setVariantFactory(ProgramVariantFactory variantFactory) {
		this.variantFactory = variantFactory;
	}

	public ProgramVariantValidator getProgramValidator() {
		return programValidator;
	}

	public void setProgramValidator(ProgramVariantValidator programValidator) {
		this.programValidator = programValidator;
		this.programValidator.setStats(currentStat);
	}

	public String getSolutionData(List<ProgramVariant> variants, int generation) {
		String line = "";
		line += "\n --SOLUTIONS DESCRIPTION--\n";
		for (ProgramVariant solutionVariant : variants) {
			line += "\n ----\n";
			line += "ProgramVariant " + solutionVariant.getId() + "\n ";
			line += "\ntime(sec)= "
					+ TimeUtil.getDateDiff(this.dateInitEvolution, solutionVariant.getBornDate(), TimeUnit.SECONDS);

			for (int i = 1; i <= generation; i++) {
				List<OperatorInstance> genOperationInstances = solutionVariant.getOperations().get(i);
				if (genOperationInstances == null)
					continue;

				for (OperatorInstance genOperationInstance : genOperationInstances) {

					line += "\noperation: " + genOperationInstance.getOperationApplied().toString() + "\nlocation= "
							+ genOperationInstance.getModificationPoint().getCtClass().getQualifiedName();

					if (genOperationInstance.getModificationPoint() instanceof SuspiciousModificationPoint) {
						SuspiciousModificationPoint gs = (SuspiciousModificationPoint) genOperationInstance
								.getModificationPoint();
						line += "\nline= " + gs.getSuspicious().getLineNumber();
						line += "\nlineSuspiciousness= " + gs.getSuspicious().getSuspiciousValueString();
					}
					line += "\nlineSuspiciousness= " + genOperationInstance.getModificationPoint().identified;
					line += "\noriginal statement= " + genOperationInstance.getOriginal().toString();
					line += "\nbuggy kind= " + genOperationInstance.getOriginal().getClass().getSimpleName() + "|"
							+ genOperationInstance.getOriginal().getParent().getClass().getSimpleName();

					line += "\nfixed statement= ";
					if (genOperationInstance.getModified() != null) {
						// if fix content is the same that original buggy
						// content, we do not write the patch, remaining empty
						// the property fixed statement
						if (genOperationInstance.getModified().toString() != genOperationInstance.getOriginal()
								.toString())
							line += genOperationInstance.getModified().toString();
						else {
							line += genOperationInstance.getOriginal().toString();
						}
						// Information about types Parents

						line += "\nPatch kind= " + genOperationInstance.getModified().getClass().getSimpleName() + "|"
								+ genOperationInstance.getModified().getParent().getClass().getSimpleName();
					}
					line += "\ngeneration= " + Integer.toString(i);
					line += "\ningredientScope= " + ((genOperationInstance.getIngredientScope() != null)
							? genOperationInstance.getIngredientScope() : "-");

					if (genOperationInstance.getIngredient() != null
							&& genOperationInstance.getIngredient().getDerivedFrom() != null)
						line += "\ningredientParent= " + genOperationInstance.getIngredient().getDerivedFrom();

				}
			}
			line += "\nvalidation=" + solutionVariant.getValidationResult().toString();
			String diffPatch = solutionVariant.getPatchDiff().getFormattedDiff();
			line += "\ndiffpatch=" + diffPatch;
			String diffPatchoriginal = solutionVariant.getPatchDiff().getOriginalStatementAlignmentDiff();
			line += "\ndiffpatchoriginal=" + diffPatchoriginal;
		}
		return line;
	}

	public List<ProgramVariant> getSolutions() {
		return solutions;
	}

	public FaultLocalizationStrategy getFaultLocalization() {
		return faultLocalization;
	}

	public void setFaultLocalization(FaultLocalizationStrategy faultLocalization) {
		this.faultLocalization = faultLocalization;
	}

	public ProjectRepairFacade getProjectFacade() {
		return projectFacade;
	}

	public OperatorSpace getOperatorSpace() {
		return operatorSpace;
	}

	public void setOperatorSpace(OperatorSpace operatorSpace) {
		this.operatorSpace = operatorSpace;
	}

	public SolutionVariantSortCriterion getPatchSortCriterion() {
		return patchSortCriterion;
	}

	public void setPatchSortCriterion(SolutionVariantSortCriterion patchSortCriterion) {
		this.patchSortCriterion = patchSortCriterion;
	}

	public void setFitnessFunction(FitnessFunction fitnessFunction) {
		this.fitnessFunction = fitnessFunction;
	}

	public FitnessFunction getFitnessFunction() {
		return fitnessFunction;
	}

	public VariantCompiler getCompiler() {
		return compiler;
	}

	public void setCompiler(VariantCompiler compiler) {
		this.compiler = compiler;
	}

	/**
	 * By default, it initializes the spoon model. It should not be created
	 * before. Otherwise, an exception occurs.
	 * 
	 * @param suspicious
	 * @throws Exception
	 */
	public void initPopulation(List<SuspiciousCode> suspicious) throws Exception {

		log.info("\n---- Creating spoon model");

		this.initModel();

		log.info("\n---- Initial suspicious size: " + suspicious.size());
		initializePopulation(suspicious);

		if (originalVariant == null) {
			log.error("Any variant for analyze ");
			return;
		}

		if (originalVariant.getModificationPoints().isEmpty()) {
			log.error("Variant with any gen");
			return;
		}

		if (!ConfigurationProperties.getPropertyBool("skipfitnessinitialpopulation")) {
			log.debug("Calculating fitness");
			setFitnessOfPopulation();
		} else {
			log.debug("Fitness for initial population is disable");
		}

	}

	protected void setFitnessOfPopulation() {
		log.debug("Calculating fitness for original program variant.");
		// temporal workaround for avoid changing the interface
		String original = ConfigurationProperties.getProperty("forceExecuteRegression");
		ConfigurationProperties.setProperty("forceExecuteRegression", Boolean.TRUE.toString());

		// Initial validation and fitness
		VariantValidationResult validationResult = validateInstance(originalVariant);

		if (validationResult == null) {
			throw new IllegalStateException("Initial run of test suite fails");
		}
		if (validationResult.isSuccessful()) {
			throw new IllegalStateException("The application under repair has not failling test cases");
		}

		double fitness = this.fitnessFunction.calculateFitnessValue(validationResult);
		originalVariant.setFitness(fitness);

		log.debug("The original fitness is : " + fitness);
		for (ProgramVariant initvariant : variants) {
			initvariant.setFitness(fitness);
		}
		ConfigurationProperties.setProperty("forceExecuteRegression", original);

	}

	public void initModel() throws Exception {

		if (!MutationSupporter.getFactory().Type().getAll().isEmpty()) {
			Factory fcurrent = MutationSupporter.getFactory();
			log.debug("The Spoon Model was already built.");
			Factory fnew = MutationSupporter.cleanFactory();
			log.debug("New factory created? " + !fnew.equals(fcurrent));
		}

		String codeLocation = projectFacade.getInDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		String bytecodeLocation = projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		String classpath = projectFacade.getProperties().getDependenciesString();
		String[] cpArray = classpath.split(File.pathSeparator);

		try {
			mutatorSupporter.buildModel(codeLocation, bytecodeLocation, cpArray);
			log.debug("Spoon Model built from location: " + codeLocation);
		} catch (Exception e) {
			log.error("Problem compiling the model with compliance level "
					+ ConfigurationProperties.getPropertyInt("javacompliancelevel"));
			log.error(e.getMessage());
			throw e;
		}

		///// ONCE ASTOR HAS BUILT THE MODEL,
		///// We apply different processes and manipulation over it.

		// We process the model to add blocks as parent of statement which are
		// not contained in a block
		BlockReificationScanner visitor = new BlockReificationScanner();
		for (CtType c : mutatorSupporter.getFactory().Type().getAll()) {
			c.accept(visitor);
		}

	}

	/**
	 * Creates the variants from the suspicious code
	 * 
	 * @param suspicious
	 * @throws Exception
	 */
	private void initializePopulation(List<SuspiciousCode> suspicious) throws Exception {

		variantFactory.setMutatorExecutor(getMutatorSupporter());

		this.variants = variantFactory.createInitialPopulation(suspicious,
				ConfigurationProperties.getPropertyInt("population"), populationControler, projectFacade);

		if (variants.isEmpty()) {
			throw new IllegalArgumentException("Any variant created from list of suspicious");
		}
		// We save the first variant
		this.originalVariant = variants.get(0);

		if (originalVariant.getModificationPoints().isEmpty()) {
			throw new IllegalStateException("Variant without any modification point. It must have at least one.");
		}
	}

	/**
	 * This method updates modification point of a variant according to a
	 * created GenOperationInstance
	 * 
	 * @param variant
	 *            variant to modify the modification point information
	 * @param operationofGen
	 *            operator to apply in the variant.
	 */
	protected void updateVariantGenList(ProgramVariant variant, OperatorInstance operation) {
		operation.getOperationApplied().updateProgramVariant(operation, variant);
	}

	protected void undoOperationToSpoonElement(OperatorInstance operation) {
		operation.undoModification();

	}

	protected void applyPreviousMutationOperationToSpoonElement(OperatorInstance operation)
			throws IllegalAccessException {
		this.applyNewMutationOperationToSpoonElement(operation);

	}

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	protected void applyNewMutationOperationToSpoonElement(OperatorInstance operationInstance)
			throws IllegalAccessException {

		operationInstance.applyModification();

	}

	public AstorOutputStatus getOutputStatus() {
		return outputStatus;
	}

	public void setOutputStatus(AstorOutputStatus outputStatus) {
		this.outputStatus = outputStatus;
	}

	public List<ReportResults> getOutputResults() {
		return outputResults;
	}

	public void setOutputResults(List<ReportResults> outputResults) {
		this.outputResults = outputResults;
	}

	public List<PatchStat> getPatchInfo() {
		return patchInfo;
	}

	public void setPatchInfo(List<PatchStat> patchInfo) {
		this.patchInfo = patchInfo;
	}

	public SuspiciousNavigationStrategy getSuspiciousNavigationStrategy() {
		return suspiciousNavigationStrategy;
	}

	public void setSuspiciousNavigationStrategy(SuspiciousNavigationStrategy suspiciousNavigationStrategy) {
		this.suspiciousNavigationStrategy = suspiciousNavigationStrategy;
	}

	public List<SuspiciousCode> calculateSuspicious() throws Exception {
		return this.getFaultLocalization().searchSuspicious(getProjectFacade()).getCandidates();
	}

	public List<TargetElementProcessor<?>> getTargetElementProcessors() {
		return targetElementProcessors;
	}

	public void setTargetElementProcessors(List<TargetElementProcessor<?>> targetElementProcessors) {
		this.targetElementProcessors = targetElementProcessors;
	}

	protected void setPropertyIfNotDefined(String name, String value) {
		String existingvalue = ConfigurationProperties.properties.getProperty(name);
		if (existingvalue == null || existingvalue.trim().isEmpty()) {
			ConfigurationProperties.properties.setProperty(name, value);
		}

	}

	public Stats getCurrentStat() {
		return currentStat;
	}

	public void setCurrentStat(Stats currentStat) {
		this.currentStat = currentStat;
	}

	public void reset(ProgramVariant programVariant) {

		// We remove previous variants
		this.solutions.clear();
		this.variants.clear();
		// We add the new one //TODO: we could do a for
		this.variants.add(programVariant);
		// The parameter becomes the original
		this.originalVariant = programVariant;
		// Removing patch info
		this.patchInfo.clear();

		this.outputStatus = null;

		this.originalVariant.setId(firstgenerationIndex);

		// Saving bytecode
		String bytecodeOutput = projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		File variantOutputFile = new File(bytecodeOutput);
		MutationSupporter.currentSupporter.getOutput().saveByteCode(programVariant.getCompilation(), variantOutputFile);

		this.currentStat = Stats.createStat();
	}

	protected void loadFaultLocalization() throws Exception {

		// Fault localization
		String flvalue = ConfigurationProperties.getProperty("faultlocalization").toLowerCase();
		if (flvalue.equals("gzoltar")) {
			this.setFaultLocalization(new GZoltarFaultLocalization());
		} else if (flvalue.equals("cocospoon")) {
			this.setFaultLocalization(new CocoFaultLocalization());
		} else
			this.setFaultLocalization(
					(FaultLocalizationStrategy) PlugInLoader.loadPlugin(ExtensionPoints.FAULT_LOCALIZATION));

	}

	protected void loadSuspiciousNavigation() throws Exception {
		String mode = ConfigurationProperties.getProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier)
				.toUpperCase();

		SuspiciousNavigationStrategy suspiciousNavigationStrategy = null;
		if (SuspiciousNavigationValues.INORDER.toString().equals(mode)) {
			suspiciousNavigationStrategy = new InOrderSuspiciousNavigation();
		} else if (SuspiciousNavigationValues.WEIGHT.toString().equals(mode))
			suspiciousNavigationStrategy = new WeightRandomSuspiciousNavitation();
		else if (SuspiciousNavigationValues.RANDOM.toString().equals(mode)) {
			suspiciousNavigationStrategy = new UniformRandomSuspiciousNavigation();
		} else if (SuspiciousNavigationValues.SEQUENCE.toString().equals(mode)) {
			suspiciousNavigationStrategy = new SequenceSuspiciousNavigationStrategy();
		} else {
			suspiciousNavigationStrategy = (SuspiciousNavigationStrategy) PlugInLoader
					.loadPlugin(ExtensionPoints.SUSPICIOUS_NAVIGATION);
		}
		this.setSuspiciousNavigationStrategy(suspiciousNavigationStrategy);
	}

	protected void loadOperatorSpaceDefinition() throws Exception {

		// We check if the user defines the operators to include in the operator
		// space
		OperatorSpace operatorSpace = null;
		String customOp = ConfigurationProperties.getProperty("customop");
		if (customOp != null && !customOp.isEmpty()) {
			operatorSpace = createCustomOperatorSpace(customOp);
		} else {
			customOp = ConfigurationProperties.getProperty("operatorspace");
			if ("irr-statements".equals(customOp) || "jgenprogspace".equals(customOp)) {
				operatorSpace = new jGenProgSpace();
			} else if ("relational-Logical-op".equals(customOp) || "mutationspace".equals(customOp)) {
				operatorSpace = new MutRepairSpace();
			} else if ("suppression".equals(customOp) || "jkalispace".equals(customOp)) {
				operatorSpace = new JKaliSpace();
			} else if ("r-expression".equals(customOp) || "cardumenspace".equals(customOp)) {
				operatorSpace = new CardumenOperatorSpace();
			} else
			// Custom
			if (customOp != null && !customOp.isEmpty())
				operatorSpace = (OperatorSpace) PlugInLoader.loadPlugin(ExtensionPoints.OPERATORS_SPACE);
		}

		this.setOperatorSpace(operatorSpace);

	}

	protected static OperatorSpace createCustomOperatorSpace(String customOp) throws Exception {
		OperatorSpace customSpace = new OperatorSpace();
		String[] operators = customOp.split(File.pathSeparator);
		for (String op : operators) {
			AstorOperator aop = (AstorOperator) PlugInLoader.loadPlugin(op, ExtensionPoints.CUSTOM_OPERATOR._class);
			if (aop != null)
				customSpace.register(aop);
		}
		if (customSpace.getOperators().isEmpty()) {

			throw new Exception("Empty custom operator space");
		}
		return customSpace;
	}

	protected void loadFitnessFunction() throws Exception {
		// Fault localization
		this.setFitnessFunction((FitnessFunction) PlugInLoader.loadPlugin(ExtensionPoints.FITNESS_FUNCTION));
	}

	protected void loadOperatorSelectorStrategy() throws Exception {
		String opStrategyClassName = ConfigurationProperties.properties.getProperty("opselectionstrategy");
		if (opStrategyClassName != null) {
			if ("uniform-random".equals(opStrategyClassName)) {
				this.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(this.getOperatorSpace()));
			} else if ("weighted-random".equals(opStrategyClassName)) {
				this.setOperatorSelectionStrategy(new WeightedRandomOperatorSelection(this.getOperatorSpace()));
			} else {
				OperatorSelectionStrategy strategy = createOperationSelectionStrategy(opStrategyClassName,
						this.getOperatorSpace());
				this.setOperatorSelectionStrategy(strategy);
			}
		} else {// By default, uniform strategy
			this.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(this.getOperatorSpace()));
		}
	}

	protected OperatorSelectionStrategy createOperationSelectionStrategy(String opSelectionStrategyClassName,
			OperatorSpace space) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(opSelectionStrategyClassName);
			object = classDefinition.getConstructor(OperatorSpace.class).newInstance(space);
		} catch (Exception e) {
			throw new Exception("Loading strategy: " + e);
		}
		if (object instanceof OperatorSelectionStrategy)
			return (OperatorSelectionStrategy) object;
		else
			throw new Exception("The strategy " + opSelectionStrategyClassName + " does not extend from "
					+ OperatorSelectionStrategy.class.getName());
	}

	protected void loadValidator() throws Exception {

		// After initializing population, we set up specific validation
		// mechanism
		// Select the kind of validation of a variant.
		String validationArgument = ConfigurationProperties.properties.getProperty("validation");
		if (validationArgument.equals("evosuite") || validationArgument.equals("augmented-test-suite")) {
			ProcessEvoSuiteValidator validator = new ProcessEvoSuiteValidator();
			this.setProgramValidator(validator);
		} else
		// if validation is different to default (process)
		if (validationArgument.equals("process") || validationArgument.equals("test-suite")) {
			ProcessValidator validator = new ProcessValidator();
			this.setProgramValidator(validator);
		} else {
			this.setProgramValidator((ProgramVariantValidator) PlugInLoader.loadPlugin(ExtensionPoints.VALIDATION));
		}
	}

	protected void loadCompiler() throws Exception {
		this.setCompiler((VariantCompiler) PlugInLoader.loadPlugin(ExtensionPoints.COMPILER));
	}

	protected void loadPopulation() throws Exception {
		// Population controller
		this.setPopulationControler(
				(PopulationController) PlugInLoader.loadPlugin(ExtensionPoints.POPULATION_CONTROLLER));
	}

	protected void loadTargetElements() throws Exception {

		List<TargetElementProcessor<?>> targetElementProcessors = new ArrayList<TargetElementProcessor<?>>();

		this.setTargetElementProcessors(targetElementProcessors);
		// Fix Space
		ExtensionPoints epoint = ExtensionPoints.TARGET_CODE_PROCESSOR;
		if (!ConfigurationProperties.hasProperty(epoint.identifier)) {
			// By default, we use statements as granularity level.
			this.getTargetElementProcessors().add(new SingleStatementFixSpaceProcessor());
		} else {
			// We load custom processors
			String ingrProcessors = ConfigurationProperties.getProperty(epoint.identifier);
			String[] in = ingrProcessors.split(File.pathSeparator);
			for (String processor : in) {
				if (processor.equals("statements")) {
					this.getTargetElementProcessors().add(new SingleStatementFixSpaceProcessor());
				} else if (processor.equals("expression")) {
					this.getTargetElementProcessors().add(new ExpressionIngredientSpaceProcessor());
				} else if (processor.equals("logical-relationaloperators")) {
					this.getTargetElementProcessors().add(new IFExpressionFixSpaceProcessor());
				} else if (processor.equals("if-conditions")) {
					this.getTargetElementProcessors().add(new IFConditionFixSpaceProcessor());
				} else {
					TargetElementProcessor proc_i = (TargetElementProcessor) PlugInLoader.loadPlugin(processor,
							epoint._class);
					targetElementProcessors.add(proc_i);
				}
			}
		}
		this.setVariantFactory(new ProgramVariantFactory(this.getTargetElementProcessors()));
	}

	protected void loadSolutionPrioritization() throws Exception {

		String patchpriority = ConfigurationProperties.getProperty("patchprioritization");
		if (patchpriority != null && !patchpriority.trim().isEmpty()) {
			SolutionVariantSortCriterion priorizStrategy = null;

			priorizStrategy = (SolutionVariantSortCriterion) PlugInLoader
					.loadPlugin(ExtensionPoints.SOLUTION_SORT_CRITERION);
			this.setPatchSortCriterion(priorizStrategy);

		}
	}

	protected void loadOutputResults() throws Exception {

		List<ReportResults> outputs = new ArrayList<>();
		this.setOutputResults(outputs);

		String outputproperty = ConfigurationProperties.getProperty("outputresults");
		if (outputproperty != null && !outputproperty.trim().isEmpty()) {
			String[] outprocess = outputproperty.split("|");

			for (String outp : outprocess) {
				ReportResults outputresult = (ReportResults) PlugInLoader.loadPlugin(outp,
						ExtensionPoints.OUTPUT_RESULTS._class);
				outputs.add(outputresult);
			}

		} else {
			outputs.add(new StandardOutputReport());
			outputs.add(new PatchJSONStandarOutput());
		}

	}

	public void loadExtensionPoints() throws Exception {
		this.loadFaultLocalization();
		this.loadTargetElements();
		this.loadSuspiciousNavigation();
		this.loadCompiler();
		this.loadValidator();
		this.loadPopulation();
		this.loadFitnessFunction();
		this.loadOperatorSpaceDefinition();
		this.loadOperatorSelectorStrategy();
		this.loadSolutionPrioritization();
		this.loadOutputResults();

	}

}
