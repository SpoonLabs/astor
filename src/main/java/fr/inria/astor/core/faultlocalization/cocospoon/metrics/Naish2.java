package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Naish2 implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        // ef - (ep / float(ep + np + 1))
        return ef - (ep / ((double) (ep + np + 1)));
    }
}
