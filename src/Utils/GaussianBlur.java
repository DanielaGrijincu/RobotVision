/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Utils; // Compose Receptive Field Histograms

import java.awt.Graphics;
import java.awt.image.*;


/**
 *
 * @author Daniela Grijincu
 */
public class GaussianBlur {

    private BufferedImage bufferedImage;
    private int sigma;
    private static float[] kernel;
    private static int radius;
    private int[] pixels;

    public GaussianBlur(BufferedImage image) {
        this.bufferedImage = image;
    }

//    public GaussianBlur(BufferedImage image, int kernelRadius) {
//        this.bufferedImage = image;
//        this.radius = kernelRadius;
//    }

    public static BufferedImage blur(BufferedImage image, int sigma) {

        computeKernel(sigma);
        BufferedImageOp conv = new ConvolveOp(new Kernel(radius, radius, GaussianBlur.kernel));
        return conv.filter(image, null);
    }

    public static BufferedImage toGrayscale(BufferedImage image) {
        
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);  
        Graphics g = result.getGraphics();  
        g.drawImage(image, 0, 0, null);  
        g.dispose();  
        return result;
    }

    private static void computeKernel(int sigma2) {

        double sigma = Math.sqrt(sigma2);

        int gaussSize = (int) Math.max(Math.ceil(sigma * 3), 1);
        radius = 2 * gaussSize + 1;

        kernel = new float[radius * radius];
        float sum = 0;
        for (int y = 0; y < radius; y++) {
            for (int x = 0; x < radius; x++) {
                int off = y * radius + x;
                int xx = x - radius / 2;
                int yy = y - radius / 2;
                kernel[off] = (float) Math.pow(Math.E, -(xx * xx + yy * yy)
                        / (2 * (sigma * sigma)));
                sum += kernel[off];
            }
        }

        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }
        
//        kernel = new float[radius * radius];
//        float sum = 0;
//        for (int y = 0; y < radius; y++) {
//            for (int x = 0; x < radius; x++) {
//                int off = y * radius + x;
//                int xx = x - radius / 2;
//                int yy = y - radius / 2;
//                kernel[off] = (float) Math.pow(Math.E, -(xx * xx + yy * yy)
//                        / (2 * (sigma * sigma)));
//                sum += kernel[off];
//            }
//        }
//
//        for (int i = 0; i < kernel.length; i++) {
//            kernel[i] /= sum;
//        }
    }
    
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
}
