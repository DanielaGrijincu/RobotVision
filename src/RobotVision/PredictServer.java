/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package RobotVision;

import Exceptions.EmptyDirectoryException;
import Exceptions.SvmInvalidOperationException;
import Utils.Annotations;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;

/**
 *
 * @author Danna
 */
public class PredictServer implements Runnable {

    private ServerSocket serverSocket;
    private int PORT = 9998;
    private BufferedReader inFromClient = null;
    private DataOutputStream outToClient = null;
    private PredictAlgorithm predictAlgorithm;
    private static boolean loadedModels = false;

    @Override
    public void run() {
        try {
            
            serverSocket = new ServerSocket(PORT);

            System.out.println("Server started at " + serverSocket.getInetAddress().getCanonicalHostName() + " PORT " + PORT);

            predictAlgorithm = new PredictAlgorithm(new ASIFTTest(), new CRFHTest(), new LSDTest(), new NARFTest());

            // load models from different thead
//            new Thread(new Runnable() {
//
//                @Override
//                public void run() {
//                    try {
//
//                        predictAlgorithm.loadModels();
//                        loadedModels = true;
//
//                    } catch (Exception ex) {
//                        System.out.println("Loading models. Exception occurred: " + ex.toString());
//                    }
//                }
//            }).start();

            while (true) {

                Socket clientSocket = serverSocket.accept();

                System.out.println("Client accepted: " + clientSocket.getInetAddress().getCanonicalHostName());

                inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outToClient = new DataOutputStream(clientSocket.getOutputStream());

                startListeningFromClient();

                try {
                    clientSocket.close();
                    System.out.println("Closed connection with client.");
                } catch (Exception ex) {
                    System.out.println("Exception occurred: " + ex.toString());
                }

            }
        } catch (IOException ex) {
            System.out.println("Exception occurred: " + ex.toString());
        } catch (Exception ex) {
            System.out.println("Exception occurred: " + ex.toString());
        }

    }

    private void startListeningFromClient() throws IOException, Exception{

            // protocol for prediction: "predict " + imageSet + " " + imageName
            String imageToPredict = inFromClient.readLine();

            System.out.println("Received request: " + imageToPredict);

            if (imageToPredict.substring(0, imageToPredict.indexOf(" ")).equals("predict")) {

                String[] s = imageToPredict.split(" ");

                String imagePathjpg = predictAlgorithm.pathToAllImages + s[1] + "\\std_cam\\" + s[2];
                String imagePathasift = predictAlgorithm.pathToAllImages + s[1] + "\\asift_features\\" + s[2].substring(0, s[2].lastIndexOf('.')) + ".asift";

                System.out.println("ImagePathJpg: " + imagePathjpg);
                System.out.println("ImagePathAsift: " + imagePathasift);

                predictAlgorithm.setPathToImageToPredictJpgFile(imagePathjpg);
                predictAlgorithm.setPathToImageToPredictAsiftFile(imagePathasift);

                if (!loadedModels) {
                    
                    String answer = "Prediction models are not finished loading...\n";
                    outToClient.writeBytes(answer);
                    outToClient.flush();
                    System.out.println("Sent answer: " + answer);
                    
                }else{
                    
                    SimpleEntry<Integer, Double> prediction = null;
                    
                    prediction = predictAlgorithm.predict();
                      
                      //protocol for prediction answer
                      //predicted ElevatorArea 0.56 imageset\imagename
                      
                      String concept = "Unknown";
                      if (prediction.getKey() != -1)
                          concept = Annotations.getAnnotation(prediction.getKey());
                      
                      String answer = "predicted" + " " + concept + " " + prediction.getValue() + " " + "\\" + "\n";
                      outToClient.writeBytes(answer);
                      outToClient.flush();
                      System.out.println("Sent answer: " + answer);
                      
            }
        }
    }
}
