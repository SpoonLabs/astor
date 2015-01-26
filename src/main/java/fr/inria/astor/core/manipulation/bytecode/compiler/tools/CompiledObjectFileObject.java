package fr.inria.astor.core.manipulation.bytecode.compiler.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;



@SuppressWarnings("restriction")
public class CompiledObjectFileObject extends SimpleJavaFileObject {

	protected ByteArrayOutputStream byteCodes;
	
	public CompiledObjectFileObject(String qualifiedName, Kind kind) {
		super(URI.create(qualifiedName), kind);
	}
	
	public CompiledObjectFileObject(String qualifiedName, Kind kind, byte[] bytes) {
		this(qualifiedName, kind);
		setBytecodes(bytes);
	}
	
	@Override
	public InputStream openInputStream() {
		return new ByteArrayInputStream(byteCodes());
	}

	@Override
	public OutputStream openOutputStream() {
      byteCodes = new ByteArrayOutputStream();
      return byteCodes;
	}
	
	private void setBytecodes(byte[] bytes) {
		try {
			openOutputStream().write(bytes);
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}
	
	public byte[] byteCodes() {
		return byteCodes.toByteArray();
	}
	
	
}
