/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import Utils.Evaluation;
import java.io.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Danna
 */
public class Main {

    public static double pcdMean(String pcdFile) throws Exception {
        FileInputStream fstream = null;
        double mean = 0;
        int points = 0;
        try {
            fstream = new FileInputStream(pcdFile);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] numbers = line.split("( |\t)+");
                    double x = Double.parseDouble(numbers[0]);
                    double y = Double.parseDouble(numbers[1]);
                    double z = Double.parseDouble(numbers[2]);
                    double t = Double.parseDouble(numbers[3]);
                    points++;
                    mean += z;
                } catch (NumberFormatException ex) {
                }
            }
        } finally {
            if (fstream != null) {
                fstream.close();
            }
        }

        return mean / points;
    }

    public static void pcdConcepts(String pcdDir, String annotationsFile, String outputDistributionFile) throws Exception {
        File folder = new File(pcdDir);
        File[] listOfFiles = folder.listFiles();
        
        LSDTest lsd = new LSDTest();
        HashMap<String, String> imageMap = lsd.getLocationsMap(annotationsFile);
        HashMap<String, ArrayList<AbstractMap.SimpleImmutableEntry<String, Object>>> depthDistribution = new HashMap<String, ArrayList<AbstractMap.SimpleImmutableEntry<String, Object>>>();
        

        for (int it = 0; it < listOfFiles.length; it++) {
            
            String pcdFile = listOfFiles[it].getName();
            
            double pcdMean = pcdMean(pcdDir + pcdFile);
            
            pcdFile = pcdFile.substring(0, pcdFile.indexOf("."));
            
            if (imageMap.containsKey(pcdFile)){
                            if (depthDistribution.get(imageMap.get(pcdFile)) != null)
                                depthDistribution.get(imageMap.get(pcdFile)).add(new AbstractMap.SimpleImmutableEntry<String, Object>(pcdFile, pcdMean));
                            else{
                                depthDistribution.put(imageMap.get(pcdFile), new ArrayList<AbstractMap.SimpleImmutableEntry<String, Object>>());
                                depthDistribution.get(imageMap.get(pcdFile)).add(new AbstractMap.SimpleImmutableEntry<String, Object>(pcdFile, pcdMean));
                            }
                        }
            
            System.out.println(it + "." + pcdFile + ": " + imageMap.get(pcdFile) + " => " + pcdMean);
            
        }
        
        lsd.saveHashMapToFile(depthDistribution, annotationsFile, outputDistributionFile);

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        
        PredictServer server = new PredictServer();
        server.run();
//        PredictAlgorithm alg = new PredictAlgorithm(new ASIFTTest(), new CRFHTest(), null, null);
//        alg.loadModels();
//        alg.predict();
//        
     
//        ASIFTTest asift = new ASIFTTest();
//        asift.transformImagesJpgToPng("D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\test1\\std_cam\\",
//                                      "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\test1\\png_cam\\");
//        asift.computeASIFTFeatures("D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\test1\\png_cam\\",
//                                   "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\test1\\asift_features\\", "");
//        asift.predict("D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\testAsift\\",
//                      "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\repr_asift_images\\", 
//                      "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\", 
//                      "concepts\\trainingConcepts.txt",
//                      "asiftPredictions.txt");
        
        
   //     Evaluation.saveResultsInPythonScriptFormat("output CRFH\\predictedResults.txt", "pythonResults.txt");
     //   Evaluation.getAccuracy("output CRFH\\predictedResults.txt", "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\training1\\locations\\rgb.txt");
 //       CRFHTest crfh = new CRFHTest();
//        crfh.computeCrfhFeatures("D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\training1\\std_cam\\", 
//                                 "concepts\\trainingConcepts.txt",
//                                 "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\training1\\locations\\rgb.txt",
//                                 "output CRFH\\crfh_features\\training1.crfh");
//        crfh.train("output CRFH\\crfh_features\\", "D:\\Dana\\FACULTATE\\LICENTA\\output CRFH\\models\\training2.model");
//        crfh.predict("D:\\Dana\\FACULTATE\\LICENTA\\output CRFH\\models\\",
//                     "D:\\Dana\\FACULTATE\\LICENTA\\output CRFH\\crfh_features\\", 
//                     "concepts\\trainingConcepts.txt",
//                     "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\training3\\std_cam\\",
//                     "output CRFH\\predictedResults.txt",
//                     "D:\\Dana\\FACULTATE\\LICENTA\\IMAGE_SETS\\training3\\locations\\rgb.txt");
        

    //  Main.pcdConcepts("D:\\Dana\\FACULTATE\\LICENTA\\training1\\pcd_files\\", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\locations\\depth.txt", "DepthDistribution.txt");
     
//        ASIFTTest asift = new ASIFTTest();
//        asift.transformImagesJpgToPng("D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\training3\\std_cam\\", 
//                                      "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\training3\\pgm_cam\\");
//     LSDTest lsd = new LSDTest();
//     lsd.determineLines("D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\training3\\pgm_cam\\",
//                           "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\training3\\locations\\rgb.txt",
//                           "concepts\\trainingConcepts.txt",
//                           "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\training3\\LineDistributionTraining3.txt");
//        //     Image.saveAsPGM(ImageIO.read(new File("D:\\Dana\\FACULTATE\\LICENTA\\test1\\std_cam\\rgb_2117.jpg")), "rgb_2117.pgm");
      
  //      Preprocessing.process("training3\\std_cam\\", "training3\\locations\\rgb.txt", "training3\\locations\\SelectedPreprocessedImages.txt", 0.08f);
        
    //    Image.splitIntoDirectories("d:\\Programming\\RobotVision\\training3\\std_cam\\", "d:\\Programming\\RobotVision\\training3\\std_repr\\", "d:\\Programming\\RobotVision\\training3\\locations\\SelectedImages2.txt");

//        ASIFTTest asift = new ASIFTTest();
//       Image.saveAsPNG(ImageIO.read(new File("d:\\Programming\\RobotVision\\testAsift\\rgb_10.jpg")), "d:\\Programming\\RobotVision\\testAsift\\rgb_10.png");
//        ASIFTDescriptor.getDescriptor("d:\\Programming\\RobotVision\\testAsift\\rgb_10.png", "d:\\Programming\\RobotVision\\testAsift\\rgb_10.asift");
//        asift.predict("D:\\Programming\\RobotVision\\testAsift\\", 
//                      "d:\\Programming\\RobotVision\\Image Sets\\training1\\repr_cam\\", 
//                      "D:\\Programming\\RobotVision\\Image Sets\\training1\\asift_features\\", 
//                      "d:\\Programming\\RobotVision\\concepts\\conceptsTraining1.txt");
    //    asift.transformImagesJpgToPng("D:\\Programming\\RobotVision\\Image Sets\\test1\\std_cam\\", "D:\\Programming\\RobotVision\\Image Sets\\test1\\png_cam\\");
 //       asift.computeASIFTFeatures("D:\\Programming\\RobotVision\\Image Sets\\test1\\png_cam\\", "D:\\Programming\\RobotVision\\Image Sets\\test1\\asift_features\\", "");
//        for (int i = 0; i < 9; i++){
//     
//            System.out.println("Getting representative images for label " + i);
//           
//            asift.getRepresentativeImages("D:\\Programming\\RobotVision\\training1\\std_cam\\",
//                                      "D:\\Programming\\RobotVision\\training1\\repr_cam\\",
//                                      "D:\\Programming\\RobotVision\\training1\\asift_features\\", 
//                                      "d:\\Programming\\RobotVision\\concepts\\conceptsTraining1.txt", 
//                                      "D:\\Programming\\RobotVision\\training1\\locations\\SelectedImages.txt", 
//                                      "D:\\Programming\\RobotVision\\output ASIFT\\representativeImages.txt", i);
//        }
            
         
        
   //     asift.train("D:\\Programming\\RobotVision\\training1\\asift_features\\", "d:\\Programming\\RobotVision\\concepts\\conceptsTraining1.txt", "D:\\Programming\\RobotVision\\training1\\locations\\rgb.txt");
      //  
   
   //     asift.computeASIFTFeatures("D:\\Dana\\FACULTATE\\LICENTA\\training1\\png_cam\\", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\asift_features\\");
        
//        float log = -0.9793f + 2;
//        System.out.println(((log <= -2) ? (Math.abs(log) / (1 + Math.abs(log))) / 2 + 0.5 : (1 / (1 + Math.exp((float)(log))))));
        
//      Image.saveAsPNG(ImageIO.read(new File("D:\\Programming\\RobotVision\\training1\\std_cam\\rgb_100.jpg")), "rgb_100.pgm");
//      Image.saveAsPng(ImageIO.read(new File("D:\\Dana\\FACULTATE\\LICENTA\\training1\\std_cam\\rgb_1000.jpg")), "rgb_1000.png");
  //     ASIFTDescriptor.getDescriptor("rgb_1.png", "rgb_1.asift");
     //  ASIFTDescriptor.getDescriptor("rgb_1000.png", "rgb_1000.asift");
    //   System.out.println(ASIFTDescriptor.compareDescriptors("rgb_1.asift", "D:\\Programming\\RobotVision\\training2\\asift_features\\rgb_8.asift"));
        
       
 //      NARFTest narf = new NARFTest();
        //narf.computeNARFFeatures("D:\\Dana\\FACULTATE\\LICENTA\\training1\\pcd_files", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\narf_features");
  //    narf.train("D:\\Dana\\FACULTATE\\LICENTA\\training1\\narf_features\\", "concepts\\conceptsTraining1.txt", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\locations\\depth.txt");
//       narf.predict("testImages\\", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\narf_features\\", "concepts\\conceptsTraining1.txt", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\locations\\depth.txt", "output NARF\\predictedNARF.txt", 1);
  // Evaluation.evaluate("output NARF\\predictedNARF.txt", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\locations\\depth.txt");
       
       
       //       NarfDescriptor.getDescriptor("D:\\Dana\\FACULTATE\\LICENTA\\training1\\pcd_files\\depth_1000.pcd", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\narf_features\\depth_1000.txt");
 //       NarfDescriptor.getDescriptor("D:\\Dana\\FACULTATE\\LICENTA\\training1\\pcd_files\\depth_1001.pcd", "D:\\Dana\\FACULTATE\\LICENTA\\training1\\narf_features\\depth_1001.txt");
 
    } 
}
