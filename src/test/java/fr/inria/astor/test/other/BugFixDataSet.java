package fr.inria.astor.test.other;


/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class BugFixDataSet {

	static String mvnpath ="C:\\Users\\adam\\.m2\\repository\\";
	
	// 0 ID,  1- failing test , 2- regression (used only for gzoltar), 3- threshold (optional)
	public static String[][] data = new String[][] {
		
			{ "280", "org.apache.commons.math.distribution.NormalDistributionTest",
					"org.apache.commons.math.distribution.NormalDistributionTest","0.5",
					mvnpath+"\\junit\\junit\\4.4\\junit-4.4.jar\\"		
			}
			
	
						
	};

	public static String[] getDataForId(String id) {
		int i = 0;
		for (String[] s : data) {
			if (s[0].equals(id)) {
				return data[i];
			}
			i++;
		}
		return null;
	}
}
