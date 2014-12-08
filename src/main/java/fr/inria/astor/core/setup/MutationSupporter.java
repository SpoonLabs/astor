package fr.inria.astor.core.setup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import spoon.OutputType;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.reflect.declaration.CtElementImpl;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.compiler.bytecode.ProgramVariantCompilationResult;
import fr.inria.astor.core.manipulation.compiler.bytecode.SpoonClassCompiler;

/**
 * This class carries out all supporter task: e.g. creation of directories, copy
 * dirs, calls to compilations.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MutationSupporter {

	public static String DEFAULT_ORIGINAL_VARIANT = "default";

	// private Logger logger =
	// Logger.getLogger(MutationSupporter.class.getName());
	private Logger logger = Logger.getLogger(Thread.currentThread().getName());

	private int mutationId = -1;

	// COMPILERS
	private SpoonClassCompiler spoonClassCompiler = null;
	private JDTBasedSpoonCompiler jdtCompiler = null;
	//
	Factory factory;
//
	/**
	 * Receives the properties and set up the dirs.
	 * 
	 * @param properties
	 */
	public MutationSupporter(Factory factory) {
		super();
		this.factory = factory;
		jdtCompiler = new JDTBasedSpoonCompiler(factory);
		spoonClassCompiler = new SpoonClassCompiler(factory);
	}




	public String currentMutatorIdentifier() {
		return (getMutationId() >= 0)? ( "variant-" + getMutationId()) : DEFAULT_ORIGINAL_VARIANT;
	}

	public void buildModel(String srcPathToBuild) {

		// JDTBasedSpoonCompiler compiler = new
		// JDTBasedSpoonCompiler(Factory.getLauchingFactory());

		logger.info("building model: " + srcPathToBuild);
		try {
			jdtCompiler.addInputSource(new File(srcPathToBuild));
			jdtCompiler.build();
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public void buildModel(String srcPathToBuild, String classpath) {

		// JDTBasedSpoonCompiler compiler = new
		// JDTBasedSpoonCompiler(Factory.getLauchingFactory());

		logger.info("building model: " + srcPathToBuild);
		try {
			jdtCompiler.addInputSource(new File(srcPathToBuild));
			jdtCompiler.setOutputDirectory(new File(srcPathToBuild));
			jdtCompiler.setSourceClasspath(classpath);
			jdtCompiler.build();
			jdtCompiler.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);//compile();
			
		
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}
	
	

	/**
	 * Load the class name TODO TO BE MODIFIED
	 * 
	 * @param classname
	 * @return
	 */
	public Class loadInNewThread(URL[] cp, String classname) {
		createClassLoadInCurrentThread(cp);
		return loadInCurrentThread(classname);
	}

	public void createClassLoadInCurrentThread(URL[] cp) {
		URLClassLoader cl = new URLClassLoader(cp);
		Thread.currentThread().setContextClassLoader(cl);
	}

	public Class loadInCurrentThread(String className) {
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class loadedclass = loader.loadClass(className);
			return loadedclass;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Saves Java File and Compiles it The Program Variant as well as the rest
	 * of the project is saved on disk. Not any more: Additionally, the compiled
	 * class are saved it on disk. Finally, the current Thread has a reference
	 * to a class loader with the ProgramVariant
	 * 
	 * @param instance
	 * @throws Exception
	 */
	public void saveSourceCodeOnDiskProgramVariant(ProgramVariant instance, String srcOutput) throws Exception {
		// this.createClassLoadInCurrentThread(cp);
		this.getSpoonClassCompiler().updateOutput(srcOutput);// mmm
		// /

		// As the compilation compiles the original model, we force to
		// recompile the cloned

		for (CtClass ctclass : instance.getBuiltClasses().values()) {
			// boolean compiles = this.compile(ctclass);
			this.generateSourceCodeFromCtClass(ctclass);

		}

	}

	/**
	 * Compiles the model, and load the bytecode in a ClassLoader. Finally, the
	 * former is associated to the Current Thread
	 * 
	 * @param instance
	 * @throws Exception
	 */
	public ProgramVariantCompilationResult compileOnMemoryProgramVariant(ProgramVariant instance, URL[] cp)
			throws Exception {
		boolean compilationResult = true;
		// --
		if (spoonClassCompiler == null) {
			throw new IllegalArgumentException("Spoon compiler must be initialized");
		}
		

		try {

			List<CtClass> ctClasses = new ArrayList<CtClass>(instance.getBuiltClasses().values());
			//addExceptionClasses(ctClasses);

			ProgramVariantCompilationResult compilation = spoonClassCompiler.compileOnMemory(ctClasses);
			logger.info("compilation result " + compilation.compiles() + " " + (compilation.getErrors()));
			if (!compilation.compiles()) {
				compilationResult = false;
			}
			//
			
			//
			
			return compilation;

		} catch (Exception ex) {
			ex.printStackTrace();
			logger.error("Problems to save the mutated model: " + ex.getMessage());
			// return false;
			return null;
		}
		// return compilationResult;
	}

	public fr.inria.astor.core.manipulation.compiler.bytecode.CompilationResult compileOnMemoryProgramVariantWithTools(ProgramVariant instance, URL[] cp)
	{
		List<CtClass> ctClasses = new ArrayList<CtClass>(instance.getBuiltClasses().values());
	
	
		fr.inria.astor.core.manipulation.compiler.bytecode.CompilationResult compilation2 = spoonClassCompiler.compileOnMemory2(ctClasses,cp);
	
	return compilation2;
	}
	
	/**
	 * Compile and save on disk Source code file and bytecode Should be
	 * configured before: .getSpoonClassCompiler().updateOutput(srcOutput);
	 * 
	 * @param type
	 * @return
	 */
	@Deprecated
	public boolean compile(CtSimpleType<?> type) {

		// ProgramVariantCompilationResult compResult =
		// this.generateSourceCodeFromCtClass((CtClass) type);

		return false;// compResult.getErrors().isEmpty();
	}

	@Deprecated
	public boolean compile(CtSimpleType<?> type, String output) {

		this.getSpoonClassCompiler().updateOutput(output);
		// ProgramVariantCompilationResult compResult =
		// this.generateSourceCodeFromCtClass((CtClass) type);

		return false;// compResult.getErrors().isEmpty();
	}

	/**
	 * Save on disk Source code file Should be configured before:
	 * .getSpoonClassCompiler().updateOutput(srcOutput);
	 * 
	 * @param type
	 * @return
	 */
	public void generateSourceCodeFromCtClass(CtSimpleType<?> type, String output) {
		this.spoonClassCompiler.updateOutput(output);
		generateSourceCodeFromCtClass(type);
	}

	/**
	 * Save on disk Source code file Should be configured before:
	 * .getSpoonClassCompiler().updateOutput(srcOutput);
	 * 
	 * @param type
	 * @return
	 */
	public void generateSourceCodeFromCtClass(CtSimpleType<?> type) {
		// WorkArround, for cloned
		SourcePosition sp = type.getPosition();
		type.setPosition(null);
		// SpoonClassCompiler compiler = new SpoonClassCompiler(fileOutput);
		if (spoonClassCompiler == null || spoonClassCompiler.getJavaPrinter() == null) {
			throw new IllegalArgumentException("Spoon compiler must be initialized");
		}
		// ProgramVariantCompilationResult compResult =
		// spoonClassCompiler.compileSpoonClassElement((CtClass) type);
		spoonClassCompiler.saveSourceCode((CtClass) type);

		// --
		// End Workarround
		type.setPosition(sp);

	}

	/**
	 * Saves the Source and Bytecode on disk
	 * 
	 * @param srcPathToBuild
	 * @param binOutputPath
	 */
	@Deprecated
	public void compileAndSaveFromSpoonModel(String srcPathToBuild, String binOutputPath) {

		logger.info("building model: " + srcPathToBuild);
		try {
			jdtCompiler.addInputSource(new File(srcPathToBuild));
			jdtCompiler.setDestinationDirectory(new File(binOutputPath));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// New: this invocation was included in the only method that called this
		// one i.e. addSourcePath
		jdtCompiler.compile();
		logger.info("End compilation: " + srcPathToBuild + " at " + binOutputPath);
	}
	


	public CtElement getRoot(CtElement e) {
		if (e.getParent() == null)
			return e;
		else
			return getRoot(e.getParent());
	}


	public int getMutationId() {
		return mutationId;
	}

	public void setMutationId(int mutationId) {
		this.mutationId = mutationId;
	}

	public CtClass getCtClass(Class classref) {
		// String className = candidate.getClassName();
		// TODO: MODIFY TO BUILD ONLY THE CANDIDATE CLASS (Instead of previously
		// create it)
		CtSimpleType ct = mirror(classref);
		CtClass ctclass = (CtClass) ct;
		return ctclass;
	}

	/**
	 * @return the class with the given qualified name.
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> load(String binDirPath, String qualifiedName) {
		try {
			URL url = new File(binDirPath/* modelBinDir */).toURI().toURL();
			URLClassLoader cl = new URLClassLoader(new URL[] { url });
			Thread.currentThread().setContextClassLoader(cl);
			return (Class<T>) (cl.loadClass(qualifiedName));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public <T> Class<T> loadInCurrent(String qualifiedName) {

		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			return (Class<T>) (cl.loadClass(qualifiedName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @return the reflective instance of the class with the given qualified
	 *         name
	 */
	public <T> CtSimpleType<T> mirror(String binDirPath, String qualifiedName) {
		Class<T> clazz = load(binDirPath, qualifiedName);
		return mirror(clazz);
	}

	/**
	 * @return the reflective instance of the given class
	 */
	public <T> CtSimpleType<T> mirror(Class<T> clazz) {
		return factory.Type().get(clazz);
	}

	public SpoonClassCompiler getSpoonClassCompiler() {
		return this.spoonClassCompiler;
	}



	public Factory getFactory() {
		return factory;
	}

	public Map<String, CtClass> getBuiltCtClasses() {
		Map<String, CtClass> result = new HashMap<String, CtClass>();
		List<CtSimpleType<?>> ct = factory.Class().getAll();
		for (CtSimpleType<?> ctSimpleType : ct) {
			if (ctSimpleType instanceof CtClass) {
				result.put(ctSimpleType.getQualifiedName(), (CtClass) ctSimpleType);
			}
		}
		return result;
	}

	public void saveSolution(ProgramVariant childVariant, String srcOutput, int generation) {
		try {
			Map<String, Integer> result = new HashMap<String, Integer>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document root = dBuilder.newDocument();
			Element rootElement = root.createElement("patch");
			root.appendChild(rootElement);

			for (int i = 1; i <= generation; i++) {
				List<GenOperationInstance> genOperationInstances = childVariant.getOperations().get(i);
				if (genOperationInstances == null)
					continue;

				for (GenOperationInstance genOperationInstance : genOperationInstances) {

					Element op = root.createElement("operation");
					rootElement.appendChild(op);

					Attr attr_location = root.createAttribute("location");
					attr_location.setValue(genOperationInstance.getGen().getCtClass().getQualifiedName());
					op.setAttributeNode(attr_location);
					
					if(genOperationInstance.getGen() instanceof GenSuspicious){
						GenSuspicious gs = (GenSuspicious) genOperationInstance.getGen();
						int line = gs.getSuspicious().getLineNumber();
						Attr attr_line = root.createAttribute("line");
						attr_line.setValue(Integer.toString(line));
						op.setAttributeNode(attr_line);
					}
					
					Attr attr_gen = root.createAttribute("generation");
					attr_gen.setValue(Integer.toString(i));
					op.setAttributeNode(attr_gen);

					Element original = root.createElement("original");
					op.appendChild(original);
					original.setTextContent(genOperationInstance.getOriginal().toString());

					Element mod = root.createElement("modified");
					op.appendChild(mod);
					mod.setTextContent(genOperationInstance.getModified().toString());

				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(root);
			// StreamResult result1 = new StreamResult(System.out);
			StreamResult result1 = new StreamResult(new File(srcOutput + File.separator + "patch.xml"));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			// ----

			transformer.transform(source, result1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static CtCodeElement clone(CtCodeElement st){
		CtCodeElement cloned =  FactoryImpl.getLauchingFactory().Core().clone(st);
		//--
		cloned.setParent(ROOT_ELEMENT);
	
		return cloned;
	}
	
	public static final CtElement ROOT_ELEMENT = new CtElementImpl() {
		private static final long serialVersionUID = 1L;

		public void accept(spoon.reflect.visitor.CtVisitor visitor) {
		}

		@Override
		public CtElement getParent() throws ParentNotInitializedException {
			return null;
		};
		
	};
}
