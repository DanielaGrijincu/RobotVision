/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package NARF;

/**
 *
 * @author Danna
 */
public class NarfDescriptor {

    public static native void getDescriptor(String imageFilename, String descriptorFilename);
    public static native float compareDescriptors(String descriptorFilename1, String descriptorFilename2, float threshold);

    static {
        System.loadLibrary("Libraries/narfExtract");
    }
}
