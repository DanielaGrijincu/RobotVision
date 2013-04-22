/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 *
 * @author Danna
 */
public class Preprocessing {
    
    public static int computePixelDifference(BufferedImage img1, BufferedImage img2){
      
        int diff = 0;
        
        int[] pixels1 = Image.getPixels(img1);
        int[] pixels2 = Image.getPixels(img2);
        
        //int[] temp = new int[pixels1.length];

        for (int i = 0; i < pixels1.length; i++){
            diff += Math.abs(pixels1[i] - pixels2[i]); 
        //  temp[i] = Math.abs(pixels1[i] - pixels2[i]);
        }
        
 //       Image.createImageFromIntArray(temp, img1.getWidth(), img1.getHeight(), "difference");
        return diff;
        
    }
    
    public static int computePixelSum(BufferedImage img) {
      
        int sum = 0;
        int[] pixels = Image.getPixels(img);
        for (int i = 0; i < pixels.length; i++) {
            sum += Math.abs(pixels[i]);

        }
        return sum;

    }
            
    
    public static void process(String imagesFolder, String sourceImages, String destImagesFolder, float threshold){
        
        DataInputStream in = null;
        DataOutputStream out = null;
        BufferedImage prev = null;
        BufferedImage current = null;
        ArrayList<String> list = new ArrayList<String>();
        
        try {
            
            FileInputStream fstream = new FileInputStream(sourceImages);
            FileOutputStream ostream = new FileOutputStream(destImagesFolder);
            
            in = new DataInputStream(fstream);
            out = new DataOutputStream(ostream);

            final BufferedReader br = new BufferedReader(new InputStreamReader(in));
            final BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(out));

            int d, s = 0;
            String strLineCurrent, strLinePrevious = "";
            while ((strLineCurrent = br.readLine()) != null) {

                String filename = strLineCurrent.subSequence(0, strLineCurrent.indexOf(':')).toString();
                
                if (prev == null){
                    prev = ImageIO.read(new File(imagesFolder + "\\" + filename));
                    s = Preprocessing.computePixelSum(Image.convertToGrayscale(prev));
                    strLinePrevious = strLineCurrent;
                }
                else{
                    
                    current = ImageIO.read(new File(imagesFolder + "\\" + filename));
                    d = Preprocessing.computePixelDifference(Image.convertToGrayscale(current), Image.convertToGrayscale(prev));
               
                    if ((float) d / (float) s < threshold) {
                        if (!list.contains(strLinePrevious))
                            list.add(strLinePrevious);
                    }else{
                        
                        if (!list.contains(strLinePrevious))
                            list.add(strLinePrevious);
                        
                        if (!list.contains(strLineCurrent))
                            list.add(strLineCurrent);
                        
                         prev = current;
                         strLinePrevious = strLineCurrent;
                         s = Preprocessing.computePixelSum(Image.convertToGrayscale(prev));
                    }
                }

                System.out.println("Processed image " + filename);
                
            }
            
            for (int i = 0; i < list.size(); i++){
                wr.append(list.get(i));
                wr.newLine();
            }
                
             wr.flush();
             
        } catch (IOException ex) {
            System.out.println(ex.getStackTrace().toString() + ex.toString());
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace().toString() + ex.toString());
        } finally {
            try {
                in.close();
                out.close();
            } catch (IOException ex) {
                System.out.println(ex.getStackTrace().toString() + ex.toString());
            }
        }
        
    }
    
}
