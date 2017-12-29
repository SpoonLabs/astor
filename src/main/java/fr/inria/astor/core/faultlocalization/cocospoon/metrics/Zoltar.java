package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Zoltar implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        return ef / (ef + nf + ep + (10000 * nf * ep)/(double)ef);
    }
}
