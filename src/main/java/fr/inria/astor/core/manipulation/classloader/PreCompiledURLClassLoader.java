package fr.inria.astor.core.manipulation.classloader;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.eclipse.jdt.internal.compiler.ClassFile;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class PreCompiledURLClassLoader extends URLClassLoader {


		
/**
 * 
 * @param urls
 * @param variant
 * @param compiler
 */
	public PreCompiledURLClassLoader(URL[] urls, List<ClassFile> compilationResult) {
		super(urls);
		for (ClassFile f : compilationResult) {
			String name = new String(f.fileName()).replace('/', '.');
			Class<?> cl = this.defineClass(name, f.getBytes(), 0, f.getBytes().length);
			this.resolveClass(cl);
		
		}
		
	}

}
