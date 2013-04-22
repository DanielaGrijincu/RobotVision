package Utils;


import java.io.*;
import libsvm.svm_problem;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Danna
 */
public class Serialization {
    
    public static void serialize(Object obj, String filename) throws IOException {
        FileOutputStream outFileStream = null;
        ObjectOutputStream outStream = null;
        try {

            outFileStream = new FileOutputStream(filename);
            outStream = new ObjectOutputStream(outFileStream);

            outStream.writeObject(obj);
            outStream.flush();

        } finally {
            try {
                outFileStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static Object deserialize(String filename) throws IOException, ClassNotFoundException {
        Object obj = null;
        FileInputStream inFileStream = null;
        ObjectInputStream inStream = null;

        try {
            inFileStream = new FileInputStream(filename);
            inStream = new ObjectInputStream(inFileStream);
            obj = inStream.readObject();
        } finally {
            try {
                inFileStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return obj;
    }
    
     public static void saveGramMatrix(svm_problem svmProblem, String filename) throws IOException {
      
         BufferedWriter out = new BufferedWriter(new FileWriter(filename));

        try {
            for (int i = 0; i < svmProblem.l; i++) {
                out.write(svmProblem.y[i] + " ");
                for (int j = 0; j < svmProblem.x[i].length; j++) {
                    out.write(svmProblem.x[i][j].index + ":" + svmProblem.x[i][j].value + " ");
                }
                out.write("\n");
            }
        } finally {
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    } 
    public static void saveVectorToFile(double[] v, String file)
    {
        try {
            // Create file
            FileWriter fstream = new FileWriter(file);
            BufferedWriter out = new BufferedWriter(fstream);
           
            for (int i = 0; i < v.length; i++)
                out.write(i + ":" + v[i] + " ");
            
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
    
}
