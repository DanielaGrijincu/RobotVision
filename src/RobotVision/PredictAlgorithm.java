/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import Exceptions.EmptyDirectoryException;
import Exceptions.SvmInvalidOperationException;
import Utils.Annotations;
import Utils.Evaluation;
import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Danna
 */
public class PredictAlgorithm {
    
    private ASIFTTest asift;
    private CRFHTest crfh;
    private LSDTest lsd;
    private NARFTest narf;
    
    public final String pathToAllImages = "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\";
   
    private String pathToImageToPredictAsiftFile;
    private String pathToImageToPredictJpgFile;
    
    private String pathWhereToSavePredictions = "D:\\Dana\\FACULTATE\\LICENTA\\Predictions\\predictedRun.txt";
    private String pathWhereToSaveTempPredictions = "D:\\Dana\\FACULTATE\\LICENTA\\Predictions\\tempPredictedRun.txt";
    
    private final String pathToAnnotationsFile = "concepts\\trainingConcepts.txt";
    //asift configuaration
    private final String pathToRepresentativeImagesDir = "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\repr_asift_images\\";
    private final String pathToAllAsiftFeaturesDir = "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\";
    //crfh configuration
    private final String pathToTrainingCrfhModels = "D:\\Dana\\FACULTATE\\LICENTA\\Application\\output CRFH\\models\\";
    private final String pathToTrainingCrfhFeatures = "D:\\Dana\\FACULTATE\\LICENTA\\Application\\output CRFH\\crfh_features\\";
    
    private String pathToActualLocationsFile = "";
    
    PredictAlgorithm(ASIFTTest a, CRFHTest c, LSDTest l, NARFTest n){
        
        this.asift = a;
        this.crfh = c;
        this.lsd = l;
        this.narf = n;
    }
    
    public void loadModels() throws Exception{
        
        if (crfh != null)
            crfh.loadModels(pathToTrainingCrfhModels, pathToTrainingCrfhFeatures, pathToAnnotationsFile);
        
    }
    
    public SimpleEntry<Integer, Double> predict() throws EmptyDirectoryException, IOException, ClassNotFoundException, SvmInvalidOperationException, InterruptedException, ExecutionException{
        
        TreeMap<Integer, String> myPredictions = new TreeMap<>();
        
        File dir2 = new File(pathToImageToPredictJpgFile);
        String[] imagesToPredict = dir2.list();
        
        if (imagesToPredict == null) {
            //predict only one image
            imagesToPredict = new String[1];
            imagesToPredict[0] = pathToImageToPredictJpgFile.substring(pathToImageToPredictJpgFile.lastIndexOf("\\") + 1);
            pathToImageToPredictJpgFile = pathToImageToPredictJpgFile.substring(0, pathToImageToPredictJpgFile.lastIndexOf("\\") + 1);
            pathToImageToPredictAsiftFile = pathToImageToPredictAsiftFile.substring(0, pathToImageToPredictAsiftFile.lastIndexOf("\\") + 1);
        }
        
        for (int i = 0; i < imagesToPredict.length; i++){
            
            String current_image = pathToImageToPredictJpgFile + imagesToPredict[i];
            String curr_asift_feature = pathToImageToPredictAsiftFile + imagesToPredict[i].substring(0, imagesToPredict[i].lastIndexOf('.')) + ".asift";
            
            int nr_image = Integer.parseInt(current_image.substring(current_image.lastIndexOf('_') + 1, current_image.lastIndexOf('.')));
            SimpleEntry<Integer, Double> prediction = null; // the class and probability predicted
            
            // first predict image with crfh
            if (crfh != null){
                System.out.println("Predicting image with CRFH...");
                prediction = crfh.predict("", "", "", current_image, "", "");
            }
            
            if (prediction.getKey() == -1){ // predict with asift
                if (asift != null){
                    System.out.println("Predicting image with ASIFT...");
                    prediction = asift.predict(curr_asift_feature, pathToRepresentativeImagesDir, pathToAllAsiftFeaturesDir, "", "");
                }
            }
            
            String concept = " ";
            if (prediction.getKey() != -1)
                concept = Annotations.getAnnotation(prediction.getKey());
            
            myPredictions.put(nr_image, concept);
            
            appendPredictionToFile(nr_image, concept, pathWhereToSaveTempPredictions);
            System.out.println("Saved prediction for image " + nr_image + " => " + concept);
            
            if (imagesToPredict.length == 1)
                return prediction;
        }
        
         if (!"".equals(pathWhereToSavePredictions))
            Evaluation.saveResultsImageClefFormat(myPredictions, pathWhereToSavePredictions);
         
         return new SimpleEntry<>(-1, (double)0);
    }
    
    public void appendPredictionToFile(int nr_image, String concept, String pathWhereToWriteFile) throws FileNotFoundException, IOException {

        FileOutputStream fstream = new FileOutputStream(pathWhereToWriteFile, true);
        DataOutputStream out = new DataOutputStream(fstream);
        final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));

        //ImageCLEF Format Output: 00001 Corridor 
        // TreeMap ordering is ascending

        String addZeros = "";

        if (nr_image < 10) {
            addZeros = "0000";
        } else if (nr_image < 100) {
            addZeros = "000";
        } else if (nr_image < 1000) {
            addZeros = "00";
        } else if (nr_image < 10000) {
            addZeros = "0";
        }

        wr.append(addZeros + nr_image + " " + concept);
        wr.newLine();

        wr.flush();
        wr.close();
        
    }
        
    /**
     * @param pathToImagesToPredictDir the pathToImagesToPredictDir to set
     */
    public void setPathToImageToPredictJpgFile(String pathToImagesToPredictDir) {
        this.pathToImageToPredictJpgFile = pathToImagesToPredictDir;
    }
    
     /**
     * @param pathToImagesToPredictDir the pathToImagesToPredictDir to set
     */
    public void setPathToImageToPredictAsiftFile(String pathToImagesToPredictDir) {
        this.pathToImageToPredictAsiftFile = pathToImagesToPredictDir;
    }


    /**
     * @param pathWhereToSavePredictions the pathWhereToSavePredictions to set
     */
    public void setPathWhereToSavePredictions(String pathWhereToSavePredictions) {
        this.pathWhereToSavePredictions = pathWhereToSavePredictions;
    }

    /**
     * @param pathToActualLocationsFile the pathToActualLocationsFile to set
     */
    public void setPathToActualLocationsFile(String pathToActualLocationsFile) {
        this.pathToActualLocationsFile = pathToActualLocationsFile;
    }
}
