/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

import Utils.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Danna
 */
enum ChannelType {

    CT_UNKNOWN, CT_L, CT_C1, CT_C2
}

/**
 * Class storing a cache of channels.
 */
public class ChannelCache {

    /** Pointer to the input image. */
    BufferedImage image;
    /** List storing pointers to channels. */
    public ArrayList<BufferedImage> channelList;
    /** List of types of channels in the _channelList. */
    
    ArrayList<ChannelType> channelTypeList;

    /** Default constructor. */
    public ChannelCache(BufferedImage image) {
        this.image = image;
    }

    /** Creates a new channel on the basis of the input image.
    If an identical channel already exists a new one will
    not be created. */
    public void createChannel(ChannelType channelType) {
        // Check if we don't have the channel in the cache
        if (channelTypeList != null) {
            for (int i = 0; i < channelTypeList.size(); ++i) {
                if (channelTypeList.get(i) == channelType) {
                    return;
                }
            }
        } else {
            channelTypeList = new ArrayList<ChannelType>();
            channelList = new ArrayList<BufferedImage>();
        }

        // Create a new channel
        BufferedImage channel = Image.getIntensitityChannelL(this.image);

        if (channel != null) {
            channelList.add(channel);
            channelTypeList.add(channelType);
        }
    }

    /** Returns a pointer to a matrix containing pixels of the channel. */
    public BufferedImage getChannel(ChannelType channelType) {
        // Find the channel
        for (int i = 0; i < channelTypeList.size(); ++i) {
            if (channelTypeList.get(i) == channelType) {
                return channelList.get(i);
            }
        }

        return null;
    }
}
