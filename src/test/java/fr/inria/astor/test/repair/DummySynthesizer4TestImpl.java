package fr.inria.astor.test.repair;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.synthesis.DynamicCollectedValues;
import fr.inria.astor.core.manipulation.synthesis.IngredientSynthesizer;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * Implementation used in test.
 * 
 * @author Matias Martinez
 *
 */
public class DummySynthesizer4TestImpl implements IngredientSynthesizer {
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Override
	public List<Ingredient> executeSynthesis(ModificationPoint modificationPoint, CtElement hole, CtType expectedtype,
			List<CtVariable> contextOfModificationPoint, DynamicCollectedValues values) {

		log.debug("--> modif point: " + modificationPoint);
		log.debug("--> mohole: " + hole);
		log.debug("--> expected type:  " + expectedtype);
		log.debug("--> context vars: " + contextOfModificationPoint);
		log.debug("--> Collected Values: \n" + values.toString());

		List<Ingredient> result = new ArrayList<>();

		return result;
	}

}
