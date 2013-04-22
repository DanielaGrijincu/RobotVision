/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package CRFH;

import Utils.Serialization;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import libsvm.svm_node;

/**
 *
 * @author Danna
 */
public class CRFH {

    private ArrayList<Descriptor> descriptorList;
    private TreeMap<Integer, Float> histogram;
    private float sum;
    private float max;
    private int nrscales;
    private int bins;

    public CRFH(ArrayList<Descriptor> descriptorList, int nrs, int bins) {

        this.descriptorList = descriptorList;
        this.nrscales = nrs;
        this.bins = bins;
        histogram = new TreeMap<Integer, Float>() {
        };

    }

    public svm_node[] computeHistogram(BufferedImage image) {

        ChannelCache channelCache = new ChannelCache(image);
        FilterCache filterCache = new FilterCache();
        
        
        //Create all required filters
        for (int i = 0; i < descriptorList.size(); i++) {
            descriptorList.get(i).createRequiredFilters(filterCache);
        }

        //Create all required channels
        for (int i = 0; i < descriptorList.size(); i++) {
            descriptorList.get(i).createRequiredChannels(channelCache);
        }
        
        ScaleSpaceCache scaleSpaceCache = new ScaleSpaceCache(channelCache, filterCache);
        //Create all required channels
        for (int i = 0; i < descriptorList.size(); i++) {
            descriptorList.get(i).createRequiredScales(scaleSpaceCache);
        }

        ArrayList<int[]> descriptorOutputs = new ArrayList<int[]>();

        for (int i = 0; i < descriptorList.size(); i++) {
            descriptorOutputs.add(descriptorList.get(i).apply(image, channelCache, scaleSpaceCache, filterCache));
        }

        // Check whether the descriptor list matches the output list
        if (descriptorOutputs.size() != descriptorList.size()) {
            System.out.println("ERROR: The size of the descriptor list does not match the size of the outputs list.");
            return null;
        }

        // Read all the necessary information and store it in the vectors
        int ndims = descriptorOutputs.size();
        ArrayList<int[]> dataVect = new ArrayList<int[]>();
        double[] minVect = new double[ndims];// = new ArrayList<int>();
        double[] maxVect = new double[ndims];// = new ArrayList<int>();
        int[] binsVect = new int[ndims];// = new ArrayList<int>();
 
        this.histogram = new TreeMap<Integer, Float>();

        int length = descriptorOutputs.get(0).length;
        
        for (int i = 0; i < ndims; ++i) {
            dataVect.add(descriptorOutputs.get(i));

            if (length != descriptorOutputs.get(i).length) {
                System.out.println("ERROR: the filter outputs have different dimensions.");
            }

            binsVect[i] = descriptorList.get(i).getBins(); //(float) Math.ceil(Math.sqrt(descriptorOutputs.get(i).length));
            minVect[i] = descriptorList.get(i).getMin();
            maxVect[i] = descriptorList.get(i).getMax();
        }

        // Calculate scaling factors
        double[] factorsVect = new double[ndims];
        for (int i = 0; i < ndims; ++i) {
            factorsVect[i] = (double) ((binsVect[i] - 2.22045e-016) / (maxVect[i] - minVect[i])); 
            // numeric_limits<double>::epsilon()
            // The difference between 1 and the smallest value greater than 1
            // for double objects is: 2.22045e-016
        }
        max = -1;
        
        for (int i = 0; i < length; i++) {

            // Number of bin for dimension k: 
           int k = ndims - 1;
           int index = (int) ((dataVect.get(k)[i] - minVect[k]) * factorsVect[k]);
            for (k = ndims - 2; k >= 0; --k) {
                
       //         index = Math.round((float) ((dataVect.get(k)[i] - minVect[k]) * factorsVect[k]));
                index = index * binsVect[k] + (int) ((dataVect.get(k)[i] - minVect[k]) * factorsVect[k]);
              //  index = index * binsVect[k] + (int) ((dataVect.get(k)[i] - minVect[k]) * factorsVect[k]); // (maxVect[k] - minVect[k]) * binsVect[k] + 1 / 2));
//            }
                
               Map.Entry<Integer, Float> myentry = histogram.ceilingEntry(index);

               if ((myentry != null) && (myentry.getKey() == index))
               {
                       float new_value = myentry.getValue() + 1;
                        if (new_value > max) {
                            max = new_value;
                        }
                        if (myentry.getKey() > 0)
                            histogram.put(myentry.getKey(), new_value);
                      // myentry.setValue(myentry.getValue() + 1);
                        
                    } else {
                   if (index > 0)
                        histogram.put(index, (float)1);
                    }
            }
        }

        int width = image.getWidth();
        int height = image.getHeight();
        int skipBorder = 15;
        this.sum = (height - 2 * skipBorder) * (width - 2 * skipBorder);

       normalizeHistogram();
       
       //serializeHistogram();
        
       return  getLibSvmVector();
        
        //float[] aux = new float[histogram.size()];
       // float[] result = new float[100];//this.nrscales * this.bins + 1];
//        int i = 0;
//        for (Map.Entry<Integer, Float> entry : histogram.entrySet()) {
//            if (i < result.length)
//                 result[i++] = entry.getValue();
//            else
//            {
//                System.out.println("WARNING: Computed a larger than expected histogram: size " + i);
//                break;
//            }
//
//
//            //aux[i++] = entry.getValue();
//        }
        
        
//        for (i = 0; i < result.length; i++)
//        {
//            if (i < histogram.size())
//                result[i] = aux[i];
//            else 
//                result[i] = 0;
//        }
        
       // return result;

    }
        
    public void normalizeHistogram() {

        for (Map.Entry<Integer, Float> entry : this.histogram.entrySet()) {
            entry.setValue(entry.getValue() / sum);
        }
    }
    
    public svm_node[] getLibSvmVector() {

	int nr = 0;
        svm_node[] x = new svm_node[this.histogram.size()];
        for (Map.Entry<Integer, Float> entry : this.histogram.entrySet()) {
                x[nr] = new svm_node();
                x[nr].index = entry.getKey();
                x[nr].value = entry.getValue();
                ++nr;
        }
        
        return x;
    }

    public void filterHistogram(float min_val) {

        double tmp = min_val;//*sum;

        for (Iterator<Map.Entry<Integer, Float>> i = this.histogram.entrySet().iterator(); i.hasNext();) {
            Map.Entry<Integer, Float> entry = i.next();
            if (entry.getValue() / max < tmp) {
                i.remove();
            }
        }
    }

    public void serializeHistogram() {

        try {
            // Create file
            FileWriter fstream = new FileWriter("output CRFH/featuresTest.crfh");
            BufferedWriter out = new BufferedWriter(fstream);
            for (Map.Entry<Integer, Float> entry : histogram.entrySet()) {

                out.write(entry.getKey() + ":" + entry.getValue() + "  ");

            }
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }


    }
}
