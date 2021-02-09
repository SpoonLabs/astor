package fr.inria.astor.core.solutionsearch.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.bridgeFLSpoon.SpoonLocationPointerLauncher;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * Creates the initial population of program variants
 *
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class ProgramVariantFactory {

	private Logger log = Logger.getLogger(Thread.currentThread().getName());

	/**
	 * counter of id to assign to program instances
	 */
	protected int idCounter = 0;

	protected MutationSupporter mutatorSupporter = null;

	protected List<TargetElementProcessor<?>> processors = null;

	protected boolean resetOperations;

	protected ProjectRepairFacade projectFacade;

	public ProgramVariantFactory() {
		super();
	}

	public ProgramVariantFactory(List<TargetElementProcessor<?>> processors) {
		this();
		this.processors = processors;
	}

	/**
	 * Create a list of Program Variant from a list of suspicious code.
	 *
	 * @param suspiciousList
	 * @param maxNumberInstances
	 * @param projectFacade
	 * @return
	 * @throws Exception
	 */
	public List<ProgramVariant> createInitialPopulation(List<SuspiciousCode> suspiciousList, int maxNumberInstances,
														ProjectRepairFacade projectFacade) throws Exception {

		this.projectFacade = projectFacade;

		List<ProgramVariant> variants = new ArrayList<ProgramVariant>();

		for (int ins = 1; ins <= maxNumberInstances; ins++) {
			// -Initial setup of directories----------
			idCounter = ins;
			ProgramVariant v_i = createProgramInstance(suspiciousList, idCounter);
			variants.add(v_i);
			log.info("Creating program variant #" + idCounter + ", " + v_i.toString());

			if (ConfigurationProperties.getPropertyBool("saveall")) {
				String srcOutput = projectFacade.getInDirWithPrefix(v_i.currentMutatorIdentifier());
				mutatorSupporter.saveSourceCodeOnDiskProgramVariant(v_i, srcOutput);
			}

		}

		return variants;
	}

	public CtClass getCtClassFromCtElement(CtElement element) {

		if (element == null)
			return null;
		if (element instanceof CtClass)
			return (CtClass) element;

		return getCtClassFromCtElement(element.getParent());
	}

	/**
	 * A Program instances is created from the list of suspicious. For each
	 * suspiciuos a list of modif point is created.
	 *
	 * @param suspiciousList
	 * @param idProgramInstance
	 * @return
	 */
	private ProgramVariant createProgramInstance(List<SuspiciousCode> suspiciousList, int idProgramInstance) {

		ProgramVariant progInstance = new ProgramVariant(idProgramInstance);

		log.debug("Creating variant " + idProgramInstance);

		if (!suspiciousList.isEmpty()) {
			int maxModPoints = ConfigurationProperties.getPropertyInt("maxmodificationpoints");
			for (SuspiciousCode suspiciousCode : suspiciousList) {

				List<SuspiciousModificationPoint> modifPoints = createModificationPoints(suspiciousCode, progInstance);
				if (modifPoints != null && !modifPoints.isEmpty()) {
					progInstance.addModificationPoints(modifPoints);
				}
				if (progInstance.getModificationPoints().size() > maxModPoints) {
					progInstance.setModificationPoints(progInstance.getModificationPoints().subList(0, maxModPoints));
					log.info("Reducing Total ModPoint created to: " + progInstance.getModificationPoints().size());
					// we dont continue creating Modif points
					break;
				}

			}
			log.info("Total suspicious from FL: " + suspiciousList.size() + ",  "
					+ progInstance.getModificationPoints().size());
		} else {
			// We do not have suspicious, so, we create modification for each
			// statement

			List<SuspiciousModificationPoint> pointsFromAllStatements = createModificationPoints(progInstance);
			progInstance.getModificationPoints().addAll(pointsFromAllStatements);
		}
		log.info("Total ModPoint created: " + progInstance.getModificationPoints().size());

		// Defining identified of each modif point
		for (int i = 0; i < progInstance.getModificationPoints().size(); i++) {
			ModificationPoint mp = progInstance.getModificationPoints().get(i);
			mp.identified = i;
		}
		return progInstance;
	}

	@SuppressWarnings("rawtypes")
	private List<SuspiciousModificationPoint> createModificationPoints(ProgramVariant progInstance) {

		List<SuspiciousModificationPoint> suspGen = new ArrayList<>();
		List<CtClass> classesFromModel = MutationSupporter.getFactory().Class().getAll().stream()
				.filter(CtClass.class::isInstance).map(sc -> (CtClass) sc).collect(Collectors.toList());
		for (CtClass ctclasspointed : classesFromModel) {

			List<String> allTest = projectFacade.getProperties().getRegressionTestCases();
			String testn = ctclasspointed.getQualifiedName();
			if (allTest != null && allTest.contains(testn)) {
				// it's a test, we ignore it
				log.debug("ModifPoints creation: Ignoring test case " + testn);
				continue;
			}

			if (!progInstance.getBuiltClasses().containsKey(ctclasspointed.getQualifiedName())) {
				// TODO: clone or not?
				// CtClass ctclasspointed = getCtClassCloned(className);
				progInstance.getBuiltClasses().put(ctclasspointed.getQualifiedName(), ctclasspointed);
			}

			List<CtElement> classesToProcess = new ArrayList<>();
			classesToProcess.add(ctclasspointed);
			List<CtElement> extractedElements = extractChildElements(classesToProcess, processors);

			// remove the elements that are instance of NoSourcePosition
			extractedElements = extractedElements.stream().filter(ctElement ->
					!(ctElement.getPosition() instanceof NoSourcePosition))
					.collect(Collectors.toList());

			int maxModPoints = ConfigurationProperties.getPropertyInt("maxmodificationpoints");

			for (CtElement suspiciousElement : extractedElements) {
					List<CtVariable> contextOfGen = VariableResolver.searchVariablesInScope(suspiciousElement);

					SuspiciousModificationPoint point = new SuspiciousModificationPoint();
					point.setSuspicious(new SuspiciousCode(ctclasspointed.getQualifiedName(), "",
							suspiciousElement.getPosition().getLine(), 0d, null));
					point.setCtClass(ctclasspointed);
					point.setCodeElement(suspiciousElement);
					point.setContextOfModificationPoint(contextOfGen);
					suspGen.add(point);
					log.info("--ModificationPoint:" + suspiciousElement.getClass().getSimpleName() + ", suspValue "
							+ point.getSuspicious().getSuspiciousValue() + ", line "
							+ suspiciousElement.getPosition().getLine() + ", file "
							+ suspiciousElement.getPosition().getFile().getName());

					if (suspGen.size() > maxModPoints) {
						log.info("Reducing Total ModPoint created to: " + maxModPoints);
						return suspGen;
					}
				}

		}
		return suspGen;

	}

	/**
	 * It receives a suspicious code (a line) and it create a list of Gens from than
	 * suspicious line when it's possible.
	 *
	 * @param suspiciousCode
	 * @param progInstance
	 * @return
	 */
	private List<SuspiciousModificationPoint> createModificationPoints(SuspiciousCode suspiciousCode,
			ProgramVariant progInstance) {

		List<SuspiciousModificationPoint> suspiciousModificationPoints = new ArrayList<SuspiciousModificationPoint>();

		CtClass ctclasspointed = resolveCtClass(suspiciousCode.getClassName(), progInstance);
		if (ctclasspointed == null) {
			log.info(" Not ctClass for suspicious code " + suspiciousCode);
			return null;
		}

		List<CtElement> ctSuspects = null;
		try {
			ctSuspects = retrieveCtElementForSuspectCode(suspiciousCode, ctclasspointed);
			// The parent first, so I inverse the order
			Collections.reverse(ctSuspects);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		// if we are not able to retrieve suspicious CtElements, we return
		if (ctSuspects.isEmpty()) {
			return null;
		}

		List<CtVariable> contextOfPoint = null;
		// We take the first element for getting the context (as the remaining
		// have the same location, it's not necessary)

		contextOfPoint = VariableResolver.searchVariablesInScope(ctSuspects.get(0));

		// From the suspicious CtElements, there are some of them we are
		// interested in.
		// We filter them using the processors
		List<CtElement> filterByType = extractChildElements(ctSuspects, processors);

		List<CtElement> filteredTypeByLine = intersection(filterByType, ctSuspects);

		// remove the elements that are instance of NoSourcePosition
		filteredTypeByLine = filteredTypeByLine.stream().filter(ctElement ->
				!(ctElement.getPosition() instanceof NoSourcePosition))
				.collect(Collectors.toList());
		// For each filtered element, we create a ModificationPoint.
		for (CtElement ctElement : filteredTypeByLine) {
                SuspiciousModificationPoint modifPoint = new SuspiciousModificationPoint();
                modifPoint.setSuspicious(suspiciousCode);
                modifPoint.setCtClass(ctclasspointed);
                modifPoint.setCodeElement(ctElement);
                modifPoint.setContextOfModificationPoint(contextOfPoint);
                suspiciousModificationPoints.add(modifPoint);
                log.debug("--ModifPoint:" + ctElement.getClass().getSimpleName() + ", suspValue "
                        + suspiciousCode.getSuspiciousValue() + ", line " + ctElement.getPosition().getLine() + ", file "
                        + ((ctElement.getPosition().getFile() == null) ? "-null-file-"
                        : ctElement.getPosition().getFile().getName()));
        }
		return suspiciousModificationPoints;
	}

	/**
	 * Retrieve the ct elements we want to consider in our model, for instance, some
	 * approach are interested only in repair If conditions.
	 *
	 * @param ctSuspects
	 * @param processors
	 * @return
	 */
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	private List<CtElement> extractChildElements(List<CtElement> ctSuspects,
			List<TargetElementProcessor<?>> processors) {

		if (processors == null || processors.isEmpty()) {
			return ctSuspects;
		}

		List<CtElement> ctMatching = new ArrayList<CtElement>();

		try {
			CodeParserLauncher spaceProcessor = new CodeParserLauncher(processors);
			for (CtElement element : ctSuspects) {
				List<CtElement> result = spaceProcessor.createFixSpace(element, false);

				for (CtElement ctElement : result) {
					if (ctElement.toString().equals("super()")) {
						continue;
					}
					if (!ctMatching.contains(ctElement))
						ctMatching.add(ctElement);
				}
			}

		} catch (JSAPException e) {
			e.printStackTrace();
		}

		return ctMatching;
	}

	/**
	 * This method revolve a CtClass from one suspicious statement. If it was
	 * resolved before, it get it from a "cache" of CtClasses stored in the Program
	 * Instance.
	 *
	 * @param progInstance
	 * @return
	 */
	public CtClass resolveCtClass(String className, ProgramVariant progInstance) {

		// if the ctclass exists in the cache, return it.
		if (progInstance.getBuiltClasses().containsKey(className)) {
			return progInstance.getBuiltClasses().get(className);
		}

		CtClass ctclasspointed = getCtClassFromName(className);
		if (ctclasspointed == null)
			return null;
		// Save the CtClass in cache
		progInstance.getBuiltClasses().put(className, ctclasspointed);

		return ctclasspointed;
	}

	public ProgramVariant createProgramVariantFromAnother(ProgramVariant parentVariant, int generation) {
		idCounter++;
		return this.createProgramVariantFromAnother(parentVariant, idCounter, generation);
	}

	/**
	 * New Program Variant Clone
	 *
	 * @param parentVariant
	 * @param id
	 * @return
	 */
	public ProgramVariant createProgramVariantFromAnother(ProgramVariant parentVariant, int id, int generation) {

		ProgramVariant childVariant = new ProgramVariant(id);
		childVariant.setGenerationSource(generation);
		childVariant.setParent(parentVariant);
		// childVariant.copyModificationPoints(parentVariant.getModificationPoints());
		childVariant.addModificationPoints(parentVariant.getModificationPoints());

		if (!ConfigurationProperties.getPropertyBool("resetoperations"))
			childVariant.getOperations().putAll(parentVariant.getOperations());
		childVariant.setLastModificationPointAnalyzed(parentVariant.getLastModificationPointAnalyzed());
		childVariant.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
		childVariant.setFitness(parentVariant.getFitness());
		childVariant.setValidationResult(parentVariant.getValidationResult());
		childVariant.setPatchDiff(parentVariant.getPatchDiff());
		return childVariant;

	}

	/**
	 * TODO: Replicated in RepairActionLoops
	 *
	 * @param candidate
	 * @param ctclass
	 * @return
	 * @throws Exception
	 */
	public List<CtElement> retrieveCtElementForSuspectCode(SuspiciousCode candidate, CtElement ctclass)
			throws Exception {

		SpoonLocationPointerLauncher muSpoonLaucher = new SpoonLocationPointerLauncher(MutationSupporter.getFactory());
		List<CtElement> susp = muSpoonLaucher.run(ctclass, candidate.getLineNumber());

		return susp;
	}

	@SuppressWarnings({ "static-access", "rawtypes" })
	public CtClass getCtClassFromName(String className) {

		CtType ct = mutatorSupporter.getFactory().Type().get(className);
		if (!(ct instanceof CtClass)) {
			return null;
		}

		return (CtClass) ct;
	}

	public MutationSupporter getMutatorExecutor() {
		return mutatorSupporter;
	}

	public void setMutatorExecutor(MutationSupporter mutatorExecutor) {
		this.mutatorSupporter = mutatorExecutor;
	}

	public static SuspiciousModificationPoint clonePoint(SuspiciousModificationPoint existingGen, CtElement modified) {
		SuspiciousCode suspicious = existingGen.getSuspicious();
		CtClass ctClass = existingGen.getCtClass();
		List<CtVariable> context = existingGen.getContextOfModificationPoint();
		SuspiciousModificationPoint newGen = new SuspiciousModificationPoint(suspicious, modified, ctClass, context);
		return newGen;

	}

	public static ModificationPoint clonePoint(ModificationPoint existingGen, CtElement modified) {
		CtClass ctClass = existingGen.getCtClass();
		List<CtVariable> context = existingGen.getContextOfModificationPoint();
		ModificationPoint newGen = new ModificationPoint(modified, ctClass, context);
		return newGen;

	}

	public <T> List<T> intersection(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<T>();

		for (T t : list1) {
			try {
				if (list2.contains(t)) {
					list.add(t);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		return list;
	}

	public List<TargetElementProcessor<?>> getProcessors() {
		return processors;
	}

	public void setProcessors(List<TargetElementProcessor<?>> processors) {
		this.processors = processors;
	}
}
