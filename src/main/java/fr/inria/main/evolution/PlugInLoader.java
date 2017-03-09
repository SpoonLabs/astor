package fr.inria.main.evolution;

import org.apache.log4j.Logger;
import fr.inria.astor.core.loop.extension.AstorExtensionPoint;
import fr.inria.astor.core.setup.ConfigurationProperties;
/**
 * 
 * @author Matias Martinez
 *
 */
public class PlugInLoader {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());
	
	public static AstorExtensionPoint loadPlugin(ExtensionPoints ep) throws Exception {
		return loadPlugin(ConfigurationProperties.getProperty(ep.identifier), ep._class);
	}
	
	public  static AstorExtensionPoint loadPlugin(String className,Class type) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(className);
			object = classDefinition.newInstance();
		} catch (Exception e) {
			log.error("Loading " + className + " --" + e);
			throw new Exception("Error Loading Engine: " + e);
		}
		if (type.isInstance(object))
			return (AstorExtensionPoint) object;
		else
			throw new Exception(
					"The strategy " + className + " does not extend from " +type.getClass().getName());

	}
	
	
}
