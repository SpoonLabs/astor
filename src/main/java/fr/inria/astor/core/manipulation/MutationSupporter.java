package fr.inria.astor.core.manipulation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.bytecode.OutputWritter;
import fr.inria.astor.core.manipulation.sourcecode.ROOTTYPE;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.OutputType;
import spoon.SpoonModelBuilder.InputType;
import spoon.compiler.Environment;
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

	private OutputWritter output;

	public static Factory factory;

	public MutationSupporter() {
		this(getFactory(), getFactory().getEnvironment());
	}

	public MutationSupporter(Factory factory, Environment environment) {
		this.factory = factory;
		this.currentSupporter = this;
		this.output = new OutputWritter(factory);
	}

	public void buildModel(String srcPathToBuild, String[] classpath) {

		buildModel(srcPathToBuild, null, classpath);
	}

	public void buildModel(String srcPathToBuild, String bytecodePathToBuild, String[] classpath) {
		JDTBasedSpoonCompiler jdtSpoonModelBuilder = null;
		logger.info("building model: " + srcPathToBuild + ", compliance level: "
				+ factory.getEnvironment().getComplianceLevel());
		factory.getEnvironment().setCommentEnabled(false);
		factory.getEnvironment().setNoClasspath(false);
		factory.getEnvironment().setPreserveLineNumbers(ConfigurationProperties.getPropertyBool("preservelinenumbers"));

		jdtSpoonModelBuilder = new JDTBasedSpoonCompiler(factory);

		String[] sources = srcPathToBuild.split(File.pathSeparator);
		for (String src : sources) {
			if (!src.trim().isEmpty())
				jdtSpoonModelBuilder.addInputSource(new File(src));
		}
		logger.info("Classpath for building SpoonModel " + Arrays.toString(classpath));
		jdtSpoonModelBuilder.setSourceClasspath(classpath);

		jdtSpoonModelBuilder.build();

		if (ConfigurationProperties.getPropertyBool("savespoonmodelondisk")) {
			factory.getEnvironment().setSourceOutputDirectory(new File(srcPathToBuild));
			jdtSpoonModelBuilder.generateProcessedSourceFiles(OutputType.COMPILATION_UNITS);
			jdtSpoonModelBuilder.setBinaryOutputDirectory(new File(bytecodePathToBuild));
			jdtSpoonModelBuilder.compile(InputType.CTTYPES);
		}

	}

	/**
	 * Saves Java File and Compiles it The Program Variant as well as the rest of
	 * the project is saved on disk. Not any more: Additionally, the compiled class
	 * are saved it on disk. Finally, the current Thread has a reference to a class
	 * loader with the ProgramVariant
	 * 
	 * @param instance
	 * @throws Exception
	 */
	public void saveSourceCodeOnDiskProgramVariant(ProgramVariant instance, String srcOutput) throws Exception {
		// Set up the dir where we save the generated output
		this.output.updateOutput(srcOutput);
		Collection<CtClass> _classes = new ArrayList<>();
		// We save only the classes affected by operations.
		List<OperatorInstance> opin = instance.getAllOperations();
		for (OperatorInstance operatorInstance : opin) {
			CtClass _classopin = operatorInstance.getModificationPoint().getCtClass();
			if (_classopin != null && !_classes.contains(_classopin))
				_classes.add(_classopin);
		}
		if (_classes.isEmpty()) {
			_classes = instance.getBuiltClasses().values();
		}

		for (CtClass ctclass : instance.getBuiltClasses().values()) {
			this.generateSourceCodeFromCtClass(ctclass);
		}

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

		if (output == null || output.getJavaPrinter() == null) {
			throw new IllegalArgumentException("Spoon compiler must be initialized");
		}
		output.saveSourceCode((CtClass) type);
		// --
		// End Workarround
		type.setPosition(sp);

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
	 * 
	 * @return
	 */
	public static Factory cleanFactory() {
		factory = null;
		return getFactory();
	}

	public static final CtElement ROOT_ELEMENT = new ROOTTYPE();

	public static CtCodeElement clone(CtCodeElement st) {
		CtCodeElement cloned = factory.Core().clone(st);

		// cloned.setParent(ROOT_ELEMENT);
		cloned.setParent(st.getParent());
		return cloned;
	}

	private static Factory createFactory() {
		Environment env = getEnvironment();
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), env);

		return factory;
	}

	public static Environment getEnvironment() {
		StandardEnvironment env = new StandardEnvironment();

		env.setComplianceLevel(ConfigurationProperties.getPropertyInt("javacompliancelevel"));
		env.setVerbose(false);
		env.setDebug(true);
		env.setTabulationSize(5);
		env.useTabulations(true);
		return env;
	}

	public OutputWritter getOutput() {
		return output;
	}

	public void setOutput(OutputWritter output) {
		this.output = output;
	}
}
