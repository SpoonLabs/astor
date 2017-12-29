package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Jaccard implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        // ef / float(ef + ep + nf)
        return ef / ((double) (ef + ep + nf));
    }
}
