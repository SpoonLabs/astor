package fr.inria.astor.core.loop.evolutionary.population;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.factory.FactoryImpl;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.bridgeFLSpoon.SpoonLocationPointerLauncher;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.IngredientProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

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

	
	
	
	public ProgramVariantFactory() {
		super();
	}

	public ProgramVariantFactory(List<AbstractFixSpaceProcessor<?>> processors) {
		this.processors = processors;
	}
/**
 * Create a list of Program Variant from a list of suspicious code. 
 * @param suspiciousList
 * @param maxNumberInstances
 * @param populationControler
 * @param projectFacade
 * @return
 * @throws Exception
 */
	public List<ProgramVariant> createInitialPopulation(List<SuspiciousCode> suspiciousList, 
			int maxNumberInstances,
			PopulationController populationControler,
			ProjectRepairFacade projectFacade) throws Exception {

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
	 * A Program instances is created from the list of suspicious. 
	 * For each suspiciuos a list of gens is created.
	 * 
	 * @param suspiciousList
	 * @param idProgramInstance
	 * @return
	 */
	private ProgramVariant createProgramInstance(List<SuspiciousCode> suspiciousList, int idProgramInstance) {

		ProgramVariant progInstance = new ProgramVariant(idProgramInstance);

		log.debug("Creating variant " + idProgramInstance);

		for (SuspiciousCode suspiciousCode : suspiciousList) {
			//For each suspicious code, we create one or more Gens (when it is possible)
			List<GenSuspicious> gens = createGens(suspiciousCode, progInstance);
			if(gens!= null)
				progInstance.getGenList().addAll(gens);

		}
		// Stats.getCurrentStats().fl_gens_size =
		// progInstance.getGenList().size();
		log.info("Total suspicious from FL: " + suspiciousList.size() + ",  " + progInstance.getGenList().size());
		log.info("Total Gens created: " + progInstance.getGenList().size());
		return progInstance;
	}


	/**
	 * It receives a suspicious code (a line) and it create a list of Gens from than suspicious line when it's possible.
	 * @param suspiciousCode
	 * @param progInstance
	 * @return
	 */
	private List<GenSuspicious> createGens(SuspiciousCode suspiciousCode, ProgramVariant progInstance) {

		List<GenSuspicious> suspGen = new ArrayList<GenSuspicious>();

		CtClass ctclasspointed = resolveCtClass(suspiciousCode, progInstance);
		if (ctclasspointed == null) {
			log.info(" Not ctClass for suspicious code " + suspiciousCode);
			return null;
		}
		//this.fixspace.createFixSpaceFromAClass(ctclasspointed.getQualifiedName(), ctclasspointed);

		List<CtElement> ctSuspects = null;
		try {
			ctSuspects = retrieveCtModelOfSuspect(suspiciousCode, ctclasspointed);
			//The parent first, so I inverse the order 
			Collections.reverse(ctSuspects);
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		//if we are not able to retrieve suspicious CtElements, we return
		if (ctSuspects.isEmpty()) {
			return null;
		}
		//TODO: move
		boolean onlyParent = false;
		//
		if(onlyParent){
			ctSuspects = ctSuspects.subList(0, 1);
		}

		List<CtVariable> contextOfGen = null;
		// We take the first element for getting the context (as the remaining have the same location, it's not necessary)
	
		contextOfGen = VariableResolver.getVariablesFromBlockInScope(ctSuspects.get(0));
		
		
		//From the suspicious CtElements, there are some of them we are interested in.
		//We filter them using the processors
		List<CtElement> filterByType = getMatchedElementsToProcess(ctSuspects, processors);

		List<CtElement> filteredTypeByLine = intersection(filterByType, ctSuspects ); 
		//For each filtered element, we create a Gen. 
		for (CtElement ctElement : filteredTypeByLine) {
			GenSuspicious gen = new GenSuspicious();
			gen.setSuspicious(suspiciousCode);
			gen.setClonedClass(ctclasspointed);
			gen.setRootElement(ctElement);
			gen.setContextOfGen(contextOfGen);
			suspGen.add(gen);
			log.info("--Gen:" + ctElement.getClass().getSimpleName()  + ", suspValue "
					+ suspiciousCode.getSuspiciousValue()
					+", line "+  ctElement.getPosition().getLine()
					+", file "+ctElement.getPosition().getFile().getName());
		}
		return suspGen;
	}

	/**
	 * Retrieve the ct elements we want to represent in our model
	 * 
	 * @param ctSuspects
	 * @param processors
	 * @return
	 */
	private List<CtElement> getMatchedElementsToProcess(List<CtElement> ctSuspects,
			List<AbstractFixSpaceProcessor<?>> processors) {

		if (processors == null || processors.isEmpty()) {
			return ctSuspects;
		}

		List<CtElement> ctMatching = new ArrayList<CtElement>();

		try {
			IngredientProcessor spaceProcessor = new IngredientProcessor(processors);
			for (CtElement element : ctSuspects) {
				List<CtElement> result = spaceProcessor.createFixSpace(element, false);
				//ctMatching.addAll(result);
				for (CtElement ctElement : result) {
					if(!ctMatching.contains(ctElement))
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
	public CtClass resolveCtClass(SuspiciousCode suspiciousCode, ProgramVariant progInstance) {

		// if the ctclass exists in the cache, return it.
		if (progInstance.getBuiltClasses().containsKey(suspiciousCode.getClassName())) {
			return progInstance.getBuiltClasses().get(suspiciousCode.getClassName());
		}

		CtClass ctclasspointed = getCtClassCloned(suspiciousCode);
		if (ctclasspointed == null)
			return null;
		// Save the CtClass in cache
		progInstance.getBuiltClasses().put(suspiciousCode.getClassName(), ctclasspointed);

		return ctclasspointed;
	}

	public ProgramVariant createProgramVariantFromAnother(ProgramVariant parentVariant, int generation) {
		idCounter++;
		return this.createProgramVariantFromAnother(parentVariant, idCounter, generation);
	}

	/**
	 * New Program Variante Clone
	 * 
	 * @param parentVariant
	 * @param id
	 * @return
	 */
	public ProgramVariant createProgramVariantFromAnother(ProgramVariant parentVariant, int id, int generation) {

		ProgramVariant childVariant = new ProgramVariant(id);
		childVariant.setGenerationSource(generation);
		childVariant.setParent(parentVariant);
		childVariant.getGenList().addAll(parentVariant.getGenList());
		childVariant.getOperations().putAll(parentVariant.getOperations());
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
	public List<CtElement> retrieveCtModelOfSuspect(SuspiciousCode candidate, CtElement ctclass) throws Exception {

		//log.debug("Analyzing candidate " + candidate.getClassName() + " , line " + candidate.getLineNumber() + " susp "+ candidate.getSuspiciousValue());

		SpoonLocationPointerLauncher muSpoonLaucher = new SpoonLocationPointerLauncher(FactoryImpl.getLauchingFactory());
		List<CtElement> susp = muSpoonLaucher.run(ctclass, candidate.getLineNumber());

		return susp;
	}

	public CtClass getCtClassCloned(SuspiciousCode candidate) {
		String className = candidate.getClassName();
		CtSimpleType ct = mutatorSupporter.getFactory().Type().get(className);
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

	public GenSuspicious cloneGen(GenSuspicious existingGen, CtElement modified) {
		SuspiciousCode suspicious = existingGen.getSuspicious();
		CtClass ctClass = existingGen.getCtClass();
		List<CtVariable> context = existingGen.getContextOfGen();
		GenSuspicious newGen = new GenSuspicious(suspicious, modified, ctClass, context);
		return newGen;

	}

	public Gen cloneGen(Gen existingGen, CtElement modified) {
		CtClass ctClass = existingGen.getCtClass();
		List<CtVariable> context = existingGen.getContextOfGen();
		Gen newGen = new Gen(modified, ctClass, context);
		return newGen;

	}
	 public <T> List<T> intersection(List<T> list1, List<T> list2) {
	        List<T> list = new ArrayList<T>();

	        for (T t : list1) {
	        	try{
	            if(list2.contains(t)) {
	                list.add(t);
	            }
	        }catch(Exception ex){
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
