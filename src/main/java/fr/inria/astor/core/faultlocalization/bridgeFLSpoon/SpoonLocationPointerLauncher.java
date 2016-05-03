package fr.inria.astor.core.faultlocalization.bridgeFLSpoon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;


/**
 * Spoon processor Launcher to obtain the CtElement contained in one line
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class SpoonLocationPointerLauncher extends SpoonLauncher {

	Logger logger = Logger.getLogger(SpoonLocationPointerLauncher.class.getName());
	public static boolean originalLocation = true;
	
	public SpoonLocationPointerLauncher(Factory factory) throws Exception {
		super(factory);
	}

	/**
	 * Return the ctElement from a line. 
	 * 
	 * @param ctelement
	 * @param lineNumber
	 * @param onlyRoot
	 * @return ctElements from the line
	 */
	public List<CtElement> run(CtElement ctelement, int lineNumber) {
		this.addProcessor(SpoonElementPointer.class.getName());
		SpoonElementPointer.inLine.clear();
		SpoonElementPointer.line = lineNumber;
		this.process(ctelement);
		return new ArrayList<>(SpoonElementPointer.inLine);
	}



	@Deprecated
	private String getCtElementContent(CtElement element) {
		CtElement selected = element;
		if (element instanceof CtIf) {
			CtIf ifelem = (CtIf) element;
			selected = ifelem.getCondition();
		}
		if (element instanceof CtWhile) {
			CtWhile ifelem = (CtWhile) element;
			selected = ifelem.getLoopingExpression();
		}
		if (element instanceof CtFor) {
			CtFor ifelem = (CtFor) element;
			selected = ifelem.getExpression();
		}
		if (element instanceof CtDo) {
			CtDo ifelem = (CtDo) element;
			selected = ifelem.getLoopingExpression();
		}
		String content = selected.toString();
		return content;
	}
}
