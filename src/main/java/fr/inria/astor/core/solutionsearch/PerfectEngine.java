package fr.inria.astor.core.solutionsearch;

import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Paths;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.AstorOutputStatus;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PerfectEngine extends AstorCoreEngine {

	public PerfectEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);

	}

	@Override
	public void startSearch() throws Exception {

		ProgramVariant programVariant = this.getVariants().get(0);

		OperatorInstance opinstance = null;

		String op = ConfigurationProperties.getProperty("peOperator");

		String contentFile = ConfigurationProperties.getProperty("pefile");

		String content = new String(Files.readAllBytes(Paths.get(contentFile)));

		CtCodeElement contentSp = MutationSupporter.getFactory().Code().createCodeSnippetStatement(content)
				.partiallyEvaluate();
		ModificationPoint modificationPoint = programVariant.getModificationPoints().get(0);

		System.out.println("-->> " + contentSp.toString());
		System.out.println("code patch " + modificationPoint.getCodeElement());

		CtElement parent = modificationPoint.getCodeElement().getParent();
		System.out.println(parent);

		contentSp.setParent(parent);

		if (op.equals("replace")) {

			ReplaceOp rop = new ReplaceOp();

			opinstance = new StatementOperatorInstance(modificationPoint, rop, modificationPoint.getCodeElement(),
					contentSp);

		} else if (op.equals("insertbefore"))

		{

			InsertBeforeOp rop = new InsertBeforeOp();

			opinstance = new StatementOperatorInstance(modificationPoint, rop, modificationPoint.getCodeElement(),
					contentSp);
		}

		boolean applied = opinstance.applyModification();

		assertTrue(applied);

		programVariant.putModificationInstance(0, opinstance);

		programVariant.setId(10);

		ConfigurationProperties.setProperty("saveall", "true");

		boolean solution = this.processCreatedVariant(programVariant, 1);

		if (solution) {
			log.info("Solution found " + getSolutions().size());

			this.solutions.add(programVariant);

		}

		opinstance.undoModification();

		if (solution) {
			log.info("Solution found " + getSolutions().size());
			this.savePatch(programVariant);
		}

		if (!this.solutions.isEmpty() && ConfigurationProperties.getPropertyBool("stopfirst")) {
			this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
			return;
		}

	}

}
