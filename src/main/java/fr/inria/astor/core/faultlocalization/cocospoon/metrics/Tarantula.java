package fr.inria.astor.core.faultlocalization.cocospoon.metrics;

/**
 * Created by spirals on 24/07/15.
 */
public class Tarantula implements Metric {
    @Override
    public double value(int ef, int ep, int nf, int np) {
        // (ef/float(ef+nf + smooth))/float((ef/float(ef+nf + smooth))+(ep/float(ep+np + smooth)) + smooth)
        if(ef+nf == 0) {
            return 0;
        }
        return (ef / ((double) (ef + nf))) / ((ef / ((double)(ef - nf))) + (ep/ ((double) ep + np))) ;
    }
}
