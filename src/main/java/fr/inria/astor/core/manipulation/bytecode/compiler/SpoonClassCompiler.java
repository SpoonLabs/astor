package fr.inria.astor.core.manipulation.bytecode.compiler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.compiler.tools.JavaXToolsCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.extension.VariantCompiler;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.RuntimeProcessingManager;

/**
 * Compiles a Spoon Class. It keeps the compilation result (bytecode) in memory.
 * The key feature of this loader is it creates the bytecode of a given CtClass.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class SpoonClassCompiler implements VariantCompiler {

	private Factory factory;

	private ProcessingManager processing;

	private DefaultJavaPrettyPrinter prettyPrinter;

	private JavaXToolsCompiler dcc = new JavaXToolsCompiler();

	private Logger logger = Logger.getLogger(SpoonClassCompiler.class.getName());

	public SpoonClassCompiler() {
		this.factory = MutationSupporter.getFactory();
	}

	public SpoonClassCompiler(Factory factory) {
		this.factory = factory;
	}

	@Override
	public CompilationResult compile(ProgramVariant instance, URL[] cp) {
		List<CtClass> ctClasses = new ArrayList<CtClass>(instance.getBuiltClasses().values());
		CompilationResult compilation2 = this.compile(ctClasses, cp);

		return compilation2;
	}

	@Override
	public CompilationResult compile(Collection<? extends CtType> ctClassList, URL[] cp) {

		Map<String, String> toCompile = new HashMap<String, String>();
		prettyPrinter = new DefaultJavaPrettyPrinter(this.getFactory().getEnvironment());

		for (CtType ctClass : ctClassList) {
			try {
				this.getProcessingManager().process(ctClass);
				toCompile.put(ctClass.getQualifiedName(), sourceForModelledClass(ctClass));
			} catch (Exception e) {
				logger.error("Error printing class " + ctClass.getQualifiedName(), e);
			}
		}
		try {
			return compile(cp, toCompile);
		} catch (Exception e) {
			logger.error("Problem compiling");
			throw e;
		}
	}

	public CompilationResult compile(URL[] cp, Map<String, String> toCompile) {
		List<String> cps = new ArrayList<>();
		cps.add("-cp");
		String path = "";
		for (URL url : cp) {
			path += ((url.getPath()) + File.pathSeparator);
		}
		cps.add(path);

		String compliance = ConfigurationProperties.getProperty("javacompliancelevel");
		cps.add("-source");
		cps.add("1." + compliance);

		cps.add("-target");
		cps.add("1." + compliance);
		dcc = new JavaXToolsCompiler();
		CompilationResult rbc = dcc.javaBytecodeFor(toCompile, new HashMap<String, byte[]>(), cps);
		return rbc;
	}

	protected synchronized String sourceForModelledClass(CtType<?> modelledClass) {
		prettyPrinter = new DefaultJavaPrettyPrinter(this.getFactory().getEnvironment());
		prettyPrinter.scan(modelledClass);
		String sourceCode = "package " + modelledClass.getPackage().toString() + ";"
				+ System.getProperty("line.separator") + prettyPrinter.toString();
		prettyPrinter = new DefaultJavaPrettyPrinter(this.getFactory().getEnvironment());
		return sourceCode;
	}

	/**
	 * Gets the associated (standard) environment. When we create it, we set the
	 * compliance level taken as parameter (if any)
	 */

	/**
	 * Gets the associated factory.
	 */

	public Factory getFactory() {
		return this.factory;
	}

	/**
	 * Gets the processing manager.
	 */
	public ProcessingManager getProcessingManager() {
		if (this.processing == null) {
			this.processing = new RuntimeProcessingManager(this.getFactory());
		}
		return this.processing;
	}

}
