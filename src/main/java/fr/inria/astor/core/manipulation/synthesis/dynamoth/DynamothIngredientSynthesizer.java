package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.synthesis.IngredientSynthesizer;
import fr.inria.astor.core.manipulation.synthesis.ExecutionContext;
import fr.inria.lille.repair.common.Candidates;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class DynamothIngredientSynthesizer implements IngredientSynthesizer {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Override
	public List<CtElement> executeSynthesis(ModificationPoint modificationPoint, CtElement hole, CtType expectedtype,
			List<CtVariable> contextOfModificationPoint, ExecutionContext values) {
		List<CtElement> result = new ArrayList<>();
		DynamothSynthesizer dynamothSynthesizer = new DynamothSynthesizer((DynamothSynthesisContext) values);
		Candidates candidates = dynamothSynthesizer.combineValues();

		for (fr.inria.lille.repair.expression.Expression expression : candidates) {
			String candidateCode = expression.toString();
			log.info("Candidate: " + candidateCode);

			if (hole instanceof CtExpression) {

				CtCodeSnippetExpression<Boolean> snippet = MutationSupporter.getFactory().Core()
						.createCodeSnippetExpression();
				snippet.setValue(candidateCode);
				result.add(snippet);
			} else {
				log.debug("Error: Other type not analyzed " + hole.getClass().getName());
			}

		}

		return result;
	}

}
