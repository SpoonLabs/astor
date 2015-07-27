package fr.inria.astor.core.loop.evolutionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtTypeReference;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Extension of Evolutionary loop for kali implementation
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JKali extends JGenProg {

	final Set<String> prim = new HashSet<String>(Arrays.asList("byte","Byte", "long","Long", "int","Integer", "float","Float",  "double","Double", "short","Short", "char", "Character"));

	public JKali(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void startEvolution() throws Exception {
		// We don't evolve variants, so the generation is always one.
		final int generation = 1;
		// For each variant (in kali mode is enough having only one)
		for (ProgramVariant parentVariant : variants) {
			// We analyze each Gen of the variant i.e. suspicious statement
			for (Gen gen : parentVariant.getGenList()) {
				// We create all kali operators to apply in the gen
				List<GenOperationInstance> genOperations = createKaliOperators((GenSuspicious) gen);

				for (GenOperationInstance genOperation : genOperations) {

					log.info("--> " + genOperation);
															
					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generation);
					solutionVariant.getOperations().put(generation, Arrays.asList(genOperation));

					applyNewMutationOperationToSpoonElement(genOperation);
					
					boolean solution = processCreatedVariant(solutionVariant, generation);

					if (solution) {
						this.solutions.add(solutionVariant);
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(genOperation);
				}
			}
		}
		showResults(generation);
	}

	/**
	 * Creates the Kali operators 1) Delete statement 2) Insert return 3) change
	 * if condition to true/false.
	 * 
	 * @param gen
	 * @return
	 */
	private List<GenOperationInstance> createKaliOperators(GenSuspicious gen) {
		List<GenOperationInstance> ops = new ArrayList<>();

		GenOperationInstance opRemove = new GenOperationInstance(gen, GenProgMutationOperation.DELETE,
				gen.getRootElement(), null);
		setParentToGenOperator(opRemove, gen);
		ops.add(opRemove);

		GenOperationInstance opInsertReturn = new GenOperationInstance(gen, GenProgMutationOperation.INSERT_BEFORE,
				gen.getRootElement(), createReturn(gen.getRootElement()));
		setParentToGenOperator(opInsertReturn, gen);
		ops.add(opInsertReturn);

		if (gen.getRootElement() instanceof CtIf) {
			GenOperationInstance opChangeIftrue = new GenOperationInstance(gen, GenProgMutationOperation.REPLACE,
					gen.getRootElement(), createIf((CtIf) gen.getRootElement(), true));
			setParentToGenOperator(opChangeIftrue, gen);
			ops.add(opChangeIftrue);

			GenOperationInstance opChangeIffalse = new GenOperationInstance(gen, GenProgMutationOperation.REPLACE,
					gen.getRootElement(), createIf((CtIf) gen.getRootElement(), false));
			setParentToGenOperator(opChangeIffalse, gen);
			ops.add(opChangeIffalse);
		}
		return ops;
	}

	/**
	 * Creates a new if from that one passed as parammeter. The next if has a
	 * condition expression expression true or false according to the variable
	 * <b>thenBranch</b>
	 * 
	 * @param ifElement
	 * @param thenBranch
	 * @return
	 */
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked", })
	private CtIf createIf(CtIf ifElement, boolean thenBranch) {

		CtIf clonedIf = this.mutatorSupporter.getFactory().Core().clone(ifElement);
		CtExpression ifExpression = this.mutatorSupporter.getFactory().Code()
				.createCodeSnippetExpression(Boolean.toString(thenBranch)).compile();

		clonedIf.setCondition(ifExpression);

		return clonedIf;
	}

	@SuppressWarnings({ "rawtypes", "unchecked", "static-access" })
	private CtElement createReturn(CtElement rootElement) {
		CtMethod method = rootElement.getParent(CtMethod.class);

		if (method == null) {
			log.info("Element without method parent");
			return null;
		}
		// We create the "if(true)"{}
		CtIf ifReturn = this.mutatorSupporter.getFactory().Core().createIf();
		CtExpression ifTrueExpression = this.mutatorSupporter.getFactory().Code().createCodeSnippetExpression("true")
				.compile();
		ifReturn.setCondition(ifTrueExpression);

		// Now we create the return statement
		CtReturn<?> returnStatement = null;
		CtTypeReference typeR = method.getType();
		if (typeR == null || "void".equals(typeR.getSimpleName())) {
			returnStatement = this.mutatorSupporter.getFactory().Core().createReturn();
		} else {
			String codeExpression = "";
			if (prim.contains(typeR.getSimpleName())) {
				codeExpression = getZeroValue(typeR.getSimpleName().toLowerCase());
			} else if(typeR.getSimpleName().toLowerCase().equals("boolean")){
				codeExpression = "false";
			} else {
				codeExpression = "null";
			}
			CtExpression returnExpression = this.mutatorSupporter.getFactory().Code()
					.createCodeSnippetExpression(codeExpression).compile();
			returnStatement = this.mutatorSupporter.getFactory().Core().createReturn();
			returnStatement.setReturnedExpression(returnExpression);
		}
		// Now, we associate if(true){return [...]}
		ifReturn.setThenStatement(returnStatement);
		return ifReturn;

	}

	private String getZeroValue(String simpleName) {
		if("float".equals(simpleName))
			return "0f";
		if("long".equals(simpleName))
			return "0l";
		if("double".equals(simpleName))
			return "0d";
		return "0";
	}

}
