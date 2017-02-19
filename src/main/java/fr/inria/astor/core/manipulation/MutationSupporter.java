package fr.inria.astor.core.manipulation;

import java.io.File;
import java.net.URL;
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

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.bytecode.compiler.SpoonClassCompiler;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.manipulation.sourcecode.ROOTTYPE;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.OutputType;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

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
	
	private List<CtClass> classes = new ArrayList<>();
	private List<CtClass> testClasses = new ArrayList<>();

	
	public static Factory factory;


	public MutationSupporter() {
		this(getFactory());
	}

	
	public MutationSupporter(Factory factory) {
		this.factory = factory;
		spoonClassCompiler = new SpoonClassCompiler(factory);
		this.currentSupporter = this;
	}
	
	public void buildModel(String srcPathToBuild, String[] classpath) {
		boolean saveOutput = true;
		buildModel(srcPathToBuild, classpath,saveOutput);
	}
	
	public void buildModel(String srcPathToBuild, String[] classpath, boolean saveOutput) {

		logger.info("building model: " + srcPathToBuild + ", compliance level: "
				+ factory.getEnvironment().getComplianceLevel());
		jdtSpoonModelBuilder = new JDTBasedSpoonCompiler(factory);
		
		String[] sources = srcPathToBuild.split(File.pathSeparator); 
		for (String src : sources) {
			if(!src.trim().isEmpty())
				jdtSpoonModelBuilder.addInputSource(new File(src));
		}
		
		jdtSpoonModelBuilder.setSourceClasspath(classpath);
		jdtSpoonModelBuilder.build();
		if(saveOutput){
			jdtSpoonModelBuilder.setSourceOutputDirectory(new File(srcPathToBuild));
			jdtSpoonModelBuilder.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
			//(OutputType.CLASSES);
		}
	}
	
	public void saveClassModel(String srcPathToBuild, String[] classpath, boolean saveOutput) {

		logger.info("building model: " + srcPathToBuild + ", compliance level: "
				+ factory.getEnvironment().getComplianceLevel());
		jdtSpoonModelBuilder = new JDTBasedSpoonCompiler(factory);
		
			jdtSpoonModelBuilder.setSourceOutputDirectory(new File(srcPathToBuild));
			jdtSpoonModelBuilder.generateProcessedSourceFiles(OutputType.CLASSES);
	
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
	public void generateSourceCodeFromCtClass(CtType<?> type) {
		// WorkArround, for cloned
		SourcePosition sp = type.getPosition();
		type.setPosition(null);

		if (spoonClassCompiler == null || spoonClassCompiler.getJavaPrinter() == null) {
			throw new IllegalArgumentException("Spoon compiler must be initialized");
		}
		spoonClassCompiler.saveSourceCode((CtClass) type);

		// --
		// End Workarround
		type.setPosition(sp);

	}


	


	

	public SpoonClassCompiler getSpoonClassCompiler() {
		return this.spoonClassCompiler;
	}

	public static Factory getFactory() {
		
		if (factory == null) {
			factory = createFactory();
			factory.getEnvironment().setLevel("OFF");
			factory.getEnvironment().setSelfChecks(true);
			
		}
		return factory;
	}
	/**
	 * Creates a new spoon factory.
	 * @return
	 */
	public static Factory cleanFactory(){
		factory = null;
		return getFactory();
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
				List<OperatorInstance> genOperationInstances = childVariant.getOperations().get(i);
				if (genOperationInstances == null)
					continue;

				for (OperatorInstance genOperationInstance : genOperationInstances) {

					Element op = root.createElement("operation");
					rootElement.appendChild(op);

					Attr attr_location = root.createAttribute("location");
					attr_location.setValue(genOperationInstance.getModificationPoint().getCtClass().getQualifiedName());
					op.setAttributeNode(attr_location);

					if (genOperationInstance.getModificationPoint() instanceof SuspiciousModificationPoint) {
						SuspiciousModificationPoint gs = (SuspiciousModificationPoint) genOperationInstance.getModificationPoint();
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
						//mod.setTextContent(genOperationInstance.getOriginal().toString());
						mod.setNodeValue(genOperationInstance.getOriginal().toString());
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

	public static final CtElement ROOT_ELEMENT = new ROOTTYPE();


	public static CtCodeElement clone(CtCodeElement st) {
		CtCodeElement cloned = factory.Core().clone(st);
	
		//cloned.setParent(ROOT_ELEMENT);
		cloned.setParent(st.getParent());
		return cloned;
	}


	
	private static Factory createFactory() {
		StandardEnvironment env = new StandardEnvironment();
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), env);
		// environment initialization
		env.setComplianceLevel(ConfigurationProperties.getPropertyInt("javacompliancelevel"));
		env.setVerbose(false);
		env.setDebug(true);
		env.setTabulationSize(5);
		env.useTabulations(true);
		return factory;
	}
	
	public List<CtClass> getClasses() {
		return classes;
	}


	public List<CtClass> getTestClasses() {
		return testClasses;
	}
	
}
