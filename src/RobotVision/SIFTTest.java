/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import Exceptions.SvmInvalidOperationException;
import Exceptions.SvmInvalidParameterException;
import SIFT.Feature;
import SIFT.SIFT;
import SVM.SvmPredict;
import SVM.SvmPrediction;
import SVM.SvmScale;
import SVM.SvmTrain;
import Utils.Annotations;
import Utils.ImageFeature;
import Kernels.MatchKernel;
import Utils.Serialization;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.imageio.ImageIO;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_print_interface;

/**
 *
 * @author Danna
 */
public class SIFTTest {

    private svm_model svmModel = null;
    public static svm_print_interface print = new svm_print_interface() {

        @Override
        public void print(String string) {

            System.out.print(string);
        }
    };

    private ImageFeature getImageFeature(Feature f, int label) {

        svm_node[] node = new svm_node[f.descriptor.length];
        for (int i = 0; i < f.descriptor.length; i++) {
            node[i] = new svm_node();
            node[i].index = i;
            node[i].value = f.descriptor[i];
        }

        return new ImageFeature(node, label);

    }
    
    private ImageFeature getImageFeature(Feature f) {

        svm_node[] node = new svm_node[f.descriptor.length];
        for (int i = 0; i < f.descriptor.length; i++) {
            node[i] = new svm_node();
            node[i].index = i;
            node[i].value = f.descriptor[i];
        }

        return new ImageFeature(node);

    }

