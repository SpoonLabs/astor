package fr.inria.astor.core.manipulation.bytecode.compiler.tools;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.StandardLocation;



@SuppressWarnings("restriction")
public class VirtualFileObjectManager extends ForwardingJavaFileManager<JavaFileManager> {

	private Map<URI, SourceCodeFileObject> sourceFiles;
	private Map<String, CompiledObjectFileObject> classFiles;
	
	public VirtualFileObjectManager(JavaFileManager fileManager) {
		super(fileManager);
		classFiles = new HashMap<>();
		sourceFiles = new HashMap<>();
	}
	
	@Override
	public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
		URI fileURI = uriFor(location, packageName, relativeName);
		if (containsSourceFileFor(fileURI)) {
			return sourceFile(fileURI);
		}
		return super.getFileForInput(location, packageName, relativeName);
	}
	
	@Override
	public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind, FileObject outputFile) throws IOException {
		CompiledObjectFileObject classFile = new CompiledObjectFileObject(qualifiedName, kind);
		classFiles().put(qualifiedName, classFile);
		return classFile;
	}
	
	@Override
	public String inferBinaryName(Location location, JavaFileObject file) {
		if (SourceCodeFileObject.class.isInstance(file) || CompiledObjectFileObject.class.isInstance(file)) {
			return file.getName();
		}
		return super.inferBinaryName(location, file);
	}
	
	@Override
	public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse) throws IOException {
		Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);
		List<JavaFileObject> files = new ArrayList<>();//MetaList.newLinkedList();
		if (location == StandardLocation.CLASS_PATH && kinds.contains(JavaFileObject.Kind.CLASS)) {
			for (JavaFileObject file : sourceFiles().values()) {
				if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
					files.add(file);
				}
			}
			files.addAll(classFiles().values());
		} else if (location == StandardLocation.SOURCE_PATH && kinds.contains(JavaFileObject.Kind.SOURCE)) {
			for (JavaFileObject file : sourceFiles().values()) {
				if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
					files.add(file);
				}
			}
		}
		//MetaCollection.addAll(files, result);
		this.addAll(files,result);
		return files;
	}

	public static <T> boolean addAll(Collection<T> destination, Iterable<? extends T> elements) {
		boolean changed = false;
		for (T element : elements) {
			changed |= destination.add(element);
		}
		return changed;
	}
	
	public void addCompiledClasses(Map<String, byte[]> compiledClasses) {
		for (String qualifiedName : compiledClasses.keySet()) {
			classFiles().put(qualifiedName, new CompiledObjectFileObject(qualifiedName, Kind.CLASS, compiledClasses.get(qualifiedName)));
		}
	}
	
	public void addSourceFile(Location location, String packageName, String simpleClassName, SourceCodeFileObject sourceFile) {
		URI fileURI = uriFor(location, packageName, simpleClassName);
		sourceFiles().put(fileURI, sourceFile);
	}
	
	public int numberOfSourceFiles() {
		return sourceFiles().size();
	}
	
	public boolean containsSourceFileFor(URI fileURI) {
		return sourceFiles().containsKey(fileURI);
	}
	
	public SourceCodeFileObject sourceFile(URI fileURI) {
		return sourceFiles().get(fileURI);
	}
	
	public int numberOfClassFiles() {
		return classFiles().size();
	}
	
	public boolean containsClassFileFor(String qualifiedName) {
		return classFiles().containsKey(qualifiedName);
	}
	
	public CompiledObjectFileObject classFile(String qualifiedName) {
		return classFiles().get(qualifiedName);
	}
	
	private URI uriFor(Location location, String packageName, String simpleClassName) {
		String uriScheme = location.getName() + '/' + packageName + '/' + simpleClassName + ".java";
		return URI.create(uriScheme);
	}
	
	public Map<String, CompiledObjectFileObject> classFiles() {
		return classFiles;
	}
	
	public Map<URI, SourceCodeFileObject> sourceFiles() {
		return sourceFiles;
	}
	

}
