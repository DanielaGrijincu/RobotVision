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
public class LDescriptor extends Descriptor {

    /**
     * Constructor.
     */
    public LDescriptor(double scale, int bins) {
        super(DescriptorType.DT_L, 0, 255, scale, bins);
    }

    @Override
    public void createRequiredFilters(FilterCache filterCache) {
        GaussianFilterInfo gfi = new GaussianFilterInfo(this.scale);
        filterCache.createFilter(gfi);
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
//  
//        GaussianFilterInfo gfi = new GaussianFilterInfo(this.scale);
//        result = filterCache.applyFilter(gfi, image);
//
      //  Image.saveAsJpg(result, "LDesc");

        int[] input = Image.getPixels(result);

        // Return the result
        return input;
    }
};
