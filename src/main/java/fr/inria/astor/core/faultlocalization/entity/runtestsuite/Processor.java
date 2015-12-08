package fr.inria.astor.core.faultlocalization.entity.runtestsuite;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class Processor {

	static final int CLASS_SUFFIX_LENGTH = ".class".length();
	static final int JAVA_SUFFIX_LENGTH = ".java".length();
	
	private final ClassFilter tester;
	private final ClassFinder finder;

	public Processor(ClassFinder finder, ClassFilter tester) {
		this.tester = tester;
		this.finder = finder;
	}
	
	public Class<?>[] process() {
		List<Class<?>> classes = new ArrayList<>();
		for (String fileName : finder.getClasses()) {
			String className;
			if(isJavaFile(fileName)){
				className = classNameFromJava(fileName);
			}else
			if(isClassFile(fileName)) {
				className = classNameFromFile(fileName);
			}else continue;
			if (!tester.acceptClassName(className)) {
				continue;
			}
			if (!tester.acceptInnerClass() && isInnerClass(className)) {
				continue;
			}
			if(!className.contains("$"))
			try {
				Class<?> clazz = Class.forName(className);
				if (clazz.isLocalClass() || clazz.isAnonymousClass()) {
					continue;
				}
				if (tester.acceptClass(clazz)) {
					classes.add(clazz);
				}
			} catch (ClassNotFoundException cnfe) {
				try {
					ClassLoader tmp= Thread.currentThread().getContextClassLoader();
					Class<?> clazz = Class.forName(className,false,tmp);
					if (clazz.isLocalClass() || clazz.isAnonymousClass()) {
						continue;
					}
					if (tester.acceptClass(clazz)) {
						classes.add(clazz);
					}
				} catch (ClassNotFoundException cnfe2) {
					cnfe2.printStackTrace();
				} catch (NoClassDefFoundError ncdfe) {
					// ignore not instantiable classes
				}
			} catch (NoClassDefFoundError ncdfe) {
				// ignore not instantiable classes
			}
		}
		
		Collections.sort(classes, new Comparator<Class<?>>() {
			public int compare(Class<?> o1, Class<?> o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		return classes.toArray(new Class[0]);
	}
	
	
	private String classNameFromJava(String fileName) {
		String s = replaceFileSeparators(cutOffExtension(fileName,JAVA_SUFFIX_LENGTH));
		while (s.startsWith("."))
			s= s.substring(1);
		return s;
	}

	private boolean isJavaFile(String fileName) {
		return fileName.endsWith(".java");
	}

	private boolean isInnerClass(String className) {
		return className.contains("$");
	}

	private boolean isClassFile(String classFileName) {
		return classFileName.endsWith(".class");
	}

	private String classNameFromFile(String classFileName) {
		String s = replaceFileSeparators(cutOffExtension(classFileName,CLASS_SUFFIX_LENGTH));
		while (s.startsWith("."))
			s= s.substring(1);
		return s;
	}

	private String replaceFileSeparators(String s) {
		String result = s.replace(File.separatorChar, '.');
		if (File.separatorChar != '/') {
			result = result.replace('/', '.');
		}
		return result;
	}

	private String cutOffExtension(String classFileName, int length) {
		return classFileName.substring(0, classFileName.length()
				- length);
	}
	
}
