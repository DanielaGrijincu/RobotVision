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
public class FilterCache {

    public ArrayList<Filter> filterList;

    public boolean createFilter(FilterInfo filterInfo) {

        // Check if we don't have an identical filter in the cache
        if (filterList != null)
            for (int i = 0; i < this.filterList.size(); ++i) {
                if (filterList.get(i).getFilterInfo() == filterInfo) {
                    return true;
                }
            }
        else
        {
            filterList = new ArrayList<Filter>();
        }


	// Create a new filter
	Filter filter = Filter.createFilter(filterInfo);
	if (filter != null) {
		filterList.add(filter);
		return true;
        }
	 else
		return false;
    }

public BufferedImage applyFilter(FilterInfo filterInfo, BufferedImage image){

        BufferedImage result = null;
	/* Find the filter */
	Filter filter = null;
	for (int i = 0; i < filterList.size() && filter == null; ++i) {
		if (filterList.get(i).getFilterInfo().filterType == filterInfo.filterType)
                    if (filterList.get(i).getFilterInfo().compareTo(filterInfo) == 0)
                                filter = filterList.get(i);
	}

	/* Filter not found? */
	if (filter == null) {
		System.out.println("filter not found!");
		return null;
	}
	else {
		//System.out.println("Filter found " + filter.getFilterInfo().getFilterType().toString());
	}

	/* Apply the filter */
	result = filter.apply(image);

	return result;
}

}
