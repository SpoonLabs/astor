package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.statelessexplore;

public class Counter {
	private int m_max;
	private int m_value = 0;
	
	public Counter(int _max) {
	    if (_max < 0) { 
			throw new RuntimeException("_max < 0");
		}
	    m_max = _max + 1;     
	}
	
	public boolean increment(){
	    m_value += 1;
	    if (m_value >= m_max) {
	    	m_value -= m_max;
	    	return true;
	    } 
	    return false;
	}

	public int getValue() {
	    return m_value;
	}

	public int getMax() {
	    return m_max;
	}
	
	public String toString() {
		return "m(" + (m_max - 1)+ ") v(" + m_value + ")";
	}
}

