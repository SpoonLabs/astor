package fr.inria.astor.core.manipulation;

import java.io.File;
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
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;
import spoon.support.reflect.declaration.CtElementImpl;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.bytecode.compiler.SpoonClassCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;

/**
 * This class carries out all supporter task: e.g. creation of directories, copy
 * dirs, calls to compilations.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class MutationSupporter {

	public static MutationSupporter currentSupporter = null;

	private Logger logger = Logger.getLogger(Thread.currentThread().getName());

	/**
	 * 
	 */
	private SpoonClassCompiler spoonClassCompiler = null;

	/**
	 * Spoon model generator
	 */
	private JDTBasedSpoonCompiler jdtSpoonModelBuilder = null;

	public static Factory factory;

	/**
	 * Receives the properties and set up the dirs.
	 * 
	 * @param properties
	 */
	public MutationSupporter(Factory factory) {
		super();
		this.factory = factory;
		spoonClassCompiler = new SpoonClassCompiler(factory);
		this.currentSupporter = this;
	}

	public void buildModel(String srcPathToBuild, String[] classpath) {

		logger.info("building model: " + srcPathToBuild + ", compliance level: "
				+ factory.getEnvironment().getComplianceLevel());
		jdtSpoonModelBuilder = new JDTBasedSpoonCompiler(factory);
		jdtSpoonModelBuilder.addInputSource(new File(srcPathToBuild));
		jdtSpoonModelBuilder.setOutputDirectory(new File(srcPathToBuild));
		jdtSpoonModelBuilder.setSourceClasspath(classpath);
		jdtSpoonModelBuilder.build();
		jdtSpoonModelBuilder.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);

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
		// Set up the dir where we save the generated output
		this.getSpoonClassCompiler().updateOutput(srcOutput);

		// For each class contemplated for the program variant,
		for (CtClass ctclass : instance.getBuiltClasses().values()) {
			this.generateSourceCodeFromCtClass(ctclass);
		}

	}

	public CompilationResult compileOnMemoryProgramVariant(ProgramVariant instance, URL[] cp) {
		List<CtClass> ctClasses = new ArrayList<CtClass>(instance.getBuiltClasses().values());
		CompilationResult compilation2 = spoonClassCompiler.compileOnMemory(ctClasses, cp);

		return compilation2;
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

	public CtElement getRoot(CtElement e) {
		if (e.getParent() == null)
			return e;
		else
			return getRoot(e.getParent());
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

	public static Factory getFactory() {
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

	public void saveSolutionData(ProgramVariant childVariant, String srcOutput, int generation) {
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

					if (genOperationInstance.getGen() instanceof GenSuspicious) {
						GenSuspicious gs = (GenSuspicious) genOperationInstance.getGen();
						int line = gs.getSuspicious().getLineNumber();
						Attr attr_line = root.createAttribute("line");
						attr_line.setValue(Integer.toString(line));
						op.setAttributeNode(attr_line);
					}

					Attr attr_gen = root.createAttribute("generation");
					attr_gen.setValue(Integer.toString(i));
					op.setAttributeNode(attr_gen);

					Attr attr_type = root.createAttribute("type");
					attr_type.setValue(genOperationInstance.getOperationApplied().toString());
					op.setAttributeNode(attr_type);

					Element original = root.createElement("original");
					op.appendChild(original);
					original.setTextContent(genOperationInstance.getOriginal().toString());

					Element mod = root.createElement("modified");
					op.appendChild(mod);

					if (genOperationInstance.getModified() != null) {
						mod.setTextContent(genOperationInstance.getModified().toString());
						if (genOperationInstance.getIngredientScope() != null) {
							Attr attr_ing = root.createAttribute("scope");
							attr_ing.setValue(genOperationInstance.getIngredientScope().toString());
							mod.setAttributeNode(attr_ing);
						}
					} else {
						mod.setTextContent(genOperationInstance.getOriginal().toString());
					}

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



	public static CtCodeElement clone(CtCodeElement st) {
		CtCodeElement cloned = factory.Core().clone(st);
		// --
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

	public static void setFactory(Factory factory) {
		MutationSupporter.factory = factory;
	}
}