    public void computeSiftFeatures(String labelsFile, String imagesFolder, String locationsFolder) // boolean turnGreyscale)
    {
        BufferedImage img;

      //  ArrayList<ArrayList<ImageFeature>> allFeatures = new ArrayList<ArrayList<ImageFeature>>();
      
        HashMap<Vector<Feature>, Integer> allSiftFeatures = new HashMap<Vector<Feature>, Integer>();

        long millis = System.currentTimeMillis();
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

        System.out.println(hours + ":" + minutes + ":" + seconds + " Started SIFT feature extraction.");

        DataInputStream in = null;
        try {

            Annotations.loadAnnotations(labelsFile);

            FileInputStream fstream = new FileInputStream(locationsFolder);
            in = new DataInputStream(fstream);

            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(':') + 2);
                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();

                img = null;
                img = ImageIO.read(new File(imagesFolder + "\\" + filename));

                Vector<Feature> descriptorset = SIFT.getFeatures(img);
//                ArrayList<ImageFeature> features = new ArrayList<ImageFeature>();
//                for (int i = 0; i < descriptorset.size(); i++) {
//                    features.add(getImageFeature(descriptorset.elementAt(i), Annotations.getAnnotation(concept)));
//                }

             //   allFeatures.add(features);
                allSiftFeatures.put(descriptorset, Annotations.getAnnotation(concept));

                System.out.println("Processed image " + filename);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
        } finally {
            try {

                in.close();

                Serialization.serialize(allSiftFeatures, "output SIFT\\siftFeatures");
               // HashMap<Vector<Feature>, Integer> imageFeatures = (HashMap<Vector<Feature>, Integer>) Serialization.deserialize("output SIFT\\siftFeatures");
   //             HashMap<Vector<Feature>, Integer> imageFeatures = (HashMap<Vector<Feature>, Integer>) Serialization.deserialize("output SIFT\\siftFeatures");
//
                millis = System.currentTimeMillis();
                seconds = (int) (millis / 1000) % 60;
                minutes = (int) ((millis / (1000 * 60)) % 60);
                hours = (int) ((millis / (1000 * 60 * 60)) % 24);

                System.out.println(hours + ":" + minutes + ":" + seconds + " Finished SIFT feature extraction.");

            }
//            catch (ClassNotFoundException ex) {
//                ex.printStackTrace();
//                System.out.println(ex.toString());
//            }
            catch (IOException ex) {
                ex.printStackTrace();
                System.out.println(ex.toString());
            }

        }
    }

    public void train() {

        long millis = System.currentTimeMillis();
        int seconds = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hours = (int) ((millis / (1000 * 60 * 60)) % 24);

        System.out.println(hours + ":" + minutes + ":" + seconds + " Started SVM trainig.");

        try {
            
            System.out.println("Deserializing features...");

           // ArrayList<ArrayList<ImageFeature>> imageFeatures = (ArrayList<ArrayList<ImageFeature>>) Serialization.deserialize("output SIFT\\siftFeatures");

            HashMap<Vector<Feature>, Integer> imageFeatures = (HashMap<Vector<Feature>, Integer>) Serialization.deserialize("output SIFT\\siftFeatures");
            
            System.out.println("Finished deserializing features.");
            
            SvmTrain svmTrain = new SvmTrain(print);
            
            svm_model myModel = svmTrain.trainSIFT(imageFeatures, new MatchKernel(), false);

            System.out.println("Serializing model...");
            
            Serialization.serialize(myModel, "output SIFT\\siftModel");
            
             System.out.println("Finished serializing model.");

            millis = System.currentTimeMillis();
            seconds = (int) (millis / 1000) % 60;
            minutes = (int) ((millis / (1000 * 60)) % 60);
            hours = (int) ((millis / (1000 * 60 * 60)) % 24);

            System.out.println(hours + ":" + minutes + ":" + seconds + " Finished SVM trainig.");

        } catch (SvmInvalidParameterException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        } catch (Exception ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        }
    }
    
    public void loadModels() {

        if (svmModel == null) {

            try {
                
                System.out.println("Deserializing model...");
                
                svmModel = (svm_model) Serialization.deserialize("output SIFT\\siftModel");
                
                System.out.println("Finished deserializing model.");

            } catch (IOException ex) {
                System.out.println(ex.toString());
            } catch (ClassNotFoundException ex) {
                System.out.println(ex.toString());
            }

        }
    }

    public int predictImage(ArrayList<ArrayList<ImageFeature>> allImageFeatures, String labelsFile, BufferedImage img, int probability) {

        try {

            Annotations.loadAnnotations(labelsFile);

            SvmScale svmScale = null; //(SvmScale)Serialization.deserialize("output CRFH\\scaleModel");

            Vector<Feature> descriptorset = SIFT.getFeatures(img);
            ArrayList<ImageFeature> features = new ArrayList<ImageFeature>();
            for (int i = 0; i < descriptorset.size(); i++) {
                    features.add(getImageFeature(descriptorset.elementAt(i)));
                }

            SvmPrediction prediction = SvmPredict.predict(allImageFeatures, features, new MatchKernel(), svmScale, svmModel, probability, print);

            if (probability == 1) {

                for (Map.Entry<String, Double> entry : prediction.getSortedEstimates(Annotations.getStringLabels())) {
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
    
    public int predictImage(HashMap<Vector<Feature>, Integer> allImageFeatures, String labelsFile, BufferedImage img, int probability) {

        try {

            Annotations.loadAnnotations(labelsFile);

            SvmScale svmScale = null; //(SvmScale)Serialization.deserialize("output CRFH\\scaleModel");

            Vector<Feature> descriptorset = SIFT.getFeatures(img);
//            ArrayList<ImageFeature> features = new ArrayList<ImageFeature>();
//            for (int i = 0; i < descriptorset.size(); i++) {
//                    features.add(getImageFeature(descriptorset.elementAt(i)));
//                }

            SvmPrediction prediction = SvmPredict.predict(allImageFeatures, descriptorset, new MatchKernel(), svmScale, svmModel, probability, print);

            if (probability == 1) {

                for (Map.Entry<String, Double> entry : prediction.getSortedEstimates(Annotations.getStringLabels())) {
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
    

    public void predict(String inputImagesFolder, String annotationsFile, String outputPredictFile, int probability) {

        loadModels();
        BufferedImage img = null;
        DataOutputStream out = null;
        
        try {
            FileOutputStream ostream = new FileOutputStream(outputPredictFile);
            out = new DataOutputStream(ostream);
            final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));
            
            File dir = new File(inputImagesFolder);
            String[] children = dir.list();
            Annotations.loadAnnotations(annotationsFile);
            System.out.println("Deserializing features...");
            //ArrayList<ArrayList<ImageFeature>> imageFeatures = (ArrayList<ArrayList<ImageFeature>>) Serialization.deserialize("output SIFT\\siftFeatures");
            HashMap<Vector<Feature>, Integer> imageFeatures = (HashMap<Vector<Feature>, Integer>) Serialization.deserialize("output SIFT\\siftFeatures");
            System.out.println("Finished deserializing features.");

            if (children == null) {

                img = null;
                img = ImageIO.read(dir);

                long millis1 = System.currentTimeMillis();

                int p = predictImage(imageFeatures, annotationsFile, img, probability);

                long millis = System.currentTimeMillis() - millis1;
                int seconds = (int) (millis / 1000) % 60;

                if (p != -1) {
                    
                    wr.append(dir + ": " + Annotations.getAnnotation(p));
                    wr.newLine();
                    
                    System.out.println("Predicted image " + dir + " => " + Annotations.getAnnotation(p) + " in " + seconds + " seconds.");
                } else {
                    System.out.println("Predicted image " + dir + " => " + "Unknown" + " in " + seconds + " seconds.");
                    wr.append(dir + ": " + "Unknown");
                    wr.newLine();
                }

                //throw new Exception("The image folder and the labels file cannot be empty!");
            } else {
                for (int i = 0; i < children.length; i++) {

                    String filename = children[i];
                    if (filename.endsWith("jpeg") || filename.endsWith("jpg")) {

                        img = ImageIO.read(new File(inputImagesFolder + "\\" + filename));

                        long millis1 = System.currentTimeMillis();

                        int p = predictImage(imageFeatures, annotationsFile, img, probability);

                        long millis = System.currentTimeMillis() - millis1;
                        int seconds = (int) (millis / 1000) % 60;

                        if (p != -1) {
                            wr.append(filename + ": " + Annotations.getAnnotation(p));
                            wr.newLine();

                            System.out.println("Predicted image " + filename + " => " + Annotations.getAnnotation(p) + " in " + seconds + " seconds.");
                        } else {
                            System.out.println("Predicted image " + filename + " => " + "Unknown" + " in " + seconds + " seconds.");
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
                System.out.println(ex.getStackTrace().toString() + ex.toString());
            }
        }
        // try
    }
}
