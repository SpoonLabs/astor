package fr.inria.astor.core.manipulation.synthesis;

import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class SynthesisBasedTransformationStrategy implements IngredientTransformationStrategy {

	IngredientSynthesizer synthesizer = null;

	public SynthesisBasedTransformationStrategy(IngredientSynthesizer synthesizer) {
		super();
		this.synthesizer = synthesizer;
	}

	public SynthesisBasedTransformationStrategy() {
		this.synthesizer = loadIngredientSynthesizer();
	}

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient) {
		// TODO: create a cache:
		ValueCollector vcollector = new ValueCollector();
		DynamicCollectedValues collectedValues = vcollector.collectValues(AstorMain.projectFacade, modificationPoint);

		CtType expectedType = null;
		if (modificationPoint.getCodeElement() instanceof CtExpression) {
			CtExpression exp = (CtExpression) modificationPoint.getCodeElement();
			expectedType = exp.getType().getTypeDeclaration();
		}
		List<Ingredient> synthesizedIngredients = this.synthesizer.executeSynthesis(modificationPoint,
				modificationPoint.getCodeElement(), expectedType, modificationPoint.getContextOfModificationPoint(),
				collectedValues);

		return synthesizedIngredients;
	}

	public abstract IngredientSynthesizer loadIngredientSynthesizer();

}
