/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package SVM;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import sun.misc.Compare;
import sun.misc.Sort;

public class SvmPrediction {

//    static {
//        try {
//            stringLabels = (HashMap<String, Integer>) Serialization.deserialize("concepts.ser");
//        } catch (Exception ex) {
//            Logger.getLogger(SvmPrediction.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
//    static HashMap<String, Integer> stringLabels;
//    private int[] labels;
//    private double[] probabilityEstimates;
    private HashMap<Integer, Double> estimates = new HashMap<Integer, Double>();
    private int predicted;

    public SvmPrediction(int[] labels, double[] probabilityEstimates) {
        for (int i = 0; i < labels.length; i++) {
            estimates.put(labels[i], probabilityEstimates[i]);
        }
    }
    
     public SvmPrediction(int v) {
        predicted = v;
    }
    
//    
//    public double getProbabilityEstimate(int label) {
//        //if (estimates.containsKey(label))
//            return estimates.get(label);
//    }
    
    public ArrayList<Entry<String, Double>> getSortedEstimates(HashMap<String, Integer> stringLabels) {

        ArrayList<Entry<String, Double>> result = new ArrayList<Entry<String, Double>>();
        Set<Entry<Integer, Double>> entries = getEstimates().entrySet();
        Object[] arrayEntries = entries.toArray();

        Compare compare = new Compare() {

            @Override
            public int doCompare(Object o, Object o1) {
                Entry<Integer, Double> entry1 = (Entry<Integer, Double>) o;
                Entry<Integer, Double> entry2 = (Entry<Integer, Double>) o1;
                if (entry1.getValue() == entry2.getValue()) {
                    return 0;
                }
                if (entry1.getValue() > entry2.getValue()) {
                    return -1;
                }
                return 1;
            }
        };
       
        Sort.quicksort(arrayEntries, compare);

        for (int i = 0; i < arrayEntries.length; i++) {
            Entry<Integer, Double> entry = (Entry<Integer, Double>) arrayEntries[i];
            String key = "";

            for (Entry<String, Integer> e : stringLabels.entrySet()) {
                if (e.getValue().equals(entry.getKey())) {
                    key = e.getKey();
                    break;
                }
            }

            Entry<String, Double> r = new SimpleEntry<String, Double>(key, entry.getValue());
            result.add(r);
        }

        return result;
    }

    /**
     * @return the estimates
     */
    public HashMap<Integer, Double> getEstimates() {
        return estimates;
    }
    
    public int getPredicted() {
        return predicted;
    }
}
