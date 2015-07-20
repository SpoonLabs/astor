package fr.inria.astor.core.loop.evolutionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Extension of Evolutionary loop with GenProgOperations
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JKali extends JGenProg {

	Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();

	public JKali(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void startEvolution() throws Exception {

		for (ProgramVariant parentVariant : variants) {

			for (Gen gen : parentVariant.getGenList()) {
				List<GenOperationInstance> genOperations = createKaliOperators((GenSuspicious) gen);
				for (GenOperationInstance genOperation : genOperations) {

					log.info("--> " + genOperation);
					// parentVariant.getOperations().put(0, genOperation);
					applyNewMutationOperationToSpoonElement(genOperation);
					//
					parentVariant.getOperations().put(1, Arrays.asList(genOperation));

					boolean solution = processCreatedVariant(parentVariant, 0);

					if (solution) {
						ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
								0);
						this.solutions.add(solutionVariant);
					}
					//
					undoOperationToSpoonElement(genOperation);
					parentVariant.getOperations().clear();
				}
			}
		}
		// Print solutions
		if (!this.solutions.isEmpty()) {
			log.info("End Repair Loops: Found solution");
			log.info("Solution stored at: " + projectFacade.getProperties().getInDir());

		} else {
			log.info("End Repair Loops: NOT Found solution");
		}
		log.info("Number solutions:" + this.solutions.size());
		for (ProgramVariant variant : solutions) {
			log.info("f (sol): " + variant.getFitness() + ", " + variant);
		}
		if (!solutions.isEmpty()) {
			log.info("\nSolution details");
			log.info(mutatorSupporter.getSolutionData(solutions, 1));

		}

	}

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
					gen.getRootElement(), createIf(gen.getRootElement(), true));
			setParentToGenOperator(opChangeIftrue, gen);
			ops.add(opChangeIftrue);

			GenOperationInstance opChangeIffalse = new GenOperationInstance(gen, GenProgMutationOperation.REPLACE,
					gen.getRootElement(), createIf(gen.getRootElement(), false));
			setParentToGenOperator(opChangeIffalse, gen);
			ops.add(opChangeIffalse);
		}
		return ops;
	}

	private CtIf createIf(CtElement rootElement, boolean thenBranch) {
		CtIf ifElement = (CtIf) rootElement;

		CtIf clonedIf = this.mutatorSupporter.getFactory().Core().clone(ifElement);
		CtExpression ifExpression = this.mutatorSupporter.getFactory().Code()
				.createCodeSnippetExpression(Boolean.toString(thenBranch)).compile();

		clonedIf.setCondition(ifExpression);

		return clonedIf;
	}

	private CtElement createReturn(CtElement rootElement) {
		CtMethod method = rootElement.getParent(CtMethod.class);
		List prim = Arrays.asList("byte", "long", "int", "float", "double", "sort");
		CtReturn<?> patch = null;
		if (method != null) {

			CtTypeReference typeR = method.getType();
			if (typeR == null || "void".equals(typeR.toString())) {
				return this.mutatorSupporter.getFactory().Core().createReturn();
			}
			String codeExpression = "";
			if (prim.contains(typeR.toString())) {
				codeExpression = "0";
			} else {
				codeExpression = "null";
			}
			CtExpression returnExpression = this.mutatorSupporter.getFactory().Code()
					.createCodeSnippetExpression(codeExpression).compile();
			patch = this.mutatorSupporter.getFactory().Core().createReturn();
			patch.setReturnedExpression(returnExpression);
		}

		return patch;
	}

}
