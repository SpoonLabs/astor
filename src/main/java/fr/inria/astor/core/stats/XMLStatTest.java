package fr.inria.astor.core.stats;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class XMLStatTest {

	@Test
	public void processStats() throws Exception{
		int[] ids = new int[]{
				198,238,240,/*243,*/273,280,288,309,340,644,691,722, 780, 836,
				428,719,746
				//Excluded 252, 780
				
		};
			
		
		for (int i : ids) {
			processStats(i);
				
		}
		
		for (String s : rowsPatches) {
			System.out.println(s);
		}
		System.out.println("***");
	
		for (String s : rowssc) {
			System.out.println(s);
		}
		System.out.println("***");
		for (String s : rowsspace) {
			System.out.println(s);
		}
		System.out.println("***");
		for (String s : rowstime) {
			System.out.println(s);
		}
		System.out.println("***");
		for (String s : rowsval) {
			System.out.println(s);
		}
		System.out.println("***");
		for (String s : rowsefforttest) {
			System.out.println(s);
		}
		
		
		System.out.println("******");
		
	}
	List<String> rowsPatches = new ArrayList<String>();
	List<String> rowssc = new ArrayList<String>();
	List<String> rowsval = new ArrayList<String>();
	List<String> rowstime = new ArrayList<String>();
	List<String> rowsspace = new ArrayList<String>();
	List<String> rowsefforttest = new ArrayList<String>();
	
	
	public void processStats(int id) throws Exception{
		String experimentDataLocation =  //"/home/matmarti/develop/mut/out_mut_unbalanced"+File.separator;
				 //"/home/matmarti/develop/mut/paper_runs/out_100_2" +
				"C:/Personal/develop/out_100_2/out_100_2"+
				//"/home/matmarti/develop/mut/paper_runs/out_100_2" +
				File.separator;

		System.out.println("----"+id);
		System.out.println("*****************GP");
		XMLStat st1 = new XMLStat();
		st1.processStats(experimentDataLocation+"GenProgLoopExpressionProjectTest/"+id+"/statistics_result.xml");
		if(rowsPatches.isEmpty()){
		rowsPatches.add(st1.rowPatcht);
		rowssc.add(st1.rowsct);
		rowsspace.add(st1.rowSpacet);
		rowstime.add(st1.rowTimet);
		rowsval.add(st1.rowvalidationt);
		rowsefforttest.add(st1.rowEffortTestt);
		}
		addRow("GenProg", id, st1);
		System.out.println("*****************Mut");
		XMLStat st2 = new XMLStat();
		st2.processStats(experimentDataLocation+"MutationEvolutionaryTest/"+id+"/statistics_result.xml");
		addRow("Mutation", id, st2);
		System.out.println("*****************PAR");
		XMLStat st3 = new XMLStat();
		st3.processStats(experimentDataLocation+"ParEvolutionaryTest/"+id+"/statistics_result.xml");
		addRow("PAR", id, st3);
		addBL();

	}
	
	public void processStatsMut(int id) throws Exception{
		String experimentDataLocation =  "/home/matmarti/develop/mut/out_mut_balanced"+File.separator;
				// "/home/matmarti/develop/mut/paper_runs/out_100"+File.separator;
		System.out.println("*****************Mut");
		XMLStat st1 = new XMLStat();
		st1.processStats(experimentDataLocation+"MutationEvolutionaryTest/"+id+"/statistics_result.xml");
		if(rowsPatches.isEmpty()){
			rowsPatches.add(st1.rowPatcht);
			rowssc.add(st1.rowsct);
			rowsspace.add(st1.rowSpacet);
			rowstime.add(st1.rowTimet);
			rowsval.add(st1.rowvalidationt);
			rowsefforttest.add(st1.rowEffortTestt);
			}
		addRow("Mutation", id, st1);
		
		addBL();

	}
	
	@Test
	public void test1() throws Exception{
		XMLStat st2 = new XMLStat();
		st2.processStats("/home/matmarti/develop/mut/outtest/GenProgLoopExpressionProjectTest2/280/statistics_result.xml");
		
	}
	
	public void addBL(){
		rowsPatches.add("\\hline");
		rowssc.add("\\hline");
		rowsspace.add("\\hline");
		rowstime.add("\\hline");
		rowsval.add("\\hline");
		rowsefforttest.add("\\hline");
	}
	
	public void addRow(String method, int id, XMLStat stat){
		String rpatch = id+"&"+method+"&"+stat.rowPatch+"\\\\";
		rowsPatches.add(rpatch);
		
		String rtime = id+"&"+method+"&"+stat.rowTime+"\\\\";
		rowstime.add(rtime);
		
		String rspace = id+"&"+method+"&"+stat.rowSpace+"\\\\";
		rowsspace.add(rspace);

		String rsc = id+"&"+method+"&"+stat.rowsc+"\\\\";
		rowssc.add(rsc);
		
		String r = id+"&"+method+"&"+stat.rowvalidation+"\\\\";
		rowsval.add(r);
		
		String e = id+"&"+method+"&"+stat.rowEffortTest+"\\\\";
		rowsefforttest.add(e);
	}
	
}
