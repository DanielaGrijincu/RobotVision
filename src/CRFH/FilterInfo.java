/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

/**
 *
 * @author Danna
 */

enum FilterType {

    FT_UNKNOWN, FT_GAUSSIAN, FT_CARTESIAN
};

public class FilterInfo implements Comparable {

    /** Filter type. */
    public FilterType filterType;

    /** Constructor. */
    public FilterInfo(FilterType filterType) {
        this.filterType = filterType;
    }

    /** Returns filter type. */
    public FilterType getFilterType() {
        return filterType;
    }

    public int compareTo(Object o) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}


class GaussianFilterInfo extends FilterInfo implements Comparable{

    public double sigma;

    /** Constructor. */
    GaussianFilterInfo(double sigma2) {

        super(FilterType.FT_GAUSSIAN);
        this.sigma = sigma2;
    }

          /** Comparison operator. */
    @Override
    public int compareTo(Object o) {

        GaussianFilterInfo g = (GaussianFilterInfo) o;
        if (g.sigma == this.sigma) {
            return 0;
        } else //   if (g.sigma > this.sigma)
        {
            return -1;
        }
        //return 1;
    }

    /** Returns sigma^2. */
    public double getSigma() {
        return sigma;
    }
};

/**
 * Cartesian filter info
 */
class CartesianFilterInfo extends FilterInfo {

    int dx;
    int dy;

    /** Constructor. */
    CartesianFilterInfo(int dx, int dy) {
        super(FilterType.FT_CARTESIAN);
        this.dx = dx;
        this.dy = dy;
    }

    /** Comparison operator. */
    @Override
    public int compareTo(Object o) {

        CartesianFilterInfo c = (CartesianFilterInfo) o;
        if (c.dx == this.dx && c.dy == this.dy) {
            return 0;
        } else {
            return -1;
        }
    }

    /** Returns the order of differentiation in the x axis. */
    public int getDx() {
        return dx;
    }

    /** Returns the order of differentiation in the y axis. */
    public int getDy() {
        return dy;
    }
};
