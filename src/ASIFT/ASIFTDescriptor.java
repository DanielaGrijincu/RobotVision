/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ASIFT;

/**
 *
 * @author Danna
 */
public class ASIFTDescriptor {
    
    public static native void getDescriptor(String imageFilename, String outputDescriptorFilename);
    public static native float compareDescriptors(String descriptorFilename1, String descriptorFilename2);

    static {
        System.loadLibrary("Libraries/demo_ASIFT");
    }
}
