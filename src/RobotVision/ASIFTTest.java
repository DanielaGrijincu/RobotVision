/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import ASIFT.ASIFTDescriptor;
import Exceptions.EmptyDirectoryException;
import Exceptions.SvmInvalidParameterException;
import Kernels.ASIFTKernel;
import SVM.SvmTrain;
import Utils.Annotations;
import Utils.Evaluation;
import Utils.Image;
import Utils.Serialization;
import java.io.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import libsvm.svm_model;
import libsvm.svm_print_interface;
import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FileUtils;

/**
 *
 * @author Danna
 */
public class ASIFTTest {

    private svm_model svmModel;
    public static int label = -1;
    
    public static svm_print_interface print = new svm_print_interface() {

        @Override
        public void print(String string) {

            System.out.print(string);
        }
    };

    public void transformImagesJpgToPng(String jpgImagesFolder, String pngImagesFolder) {

        File dir = new File(jpgImagesFolder);
        String[] children = dir.list();

        if (children != null) {

            for (int i = 0; i < children.length; i++) {

                String filename = children[i];

                if (filename.endsWith("jpg") || filename.endsWith("jpeg")) {
                    try {

                        String name = filename.subSequence(0, filename.indexOf('.')).toString();
                        Image.saveAsPGM(ImageIO.read(new File(jpgImagesFolder + filename)), pngImagesFolder + name + ".pgm");
                        //Image.saveAsPNG(ImageIO.read(new File(jpgImagesFolder + filename)), pngImagesFolder + name + ".png");

                    } catch (IOException ex) {
                        ex.getStackTrace();
                        System.out.println(ex.toString());
                    }
                }
            }
        }
    }

    public void computeASIFTFeatures(String pathToInputPNGImages, String pathWhereToSaveFeatures, String pathToPreprocessedImagesFile) throws IOException {


        System.out.println("Started ASIFT feature extraction.");

        DataInputStream in = null;

        if ("".equals(pathToPreprocessedImagesFile)) {

            File dir = new File(pathToInputPNGImages);
            String[] children = dir.list();

            if (children != null) {

                for (int i = 0; i < children.length; i++) {

                    String filename = children[i];

                    if (filename.endsWith("png")) {

                        String name = filename.subSequence(0, filename.indexOf('.')).toString();
                        File asiftFile = new File(pathWhereToSaveFeatures + name + ".asift");

                        if (!asiftFile.exists()) {

                            long millis = System.currentTimeMillis();

                            ASIFTDescriptor.getDescriptor(pathToInputPNGImages + filename, pathWhereToSaveFeatures + name + ".asift");

                            System.out.println(i + ". " + "Computed asift features for " + filename + " in " + (System.currentTimeMillis() - millis) / 1000 + " seconds.");

                            ++i;
                        }
                    }
                }
            }
        } else {
            FileInputStream fstream = new FileInputStream(pathToPreprocessedImagesFile);
            in = new DataInputStream(fstream);

            final BufferedReader br = new BufferedReader(new InputStreamReader(in));

            int i = 0;
            String strLine;
            while ((strLine = br.readLine()) != null) {

                String filename = strLine.substring(0, strLine.indexOf('.')) + ".png";
                File file = new File(pathToInputPNGImages + filename);
                if (file.exists()) {

                    long millis = System.currentTimeMillis();

                    File asiftFile = new File(pathWhereToSaveFeatures + filename.substring(0, strLine.indexOf('.')) + ".asift");

                    if (!asiftFile.exists()) {

                        ASIFTDescriptor.getDescriptor(pathToInputPNGImages + filename, pathWhereToSaveFeatures + filename.substring(0, strLine.indexOf('.')) + ".asift");

                        System.out.println(i + ". " + "Computed asift features for " + filename + " in " + (System.currentTimeMillis() - millis) / 1000 + " seconds.");

                        ++i;
                    }
                }
            }
        }

    }

