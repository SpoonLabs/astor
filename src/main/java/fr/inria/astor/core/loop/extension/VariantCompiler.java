package fr.inria.astor.core.loop.extension;

import java.net.URL;
import java.util.Collection;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import spoon.reflect.declaration.CtClass;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface VariantCompiler extends AstorExtensionPoint {

	public CompilationResult compile(ProgramVariant instanceToCompile, URL[] classpath) ;

	public CompilationResult compile(Collection<CtClass> classesToCompile, URL[] urlArray);
		
}
