/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SVM;

import Exceptions.SvmInvalidParameterException;
import Kernels.ASIFTKernel;
import Kernels.MyKernel;
import SIFT.Feature;
import Utils.ImageFeature;
import Utils.Serialization;
import java.io.IOException;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import libsvm.*;

public class SvmTrain {

    private svm_problem svmProblem;
    private svm_parameter svmParameter;
    private svm_model svmModel;
    private int cross_validation;
    private final int C = 10;
    private static svm_print_interface printInterface;


    public SvmTrain(svm_print_interface printInterface) {
        SvmTrain.printInterface = printInterface;
        svm.svm_set_print_string_function(printInterface);
    }

     public svm_model trainASIFT(ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles, MyKernel kernel) throws SvmInvalidParameterException {
     
        svmParameter = new svm_parameter();
        svmModel = new svm_model();
        svm.svm_set_print_string_function(printInterface);

        svmParameter.degree = 3;
        svmParameter.gamma = 0;	// 0 1/num_features
        svmParameter.coef0 = 0;
        svmParameter.nu = 0.7; //0.5 was default
        svmParameter.cache_size = 100;
        svmParameter.C = this.C;
        svmParameter.eps = 1e-3;
        svmParameter.p = 0.1;
        svmParameter.shrinking = 0;
        svmParameter.probability = 1;
        svmParameter.nr_weight = 0;

        svmParameter.weight_label = new int[0]; //labels;
        svmParameter.weight = new double[0]; //weights;
        cross_validation = 0;

        if (kernel != null) {

            svmParameter.svm_type = svm_parameter.C_SVC;
            svmParameter.kernel_type = svm_parameter.PRECOMPUTED;

            try {

              //  svmProblem = (svm_problem) Serialization.deserialize("output NARF\\svmProblem");
              //  this.doGridSearch(svmProblem, svmParameter, 1, 200, 10);

                System.out.println("Started computing Gram Matrix.");
                
                svmProblem = kernel.getTrainGramMatrix(allFeaturesFiles);

                System.out.println("Serializing Gram Matrix...");

                Serialization.serialize(svmProblem, "output ASIFT\\asiftProblem");
                
                Serialization.saveGramMatrix(svmProblem, "output ASIFT\\GramMatrix.txt");

                System.out.println("Finished serializing Gram Matrix.");

            } 
//catch (ClassNotFoundException ex) {
//                ex.printStackTrace();
//                System.out.println(ex.toString());
//            } 
            catch (IOException ex) {
                ex.printStackTrace();
                System.out.println(ex.toString());
            }

            //  System.out.println("Finished computing Gram Matrix..");

        } else {
            svmParameter.kernel_type = svm_parameter.RBF;
            //   initializeSvmProblem(allImagesFeatures);
        }


        String errorMsg = svm.svm_check_parameter(svmProblem, svmParameter);

        if (errorMsg != null) {
            throw new SvmInvalidParameterException(errorMsg);
        }

        svmModel = svm.svm_train(svmProblem, svmParameter);

        return svmModel;

    }
     
