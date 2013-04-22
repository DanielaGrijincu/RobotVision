/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import CRFH.CRFH;
import CRFH.Descriptor;
import CRFH.LxDescriptor;
import CRFH.LyDescriptor;
import Exceptions.EmptyDirectoryException;
import Exceptions.SvmInvalidOperationException;
import Kernels.HistogramDistanceKernel;
import SVM.SvmPredict;
import SVM.SvmPrediction;
import SVM.SvmScale;
import SVM.SvmTrain;
import Utils.Annotations;
import Utils.Evaluation;
import Utils.ImageFeature;
import Utils.Serialization;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_print_interface;

/**
 *
 * @author Danna
 */
public class CRFHTest {

    private CRFH mycrfh = null;
    private ArrayList<svm_model> allSvmModels = null;
    private ArrayList<ImageFeature> imageFeatures = null;
    private ArrayList<ArrayList<ImageFeature>> allTrainingFeatures = null;
    
    private int scale1 = 1;
    private int scale2 = 2;
    private int scale3 = 4;
    private int scale4 = 8;
    private int bins = 14;
    
    private int probability = 1;
    
    public CRFHTest(int scale1, int scale2, int scale3, int scale4, int bins){
        this.scale1 = scale1;
        this.scale2 = scale2;
        this.scale3 = scale3;
        this.scale4 = scale4;
        this.bins = bins;
    }

    CRFHTest() {
        
    }
    
    private ArrayList<Descriptor> initializeDescriptors() {

        ArrayList<Descriptor> descriptorsList = new ArrayList<Descriptor>();

        LxDescriptor lx1 = new LxDescriptor(scale1, bins);
        LyDescriptor ly1 = new LyDescriptor(scale1, bins);
        
        LxDescriptor lx2 = new LxDescriptor(scale2, bins);
        LyDescriptor ly2 = new LyDescriptor(scale2, bins);
        
        LxDescriptor lx3 = new LxDescriptor(scale3, bins);
        LyDescriptor ly3 = new LyDescriptor(scale3, bins);
        
        LxDescriptor lx4 = new LxDescriptor(scale4, bins);
        LyDescriptor ly4 = new LyDescriptor(scale4, bins);

        descriptorsList.add(ly1);
        descriptorsList.add(lx1);
        
        descriptorsList.add(ly2);
        descriptorsList.add(lx2);
        
        descriptorsList.add(ly3);
        descriptorsList.add(lx3);
        
        descriptorsList.add(ly4);
        descriptorsList.add(lx4);

        return descriptorsList;
    }
    
    public static svm_print_interface print = new svm_print_interface() {

        @Override
        public void print(String string) {

            System.out.print(string);
        }
    };

