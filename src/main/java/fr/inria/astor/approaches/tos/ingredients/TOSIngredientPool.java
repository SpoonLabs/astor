package fr.inria.astor.approaches.tos.ingredients;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.entity.TOSCounter;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.approaches.tos.ingredients.processors.LiteralPlaceholderGenerator;
import fr.inria.astor.approaches.tos.ingredients.processors.PlaceholderGenerator;
import fr.inria.astor.approaches.tos.ingredients.processors.TOSInvocationGenerator;
import fr.inria.astor.approaches.tos.ingredients.processors.VarLiPlaceholderGenerator;
import fr.inria.astor.approaches.tos.ingredients.processors.VariablePlaceholderGenerator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.CacheList;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * Ingredient space (pool) which content are "TOSs"
 * 
 * @author Matias Martinez
 *
 */
public class TOSIngredientPool<T extends CtElement>
		extends IngredientPoolLocationType<CtElement, String, Ingredient, String, CtCodeElement> {

	// TODO:
	public IngredientPoolScope scope;
	/**
	 * Entity that stores the stats of TOS.
	 */
	protected TOSCounter tosCounter = null;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Generators used for creating the TOS
	 */
	protected List<PlaceholderGenerator<T>> tosGenerators = new ArrayList<>();

	public TOSIngredientPool(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);

		if (!ConfigurationProperties.getPropertyBool("excludevariableplaceholder"))
			tosGenerators.add(new VariablePlaceholderGenerator<T>());
		if (!ConfigurationProperties.getPropertyBool("excludeinvocationplaceholder"))
			tosGenerators.add(new TOSInvocationGenerator<T>());
		if (!ConfigurationProperties.getPropertyBool("excludeliteralplaceholder"))
			tosGenerators.add(new LiteralPlaceholderGenerator<T>());
		if (!ConfigurationProperties.getPropertyBool("excludevarliteralplaceholder"))
			tosGenerators.add(new VarLiPlaceholderGenerator<T>());

		log.info("Generators loaded " + tosGenerators);
	}

	@Override
	public void defineSpace(ProgramVariant variant) {

		tosCounter = new TOSCounter();

		List<CtType<?>> affected = obtainClassesFromScope(variant);
		log.debug("Creating Expression Ingredient space: affected " + affected.size());
		for (CtType<?> classToProcess : affected) {
			log.debug("Processing class " + classToProcess.getQualifiedName());
			List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(classToProcess);
			TargetElementProcessor.mustClone = true;

			for (Object originalIngredient : ingredients) {

				// log.debug(String.format("Ingredient to process: %s",
				// originalIngredient.toString()));

				T originalIngredientStatement = (T) originalIngredient;

				String keyLocation = mapKey(originalIngredientStatement);
				MutationSupporter.getEnvironment().setNoClasspath(true);
				List<TOSEntity> xTemplates = createAllTOS(originalIngredientStatement);

				List<Ingredient> ingredientPoolForLocation = this.retrieveIngredients(keyLocation);
				for (TOSEntity templateElement : xTemplates) {
					// log.debug("\nGenerating element " +
					// templateElement.getDerivedFrom() + " vars "
					// + templateElement.getPlaceholders());

					CtElement generatedTos = templateElement.generateCodeofTOS();

					if (!ingredientPoolForLocation.contains(templateElement)) {
						// log.debug("Adding tos " +
						// templateElement.getCode() + " to" +
						// ingredientPoolForLocation);
						ingredientPoolForLocation.add(templateElement);
					} else {
						// log.debug("Existing template");
					}
					// TODO: remove comment
					tosCounter.saveStatisticsOfTos(templateElement, originalIngredientStatement);
				}

			}

		}
		// once we finish creating the space, we split ingredients by type
		this.recreateTypesStructures();
	}

	public List<TOSEntity> createAllTOS(T ingredientSource) {
		boolean combinated = ConfigurationProperties.getPropertyBool("toscombinated");
		if (combinated) {
			return createAllTOSCombined(ingredientSource);
		} else
			return createAllTOSNotCombined(ingredientSource);
	}

	public List<TOSEntity> createAllTOSNotCombined(T ingredientStatement) {
		List<TOSEntity> xGeneratedTos = new ArrayList<>();
		for (PlaceholderGenerator tosGenerator : tosGenerators) {
			List<? extends Placeholder> xpalceholders = tosGenerator.createTOS(ingredientStatement);
			for (Placeholder placeholder : xpalceholders) {

				TOSEntity tosToAdd = new TOSEntity();
				tosToAdd.setDerivedFrom(ingredientStatement);
				tosToAdd.getPlaceholders().add(placeholder);

				if (!tosToAdd.getPlaceholders().isEmpty()) {
					xGeneratedTos.add(tosToAdd);
				}
			}

		}

		return xGeneratedTos;

	}

	public List<TOSEntity> createAllTOSCombined(T ingredientStatement) {
		// all generated
		List<TOSEntity> xGeneratedTos = new ArrayList<>();
		// for each generator
		for (PlaceholderGenerator tosGenerator : tosGenerators) {

			List<TOSEntity> generatedTosFromGenerator = new ArrayList<>();
			log.debug("-->" + tosGenerator.getClass().getName());
			List<? extends Placeholder> xpalceholders = tosGenerator.createTOS(ingredientStatement);
			for (Placeholder placeholder : xpalceholders) {

				// we add a tos for the recently created placeholder
				TOSEntity tosToAdd = new TOSEntity();
				tosToAdd.setDerivedFrom(ingredientStatement);
				tosToAdd.getPlaceholders().add(placeholder);
				// Avoid adding empty placeholders
				if (!tosToAdd.getPlaceholders().isEmpty()) {
					generatedTosFromGenerator.add(tosToAdd);
				}
				// for each of the previously created tos (by other generators)
				for (TOSEntity tosEntity : xGeneratedTos) {

					List<?> intersection = tosEntity.getAffectedElements().stream()
							.filter(f -> placeholder.getAffectedElements().contains(f)).collect(Collectors.toList());

					log.debug("\n\nAnalyzing:---------\n" + tosEntity.getCode() + "\nConflicts? "
							+ !intersection.isEmpty() + " " + intersection + "\n----existing: "
							+ tosEntity.getPlaceholders() + "\n----newph: " + placeholder);
					// If not conflict with the placeholders
					if (intersection.isEmpty()) {
						TOSEntity nEnt = new TOSEntity();
						nEnt.setDerivedFrom(ingredientStatement);
						nEnt.getPlaceholders().addAll(tosEntity.getPlaceholders());
						nEnt.getPlaceholders().add(placeholder);
						generatedTosFromGenerator.add(nEnt);
						// System.out.println("-!-!-->Adding new tos
						// "+nEnt.getChacheCodeString() + " "+nEnt);
					}
				}

			}
			if (generatedTosFromGenerator.size() > 0) {
				xGeneratedTos.addAll(generatedTosFromGenerator);
				int i = 0;
				// log.debug("\n------\nIntermediate tos set " + xGeneratedTos);
				for (TOSEntity tosEntity : xGeneratedTos) {
					// System.out.println("--> "+(i++)+ " "+tosEntity);
				}
				log.debug("\n--**********----\n");
			}
		}

		return xGeneratedTos;
	}

	public List<Ingredient> retrieveIngredients(String key) {

		List<Ingredient> ingredientsKey = getFixSpace().get(key);
		if (!getFixSpace().containsKey(key)) {
			ingredientsKey = new CacheList<>();
			getFixSpace().put(key, ingredientsKey);
		}
		return ingredientsKey;
	}

	protected List<CtType<?>> obtainClassesFromScope(ProgramVariant variant) {

		if (IngredientPoolScope.PACKAGE.equals(scope)) {
			List<CtType<?>> affected = variant.getAffectedClasses();
			List<CtType<?>> types = new ArrayList<>();
			List<CtPackage> packageAnalyzed = new ArrayList<>();
			for (CtType<?> ing : affected) {

				CtPackage p = ing.getParent(CtPackage.class);
				if (!packageAnalyzed.contains(p)) {
					packageAnalyzed.add(p);
					for (CtType<?> type : p.getTypes()) {
						types.add(type);
					}
				}
			}
			return types;
		}
		if (IngredientPoolScope.LOCAL.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientPoolScope.GLOBAL.equals(scope)) {
			return MutationSupporter.getFactory().Type().getAll();
		}
		return null;
	}

	@Override
	public IngredientPoolScope spaceScope() {
		return this.scope;
	}

	@Override
	public String calculateLocation(CtElement elementToModify) {

		if (IngredientPoolScope.PACKAGE.equals(scope)) {
			return elementToModify.getParent(CtPackage.class).getQualifiedName();
		} else if (IngredientPoolScope.LOCAL.equals(scope)) {
			return elementToModify.getParent(CtType.class).getQualifiedName();
		} else if (IngredientPoolScope.GLOBAL.equals(scope))
			return "Global";

		return null;

	}

	@SuppressWarnings("rawtypes")
	@Override
	public String getType(Ingredient ingredient) {

		if (ingredient.getCode() instanceof CtExpression) {

			CtExpression exp = (CtExpression) ingredient.getCode();
			return exp.getType().getSimpleName();
		}
		return ingredient.getCode().getClass().getSimpleName();
	}

	public TOSCounter getTosCounter() {
		return tosCounter;
	}

	public boolean support(TOSEntity tos) {
		int minsupport = ConfigurationProperties.getPropertyInt("minsupport");
		return support(tos, minsupport);
	}

	public boolean support(TOSEntity tos, int minsupport) {
		String tosString = tos.getChacheCodeString();
		int occurrences = this.tosCounter.getTosOcurrenceCounter().get(tosString);

		return (occurrences >= minsupport);

	}
}
