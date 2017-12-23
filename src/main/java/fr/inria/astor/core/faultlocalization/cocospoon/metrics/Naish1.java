package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Naish1 implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        // return np if ef == 0 else -1
        if(ef == 0)
            return np;
        return -1;
    }
}
