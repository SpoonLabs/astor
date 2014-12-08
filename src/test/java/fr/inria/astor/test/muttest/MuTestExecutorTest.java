package fr.inria.astor.test.muttest;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.loop.mutation.muttest.MuTestingExecutor;
import fr.inria.astor.core.loop.mutation.muttest.MuTestingResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.BugFixDataSet;

public class MuTestExecutorTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	//@Test
	public void testRunMutTesting() throws MalformedURLException, InterruptedException, ExecutionException {
		MuTestingExecutor executor = new MuTestingExecutor();
		String path = "C:/Personal/develop/workspaceEvolution/MutationEngine/outputMutation/MuTestingEvolutionaryTest-428/out2/" ;
		
		String original = "default/";
		//String mutant = "expression-mut-3/";
		String id = ConfigurationProperties.properties.getProperty("bugId"); 
		String[] bugproperties = BugFixDataSet.getDataForId(id);
		
		String classpath = bugproperties[4];
		System.out.println("classpath "+classpath);
		String[] testClasses = executor.findTestCases(new File(path+File.separator+original).toURL());
		
		System.out.println("tc ("+testClasses.length+") "+Arrays.toString(testClasses));
		assertNotNull(testClasses);
		
			
		MuTestingResult testResult= executor.run("default",testClasses, classpath, path,original);
		System.out.println("Test Result "+testResult);
		//---
		
	}
		
	private void executeBeforeAfterMutation(String spooned_path, String projectBefore_path,String projectAfter_path,String classpath,String binloc) throws MalformedURLException, InterruptedException, ExecutionException {
		
		long inittime = System.currentTimeMillis();
		MuTestingExecutor executor = new MuTestingExecutor();
		
		String before_purif_path = projectBefore_path+File.separator+binloc;
		
		String purified_path = projectAfter_path+File.separator+binloc;
		
		String original = "default/";
	
		System.out.println("classpath "+classpath);
		String[] testClassesAfter = executor.findTestCases(new File(purified_path).toURL());
		System.out.println(testClassesAfter.length + " " +Arrays.toString(testClassesAfter));
			
		System.out.println("*************************Executing test cases before purification******************************");
		MuTestingResult beforePurification = executor.run("default",testClassesAfter /*Before*/, classpath, spooned_path, before_purif_path);
		
		String classPathAfterPurification = classpath;
		System.out.println("*************************Executing test cases after purification*******************************");
		MuTestingResult afterPurification = executor.run("default",testClassesAfter, classPathAfterPurification, spooned_path, purified_path);
		
		//executor.compareResults(beforePurification, afterPurification);
		
		System.out.println("-------------------Results-------------------");
		System.out.println("=Analyze Killed before in after results");
		executor.compareResults(beforePurification, afterPurification);
		
		System.out.println("=Analyze Killed after in before results");
		executor.compareResults(afterPurification, beforePurification);
		
		
		System.out.println("Total time "+((System.currentTimeMillis() - inittime)/1000));
	}
	
	String mvnDir = "/home/matmarti/.m2/repository/";
	
		
	@Test
	public void testCalculateLang2() throws MalformedURLException, InterruptedException, ExecutionException{
		
		String spooned_path = "/home/matmarti/develop/mutants-generated/mutlang/out2/";
					//"/home/matmarti/develop/mutants-generated/mutlang/MuTestingEvolutionaryTest-commons-lang3-before/out2/" ;
	
		String projectBefore_path = "/home/matmarti/develop/workspacePurification/lang3-if4mut/";
		
		String projectAfter_path = "/home/matmarti/develop/workspacePurification/lang3-if4mut-spooned/";
		
		String classpath = ""+
			//	"/home/matmarti/develop/workspacePurification/toRunExp/bin"+File.pathSeparator
				"/home/matmarti/develop/benoitPurification/b-refactoring/spoon-if/bin"+File.pathSeparator
				//Project Dependencies
				+mvnDir+"junit/junit/4.11/junit-4.11.jar"+File.pathSeparator+mvnDir+"org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"+File.pathSeparator+mvnDir+"commons-io/commons-io/2.4/commons-io-2.4.jar"+File.pathSeparator+mvnDir+"org/easymock/easymock/3.1/easymock-3.1.jar"+File.pathSeparator+mvnDir+"cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar"+File.pathSeparator+mvnDir+"org/objenesis/objenesis/1.2/objenesis-1.2.jar";

		
		this.executeBeforeAfterMutation(spooned_path, projectBefore_path, projectAfter_path, classpath,"bin");
	} 
	@Test
	public void testCalculateSpojo() throws MalformedURLException, InterruptedException, ExecutionException{
		
		String spooned_path = "/home/matmarti/develop/mutants-generated/mutspojo/out2/" ;
		
		String projectBefore_path = "/home/matmarti/develop/workspacePurification/spojo-core4mut/";
		
		String projectAfter_path = "/home/matmarti/develop/workspacePurification/spojo-core4mut-spooned/";
				
		String classpath = 
				"/home/matmarti/develop/benoitPurification/b-refactoring/spoon-if/bin"+File.pathSeparator
				+
				//""+mvnDir+"junit/junit/4.11/junit-4.11.jar"+File.pathSeparator+mvnDir+"org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"+File.pathSeparator+mvnDir+"commons-io/commons-io/2.4/commons-io-2.4.jar"+File.pathSeparator+mvnDir+"org/easymock/easymock/3.1/easymock-3.1.jar"+File.pathSeparator+mvnDir+"cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar"+File.pathSeparator+mvnDir+"org/objenesis/objenesis/1.2/objenesis-1.2.jar";
				mvnDir+"/org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar"+File.pathSeparator+
				mvnDir+"/junit/junit/4.11/junit-4.11.jar"+File.pathSeparator+
				mvnDir+"/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"+File.pathSeparator+
				mvnDir+"/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"+File.pathSeparator+
				mvnDir+"/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar"+File.pathSeparator+
				mvnDir+"/log4j/log4j/1.2.16/log4j-1.2.16.jar"+File.pathSeparator+
				mvnDir+"/org/springframework/spring-beans/3.0.6.RELEASE/spring-beans-3.0.6.RELEASE.jar"+File.pathSeparator+
				mvnDir+"/org/springframework/spring-core/3.0.6.RELEASE/spring-core-3.0.6.RELEASE.jar"+File.pathSeparator+
				mvnDir+"/org/springframework/spring-asm/3.0.6.RELEASE/spring-asm-3.0.6.RELEASE.jar"+File.pathSeparator+
				mvnDir+"/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"+File.pathSeparator+
				mvnDir+"/com/google/code/gson/gson/1.4/gson-1.4.jar"+File.pathSeparator;
		
		this.executeBeforeAfterMutation(spooned_path, projectBefore_path, projectAfter_path, classpath,"bin");

	} 
	@Test
	public void testCalculateCodec() throws MalformedURLException, InterruptedException, ExecutionException{
		
		//Used only to retrieve the mutants
		String spooned_path = "/home/matmarti/develop/mutants-generated/mutcodec/out2/" ;
		
		String projectBefore_path = "/home/matmarti/develop/workspacePurification/commons-codec4mut";
		
		String projectAfter_path = "/home/matmarti/develop/workspacePurification/commons-codec4mut-spooned/";
		
		
		String classpath = 	
				"/home/matmarti/develop/benoitPurification/b-refactoring/spoon-if/bin"+File.pathSeparator
				+mvnDir+"org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar;"
				+ ""+mvnDir+"junit/junit/4.11/junit-4.11.jar"+File.pathSeparator+mvnDir+"org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"+File.pathSeparator+mvnDir+"org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar"+File.pathSeparator+mvnDir+"log4j/log4j/1.2.16/log4j-1.2.16.jar"+File.pathSeparator+mvnDir+"org/springframework/spring-beans/3.0.6.RELEASE/spring-beans-3.0.6.RELEASE.jar"+File.pathSeparator+mvnDir+"org/springframework/spring-core/3.0.6.RELEASE/spring-core-3.0.6.RELEASE.jar"+File.pathSeparator+mvnDir+"org/springframework/spring-asm/3.0.6.RELEASE/spring-asm-3.0.6.RELEASE.jar"+File.pathSeparator+mvnDir+"commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"+File.pathSeparator+mvnDir+"com/google/code/gson/gson/1.4/gson-1.4.jar";
		
		
		this.executeBeforeAfterMutation(spooned_path, projectBefore_path, projectAfter_path, classpath,"bin");

	}
	
	@Test
	public void testCalculateJBehave() throws MalformedURLException, InterruptedException, ExecutionException{
		
		//Used only to retrieve the mutants
		String spooned_path = "/home/matmarti/develop/mutants-generated/mutjbehave-core/out2/" ;
		
		String projectBefore_path = "/home/matmarti/develop/workspacePurification/jbehave-core4mut/";
		
		String projectAfter_path = "/home/matmarti/develop/workspacePurification/jbehave-core4mut-spooned/";
		
		
		String classpath = 	
			/*	"/home/matmarti/develop/benoitPurification/b-refactoring/spoon-if/bin"+File.pathSeparator
				+mvnDir+"org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar;"
				+ ""+mvnDir+"junit/junit/4.11/junit-4.11.jar"+File.pathSeparator+mvnDir+"org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"+File.pathSeparator+mvnDir+"org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar"+File.pathSeparator+mvnDir+"log4j/log4j/1.2.16/log4j-1.2.16.jar"+File.pathSeparator+mvnDir+"org/springframework/spring-beans/3.0.6.RELEASE/spring-beans-3.0.6.RELEASE.jar"+File.pathSeparator+mvnDir+"org/springframework/spring-core/3.0.6.RELEASE/spring-core-3.0.6.RELEASE.jar"+File.pathSeparator+mvnDir+"org/springframework/spring-asm/3.0.6.RELEASE/spring-asm-3.0.6.RELEASE.jar"+File.pathSeparator+mvnDir+"commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"+File.pathSeparator+mvnDir+"com/google/code/gson/gson/1.4/gson-1.4.jar";
		*/
		"/home/matmarti/develop/benoitPurification/b-refactoring/spoon-if/bin"+File.pathSeparator
		+mvnDir+"/javax/inject/javax.inject/1/javax.inject-1.jar"+File.pathSeparator
		+mvnDir+"/org/hamcrest/hamcrest-core/1.1/hamcrest-core-1.1.jar"+File.pathSeparator
		+mvnDir+"/org/hamcrest/hamcrest-library/1.1/hamcrest-library-1.1.jar"+File.pathSeparator
		+mvnDir+"/org/hamcrest/hamcrest-integration/1.1/hamcrest-integration-1.1.jar"+File.pathSeparator
		+mvnDir+"/commons-collections/commons-collections/3.2.1/commons-collections-3.2.1.jar"+File.pathSeparator
		+mvnDir+"/commons-io/commons-io/2.4/commons-io-2.4.jar"+File.pathSeparator
		+mvnDir+"/commons-lang/commons-lang/2.6/commons-lang-2.6.jar"+File.pathSeparator
		+mvnDir+"/com/google/guava/guava/14.0/guava-14.0.jar"+File.pathSeparator
		+mvnDir+"/org/codehaus/groovy/groovy-all/2.1.1/groovy-all-2.1.1.jar"+File.pathSeparator
		+mvnDir+"/org/codehaus/plexus/plexus-utils/3.0.10/plexus-utils-3.0.10.jar"+File.pathSeparator
		+mvnDir+"/org/freemarker/freemarker/2.3.19/freemarker-2.3.19.jar"+File.pathSeparator
		+mvnDir+"/com/thoughtworks/paranamer/paranamer/2.4/paranamer-2.4.jar"+File.pathSeparator
		+mvnDir+"/com/thoughtworks/xstream/xstream/1.4.4/xstream-1.4.4.jar"+File.pathSeparator
		+mvnDir+"/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"+File.pathSeparator
		+mvnDir+"/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar"+File.pathSeparator
		+mvnDir+"/xmlunit/xmlunit/1.4/xmlunit-1.4.jar"+File.pathSeparator
		+mvnDir+"/org/mockito/mockito-all/1.9.5/mockito-all-1.9.5.jar"+File.pathSeparator
		+mvnDir+"/org/apache/maven/skins/maven-default-skin/1.0/maven-default-skin-1.0.jar"+File.pathSeparator
		+"/home/matmarti/develop/workspacePurification/jbehave-core4mut-spooned/lib/junit-4.11.jar"+File.pathSeparator;
		
		
		this.executeBeforeAfterMutation(spooned_path, projectBefore_path, projectAfter_path, classpath,"bin");

	}
	
	
	@Test
	public void testCalculateShi() throws MalformedURLException, InterruptedException, ExecutionException{
		
		//Used only to retrieve the mutants
		String spooned_path = "/home/matmarti/develop/mutants-generated/mutshi/out2/" ;
		
		String projectBefore_path = "/home/matmarti/develop/workspacePurification/shindig-gadgets-4mut";
		
		String projectAfter_path = "/home/matmarti/develop/workspacePurification/shindig-gadgets-4mut-spooned/";
		
		
		String classpath = 	
				"/home/matmarti/develop/benoitPurification/b-refactoring/spoon-if/bin"+File.pathSeparator
				+ 
				mvnDir+"/javax/inject/javax.inject/1/javax.inject-1.jar"+File.pathSeparator+
				mvnDir+"/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar"+File.pathSeparator+
				mvnDir+"/org/apache/ant/ant/1.8.2/ant-1.8.2.jar"+File.pathSeparator+
				mvnDir+"/org/apache/ant/ant-launcher/1.8.2/ant-launcher-1.8.2.jar"+File.pathSeparator+
				mvnDir+"/aopalliance/aopalliance/1.0/aopalliance-1.0.jar"+File.pathSeparator+
				mvnDir+"/args4j/args4j/2.0.16/args4j-2.0.16.jar"+File.pathSeparator+
				mvnDir+"/caja/caja/r5054/caja-r5054.jar"+File.pathSeparator+
				mvnDir+"/cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar"+File.pathSeparator+
				mvnDir+"/com/google/javascript/closure-compiler/v20130227/closure-compiler-v20130227.jar"+File.pathSeparator+
				mvnDir+"/commons-codec/commons-codec/1.7/commons-codec-1.7.jar"+File.pathSeparator+
				mvnDir+"/commons-fileupload/commons-fileupload/1.2.2/commons-fileupload-1.2.2.jar"+File.pathSeparator+
				mvnDir+"/commons-io/commons-io/2.4/commons-io-2.4.jar"+File.pathSeparator+
				mvnDir+"/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar"+File.pathSeparator+
				mvnDir+"/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"+File.pathSeparator+
				mvnDir+"/diff_match_patch/diff_match_patch/current/diff_match_patch-current.jar"+File.pathSeparator+
				mvnDir+"/org/easymock/easymock/3.1/easymock-3.1.jar"+File.pathSeparator+
				mvnDir+"/net/sf/ehcache/ehcache-core/2.5.2/ehcache-core-2.5.2.jar"+File.pathSeparator+
				mvnDir+"/org/apache/tomcat/el-api/6.0.36/el-api-6.0.36.jar"+File.pathSeparator+
				mvnDir+"/com/google/guava/guava/14.0/guava-14.0.jar"+File.pathSeparator+
				mvnDir+"/com/google/inject/guice/3.0/guice-3.0.jar"+File.pathSeparator+
				mvnDir+"/com/google/inject/extensions/guice-multibindings/3.0/guice-multibindings-3.0.jar"+File.pathSeparator+
				mvnDir+"/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"+File.pathSeparator+
				mvnDir+"/caja/htmlparser/r4209/htmlparser-r4209.jar"+File.pathSeparator+
				mvnDir+"/org/apache/httpcomponents/httpclient/4.1.2/httpclient-4.1.2.jar"+File.pathSeparator+
				mvnDir+"/org/apache/httpcomponents/httpcore/4.1.2/httpcore-4.1.2.jar"+File.pathSeparator+
				mvnDir+"/com/ibm/icu/icu4j/4.8.1.1/icu4j-4.8.1.1.jar"+File.pathSeparator+
				mvnDir+"/com/googlecode/jarjar/jarjar/1.1/jarjar-1.1.jar"+File.pathSeparator+
				mvnDir+"/org/apache/tomcat/jasper-el/6.0.36/jasper-el-6.0.36.jar"+File.pathSeparator+
				mvnDir+"/jdom/jdom/1.0/jdom-1.0.jar"+File.pathSeparator+
				mvnDir+"/org/mortbay/jetty/jetty/6.1.26/jetty-6.1.26.jar"+File.pathSeparator+
				mvnDir+"/org/mortbay/jetty/jetty-util/6.1.26/jetty-util-6.1.26.jar"+File.pathSeparator+
				mvnDir+"/joda-time/joda-time/2.1/joda-time-2.1.jar"+File.pathSeparator+
				mvnDir+"/org/json/json/20070829/json-20070829.jar"+File.pathSeparator+
				mvnDir+"/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar"+File.pathSeparator+
				mvnDir+"/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar"+File.pathSeparator+
				mvnDir+"/de/odysseus/juel/juel-impl/2.2.5/juel-impl-2.2.5.jar"+File.pathSeparator+
				mvnDir+"/junit/junit/4.11/junit-4.11.jar"+File.pathSeparator+
				mvnDir+"/junit-addons/junit-addons/1.4/junit-addons-1.4.jar"+File.pathSeparator+
				mvnDir+"/rome/modules/0.3.2/modules-0.3.2.jar"+File.pathSeparator+
				mvnDir+"/net/sourceforge/nekohtml/nekohtml/1.9.17/nekohtml-1.9.17.jar"+File.pathSeparator+
				mvnDir+"/net/oauth/core/oauth/20100527/oauth-20100527.jar"+File.pathSeparator+
				mvnDir+"/net/oauth/core/oauth-consumer/20090617/oauth-consumer-20090617.jar"+File.pathSeparator+
				mvnDir+"/net/oauth/core/oauth-httpclient4/20090913/oauth-httpclient4-20090913.jar"+File.pathSeparator+
				mvnDir+"/net/oauth/core/oauth-provider/20100527/oauth-provider-20100527.jar"+File.pathSeparator+
				mvnDir+"/org/objenesis/objenesis/1.2/objenesis-1.2.jar"+File.pathSeparator+
				mvnDir+"/com/google/protobuf/protobuf-java/2.4.1/protobuf-java-2.4.1.jar"+File.pathSeparator+
				mvnDir+"/rome/rome/1.0/rome-1.0.jar"+File.pathSeparator+
				mvnDir+"/org/apache/sanselan/sanselan/0.97-incubator/sanselan-0.97-incubator.jar"+File.pathSeparator+
				mvnDir+"/org/mortbay/jetty/servlet-api/2.5-20081211/servlet-api-2.5-20081211.jar"+File.pathSeparator+
				mvnDir+"/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"+File.pathSeparator+
				mvnDir+"/xerces/xercesImpl/2.9.1/xercesImpl-2.9.1.jar"+File.pathSeparator+
				mvnDir+"/xml-apis/xml-apis/1.3.04/xml-apis-1.3.04.jar"+File.pathSeparator+
				mvnDir+"/xerces/xmlParserAPIs/2.6.2/xmlParserAPIs-2.6.2.jar"+File.pathSeparator+
				mvnDir+"/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"+File.pathSeparator+
				mvnDir+"/xmlunit/xmlunit/1.3/xmlunit-1.3.jar"+File.pathSeparator+
				mvnDir+"/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar"+File.pathSeparator+
				mvnDir+"/com/thoughtworks/xstream/xstream/1.4.3/xstream-1.4.3.jar"+File.pathSeparator+
				"/home/matmarti/.m2/repository/org/apache/shindig/shindig-common/2.5.0-beta6/shindig-common-2.5.0-beta6.jar"+File.pathSeparator+
				"/home/matmarti/.m2/repository/org/apache/shindig/shindig-common/2.5.0-beta6/shindig-common-2.5.0-beta6-tests.jar";


		
		this.executeBeforeAfterMutation(spooned_path, projectBefore_path, projectAfter_path, classpath,"bin");

	}
	
	
}
