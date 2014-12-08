package fr.inria.astor.core.faultlocalization.bridgeFLSpoon;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.Launcher;
import spoon.processing.AbstractProcessor;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import fr.inria.astor.core.faultlocalization.SuspiciousCode;
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
	@Deprecated
	public List<CtElement> retrieveSpoonElement(SuspiciousCode candidate) throws Exception {
		return retrieveSpoonElement(candidate.getClassName(), candidate.getLineNumber());
	}
	/**
	 * OLD
	 * @param path
	 * @param line
	 * @return
	 * @throws Exception
	 */
	@Deprecated
	public List<CtElement> retrieveSpoonElement(String path,int line) throws Exception {
		inLine.clear();
		
		logger.info("Searching line "+line+" in file "+path);
		String[] argsS = ("-i "+path
				+ " -p mut.manipulator.SpoonElementPointer").split(" ");
		
		SpoonElementPointer.line = line;
		Launcher.main(argsS);
		logger.info("For Line: " + SpoonElementPointer.inLine);
		for (CtElement e : inLine) {
			logger.info("--> " + e.getClass().getSimpleName()
					+ " " + e.getPosition().getFile());
		}
		return  SpoonElementPointer.inLine;
	
	}

}
