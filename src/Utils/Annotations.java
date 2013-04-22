/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.io.*;
import java.util.HashMap;

/**
 *
 * @author Danna
 */
public class Annotations {
    
    private static HashMap<String, Integer> stringLabels = new HashMap<String, Integer>();
    private static HashMap<Integer, String> intLabels = new HashMap<Integer, String>();
    private static int count = 0;
    
    public static int addAnnotation(String concept) {
        getStringLabels().put(concept, count++);
        getIntLabels().put(count, concept);
        return count;
    }

    public static void addAnnotation(String concept, int index) {
        getStringLabels().put(concept, index);
        getIntLabels().put(index, concept);
    }
    
    public static int getAnnotation(String concept) {
        return stringLabels.get(concept);
    }
    
    public static String getAnnotation(int index) {
        return intLabels.get(index);
    }

    public static HashMap<String, Integer> getStringLabels() {
        return stringLabels;
    }

    
    public static HashMap<Integer, String> getIntLabels() {
        return intLabels;
    }
    
    public static int count() {
        return stringLabels.size();
    }

    public static void loadAnnotations(String filename) throws IOException {
        DataInputStream in = null;
        try {
            FileInputStream fstream = new FileInputStream(filename);
            in = new DataInputStream(fstream);
            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                try {
                    int index = Integer.parseInt(strLine.substring(0, strLine.indexOf('\t')));
                    String concept = strLine.substring(strLine.indexOf('\t') + 1);
                    Annotations.addAnnotation(concept, index);
                } catch (NumberFormatException ex) {
                }
            }
        } finally {
            in.close();
        }
    } 
    
}
