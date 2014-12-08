package fr.inria.astor.core.loop.mutation.muttest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MuTestingResult {

	public List<String> killedTestCases = new ArrayList<>();
	public List<String> alivedTestCases = new ArrayList<>();
	
	public Map<String, List> failingTestCases = new HashMap<String, List>();
	
	public List<String> notFinishTestCases = new ArrayList<>();
	

}
