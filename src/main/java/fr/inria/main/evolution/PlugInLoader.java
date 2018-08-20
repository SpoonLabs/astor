package fr.inria.main.evolution;

import org.apache.log4j.Logger;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PlugInLoader {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public static AstorExtensionPoint loadPlugin(ExtensionPoints ep) throws Exception {
		String property = ConfigurationProperties.getProperty(ep.identifier);
		if (property == null || property.trim().isEmpty())
			return null;

		return loadPlugin(property, ep._class);
	}

	public static Class loadClassFromProperty(ExtensionPoints ep) throws Exception {
		String property = ConfigurationProperties.getProperty(ep.identifier);
		if (property == null || property.trim().isEmpty())
			return null;

		Class classDefinition = null;
		try {
			classDefinition = Class.forName(property);

		} catch (Exception e) {
			log.error("Loading " + property + " --" + e);
			throw new Exception("Error Loading Engine: " + e);
		}

		return classDefinition;
	}

	public static AstorExtensionPoint loadPlugin(String className, Class type) throws Exception {
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
			throw new Exception("The strategy " + className + " does not extend from " + type.getCanonicalName());

	}

	public static AstorExtensionPoint loadPlugin(ExtensionPoints ep, Class[] typesConst, Object[] args)
			throws Exception {
		String property = ConfigurationProperties.getProperty(ep.identifier);
		if (property == null || property.trim().isEmpty())
			return null;

		return loadPlugin(property, ep._class, typesConst, args);
	}

	public static AstorExtensionPoint loadPlugin(String className, Class type, Class[] typesConst, Object[] args)
			throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(className);
			object = classDefinition.getConstructor(typesConst).newInstance(args);
		} catch (Exception e) {
			log.error("Loading " + className + " --" + e);
			throw new Exception("Error Loading Engine: " + e);
		}
		if (type.isInstance(object))
			return (AstorExtensionPoint) object;
		else
			throw new Exception("The strategy " + className + " does not extend from " + type.getClass().getName());

	}

}