    public svm_model trainNARF(ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles, MyKernel kernel) throws SvmInvalidParameterException {

        svmParameter = new svm_parameter();
        svmModel = new svm_model();
        svm.svm_set_print_string_function(printInterface);

        svmParameter.degree = 3;
        svmParameter.gamma = 1/36;	// 0 1/num_features
        svmParameter.coef0 = 0;
        svmParameter.nu = 0.7; //0.5 was default
        svmParameter.cache_size = 100;
        svmParameter.C = this.C;
        svmParameter.eps = 1e-3;
        svmParameter.p = 0.1;
        svmParameter.shrinking = 0;
        svmParameter.probability = 1;
        svmParameter.nr_weight = 9;

        int[] weight_label = new int[svmParameter.nr_weight];
        for (int i = 0; i < svmParameter.nr_weight; i++)
            weight_label[i] = i;
        
        double[] weight = new double[svmParameter.nr_weight];
        weight[0] = 2;
        weight[1] = 1;
        weight[2] = 1;
        weight[3] = 1;
        weight[4] = 4;
        weight[5] = 1;
        weight[6] = 1;
        weight[7] = 1;
        weight[8] = 1;
        
        
        svmParameter.weight_label = weight_label; //labels;
        svmParameter.weight = weight; //weights;
        cross_validation = 0;

        if (kernel != null) {

            svmParameter.svm_type = svm_parameter.C_SVC;
            svmParameter.kernel_type = svm_parameter.PRECOMPUTED;

            try {
                
                System.out.println("Deserializing Gram Matrix...");
                svmProblem = (svm_problem) Serialization.deserialize("output NARF\\svmProblem");
                
                //this.doCrossValidation(svmProblem, svmParameter, 3);
           
                  this.doGridSearch(svmProblem, svmParameter, 1, 1000, 10);

//                System.out.println("Started computing Gram Matrix.");
//                
//                svmProblem = kernel.getTrainGramMatrix(allFeaturesFiles);
//
//                System.out.println("Serializing Gram Matrix...");
//
//                Serialization.serialize(svmProblem, "output NARF\\svmProblem");
//                Serialization.saveGramMatrix(svmProblem, "output NARF\\GramMatrix.txt");
//
//                System.out.println("Finished serializing Gram Matrix.");

            } 
            catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                System.out.println(ex.toString());
            } 
            catch (IOException ex) {
                ex.printStackTrace();
                System.out.println(ex.toString());
            }

            //  System.out.println("Finished computing Gram Matrix..");

        } else {
            svmParameter.kernel_type = svm_parameter.RBF;
            //   initializeSvmProblem(allImagesFeatures);
        }


        String errorMsg = svm.svm_check_parameter(svmProblem, svmParameter);

        if (errorMsg != null) {
            throw new SvmInvalidParameterException(errorMsg);
        }

 //       svmModel = svm.svm_train(svmProblem, svmParameter);

        return svmModel;

    }

    public svm_model trainCRFH(ArrayList<ImageFeature> allImagesFeatures, MyKernel kernel, boolean scale) throws SvmInvalidParameterException, IOException, ClassNotFoundException {

        if (scale) {
            SvmScale svmScale = new SvmScale();
            allImagesFeatures = svmScale.scaleImageFeatures((ArrayList<ImageFeature>) allImagesFeatures);
        }

        setParameters(allImagesFeatures, kernel);
        
     //   this.doGridSearch(svmProblem, svmParameter, 1, 100, 10);
        
        svmModel = svm.svm_train(svmProblem, svmParameter);

     //   Cross Validation
//           System.out.println("Started Cross Validation...");
//           this.doCrossValidation(svmProblem, svmParameter, 10);
//           System.out.println("Finished Cross Validation.");

        return svmModel;
    }

