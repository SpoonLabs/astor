package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.List;

import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import spoon.reflect.code.CtStatement;

/**
 * Represent a processor that generates a list of TOS
 * @author Matias Martinez
 *
 */
public interface PlaceholderGenerator {

	public List<? extends Placeholder> createTOS(CtStatement ingredientStatement);

}