    public static ArrayList<SimpleImmutableEntry<String, Integer>> getImageLocationMap(String pathToImagesFolder, String pathToAnnotationsFile, String pathToImagesLocations) throws IOException {

        ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles = new ArrayList<SimpleImmutableEntry<String, Integer>>();

        DataInputStream in = null;

        FileInputStream fstream = new FileInputStream(pathToImagesLocations);
        in = new DataInputStream(fstream);

        final BufferedReader br = new BufferedReader(new InputStreamReader(in));

        String strLine;
        while ((strLine = br.readLine()) != null) {

            String concept = strLine.substring(strLine.indexOf(':') + 2);

            //get map of a specific Label (Corridor, Toilet, etc.)
            if (label != -1) {

                if (Annotations.getAnnotation(concept) == label) {

                    String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();
                    filename = strLine.substring(0, filename.indexOf("."));

                    File file = new File(pathToImagesFolder + filename + ".asift");
                    if (file.exists()) {

                        allFeaturesFiles.add(new SimpleImmutableEntry<String, Integer>(pathToImagesFolder + filename + ".asift", Annotations.getAnnotation(concept)));

                    }
                }
            } // map all images to their location
            else {

                String filename = strLine.subSequence(0, strLine.indexOf(':')).toString();
                filename = strLine.substring(0, filename.indexOf("."));

                File file = new File(pathToImagesFolder + filename + ".asift");
                if (file.exists()) {

                    allFeaturesFiles.add(new SimpleImmutableEntry<String, Integer>(pathToImagesFolder + filename + ".asift", Annotations.getAnnotation(concept)));
                }

            }

        }

        return allFeaturesFiles;

    }

    public void train(String pathToFeaturesFolder, String pathToAnnotationsFile, String pathToImagesLocations, String pathWhereToSaveModel) throws IOException, SvmInvalidParameterException {



        ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles = ASIFTTest.getImageLocationMap(pathToFeaturesFolder, pathToAnnotationsFile, pathToImagesLocations);

        SvmTrain svmTrain = new SvmTrain(print);
        svm_model myModel = svmTrain.trainASIFT(allFeaturesFiles, new ASIFTKernel());

        Serialization.serialize(myModel, pathWhereToSaveModel);

    }
    private boolean stop;

    public synchronized void setStop(boolean newStop) {
        stop = newStop;
    }

    public synchronized boolean getStop() {
        return stop;
    }

