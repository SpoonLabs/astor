package fr.inria.astor.core.manipulation.compiler.bytecode;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;

import spoon.compiler.Environment;
import spoon.processing.ProcessingManager;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.RuntimeProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.util.BasicCompilationUnit;

/**
 * Compiles a Spoon Class. It keeps the compilation result (bytecode) in memory.
 * The key feature of this loader is it creates the bytecode of a given CtClass.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class SpoonClassCompiler {

	private CoreFactory coreFactory;

	private Environment environment;

	private Factory factory;

	private ProcessingManager processing;

	private JavaOutputProcessor javaPrinter;

	private JDTByteCodeCompiler compiler = new JDTByteCodeCompiler();;

	private List<ICompilationUnit> units = new ArrayList<ICompilationUnit>();

	public static final String CLASS_EXT = ".class";

	public SpoonClassCompiler(Factory factory) {
		this.factory = factory;
	}

	public void updateOutput(String output) {
		JavaOutputProcessor fileOutput = new JavaOutputProcessor(new File(output), new DefaultJavaPrettyPrinter(getEnvironment()));
		fileOutput.setFactory(getFactory());
		this.javaPrinter = fileOutput;
	}

	/**
	 * Given a simple class, the method: 1- Generates the java source code from
	 * the spoon element. 2 - Compiles the source code generated 3- return the
	 * bycode It does not save the bytecode to disk PROBLEM: It saves the Java
	 * code on disk
	 * 
	 * @param element
	 * @return
	 */
	public ProgramVariantCompilationResult compileSpoonClassElement(CtClass element) {
		if (javaPrinter == null) {
			throw new IllegalArgumentException("Java printer null");
		}
		if (!element.isTopLevel()) {
			return null;
		}
		// Create Java code and create ICompilationUnit
		units.clear();
		javaPrinter.getCreatedFiles().clear();
		//javaPrinter.createJavaFile(element);
		javaPrinter.process(element);
		// We create the Units
		for (File f : javaPrinter.getCreatedFiles()) {
			try {
				units.add(JDTByteCodeCompiler.getUnit(element.getQualifiedName(), f));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// We call JDTCompiler to compile the units.

		compiler = new JDTByteCodeCompiler();

		configureJDTCompilator();

		compiler.compile(units.toArray(new ICompilationUnit[0]));

		return new ProgramVariantCompilationResult(compiler.getClassFiles(), compiler.getErrors(), element);
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
		//javaPrinter.createJavaFile(element);
		javaPrinter.process(element);
		
	}
	
	boolean adjust_lines = false;
	
	public void saveByteCode(List<ClassFile> classfiles, File outputDir) {
		try {
			outputDir.mkdirs();

			for (ClassFile f : classfiles) {
				String fileName = new String(f.fileName()).replace('/', File.separatorChar) + CLASS_EXT;
				if(adjust_lines){
				ClassFileUtil.adjustLineNumbers(f.getBytes(), f.headerOffset + f.methodCountOffset - 1, javaPrinter
						.getLineNumberMappings().get(new String(f.fileName()).replace('/', '.')));
				}
				ClassFileUtil.writeToDisk(true, outputDir.getAbsolutePath(), fileName, f.getBytes());

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	
	public void saveByteCode(CompilationResult compilation, File outputDir) {
		try {
			outputDir.mkdirs();

			for (String  compiledClassName : compilation.getByteCodes().keySet()) {
				String fileName = new String(compiledClassName).replace('.', File.separatorChar) + CLASS_EXT;
				byte[] compiledClass = compilation.getByteCodes().get(compiledClassName);
				ClassFileUtil.writeToDisk(true, outputDir.getAbsolutePath(), fileName, compiledClass);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	public void configureJDTCompilator() {
		compiler.getCompilerOption().sourceLevel = getJavaCompliance();
		compiler.getCompilerOption().targetJDK = getJavaCompliance();
		// MM
		compiler.getCompilerOption().complianceLevel = getJavaCompliance();
		compiler.getCompilerOption().produceDebugAttributes = ClassFileConstants.ATTR_SOURCE
				| ClassFileConstants.ATTR_LINES | ClassFileConstants.ATTR_VARS;
		compiler.getCompilerOption().preserveAllLocalVariables = true;
		compiler.getCompilerOption().inlineJsrBytecode = true;
	}

	/**
	 * Tells if the source is Java 1.4 or lower.
	 */
	public long getJavaCompliance() {
		switch (FactoryImpl.getLauchingFactory().getEnvironment().getComplianceLevel()) {
		case 1:
			return ClassFileConstants.JDK1_1;
		case 2:
			return ClassFileConstants.JDK1_2;
		case 3:
			return ClassFileConstants.JDK1_3;
		case 4:
			return ClassFileConstants.JDK1_4;
		case 5:
			return ClassFileConstants.JDK1_5;
		case 6:
			return ClassFileConstants.JDK1_6;
		}
		return ClassFileConstants.JDK1_5;
	}

	/**
	 * Compiles in memory and do not save neither the source code and bytecode
	 * (.class) on disk.
	 * 
	 * @param ctClass
	 * @return
	 */
	public ProgramVariantCompilationResult compileOnMemory(CtClass ctClass) {

		this.getProcessingManager().process(ctClass);
		// Printing it
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(this.getEnvironment());
		printer.scan(ctClass);

		String[] tmp = ctClass.getQualifiedName().split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		String classBody = printer.toString();
		StringBuffer classBuffer = new StringBuffer(classBody.length() + 100);
		classBuffer.append(ctClass.getPackage()).append(classBody);

		BasicCompilationUnit unit = new BasicCompilationUnit(classBuffer.toString().toCharArray(), pack,
				ctClass.getSimpleName() + ".java");
		// MM
		// JDTByteCodeCompiler compiler = new JDTByteCodeCompiler();
		compiler.compile(new ICompilationUnit[] { unit });

		return new ProgramVariantCompilationResult(compiler.getClassFiles(), compiler.getErrors(), ctClass, classBody);

	}

	public ProgramVariantCompilationResult compileOnMemory(Collection<CtClass> ctClassList) {
		List<BasicCompilationUnit> res = new ArrayList<BasicCompilationUnit>();
		for (CtClass ctClass : ctClassList) {

			this.getProcessingManager().process(ctClass);
			// Printing it
			DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(this.getEnvironment());
			printer.scan(ctClass);

			String[] tmp = ctClass.getQualifiedName().split("[.]");
			char[][] pack = new char[tmp.length - 1][];

			for (int i = 0; i < tmp.length - 1; i++) {
				pack[i] = tmp[i].toCharArray();
			}

			String classBody = printer.toString();
			StringBuffer classBuffer = new StringBuffer(classBody.length() + 100);
			classBuffer.append(ctClass.getPackage()).append(classBody);

			BasicCompilationUnit unit = new BasicCompilationUnit(classBuffer.toString().toCharArray(), pack,
					ctClass.getSimpleName() + ".java");

			res.add(unit);
		}
		compiler.compile(res.toArray((new ICompilationUnit[] {})));
		return new ProgramVariantCompilationResult(compiler.getClassFiles(), compiler.getErrors(), null, null);

	}
	public CompilationResult compileOnMemory2(Collection<CtClass> ctClassList,URL[] cp) {
		//
		Map<String, String> toCompile =  new HashMap<String,String>();
		prettyPrinter = new DefaultJavaPrettyPrinter(getEnvironment());
		
		try{
			
		for (CtClass ctClass : ctClassList) {

			this.getProcessingManager().process(ctClass);
			String[] tmp = ctClass.getQualifiedName().split("[.]");
			char[][] pack = new char[tmp.length - 1][];
				toCompile.put(ctClass.getQualifiedName(),sourceForModelledClass(ctClass));
			
		}
		
		}
		catch(Exception e){
			e.printStackTrace();
			List<String> errors = new ArrayList(){};
			errors.add(e.getMessage());
			CompilationResult rbc = new CompilationResult(null, true, errors);
			return rbc;
		}	
		List<String> cps = new ArrayList<>();
		cps.add("-cp");
		String s = "";
		for (URL url : cp) {
			s+=((url.getPath())+File.pathSeparator);
		}
		cps.add(s);
		//cps.add("-d");
		//cps.add("C:/Personal/develop/workspaceEvolution/MutationEngine/outputMutation/test");
		//
		CompilationResult rbc = dcc.javaBytecodeFor(toCompile, new HashMap<String, byte[]>(),cps);
		System.out.println("-comp result->"+rbc);
		return rbc;
	}
	DefaultJavaPrettyPrinter prettyPrinter;
	protected synchronized String sourceForModelledClass(CtSimpleType<?> modelledClass) {
		prettyPrinter.scan(modelledClass);
		String sourceCode = modelledClass.getPackage().toString() +  System.getProperty("line.separator") +  prettyPrinter.toString();
		prettyPrinter.reset();
		return sourceCode;
	}
	
	//
	private Map<String, byte[]> compiledClasses;
	JavaXToolsCompiler dcc = new JavaXToolsCompiler();
	
	//
	
	public static BytecodeClassLoader loaderWith(Map<String, byte[]> bytecodes, URL[] classpath) {
		BytecodeClassLoader newLoader = new BytecodeClassLoader(classpath);
		newLoader.setBytecodes(bytecodes);
		return newLoader;
	}

	/**
	 * Gets the associated (default) core factory.
	 */
	public CoreFactory getCoreFactory() {
		if (this.coreFactory == null) {
			this.coreFactory = new DefaultCoreFactory();
		}
		return this.coreFactory;
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
