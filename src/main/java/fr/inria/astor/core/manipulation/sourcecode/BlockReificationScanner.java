package fr.inria.astor.core.manipulation.sourcecode;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.visitor.CtScanner;

/**
 * Reification of Blocks. It creates a new parent 
 * of type block for all statements which parents are not blocks
 * (for the moment only if)
 * @author matias
 *
 */
public class BlockReificationScanner extends CtScanner {

	@Override
	public void visitCtIf(CtIf element) {
		super.visitCtIf(element);
		if(!(element.getThenStatement() instanceof CtBlock)){
			CtStatement c = element.getThenStatement() ;
			CtBlock nBlock = MutationSupporter.getFactory().Core().createBlock();
			nBlock.addStatement(c);
			element.setThenStatement(nBlock);
		}
		
		if( element.getElseStatement() != null && !(element.getElseStatement() instanceof CtBlock)){
			CtStatement c = element.getElseStatement() ;
			CtBlock nBlock = MutationSupporter.getFactory().Core().createBlock();
			nBlock.addStatement(c);
			element.setElseStatement(nBlock);
		}
	}

}
