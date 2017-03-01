package fr.inria.astor.core.manipulation.bytecode;

import java.io.File;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.bytecode.compiler.ClassFileUtil;
import fr.inria.astor.core.manipulation.bytecode.compiler.SpoonClassCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.compiler.Environment;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.JavaOutputProcessor;
import spoon.support.StandardEnvironment;

/**
 * 
 * @author Matias Martinez
 *
 */
public class OutputWritter {

	private JavaOutputProcessor javaPrinter;

	private Factory factory;
	

	public static final String CLASS_EXT = ".class";

	private Logger logger = Logger.getLogger(SpoonClassCompiler.class.getName());

	public OutputWritter(Factory factory) {
		super();
		this.factory = factory;
	}

	public void updateOutput(String output) {
		JavaOutputProcessor fileOutput = new JavaOutputProcessor(new File(output),
				new DefaultJavaPrettyPrinter(getEnvironment()));
		this.javaPrinter = fileOutput;
		fileOutput.setFactory(getFactory());
	}

	public void saveSourceCode(CtClass element) {
		if (javaPrinter == null) {
			throw new IllegalArgumentException("Java printer is null");
		}
		if (!element.isTopLevel()) {
			return;
		}
		// Create Java code and create ICompilationUnit
		try {
			javaPrinter.getCreatedFiles().clear();
			javaPrinter.process(element);
		} catch (Exception e) {
			logger.error("Error saving ctclass " + element.getQualifiedName());
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

	public Environment getEnvironment() {
		return this.getFactory().getEnvironment();
	}

	/**
	 * Gets the associated factory.
	 */

	public Factory getFactory() {
		return this.factory;
	}

	public JavaOutputProcessor getJavaPrinter() {
		return javaPrinter;
	}

	public void setJavaPrinter(JavaOutputProcessor javaPrinter) {
		this.javaPrinter = javaPrinter;
	}
}
