package fr.inria.astor.core.loop.evolutionary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.entities.taxonomy.Operator;
import fr.inria.astor.core.faultlocalization.GZoltarFaultLocalization;
import fr.inria.astor.core.faultlocalization.IFaultLocalization;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.IngredientSpaceStrategy;
import fr.inria.astor.core.loop.evolutionary.transformators.BlockReificationScanner;
import fr.inria.astor.core.loop.evolutionary.transformators.CtExpressionTransformator;
import fr.inria.astor.core.loop.evolutionary.transformators.CtStatementTransformator;
import fr.inria.astor.core.loop.evolutionary.transformators.ModelTransformator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.StatSpaceSize.INGREDIENT_STATUS;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
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

	Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();

	public JGenProg(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		//By default, we use GZoltar fault localization
		this.faultLocalization = new GZoltarFaultLocalization();
	}

	public JGenProg(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade, IFaultLocalization faultLocalization) throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.faultLocalization = faultLocalization;
	}
	
	public void createInitialPopulation() throws Exception {
		if (ConfigurationProperties.getPropertyBool("skipfaultlocalization")) {
			// We dont use FL, so at this point the do not have suspicious
			this.initPopulation(new ArrayList<SuspiciousCode>());
		} else {
			List<SuspiciousCode> suspicious = projectFacade.calculateSuspicious(faultLocalization);
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

		if (getFixSpace() != null) {
			List<?> classesForIngredients = retrieveClassesForIngredients();
			getFixSpace().defineSpace(classesForIngredients);
		}

		boolean validInstance = validateInstance(originalVariant);
		if (validInstance) {
			throw new IllegalStateException("The application under repair has not failling test cases");
		}

		for (ProgramVariant initvariant : variants) {
			initvariant.setFitness(originalVariant.getFitness());
		}

	}

	protected List<CtType<?>> retrieveClassesForIngredients() {
		if (getFixSpace().strategy().equals(IngredientSpaceStrategy.LOCAL)
				|| getFixSpace().strategy().equals(IngredientSpaceStrategy.PACKAGE))
			return originalVariant.getAffectedClasses();

		if (getFixSpace().strategy().equals(IngredientSpaceStrategy.GLOBAL))
			return this.mutatorSupporter.getFactory().Type().getAll();

		return null;
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
		//Finally, we update the list of test cases (we apply a pre-process using info from Spoon Model)
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
			throw new IllegalStateException("Variant without gens. It must have at least one.");
		}
	}

	/**
	 * This method updates gens of a variant according to a created
	 * GenOperationInstance
	 * 
	 * @param variant
	 *            variant to modify the gen information
	 * @param operationofGen
	 *            operator to apply in the variant.
	 */
	@Override
	protected void updateVariantGenList(ProgramVariant variant, ModificationInstance operation) {
		List<ModificationPoint> gens = variant.getModificationPoints();
		GenProgMutationOperation type = (GenProgMutationOperation) operation.getOperationApplied();
		if (type.equals(GenProgMutationOperation.DELETE) || type.equals(GenProgMutationOperation.REPLACE)) {
			boolean removed = gens.remove(operation.getModificationPoint());
			if (type.equals(GenProgMutationOperation.DELETE))
				log.debug("---" + operation + " removed gen? " + removed);
		}
		if (!type.equals(GenProgMutationOperation.DELETE)) {
			ModificationPoint existingGen = operation.getModificationPoint();
			ModificationPoint newGen = null;
			if (existingGen instanceof SuspiciousModificationPoint)
				newGen = variantFactory.cloneGen((SuspiciousModificationPoint) existingGen, operation.getModified());
			else
				newGen = variantFactory.cloneGen(existingGen, operation.getModified());

			log.debug("---" + newGen);
			log.debug("---" + operation);
			gens.add(newGen);
		}

	}

	/**
	 * Create a Gen Mutation for a given CtElement
	 * 
	 * @param ctElementPointed
	 * @param className
	 * @param suspValue
	 * @return
	 * @throws IllegalAccessException
	 */
	@Override
	protected ModificationInstance createModificationForPoint(ModificationPoint modificationPoint) throws IllegalAccessException {
		SuspiciousModificationPoint genSusp = (SuspiciousModificationPoint) modificationPoint;

		GenProgMutationOperation operationType = (GenProgMutationOperation) repairActionSpace
				.getNextOperator(genSusp.getSuspicious().getSuspiciousValue());

		if (operationType == null) {
			log.debug("Operation Null");
			return null;
		}

		CtElement targetStmt = genSusp.getCodeElement();

		ModificationInstance operation = new ModificationInstance();
		operation.setOriginal(targetStmt);
		operation.setOperationApplied(operationType);
		operation.setModificationPoint(genSusp);

		setParentToGenOperator(operation, genSusp);

		Ingredient fix = null;
		if (operationType.equals(GenProgMutationOperation.INSERT_AFTER)
				|| operationType.equals(GenProgMutationOperation.INSERT_BEFORE)) {

			fix = this.getFixIngredient(modificationPoint, targetStmt, operationType);

			if (operationType.equals(GenProgMutationOperation.INSERT_AFTER)) {
				operation.setLocationInParent(operation.getLocationInParent() + 1);
			}
		}

		if (operationType.equals(GenProgMutationOperation.REPLACE)) {
			fix = this.getFixIngredient(modificationPoint, targetStmt, modificationPoint.getCodeElement().getClass().getSimpleName(),
					operationType);
		}

		if (!operationType.equals(GenProgMutationOperation.DELETE) && fix == null) {
			log.debug("***fix ingredient null");
			return null;
		}

		if (fix != null) {
			// TODO: Next we associate Operation with Ingredients
			operation.setModified(fix.getCode());
			operation.setIngredientScope(fix.getScope());
		}

		return operation;
	}

	protected void setParentToGenOperator(ModificationInstance operation, SuspiciousModificationPoint genSusp) {
		CtElement targetStmt = genSusp.getCodeElement();
		CtElement cparent = targetStmt.getParent();
		if ((cparent != null && (cparent instanceof CtBlock))) {
			CtBlock parentBlock = (CtBlock) cparent;
			operation.setParentBlock(parentBlock);
			operation.setLocationInParent(
					locationInParent(parentBlock, genSusp.getSuspicious().getLineNumber(), targetStmt));

		} else {
			log.error("Parent diferent to block");
		}
	}

	/**
	 * Return the position of the element in the block. It searches the same
	 * object instance
	 * 
	 * @param parentBlock
	 * @param line
	 * @param element
	 * @return
	 */
	private int locationInParent(CtBlock parentBlock, int line, CtElement element) {
		int pos = 0;
		for (CtStatement s : parentBlock.getStatements()) {
			// if(s.getPosition().getLine() == line && s.equals(element))
			if (s == element)// the same object
				return pos;
			pos++;
		}

		log.error("Error: parent not found");
		return -1;

	}

	protected Ingredient getFixIngredient(ModificationPoint gen, CtElement targetStmt, GenProgMutationOperation operationType) {
		return this.getFixIngredient(gen, targetStmt, null, operationType);
	}

	/**
	 * Return fix ingredient considering cache.
	 * 
	 * @param gen
	 * @param targetStmt
	 * @param operationType
	 * @param elementsFromFixSpace
	 * @return
	 */
	protected Ingredient getFixIngredient(ModificationPoint gen, CtElement targetStmt, String type,
			GenProgMutationOperation operationType) {

		int attempts = 0;

		boolean continueSearching = true;

		int elementsFromFixSpace = 0;
		// Here, search in the space an element witout type preference
		List<?> ingredients = null;
		if (type == null) {
			ingredients = this.fixspace.getFixSpace(gen.getCodeElement());
		} else {// We search for ingredients of one particular type
			ingredients = this.fixspace.getFixSpace(gen.getCodeElement(), type);
		}
		elementsFromFixSpace = (ingredients == null) ? 0 : ingredients.size();

		while (continueSearching && attempts < elementsFromFixSpace) {
			CtElement fix = null;
			if (type == null) {
				fix = this.fixspace.getElementFromSpace(gen.getCodeElement());
			} else {
				fix = this.fixspace.getElementFromSpace(gen.getCodeElement(), type);
			}
			if (fix == null) {
				return null;
			}

			attempts++;
			INGREDIENT_STATUS fixStat = null;

			boolean alreadyApplied = alreadyApplied(gen, fix, operationType), ccompatibleNameTypes = false;

			if (!alreadyApplied && !fix.getSignature().equals(targetStmt.getSignature())) {

				ccompatibleNameTypes = VariableResolver.fitInPlace(gen.getContextOfModificationPoint(), fix);
				continueSearching = !ccompatibleNameTypes;
				fixStat = (ccompatibleNameTypes) ? INGREDIENT_STATUS.compiles : INGREDIENT_STATUS.notcompiles;
			} else {
				fixStat = INGREDIENT_STATUS.alreadyanalyzed;
			}

			IngredientSpaceStrategy scope = (fix != null)
					? determineIngredientScope(gen.getCodeElement(), fix, ingredients) : IngredientSpaceStrategy.GLOBAL;

			if (!continueSearching) {
				return new Ingredient(fix, scope);
			}

		}

		log.debug("--- no mutation left to apply in element " + targetStmt.getSignature());
		return null;

	}

	@Override
	protected void undoOperationToSpoonElement(ModificationInstance operation) {
		List<AbstractFixSpaceProcessor<?>> processors = this.getVariantFactory().getProcessors();
		for (AbstractFixSpaceProcessor<?> processor : processors) {
			ModelTransformator mt = processor.getTransformator();
			if (mt.canTransform(operation)) {
				try {
					mt.revert(operation);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// After the operator instance is processed by one
				// transformator, we break.
				break;
			}
		}

	}

	/**
	 * 
	 */
	@Override
	protected void applyPreviousMutationOperationToSpoonElement(ModificationInstance operation)
			throws IllegalAccessException {
		this.applyNewMutationOperationToSpoonElement(operation);

	}

	/**
	 * Apply a given Mutation to the node referenced by the operation
	 * 
	 * @param operation
	 * @throws IllegalAccessException
	 */
	CtExpressionTransformator expTransform = new CtExpressionTransformator();
	CtStatementTransformator stTransformator = new CtStatementTransformator();

	/**
	 * 
	 */
	@Override
	protected void applyNewMutationOperationToSpoonElement(ModificationInstance operation)
			throws IllegalAccessException {

		List<AbstractFixSpaceProcessor<?>> processors = this.getVariantFactory().getProcessors();
		for (AbstractFixSpaceProcessor<?> processor : processors) {
			ModelTransformator mt = processor.getTransformator();
			if (mt.canTransform(operation)) {
				try {
					mt.transform(operation);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// After the operator instance is processed by one
				// transformator, we break.
				break;

			}
		}

	}

	/**
	 * Check if the fix were applied in the location for a program instance
	 * 
	 * @param id
	 *            program instance id.
	 * @param fix
	 * @param location
	 * @return
	 */
	protected boolean alreadyApplied(ModificationPoint gen, CtElement fixElement, Operator operator) {
		// we add the instance identifier to the patch.
		String lockey = gen.getCodeElement() + "-" + operator.toString();
		String fix = "";
		try {
			fix = fixElement.toString();
		} catch (Exception e) {
			log.error("to string fails");
		}
		List<String> prev = appliedCache.get(lockey);
		// The element does not have any mutation applied
		if (prev == null) {
			prev = new ArrayList<String>();
			prev.add(fix);
			appliedCache.put(lockey, prev);
			return false;
		} else {
			// The element has mutation applied
			if (prev.contains(fix))
				return true;
			else {
				prev.add(fix);
				return false;
			}
		}
	}

	protected IngredientSpaceStrategy determineIngredientScope(CtElement ingredient, CtElement fix,
			List<?> ingredients) {

		IngredientSpaceStrategy orig = determineIngredientScope(ingredient, fix);

		String fixStr = fix.toString();
		for (Object ing : ingredients) {
			try {
				ing.toString();
			} catch (Exception e) {
				log.error(e.toString());
				continue;
			}
			if (ing.toString().equals(fixStr)) {
				IngredientSpaceStrategy n = determineIngredientScope(ingredient, (CtElement) ing);
				if (n.ordinal() < orig.ordinal()) {
					orig = n;
					if (IngredientSpaceStrategy.values()[0].equals(orig))
						return orig;
				}

			}
		}
		return orig;
	}

	protected IngredientSpaceStrategy determineIngredientScope(CtElement ingredient, CtElement fix) {

		File ingp = ingredient.getPosition().getFile();
		File fixp = fix.getPosition().getFile();

		if (ingp.getAbsolutePath().equals(fixp.getAbsolutePath())) {
			return IngredientSpaceStrategy.LOCAL;
		}
		if (ingp.getParentFile().getAbsolutePath().equals(fixp.getParentFile().getAbsolutePath())) {
			return IngredientSpaceStrategy.PACKAGE;
		}
		return IngredientSpaceStrategy.GLOBAL;
	}

}
