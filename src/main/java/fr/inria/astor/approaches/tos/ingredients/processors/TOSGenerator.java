package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.List;

import fr.inria.astor.approaches.tos.entity.TOSEntity;
import spoon.reflect.code.CtStatement;

/**
 * Represent a processor that generates a list of TOS
 * @author Matias Martinez
 *
 */
public interface TOSGenerator {

	public List<? extends TOSEntity> createTOS(CtStatement ingredientStatement);

}
