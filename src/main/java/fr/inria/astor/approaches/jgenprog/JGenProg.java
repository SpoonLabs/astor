package fr.inria.astor.approaches.jgenprog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.BlockReificationScanner;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

/**
 * Extension of Evolutionary loop with GenProgOperations
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JGenProg extends AstorCoreEngine {

	IngredientSearchStrategy ingredientSearchStrategy;
	
	public JGenProg(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}


	public void createInitialPopulation() throws Exception {
		if (ConfigurationProperties.getPropertyBool("skipfaultlocalization")) {
			// We dont use FL, so at this point the do not have suspicious
			this.initPopulation(new ArrayList<SuspiciousCode>());
		} else {
			List<SuspiciousCode> suspicious = projectFacade.calculateSuspicious(getFaultLocalization());
			this.initPopulation(suspicious);
		}
	}

	
	/**
	 * By default, it initializes the spoon model. It should not be created
	 * before. Otherwise, an exception occurs.
	 * 
	 * @param suspicious
	 * @throws Exception
	 */
	public void initPopulation(List<SuspiciousCode> suspicious) throws Exception {

		if (!MutationSupporter.getFactory().Type().getAll().isEmpty()) {
			Factory fcurrent = MutationSupporter.getFactory();
			log.debug("The Spoon Model was already built.");
			Factory fnew = MutationSupporter.cleanFactory();
			log.debug("New factory created? " + !fnew.equals(fcurrent));
		}
		initModel();

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

		if (this.ingredientSearchStrategy != null) {
			this.ingredientSearchStrategy.getIngredientSpace().defineSpace(originalVariant);
		}

		 setFitnessOfPopulation();
	}
	
	protected void setFitnessOfPopulation(){
		log.debug("Calculating fitness for original program variant.");
		//temporal workaround for avoid changing the interface
		String original = ConfigurationProperties.getProperty("forceExecuteRegression");
		ConfigurationProperties.setProperty("forceExecuteRegression", Boolean.TRUE.toString());
		
		//Initial validation and fitness 
		boolean validInstance = validateInstance(originalVariant);
		if (validInstance) {
			throw new IllegalStateException("The application under repair has not failling test cases");
		}
		
		double fitness = this.fitnessFunction.calculateFitnessValue(originalVariant);
		originalVariant.setFitness(fitness);
		
	
		log.debug("The original fitness is : "+fitness);
		for (ProgramVariant initvariant : variants) {
			initvariant.setFitness(fitness);
		}
		ConfigurationProperties.setProperty("forceExecuteRegression", original);//WA.
		
	} 



	private void initModel() throws Exception {
		String codeLocation = projectFacade.getInDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		String classpath = projectFacade.getProperties().getDependenciesString();
		String[] cpArray = classpath.split(File.pathSeparator);

		try {
			mutatorSupporter.buildModel(codeLocation, cpArray);
			log.debug("Spoon Model built from location: " + codeLocation);
		} catch (Exception e) {
			log.error("Problem compiling the model with compliance level "
					+ ConfigurationProperties.getPropertyInt("javacompliancelevel"));
			log.error(e.getMessage());
			mutatorSupporter.getFactory().getEnvironment()
					.setComplianceLevel(ConfigurationProperties.getPropertyInt("alternativecompliancelevel"));
			mutatorSupporter.buildModel(codeLocation, cpArray);
		}
		
		/////ONCE ASTOR HAS BUILT THE MODEL,
		/////We apply different processes and manipulation over it.
		
		//We process the model to add blocks as parent of statement which are not contained in a block
		BlockReificationScanner visitor = new BlockReificationScanner();
		for (CtType c : mutatorSupporter.getFactory().Type().getAll()) {
			c.accept(visitor);
		}
		//We divide the CtClasses from the model in two set:
		//One that represents test cases, the other 'normal' classes (not test cases)
		List<String> testcases = projectFacade.getProperties().getRegressionTestCases();
		List<CtType<?>> types = mutatorSupporter.getFactory().Class().getAll();
		
		for (CtType<?> ctType : types) {

			if (!(ctType instanceof CtClass)) {
				continue;
			}
			if(testcases.contains(ctType.getQualifiedName())){
				mutatorSupporter.getTestClasses().add((CtClass) ctType);
			}else{
				mutatorSupporter.getClasses().add((CtClass) ctType);
			}
		}
		//We do not refine the test case list using the spoon model, due we do not create the model for test any more.
		FinderTestCases.updateRegressionTestCases(projectFacade);
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
	 * @param variant
	 *            variant to modify the modification point information
	 * @param operationofGen
	 *            operator to apply in the variant.
	 */
	@Override
	protected void updateVariantGenList(ProgramVariant variant, OperatorInstance operation) {
		operation.getOperationApplied().updateProgramVariant(operation, variant);
	}

	/**
	 * Create a modification point Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */
	@Override
	protected OperatorInstance createOperatorInstanceForPoint(ModificationPoint modificationPoint) throws IllegalAccessException {
		SuspiciousModificationPoint suspModificationPoint = (SuspiciousModificationPoint) modificationPoint;

		
		AstorOperator operationType =  operatorSelectionStrategy.getNextOperator(suspModificationPoint);

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
		
		if(operationType.needIngredient()){
			Ingredient fix = null;
			fix = this.ingredientSearchStrategy.getFixIngredient(modificationPoint, operationType);
			if(fix == null){
				log.debug("Any ingredient for this point, we discard it");
				return null;
			}else{
				operation.setModified(fix.getCode());
				operation.setIngredientScope(fix.getScope());
			}
		}

		return operation;
	}

	
	@Override
	protected void undoOperationToSpoonElement(OperatorInstance operation) {
		operation.undoModification();

	}

	/**
	 * 
	 */
	@Override
	protected void applyPreviousMutationOperationToSpoonElement(OperatorInstance operation)
			throws IllegalAccessException {
		this.applyNewMutationOperationToSpoonElement(operation);

	}


	/**
	 * 
	 */
	@Override
	protected void applyNewMutationOperationToSpoonElement(OperatorInstance operationInstance)
			throws IllegalAccessException {

		operationInstance.applyModification();
	
	}

	public IngredientSearchStrategy getIngredientStrategy() {
		return ingredientSearchStrategy;
	}

	public void setIngredientStrategy(IngredientSearchStrategy ingredientStrategy) {
		this.ingredientSearchStrategy = ingredientStrategy;
	}

	@Override
	public void prepareNextGeneration(List<ProgramVariant> temporalInstances, int generation) {
		
		super.prepareNextGeneration(temporalInstances, generation);
		if(false || ConfigurationProperties.getPropertyBool("applyCrossover")){
			int numberVariants = this.variants.size();
			if(numberVariants <= 1){
				log.debug("CO|Not Enough variants to apply Crossover");
				return;
			}
			
			//We randomly choose the two variants to crossover
			ProgramVariant v1 = this.variants.get(RandomManager.nextInt(numberVariants));
			ProgramVariant v2 = this.variants.get(RandomManager.nextInt(numberVariants));
			//Same instance
			if(v1 == v2){
				log.debug("CO|randomless chosen the same variant to apply crossover");
				return;
			}
			
			if(v1.getOperations().isEmpty() || v2.getOperations().isEmpty()){
				log.debug("CO|Not Enough ops to apply Crossover");
				return;
			}
			//we randomly select the generations to apply
			int rgen1index = RandomManager.nextInt(v1.getOperations().keySet().size()) + 1;
			int rgen2index = RandomManager.nextInt(v2.getOperations().keySet().size()) + 1;
			
			List<OperatorInstance> ops1 = v1.getOperations((int) v1.getOperations().keySet().toArray()[rgen1index]);
			List<OperatorInstance> ops2 = v2.getOperations((int) v2.getOperations().keySet().toArray()[rgen2index]);
	
			OperatorInstance opinst1 = ops1.remove((int)RandomManager.nextInt(ops1.size()));
			OperatorInstance opinst2 = ops2.remove((int)RandomManager.nextInt(ops2.size()));
			
			if(opinst1 == null || opinst2 == null){
				log.debug("CO|We could not retrieve a operator");
				return;
			}
			
			//The generation of both new operators is the Last one.
			//In the first variant we put the operator taken from the 2 one.
			v1.putModificationInstance(generation, opinst2);
			//In the second variant we put the operator taken from the 1 one.
			v2.putModificationInstance(generation, opinst1);
			//
		}
	}

	@Override
	public void showResults() {
		super.showResults();
		log.info("\nsuccessful_ing_attempts ("+this.currentStat.ingAttemptsSuccessfulPatches.size()+ "): "+this.currentStat.ingAttemptsSuccessfulPatches);
		log.info("\nfailing_ing_attempts ("+this.currentStat.ingAttemptsFailingPatches.size()+ "): "+this.currentStat.ingAttemptsFailingPatches);
		
	}


}
