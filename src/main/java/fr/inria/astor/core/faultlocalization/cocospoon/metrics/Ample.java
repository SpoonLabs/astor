package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Ample implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        // abs((ef/float(ef+nf)) - (ep/float(ep+np)))
        return Math.abs((ef/((double) (ef + nf))) - (ep/((double) (ep + np))));
    }
}
