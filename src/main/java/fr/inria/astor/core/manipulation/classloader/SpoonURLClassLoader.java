package fr.inria.astor.core.manipulation.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.jdt.internal.compiler.ClassFile;

import spoon.reflect.declaration.CtClass;
import fr.inria.astor.core.manipulation.compiler.bytecode.ProgramVariantCompilationResult;
import fr.inria.astor.core.manipulation.compiler.bytecode.SpoonClassCompiler;

/**
 * Class loader used to load spoon classes.
 * It is related to one Program Variant. 
 * The difference with the existing one is:
 * 
 * - Load only the class passed as parameter from the model, but not all the spoon model. 
 * - Extends URL_CL, so, the classes not presented in the spoon model are taken from the URL and loaded by the parent.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class SpoonURLClassLoader extends URLClassLoader {

	Logger logger = Logger.getLogger(SpoonURLClassLoader.class.getName());

		
	private SpoonClassCompiler compiler = null;
	/**
	 * Map of cached ct classes.
	 */
	private Map<String, CtClass> builtSpoonCtClasses;
	
	/**
	 * Map of loaded classes.
	 */
	private Map<String, Class> loadedFromModel = new HashMap<String, Class>(); 

	
	public SpoonURLClassLoader(URL[] urls, Map<String, CtClass> builtSpoonCtClasses, SpoonClassCompiler compiler) {
		super(urls);
		
		this.compiler = compiler;
		this.builtSpoonCtClasses = builtSpoonCtClasses;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<?> loadClass(String classname) throws ClassNotFoundException {
		// MODIFIFY NAME
		Map<String, CtClass> classesofVariant = this.builtSpoonCtClasses;//variant.getBuiltClasses();
		CtClass ctCl = classesofVariant.get(classname);
		if (ctCl == null) {
		//	logger.info(name + " not has a Spoon Representation, request to parent URLClassLoader");
			return super.loadClass(classname);
		} else {
			logger.debug(classname + " has a spoon representation");
			//if it's in cache of loaded spoon classes, return it
			if(loadedFromModel.containsKey(classname)){
				logger.debug(classname +" previously loaded");
				return loadedFromModel.get(classname);
			}
			Class classloader = compileAndLoad(ctCl);
			return classloader;
		}
	}
/**
 * 
 * @param compiler
 * @param ctCl
 * @return
 * @throws ClassNotFoundException
 */
	@SuppressWarnings("rawtypes")
	public Class compileAndLoad(CtClass ctCl) throws ClassNotFoundException {
		//
		ProgramVariantCompilationResult comp = compiler.compileOnMemory(ctCl);//compiler.compileSpoonClassElement(ctCl);
		if(!comp.getErrors().isEmpty()){
			logger.info("Compilation failing of "+comp.getCompiledCtType().getQualifiedName()+", error "+comp.getErrors());
		}	
		Class classloader = load(comp);
		return classloader;
	}
	
	public Class load(ProgramVariantCompilationResult comp) throws ClassNotFoundException {
		String name = comp.getCompiledCtType().getQualifiedName();
		
		Map<String, Class> loadedFromModel = loadClassesOfCompilation(comp);
		this.loadedFromModel.putAll(loadedFromModel);
		Class classloader = loadedFromModel.get(name);
		if (classloader == null) {
			throw new ClassNotFoundException(name);
		}
		return classloader;
	}

	public Map<String, Class> loadClassesOfCompilation(ProgramVariantCompilationResult comp) {
		
		Map<String, Class> results = new HashMap<String, Class>();
		
		return loadClassesOfCompilation(comp.getCompilationList()); 
	
	}

	public Map<String, Class> loadClassesOfCompilation(List<ClassFile>  compilationResult) {
		
		Map<String, Class> results = new HashMap<String, Class>();
		
		for (ClassFile f : compilationResult) {
			String name = new String(f.fileName()).replace('/', '.');
			logger.debug("load "+name +", prev "+loadedFromModel.keySet() );
			Class<?> cl = this.defineClass(name, f.getBytes(), 0, f.getBytes().length);
			results.put(name, cl);
		}
		return results;
	}
}
