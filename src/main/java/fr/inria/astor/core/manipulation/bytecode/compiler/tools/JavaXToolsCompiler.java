package fr.inria.astor.core.manipulation.bytecode.compiler.tools;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;

@SuppressWarnings("restriction")
public class JavaXToolsCompiler {

	private List<String> options;
	private JavaCompiler compiler;
	private VirtualFileObjectManager fileManager;
	private DiagnosticCollector<JavaFileObject> diagnostics;

	public JavaXToolsCompiler() {
		options = asList("-nowarn");
		compiler = ToolProvider.getSystemJavaCompiler();
		diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager standardFileManager = compiler().getStandardFileManager(diagnostics(), null, null);
		fileManager = new VirtualFileObjectManager(standardFileManager);
	}

	public synchronized CompilationResult javaBytecodeFor(Map<String, String> qualifiedNameAndContent,
			Map<String, byte[]> compiledDependencies, List<String> options) {
		diagnostics = new DiagnosticCollector<JavaFileObject>();
		fileManager.classFiles().clear();
		
		Collection<JavaFileObject> units = addCompilationUnits(qualifiedNameAndContent);
		fileManager.addCompiledClasses(compiledDependencies);
		CompilationTask task = compiler().getTask(null, fileManager, diagnostics(), options, null, units);
		runCompilationTask(task);
		Map<String, byte[]> bytecodes = collectBytecodes(qualifiedNameAndContent);
		List<String> errors = new ArrayList<>();
		copyErrors(errors, diagnostics);
		CompilationResult cr = new CompilationResult(bytecodes, errors);

		return cr;
	}

	private void copyErrors(List<String> errors, DiagnosticCollector<JavaFileObject> diagnostics2) {
		for (Diagnostic d : diagnostics2.getDiagnostics()) {
			if (d.getKind() == Kind.ERROR || d.getKind() == Kind.MANDATORY_WARNING) {
				errors.add(d.toString());
			}
		}
	}

	protected Collection<JavaFileObject> addCompilationUnits(Map<String, String> qualifiedNameAndContent) {
		Collection<JavaFileObject> units = new ArrayList<>();
		for (String qualifiedName : qualifiedNameAndContent.keySet()) {
			String sourceContent = qualifiedNameAndContent.get(qualifiedName);
			JavaFileObject sourceFile = addCompilationUnit(qualifiedName, sourceContent);
			units.add(sourceFile);
		}
		return units;
	}

	protected JavaFileObject addCompilationUnit(String qualifiedName, String sourceContent) {
		String simpleClassName = StringLibrary.lastAfterSplit(qualifiedName, '.');
		String packageName = stripEnd(qualifiedName, '.' + simpleClassName);
		SourceCodeFileObject sourceFile = new SourceCodeFileObject(simpleClassName, sourceContent);//This is the original statement
		fileManager.addSourceFile(StandardLocation.SOURCE_PATH, packageName, simpleClassName, sourceFile);
		return sourceFile;
	}

	protected boolean runCompilationTask(CompilationTask task) {
		boolean success = task.call();
		if (!success) {
			Collection<String> errors = new ArrayList<>();
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics().getDiagnostics()) {
				errors.add(diagnostic.toString());
			}
		}
		return success;
	}

	private Map<String, byte[]> collectBytecodes(Map<String, String> qualifiedNameAndContent) {
		Map<String, byte[]> bytecodes = new HashMap<>();
		Map<String, CompiledObjectFileObject> classFiles = fileManager.classFiles();
		for (String qualifiedName : classFiles.keySet()) {
			String topClassName = topClassName(qualifiedName);
			if (qualifiedNameAndContent.containsKey(topClassName)) {
				bytecodes.put(qualifiedName, classFiles.get(qualifiedName).byteCodes());
			}
		}
		return bytecodes;
	}

	
	private String topClassName(String qualifiedName) {
		return qualifiedName.split("[$]")[0];
	}

	private List<String> options() {
		return options;
	}

	private JavaCompiler compiler() {
		return compiler;
	}

	private DiagnosticCollector<JavaFileObject> diagnostics() {
		return diagnostics;
	}

	public static String stripEnd(String string, String suffix) {
		if (string.endsWith(suffix)) {
			return string.substring(0, string.length() - suffix.length());
		}
		return string;
	}

}