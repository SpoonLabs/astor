package fr.inria.astor.core.manipulation.bytecode.compiler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import fr.inria.astor.core.manipulation.bytecode.compiler.tools.JavaXToolsCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.compiler.Environment;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;

/**
 * Compiles a Spoon Class. It keeps the compilation result (bytecode) in memory.
 * The key feature of this loader is it creates the bytecode of a given CtClass.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class SpoonClassCompiler {

	private Environment environment;

	private Factory factory;

	private ProcessingManager processing;

	private JavaOutputProcessor javaPrinter;

	private DefaultJavaPrettyPrinter prettyPrinter;

	private JavaXToolsCompiler dcc = new JavaXToolsCompiler();

	private List<ICompilationUnit> units = new ArrayList<ICompilationUnit>();

	public static final String CLASS_EXT = ".class";

	private Logger logger = Logger.getLogger(SpoonClassCompiler.class.getName());

	
	public SpoonClassCompiler(Factory factory) {
		this.factory = factory;
	}

	public void updateOutput(String output) {
		JavaOutputProcessor fileOutput = new JavaOutputProcessor(new File(output),
				new DefaultJavaPrettyPrinter(getEnvironment()));
		fileOutput.setFactory(getFactory());
		this.javaPrinter = fileOutput;
	}

	public void saveSourceCode(CtClass element) {
		if (javaPrinter == null) {
			throw new IllegalArgumentException("Java printer null");
		}
		if (!element.isTopLevel()) {
			return;
		}
		// Create Java code and create ICompilationUnit
		try{
			units.clear();
			javaPrinter.getCreatedFiles().clear();
			javaPrinter.process(element);
		}catch(Exception e){
			logger.error("Error saving ctclass "+element.getQualifiedName());
		}

	}

	public void saveByteCode(CompilationResult compilation, File outputDir) {
		try {
			outputDir.mkdirs();

			for (String compiledClassName : compilation.getByteCodes().keySet()) {
				String fileName = new String(compiledClassName).replace('.', File.separatorChar) + CLASS_EXT;
				byte[] compiledClass = compilation.getByteCodes().get(compiledClassName);
				ClassFileUtil.writeToDisk(true, outputDir.getAbsolutePath(), fileName, compiledClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public CompilationResult compileOnMemory(Collection<CtClass> ctClassList, URL[] cp) {

		Map<String, String> toCompile = new HashMap<String, String>();
		prettyPrinter = new DefaultJavaPrettyPrinter(getEnvironment());
		dcc = new JavaXToolsCompiler();
		
		for (CtClass ctClass : ctClassList) {
			try {
				this.getProcessingManager().process(ctClass);
				toCompile.put(ctClass.getQualifiedName(), sourceForModelledClass(ctClass));
			} catch (Exception e) {
				logger.error("Error printing class "+ctClass.getQualifiedName(),e);
			}
		}

		List<String> cps = new ArrayList<>();
		cps.add("-cp");
		String path = "";
		for (URL url : cp) {
			path += ((url.getPath()) + File.pathSeparator);
		}
		cps.add(path);
		
		String compliance = ConfigurationProperties.getProperty("javacompliancelevel");
		cps.add("-source");
		cps.add("1."+ compliance);
	
		cps.add("-target");
		cps.add("1."+ compliance);
		
		CompilationResult rbc = dcc.javaBytecodeFor(toCompile, new HashMap<String, byte[]>(), cps);
		return rbc;
	}

	protected synchronized String sourceForModelledClass(CtType<?> modelledClass) {
		prettyPrinter.reset();
		prettyPrinter.scan(modelledClass);
		String sourceCode = "package " + modelledClass.getPackage().toString() + ";"
				+ System.getProperty("line.separator") + prettyPrinter.toString();
		prettyPrinter.reset();
		return sourceCode;
	}

	/**
	 * Gets the associated (standard) environment.
	 * When we create it, we set the compliance level taken as parameter (if any)
	 */

	public Environment getEnvironment() {
		if (this.environment == null) {
			this.environment = new StandardEnvironment();
			String compliance = ConfigurationProperties.getProperty("javacompliancelevel");
			this.environment.setLevel(compliance);
		}
		return this.environment;
	}

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

	public JavaOutputProcessor getJavaPrinter() {
		return javaPrinter;
	}

	public void setJavaPrinter(JavaOutputProcessor javaPrinter) {
		this.javaPrinter = javaPrinter;
	}

}
