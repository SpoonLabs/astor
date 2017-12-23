package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public interface Metric {
    double value(int ef, int ep, int nf, int np);
}
