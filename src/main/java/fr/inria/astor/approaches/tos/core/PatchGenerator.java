package fr.inria.astor.approaches.tos.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.LiteralPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VarLiPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VariablePlaceholder;
import fr.inria.astor.approaches.tos.entity.transf.InvocationTransformation;
import fr.inria.astor.approaches.tos.entity.transf.LiteralTransformation;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.approaches.tos.entity.transf.VarLiTransformation;
import fr.inria.astor.approaches.tos.entity.transf.VariableTransformation;
import fr.inria.astor.approaches.tos.ingredients.LiteralsSpace;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver.InvocationMatching;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtExecutableReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchGenerator {

	protected static Logger logger = Logger.getLogger(PatchGenerator.class.getName());

	LiteralsSpace literalspace = null;

	public PatchGenerator() {
		literalspace = null;
	}

	@SuppressWarnings("rawtypes")
	public List<Transformation> process(ModificationPoint modificationPoint, InvocationPlaceholder varplaceholder) {

		List<Transformation> transformed = new ArrayList<>();
		InvocationMatching matchingVar = InvocationResolver.mapImplicitInvocation(modificationPoint.getCtClass(),
				varplaceholder.getInvocation());

		if (!matchingVar.isCorrect()) {
			logger.debug("Incorrect: we cannot put that ingredient there.");
			return transformed;
		} else {
			//
			if (matchingVar.TARGET_IS_VARIABLE.equals(matchingVar)) {
				Collection<CtExecutableReference<?>> allExecutables = varplaceholder.getTarget().getAllExecutables();
				for (CtExecutableReference executableTarget : allExecutables) {

					createTransformations(varplaceholder, transformed, executableTarget);
				}
			} else if (InvocationMatching.TARGET_SAME_TYPE.equals(matchingVar)
					|| InvocationMatching.SAME_SIGNATURE_FROM_DIFF_TYPE.equals(matchingVar)) {
				CtAbstractInvocation inv = varplaceholder.getInvocation();
				CtExecutableReference executableTarget = inv.getExecutable();
				createTransformations(varplaceholder, transformed, executableTarget);
			}
		}

		return transformed;

	}

	private void createTransformations(InvocationPlaceholder varplaceholder, List<Transformation> transformed,
			CtExecutableReference executableTarget) {
		if (executableTarget.getType().equals(varplaceholder.getType()) && varplaceholder.getInvocation()
				.getExecutable().getParameters().equals(executableTarget.getParameters())) {

			InvocationTransformation it = new InvocationTransformation(varplaceholder, executableTarget);

			transformed.add(it);
		}
	}

	@SuppressWarnings("rawtypes")
	public List<Transformation> process(ModificationPoint modificationPoint, VariablePlaceholder varplaceholder) {

		List<Transformation> transformation = new ArrayList<>();

		// Vars in scope at the modification point
		List<Transformation> transformationVariables = replaceByVars(varplaceholder, modificationPoint);
		transformation.addAll(transformationVariables);

		return transformation;
	}

	@SuppressWarnings("rawtypes")
	private List<Transformation> replaceByVars(VariablePlaceholder varplaceholder,
			ModificationPoint modificationPoint) {
		List<CtVariable> variablesInScope = modificationPoint.getContextOfModificationPoint();

		List<Transformation> transformation = new ArrayList<>();
		// Check Those vars not transformed must exist in context
		List<CtVariableAccess> concreteVars = varplaceholder.getVariablesNotModified();
		List<CtVariableAccess> outOfContext = VariableResolver.retriveVariablesOutOfContext(variablesInScope,
				concreteVars);
		if (outOfContext != null && !outOfContext.isEmpty()) {
			logger.debug("Concrete vars could not be mapped  " + outOfContext + "\nin context: " + variablesInScope);
			return transformation;

		}

		// Once we mapped all concrete variables (i.e., not transformed), and we
		// are sure they exist in
		// context.

		// Now we map placeholders with vars in scope:
		MapList<String, CtVariableAccess> placeholders = varplaceholder.getPalceholders();

		List<CtVariableAccess> placeholdersVariables = new ArrayList<>();
		for (List<CtVariableAccess> pvs : placeholders.values()) {
			placeholdersVariables.addAll(pvs);
		}

		logger.debug("Placeholder variables to map: " + placeholdersVariables);
		VarMapping mapping = VariableResolver.mapVariablesFromContext(variablesInScope, placeholdersVariables);

		// if we map all placeholder variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				logger.debug("Something is wrong: Any placeholder var was mapped ");

			} else {

				List<VarCombinationForIngredient> allCombinations = findAllVarMappingCombinationUsingRandom(
						mapping.getMappedVariables());

				if (allCombinations.size() > 0) {

					for (VarCombinationForIngredient varCombinationForIngredient : allCombinations) {
						transformation.add(new VariableTransformation(varplaceholder, placeholders,
								varCombinationForIngredient, mapping));
					}
				}
			}
		} else {

			// Placeholders without mapping: we discart it.
			logger.debug(
					String.format("Placeholders without mapping (%d/%d): %s ", mapping.getNotMappedVariables().size(),
							placeholdersVariables.size(), mapping.getNotMappedVariables().toString()));
			String varContext = "";
			for (CtVariable context : variablesInScope) {
				varContext += context.getSimpleName() + " " + context.getType().getQualifiedName() + ", ";
			}
			logger.debug("Context: " + varContext);
			for (CtVariableAccess ingredient : mapping.getNotMappedVariables()) {
				logger.debug("---out_of_context: " + ingredient.getVariable().getSimpleName() + ": "
						+ ingredient.getVariable().getType().getQualifiedName());
			}
		}
		return transformation;
	}

	@SuppressWarnings("rawtypes")
	public List<VarCombinationForIngredient> findAllVarMappingCombinationUsingRandom(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars) {

		List<VarCombinationForIngredient> allCom = new ArrayList<>();

		List<Map<String, CtVariable>> allWithoutOrder = VariableResolver.findAllVarMappingCombination(mappedVars, null);

		for (Map<String, CtVariable> varMapping : allWithoutOrder) {
			try {
				VarCombinationForIngredient varCombinationWrapper = new VarCombinationForIngredient(varMapping);
				// In random mode, all same probabilities
				varCombinationWrapper.setProbality((double) 1 / (double) allWithoutOrder.size());
				allCom.add(varCombinationWrapper);
			} catch (Exception e) {
				logger.error("Error for obtaining a string representation of combination with " + varMapping.size()
						+ " variables");
			}
		}
		Collections.shuffle(allCom, RandomManager.getRandom());

		logger.debug("Number combination RANDOMLY sorted : " + allCom.size() + " over " + allWithoutOrder.size());

		return allCom;

	}

	public LiteralsSpace getSpace(ProgramVariant pv) {

		String scope = ConfigurationProperties.properties.getProperty("scope");
		IngredientPoolScope ingScope = IngredientPoolScope.valueOf(scope.toUpperCase());
		if (literalspace == null) {
			try {
				logger.debug("Initializing literal space: scope " + ingScope);
				literalspace = new LiteralsSpace(ingScope);
				literalspace.defineSpace(pv);

			} catch (JSAPException e) {
				e.printStackTrace();
				logger.error(e);
			}
		}
		return literalspace;
	}

	public List<Transformation> process(ModificationPoint modificationPoint, LiteralPlaceholder literalPlaceholder) {

		List<Transformation> transformation = new ArrayList<>();

		///
		List<Ingredient> ingredients = getSpace(modificationPoint.getProgramVariant())
				.getIngredients(modificationPoint.getCodeElement());
		logger.debug("Ingredients lit (" + ingredients.size() + ") " + ingredients);
		// logger.debug("Placeholder vars "+
		// varplaceholder.getPalceholders().keySet().size());

		for (Ingredient ctCodeElement : ingredients) {
			CtLiteral literal4Space = (CtLiteral) ctCodeElement.getCode();
			if (literal4Space.getType().isSubtypeOf(literalPlaceholder.getAffected().getType())) {
				Transformation t = new LiteralTransformation(literalPlaceholder, literalPlaceholder.getAffected(),
						literal4Space.getValue());
				transformation.add(t);
			}
		}

		return transformation;
	}

	public List<Transformation> process(ModificationPoint modificationPoint, VarLiPlaceholder varLiPlaceholder) {

		List<Transformation> transformation = new ArrayList<>();

		List<Ingredient> ingredients = getSpace(modificationPoint.getProgramVariant())
				.getIngredients(modificationPoint.getCodeElement());
		logger.debug("Ingredients lit (" + ingredients.size() + ") " + ingredients);

		for (Ingredient ctCodeElement : ingredients) {
			CtLiteral literal4Space = (CtLiteral) ctCodeElement.getCode();
			if (literal4Space.getType().isSubtypeOf(varLiPlaceholder.getAffectedVariable().getType())) {
				Transformation t = new VarLiTransformation(varLiPlaceholder, varLiPlaceholder.getAffectedVariable(),
						literal4Space.clone());
				transformation.add(t);
			}
		}

		return transformation;
	}

}
