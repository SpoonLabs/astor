package fr.inria.astor.core.manipulation.bytecode.compiler.tools;

import java.net.URI;

import javax.tools.SimpleJavaFileObject;



@SuppressWarnings("restriction")
public class SourceCodeFileObject extends SimpleJavaFileObject {

	private String sourceContent;
	
	public SourceCodeFileObject(String simpleClassName, String sourceContent) {
		super(URI.create((simpleClassName + Kind.SOURCE.extension)), Kind.SOURCE);
		this.sourceContent = sourceContent;
	}
	
	@Override
	public CharSequence getCharContent(boolean ignoreEncodingErrors) {
		return sourceContent;
	}
	

}
