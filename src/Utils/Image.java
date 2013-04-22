/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.*;
import java.io.*;
import javax.media.jai.*;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
 
import com.sun.media.jai.codec.PNMEncodeParam;
/**
 *
 * @author Danna
 */
public class Image {

    public final static float FLT_EPSILON = 1.192092896e-07F;

    public static int[] getPixels(BufferedImage pi) {

        SampleModel sm = pi.getSampleModel();
        int width = pi.getWidth();
        int height = pi.getHeight();
        int bands = sm.getNumBands();

        Raster raster = pi.getData();

        int[] pixels = new int[width * height * bands];
        int y = pi.getMinY();
        int x = pi.getMinX();
        pixels = raster.getPixels(x, y, width, height, pixels);

        return pixels;
    }
    
    public static BufferedImage convertToGrayscale(BufferedImage image) {
        
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);  
        Graphics g = result.getGraphics();  
        g.drawImage(image, 0, 0, null);  
        g.dispose();  
        return result;
    }
    
    static int rgb2gray(int srgb) {
        int r = (srgb >> 16) & 0xFF;
        int g = (srgb >> 8) & 0xFF;
        int b = srgb & 0xFF;

//        float h = 0.f;
//        float s = 0.f;
//        float vmin, vmax, diff, l;
//
//        vmax = vmin = r;
//        if( vmax < g ) vmax = g;
//        if( vmax < b ) vmax = b;
//        if( vmin > g ) vmin = g;
//        if( vmin > b ) vmin = b;
//
//        diff = vmax - vmin;
//        l = (vmax + vmin)*0.5f;
//
//            if( diff > FLT_EPSILON )
//            {
//                s = l < 0.5f ? diff/(vmax + vmin) : diff/(2 - vmax - vmin);
//                diff = 60.f/diff;
//
//                if( vmax == r )
//                    h = (g - b)*diff;
//                else if( vmax == g )
//                    h = (b - r)*diff + 120.f;
//                else
//                    h = (r - g)*diff + 240.f;
//
//                if( h < 0.f ) h += 360.f;
//            }

//            return (int)(h + l + s);
        
//        int vmin = Math.min(Math.min(r, g), b);
//        int vmax = Math.max(Math.max(r, g), b);
//
//        float l = (vmin + vmax)/2;
//
//        float s = 1;
//        if (l < 0.5)
//            s = (vmax - vmin) / (vmax + vmin);
//        else
//            if (l > 0.5)
//                if (2 - (vmax + vmin) != 0)
//                     s = (vmax - vmin) / (2 - (vmax + vmin));
//
//        float h = 1;
//
//
//
//        if (vmax == r)
//          if (s != 0)
//            h = 60 * (g - b) / s;
//        else
//            if (vmax == g)
//                if (s != 0)
//                h = 120 + 60 * (b - r) / s;
//            else
//                if (s != 0)
//                h = 240 + 60 * (r - g) / s;
//
//        if (h < 0)
//            h = h + 360;
//
//
//        l = l * 255;
//        s = 255 * s;
//        h = h / 2;
//
//        return (int) (h + l + s);

        return (int) (0.299 * r + 0.587 * g + 0.114 * b);
    }

    public static BufferedImage getIntensitityChannelL(BufferedImage pi) {


        SampleModel sm = pi.getSampleModel();
        int width = pi.getWidth();
        int height = pi.getHeight();
        int bands = sm.getNumBands();
        
        int[] input = new int[width*height];
         for (int i = 0; i < width - 1; i++) {
            for (int j = 0; j < height - 1; j++) {


                input[i + j * width] = rgb2gray(pi.getRGB(i, j));

              //  System.out.println(input[i + j * width]);
            }
        }
         
         pi = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
         pi.getRaster().setPixels(0, 0, width, height, input);

        // saveAsJpg(pi, "DescL");

         return pi;
         //return input;
        
//        int[] pixels = getPixels(pi);
//        int[] ch = new int[pixels.length / bands];
//        int offset;
//        int i = 0;
//        int[] result = new int[width * height];
//        
//        for (int h = 0; h < height; h++) {
//            for (int w = 0; w < width; w++) {
//                offset = h * width * bands + w * bands;
//                int R = ((offset < pixels.length)) ? pixels[offset] : 0;
//                int G = ((offset + 1 < pixels.length)) ? pixels[offset + 1] : 0;
//                int B = ((offset + 2 < pixels.length)) ? pixels[offset + 2] : 0;
//
//                result[i++] = (int) (0.299 * R + 0.587 * G + 0.114 * B);
//            }
//        }
        
        //return result;
//        int[][] pix;
//        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
//        img.getRaster().setPixels(0, 0, width, height, result);
//         Image.createImageFromIntArray(result, width, height, "greyscaletest");
//        return result;
    }

    public static void createImageFromIntArray(int[] input, int width, int height, String name) {

        DataBufferInt dbuffer = new DataBufferInt(input, input.length);
        SampleModel sampleModel = RasterFactory.createPixelInterleavedSampleModel(DataBuffer.TYPE_INT, width, height, 1);
        ColorModel colorModel = PlanarImage.createColorModel(sampleModel);
        Raster raster = RasterFactory.createWritableRaster(sampleModel, dbuffer, new Point(0, 0));
        TiledImage tiledImage = new TiledImage(0, 0, width, height, 0, 0, sampleModel, colorModel);
        tiledImage.setData(raster);
        JAI.create("filestore", tiledImage, "Output CRFH\\" + name + ".tiff", "TIFF");

    }
    
    public static void saveAsJPG(BufferedImage image, String outputfileName) {

        File file = new File(outputfileName);
        try {
            ImageIO.write(image, "jpg", file);  // ignore returned boolean
        } catch(IOException e) {
            System.out.println("Write error for " + file.getPath() +
                               ": " + e.getMessage());
        }
    }
    
    public static void saveAsPNG(BufferedImage image, String fileName) {
       
        File file = new File(fileName);
        try {
            
              ImageIO.write(image, "png", file);  // ignore returned boolean
              
        } catch(IOException e) {
            System.out.println("Write error for " + file.getPath() +
                               ": " + e.getMessage());
        }
        
    }
    
    public static void saveAsPGM(BufferedImage my_image, String outputFilename) {
       
        try {

            RenderedImage image = my_image;

            PNMEncodeParam param = new PNMEncodeParam();
            param.setRaw(true);

            RenderedOp op = JAI.create("filestore", image, outputFilename, "PNM", param);

        } catch (Exception ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        }
        
    }
    
    public static void splitIntoDirectories(String sourceImagesDir, String destImagesDir, String imagesLocationsFile){
        
        try {

            DataInputStream in = null;

            FileInputStream fstream = new FileInputStream(imagesLocationsFile);
            in = new DataInputStream(fstream);

            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String strLine;
            while ((strLine = br.readLine()) != null) {

                String concept = strLine.substring(strLine.indexOf(':') + 2);
                
                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();
                
                File source = new File(sourceImagesDir + filename);
                if (source.exists()) {

                    File desc = new File(destImagesDir + concept + "\\" + filename);

                    FileUtils.copyFile(source, desc);
                    
                }
            }

        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        }
    }
//
//    public static void createImageFromPlanarImage(BufferedImage image, String name) {
//        JAI.create("filestore", image, "output CRFH\\" + name + ".jpg", "JPEG");
//
//    }
//    public static void createImageFromPlanarImage(PlanarImage image, String name) {
//        JAI.create("filestore", image, "output CRFH\\" + name + ".jpg", "JPEG");
//
//    }
}
