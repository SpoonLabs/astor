package fr.inria.astor.test.muttest;

import java.io.File;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.MutationProperties;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.test.repair.evaluation.BaseEvolutionaryTest;
import fr.inria.main.AbstractMain;
import fr.inria.main.mutation.Main;


/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class MuTestingMutantGeneratorTest extends BaseEvolutionaryTest {

	
	private String mvnDir = ConfigurationProperties.getProperty("C:/Users/adam/.m2/repository/");

	

	protected void generateMutants( String projectName, String dependenciespath, String location, String packageToMine)
			throws Exception {
		
		System.gc();
		Stats currentStat = new Stats();

		MutationProperties.maxGeneration = new Integer(1/*ConfigurationProperties.properties.getProperty("maxGeneration")*/);
		MutationProperties.populationSize = new Integer(1/*ConfigurationProperties.properties.getProperty("populationSize")*/);
		
		
		getMain().run(location, projectName, dependenciespath, currentStat,  packageToMine);
		
	}



	@Override
	public AbstractMain createMain() {
		if(main != null)
			return main;
		
		return new Main();
	}

	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl, Stats currentStat) throws Exception {
			
		throw new IllegalArgumentException("Not such functionality");
		
	}
	
	@Test
	public void mutateCodec() throws Exception {
		
		String classpath = "C:/Users/adam/.m2/repository/org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar;"
				+ "C:/Users/adam/.m2/repository/junit/junit/4.7/junit-4.7.jar;C:/Users/adam/.m2/repository/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar;C:/Users/adam/.m2/repository/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar;C:/Users/adam/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar;C:/Users/adam/.m2/repository/org/springframework/spring-beans/3.0.6.RELEASE/spring-beans-3.0.6.RELEASE.jar;C:/Users/adam/.m2/repository/org/springframework/spring-core/3.0.6.RELEASE/spring-core-3.0.6.RELEASE.jar;C:/Users/adam/.m2/repository/org/springframework/spring-asm/3.0.6.RELEASE/spring-asm-3.0.6.RELEASE.jar;C:/Users/adam/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar;C:/Users/adam/.m2/repository/com/google/code/gson/gson/1.4/gson-1.4.jar;";
		
		String location = "C:/Personal/develop/astor/workspace-version/";
		String packageToMine = "org.apache.commons.codec";
		String projectName = "commons-codec-before";
		generateMutants(projectName, classpath, location, packageToMine);

	}

	@Test
	public void mutateSpojo() throws Exception {
		String classpath = "C:/Users/adam/.m2/repository/org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar;"
				+ "C:/Users/adam/.m2/repository/junit/junit/4.11/junit-4.11.jar;C:/Users/adam/.m2/repository/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar;C:/Users/adam/.m2/repository/org/slf4j/slf4j-log4j12/1.6.1/slf4j-log4j12-1.6.1.jar;C:/Users/adam/.m2/repository/log4j/log4j/1.2.16/log4j-1.2.16.jar;C:/Users/adam/.m2/repository/org/springframework/spring-beans/3.0.6.RELEASE/spring-beans-3.0.6.RELEASE.jar;C:/Users/adam/.m2/repository/org/springframework/spring-core/3.0.6.RELEASE/spring-core-3.0.6.RELEASE.jar;C:/Users/adam/.m2/repository/org/springframework/spring-asm/3.0.6.RELEASE/spring-asm-3.0.6.RELEASE.jar;C:/Users/adam/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar;C:/Users/adam/.m2/repository/com/google/code/gson/gson/1.4/gson-1.4.jar;";

		String location = "C:/Personal/develop/astor/workspace-version/";
		String projectName = "spojo-core-before";
		String packageToMine = "com.github.sworm.spojo";
		
		generateMutants(projectName, classpath, location, packageToMine);

	}

	@Test
	public void mutateLang() throws Exception {
		String classpath = "C:/Users/adam/.m2/repository/junit/junit/4.11/junit-4.11.jar;C:/Users/adam/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar;C:/Users/adam/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar;C:/Users/adam/.m2/repository/org/easymock/easymock/3.1/easymock-3.1.jar;C:/Users/adam/.m2/repository/cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar;C:/Users/adam/.m2/repository/org/objenesis/objenesis/1.2/objenesis-1.2.jar;";
		String location = "C:/Personal/develop/astor/workspace-version/"; 
		String projectName = "commons-lang3-before";
		String packageToMine = "org.apache.commons.lang3";
		generateMutants(projectName, classpath, location, packageToMine);

	}



	//

	@Test
	public void mutateSh() throws Exception {
		String classpath = "";
		classpath = mvnDir
				+ "/javax/inject/javax.inject/1/javax.inject-1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/ant/ant/1.8.2/ant-1.8.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/ant/ant-launcher/1.8.2/ant-launcher-1.8.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/aopalliance/aopalliance/1.0/aopalliance-1.0.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/args4j/args4j/2.0.16/args4j-2.0.16.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/caja/caja/r5054/caja-r5054.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/google/javascript/closure-compiler/v20130227/closure-compiler-v20130227.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/commons-codec/commons-codec/1.7/commons-codec-1.7.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/commons-fileupload/commons-fileupload/1.2.2/commons-fileupload-1.2.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/commons-io/commons-io/2.4/commons-io-2.4.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/commons/commons-lang3/3.1/commons-lang3-3.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/diff_match_patch/diff_match_patch/current/diff_match_patch-current.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/easymock/easymock/3.1/easymock-3.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/net/sf/ehcache/ehcache-core/2.5.2/ehcache-core-2.5.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/tomcat/el-api/6.0.36/el-api-6.0.36.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/google/guava/guava/14.0/guava-14.0.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/google/inject/guice/3.0/guice-3.0.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/google/inject/extensions/guice-multibindings/3.0/guice-multibindings-3.0.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/caja/htmlparser/r4209/htmlparser-r4209.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/httpcomponents/httpclient/4.1.2/httpclient-4.1.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/httpcomponents/httpcore/4.1.2/httpcore-4.1.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/ibm/icu/icu4j/4.8.1.1/icu4j-4.8.1.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/googlecode/jarjar/jarjar/1.1/jarjar-1.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/tomcat/jasper-el/6.0.36/jasper-el-6.0.36.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/jdom/jdom/1.0/jdom-1.0.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/mortbay/jetty/jetty/6.1.26/jetty-6.1.26.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/mortbay/jetty/jetty-util/6.1.26/jetty-util-6.1.26.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/joda-time/joda-time/2.1/joda-time-2.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/json/json/20070829/json-20070829.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/googlecode/json-simple/json-simple/1.1/json-simple-1.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/de/odysseus/juel/juel-impl/2.2.5/juel-impl-2.2.5.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/junit/junit/4.11/junit-4.11.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/junit-addons/junit-addons/1.4/junit-addons-1.4.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/rome/modules/0.3.2/modules-0.3.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/net/sourceforge/nekohtml/nekohtml/1.9.17/nekohtml-1.9.17.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/net/oauth/core/oauth/20100527/oauth-20100527.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/net/oauth/core/oauth-consumer/20090617/oauth-consumer-20090617.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/net/oauth/core/oauth-httpclient4/20090913/oauth-httpclient4-20090913.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/net/oauth/core/oauth-provider/20100527/oauth-provider-20100527.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/objenesis/objenesis/1.2/objenesis-1.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/google/protobuf/protobuf-java/2.4.1/protobuf-java-2.4.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/rome/rome/1.0/rome-1.0.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/apache/sanselan/sanselan/0.97-incubator/sanselan-0.97-incubator.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/mortbay/jetty/servlet-api/2.5-20081211/servlet-api-2.5-20081211.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/org/slf4j/slf4j-api/1.6.1/slf4j-api-1.6.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/xerces/xercesImpl/2.9.1/xercesImpl-2.9.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/xml-apis/xml-apis/1.3.04/xml-apis-1.3.04.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/xerces/xmlParserAPIs/2.6.2/xmlParserAPIs-2.6.2.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/xmlpull/xmlpull/1.1.3.1/xmlpull-1.1.3.1.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/xmlunit/xmlunit/1.3/xmlunit-1.3.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/xpp3/xpp3_min/1.1.4c/xpp3_min-1.1.4c.jar"
				+ File.pathSeparator
				+ mvnDir
				+ "/com/thoughtworks/xstream/xstream/1.4.3/xstream-1.4.3.jar"
				+ File.pathSeparator
				+ "/home/matmarti/.m2/repository/org/apache/shindig/shindig-common/2.5.0-beta6/shindig-common-2.5.0-beta6.jar"
				+ File.pathSeparator
				+ "/home/matmarti/.m2/repository/org/apache/shindig/shindig-common/2.5.0-beta6/shindig-common-2.5.0-beta6-tests.jar";
		String location = "/home/matmarti/develop/workspacePurification/";
		String packageToMine = "org.apache.shindig.gadgets";
		generateMutants("shindig-gadgets-4mut", classpath, location, packageToMine);

	}


}
