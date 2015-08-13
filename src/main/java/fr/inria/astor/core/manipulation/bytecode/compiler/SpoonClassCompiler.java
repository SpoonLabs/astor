package fr.inria.astor.core.manipulation.bytecode.compiler;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import spoon.compiler.Environment;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import fr.inria.astor.core.manipulation.bytecode.compiler.tools.JavaXToolsCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;

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

	public SpoonClassCompiler(Factory factory) {
		this.factory = factory;
	}

	public void updateOutput(String output) {
		JavaOutputProcessor fileOutput = new JavaOutputProcessor(new File(
				output), new DefaultJavaPrettyPrinter(getEnvironment()));
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
		units.clear();
		javaPrinter.getCreatedFiles().clear();
		javaPrinter.process(element);

	}

	public void saveByteCode(CompilationResult compilation, File outputDir) {
		try {
			outputDir.mkdirs();

			for (String compiledClassName : compilation.getByteCodes().keySet()) {
				String fileName = new String(compiledClassName).replace('.',
						File.separatorChar) + CLASS_EXT;
				byte[] compiledClass = compilation.getByteCodes().get(
						compiledClassName);
				ClassFileUtil.writeToDisk(true, outputDir.getAbsolutePath(),
						fileName, compiledClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public CompilationResult compileOnMemory(Collection<CtClass> ctClassList,
			URL[] cp) {
		//
		Map<String, String> toCompile = new HashMap<String, String>();
		prettyPrinter = new DefaultJavaPrettyPrinter(getEnvironment());

		try {

			for (CtClass ctClass : ctClassList) {

				this.getProcessingManager().process(ctClass);
				String[] tmp = ctClass.getQualifiedName().split("[.]");
				char[][] pack = new char[tmp.length - 1][];
				toCompile.put(ctClass.getQualifiedName(),
						sourceForModelledClass(ctClass));

			}

		} catch (Exception e) {
			e.printStackTrace();
			List<String> errors = new ArrayList<String>();
			errors.add(e.getMessage());
			CompilationResult rbc = new CompilationResult(null,errors);
			return rbc;
		}
		List<String> cps = new ArrayList<>();
		cps.add("-cp");
		String s = "";
		for (URL url : cp) {
			s += ((url.getPath()) + File.pathSeparator);
		}
		cps.add(s);
		CompilationResult rbc = dcc.javaBytecodeFor(toCompile,
				new HashMap<String, byte[]>(), cps);
		return rbc;
	}



	protected synchronized String sourceForModelledClass(
			CtSimpleType<?> modelledClass) {
		prettyPrinter.scan(modelledClass);
		String sourceCode = modelledClass.getPackage().toString()
				+ System.getProperty("line.separator")
				+ prettyPrinter.toString();
		prettyPrinter.reset();
		return sourceCode;
	}

	

	/**
	 * Gets the associated (standard) environment.
	 */

	public Environment getEnvironment() {
		if (this.environment == null) {
			this.environment = new StandardEnvironment();
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
