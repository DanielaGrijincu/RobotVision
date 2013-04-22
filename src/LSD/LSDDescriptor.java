/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package LSD;

/**
 *
 * @author Danna
 */
public class LSDDescriptor {
    
    public static native int getLineSegments(String inputFilename);

    static {
        if (System.getProperty("os.arch").toLowerCase().equals("amd64"))
              System.loadLibrary("Libraries/LSDLibrary64");
        else
            System.loadLibrary("Libraries/LSDLibrary32");
    }
    
}
