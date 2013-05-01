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

    } 
}
