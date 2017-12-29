package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Kulczynski1 implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        return ef / ((double) (nf + ep));
    }
}
