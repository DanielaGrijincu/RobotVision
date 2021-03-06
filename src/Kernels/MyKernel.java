/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Kernels;

import SIFT.Feature;
import Utils.ImageFeature;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import libsvm.svm_node;
import libsvm.svm_problem;

/**
 *
 * @author Danna
 */
public abstract class MyKernel {

    public static double gamma;

    protected abstract double compute(ImageFeature f1, ImageFeature f2);

    protected abstract double compute(ArrayList<ImageFeature> L1, ArrayList<ImageFeature> L2);

    protected abstract double compute(Vector<Feature> L1, Vector<Feature> L2);

    protected abstract double compute(float[] feature1, float[] feature2);

    protected abstract float compute(String featureFile1, String featureFile2);

    public svm_problem getTrainGramMatrixCRFH(ArrayList<ImageFeature> allImagesFeatures) {
       
        svm_problem svmProblem = new svm_problem();

        svmProblem.l = allImagesFeatures.size();
        svmProblem.x = new svm_node[svmProblem.l][svmProblem.l + 1];
        svmProblem.y = new double[svmProblem.l];

        for (int i = 0; i < svmProblem.l; i++) {

            svmProblem.y[i] = (double) allImagesFeatures.get(i).getAnnotation();

            svmProblem.x[i][0] = new svm_node();
            svmProblem.x[i][0].index = 0;
            svmProblem.x[i][0].value = i + 1;

            svmProblem.x[i][i + 1] = new svm_node();
            svmProblem.x[i][i + 1].index = i + 1;
            svmProblem.x[i][i + 1].value = (float) compute(allImagesFeatures.get(i), allImagesFeatures.get(i));

            System.out.println("Computing distances for image " + i);
            
            for (int j = i + 1; j < svmProblem.l; j++) {
                svmProblem.x[i][j + 1] = new svm_node();
                svmProblem.x[i][j + 1].index = j + 1;
                svmProblem.x[i][j + 1].value = (float) compute(allImagesFeatures.get(i),allImagesFeatures.get(j));

                svmProblem.x[j][i + 1] = new svm_node();
                svmProblem.x[j][i + 1].index = i + 1;
                svmProblem.x[j][i + 1].value = svmProblem.x[i][j + 1].value;
                
            }
        }

        return svmProblem;
    }

    public svm_problem getTrainGramMatrix(ArrayList<AbstractMap.SimpleImmutableEntry<String, Integer>> allFeaturesFiles) {
       
        svm_problem svmProblem = new svm_problem();
        
        svmProblem.l = allFeaturesFiles.size();
        svmProblem.x = new svm_node[svmProblem.l][svmProblem.l + 1];
        svmProblem.y = new double[svmProblem.l];

        for (int i = 0; i < svmProblem.l; i++) {

            svmProblem.y[i] = (double) allFeaturesFiles.get(i).getValue();

            svmProblem.x[i][0] = new svm_node();
            svmProblem.x[i][0].index = 0;
            svmProblem.x[i][0].value = i + 1;

            svmProblem.x[i][i + 1] = new svm_node();
            svmProblem.x[i][i + 1].index = i + 1;
            svmProblem.x[i][i + 1].value = (float) compute(allFeaturesFiles.get(i).getKey(), allFeaturesFiles.get(i).getKey());

            
            System.out.println("Computing distances for image " + i + "...");
            
            double milis = System.currentTimeMillis();
            
            for (int j = i + 1; j < svmProblem.l; j++) {
                svmProblem.x[i][j + 1] = new svm_node();
                svmProblem.x[i][j + 1].index = j + 1;
                svmProblem.x[i][j + 1].value = (float) compute(allFeaturesFiles.get(i).getKey(), allFeaturesFiles.get(j).getKey());

                svmProblem.x[j][i + 1] = new svm_node();
                svmProblem.x[j][i + 1].index = i + 1;
                svmProblem.x[j][i + 1].value = svmProblem.x[i][j + 1].value;
                
             //   System.out.println("Computed distance between image " + i + " and image " + j + " => " + svmProblem.x[i][j + 1].value);
            }
            
            System.out.println("Computed distances for image " + i + " in " + (System.currentTimeMillis() - milis) / 1000  + " seconds.");
        }

        return svmProblem;
    }

