package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Hamming implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        return  ef + np;
    }
}
