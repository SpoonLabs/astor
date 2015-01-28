package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spoon.reflect.declaration.CtClass;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
/**
 * Representation of a Program Variant (Genotype)
 * The program is represented by a list of Gens. 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ProgramVariant {
	

	public static String DEFAULT_ORIGINAL_VARIANT = "default";

	/**
	 * Variand ID
	 */
	protected int id = 0;
	
	/**
	 * List of gens (statements be able to modify) of the program 
	 */
	protected List<Gen> genList = null;
	/**
	 * Reference to the loaded classes from the spoon model
	 */
	protected Map<String, CtClass> loadClasses = new HashMap<String, CtClass>();
	
	/**
	 * operations applied to gen, organizated by generations
	 */
	protected Map<Integer,List<GenOperationInstance>> operations  = null;
	/**
	 * Fitness value of the variant	
	 */
	protected double fitness = 0;
	
	/**
	 * Parent Variant
	 */
	protected ProgramVariant parent = null;
	/**
	 * Number of the generation this variant born
	 */
	protected int generationSource = 0;

	
	protected CompilationResult compilationResult = null;


	public ProgramVariant(){
		genList = new ArrayList<Gen>();
		operations = new HashMap<Integer,List<GenOperationInstance>>();
	}

	public ProgramVariant(int id) {
		this();
		this.id = id;
	}

	public List<Gen> getGenList() {
		return genList;
	}

	public void setGenList(List<Gen> genList) {
		this.genList = genList;
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
	public void putGenOperation(int generation, GenOperationInstance op){
		List<GenOperationInstance> genList = operations.get(generation);
		if(genList == null){
			genList = new ArrayList<GenOperationInstance>();
			operations.put(generation, genList);
		}
		genList.add(op);
		
	}
	public Map<Integer, List<GenOperationInstance>> getOperations() {
		return operations;
	}
	public List<GenOperationInstance> getOperations(int generation) {
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
	
	public String toString(){
		return "[Variant id: "+this.id+", #gens: "+this.getGenList().size()+ ", #ops: "+this.operations.values().size()+", parent:"+((this.parent==null)?"-":this.parent.id)+"]";
	}
	
	public String currentMutatorIdentifier() {
		return (id >= 0)? ( "variant-" + id) : DEFAULT_ORIGINAL_VARIANT;
	}
	
}
