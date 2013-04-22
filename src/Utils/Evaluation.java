/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Danna
 */
public class Evaluation {

    public static HashMap<String, String> mapPredictedConcepts = new HashMap<String, String>();
    public static HashMap<String, String> mapActualConcepts = new HashMap<String, String>();

    public static void evaluate(String pathToPredictionFile, String pathToActualLocationsFile) {

        DataInputStream in1 = null;
        DataInputStream in2 = null;

        try {

            FileInputStream fstream1 = new FileInputStream(pathToPredictionFile);
            FileInputStream fstream2 = new FileInputStream(pathToActualLocationsFile);

            in1 = new DataInputStream(fstream1);
            in2 = new DataInputStream(fstream2);

            final BufferedReader readPredicted = new BufferedReader(new InputStreamReader(in1));
            final BufferedReader readActual = new BufferedReader(new InputStreamReader(in2));

            String strLine;
            while ((strLine = readPredicted.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(' ') + 2);
                String filename = strLine.subSequence(0, strLine.indexOf(' ')).toString();

                mapPredictedConcepts.put(filename, concept);

            }

            while ((strLine = readActual.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(':') + 2);
                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();

                mapActualConcepts.put(filename, concept);
            }

            int correct = 0;
            int incorrect = 0;
            int unknown = 0;

            for (Map.Entry<String, String> entry : mapPredictedConcepts.entrySet()) {
                if (mapActualConcepts.containsKey(entry.getKey())) {
                    if (entry.getValue().equals(mapActualConcepts.get(entry.getKey())) == true) {
                        correct++;
                    } else if (entry.getValue().equals("Unknown")) {
                        unknown++;
                    } else {
                        incorrect++;
                    }
                }
            }



            System.out.println("Correctly classified images: " + correct);
            System.out.println("Incorrectly classified images: " + incorrect);
            System.out.println("Unknown classified images: " + unknown);
            System.out.println("Classification percent: " + (float) ((float) correct / (float) (correct + incorrect + unknown)) * 100 + "%");



        } catch (IOException ex) {
            System.out.println(ex.getStackTrace().toString() + ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().toString() + ex.toString());
        } finally {
            try {
                in1.close();
                in2.close();
            } catch (IOException ex) {
                System.out.println(ex.getStackTrace().toString() + ex.toString());
            }
        }
    }
    
    public static void getAccuracy(String pathToPredictionsFile, String pathToActualImageLocations) throws IOException{
        
        TreeMap<Integer, String> myPredictions = Evaluation.getPredictions(pathToPredictionsFile);
        
        Evaluation.getAccuracy(myPredictions, pathToActualImageLocations);
                
    }

    public static void getAccuracy(TreeMap<Integer, String> myPredictions, String pathToActualImageLocations) throws FileNotFoundException, IOException {

               
        TreeMap<Integer, String> actualLocations = new TreeMap<Integer, String>();
        DataInputStream in = null;
        FileInputStream fstream = new FileInputStream(pathToActualImageLocations);
        in = new DataInputStream(fstream);
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while ((strLine = br.readLine()) != null) {

            String concept = strLine.substring(strLine.indexOf(':') + 2);
            int nr_image = Integer.parseInt(strLine.substring(strLine.indexOf("_") + 1, strLine.indexOf(".")));

            actualLocations.put(nr_image, concept);
        }

        int correct = 0;
        int incorrect = 0;
        int unknown = 0;

        for (Iterator<Map.Entry<Integer, String>> j = myPredictions.entrySet().iterator(); j.hasNext();) {

            Map.Entry<Integer, String> entry = j.next();

            if (actualLocations.containsKey(entry.getKey())) {

                String actual = actualLocations.get(entry.getKey());
                if (entry.getValue().equals(actual)) {
                    correct++;
                } else if (entry.getValue().equals(" ")) {
                    unknown++;
                } else {
                    incorrect++;
                }
            }
        }
        
        

        System.out.println("Correctly classified images: " + correct);
        System.out.println("Incorrectly classified images: " + incorrect);
        System.out.println("Unknown classified images: " + unknown);
        System.out.println("Classification percent: " + (float) ((float) correct / (float) (correct + incorrect + unknown)) * 100 + "%");
    }
    
    public static TreeMap<Integer, String> getPredictions(String pathToPredictionsFile) throws IOException{
        
        TreeMap<Integer, String> myPredictions = new TreeMap<Integer, String>();
        
        DataInputStream in = null;
        FileInputStream fstream = new FileInputStream(pathToPredictionsFile);
        in = new DataInputStream(fstream);
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while ((strLine = br.readLine()) != null) {

            String concept = strLine.substring(strLine.indexOf(" ") + 1);
            int nr_image = Integer.parseInt(strLine.substring(0, strLine.indexOf(" ")));

            myPredictions.put(nr_image, concept);
        }
        
        return myPredictions;
    }
    
    public static void saveResultsInPythonScriptFormat(String pathToPredictionsFile, String pathWhereToSaveOutput) throws IOException{
        
        DataInputStream in = null;
        FileInputStream fstream1 = new FileInputStream(pathToPredictionsFile);
        in = new DataInputStream(fstream1);
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));

        FileOutputStream fstream2 = null;
        DataOutputStream out = null;
        fstream2 = new FileOutputStream(pathWhereToSaveOutput);
        out = new DataOutputStream(fstream2);
        final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));
        
        String strLine;

        while ((strLine = br.readLine()) != null) {

            String concept = strLine.substring(strLine.indexOf(" ") + 1);
            String nr_image = strLine.substring(0, strLine.indexOf(" "));

            wr.append("(" + "\"" + nr_image + "\"" + "," + "\"" + concept + "\"" + ")" + ",");
            wr.newLine();
        }
        
        wr.flush();
        wr.close();
        
    }
    
     public static void saveResultsImageClefFormat(TreeMap<Integer, String> myPredictions, String pathWhereToWriteFile) throws IOException{

        String defaultWhereToWriteFile = "PredictionsForImageCLEF.txt";

        FileOutputStream fstream = null;
        DataOutputStream out = null;
        
        try {
            fstream = new FileOutputStream(pathWhereToWriteFile);
        } catch (FileNotFoundException ex) {
            try {
                fstream = new FileOutputStream(defaultWhereToWriteFile);
            } catch (FileNotFoundException ex1) {
            }
        }
        
        out = new DataOutputStream(fstream);
        final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));
        
        //ImageCLEF Format Output: 00001 Corridor 
        // TreeMap ordering is ascending
         for (Iterator<Map.Entry<Integer, String>> j = myPredictions.entrySet().iterator(); j.hasNext();) {
            
             Map.Entry<Integer, String> entry = j.next();
             
             String addZeros = "";
             
             if (entry.getKey() < 10)
                 addZeros = "0000";
             else
                 if (entry.getKey() < 100)
                      addZeros = "000";  
                 else
                     if (entry.getKey() < 1000)
                      addZeros = "00";  
                 else
                      if (entry.getKey() < 10000)
                          addZeros = "0";  
             
             wr.append(addZeros + entry.getKey() + " " + entry.getValue());
             wr.newLine();
         }
         
         wr.flush();
         wr.close();
    }
}
