/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Danna
 */
public class ScaleSpaceCache {
    
    /** Pointer to the channel cache. */
  private ChannelCache _channelCache;

  /** Pointer to the filter cache. */
  private FilterCache _filterCache;

  /** List storing information about samples. */
  private ArrayList<ScaleSpaceSampleInfo> _scaleSpaceSamplesList; 
  
    public ScaleSpaceCache(ChannelCache channelCache, FilterCache filterCache) {
        this._channelCache = channelCache;
        this._filterCache = filterCache;
        _scaleSpaceSamplesList = new ArrayList<ScaleSpaceSampleInfo>();
    }
  
  public void createScaleSpaceSample(ChannelType channelType, double scale)
{
  // Check if we don't have the sample in the cache
  for (int i=0; i<_scaleSpaceSamplesList.size(); ++i)
    if ((_scaleSpaceSamplesList.get(i).channelType == channelType) &&
        (_scaleSpaceSamplesList.get(i).scale == scale))
      return;

  // Create a new sample
  ScaleSpaceSampleInfo sssi = new ScaleSpaceSampleInfo();
  sssi.channelType = channelType;
  sssi.scale = scale;

  GaussianFilterInfo gfi = new GaussianFilterInfo(scale);
  sssi.inputImage = _filterCache.applyFilter(gfi, _channelCache.getChannel(channelType));

  // Append sample to the list
  _scaleSpaceSamplesList.add(sssi);
}
  
 public BufferedImage getScaleSpaceSample(ChannelType channelType, double scale)
{
  for (int i=0; i<_scaleSpaceSamplesList.size(); ++i)
    if ((_scaleSpaceSamplesList.get(i).channelType == channelType) &&
        (_scaleSpaceSamplesList.get(i).scale == scale))
      return _scaleSpaceSamplesList.get(i).inputImage;

  return null;
}
    
}

