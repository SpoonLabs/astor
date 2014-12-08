package fr.inria.astor.core.faultlocalization;

import spoon.Launcher;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.support.QueueProcessingManager;
//import spoon.AbstractLauncher;

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
		// processing (consume all the processors)
		ProcessingManager processing = new QueueProcessingManager(factory);
		for (String processorName : getProcessorTypes()) {
			processing.addProcessor(processorName);
			//Factory.getLauchingFactory().getEnvironment().debugMessage("Loaded processor " + processorName + ".");
		}

		processing.process(element);
	}
	
}
