/* 
 * Spoon - http://spoon.gforge.inria.fr/
 * Copyright (C) 2006 INRIA Futurs <renaud.pawlak@inria.fr>
 * 
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify 
 * and/or redistribute the software under the terms of the CeCILL-C license as 
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *  
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */

package fr.inria.astor.core.manipulation.compiler.bytecode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ClassFile;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;

import spoon.support.util.BasicCompilationUnit;

/**
 * From Spoonload (old JDTCompiler)
 *
 */
public class JDTByteCodeCompiler implements ICompilerRequestor {

	private Set<String> classErrors = new HashSet<String>();

	private List<ClassFile> classFiles = new ArrayList<ClassFile>();

	CompilerOptions compilerOption;
	
	
	/**
	 * 
	 */
	@Override
	public void acceptResult(CompilationResult result) {
		if (result.hasErrors()) {
			String problems = "";
			for (CategorizedProblem cp : result.getAllProblems()) {
				problems+=new String(result.getFileName())+ "-l:("+cp.getSourceLineNumber() +"):"+cp.toString()+", ";
			}
			classErrors.add(problems);
		}

		for (ClassFile f : result.getClassFiles()) {
			classFiles.add(f);
		}
	}
	/**
	 * Compiles Compilation units and returns the compilation result as ClassFile
	 * @param units
	 * @return
	 */
	public List<ClassFile> compile(ICompilationUnit[] units) {
		
		//Setup result list
		classFiles.clear();
		classErrors.clear();
		
		//JDT compiler
		org.eclipse.jdt.internal.compiler.Compiler compiler = new org.eclipse.jdt.internal.compiler.Compiler(
				getLibraryAccess(), getHandlingPolicy(), getCompilerOption(),
				this, new DefaultProblemFactory());
		//COMPILES
		compiler.compile(units);
		//
		return classFiles;
	}
	
	public static ICompilationUnit getUnit(String name, File file)
			throws Exception {

		String[] tmp = name.split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		FileInputStream in = new FileInputStream(file);
		byte[] buffer = new byte[512];
		int read;
		while ((read = in.read(buffer, 0, 512)) >= 0)
			out.write(buffer, 0, read);

		ICompilationUnit unit = null;

		unit = new BasicCompilationUnit(out.toString().toCharArray(), pack,
				file.getName());

		out.close();
		in.close();
		return unit;
	}
		
	//TODO: remove it
	public CompilerOptions getCompilerOption() {
		if (compilerOption == null) {
			compilerOption = new CompilerOptions();
			compilerOption.sourceLevel = ClassFileConstants.JDK1_6;//ClassFileConstants.JDK1_5;
			compilerOption.suppressWarnings = true;
			
			//
			
			compilerOption.targetJDK = ClassFileConstants.JDK1_6; //was 6
			
			compilerOption.produceDebugAttributes = ClassFileConstants.ATTR_SOURCE
					| ClassFileConstants.ATTR_LINES | ClassFileConstants.ATTR_VARS;
			compilerOption.preserveAllLocalVariables = true;
			compilerOption.inlineJsrBytecode = true;
			
			//MM
			compilerOption.tolerateIllegalAmbiguousVarargsInvocation=true;
		}
		return compilerOption;
	}

	private IErrorHandlingPolicy getHandlingPolicy() {
		return new IErrorHandlingPolicy() {
			public boolean proceedOnErrors() {
				return true; // stop if there are some errors
			}

			public boolean stopOnFirstError() {
				return false;
			}
		};
	}

	FileSystem getLibraryAccess() {
		
		// strategy 1
		String bootpath = System.getProperty("sun.boot.class.path");
		Set<String> lst = new HashSet<String>();
		for (String s : bootpath.split(File.pathSeparator)) {
			File f = new File(s);
			if (f.exists()) {
				lst.add(f.getAbsolutePath());
			}
		}
		
		
		// strategy 2
		ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();//ClassLoader.getSystemClassLoader();		
		if(currentClassLoader instanceof URLClassLoader){
			URL[] urls = ((URLClassLoader) currentClassLoader).getURLs();
			if(urls!=null && urls.length>0){
				
				for (URL url : urls) {
				//	classpath+=File.pathSeparator+url.getFile();
					lst.add(url.getFile());
				}
			
			}
		}
		
		// strategy 3
		String classpath = System.getProperty("java.class.path");
		for (String s : classpath.split(File.pathSeparator)) {
			File f = new File(s);
			if (f.exists()) {
				lst.add(f.getAbsolutePath());
			}
		}
		
			
		//	
		return new FileSystem(lst.toArray(new String[0]), new String[0], System
				.getProperty("file.encoding"));
	}

	public List<ClassFile> getClassFiles() {
		return classFiles;
	}

	public Set<String> getErrors() {
		return classErrors;
	}

	/*
	 *ALTERNATIVE GET UNIT
	 * DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(this.getEnvironment());
		printer.scan(ctClass);

		String[] tmp = ctClass.getQualifiedName().split("[.]");
		char[][] pack = new char[tmp.length - 1][];

		for (int i = 0; i < tmp.length - 1; i++) {
			pack[i] = tmp[i].toCharArray();
		}

		String classBody = printer.toString();
		StringBuffer classBuffer = new StringBuffer(classBody.length() + 100);
		classBuffer.append(ctClass.getPackage()).append(classBody);


		BasicCompilationUnit unit = new BasicCompilationUnit(
				classBuffer.toString().toCharArray(), pack, ctClass.getSimpleName()
				+ ".java");
	 */
	
}
