package fr.inria.astor.core.loop.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.bridgeFLSpoon.SpoonLocationPointerLauncher;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
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

	protected List<AbstractFixSpaceProcessor<?>> processors = null;

	protected boolean resetOperations;
	
	protected ProjectRepairFacade projectFacade;

	public ProgramVariantFactory() {
		super();
	}

	public ProgramVariantFactory(List<AbstractFixSpaceProcessor<?>> processors) {
		this();
		this.processors = processors;
	}

	/**
	 * Create a list of Program Variant from a list of suspicious code.
	 * 
	 * @param suspiciousList
	 * @param maxNumberInstances
	 * @param populationControler
	 * @param projectFacade
	 * @return
	 * @throws Exception
	 */
	public List<ProgramVariant> createInitialPopulation(List<SuspiciousCode> suspiciousList, int maxNumberInstances,
			PopulationController populationControler, ProjectRepairFacade projectFacade) throws Exception {
		
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
			for (SuspiciousCode suspiciousCode : suspiciousList) {
				// For each suspicious code, we create one or more Gens (when it
				// is possible)
				List<SuspiciousModificationPoint> modifPoints = createModificationPoints(suspiciousCode, progInstance);
				if (modifPoints != null)
					progInstance.getModificationPoints().addAll(modifPoints);
				else {
					log.info("-any mod point created for suspicious " + suspiciousCode);
				}

			}
			log.info("Total suspicious from FL: " + suspiciousList.size() + ",  "
					+ progInstance.getModificationPoints().size());
		} else {
			// We do not have suspicious, so, we create modification for each statement

			List<SuspiciousModificationPoint> pointsFromAllStatements = createModificationPoints(progInstance);
			progInstance.getModificationPoints().addAll(pointsFromAllStatements);
		}
		log.info("Total ModPoint created: " + progInstance.getModificationPoints().size());
		return progInstance;
	}

	@SuppressWarnings("rawtypes")
	private List<SuspiciousModificationPoint> createModificationPoints(ProgramVariant progInstance) {

		List<SuspiciousModificationPoint> suspGen = new ArrayList<>();
		List<CtClass> classesFromModel = mutatorSupporter.getClasses();

		for (CtClass ctclasspointed : classesFromModel) {
			
			List<String> allTest = projectFacade.getProperties().getRegressionTestCases();
			String testn = ctclasspointed.getQualifiedName();
			if(allTest.contains(testn) ){
				//it's a test, we ignore it
				log.debug("ModifPoints creation: Ignoring test case "+testn);
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

			for (CtElement suspiciousElement : extractedElements) {

				List<CtVariable> contextOfGen = VariableResolver.getVariablesFromBlockInScope(suspiciousElement);

				SuspiciousModificationPoint point = new SuspiciousModificationPoint();
				point.setSuspicious(new SuspiciousCode(ctclasspointed.getQualifiedName(), "",
						suspiciousElement.getPosition().getLine(), 0d));
				point.setClonedClass(ctclasspointed);
				point.setCodeElement(suspiciousElement);
				point.setContextOfModificationPoint(contextOfGen);
				suspGen.add(point);
				log.info("--ModificationPoint:" + suspiciousElement.getClass().getSimpleName() + ", suspValue "
						+ point.getSuspicious().getSuspiciousValue() + ", line "
						+ suspiciousElement.getPosition().getLine() + ", file "
						+ suspiciousElement.getPosition().getFile().getName());
			}

		}
		return suspGen;

	}

	/**
	 * It receives a suspicious code (a line) and it create a list of Gens from
	 * than suspicious line when it's possible.
	 * 
	 * @param suspiciousCode
	 * @param progInstance
	 * @return
	 */
	private List<SuspiciousModificationPoint> createModificationPoints(SuspiciousCode suspiciousCode, ProgramVariant progInstance) {

		List<SuspiciousModificationPoint> suspGen = new ArrayList<SuspiciousModificationPoint>();

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

		contextOfPoint = VariableResolver.getVariablesFromBlockInScope(ctSuspects.get(0));

		// From the suspicious CtElements, there are some of them we are
		// interested in.
		// We filter them using the processors
		List<CtElement> filterByType = extractChildElements(ctSuspects, processors);

		List<CtElement> filteredTypeByLine = intersection(filterByType, ctSuspects);
		// For each filtered element, we create a Gen.
		int id = 0;
		for (CtElement ctElement : filteredTypeByLine) {
			SuspiciousModificationPoint modifPoint = new SuspiciousModificationPoint();
			modifPoint.setSuspicious(suspiciousCode);
			modifPoint.setClonedClass(ctclasspointed);
			modifPoint.setCodeElement(ctElement);
			modifPoint.setContextOfModificationPoint(contextOfPoint);
			suspGen.add(modifPoint);
			log.info("--ModifPoint:" + ctElement.getClass().getSimpleName() + ", suspValue "
					+ suspiciousCode.getSuspiciousValue() + ", line " + ctElement.getPosition().getLine() + ", file "
					+ ctElement.getPosition().getFile().getName());
		}
		return suspGen;
	}

	/**
	 * Retrieve the ct elements we want to consider in our model, for instance,
	 * some approach are interested only in repair If conditions.
	 * 
	 * @param ctSuspects
	 * @param processors
	 * @return
	 */
	@SuppressWarnings(value = { "unchecked", "rawtypes" })
	private List<CtElement> extractChildElements(List<CtElement> ctSuspects,
			List<AbstractFixSpaceProcessor<?>> processors) {

		if (processors == null || processors.isEmpty()) {
			return ctSuspects;
		}

		List<CtElement> ctMatching = new ArrayList<CtElement>();

		try {
			IngredientProcessor spaceProcessor = new IngredientProcessor(processors);
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
	 * resolved before, it get it from a "cache" of CtClasses stored in the
	 * Program Instance.
	 * 
	 * @param suspiciousCode
	 * @param progInstance
	 * @return
	 */
	public CtClass resolveCtClass(String className, ProgramVariant progInstance) {

		// if the ctclass exists in the cache, return it.
		if (progInstance.getBuiltClasses().containsKey(className)) {
			return progInstance.getBuiltClasses().get(className);
		}

		CtClass ctclasspointed = getCtClassCloned(className);
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
		childVariant.getModificationPoints().addAll(parentVariant.getModificationPoints());

		if (!ConfigurationProperties.getPropertyBool("resetoperations"))
			childVariant.getOperations().putAll(parentVariant.getOperations());
		childVariant.setLastGenAnalyzed(parentVariant.getLastGenAnalyzed());
		childVariant.getBuiltClasses().putAll(parentVariant.getBuiltClasses());
		childVariant.setFitness(parentVariant.getFitness());
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

	public CtClass getCtClassCloned(String className) {

		CtType ct = mutatorSupporter.getFactory().Type().get(className);
		if (!(ct instanceof CtClass)) {
			return null;
		}

		CtClass ctclass = (CtClass) ct;

		CtClass cloned = mutatorSupporter.getFactory().Core().clone(ctclass);
		cloned.setParent(ctclass.getParent());
		return cloned;
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

	public List<AbstractFixSpaceProcessor<?>> getProcessors() {
		return processors;
	}

	public void setProcessors(List<AbstractFixSpaceProcessor<?>> processors) {
		this.processors = processors;
	}
}
