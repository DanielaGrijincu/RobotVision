/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

/**
 *
 * @author Danna
 */

public class GaussianFilter extends Filter {

    /** Vertical component of a separable Gaussian filter. */
    static float[] verticalKernel;
    /** Horizontal component of a separable Gaussian filter. */
    static float[] horizontalKernel;

    float[] kernel;

    /** Constructor. */
    GaussianFilter(double sigma) {
        super(new GaussianFilterInfo(sigma));
        createGaussHorizontalKernel(sigma);
        createGaussVerticalKernel(sigma);
        //computeKernel(sigma);
    }

       /** Returns the filter info. */
    @Override
    public GaussianFilterInfo getFilterInfo() {
        return (GaussianFilterInfo) this.filterInfo;
    }

    /** Creates vertical component of a separable Gaussian filter. */
    private void createGaussVerticalKernel(double sigma2) {

        double sigma = Math.sqrt(sigma2);
        int gaussSize = (int) Math.max(Math.ceil(sigma * GAUSSIAN_SIGMAS), 1);
        int kernelSize = 2 * gaussSize + 1;

        // Allocate memory for the kernel
        verticalKernel = new float[kernelSize];

        // Be robust to sigma = 0
        if (sigma2 == 0) {
            verticalKernel[0] = 0;
            verticalKernel[1] = 1;
            verticalKernel[2] = 0;
            return;
        }

        // Fill in the values
        double sum = 0;
        for (int i = 0; i < kernelSize; ++i) {
            int y = i - gaussSize;
            float value =  (float) Math.pow(Math.E, -(y * y) / (2 * sigma2));
            verticalKernel[i] = value;
            sum += value;
        }

        // Normalize
        for (int i = 0; i < kernelSize; ++i) {
            verticalKernel[i] /= sum;
        }
//
//        System.out.println("Vertical kernel : \n");
//        for (int i = 0; i < kernelSize; ++i) {
//            System.out.println(verticalKernel[i] + " ");
//        }

    }

    private void computeKernel(double sigma2) {

        double sigma = Math.sqrt(sigma2);

        int gaussSize = (int) Math.max(Math.ceil(sigma * GAUSSIAN_SIGMAS), 1);
        int radius = 2 * gaussSize + 1;

        this.kernel = new float[radius * radius];
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

        for (int i = 0; i < this.kernel.length; i++) {
            this.kernel[i] /= sum;
        }
    }

    /** Creates horizontal component of a separable Gaussian filter. */
    private void createGaussHorizontalKernel(double sigma2) {

        double sigma = Math.sqrt(sigma2);

        int gaussSize = (int) Math.max(Math.ceil(sigma * GAUSSIAN_SIGMAS), 1);
        int kernelSize = 2 * gaussSize + 1;

        // Allocate memory for the kernel
        horizontalKernel = new float[kernelSize];

        // Be robust to sigma = 0
        if (sigma2 == 0) {
            horizontalKernel[0] = 0;
            horizontalKernel[1] = 1;
            horizontalKernel[2] = 0;
            return;
        }

        // Fill in the values
        double sum = 0;
        for (int i = 0; i < kernelSize; ++i) {
            int x = i - gaussSize;
            float value = (float) Math.pow(Math.E, -(x * x) / (2 * sigma2));
            horizontalKernel[i] = value;
            sum += value;
        }

        // Normalize
        for (int i = 0; i < kernelSize; ++i) {
            horizontalKernel[i] /= sum;
        }

//        System.out.println("Horizontal kernel : \n");
//        for (int i = 0; i < kernelSize; ++i) {
//            System.out.println(horizontalKernel[i] + " ");
//        }

    }

    public BufferedImage apply(BufferedImage bufferedImage) {

        BufferedImageOp conv1 = new ConvolveOp(new Kernel(GaussianFilter.horizontalKernel.length, 1, GaussianFilter.horizontalKernel), ConvolveOp.EDGE_NO_OP, null);
        BufferedImageOp conv2 = new ConvolveOp(new Kernel(1, GaussianFilter.verticalKernel.length, GaussianFilter.verticalKernel), ConvolveOp.EDGE_NO_OP, null);
        return conv1.filter(conv2.filter(bufferedImage, null), null);
        
        	
    }

}
