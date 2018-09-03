package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.statelessexplore;

import java.util.*;

//import org.apache.log4j.Logger;
@SuppressWarnings("rawtypes")
public class Explorer {
	
//	protected static Logger log = Logger.getLogger(Explorer.class.getName());
    private static boolean m_multipleRuns = false;
	private static ArrayList m_counters;
    private static int m_counterPointer;
    
    private static Counter get(int i) {
    	return (Counter)m_counters.get(i);
    }

    public static void backtrack() throws BacktrackException {
    	throw new BacktrackException();
    }

    public static boolean incrementCounter() {
    	
    	if (m_counters.size() == 0) { //if there's not a counter at the end of an execution, then end the run
    		return false;
    	}
        
	    //remove the last elements of the m_counter if it is at its max value before increment
		while (m_counters.size() > 0) {
			int lastIndex = m_counters.size() - 1;	
			try{
			    Counter last = get(lastIndex);
			    if (last.getValue() == (last.getMax() - 1)) {
			       m_counters.remove(last); 
			     } else {
			       break;
			     }
			 } catch (Exception e) {}
		}	
		
		int i = 0;
		boolean atMax = true;
		
		// Check to see if ALL counters has reached their maximum
		while (atMax && (i < m_counters.size())) {
		    if (get(i).getValue() != (get(i).getMax() - 1)) {
	//			log.debug("Counter with max " + (get(i).getMax() - 1) + " has value " + get(i).getValue());
		    	atMax = false;
		    	break;
		    }
		    i++;
		}
		if (atMax) {
	//		log.debug("have reached the maximal vlaue for each counter, thus return");
		    return false;
		}
		
		get(m_counters.size() - 1).increment();
	
		m_counterPointer = 0;
		
		return true;
    }
    
    public static void initialize() {
        initialize(false);
    }

    public static void initialize(boolean mRuns) {
    	m_multipleRuns = mRuns;
    	m_counters = new ArrayList();
    	m_counterPointer = 0;
    }
    
    @SuppressWarnings("unchecked")
	public static int choose(int _max) {
		int value;
	
		if (m_counterPointer >= m_counters.size()) {   //add a new counter if reaches a new choose call
			m_counters.add(new Counter(_max));
		    value = 0;
		} else {                                      //read from a current pointer  
		    value = get(m_counterPointer).getValue();   
		}
		m_counterPointer++;
		return value;
    }
    
    public static int getCounterPointer() {
    	return m_counterPointer;
    }
}

