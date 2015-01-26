package fr.inria.astor.core.faultlocalization.entity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import fr.inria.astor.core.faultlocalization.SuspiciousCode;
import fr.inria.astor.core.setup.TransformationProperties;

/**
 * Facade of Fault Localization techniques like GZoltar or own implementations (package {@link org.inria.sacha.faultlocalization}.).
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class FaultLocalizationFacade {

	
	Logger logger = Logger.getLogger(FaultLocalizationFacade.class.getName());

	List<SuspiciousCode> candidates = new ArrayList<SuspiciousCode>();
	List<String> failingTestCases = new ArrayList<String>();
	
	static boolean EXCLUDE_TEST = true;
		
	public List<SuspiciousCode> searchGZoltar(String location, List<String> testsToExecute,List<String> toInstrument, HashSet<String> cp) throws FileNotFoundException, IOException {
		candidates.clear();
		failingTestCases.clear();
		logger.info("Gzoltar fault localization: min susp value parameter: "+TransformationProperties.THRESHOLD_SUSPECTNESS);
		// 1. Instantiate GZoltar
		// here you need to specify the working directory where the tests will
		// be run. Can be the full or relative path.
		//Example: GZoltar gz = new GZoltar("C:\\Personal\\develop\\workspaceEvolution\\testProject\\target\\classes");

		System.out.println(new File(location).getAbsolutePath());
		GZoltar gz = new GZoltar(new File(location).getAbsolutePath());
		
		// 2. Add Package/Class names to instrument
		// 3. Add Package/Test Case/Test Suite names to execute
		//Example: gz.addPackageToInstrument("org.test1.Person");
		for(String to: toInstrument){
			gz.addPackageToInstrument(to);
		}
		if(cp!=null || !cp.isEmpty()){
			//gz.setClassPaths(cp);
			logger.info("Current classpath: "+System.getProperty("java.class.path"));
			logger.info("Adding classpath: "+cp);
			gz.getClasspaths().addAll(cp);
		}
		for (String test : testsToExecute) {
			gz.addTestToExecute(test);
		}
		gz.run();
		
		int[] sum= new int[2];
		for( TestResult tr : gz.getTestResults()){
			sum[0]++;
			logger.debug("Test "+tr.getName()+", success: "+ tr.wasSuccessful());
			sum[1]+=tr.wasSuccessful()?0:1;
			if(!tr.wasSuccessful()){
				logger.info("Test failt: "+tr.getName());
			//	logger.info(tr.getTrace());
				failingTestCases.add(tr.getName().split("#")[0]);
			}
			if(tr.getTrace() != null){
				//logger.info(tr.getTrace());
			}
					
		}
		logger.info("Test Result Total:"+sum[0]+", fails: "+sum[1] + ", suspicious "+gz.getSuspiciousStatements().size());
		
		DecimalFormat df = new DecimalFormat( "#.###" );
		for (Statement s : gz.getSuspiciousStatements()) {
			String compName = s.getMethod().getParent().getLabel();
			String[] seg = compName.split("\\.");
			String name = seg[seg.length-1];
			if (s.getSuspiciousness() > TransformationProperties.THRESHOLD_SUSPECTNESS 
					&& !compName.toLowerCase().endsWith("test") 
					&& !name.toLowerCase().startsWith("test")
					//
					&& !name.toLowerCase().startsWith("validate")
					) {
				logger.debug("Suspicious: line " + compName + " l: " + s.getLineNumber() + ", susp "
						+ df.format(s.getSuspiciousness()));
				SuspiciousCode c = new SuspiciousCode(compName,s.getMethod().toString(), s.getLineNumber(), s.getSuspiciousness());
				candidates.add(c);
			}
		}
		
		Collections.sort(candidates, new ComparatorCandidates());
		return candidates;
	}

	public class ComparatorCandidates implements Comparator<SuspiciousCode>{

		@Override
		public int compare(SuspiciousCode o1, SuspiciousCode o2) {
			if(o1 == null || o2 == null ){
				return 0;
			}
		return Double.compare(o2.getSuspiciousValue(),o1.getSuspiciousValue());	
		}

	
		
	}
	
}
