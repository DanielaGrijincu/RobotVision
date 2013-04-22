/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import SIFT.Feature;
import libsvm.svm_node;

/**
 *
 * @author Danna
 */
public class Norms {
    
   public static double computeL2Norm(ImageFeature feature1, ImageFeature feature2) {

        svm_node[] f1 = feature1.getFeature();
        svm_node[] f2 = feature2.getFeature();
        
        double d = 0;
        for (int i = 0; i < f1.length; ++i) {
            double a = f1[i].value - f2[i].value;
            d += a * a;
        }
        return Math.sqrt(d);
    }
   
   public static double computeRbfDistance(float[] f1, float[] f2) {

        
        double d = 0;
        for (int i = 0; i < f1.length; ++i) {
            double a = f1[i] - f2[i];
            d += a * a;
        }
        
        double gamma = (double)3;
        
        return Math.exp(-d * gamma);
    }
}
