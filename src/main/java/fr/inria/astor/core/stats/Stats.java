package fr.inria.astor.core.stats;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class Stats {
	
	private static Logger log = Logger.getLogger(Stats.class.getName());

	public static Stats currentStat = null;
	
	public static Stats getCurrentStats(){
		
		if(currentStat == null){
			currentStat = new Stats();
		}
		return currentStat;
	}
	
	public int id = 0;
		
	public int numberOfElementsToMutate = 0;
	public List<StatSpaceSize> sizeSpace = new ArrayList<StatSpaceSize>();
	
	public int numberOfRightCompilation = 0;
	public int numberOfFailingCompilation = 0;
	public  int numberOfFailingTestCaseExecution = 0;
	public int numberOfRegressionTestExecution = 0;
	public  int numberOfFailingTestCase = 0;
	public int numberOfRegressionTestCases = 0;
	
	public  int numberGenerations = 0;
	
	public  int patches = 0;
	public  List<StatPatch> genPatches = new ArrayList<StatPatch>(); 
	
	public  int numberOfAppliedOp = 0;
	public  int numberOfNotAppliedOp = 0;
	//this property contains the number of gen that the approach try to mutate but it could. For instance, it takes as same ingredient one element that gen already containe
	public int numberOfGenInmutated = 0;
	public  List<Long> time1Validation = new ArrayList<Long>(); 
	public  List<Long> time2Validation = new ArrayList<Long>(); 
	public long timeIteraction;
	//
	public double fl_threshold;
	public int fl_size;
	public int fl_gens_size;
	
	public int passFailingval1 = 0;
	public int passFailingval2 = 0;
	
	public int numberOfTestcasesExecutedval1 = 0;
	public int numberOfTestcasesExecutedval2 = 0;
	
	public void printStats(){
		log.info(toString());
	}
	
	public String toString(){
		String s = "";
		s+=("\nspaces: ["+this.sizeSpace.size()+"]: "+this.sizeSpace);
		s+=("\ntime val1 ["+this.time1Validation.size()+"]: "+this.time1Validation);
		s+=("\ntime val2 ["+this.time2Validation.size()+"]: "+this.time2Validation);
		s+=("\n#gen: "+this.numberGenerations);
		s+=("\n#patches: "+this.patches);
		s+=("\n#RightCompilation: "+this.numberOfRightCompilation);
		s+=("\n#WrongCompilation: "+this.numberOfFailingCompilation);
		s+=("\n#FailingTestCaseExecution: "+this.numberOfFailingTestCaseExecution);
		s+=("\n#RegressionTestExecution: "+this.numberOfRegressionTestExecution );
		s+=("\n#TestcasesExecutedval1: "+this.numberOfTestcasesExecutedval1);
		s+=("\n#TestcasesExecutedval2: "+this.numberOfTestcasesExecutedval2);
	
		s+=("\n#FailingTestCase: "+this.numberOfFailingTestCase);
		s+=("\n#RegressionTestCases: "+this.numberOfRegressionTestCases);
	
		
		s+=("\n#OfAppliedOp: "+this.numberOfAppliedOp);
		s+=("\n#NotAppliedOp: "+this.numberOfNotAppliedOp);
		s+=("\n#InmutatedGen: "+this.numberOfGenInmutated);
		return s;
	}
	
}

