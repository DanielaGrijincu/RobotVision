/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.Serializable;
import libsvm.svm_node;

/**
 *
 * @author Danna
 */
public class ImageFeature implements Serializable {
    
    private svm_node[] featureVector;
    private int label;

    public ImageFeature(svm_node[] vector, int l){
        
        this.featureVector  = new svm_node[vector.length];
        System.arraycopy(vector, 0, this.featureVector, 0, vector.length);
        
        this.label = l;
    }
    public ImageFeature(svm_node[] vector){
        
        this.featureVector  = new svm_node[vector.length];
        System.arraycopy(vector, 0, this.featureVector, 0, vector.length);
    }
    
    public svm_node[] getFeature(){
        return this.featureVector;
    }
    
    public int getAnnotation(){
        return this.label;
    }
}
