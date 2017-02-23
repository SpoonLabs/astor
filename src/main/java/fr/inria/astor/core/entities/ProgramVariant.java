package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * A ProgramVariant is a variant of a program. The variant can change over time.
 * 
 * Each program variant contains a list of "Gen", which represents all locations (i.e., statements) that can be modified for that variant during the whole evolution. For single-point repair, only one of those gens is affected by a mutation operator.. In case of multipoint repair (which can be
 * activated in astor by a command line argument), different gens from a ProgramVariant can be modified by mutation operator. Note that 2 gens could be modified a) in the same generation, and/or b) in different generations (thus the variant changes over time). For instance, in generation X you
 * modified gen at position i, and in the generation X+1 you modify position j. ProgramVariant contains a Map “operations” that tracks the history, i.e. the operations done over gens in each generation.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class ProgramVariant {
	

	public static String DEFAULT_ORIGINAL_VARIANT = "default";

	/**
	 * Variand ID
	 */
	protected int id = 0;
	
	/**
	 * List of gens (statements that can be modified for finding a patch) of the program 
	 */
	protected List<ModificationPoint> modificationPoints = null;
	/**
	 * Reference to the loaded classes from the spoon model. The classes are shared by all variants, a
	 * s consequence, it does not have changes of any variant.
	 */
	protected Map<String, CtClass> loadClasses = new HashMap<String, CtClass>();
	
	/**
	 * operations applied to a Modification Point, organizated by generations
	 */
	protected Map<Integer,List<OperatorInstance>> operations  = null;
	/**
	 * Fitness value of the variant	
	 */
	protected double fitness = Double.MAX_VALUE;
	
	/**
	 * Parent Variant
	 */
	protected ProgramVariant parent = null;
	/**
	 * Id of the generation this variant born
	 */
	protected int generationSource = 0;

	
	protected CompilationResult compilationResult = null;

	protected boolean isSolution = false;	
	/**
	 * When we want to analyze one gen per generation, 
	 * we need to track the last gen analyzed
	 */
	protected int lastGenAnalyzed = 0;
	
	/**
	 * Date the variant were born
	 */
	protected Date bornDate = new Date(); 
	
	/**
	 * List that contains the classes affected by the variant, 
	 * with the corresponding changes that the variant proposes.
	 * Note that these classes are cloned from the share model, 
	 * and only belong to this variant. 
	 * That means the children of this variant do not refer to those instances.
	 */
	protected List<CtClass> modifiedClasses = new ArrayList<CtClass>();

	
	VariantValidationResult validationResult = null;
	
	public ProgramVariant(){
		modificationPoints = new ArrayList<ModificationPoint>();
		operations = new HashMap<Integer,List<OperatorInstance>>();
	}

	public ProgramVariant(int id) {
		this();
		this.id = id;
	}

	public List<ModificationPoint> getModificationPoints() {
		return modificationPoints;
	}

	public void setModificationPoints(List<ModificationPoint> modificationPoints) {
		this.modificationPoints = modificationPoints;
	}

	public double getFitness() {
		return fitness;
	}

	public void setFitness(double fitness) {
		this.fitness = fitness;
	}

	public Map<String, CtClass> getBuiltClasses() {
		return loadClasses;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	public void putModificationInstance(int generation, OperatorInstance op){
		List<OperatorInstance> modificationPoints = operations.get(generation);
		if(modificationPoints == null){
			modificationPoints = new ArrayList<OperatorInstance>();
			operations.put(generation, modificationPoints);
		}
		modificationPoints.add(op);
		
	}
	public Map<Integer, List<OperatorInstance>> getOperations() {
		return operations;
	}
	public List<OperatorInstance> getOperations(int generation) {
		return  operations.get(generation);
	}

	
	public ProgramVariant getParent() {
		return parent;
	}

	public void setParent(ProgramVariant parent) {
		this.parent = parent;
	}
	
	public int getGenerationSource() {
		return generationSource;
	}

	public void setGenerationSource(int generationSource) {
		this.generationSource = generationSource;
	}

	
	public CompilationResult getCompilation() {
		return compilationResult;
	}

	public void setCompilation(CompilationResult compilation) {
		this.compilationResult = compilation;
	}
	
	@Override
	public String toString(){
		return "[Variant id: "+this.id+(this.isSolution()?" (SOL) ":"") +", #gens: "+this.getModificationPoints().size()+ ", #ops: "+this.operations.values().size()+", parent:"+((this.parent==null)?"-":this.parent.id)+"]";
	}
	
	public String currentMutatorIdentifier() {
		return (id >= 0)? ( ConfigurationProperties.getProperty("pvariantfoldername") + id) : DEFAULT_ORIGINAL_VARIANT;
	}
	/**
	 * Return the classes affected by the variant. Note that those classes are shared between all variant, 
	 * so, they do not have applied the changes proposed by the variant after the variant is validated. 
	 * @return
	 */
	public List<CtType<?>> getAffectedClasses(){
		List<CtType<?>> r = new ArrayList<CtType<?>>();
		for (CtClass c:loadClasses.values()) {
			r.add(c);
		}
		return Collections.unmodifiableList(r);
	}
	
	public boolean isSolution() {
		return isSolution;
	}

	public void setIsSolution(boolean solution) {
		this.isSolution = solution;
	}
	
	public void resetOperations(){
		this.operations.clear();
	}

	public int getLastGenAnalyzed() {
		return lastGenAnalyzed;
	}

	public void setLastGenAnalyzed(int lastGenAnalyzed) {
		this.lastGenAnalyzed = lastGenAnalyzed;
	}

	public Date getBornDate() {
		return bornDate;
	}

	public void setBornDate(Date bornDate) {
		this.bornDate = bornDate;
	}

	public List<CtClass> getModifiedClasses() {
		return modifiedClasses;
	}

	public void setModifiedClasses(List<CtClass> resultedClasses) {
		this.modifiedClasses = resultedClasses;
	}

	public VariantValidationResult getValidationResult() {
		return validationResult;
	}

	public void setValidationResult(VariantValidationResult validationResult) {
		this.validationResult = validationResult;
	}
	
	public ModificationPoint getModificationPoint(CtElement element){
		for (Iterator iterator = modificationPoints.iterator(); iterator.hasNext();) {
			ModificationPoint modificationPoint = (ModificationPoint) iterator.next();
			if(element == modificationPoint.getCodeElement())
				return modificationPoint;
		}
		return null;
	}
	
	public void addModificationPoints(List<? extends ModificationPoint> points){
		for (ModificationPoint modificationPoint : points) {
			this.modificationPoints.add(modificationPoint);
			modificationPoint.setProgramVariant(this);
		}
	}
	public List<CtType<?>> computeAffectedClassesByOperators() {
		List<CtType<?>> typesToProcess = new ArrayList<>();
		for (List<OperatorInstance> modifofGeneration : this.getOperations().values()) {
			for (OperatorInstance modificationInstance : modifofGeneration) {
				typesToProcess.add(modificationInstance.getModificationPoint().getCtClass());
			}
		}
		return typesToProcess;
	}
}
