package fr.inria.astor.test.repair.approaches.scaffold;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.ScaffoldSynthesisEntry;

public class SimpleSketch {

	    public int simpleExpError() {
	        int a = 2;
	        int b = 1;
	        // expect to have int c = a;
	        int c = ((Integer) ScaffoldSynthesisEntry.EXP(new Object[] { b, a }, 0, new String[] { "b", "a" }, int.class).invoke());
	        return c;
	    }
	}