    public void computeCrfhFeatures(String pathToImagesToComputeDir, String pathToAnnotationsFile, String pathToLocationsFile, String pathWhereToSaveFeatures) throws Exception {

        System.out.println("Started CRFH feature extraction.");

        this.imageFeatures = new ArrayList<ImageFeature>();
        mycrfh = new CRFH(this.initializeDescriptors(), 4, 14);
        BufferedImage img;

        if (pathToAnnotationsFile.equals("") || pathToLocationsFile.equals("")) { // compute features for test images

            File dir = new File(pathToImagesToComputeDir);
            String[] images = dir.list();

            if (images == null) {
                throw new EmptyDirectoryException("The image features path is an empty directory!");
            }

            for (int i = 0; i < images.length; i++) {

                String current_image = images[i];

                img = ImageIO.read(new File(pathToImagesToComputeDir + current_image));

                double milis = System.currentTimeMillis();

                svm_node[] histogram = mycrfh.computeHistogram(img);
                imageFeatures.add(new ImageFeature(histogram, 0));

                System.out.println("Processed image " + current_image + " in " + (System.currentTimeMillis() - milis) + " miliseconds.");

            }
        } else {

            DataInputStream in = null;
            FileInputStream fstream = new FileInputStream(pathToLocationsFile);
            in = new DataInputStream(fstream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            //load training annotations
            Annotations.loadAnnotations(pathToAnnotationsFile);

            String strLine;
            while ((strLine = br.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(':') + 2);
                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();

                img = ImageIO.read(new File(pathToImagesToComputeDir + "\\" + filename));

                double milis = System.currentTimeMillis();

                svm_node[] histogram = mycrfh.computeHistogram(img);
                imageFeatures.add(new ImageFeature(histogram, Annotations.getAnnotation(concept)));

                System.out.println("Processed image " + filename + " in " + (System.currentTimeMillis() - milis) + " miliseconds.");
            }

            in.close();
        }

        Serialization.serialize(imageFeatures, pathWhereToSaveFeatures);


        System.out.println("Finished CRFH feature extraction.");


    }
    
    public ArrayList<ArrayList<ImageFeature>> loadTrainingFeatures(String pathToImageFeaturesDir) throws EmptyDirectoryException, IOException, ClassNotFoundException{
        
        File dir = new File(pathToImageFeaturesDir);
        String[] children = dir.list();

        if (children == null) {
            throw new EmptyDirectoryException("The image features path is an empty directory!");
        }
        
        Arrays.sort(children);

        ArrayList<ArrayList<ImageFeature>> imageFeaturesAll = new ArrayList<ArrayList<ImageFeature>>();
        
        ArrayList<ImageFeature> featuresTemp;


        System.out.println("Deserializing features...");

        for (int i = 0; i < children.length; i++) {

            featuresTemp = (ArrayList<ImageFeature>) Serialization.deserialize(pathToImageFeaturesDir + children[i]);
            imageFeaturesAll.add(featuresTemp);

        }

        System.out.println("Finished deserializing features.");
        
        return imageFeaturesAll;
        
    }
    
    public ArrayList<svm_model> loadTrainingModels(String pathToModelsDir) throws EmptyDirectoryException, IOException, ClassNotFoundException{
        
        File dir = new File(pathToModelsDir);
        String[] children = dir.list();

        if (children == null) {
            throw new EmptyDirectoryException("The models path is an empty directory!");
        }
        
        Arrays.sort(children);

        ArrayList<svm_model> training_models = new ArrayList<svm_model>();
        
        System.out.println("Deserializing models...");

        for (int i = 0; i < children.length; i++) {

            training_models.add((svm_model) Serialization.deserialize(pathToModelsDir + children[i]));
        }
        

        System.out.println("Finished deserializing models.");
        
        return training_models;
        
    }
    
    public ArrayList<ImageFeature> loadFeatures(String pathToImageFeaturesDir) throws EmptyDirectoryException, IOException, ClassNotFoundException{
        
        File dir = new File(pathToImageFeaturesDir);
        String[] children = dir.list();

        if (children == null) {
            throw new EmptyDirectoryException("The image features path is an empty directory!");
        }

        ArrayList<ImageFeature> imageFeaturesAll = new ArrayList<ImageFeature>();
        
        ArrayList<ImageFeature> featuresTemp;


        System.out.println("Deserializing features...");

        for (int i = 0; i < children.length; i++) {

            featuresTemp = (ArrayList<ImageFeature>) Serialization.deserialize(pathToImageFeaturesDir + children[i]);
            imageFeaturesAll.addAll(featuresTemp);

        }

        System.out.println("Finished deserializing features.");
        
        return imageFeaturesAll;
        
    }

    public void train(String pathToImageFeaturesDir, String pathWhereToSaveModel) throws Exception {

        this.imageFeatures = loadFeatures(pathToImageFeaturesDir);

        SvmTrain svmTrain = new SvmTrain(print);
        svm_model myModel = svmTrain.trainCRFH(imageFeatures, new HistogramDistanceKernel(), false);

        System.out.println("Serializing model...");
        Serialization.serialize(myModel, pathWhereToSaveModel);
        System.out.println("Finished serializing model");
        
    }

    public void loadModels(String pathFromWhereToLoadModel, String pathFromWhereToLoadFeatures, String pathToAnnotationsFile) throws EmptyDirectoryException, IOException, ClassNotFoundException{

        if (allSvmModels == null) {

            System.out.println("Loading models...");

            // load svm models
            this.allSvmModels = loadTrainingModels(pathFromWhereToLoadModel);
            
            // load annotations
            Annotations.loadAnnotations(pathToAnnotationsFile);
        
            //load features
            this.allTrainingFeatures = loadTrainingFeatures(pathFromWhereToLoadFeatures);

            // initialize CRFH configuration
            this.mycrfh = new CRFH(this.initializeDescriptors(), 4, 14);
            
            System.out.println("Finished loading models.");

        }
    }

    public SimpleEntry<Integer, Double> predictImage(ArrayList<ImageFeature> allImageFeatures, String pathToAnnotationsFile, BufferedImage image, int probability) throws IOException, SvmInvalidOperationException{

        SvmScale svmScale = null; //(SvmScale)Serialization.deserialize("output CRFH\\scaleModel");

        // extract feature from image
        svm_node[] histogram = mycrfh.computeHistogram(image);
        ImageFeature featuresImg = new ImageFeature(histogram);

        //predict with every model and get the concept with max probability
        double maxprob = 0;
        String concept = null;
        for (int i = 0; i < this.allSvmModels.size(); i++) {

            SvmPrediction prediction = SvmPredict.predict(this.allTrainingFeatures.get(i), featuresImg, new HistogramDistanceKernel(), svmScale, this.allSvmModels.get(i), probability, print);

            if (probability == 1) {

                for (Entry<String, Double> entry : prediction.getSortedEstimates(Annotations.getStringLabels())) {
                    
                    if (entry.getValue() > maxprob){
                        maxprob = entry.getValue();
                        concept = entry.getKey();
                    }
                    
                    break;

//                    if (entry.getValue() < 0.2)
//                        return -1;
//                    else
                    // System.out.println("Probability: " + entry.getValue());
                 //   return Annotations.getAnnotation(entry.getKey());
                }
                
            } else if (probability == 0) {
                return new SimpleEntry<>(prediction.getPredicted(), (double)0);
                // System.out.println("Predicted concept: " + Annotations.getAnnotation(prediction.getPredicted()));
            }

        }
        
        if (concept != null) {
            if (maxprob < (double) 0.5) {
                return new SimpleEntry<>(-1, (double)0);
            } else {
                return new SimpleEntry<>(Annotations.getAnnotation(concept), maxprob);
            }
        }

        return new SimpleEntry<>(-1, (double)0);

    }

    public SimpleEntry<Integer, Double> predict(String pathToTrainingModels, String pathToTrainingFeatures, String pathToAnnotationsFile, String pathToImagesToPredictDir, String pathWhereToSaveOutput, String pathToActualLocationsFile) throws EmptyDirectoryException, IOException, ClassNotFoundException, SvmInvalidOperationException{

        if (!"".equals(pathToTrainingModels) && !"".equals(pathToTrainingFeatures) && !"".equals(pathToAnnotationsFile)) {
            // load model and annotations
            loadModels(pathToTrainingModels, pathToTrainingFeatures, pathToAnnotationsFile);
        }
        
        TreeMap<Integer, String> myPredictions = new TreeMap<Integer, String>();
        
        BufferedImage img = null;

        File images = new File(pathToImagesToPredictDir);
        String[] children = images.list();

        //predict a single image
        if (children == null) {
            children = new String[1];
            children[0] = pathToImagesToPredictDir.substring(pathToImagesToPredictDir.lastIndexOf("\\") + 1);
            pathToImagesToPredictDir = pathToImagesToPredictDir.substring(0, pathToImagesToPredictDir.lastIndexOf("\\") + 1);
        }

            for (int i = 0; i < children.length; i++) {

                String current_image = children[i];

                if (current_image.endsWith("jpeg") || current_image.endsWith("jpg")) {

                    img = ImageIO.read(new File(pathToImagesToPredictDir + current_image));

                    long millis1 = System.currentTimeMillis();

                    SimpleEntry<Integer, Double> p = predictImage(imageFeatures, pathToAnnotationsFile, img, probability);

                    long millis = System.currentTimeMillis() - millis1;

                    String image_concept = " ";
                    
                    if (p.getKey() != -1) {
                        image_concept = Annotations.getAnnotation(p.getKey());
                    } 
                    
                    int nr_image = Integer.parseInt(current_image.substring(current_image.indexOf("_")+1, current_image.indexOf(".")));
                    System.out.println("Predicted image " + current_image + " => " + image_concept + " in " + millis + " miliseconds.");
                    
                    if (children.length == 1) // just a sigle image => return prediction
                        return p;
                    
                    myPredictions.put(nr_image, image_concept);

                } //if
            } // for
      
        if (!pathWhereToSaveOutput.equals(""))
            Evaluation.saveResultsImageClefFormat(myPredictions, pathWhereToSaveOutput);
        
        if (!pathToActualLocationsFile.equals(""))
            Evaluation.getAccuracy(myPredictions, pathToActualLocationsFile);
        
        return new SimpleEntry<>(-1, (double)0);
    }
    
   
}
