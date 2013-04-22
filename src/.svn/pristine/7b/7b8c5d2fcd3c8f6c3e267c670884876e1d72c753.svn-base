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
public class CartesianFilter extends Filter {

    public float[] xKernel = null;
    public float[] yKernel = null;

//	/** Constructor. */
    public CartesianFilter(int dx, int dy) {
        super(new CartesianFilterInfo(dx, dy));
        createXKernel(dx);
        createYKernel(dy);
    }

    /**
     * Returns the filter info.
     */
    @Override
    public CartesianFilterInfo getFilterInfo() {
        return (CartesianFilterInfo) this.filterInfo;
    }

    /**
     * Creates horizontal kernel.
     */
    private void createXKernel(int dx) {

        if (dx == 1) {
            // Allocate memory for the kernel
            xKernel = new float[3];

            // Fill in the kernel matrix
            xKernel[0] = (float) -0.5;
            xKernel[1] = 0;
            xKernel[2] = (float) 0.5;
        } else if (dx == 2) {
            // Allocate memory for the kernel
            xKernel = new float[3];

            // Fill in the kernel matrix
            xKernel[0] = 1;
            xKernel[1] = -2;
            xKernel[2] = 1;
        }


//        System.out.println("X kernel : \n");
//        for (int i = 0; i < 3; ++i) {
//            System.out.println(xKernel[i] + " ");
//        }
    }

    /**
     * Creates vertical kernel.
     */
    private void createYKernel(int dy) {

        if (dy == 1) {
            // Allocate memory for the kernel
            yKernel = new float[3];

            // Fill in the kernel matrix
            yKernel[0] = (float) -0.5;
            yKernel[1] = 0;
            yKernel[2] = (float) 0.5;
        } else if (dy == 2) {
            // Allocate memory for the kernel
            yKernel = new float[3];

            // Fill in the kernel matrix
            yKernel[0] = 1;
            yKernel[1] = -2;
            yKernel[2] = 1;
        }

//        System.out.println("Y kernel : \n");
//        for (int i = 0; i < 3; ++i) {
//            System.out.println(xKernel[i] + " ");
//        }
    }

    @Override
    public BufferedImage apply(BufferedImage bufferedImage) {

        BufferedImage result = null;
        BufferedImageOp conv;

        if (xKernel != null) {
            conv = new ConvolveOp(new Kernel(3, 1, this.xKernel), ConvolveOp.EDGE_NO_OP, null);
            result = conv.filter(bufferedImage, null);

            if (yKernel != null) {
                conv = new ConvolveOp(new Kernel(1, 3, this.yKernel), ConvolveOp.EDGE_NO_OP, null);
                result = conv.filter(bufferedImage, null);
            }
        } else if (yKernel != null) {
            conv = new ConvolveOp(new Kernel(1, 3, this.yKernel), ConvolveOp.EDGE_NO_OP, null);
            result = conv.filter(bufferedImage, null);
        }

        return result;
    }
}
//Kernel k1 = new Kernel(this.xKernel.length, 1, this.xKernel);
//ConvolveOp op = new ConvolveOp(k1);
//result = op.filter(bufferedImage, null);
//             KernelJAI x = new KernelJAI(1, this.xKernel.length, this.xKernel);
//             result = JAI.create("convolve", bufferedImage, x).getAsBufferedImage();
