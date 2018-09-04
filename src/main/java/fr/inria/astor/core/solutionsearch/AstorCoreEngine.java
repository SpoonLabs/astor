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
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.astor.core.stats.PatchStat.PatchStatEnum;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.core.stats.Stats.GeneralStatEnum;
import fr.inria.astor.core.validation.ProgramVariantValidator;
import fr.inria.astor.core.validation.processbased.ProcessValidator;
import fr.inria.astor.util.PatchDiffCalculator;
import fr.inria.astor.util.TimeUtil;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

/**
 * 
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

	protected FaultLocalizationStrategy faultLocalization = null;

	protected SolutionVariantSortCriterion patchSortCriterion = null;

	protected FitnessFunction fitnessFunction = null;

	protected VariantCompiler compiler = null;

	protected List<TargetElementProcessor<?>> targetElementProcessors = null;

	// Output
	protected List<ReportResults> outputResults = null;

	/// // Flag, output status, results
	protected List<ProgramVariant> variants = new ArrayList<ProgramVariant>();

	protected List<ProgramVariant> solutions = new ArrayList<ProgramVariant>();

	protected ProgramVariant originalVariant = null;

	protected Date dateInitEvolution = new Date();
	protected Date dateEngineCreation = new Date();

	protected int generationsExecuted = 0;

	protected int nrGenerationWithoutModificatedVariant = 0;

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

	public abstract void startEvolution() throws Exception;

	public void atEnd() {

		long startT = dateInitEvolution.getTime();
		long endT = System.currentTimeMillis();
		log.info("Time Repair Loop (s): " + (endT - startT) / 1000d);
		currentStat.getGeneralStats().put(GeneralStatEnum.TOTAL_TIME, ((endT - startT)) / 1000d);
		log.info("generationsexecuted: " + this.generationsExecuted);

		currentStat.getGeneralStats().put(GeneralStatEnum.OUTPUT_STATUS, this.getOutputStatus());
		currentStat.getGeneralStats().put(GeneralStatEnum.EXECUTION_IDENTIFIER,
				ConfigurationProperties.getProperty("projectIdentifier"));

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
			patchInfo = createStatsForPatches(solutions, generationsExecuted, dateInitEvolution);
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
	 * @param dateInit   start date of execution
	 * @param maxMinutes max minutes for operating
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

	Map<String, String> originalModel = new HashedMap();
	Map<String, String> modifModel = new HashedMap();

	protected void saveOriginalVariant(ProgramVariant variant) {
		originalModel.clear();
		for (CtType st : variant.getAffectedClasses()) {
			try {
				originalModel.put(st.getQualifiedName(), st.toString());
			} catch (Exception e) {
				log.error("Problems saving cttype: " + st.getQualifiedName());
			}
		}

	}

	protected void saveModifVariant(ProgramVariant variant) {
		modifModel.clear();
		for (CtType st : variant.getAffectedClasses()) {
			try {
				modifModel.put(st.getQualifiedName(), st.toString());
			} catch (Exception e) {
				log.error("Problems saving cttype: " + st.getQualifiedName());
			}

		}

	}

	protected boolean validateReversedOriginalVariant(ProgramVariant variant) {

		for (CtType st : variant.getAffectedClasses()) {
			String original = originalModel.get(st.getQualifiedName());
			if (original != null) {
				boolean idem = original.equals(st.toString());
				if (!idem) {
					log.error("Error variant :" + variant.getId()
							+ " the model was not the same from the original after this generation");
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
		savePatchDiff(programVariant, !codeFormated);
		savePatchDiff(programVariant, codeFormated);

	}

	private void savePatchDiff(ProgramVariant programVariant, boolean format) throws Exception {

		boolean originalValue = ConfigurationProperties.getPropertyBool("preservelinenumbers");

		String srcOutput = determineSourceFolderInWorkspace(programVariant, format);
		log.debug("\n-Saving child on disk variant #" + programVariant.getId() + " at " + srcOutput);
		ConfigurationProperties.setProperty("preservelinenumbers", Boolean.toString(!format));
		mutatorSupporter.saveSourceCodeOnDiskProgramVariant(programVariant, srcOutput);

		ConfigurationProperties.setProperty("preservelinenumbers", Boolean.toString(originalValue));
	}

	private String determineSourceFolderInWorkspace(ProgramVariant programVariant, boolean format) {
		final String suffix = format ? PatchDiffCalculator.DIFF_SUFFIX : "";
		String srcOutput = projectFacade.getInDirWithPrefix(programVariant.currentMutatorIdentifier()) + suffix;
		return srcOutput;
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
	 * Undo in reverse order that the mutation were applied.
	 * 
	 * @param variant
	 * @param generation
	 */
	public void reverseOperationInModel(ProgramVariant variant, int generation) {

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
	 * This method updates gens of a variant according to a created
	 * GenOperationInstance
	 * 
	 * @param variant
	 * @param operationofGen
	 */
	protected void updateVariantGenList(ProgramVariant variant, int generation) {
		List<OperatorInstance> operations = variant.getOperations().get(generation);

		for (OperatorInstance genOperationInstance : operations) {
			updateVariantGenList(variant, genOperationInstance);
		}
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
							? genOperationInstance.getIngredientScope()
							: "-");

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
	 * By default, it initializes the spoon model. It should not be created before.
	 * Otherwise, an exception occurs.
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
		long inittime = System.currentTimeMillis();
		VariantValidationResult validationResult = validateInstance(originalVariant);
		long endtime = System.currentTimeMillis();

		if (validationResult == null) {
			log.error("Initial run of test suite fails");

		} else if (validationResult.isSuccessful()) {
			throw new IllegalStateException("The application under repair has not failling test cases");
		}

		if (ConfigurationProperties.getPropertyBool("overridemaxtime")) {
			Long diff = (endtime - inittime) * 2;// in milliseconds
			ConfigurationProperties.setProperty("tmax2", diff.toString());
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
			if (ConfigurationProperties.getPropertyBool("resetmodel")) {
				Factory fcurrent = MutationSupporter.getFactory();
				log.debug("The Spoon Model was already built.");
				Factory fnew = MutationSupporter.cleanFactory();
				log.debug("New factory created? " + !fnew.equals(fcurrent));
			} else {
				log.debug("we keep previous factory");
				// we do not generate a new model
				return;
			}
		}

		String codeLocation = "";
		if (ConfigurationProperties.getPropertyBool("parsesourcefromoriginal")) {
			List<String> codeLocations = projectFacade.getProperties().getOriginalDirSrc();
			for (String source : codeLocations) {
				codeLocation += source + File.pathSeparator;
			}
			if (codeLocation.length() > 0) {
				codeLocation = codeLocation.substring(0, codeLocation.length() - 1);
			}
		} else {
			codeLocation = projectFacade.getInDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		}

		String bytecodeLocation = projectFacade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		String classpath = projectFacade.getProperties().getDependenciesString();
		String[] cpArray = classpath.split(File.pathSeparator);

		log.info("Creating model,  Code location from working folder: " + codeLocation);

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
	 * This method updates modification point of a variant according to a created
	 * GenOperationInstance
	 * 
	 * @param variant        variant to modify the modification point information
	 * @param operationofGen operator to apply in the variant.
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
		String repairOperatorsArgument = ConfigurationProperties
				.getProperty(ExtensionPoints.REPAIR_OPERATORS.identifier);
		if (repairOperatorsArgument != null && !repairOperatorsArgument.isEmpty()) {
			operatorSpace = createOperatorSpaceFromArgument(repairOperatorsArgument);
		} else {
			repairOperatorsArgument = ConfigurationProperties.getProperty(ExtensionPoints.OPERATORS_SPACE.identifier);
			if ("irr-statements".equals(repairOperatorsArgument) || "jgenprogspace".equals(repairOperatorsArgument)) {
				operatorSpace = new jGenProgSpace();
			} else if ("relational-Logical-op".equals(repairOperatorsArgument)
					|| "mutationspace".equals(repairOperatorsArgument)) {
				operatorSpace = new MutRepairSpace();
			} else if ("suppression".equals(repairOperatorsArgument) || "jkalispace".equals(repairOperatorsArgument)) {
				operatorSpace = new JKaliSpace();
			} else if ("r-expression".equals(repairOperatorsArgument)
					|| "cardumenspace".equals(repairOperatorsArgument)) {
				operatorSpace = new CardumenOperatorSpace();
			} else
			// Custom
			if (repairOperatorsArgument != null && !repairOperatorsArgument.isEmpty())
				operatorSpace = (OperatorSpace) PlugInLoader.loadPlugin(ExtensionPoints.OPERATORS_SPACE);
		}

		this.setOperatorSpace(operatorSpace);

	}

	protected static OperatorSpace createOperatorSpaceFromArgument(String repairOperatorsFromExtensionPoint)
			throws Exception {
		OperatorSpace operatorSpace = new OperatorSpace();
		String[] operators = repairOperatorsFromExtensionPoint.split(File.pathSeparator);
		for (String op : operators) {
			AstorOperator aop = (AstorOperator) PlugInLoader.loadPlugin(op, ExtensionPoints.REPAIR_OPERATORS._class);
			if (aop != null)
				operatorSpace.register(aop);
		}
		if (operatorSpace.getOperators().isEmpty()) {

			throw new Exception("The repair operator space could not be created from the extension point");
		}
		return operatorSpace;
	}

	protected void loadFitnessFunction() throws Exception {
		// Fault localization
		this.setFitnessFunction((FitnessFunction) PlugInLoader.loadPlugin(ExtensionPoints.FITNESS_FUNCTION));
	}

	protected void loadOperatorSelectorStrategy() throws Exception {
		String opStrategyClassName = ConfigurationProperties.properties
				.getProperty(ExtensionPoints.OPERATOR_SELECTION_STRATEGY.identifier);
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

		ExtensionPoints extensionPointpoint = ExtensionPoints.TARGET_CODE_PROCESSOR;

		List<TargetElementProcessor<?>> loadedTargetElementProcessors = loadTargetElements(extensionPointpoint);

		this.setTargetElementProcessors(loadedTargetElementProcessors);
		this.setVariantFactory(new ProgramVariantFactory(this.getTargetElementProcessors()));
	}

	protected List<TargetElementProcessor<?>> loadTargetElements(ExtensionPoints epoint) throws Exception {

		List<TargetElementProcessor<?>> loadedTargetElementProcessors = new ArrayList<TargetElementProcessor<?>>();

		if (!ConfigurationProperties.hasProperty(epoint.identifier)) {
			// By default, we use statements as granularity level.
			loadedTargetElementProcessors.add(new SingleStatementFixSpaceProcessor());
		} else {
			// We load custom processors
			String ingrProcessors = ConfigurationProperties.getProperty(epoint.identifier);
			String[] in = ingrProcessors.split(File.pathSeparator);
			for (String processor : in) {
				if (processor.equals("statements")) {
					loadedTargetElementProcessors.add(new SingleStatementFixSpaceProcessor());
				} else if (processor.equals("expression")) {
					loadedTargetElementProcessors.add(new ExpressionIngredientSpaceProcessor());
				} else if (processor.equals("logical-relationaloperators")) {
					loadedTargetElementProcessors.add(new IFExpressionFixSpaceProcessor());
				} else if (processor.equals("if-conditions")) {
					loadedTargetElementProcessors.add(new IFConditionFixSpaceProcessor());
				} else {
					TargetElementProcessor proc_i = (TargetElementProcessor) PlugInLoader.loadPlugin(processor,
							epoint._class);
					loadedTargetElementProcessors.add(proc_i);
				}
			}
		}
		return loadedTargetElementProcessors;
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

	public List<PatchStat> createStatsForPatches(List<ProgramVariant> variants, int generation,
			Date dateInitEvolution) {
		List<PatchStat> patches = new ArrayList<>();

		for (ProgramVariant solutionVariant : variants) {

			PatchStat patch_i = new PatchStat();
			solutionVariant.setPatchInfo(patch_i);
			patches.add(patch_i);
			patch_i.addStat(PatchStatEnum.TIME,
					TimeUtil.getDateDiff(dateInitEvolution, solutionVariant.getBornDate(), TimeUnit.SECONDS));
			patch_i.addStat(PatchStatEnum.VARIANT_ID, solutionVariant.getId());

			patch_i.addStat(PatchStatEnum.VALIDATION, solutionVariant.getValidationResult().toString());

			patch_i.addStat(PatchStatEnum.PATCH_DIFF_ORIG,
					solutionVariant.getPatchDiff().getOriginalStatementAlignmentDiff());

			patch_i.addStat(PatchStatEnum.PATCH_DIFF_FORMATTED, solutionVariant.getPatchDiff().getFormattedDiff());

			patch_i.addStat(PatchStatEnum.FOLDER_SOLUTION_CODE,
					projectFacade.getOutDirWithPrefix(solutionVariant.currentMutatorIdentifier()));

			List<PatchHunkStats> hunks = new ArrayList<>();
			patch_i.addStat(PatchStatEnum.HUNKS, hunks);

			int lastGeneration = -1;
			for (int i = 1; i <= generation; i++) {
				List<OperatorInstance> genOperationInstances = solutionVariant.getOperations().get(i);
				if (genOperationInstances == null)
					continue;
				lastGeneration = i;

				for (OperatorInstance genOperationInstance : genOperationInstances) {

					PatchHunkStats hunk = new PatchHunkStats();
					hunks.add(hunk);
					hunk.getStats().put(HunkStatEnum.OPERATOR, genOperationInstance.getOperationApplied().toString());
					hunk.getStats().put(HunkStatEnum.LOCATION,
							genOperationInstance.getModificationPoint().getCtClass().getQualifiedName());

					hunk.getStats().put(HunkStatEnum.PATH, genOperationInstance.getModificationPoint().getCtClass()
							.getPosition().getFile().getAbsolutePath());

					boolean originalAlingment = ConfigurationProperties.getPropertyBool("parsesourcefromoriginal");
					String mpath = determineSourceFolderInWorkspace(solutionVariant, !originalAlingment)
							+ File.separator + genOperationInstance.getModificationPoint().getCtClass()
									.getQualifiedName().replace(".", File.separator)
							+ ".java";
					hunk.getStats().put(HunkStatEnum.MODIFIED_FILE_PATH, mpath);

					hunk.getStats().put(HunkStatEnum.MP_RANKING,
							genOperationInstance.getModificationPoint().identified);

					if (genOperationInstance.getModificationPoint() instanceof SuspiciousModificationPoint) {
						SuspiciousModificationPoint gs = (SuspiciousModificationPoint) genOperationInstance
								.getModificationPoint();
						hunk.getStats().put(HunkStatEnum.LINE, gs.getSuspicious().getLineNumber());
						hunk.getStats().put(HunkStatEnum.SUSPICIOUNESS, gs.getSuspicious().getSuspiciousValueString());
					}
					hunk.getStats().put(HunkStatEnum.ORIGINAL_CODE, genOperationInstance.getOriginal().toString());
					hunk.getStats().put(HunkStatEnum.BUGGY_CODE_TYPE,
							genOperationInstance.getOriginal().getClass().getSimpleName() + "|"
									+ genOperationInstance.getOriginal().getParent().getClass().getSimpleName());

					if (genOperationInstance.getModified() != null) {
						// if fix content is the same that original buggy
						// content, we do not write the patch, remaining empty
						// the property fixed statement
						if (genOperationInstance.getModified().toString() != genOperationInstance.getOriginal()
								.toString())

							hunk.getStats().put(HunkStatEnum.PATCH_HUNK_CODE,
									genOperationInstance.getModified().toString());
						else {
							hunk.getStats().put(HunkStatEnum.PATCH_HUNK_CODE,
									genOperationInstance.getOriginal().toString());

						}
						// Information about types Parents

						hunk.getStats().put(HunkStatEnum.PATCH_HUNK_TYPE,
								genOperationInstance.getModified().getClass().getSimpleName() + "|"
										+ genOperationInstance.getModified().getParent().getClass().getSimpleName());
					}

					setParticularStats(hunk, genOperationInstance);

				}
			}
			if (lastGeneration > 0) {
				patch_i.addStat(PatchStatEnum.GENERATION, lastGeneration);

			}

		}
		return patches;
	}

	/**
	 * Set the particularities of each approach
	 * 
	 * @param hunk
	 * @param genOperationInstance
	 */
	protected void setParticularStats(PatchHunkStats hunk, OperatorInstance genOperationInstance) {
		// Nothing by default
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
