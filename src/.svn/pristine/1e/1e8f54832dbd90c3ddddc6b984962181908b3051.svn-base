/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kernels;

import SIFT.Feature;
import Utils.ImageFeature;
import java.util.ArrayList;
import java.util.Vector;
import libsvm.svm_node;

/**
 *
 * @author Danna
 */
public class HistogramDistanceKernel extends MyKernel {
    
    protected double compute(ImageFeature f1, ImageFeature f2)
    {
        svm_node[] x = f1.getFeature();
        svm_node[] y = f2.getFeature(); 
        
        double sum = 0;
        int xlen = x.length;
        int ylen = y.length;
        int i = 0;
        int j = 0;
        while (i < xlen && j < ylen) {
            if (x[i].index == y[j].index) {
                sum += Math.pow(x[i].value - y[j].value, 2) / (x[i].value + y[j].value);
                i++;
                j++;
            } else {
                if (x[i].index > y[j].index) {
                   // sum += y[j].value;
                    ++j;
                } else {
                   // sum += x[i].value;
                    ++i;
                }
            }
        }
        
//        if (i < xlen)
//        {
//            sum += x[i].value;
//        }
//        
//        if (j < ylen)
//        {
//            sum += y[j].value;
//        }
        
        return Math.exp(-1 * sum);
        
    }

    @Override
    protected double compute(ArrayList<ImageFeature> L1, ArrayList<ImageFeature> L2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double compute(Vector<Feature> L1, Vector<Feature> L2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected double compute(float[] feature1, float[] feature2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected float compute(String featureFile1, String featureFile2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
