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
public abstract class Filter {

    static final int GAUSSIAN_SIGMAS = 3;
    FilterInfo filterInfo;

    /** Constructor. */
    Filter(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
    }

    /** Applies the filter to the input matrix. */
    public abstract BufferedImage apply(BufferedImage planarImage);

    /** Returns the filter info. */
    public FilterInfo getFilterInfo() {
        return filterInfo;
    }

    /** Creates a filter of a given type. */
    public static Filter createFilter(FilterInfo fi) {

        switch (fi.getFilterType()) {
            case FT_GAUSSIAN:
                GaussianFilterInfo info = (GaussianFilterInfo) fi;
                return new GaussianFilter(info.getSigma());
            case FT_CARTESIAN:
                CartesianFilterInfo info2 = (CartesianFilterInfo) fi;
                return new CartesianFilter(info2.getDx(), info2.getDy());

        }

        return null;

    }

};
