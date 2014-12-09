package fr.inria.astor.test.repair.evaluation;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.junit.Test;

import fr.inria.astor.core.manipulation.compiler.bytecode.CompiledObjectFileObject;
import fr.inria.astor.core.manipulation.compiler.bytecode.VirtualFileObjectManager;
 
/**
 * A test class to test dynamic compilation API.
 * http://www.accordess.com/wpblog/an-overview-of-java-compilation-api-jsr-199/
 */
public class CompilerAPITest {
    final Logger logger = Logger.getLogger(CompilerAPITest.class.getName()) ;
 
    /**Java source code to be compiled dynamically*/
    static String sourceCode = "package com.accordess.ca;" +
        "class DynamicCompilationHelloWorld{" +
            "public static void main (String args[]){" +
            "System.out.println (\"Hello, dynamic compilation world!\");" +
            "}" +
        "}" ;
 
    /**
     * Does the required object initialization and compilation.
     */
    public void doCompilation (){
        /*Creating dynamic java source code file object*/
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject ("com.accordess.ca.DynamicCompilationHelloWorld", sourceCode) ;
        JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
 
        /*Instantiating the java compiler*/
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        /**
         * Retrieving the standard file manager from compiler object, which is used to provide
         * basic building block for customizing how a compiler reads and writes to files.
         *
         * The same file manager can be reopened for another compiler task.
         * Thus we reduce the overhead of scanning through file system and jar files each time
         */
        VirtualFileObjectManager stdFileManager = new VirtualFileObjectManager(compiler.getStandardFileManager(null, Locale.getDefault(), null));
        		//compiler.getStandardFileManager(null, Locale.getDefault(), null);

        /* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
 
        /*Prepare any compilation options to be used during compilation*/
        //In this example, we are asking the compiler to place the output files under bin folder.
        String[] compileOptions = new String[]{"-d", "outputMutation"} ;
        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
 
        /*Create a diagnostic controller, which holds the compilation problems*/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
 
        /*Create a compilation task from compiler by passing in the required input objects prepared above*/
        CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, compilationUnits) ;
      
        //Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();
 
        if (!status){//If compilation error occurs
            /*Iterate through each compilation problem and print it*/
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
                System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
            }
        }
        Map<String, byte[]> result = returnByteCode(stdFileManager.classFiles());
        System.out.println(result);
        try {
            stdFileManager.close() ;//Close the file manager
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public Map<String, byte[]> returnByteCode(Map<String, CompiledObjectFileObject> vc){
    
    	Map<String, byte[]> bytecodes = new HashMap<String, byte[]>();
		Map<String, CompiledObjectFileObject> classFiles = vc;
		for (String qualifiedName : classFiles.keySet()) {
			String topClassName = qualifiedName.split("[$]")[0];
			if (vc.containsKey(topClassName)) {
				bytecodes.put(qualifiedName, classFiles.get(qualifiedName).byteCodes());
			}
		}
		return bytecodes;
    } 
    /*
     * Map<String, byte[]> bytecodes = MetaMap.newHashMap();
		Map<String, VirtualClassFileObject> classFiles = fileManager().classFiles();
		for (String qualifiedName : classFiles.keySet()) {
			String topClassName = topClassName(qualifiedName);
			if (qualifiedNameAndContent.containsKey(topClassName)) {
				bytecodes.put(qualifiedName, classFiles.get(qualifiedName).byteCodes());
			}
		}*/
    
    /**
     * Does the required object initialization and compilation.
     */
    public void doCompilationOriginal (){
        /*Creating dynamic java source code file object*/
        SimpleJavaFileObject fileObject = new DynamicJavaSourceCodeObject ("com.accordess.ca.DynamicCompilationHelloWorld", sourceCode) ;
        JavaFileObject javaFileObjects[] = new JavaFileObject[]{fileObject} ;
 
        /*Instantiating the java compiler*/
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        /**
         * Retrieving the standard file manager from compiler object, which is used to provide
         * basic building block for customizing how a compiler reads and writes to files.
         *
         * The same file manager can be reopened for another compiler task.
         * Thus we reduce the overhead of scanning through file system and jar files each time
         */
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, Locale.getDefault(), null);
 
        /* Prepare a list of compilation units (java source code file objects) to input to compilation task*/
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(javaFileObjects);
 
        /*Prepare any compilation options to be used during compilation*/
        //In this example, we are asking the compiler to place the output files under bin folder.
        String[] compileOptions = new String[]{"-d", "outputMutation"} ;
        Iterable<String> compilationOptionss = Arrays.asList(compileOptions);
 
        /*Create a diagnostic controller, which holds the compilation problems*/
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
 
        /*Create a compilation task from compiler by passing in the required input objects prepared above*/
        CompilationTask compilerTask = compiler.getTask(null, stdFileManager, diagnostics, compilationOptionss, null, compilationUnits) ;
      
        //Perform the compilation by calling the call method on compilerTask object.
        boolean status = compilerTask.call();
 
        if (!status){//If compilation error occurs
            /*Iterate through each compilation problem and print it*/
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()){
                System.out.format("Error on line %d in %s", diagnostic.getLineNumber(), diagnostic);
            }
        }
        try {
            stdFileManager.close() ;//Close the file manager
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testCompilerAPI(){
        new CompilerAPITest ().doCompilation() ;
    }
 
}
 
/**
 * Creates a dynamic source code file object
 *
 * This is an example of how we can prepare a dynamic java source code for compilation.
 * This class reads the java code from a string and prepares a JavaFileObject
 *
 */
class DynamicJavaSourceCodeObject extends SimpleJavaFileObject{
    private String qualifiedName ;
    private String sourceCode ;
 
    /**
     * Converts the name to an URI, as that is the format expected by JavaFileObject
     *
     *
     * @param fully qualified name given to the class file
     * @param code the source code string
     */
    protected DynamicJavaSourceCodeObject(String name, String code) {
        super(URI.create("string:///" +name.replaceAll("\\.", "/") + Kind.SOURCE.extension), Kind.SOURCE);
        this.qualifiedName = name ;
        this.sourceCode = code ;
    }
 
    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors)
            throws IOException {
        return sourceCode ;
    }
 
    public String getQualifiedName() {
        return qualifiedName;
    }
 
    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
 
    public String getSourceCode() {
        return sourceCode;
    }
 
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
}
