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
public class ScaleSpaceSampleInfo
{
  public ChannelType channelType;
  public double scale;
  public BufferedImage inputImage;
  
  public ScaleSpaceSampleInfo(ChannelType c, double s, BufferedImage m)
  {
      this.channelType = c;
      this.scale = s;
      this.inputImage = m;
  }
  
   public ScaleSpaceSampleInfo()
  {
  }
  
}

