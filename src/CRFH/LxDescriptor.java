/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

import Utils.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author Danna
 */
public class LxDescriptor extends Descriptor {

    /** Constructor. */
    public LxDescriptor(double scale, int bins) {
        super(DescriptorType.DT_Lx, -128, 128, scale, bins);
    }

    @Override
    public void createRequiredFilters(FilterCache filterCache) {
       
        GaussianFilterInfo gfi = new GaussianFilterInfo(this.scale);
        CartesianFilterInfo cfi = new CartesianFilterInfo(1, 0);

        filterCache.createFilter(gfi);
        filterCache.createFilter(cfi);
    }

    @Override
    public void createRequiredChannels(ChannelCache channelCache) {
        channelCache.createChannel(ChannelType.CT_L);
    }
    
    @Override
    public void createRequiredScales(ScaleSpaceCache scaleSpaceCache){

     scaleSpaceCache.createScaleSpaceSample(ChannelType.CT_L, this.scale);
    }

    @Override
    public int[] apply(BufferedImage image, ChannelCache channelCache, ScaleSpaceCache scaleSpaceCache, FilterCache filterCache) {

        BufferedImage result = null;
        
        result = scaleSpaceCache.getScaleSpaceSample(ChannelType.CT_L, this.scale);

//        GaussianFilterInfo gfi = new GaussianFilterInfo(this.scale);
//        result = filterCache.applyFilter(gfi, image);

        CartesianFilterInfo cfi = new CartesianFilterInfo(1, 0);
        result = filterCache.applyFilter(cfi, result);
        
        //Image.saveAsJpg(result, "LxDesc");

//        int width = result.getWidth();
//        int height = result.getHeight();
        
        int[] input = Image.getPixels(result);

        // Normalize
        double factor = Math.sqrt(this.scale);
        for (int i = 0; i < input.length; i++) {
            input[i] *= factor;
        }

//        Image.createImageFromIntArray(input, width, height, "LxDesc");

        // Return the result
        return input;
    }

};
