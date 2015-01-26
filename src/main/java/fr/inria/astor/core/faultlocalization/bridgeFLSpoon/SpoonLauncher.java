package fr.inria.astor.core.faultlocalization.bridgeFLSpoon;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;


/**
 * Spoon Laucher. It's used to load an processor (using add method) and process an ctelement (process method) and its children
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */

public abstract class SpoonLauncher extends Launcher {

	Factory factory = null;
	
	public SpoonLauncher(Factory factory) throws Exception {
		this.factory = factory;
	}
	
	protected void process(CtElement element) {
		ProcessingManager processing = new QueueProcessingManager(factory);
		for (String processorName : getProcessorTypes()) {
			processing.addProcessor(processorName);
		}

		processing.process(element);
	}
	
}
