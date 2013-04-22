/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import LSD.LSDDescriptor;
import Utils.Annotations;
import Utils.Image;
import java.io.*;
import java.util.*;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;
import javax.imageio.ImageIO;

/**
 *
 * @author Danna
 */
public class LSDTest {
    
    public HashMap<String, String> getLocationsMap(String imageLocationsFile) throws FileNotFoundException, IOException {

        HashMap<String, String> allImages = new HashMap<String, String>();
        
        try {

            DataInputStream in = null;

            FileInputStream fstream = new FileInputStream(imageLocationsFile);
            in = new DataInputStream(fstream);

            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(':') + 2);
                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();
                filename = strLine.substring(0, filename.indexOf("."));

                allImages.put(filename, concept);

            }

        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        }

        return allImages;

    }

    
    public void determineLines(String inputImagesDir, String imageLocations, String annotationsFile, String outputStatisticsFile) throws IOException{
        
        HashMap<String, String> imageMap = this.getLocationsMap(imageLocations); // image_name => image_concept
        
        // concept => ArrayList<image_name, nr_lines_detected>
        HashMap<String, ArrayList<SimpleImmutableEntry<String, Object>>> lineDistribution = new HashMap<String, ArrayList<AbstractMap.SimpleImmutableEntry<String, Object>>>();

        String outputPgmDir = (String) inputImagesDir.subSequence(0, inputImagesDir.lastIndexOf("\\"));
        outputPgmDir = (String) outputPgmDir.subSequence(0, outputPgmDir.lastIndexOf("\\")) + "\\pgm_cam\\";

        System.out.println("Started Line Segment Detection.");

        DataInputStream in = null;

        File dir = new File(inputImagesDir);
        String[] children = dir.list();

            if (children != null) {

                for (int i = 0; i < children.length; i++) {

                    String filename = children[i];

                    if (filename.endsWith(".pgm")) {
                        // fist the image needs to be transformed into PGM format
                        String name = filename.subSequence(0, filename.indexOf('.')).toString();
                        
                        //Image.saveAsPGM(ImageIO.read(new File(inputImagesDir + filename)), outputPgmDir + name + ".pgm");
                        
                        int nr_lines = LSDDescriptor.getLineSegments(inputImagesDir + filename);//outputPgmDir + name + ".pgm");
                        
                        if (imageMap.containsKey(name)){
                            if (lineDistribution.get(imageMap.get(name)) != null)
                                lineDistribution.get(imageMap.get(name)).add(new AbstractMap.SimpleImmutableEntry<String, Object>(filename, nr_lines));
                            else{
                                lineDistribution.put(imageMap.get(name), new ArrayList<AbstractMap.SimpleImmutableEntry<String, Object>>());
                                lineDistribution.get(imageMap.get(name)).add(new AbstractMap.SimpleImmutableEntry<String, Object>(filename, nr_lines));
                            }
                        }
                        
                        if (i % 100 == 0)
                            System.out.println("Processed image " + i);
                        
                }
            }
            }
            
            saveHashMapToFile(lineDistribution, annotationsFile, outputStatisticsFile);
    }
    
    public void saveHashMapToFile(HashMap<String, ArrayList<SimpleImmutableEntry<String, Object>>> hashMap, String annotationsFile, String filename) throws FileNotFoundException, IOException{
        
        Annotations.loadAnnotations(annotationsFile);
       
        DataOutputStream out = null;
        
        FileOutputStream ostream = new FileOutputStream(filename);
            
        out = new DataOutputStream(ostream);

        final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));
        
        for (int i = 0; i < Annotations.count(); i++){
            
            wr.append("-----------------------------------------------------------");
            wr.newLine();
            wr.append("++++++++  " + Annotations.getAnnotation(i) + "  ++++++++");
            wr.newLine();
            wr.append("-----------------------------------------------------------");
            
            for (Iterator<Entry<String, ArrayList<SimpleImmutableEntry<String, Object>>>> j = hashMap.entrySet().iterator(); j.hasNext();) {
            Entry<String, ArrayList<SimpleImmutableEntry<String, Object>>> entry = j.next();
            
            if (entry.getKey().equals(Annotations.getAnnotation(i))) {
                
                for (int k = 0; k < entry.getValue().size(); k++){
                    wr.append( (entry.getValue()).get(k).getKey() + ": " + (entry.getValue()).get(k).getValue().toString());
                    wr.newLine();
                }
                
            }
        }
            
        }
        
        wr.flush();
        wr.close();
    }
}
      
        
    
    

