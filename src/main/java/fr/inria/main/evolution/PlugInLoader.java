package fr.inria.main.evolution;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.extension.AstorExtensionPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.GlobalBasicIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.LocalIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.PackageBasicFixSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;

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
			throw new Exception("The strategy " + className + " does not extend from " + type.getClass().getName());

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

	public static OperatorSpace loadOperatorSpace() throws Exception {
		// We check if the user defines the operators to include in the operator
		// space
		OperatorSpace operatorSpace = null;
		String customOp = ConfigurationProperties.getProperty("customop");
		if (customOp != null && !customOp.isEmpty()) {
			operatorSpace = createCustomOperatorSpace(customOp);
		} else {
			customOp = ConfigurationProperties.getProperty("operatorspace");
			if (customOp != null && !customOp.isEmpty())
				operatorSpace = (OperatorSpace) PlugInLoader.loadPlugin(ExtensionPoints.OPERATORS_SPACE);
		}
		return operatorSpace;
	}

	public static IngredientSpace loadIngredientSpace(List<TargetElementProcessor<?>> ingredientProcessors)
			throws JSAPException, Exception {
		// The ingredients for build the patches
		String scope = ConfigurationProperties.properties.getProperty("scope");
		IngredientSpace ingredientspace = null;
		if ("global".equals(scope)) {
			ingredientspace = (new GlobalBasicIngredientSpace(ingredientProcessors));
		} else if ("package".equals(scope)) {
			ingredientspace = (new PackageBasicFixSpace(ingredientProcessors));
		} else if ("local".equals(scope)) {
			ingredientspace = (new LocalIngredientSpace(ingredientProcessors));
		} else {
			ingredientspace = (IngredientSpace) PlugInLoader.loadPlugin(ExtensionPoints.INGREDIENT_STRATEGY_SCOPE,
					new Class[] { List.class }, new Object[] { ingredientProcessors });

		}
		return ingredientspace;
	}

	private static OperatorSpace createCustomOperatorSpace(String customOp) throws Exception {
		OperatorSpace customSpace = new OperatorSpace();
		String[] operators = customOp.split(File.pathSeparator);
		for (String op : operators) {
			AstorOperator aop = (AstorOperator) PlugInLoader.loadPlugin(op, ExtensionPoints.CUSTOM_OPERATOR._class);
			if (aop != null)
				customSpace.register(aop);
		}
		if (customSpace.getOperators().isEmpty()) {
			log.error("Empty custom operator space");
			throw new Exception("Empty custom operator space");
		}
		return customSpace;
	}

}
