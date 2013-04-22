/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kernels;

import NARF.NarfDescriptor;
import SIFT.Feature;
import Utils.ImageFeature;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Danna
 */
public class NARFKernel extends MyKernel{
    
    private float threshold = (float) 0.75;

    @Override
    protected double compute(ImageFeature f1, ImageFeature f2) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        
        return NarfDescriptor.compareDescriptors(featureFile1, featureFile2, threshold);
    }
    
}