    public svm_problem getTrainGramMatrixSIFT(ArrayList<ArrayList<ImageFeature>> allImagesFeatures) {
        svm_problem svmProblem = new svm_problem();

        svmProblem.l = allImagesFeatures.size();
        svmProblem.x = new svm_node[svmProblem.l][svmProblem.l + 1];
        svmProblem.y = new double[svmProblem.l];

        System.out.println("All Images Features size: " + allImagesFeatures.size());

        for (int i = 0; i < allImagesFeatures.size(); i++) {

            svmProblem.y[i] = (double) allImagesFeatures.get(i).get(0).getAnnotation();

            //svm_node[] computedXs = new svm_node[svmProblem.l + 1];
            svmProblem.x[i][0] = new svm_node();
            svmProblem.x[i][0].index = 0;
            svmProblem.x[i][0].value = i + 1;

            svmProblem.x[i][i + 1] = new svm_node();
            svmProblem.x[i][i + 1].index = i + 1;
            svmProblem.x[i][i + 1].value = (float) compute(allImagesFeatures.get(i), allImagesFeatures.get(i));//TopSurfDescriptor.compareDescriptorsCosine(tsImageFeatures1.getDescriptor(), tsImageFeatures2.getDescriptor());

            for (int j = i + 1; j < svmProblem.l; j++) {
                svmProblem.x[i][j + 1] = new svm_node();
                svmProblem.x[i][j + 1].index = j + 1;
                svmProblem.x[i][j + 1].value = (float) compute(allImagesFeatures.get(i), allImagesFeatures.get(j));//TopSurfDescriptor.compareDescriptorsCosine(tsImageFeatures1.getDescriptor(), tsImageFeatures2.getDescriptor());

                svmProblem.x[j][i + 1] = new svm_node();
                svmProblem.x[j][i + 1].index = i + 1;
                svmProblem.x[j][i + 1].value = svmProblem.x[i][j + 1].value;
            }
        }

        return svmProblem;
    }

    public svm_problem getTrainGramMatrixSIFT(HashMap<Vector<Feature>, Integer> allImagesFeatures) {
        svm_problem svmProblem = new svm_problem();

        svmProblem.l = allImagesFeatures.size();
        svmProblem.x = new svm_node[svmProblem.l][svmProblem.l + 1];
        svmProblem.y = new double[svmProblem.l];

        System.out.println("All Images Features size: " + allImagesFeatures.size());

        int i = 0;
        for (Entry<Vector<Feature>, Integer> entryi : allImagesFeatures.entrySet()) {

            svmProblem.y[i] = (double) entryi.getValue();

            //Gram Matrix
            svmProblem.x[i][0] = new svm_node();
            svmProblem.x[i][0].index = 0;
            svmProblem.x[i][0].value = i + 1;
            int j = 0;
            for (Entry<Vector<Feature>, Integer> entryj : allImagesFeatures.entrySet()) {

                svmProblem.x[i][j + 1] = new svm_node();
                svmProblem.x[i][j + 1].index = j + 1;

                svmProblem.x[i][j + 1].value = compute(entryi.getKey(), entryj.getKey());

                System.out.println("Computed distance between image " + i + " and image " + j + " => " + svmProblem.x[i][j + 1].value);
                ++j;
            }
            ++i;
        }

        return svmProblem;
    }