    public SimpleEntry<Integer, Double> predict(String pathToImagesToPredictDir, final String pathToRepresentativeImagesDir, final String pathToAllFeaturesDir, String pathToAnnotationsFile, final String pathWhereToSavePredictions) throws IOException, EmptyDirectoryException {

        final TreeMap<Integer, String> myPredictions = new TreeMap<Integer, String>();
        final float threshold = (float) 0.99;

        if (!"".equals(pathToAnnotationsFile)) //load annotations
        {
            Annotations.loadAnnotations(pathToAnnotationsFile);
        }

        // load images to predict
        File dir2 = new File(pathToImagesToPredictDir);
        String[] imagesToPredict = dir2.list();
        
        if (imagesToPredict == null) {
            //predict only one image
            imagesToPredict = new String[1];
            imagesToPredict[0] = pathToImagesToPredictDir.substring(pathToImagesToPredictDir.lastIndexOf("\\") + 1);
            pathToImagesToPredictDir = pathToImagesToPredictDir.substring(0, pathToImagesToPredictDir.lastIndexOf("\\") + 1);
            //throw new EmptyDirectoryException("There are no images to predict!");
        }

        // load repr images
        File dir3 = new File(pathToRepresentativeImagesDir);
        final String[] training_sets = dir3.list();
        if (training_sets == null) {
            throw new EmptyDirectoryException("There are no representative images!");
        }


        // go through all the images
        for (int i = 0; i < imagesToPredict.length; i++) {

            //get the current image to predict (must be .asift)
            final String curr_image;
            if (imagesToPredict[i].endsWith(".asift")) {
                curr_image = pathToImagesToPredictDir + imagesToPredict[i];

            } else {
                continue;
            }

            //get the image number
            final int nr_image = Integer.parseInt(curr_image.substring(curr_image.lastIndexOf('_') + 1, curr_image.lastIndexOf('.')));

            //max predicted probabilities for each concept
            final float[] predictedProbabilities = new float[Annotations.count()];

            BlockingQueue<Runnable> workqueue = new LinkedBlockingQueue<Runnable>();
            int workingThreads = Runtime.getRuntime().availableProcessors();
            final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(workingThreads, workingThreads, 100, TimeUnit.SECONDS, workqueue);

            final Collection<Future<?>> futures = new LinkedList<Future<?>>();

            System.out.println("Predicting for image:" + imagesToPredict[i]);

            setStop(false);
            long start = System.currentTimeMillis();

            for (int ii = 0; ii < Annotations.count(); ii++) {

                final String current_concept = Annotations.getAnnotation(ii);

                // Thread start
                Runnable task = new Runnable() {

                    public synchronized void setPrediction(int concept, float value) {

                        predictedProbabilities[concept] = value;
                        //stopOtherThread
                        //put value in myPredictions
                        if (value >= threshold) {
                            myPredictions.put(nr_image, Annotations.getAnnotation(concept));
                            setStop(true);
                        }
                    }

                    @Override
                    public void run() {

                        System.out.println("Comparing with images from concept: " + current_concept);

                        float max = 0; // keep track of the best predicted probability

                        for (int ts = 0; ts < training_sets.length; ts++) {

                            String current_training_set = training_sets[ts];

                            // simultaneously compare the image with images from every concept
                            File conceptDir = new File(pathToRepresentativeImagesDir + current_training_set + "\\" + current_concept + "\\"); //get the first concept

                            if (conceptDir.exists()) {
                                if (getStop()) {
                                    System.out.println("Searching for " + current_concept + " stopped.");
                                    return;
                                }

                                String[] repr_images = conceptDir.list(); // get all representative images for this concept

                                if (repr_images != null) {

                                    for (int j = 0; j < repr_images.length; j++) {

                                        //get the current representative image to compare to
                                        String curr_repr_image = repr_images[j];

                                        curr_repr_image = pathToAllFeaturesDir + current_training_set + "\\asift_features\\" + curr_repr_image.substring(0, curr_repr_image.lastIndexOf('.')) + ".asift";

                                        File file = new File(curr_repr_image);

                                        if (file.exists()) {
                                            if (getStop()) {
                                                System.out.println("Searching for " + current_concept + " stopped.");
                                                return;
                                            }

                                            float prediction = ASIFTDescriptor.compareDescriptors(curr_image, curr_repr_image);

                                            if (prediction > threshold) {
                                                max = prediction;
                                                break;
                                            }

                                            if (prediction > max) {
                                                max = prediction;
                                            }

                                        }
                                    }

                                }
                            }
                        } // done comparing with current_concept from every training set

                        setPrediction(Annotations.getAnnotation(current_concept), max);
                        //predictedProbabilities[Annotations.getAnnotation(current_concept)] = max;
                    }
                };

                futures.add(threadPool.submit(task));
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (        InterruptedException | ExecutionException ex) {
                    setStop(true);
                }
            }


            threadPool.shutdownNow();
            //finished comparing with all representative images. Now get the concept with max probability
            int concept = 0;
            if (!myPredictions.containsKey(nr_image)) {
                float max = 0;
                for (int j = 0; j < predictedProbabilities.length; j++) {
                    if (predictedProbabilities[j] > max) {
                        max = predictedProbabilities[j];
                        concept = j;
                    }
                }

                myPredictions.put(nr_image, Annotations.getAnnotation(concept));
            }
                        
            System.out.println("Predicted image " + nr_image + " => " + myPredictions.get(nr_image) + "(" + predictedProbabilities[Annotations.getAnnotation(myPredictions.get(nr_image))] + ")" + " time:" + ((System.currentTimeMillis() - start) / 1000) + " sec.");

            if (imagesToPredict.length == 1)
                if (predictedProbabilities[Annotations.getAnnotation(myPredictions.get(nr_image))] < (float) 0.1)
                    return new SimpleEntry<>(-1, (double) 0);
                else
                    return new SimpleEntry<>(Annotations.getAnnotation(myPredictions.get(nr_image)), (double) predictedProbabilities[Annotations.getAnnotation(myPredictions.get(nr_image))]);
            
        }

        if (!"".equals(pathWhereToSavePredictions))
            Evaluation.saveResultsImageClefFormat(myPredictions, pathWhereToSavePredictions);
        
        return new SimpleEntry<>(-1, (double)0);

    }

