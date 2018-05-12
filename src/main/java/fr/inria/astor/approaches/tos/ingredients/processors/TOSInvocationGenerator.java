package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TOSInvocationGenerator<T extends CtElement> implements PlaceholderGenerator<T> {

	public static String PATTERN = "_%s_%s_%d_";
	protected Logger log = Logger.getLogger(this.getClass().getName());

	@SuppressWarnings("rawtypes")
	@Override
	public List<? extends Placeholder> createTOS(T ingredientSource) {

		List<InvocationPlaceholder> tosGenerated = new ArrayList<>();
		List<CtAbstractInvocation> invocations = InvocationResolver.collectInvocation(ingredientSource, true);
		for (CtAbstractInvocation ctAbstractInvocation : invocations) {
			//this.log.debug("--> analyzing " + ingredientStatement);
			tosGenerated.addAll(generate(ctAbstractInvocation, ingredientSource));
		}

		return tosGenerated;
	}

	@SuppressWarnings("rawtypes")
	private List<InvocationPlaceholder> generate(CtAbstractInvocation ctAbstractInvocation,
			T originalStatement) {
		List<InvocationPlaceholder> tosGenerated = new ArrayList<>();

		// We collect all variables
		List<CtAbstractInvocation> invocations = InvocationResolver.collectInvocation(originalStatement, true);

		int nrmethod = 0;
		for (int i = 0; i < invocations.size(); i++) {

			CtAbstractInvocation invocation_i = invocations.get(i);
			if (invocation_i instanceof CtInvocation) {
				if (ctAbstractInvocation.equals(invocation_i)) {

					CtInvocation cinvocationToModify = (CtInvocation) invocation_i;
					if (cinvocationToModify.getTarget() == null) {
						//log.debug("\nTarget null " + ctAbstractInvocation);
						continue;
					}
					String newName = String.format(PATTERN,
							cinvocationToModify.getTarget().getType().getQualifiedName(),
							cinvocationToModify.getType().getQualifiedName(), nrmethod++);

					InvocationPlaceholder tf = new InvocationPlaceholder(newName, cinvocationToModify,
							cinvocationToModify.getTarget().getType(), cinvocationToModify.getType());

					tosGenerated.add(tf);

					break;
				}
			}

		}

		return tosGenerated;
	}

}
