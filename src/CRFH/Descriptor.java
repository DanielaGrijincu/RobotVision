/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

import java.awt.image.BufferedImage;


/**
 *
 * @author Danna
 */
enum DescriptorType {

    DT_UNKNOWN, DT_L, DT_Lx, DT_Lxx, DT_Ly, DT_Lyy, DT_Lxy
};

/**
 * Interface for a class defining a descriptor.
 * Descriptor can be seen as a kind of combination of the
 * information what filter to apply to which channel at
 * which scale.
 */
public abstract class Descriptor {

    /** Type of the descriptor. */
    DescriptorType descriptorType;
    /** Minimum value of the filter response. */
    int min;
    /** Maximum value of the filter response. */
    int max;
    /** Scale. Sigma*/
    double scale;
    /** Number of quantization levels for this descriptor. */
    int bins;

    /** Constructor. */
    public Descriptor(DescriptorType descriptorType, int min, int max, double scale, int bins) {

        this.descriptorType = descriptorType;
        this.min = min;
        this.max = max;
        this.scale = scale;
        this.bins = bins;
    }

    /** Creates all the required filters in the filter cache. */
    public abstract void createRequiredFilters(FilterCache filterCache);

    /** Creates all the required channels in the channel cache. */
    public abstract void createRequiredChannels(ChannelCache channelCache);
    
    /** Applies the descriptor to the proper channel using proper filters. */
    public abstract int[] apply(BufferedImage image, ChannelCache channelCache, ScaleSpaceCache scaleSpaceCache, FilterCache filterCache);
    
     /** Creates all the samples of the scale-space in the scale-space cache. */
    public abstract void createRequiredScales(ScaleSpaceCache scaleSpaceCache); 

    /** Creates a descriptor characterized by type. */
    public static Descriptor createDescriptor(DescriptorType descriptorType, double scale, int bins){
        switch (descriptorType) {
	case DT_L:
		return new LDescriptor(scale, bins);
	case DT_Lx:
		return new LxDescriptor(scale, bins);
	case DT_Lxx:
		return new LxxDescriptor(scale, bins);
	case DT_Ly:
		return new LyDescriptor(scale, bins);
	case DT_Lyy:
		return new LyyDescriptor(scale, bins);
	case DT_Lxy:
		return new LxyDescriptor(scale, bins);

	default:
		return null;
	}
    }

    /** Returns min. */
    public int getMin() {
        return min;
    }

    /** Returns max. */
    public int getMax() {
        return max;
    }

    /** Returns bins. */
    public int getBins() {
        return bins;
    }

};




