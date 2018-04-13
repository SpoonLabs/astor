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
import fr.inria.astor.core.loop.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CacheList;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * Ingredient space (pool) which content are "TOSs"
 * 
 * @author Matias Martinez
 *
 */
public class TOSBStatementIngredientSpace
		extends AstorIngredientSpace<CtElement, String, Ingredient, String, CtCodeElement> {

	// TODO:
	public IngredientSpaceScope scope;
	/**
	 * Entity that stores the stats of TOS.
	 */
	protected TOSCounter tosCounter = null;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Generators used for creating the TOS
	 */
	protected List<PlaceholderGenerator> tosGenerators = new ArrayList<>();

	public TOSBStatementIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);

		if (!ConfigurationProperties.getPropertyBool("excludevariableplaceholder"))
			tosGenerators.add(new VariablePlaceholderGenerator());
		if (!ConfigurationProperties.getPropertyBool("excludeinvocationplaceholder"))
			tosGenerators.add(new TOSInvocationGenerator());
		if (!ConfigurationProperties.getPropertyBool("excludeliteralplaceholder"))
			tosGenerators.add(new LiteralPlaceholderGenerator());
		if (!ConfigurationProperties.getPropertyBool("excludevarliteralplaceholder"))
			tosGenerators.add(new VarLiPlaceholderGenerator());

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

				if (originalIngredient instanceof CtStatement) {
					CtStatement originalIngredientStatement = (CtStatement) originalIngredient;

					String keyLocation = mapKey(originalIngredientStatement);
					MutationSupporter.getEnvironment().setNoClasspath(true);
					List<TOSEntity> xTemplates = createAllTOS(originalIngredientStatement);

					List<Ingredient> ingredientPoolForLocation = this.retrieveIngredients(keyLocation);
					for (TOSEntity templateElement : xTemplates) {
						//log.debug("\nGenerating element " + templateElement.getDerivedFrom() + " vars "
						//		+ templateElement.getPlaceholders());

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
				} else {
					log.debug("Ingredient is not a statement " + originalIngredient);
				}
			}

		}
		// once we finish creating the space, we split ingredients by type
		this.recreateTypesStructures();
	}

	public List<TOSEntity> createAllTOS(CtStatement ingredientStatement) {
		boolean combinated = ConfigurationProperties.getPropertyBool("toscombinated");
		if (combinated) {
			return createAllTOSCombined(ingredientStatement);
		} else
			return createAllTOSNotCombined(ingredientStatement);
	}

	public List<TOSEntity> createAllTOSNotCombined(CtStatement ingredientStatement) {
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

	public List<TOSEntity> createAllTOSCombined(CtStatement ingredientStatement) {
		// all generated
		List<TOSEntity> xGeneratedTos = new ArrayList<>();
		// for each generator
		for (PlaceholderGenerator tosGenerator : tosGenerators) {

			List<TOSEntity> generatedTosFromGenerator = new ArrayList<>();
			log.debug("-->"+tosGenerator.getClass().getName());
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

					log.debug("\n\nAnalyzing:---------\n" + tosEntity.getCode() + "\nConflicts? " + !intersection.isEmpty() + " "
							+ intersection + "\n----existing: " + tosEntity.getPlaceholders() + "\n----newph: "
							+ placeholder);
					// If not conflict with the placeholders
					if (intersection.isEmpty()) {
						TOSEntity nEnt = new TOSEntity();
						nEnt.setDerivedFrom(ingredientStatement);
						nEnt.getPlaceholders().addAll(tosEntity.getPlaceholders());
						nEnt.getPlaceholders().add(placeholder);
						generatedTosFromGenerator.add(nEnt);
					//	System.out.println("-!-!-->Adding new tos "+nEnt.getChacheCodeString() + " "+nEnt);
					}
				}

			}
			if (generatedTosFromGenerator.size() > 0) {
				xGeneratedTos.addAll(generatedTosFromGenerator);
				int i = 0;
			//	log.debug("\n------\nIntermediate tos set " + xGeneratedTos);
				for (TOSEntity tosEntity : xGeneratedTos) {
				//	System.out.println("--> "+(i++)+ " "+tosEntity);
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

		if (IngredientSpaceScope.PACKAGE.equals(scope)) {
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
		if (IngredientSpaceScope.LOCAL.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientSpaceScope.GLOBAL.equals(scope)) {
			return MutationSupporter.getFactory().Type().getAll();
		}
		return null;
	}

	@Override
	public IngredientSpaceScope spaceScope() {
		return this.scope;
	}

	@Override
	public String calculateLocation(CtElement elementToModify) {

		if (IngredientSpaceScope.PACKAGE.equals(scope)) {
			return elementToModify.getParent(CtPackage.class).getQualifiedName();
		} else if (IngredientSpaceScope.LOCAL.equals(scope)) {
			return elementToModify.getParent(CtType.class).getQualifiedName();
		} else if (IngredientSpaceScope.GLOBAL.equals(scope))
			return "Global";

		return null;

	}

	@Override
	protected String getType(Ingredient ingredient) {
		// before was new code
		return ingredient.getDerivedFrom().getClass().getSimpleName();
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
