package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.List;

import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import spoon.reflect.declaration.CtElement;

/**
 * Represent a processor that generates a list of TOS
 * @author Matias Martinez
 *
 */
public interface PlaceholderGenerator<T extends CtElement> {

	public List<? extends Placeholder> createTOS(T ingredientStatement);

}
