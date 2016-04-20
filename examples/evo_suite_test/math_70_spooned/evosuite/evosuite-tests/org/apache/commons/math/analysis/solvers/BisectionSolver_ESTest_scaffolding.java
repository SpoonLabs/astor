package org.apache.commons.math.analysis.solvers;


@org.evosuite.runtime.annotation.EvoSuiteClassExclude
public class BisectionSolver_ESTest_scaffolding {
	private static final java.util.Properties defaultProperties = ((java.util.Properties)(java.lang.System.getProperties().clone()));

	private org.evosuite.runtime.thread.ThreadStopper threadStopper = new org.evosuite.runtime.thread.ThreadStopper(org.evosuite.runtime.thread.KillSwitchHandler.getInstance() , 3000);

	@org.junit.BeforeClass
	public static void initEvoSuiteFramework() {
		org.evosuite.runtime.RuntimeSettings.className = "org.apache.commons.math.analysis.solvers.BisectionSolver";
		org.evosuite.runtime.GuiSupport.initialize();
		org.evosuite.runtime.RuntimeSettings.maxNumberOfThreads = 100;
		org.evosuite.runtime.RuntimeSettings.maxNumberOfIterationsPerLoop = 10000;
		org.evosuite.runtime.RuntimeSettings.mockSystemIn = true;
		org.evosuite.runtime.RuntimeSettings.sandboxMode = org.evosuite.runtime.sandbox.Sandbox.SandboxMode.RECOMMENDED;
		org.evosuite.runtime.sandbox.Sandbox.initializeSecurityManagerForSUT();
		org.evosuite.runtime.classhandling.JDKClassResetter.init();
		org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest_scaffolding.initializeClasses();
		org.evosuite.runtime.Runtime.getInstance().resetRuntime();
	}

	@org.junit.AfterClass
	public static void clearEvoSuiteFramework() {
		org.evosuite.runtime.sandbox.Sandbox.resetDefaultSecurityManager();
		java.lang.System.setProperties(((java.util.Properties)(defaultProperties.clone())));
	}

	@org.junit.Before
	public void initTestCase() {
		threadStopper.storeCurrentThreads();
		threadStopper.startRecordingTime();
		org.evosuite.runtime.jvm.ShutdownHookHandler.getInstance().initHandler();
		org.evosuite.runtime.sandbox.Sandbox.goingToExecuteSUTCode();
		setSystemProperties();
		org.evosuite.runtime.GuiSupport.setHeadless();
		org.evosuite.runtime.Runtime.getInstance().resetRuntime();
		org.evosuite.runtime.agent.InstrumentingAgent.activate();
	}

	@org.junit.After
	public void doneWithTestCase() {
		threadStopper.killAndJoinClientThreads();
		org.evosuite.runtime.jvm.ShutdownHookHandler.getInstance().safeExecuteAddedHooks();
		org.evosuite.runtime.classhandling.JDKClassResetter.reset();
		org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest_scaffolding.resetClasses();
		org.evosuite.runtime.sandbox.Sandbox.doneWithExecutingSUTCode();
		org.evosuite.runtime.agent.InstrumentingAgent.deactivate();
		org.evosuite.runtime.GuiSupport.restoreHeadlessMode();
	}