    public svm_problem getPredictGramMatrixCRFH(ArrayList<ImageFeature> allImagesFeatures, ImageFeature imageFeature) {

        svm_problem svmProblem = new svm_problem();

        svmProblem.l = 1;
        svmProblem.x = new svm_node[1][allImagesFeatures.size() + 1];
        svmProblem.y = new double[1];
        svmProblem.y[0] = imageFeature.getAnnotation();

        final svm_node[] gramFeatures = new svm_node[allImagesFeatures.size() + 1];

        gramFeatures[0] = new svm_node();
        gramFeatures[0].index = 0;
        gramFeatures[0].value = 1;

        for (int j = 0; j < allImagesFeatures.size(); j++) {

            gramFeatures[j + 1] = new svm_node();
            gramFeatures[j + 1].index = j + 1;
            gramFeatures[j + 1].value = compute(imageFeature, allImagesFeatures.get(j));

          //  System.out.println("Computed distance with image " + j + " => " + gramFeatures[j + 1].value);
        }

        svmProblem.x[0] = gramFeatures;

        return svmProblem;
    }

    public svm_problem getPredictGramMatrixSIFT(ArrayList<ArrayList<ImageFeature>> allImagesFeatures, ArrayList<ImageFeature> imageFeature) {

        svm_problem svmProblem = new svm_problem();

        svmProblem.l = 1;
        svmProblem.x = new svm_node[1][allImagesFeatures.size() + 1];
        svmProblem.y = new double[1];
        svmProblem.y[0] = imageFeature.get(0).getAnnotation();

        final svm_node[] gramFeatures = new svm_node[allImagesFeatures.size() + 1];

        gramFeatures[0] = new svm_node();
        gramFeatures[0].index = 0;
        gramFeatures[0].value = 1;

        for (int j = 0; j < allImagesFeatures.size(); j++) {

            gramFeatures[j + 1] = new svm_node();
            gramFeatures[j + 1].index = j + 1;
            gramFeatures[j + 1].value = compute(imageFeature, allImagesFeatures.get(j));

            System.out.println("Computed distance with image " + j + " => " + gramFeatures[j + 1].value);
        }

        svmProblem.x[0] = gramFeatures;

        return svmProblem;
    }

    public svm_problem getPredictGramMatrixSIFT(HashMap<Vector<Feature>, Integer> allImagesFeatures, Vector<Feature> imageFeature) {

        svm_problem svmProblem = new svm_problem();

        svmProblem.l = 1;
        svmProblem.x = new svm_node[1][allImagesFeatures.size() + 1];
        svmProblem.y = new double[1];
        svmProblem.y[0] = 0;

        final svm_node[] gramFeatures = new svm_node[allImagesFeatures.size() + 1];

        gramFeatures[0] = new svm_node();
        gramFeatures[0].index = 0;
        gramFeatures[0].value = 1;

        int j = 0;
        for (Entry<Vector<Feature>, Integer> entryi : allImagesFeatures.entrySet()) {

            gramFeatures[j + 1] = new svm_node();
            gramFeatures[j + 1].index = j + 1;
            gramFeatures[j + 1].value = compute(imageFeature, entryi.getKey());

            System.out.println("Computed distance with image " + j + " => " + gramFeatures[j + 1].value);
            ++j;
        }

        svmProblem.x[0] = gramFeatures;

        return svmProblem;
    }
    
     public svm_problem getPredictGramMatrix(ArrayList<SimpleImmutableEntry<String, Integer>> allFeatures, String imageToPredictFile) {
        
        svm_problem svmProblem = new svm_problem();

        svmProblem.l = 1;
        svmProblem.x = new svm_node[1][allFeatures.size() + 1];
        svmProblem.y = new double[1];
        svmProblem.y[0] = 0;

        final svm_node[] gramFeatures = new svm_node[allFeatures.size() + 1];

        gramFeatures[0] = new svm_node();
        gramFeatures[0].index = 0;
   //     gramFeatures[0].value = 1;

        for (int i = 0; i < allFeatures.size(); i++) {

            gramFeatures[i + 1] = new svm_node();
            gramFeatures[i + 1].index = i + 1;
            gramFeatures[i + 1].value = (float) compute(imageToPredictFile, allFeatures.get(i).getKey());
        }
        
        
        svmProblem.x[0] = gramFeatures;

        return svmProblem;
        
    }
}