    public void getRepresentativeImages(String originalImagesDir,
            String destinationImagesDir,
            String asiftFeaturesDir,
            String conceptsFile,
            String imagesLocationsFile,
            int currentLabel) {

        float threshold = (float) 0.7;


        try {

            Annotations.loadAnnotations(conceptsFile);

            ASIFTTest.label = currentLabel;

            ArrayList<SimpleImmutableEntry<String, Integer>> allFeaturesFiles = ASIFTTest.getImageLocationMap(asiftFeaturesDir, conceptsFile, imagesLocationsFile);

            if (allFeaturesFiles.isEmpty()) {
                return;
            }

            int lastSelected = 0;
            int next = lastSelected + 1;

            saveRepresentativeImage(originalImagesDir,
                    destinationImagesDir,
                    allFeaturesFiles.get(lastSelected).getKey(),
                    allFeaturesFiles.get(lastSelected).getValue());

            System.out.println("Last selected image is " + lastSelected);

            while (next < allFeaturesFiles.size()) {

                float match = ASIFTDescriptor.compareDescriptors(allFeaturesFiles.get(lastSelected).getKey(), allFeaturesFiles.get(next).getKey());

                if (match < threshold) {

                    saveRepresentativeImage(originalImagesDir,
                            destinationImagesDir,
                            allFeaturesFiles.get(next).getKey(),
                            allFeaturesFiles.get(next).getValue());

                    lastSelected = next;
                    ++next;

                    System.out.println("Last selected image is " + lastSelected);

                } else {
                    ++next;
                }

            }


        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.toString());
        } finally {
        }

    }

    public void saveRepresentativeImage(String originalImagesDir, String destinationImagesDir, String imageFile, int annotation) throws IOException {

        String filename = imageFile.subSequence(imageFile.lastIndexOf('\\') + 1, imageFile.lastIndexOf('.')).toString();
        filename = filename + ".jpg";

        File source = new File(originalImagesDir + filename);
        if (source.exists()) {
            File desc = new File(destinationImagesDir + Annotations.getAnnotation(annotation) + "\\" + filename);
            FileUtils.copyFile(source, desc);
        }

    }

    public void moveReprAsiftFeatures(String pathToReprDirs, String pathToAsiftFeatures, String pathToDestinationDir, String annotationsFile) throws IOException {

        Annotations.loadAnnotations(annotationsFile);

        for (int ii = 0; ii < Annotations.count(); ii++) {

            String current_concept = Annotations.getAnnotation(ii);

            File conceptDir = new File(pathToReprDirs + current_concept + "\\"); //get the first concept

            if (conceptDir.exists()) {

                String[] repr_images = conceptDir.list();

                float max = 0; // keep track of the best predicted probability

                if (repr_images != null) {

                    for (int j = 0; j < repr_images.length; j++) {

                        //get the current representative image to compare to
                        String curr_repr_image = repr_images[j];

                        curr_repr_image = pathToAsiftFeatures + curr_repr_image.substring(0, curr_repr_image.indexOf(".")) + ".asift";

                        File file = new File(curr_repr_image);
                        if (file.exists()) {

                            File desc = new File(pathToDestinationDir + curr_repr_image.substring(curr_repr_image.lastIndexOf("\\")));
//                            FileUtils.copyFile(file, desc);
                        }



                    }
                }

            } // done comparing with current_concept 


        }
    }
}