	public void setSystemProperties() {
		java.lang.System.setProperties(((java.util.Properties)(defaultProperties.clone())));
		java.lang.System.setProperty("java.vm.vendor", "Oracle Corporation");
		java.lang.System.setProperty("java.specification.version", "1.8");
		java.lang.System.setProperty("java.home", "/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre");
		java.lang.System.setProperty("java.awt.headless", "true");
		java.lang.System.setProperty("user.home", "/Users/matias");
		java.lang.System.setProperty("user.dir", "/Users/matias/develop/code/astor/outputMutation/AstorMain-math_70/src/evosuite");
		java.lang.System.setProperty("java.io.tmpdir", "/var/folders/j2/8n92xqwx5h98smc8nzwpwysh0000gn/T/");
		java.lang.System.setProperty("awt.toolkit", "sun.lwawt.macosx.LWCToolkit");
		java.lang.System.setProperty("file.encoding", "UTF-8");
		java.lang.System.setProperty("file.separator", "/");
		java.lang.System.setProperty("java.awt.graphicsenv", "sun.awt.CGraphicsEnvironment");
		java.lang.System.setProperty("java.awt.printerjob", "sun.lwawt.macosx.CPrinterJob");
		java.lang.System.setProperty("java.class.path", "/var/folders/j2/8n92xqwx5h98smc8nzwpwysh0000gn/T/EvoSuite_pathingJar4361697754274291834.jar");
		java.lang.System.setProperty("java.class.version", "52.0");
		java.lang.System.setProperty("java.endorsed.dirs", "/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/endorsed");
		java.lang.System.setProperty("java.ext.dirs", "/Users/matias/Library/Java/Extensions:/Library/Java/JavaVirtualMachines/jdk1.8.0_65.jdk/Contents/Home/jre/lib/ext:/Library/Java/Extensions:/Network/Library/Java/Extensions:/System/Library/Java/Extensions:/usr/lib/java");
		java.lang.System.setProperty("java.library.path", "lib");
		java.lang.System.setProperty("java.runtime.name", "Java(TM) SE Runtime Environment");
		java.lang.System.setProperty("java.runtime.version", "1.8.0_65-b17");
		java.lang.System.setProperty("java.specification.name", "Java Platform API Specification");
		java.lang.System.setProperty("java.specification.vendor", "Oracle Corporation");
		java.lang.System.setProperty("java.vendor", "Oracle Corporation");
		java.lang.System.setProperty("java.vendor.url", "http://java.oracle.com/");
		java.lang.System.setProperty("java.version", "1.8.0_65");
		java.lang.System.setProperty("java.vm.info", "mixed mode");
		java.lang.System.setProperty("java.vm.name", "Java HotSpot(TM) 64-Bit Server VM");
		java.lang.System.setProperty("java.vm.specification.name", "Java Virtual Machine Specification");
		java.lang.System.setProperty("java.vm.specification.vendor", "Oracle Corporation");
		java.lang.System.setProperty("java.vm.specification.version", "1.8");
		java.lang.System.setProperty("java.vm.version", "25.65-b01");
		java.lang.System.setProperty("line.separator", "\n");
		java.lang.System.setProperty("os.arch", "x86_64");
		java.lang.System.setProperty("os.name", "Mac OS X");
		java.lang.System.setProperty("os.version", "10.11.3");
		java.lang.System.setProperty("path.separator", ":");
		java.lang.System.setProperty("user.country", "US");
		java.lang.System.setProperty("user.language", "en");
		java.lang.System.setProperty("user.name", "matias");
		java.lang.System.setProperty("user.timezone", "Europe/Zurich");
	}

	private static void initializeClasses() {
		org.evosuite.runtime.classhandling.ClassStateSupport.initializeClasses(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest_scaffolding.class.getClassLoader(), "org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils", "org.apache.commons.math.MathException", "org.apache.commons.math.ConvergenceException", "org.apache.commons.math.analysis.solvers.UnivariateRealSolver", "org.apache.commons.math.analysis.solvers.BisectionSolver", "org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl", "org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction", "org.apache.commons.math.analysis.UnivariateRealFunction", "org.apache.commons.math.MaxIterationsExceededException", "org.apache.commons.math.analysis.SinFunction", "org.apache.commons.math.FunctionEvaluationException", "org.apache.commons.math.analysis.Expm1Function", "org.apache.commons.math.analysis.MonitoredFunction", "org.apache.commons.math.ConvergingAlgorithm", "org.apache.commons.math.analysis.Expm1Function$1", "org.apache.commons.math.analysis.QuinticFunction", "org.apache.commons.math.ConvergingAlgorithmImpl", "org.apache.commons.math.MathRuntimeException", "org.apache.commons.math.MathRuntimeException$1", "org.apache.commons.math.MathRuntimeException$2", "org.apache.commons.math.MathRuntimeException$3", "org.apache.commons.math.MathRuntimeException$4", "org.apache.commons.math.MathRuntimeException$5", "org.apache.commons.math.MathRuntimeException$6", "org.apache.commons.math.MathRuntimeException$7", "org.apache.commons.math.MathRuntimeException$8", "org.apache.commons.math.MathRuntimeException$10", "org.apache.commons.math.MathRuntimeException$9");
	}

	private static void resetClasses() {
		org.evosuite.runtime.classhandling.ClassResetter.getInstance().setClassLoader(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest_scaffolding.class.getClassLoader());
		org.evosuite.runtime.classhandling.ClassStateSupport.resetClasses("org.apache.commons.math.MathRuntimeException", "org.apache.commons.math.MathRuntimeException$4", "org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils", "org.apache.commons.math.MathRuntimeException$5", "org.apache.commons.math.MathException", "org.apache.commons.math.ConvergenceException", "org.apache.commons.math.MaxIterationsExceededException");
	}
}

