/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import Exceptions.SvmInvalidOperationException;
import Exceptions.SvmInvalidParameterException;
import Kernels.NARFKernel;
import NARF.NarfDescriptor;
import SVM.SvmPredict;
import SVM.SvmPrediction;
import SVM.SvmScale;
import SVM.SvmTrain;
import Utils.Annotations;
import Utils.Serialization;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Map.Entry;
import libsvm.svm_model;
import libsvm.svm_print_interface;

/**
 *
 * @author Danna
 */
public class NARFTest {
    
    private svm_model svmModel;
    
    public static svm_print_interface print = new svm_print_interface() {

        @Override
        public void print(String string) {

            //System.out.print(string);
        }
    };

    public void computeNARFFeatures(String inputPcdFolder, String outputFeaturesFolder) {
        long millis = System.currentTimeMillis();
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

        System.out.println(hours + ":" + minutes + ":" + seconds + " Started NARF feature extraction.");

        File dir = new File(inputPcdFolder);
        String[] children = dir.list();

        if (children != null) {

            for (int i = 0; i < children.length; i++) {

                String filename = children[i];
                if (filename.endsWith("pcd")) {

                    String name = filename.subSequence(0, filename.indexOf('.')).toString();
                    NarfDescriptor.getDescriptor(inputPcdFolder + "\\" + filename, outputFeaturesFolder + "\\" + name + ".txt");

                    System.out.println(i + ". " + "Computed narf features for " + filename);
                }
            } // for
        }

    }
    
     public static ArrayList<SimpleImmutableEntry<String, Integer>> getFeaturesMap(String featuresFolder, String annotationsFile, String imagesLocationsFile) {

        ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles = new ArrayList<SimpleImmutableEntry<String, Integer>>();

        try {

            Annotations.loadAnnotations(annotationsFile);

            DataInputStream in = null;

            FileInputStream fstream = new FileInputStream(imagesLocationsFile);
            in = new DataInputStream(fstream);

            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(':') + 2);
                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();
                filename = strLine.substring(0, filename.indexOf("."));

                File file = new File(featuresFolder + filename + ".txt");
                if (file.exists()) {

                    allFeaturesFiles.add(new SimpleImmutableEntry<String, Integer>(featuresFolder + filename + ".txt", Annotations.getAnnotation(concept)));
                }
            }



        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        }

        return allFeaturesFiles;

    }

    public void train(String featuresFolder, String annotationsFile, String imagesLocationsFile) {

        try {

            ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles = NARFTest.getFeaturesMap(featuresFolder, annotationsFile, imagesLocationsFile);

            // Finished Loading Features Files...now trainCRFH the SVM
            SvmTrain svmTrain = new SvmTrain(print);
            svm_model myModel = svmTrain.trainNARF(allFeaturesFiles, new NARFKernel());

            Serialization.serialize(myModel, "output NARF\\narfModel");

        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        } catch (SvmInvalidParameterException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        }

    }

    public void loadModels() {

        if (svmModel == null) {

            try {

                System.out.println("Deserializing model...");

                svmModel = (svm_model) Serialization.deserialize("output NARF\\narfModel");

                System.out.println("Finished deserializing model.");


            } catch (IOException ex) {
                System.out.println(ex.toString());
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.toString());
            }

        }
    }

    public int predictImage(String imageToPredict, ArrayList<SimpleImmutableEntry<String, Integer>> allFeatures, int probability) {

        try {

            SvmScale svmScale = null;

            SvmPrediction prediction = SvmPredict.predict(allFeatures, imageToPredict, new NARFKernel(), svmScale, svmModel, probability, print);

            if (probability == 1) {

                for (Entry<String, Double> entry : prediction.getSortedEstimates(Annotations.getStringLabels())) {
//                    if (entry.getValue() < 0.4)
//                        return -1;
//                    else
                    return Annotations.getAnnotation(entry.getKey());
                }
            } else if (probability == 0) {

                return prediction.getPredicted();
                // System.out.println("Predicted concept: " + Annotations.getAnnotation(prediction.getPredicted()));
            }

        } catch (IOException ex) {
            System.out.println(ex.toString());
        } catch (SvmInvalidOperationException ex) {
            System.out.println(ex.toString());
        }

        return -1;

    }

    public void predict(String imagesToPredict, String allFeaturesFolder, String annotationsFile, String allFeaturesLocationsFile, String outputPredictedFile, int probability) {

        BufferedImage img = null;
        DataOutputStream out = null;

        try {

            loadModels();
            ArrayList<SimpleImmutableEntry<String, Integer>> allFeatures = NARFTest.getFeaturesMap(allFeaturesFolder, annotationsFile, allFeaturesLocationsFile);

            FileOutputStream ostream = new FileOutputStream(outputPredictedFile);
            out = new DataOutputStream(ostream);
            final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));

            File dir = new File(imagesToPredict);
            String[] children = dir.list();

            if (children == null) {


                long millis1 = System.currentTimeMillis();

                int p = predictImage(imagesToPredict, allFeatures, probability);

                long millis = System.currentTimeMillis() - millis1;

                if (p != -1) {

                    wr.append(dir + ": " + Annotations.getAnnotation(p));
                    wr.newLine();

                    System.out.println("Predicted image " + dir + " => " + Annotations.getAnnotation(p) + " in " + millis + " miliseconds.");

                } else {

                    System.out.println("Predicted image " + dir + " => " + "Unknown" + " in " + (int) (millis / 1000) % 60 + " seconds.");
                    wr.append(dir + ": " + "Unknown");
                    wr.newLine();
                }

                //throw new Exception("The image folder and the labels file cannot be empty!");
            } else {
                for (int i = 0; i < children.length; i++) {

                    String filename = children[i];
                  
                    if (filename.endsWith("txt")) {

                        long millis1 = System.currentTimeMillis();

                        int p = predictImage(imagesToPredict + filename, allFeatures, probability);

                        long millis = System.currentTimeMillis() - millis1;

                        if (p != -1) {

                            wr.append(filename + ": " + Annotations.getAnnotation(p));
                            wr.newLine();

                            System.out.println("Predicted image " + filename + " => " + Annotations.getAnnotation(p) + " in " + (int) (millis / 1000) % 60 + " seconds.");

                        } else {

                            System.out.println("Predicted image " + filename + " => " + "Unknown" + " in " + millis + " miliseconds.");
                            wr.append(filename + ": " + "Unknown");
                            wr.newLine();
                        }

                    }
                } // for
            } //else

            wr.flush();

        } catch (IOException ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println(ex.toString());
            ex.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                System.out.println(ex.toString());
                ex.printStackTrace();
            }
        }

    }
}