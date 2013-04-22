/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kernels;

import SIFT.Feature;
import Utils.ImageFeature;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author Danna
 */
public class MatchKernel extends MyKernel {

    
     @Override
    protected double compute(Vector<Feature> L1, Vector<Feature> L2) {
        
        double val1 = computeMinorKernel(L1, L2);
        double val2 = computeMinorKernel(L2, L1);
        
        return ((double)1 / (double)2) * (val1 + val2);
//           Vector< PointMatch > candidates1 = SIFT.createMatches(L1, L2, 1.5f, null, Float.MAX_VALUE );
//           Vector< PointMatch > candidates2 = SIFT.createMatches(L2, L2, 1.5f, null, Float.MAX_VALUE );
//           
//           double val1 = (double)candidates1.size() / (double)L1.size(); 
//           double val2 = (double)candidates2.size() / (double)L2.size(); 
//           
//           return ((double)1 / (double)2) * (val1 + val2);
   
    }
    
    protected double compute(ArrayList<ImageFeature> L1, ArrayList<ImageFeature> L2){
        
        double val1 = computeMinorKernel(L1, L2);
        double val2 = computeMinorKernel(L2, L1);
        
        return ((double)1 / (double)2) * (val1 + val2);
    }
    
    private double computeMinorKernel(ArrayList<ImageFeature> L1, ArrayList<ImageFeature> L2){
        
        int n1 = L1.size();
        int n2 = L2.size();
        
        RBFKernel rbf = new RBFKernel();
        double sum = 0;
        
        for (int i = 0; i < n1; i++){
            
            double max = 0;
            
            for (int j = 0; j < n2; j++){
                double val = rbf.compute(L1.get(i), L2.get(j));
                
                if (val > max)
                    max = val;
            }
            
            sum += max;
        }
        
        return (sum / (double)n1);
        
    }

    private double computeMinorKernel(Vector<Feature> L1, Vector<Feature> L2){
        
        int n1 = L1.size();
        int n2 = L2.size();
        
        RBFKernel rbf = new RBFKernel();
        
        double sum = 0;
        
        for (int i = 0; i < n1; i++){
            
            double max = 0;
            
            for (int j = 0; j < n2; j++){
                
                double val = rbf.compute(L1.get(i).descriptor, L2.get(j).descriptor);
                               
                if (val > max)
                   max = val;
            }
            
            sum += max;
            
        }
        
        return (double)sum / (double)n1;
        
    }

    @Override
    protected double compute(float[] feature1, float[] feature2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
    @Override
    protected double compute(ImageFeature f1, ImageFeature f2) {
       throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    protected float compute(String featureFile1, String featureFile2) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
       
}
