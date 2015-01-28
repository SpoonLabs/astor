package fr.inria.astor.core.faultlocalization.bridgeFLSpoon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
/**
 * Spoon processor used to retrieve the CtElements contained in a given a line number.
 * Used by the {@link SpoonLocationPointerLauncher}}
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class SpoonElementPointer extends AbstractProcessor<CtElement> {

	Logger logger = Logger.getLogger(SpoonElementPointer.class.getName());

	
	/**
	 * Line to search CtElements
	 */
	public static int line = 0;

	/**
	 * Result of the processor: CtElements found in line given by attribute @line
	 */
	public static List<CtElement> inLine = new ArrayList<CtElement>();
	
	
	public void process(CtElement element) {
		
		SourcePosition pos = element.getPosition();
		if (pos != null && pos.getLine() == line) {
			inLine.add(element);
		}
	}
	


}
