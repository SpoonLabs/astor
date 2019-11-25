package fr.inria.astor.approaches.extensions.rt.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.util.MapList;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RuntimeInformation {
	public List<String> allTestCases = new ArrayList();

	public List<String> allTestCasesWithoutParent = null;
	public MapList<String, Integer> mapLinesCovered = new MapList<>();
	public Map<String, SuspiciousCode> mapCacheSuspicious = new HashMap<>();
	public MapList<String, String> passingCoveredTestCaseFromClass = new MapList<>();
	public List<String> notexec = new ArrayList<>();

	public RuntimeInformation(List<String> allTestCase, List<String> allTestCasesWithoutParent,
			MapList<String, Integer> mapLinesCovered, Map<String, SuspiciousCode> mapCacheSuspicious,
			MapList<String, String> passingCoveredTestCaseFromClass, List<String> notexec) {
		super();
		this.allTestCases = allTestCase;
		this.allTestCasesWithoutParent = allTestCasesWithoutParent;
		this.mapLinesCovered = mapLinesCovered;
		this.mapCacheSuspicious = mapCacheSuspicious;
		this.passingCoveredTestCaseFromClass = passingCoveredTestCaseFromClass;
		this.notexec = notexec;
	}

}