//   public svm_model trainFeatureArrayList(ArrayList<ArrayList<ImageFeature>> allImagesFeatures, MyKernel kernel,  boolean scale) throws SvmInvalidParameterException{
//        
//        svmParameter = new svm_parameter();
//        svmModel = new svm_model();
//        svm.svm_set_print_string_function(printInterface);
//        
//        svmParameter.degree = 3;
//        svmParameter.gamma = 0;	// 0 1/num_features
//        svmParameter.coef0 = 0;
//        svmParameter.nu = 0.7; //0.5 was default
//        svmParameter.cache_size = 100;
//        svmParameter.C = this.C;
//        svmParameter.eps = 1e-3;
//        svmParameter.p = 0.1;
//        svmParameter.shrinking = 0;
//        svmParameter.probability = 1;
//        svmParameter.nr_weight = 0;
//        
//        int maxIndex = 0;
//
//        for (ArrayList<ImageFeature> imageFeatures : allImagesFeatures) {
//            maxIndex = Math.max(maxIndex, imageFeatures.size());
//        }
//        
//        if (svmParameter.gamma == 0 && maxIndex > 0) {
//            svmParameter.gamma = 1.0 / maxIndex;
//        }
//             
//        svmParameter.weight_label = new int[0]; //labels;
//        svmParameter.weight = new double[0]; //weights;
//        cross_validation = 0;
//
//        if (kernel != null) {
//            
//            svmParameter.svm_type = svm_parameter.C_SVC; 
//            svmParameter.kernel_type = svm_parameter.PRECOMPUTED;
//            
//            double millis = System.currentTimeMillis();
//            int seconds = (int) (millis / 1000) % 60;
//            int minutes = (int) ((millis / (1000 * 60)) % 60);
//            int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
//
//            System.out.println(hours + ":" + minutes + ":" + seconds + " Started computing Gram Matrix.");
//            
//            svmProblem = kernel.getTrainGramMatrixSIFT(allImagesFeatures);
//            
//            millis = System.currentTimeMillis();
//            seconds = (int) (millis / 1000) % 60;
//            minutes = (int) ((millis / (1000 * 60)) % 60);
//            hours = (int) ((millis / (1000 * 60 * 60)) % 24);
//
//            System.out.println(hours + ":" + minutes + ":" + seconds + " Finished computing Gram Matrix..");
//            
//        } else {
//            svmParameter.kernel_type = svm_parameter.RBF;
//         //   initializeSvmProblem(allImagesFeatures);
//        }
// 
//       
//        String errorMsg = svm.svm_check_parameter(svmProblem, svmParameter);
//
//        if (errorMsg != null) {
//            throw new SvmInvalidParameterException(errorMsg);
//        }
//
//        svmModel = svm.svm_train(svmProblem, svmParameter);
//        
//        return svmModel;
//    }
    //SIFT TRAIN
    public svm_model trainSIFT(HashMap<Vector<Feature>, Integer> allImagesFeatures, MyKernel kernel, boolean scale) throws SvmInvalidParameterException {

        svmParameter = new svm_parameter();
        svmModel = new svm_model();
        svm.svm_set_print_string_function(printInterface);

        svmParameter.svm_type = svm_parameter.C_SVC;
        svmParameter.degree = 3;
        svmParameter.gamma = 0;	// 0 1/num_features
        svmParameter.coef0 = 0;
        svmParameter.nu = 0.5; //0.5 was default
        svmParameter.cache_size = 1000;
        svmParameter.C = this.C;
        svmParameter.eps = 1e-3;
        svmParameter.p = 0.1;
        svmParameter.shrinking = 0;
        svmParameter.probability = 1;
        svmParameter.nr_weight = 0;

        int maxIndex = 0;

        for (Map.Entry<Vector<Feature>, Integer> entry : allImagesFeatures.entrySet()) {
            maxIndex = Math.max(maxIndex, entry.getKey().size());
        }

        if (svmParameter.gamma == 0 && maxIndex > 0) {
            svmParameter.gamma = 1.0 / maxIndex;
        }

        svmParameter.weight_label = new int[0]; //labels;
        svmParameter.weight = new double[0]; //weights;
        cross_validation = 0;

        if (kernel != null) {

            svmParameter.kernel_type = svm_parameter.PRECOMPUTED;
            System.out.println("Started Grid Search for C and kernel gamma.");
            doGridSearch(allImagesFeatures, kernel, svmParameter, 0, 1000, 1.5, 30.0);
            System.out.println("Finished Grid Search for C and kernel gamma.");


//            double millis = System.currentTimeMillis();
//            int seconds = (int) (millis / 1000) % 60;
//            int minutes = (int) ((millis / (1000 * 60)) % 60);
//            int hours = (int) ((millis / (1000 * 60 * 60)) % 24);
//
//            System.out.println(hours + ":" + minutes + ":" + seconds + " Started computing Gram Matrix.");
//            
//            
//            svmProblem = kernel.getTrainGramMatrixSIFT(allImagesFeatures);
//            
//            millis = System.currentTimeMillis();
//            seconds = (int) (millis / 1000) % 60;
//            minutes = (int) ((millis / (1000 * 60)) % 60);
//            hours = (int) ((millis / (1000 * 60 * 60)) % 24);
//
//            System.out.println(hours + ":" + minutes + ":" + seconds + " Finished computing Gram Matrix..");

        } else {
            svmParameter.kernel_type = svm_parameter.RBF;
            //   initializeSvmProblem(allImagesFeatures);
        }


        String errorMsg = svm.svm_check_parameter(svmProblem, svmParameter);

        if (errorMsg != null) {
            throw new SvmInvalidParameterException(errorMsg);
        }

        //     svmModel = svm.svm_train(svmProblem, svmParameter);

        return svmModel;
    }

    private void setParameters(ArrayList<ImageFeature> allImagesFeatures, MyKernel kernel) throws SvmInvalidParameterException, IOException, ClassNotFoundException {

        this.svmParameter = new svm_parameter();
        svm.svm_set_print_string_function(printInterface);

        svmParameter.degree = 3;
        svmParameter.gamma = 0;	// 0 1/num_features
        svmParameter.coef0 = 0;
        svmParameter.nu = 0.7; //0.5 was default
        svmParameter.cache_size = 100;
        svmParameter.C = this.C;
        svmParameter.eps = 1e-3;
        svmParameter.p = 0.1;
        svmParameter.shrinking = 0;
        svmParameter.probability = 1;
        cross_validation = 0;
        svmParameter.nr_weight = 0;

       

//        int[] weight_label = new int[svmParameter.nr_weight];
//        for (int i = 0; i < svmParameter.nr_weight; i++)
//            weight_label[i] = i;
//        
//        double[] weight = new double[svmParameter.nr_weight];
//        weight[0] = 2;
//        weight[1] = 1;
//        weight[2] = 1;
//        weight[3] = 1;
//        weight[4] = 4;
//        weight[5] = 1;
//        weight[6] = 1;
//        weight[7] = 1;
//        weight[8] = 1;
        
        svmParameter.weight_label = new int[0];
        svmParameter.weight = new double[0];
        
        int maxIndex = 0;

        for (ImageFeature imageFeatures : allImagesFeatures) {
            maxIndex = Math.max(maxIndex, imageFeatures.getFeature().length);
        }

        if (svmParameter.gamma == 0 && maxIndex > 0) {
            svmParameter.gamma = 1.0 / maxIndex;
        }

       
        if (kernel != null) {

            svmParameter.svm_type = svm_parameter.C_SVC;
            svmParameter.kernel_type = svm_parameter.PRECOMPUTED;

           // double millis = System.currentTimeMillis();
            System.out.println("Started computing Gram Matrix.");
            
            svmProblem = kernel.getTrainGramMatrixCRFH(allImagesFeatures);
           
            System.out.println("Finished computing Gram Matrix...");
           
          //  this.svmProblem = (svm_problem) Serialization.deserialize("output CRFH\\crfh_svmProblem"); //kernel.getTrainGramMatrixCRFH(allImagesFeatures);

//            try {
//
//                System.out.println("Serializing...");
//                Serialization.serialize(svmProblem, "output CRFH\\crfh_svmProblem");
//                Serialization.saveGramMatrix(svmProblem, "output CRFH\\crfh_svmGramMatrix.txt");
//
//            } catch (IOException ex) {
//                System.out.println(ex.toString());
//            }
         //    System.out.println("Finished deserializing svm_problem.");
          //  System.out.println("Finished computing Gram Matrix in " + (System.currentTimeMillis() - millis) / 1000 + " seconds.");

        } else {
            svmParameter.svm_type = svm_parameter.C_SVC;
            svmParameter.kernel_type = svm_parameter.RBF;
            initializeSvmProblem(allImagesFeatures);
        }

        String errorMsg = svm.svm_check_parameter(svmProblem, svmParameter);

        if (errorMsg != null) {
            throw new SvmInvalidParameterException(errorMsg);
        }
    }

    private void initializeSvmProblem(ArrayList<ImageFeature> allImagesFeatures) {
        svmProblem = new svm_problem();

        ArrayList<svm_node[]> xs = new ArrayList<svm_node[]>();
        ArrayList<Integer> ys = new ArrayList<Integer>();

        for (ImageFeature imageFeatures : allImagesFeatures) {
            svm_node[] x = imageFeatures.getFeature();
            int annotation = imageFeatures.getAnnotation();

            xs.add(x);
            ys.add(annotation);
        }

        svmProblem.l = allImagesFeatures.size();
        svmProblem.x = new svm_node[svmProblem.l][];

        for (int i = 0; i < svmProblem.l; i++) {
            svmProblem.x[i] = xs.get(i);
        }

        svmProblem.y = new double[svmProblem.l];

        for (int i = 0; i < svmProblem.l; i++) {
            svmProblem.y[i] = ys.get(i);
        }
    }

    public static void saveSvmModel(svm_model svmModel, String modelFilename) throws IOException {
        svm.svm_save_model(modelFilename, svmModel);
    }

    public static svm_model loadSvmModel(String modelFilename) throws IOException {
        return svm.svm_load_model(modelFilename);
    }

    public void doGridSearch(HashMap<Vector<Feature>, Integer> allImageFeatures, MyKernel kernel, svm_parameter svmParameter, double minC, double maxC, double minGamma, double maxGamma) throws SvmInvalidParameterException {

        svm_parameter bestParameter = svmParameter;
        svmProblem = new svm_problem();
        double stepC = 10;
        double stepGamma = (double) 1;

        //search for C and gamma
        double max = 0;
        double bestC = minC;
        double bestGamma = minGamma;
        for (double C = minC; C < maxC; C += stepC) {
            for (double gamma = minGamma; gamma < maxGamma; gamma += stepGamma) {

                bestParameter.C = C;
                MyKernel.gamma = gamma;

                printInterface.print("C:" + C + "\nGamma:" + gamma + "\n");

                svmProblem = kernel.getTrainGramMatrixSIFT(allImageFeatures);

                printInterface.print("Cross validation for C:" + C + "\nGamma:" + gamma + "\n");
                double accuracy = doCrossValidation(svmProblem, bestParameter, 3);
                printInterface.print("Done cross validation - accuracy: " + accuracy + " \n");

                if (accuracy > max) {
                    max = accuracy;
                    bestC = C;
                    bestGamma = gamma;
                }
            }
        }


        printInterface.print("Best C:" + bestC + "\nBest gamma:" + bestGamma + " (Max Accuracy: " + max + ")\n");
    }
    
    //Search for best C parameter
    public void doGridSearch(svm_problem svmProblem, svm_parameter svmParameter, double minC, double maxC, double stepC) throws SvmInvalidParameterException {

        svm_parameter bestParameter = svmParameter;

        //search for C
        double max = 0;
        double bestC = minC;
        for (double c = minC; c < maxC; c += stepC) {

            bestParameter.C = c;

            double accuracy = doCrossValidation(svmProblem, bestParameter, 3);
            System.out.println("CrossValidation for C = " + c + ". Accuracy = " + accuracy + " \n");
            //printInterface.print("CrossValidation for C = " + c + ". Accuracy = " + accuracy + " \n");

            if (accuracy > max) {
                max = accuracy;
                bestC = c;
            }
        }
        System.out.println("Best C:" + bestC + " (Max Accuracy: " + max + ")\n");
        
        this.svmParameter.C = bestC;
        
       // printInterface.print("Best C:" + bestC + " (Max Accuracy: " + max + ")\n");
    }

    public double doCrossValidation(svm_problem svmProblem, svm_parameter svmParameter, int nrFoldCrossValidation) throws SvmInvalidParameterException {

        //setParameters(allImagesFeatures);

        int i;
        int total_correct = 0;
        double total_error = 0;
        double sumv = 0, sumy = 0, sumvv = 0, sumyy = 0, sumvy = 0;
        double[] target = new double[svmProblem.l];


        svm.svm_cross_validation(svmProblem, svmParameter, nrFoldCrossValidation, target);

        if (svmParameter.svm_type == svm_parameter.EPSILON_SVR || svmParameter.svm_type == svm_parameter.NU_SVR) {
            for (i = 0; i < svmProblem.l; i++) {
                double y = svmProblem.y[i];
                double v = target[i];
                total_error += (v - y) * (v - y);
                sumv += v;
                sumy += y;
                sumvv += v * v;
                sumyy += y * y;
                sumvy += v * y;
            }

            printInterface.print("Cross Validation Mean squared error = " + total_error / svmProblem.l + "\n");
            printInterface.print("Cross Validation Squared correlation coefficient = "
                    + ((svmProblem.l * sumvy - sumv * sumy) * (svmProblem.l * sumvy - sumv * sumy))
                    / ((svmProblem.l * sumvv - sumv * sumv) * (svmProblem.l * sumyy - sumy * sumy)) + "\n");
        } else {
            for (i = 0; i < svmProblem.l; i++) {
                if (target[i] == svmProblem.y[i]) {
                    ++total_correct;
                }
            }
        }
        this.printInterface.print("Cross Validation Accuracy = " + 100.0 * total_correct / svmProblem.l + "%\n");

        return (double) 100.0 * total_correct / svmProblem.l;
    }

   
}
