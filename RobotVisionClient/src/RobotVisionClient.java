/*
 * To change this template, choose Tools | Templates
 * and open the template inFromServer the editor.
 */
package robotvisionclient;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.FutureTask;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.swing.JOptionPane;

/**
 *
 * @author Danna
 */
public class RobotVisionClient extends Application {
    
    public static int PORT;
    public static String ADDR;
    public static Socket servSocket;
    public static DataOutputStream outToServer = null;
    public static BufferedReader inFromServer = null;
    public static boolean conn_details = false; 

    private double initX;
    private double initY;
    private String defaultLocationWhereToLoadFiles = "D:\\Dana\\FACULTATE\\LICENTA\\Application\\IMAGE_SETS\\test1\\std_cam\\";
    private String pathToAnnotationsFile = "trainingConcepts.txt";
    static Stage myStage;
    static DisplayShelf myDisplayShelf;
    
    private static HashMap<String, Image> images_map;
    private static HashMap<String, Image> add_images_map;;
    
    static String styledToolBarCss = "Resources/Styles.css";
    
    private static final double WIDTH = 835, HEIGHT = 370;
    
    public static PerspectiveImage currentCenterImage;
    
    public static HashMap<String, Text> myTexts;
    
    public static Label predictionStatus;
    public static Label status;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(final Stage primaryStage) throws FileNotFoundException, IOException {
       
        myStage = primaryStage;
        
        primaryStage.initStyle(StageStyle.TRANSPARENT); // here it is

         // load images
        images_map = new HashMap<String, Image>();
        
        //get random 10 images from default location
//        File dir = new File(defaultLocationWhereToLoadFiles);
//        String[] children = dir.list();
//        if (children != null){
//            for (int i = 0; i < 10; i++){
//            Random r = new Random();
//            String imagename = children[r.nextInt(children.length)];
//            images_map.put(defaultLocationWhereToLoadFiles + imagename, new Image(defaultLocationWhereToLoadFiles + imagename));
//            }
//        }
        
        Group imageGroup = new Group();
        // create display shelf
    //    myDisplayShelf = new DisplayShelf(images_map);
    //    myDisplayShelf.setPrefSize(WIDTH-3, HEIGHT);
    //    imageGroup.getChildren().add(myDisplayShelf);
        
        Pane rootBorderPane = new Pane();
        rootBorderPane.setStyle("-fx-background-color: #E0E0E0; -fx-border-width: 2; -fx-border-color: gray; -fx-border-style: solid;");
        
        predictionStatus = new Label("Prediction status: ");
        predictionStatus.setTextFill(Color.DARKGREY);
        predictionStatus.setStyle("-fx-effect: dropshadow(one-pass-box , white, 0, 1 , 0, 1); -fx-padding: 6px; -fx-font-size: 1.1em;");
        status = new Label("none");
        status.setTextFill(Color.BROWN);
        status.setStyle("-fx-effect: dropshadow(one-pass-box , white, 0, 1 , 0, 1); -fx-padding: 6px; -fx-font-size: 1.1em;");
       
        GridPane gridlabel = new GridPane();
        gridlabel.setPadding(new Insets(0,0,0,20));
        gridlabel.add(predictionStatus, 0, 0);
        gridlabel.add(status, 1, 0);
        
        
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(3);
        grid.setPadding(new Insets(0, 4, 0, 2));
        grid.add(new ToolButtons(myStage, WIDTH), 0, 0);
        grid.add(new ToolBarGroup(), 0, 1);
        grid.add(new PredictionGroup(pathToAnnotationsFile),0, 3);
        grid.add(gridlabel, 0, 4);
        grid.add(imageGroup, 0, 5); 
        grid.setGridLinesVisible(false);
        
        rootBorderPane.getChildren().add(grid);
        
        Scene myScene = new Scene(rootBorderPane, WIDTH, 638);
        
        myScene.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                initX = me.getScreenX() - primaryStage.getX();
                initY = me.getScreenY() - primaryStage.getY();
            }
        });

        //when screen is dragged, translate it accordingly
        myScene.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                primaryStage.setX(me.getScreenX() - initX);
                primaryStage.setY(me.getScreenY() - initY);
            }
        });
        
        
        primaryStage.setScene(myScene);
        primaryStage.show();
        
    }
    
     public static class PredictionGroup extends Region{
        
        private Rectangle clip;
        
        private final GridPane grid;
        
        private static double ginitX;
        private static double ginitY;
        
        public PredictionGroup(String pathToAnnotationsFile) throws FileNotFoundException, IOException{

            clip = new Rectangle(0, 0, WIDTH, 150);
            setClip(clip);
          
            myTexts = getTextConcepts(pathToAnnotationsFile);
                 
//            Line line1 = new Line(0, 0, 150,0);
//            line1.setFill(null);
//            line1.setStroke(Color.GRAY);
//            line1.setStrokeWidth(1);
//            
//            Line line2 = new Line(0, 0, 0,150);
//            line2.setFill(null);
//            line2.setStroke(Color.GRAY);
//            line2.setStrokeWidth(1);
//            
            GridPane grid1 = new GridPane();
            grid1.setHgap(15);
            grid1.setVgap(5);
            grid1.add(getBorderGroup(myTexts.get("StudentOffice")), 0, 1, 1, 1);
            grid1.add(getBorderGroup(myTexts.get("ProfessorOffice")), 2, 0);
            grid1.add(getBorderGroup(myTexts.get("ElevatorArea")), 3, 1);
            grid1.add(getBorderGroup(myTexts.get("Toilet")), 4, 0);
            
            GridPane grid2 = new GridPane();
            grid2.setHgap(5);
            grid2.setVgap(5);
            grid2.setPadding(new Insets(0, 15, 0, 10));
            grid2.add(getBorderGroup(myTexts.get("PrinterRoom")), 0, 0);
            grid2.add(getBorderGroup(myTexts.get("Corridor")), 0, 2);
            
            
            GridPane grid3 = new GridPane();
            grid3.setHgap(5);
            grid3.setVgap(0);
            grid3.add(getBorderGroup(myTexts.get("LoungeArea")), 0, 0);
            grid3.add(getBorderGroup(myTexts.get("TechnicalRoom")), 2, 0);
            grid3.add(getBorderGroup(myTexts.get("VisioConference")), 5, 0);
            
            GridPane grid4 = new GridPane();
            grid4.setHgap(5);
            grid4.setVgap(0);
            grid4.setPadding(new Insets(20, 0, 0, 0));
            grid4.add(getBorderGroup(myTexts.get("Unknown")), 0, 1);
            
            grid = new GridPane();
            grid.setHgap(5);
            grid.setVgap(5);
            grid.setPadding(new Insets(5, 5, 0, 20));
            grid.setGridLinesVisible(false);
            
            grid.add(grid1, 2, 0);
            grid.add(grid2, 0, 0);
            grid.add(grid3, 2, 2);
            grid.add(grid4, 3, 0);
            getChildren().add(grid);
        }
        
        private Group getBorderGroup(Text t){
            
            final Group g = new Group();

            Rectangle rectangle = new Rectangle();
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setStrokeWidth(2);
            rectangle.setStroke(Color.WHITE);
            Bounds bounds = t.getBoundsInParent();
            double arcH = 3;
            double arcW = 3;
            double x = 0;
            double y = 0;
            t.setLayoutX(x);
            t.setLayoutY(y);
            double sWidth = 100;
            double sHeight = 100;
            x = sWidth / 2 - (bounds.getWidth() / 2);
            y = sHeight / 2 - (bounds.getHeight() / 2);
            t.setLayoutX(x);
            t.setLayoutY(y);
            bounds = t.getBoundsInParent();
            double baseLineOffset = t.getBaselineOffset();

            rectangle.setLayoutX(x - arcW);
            rectangle.setLayoutY(y - baseLineOffset - arcH);
            rectangle.setArcHeight(arcH);
            rectangle.setArcWidth(arcW);
            rectangle.setWidth(bounds.getWidth() + arcW * 2);
            rectangle.setHeight(bounds.getHeight() + arcH * 2);
            
            g.getChildren().addAll(rectangle, t);
            
            g.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {

                Text get = (Text) g.getChildren().get(1);
                if (get.getFill() == Color.RED)
                    get.setFill(Color.WHITE);
                me.consume();
            }
        });

            g.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                ginitX = me.getScreenX() - g.getLayoutX();
                ginitY = me.getScreenY() - g.getLayoutY();
                    
                Rectangle get = (Rectangle) g.getChildren().get(0);
                get.setStroke(Color.DARKGRAY);
                me.consume();
            }
        });
            
            g.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                Rectangle get = (Rectangle) g.getChildren().get(0);
                get.setStroke(Color.WHITE);
                me.consume();
            }
        });

        //when screen is dragged, translate it accordingly
        g.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent me) {
                
                g.setLayoutX(me.getScreenX() - ginitX);
                g.setLayoutY(me.getScreenY() - ginitY);
                me.consume();
                
            }
        });
        
            return g;
        }
        
        private HashMap<String, Text> getTextConcepts(String pathToAnnotationsFile) throws FileNotFoundException, IOException{
              
            HashMap<String, Text> texts = new HashMap<String, Text>() ;
            DataInputStream in = null;
            try {
                FileInputStream fstream = new FileInputStream(pathToAnnotationsFile);
                in = new DataInputStream(fstream);
                final BufferedReader br = new BufferedReader(new InputStreamReader(in));
                
                String strLine;
                while ((strLine = br.readLine()) != null) {
                    try {
                        int index = Integer.parseInt(strLine.substring(0, strLine.indexOf('\t')));
                        String concept = strLine.substring(strLine.indexOf('\t') + 1);
                        
                        Text t = new Text(index + ". " + concept);
                        t.setFill(Color.WHITE);
                        t.setFont(Font.font(null, FontWeight.BOLD, 15));
                        Reflection r = new Reflection();
                        r.setFraction(0.7f);
                        t.setEffect(r);
                        t.setFontSmoothingType(FontSmoothingType.LCD);
                       
                        texts.put(concept,t);
                    
                    } catch (NumberFormatException ex) {
                    }
                }
                
                Text t = new Text(texts.size() + ". " + "Unknown");
                t.setFill(Color.WHITE);
                t.setFont(Font.font(null, FontWeight.BOLD, 15));
                Reflection r = new Reflection();
                r.setFraction(0.7f);
                t.setFontSmoothingType(FontSmoothingType.LCD);
                t.setEffect(r);

                texts.put("Unknown",t);

            } finally {
                in.close();
            }
            return texts;
        }
    }
    
    public static class ToolBarGroup extends Region{
        
        private Rectangle clip;
        
        private ToolBar toolbar;
        private Button connect;
        private TextField serverAddr;
        
        private Button addPics;
        private Button deletePics;
        private Button predict;
        private Button charts;
        
        private final Group root = new Group();
        
        public ToolBarGroup(){

            clip = new Rectangle(0, 0, WIDTH, 500);
            setClip(clip);

            toolbar = new ToolBar();
            
            serverAddr = new TextField("127.0.0.1:9998");
            serverAddr.setMaxSize(140, 20);
            serverAddr.setAlignment(Pos.CENTER);
            connect = new Button("Update");
            
            addPics = new Button("Add Images");
            addPics.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
           
            deletePics = new Button("Delete Images");
            deletePics.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
            
            charts = new Button("Show charts");
            charts.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
           
            predict = new Button("Predict Image");
            predict.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
            
            HBox hbox1 = HBoxBuilder.create().spacing(10).children(serverAddr,connect).build();
            
            HBox hbox2 = HBoxBuilder.create().spacing(20).children(addPics, deletePics, predict, charts).build();
            
            HBox hbox = HBoxBuilder.create().spacing(179).children(hbox1,hbox2).build();
            
            addPics.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    FileChooser choose = new FileChooser();
                    List<File> files = choose.showOpenMultipleDialog(myStage);
                    
                    if (files != null){
                        add_images_map = new HashMap<String, Image>();
                        
                        for (int i = 0; i < files.size(); i++){
                            add_images_map.put(files.get(i).getAbsolutePath(), new Image(files.get(i).getAbsolutePath()));
                    }

                        myDisplayShelf.addImages(add_images_map);
                        
                    }
                }
                });
            
            addPics.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    addPics.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                        
                    }
                });
            
              addPics.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    addPics.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#ffffffff 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#ffffffff 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                    
                }
                });
            
            deletePics.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    myDisplayShelf.deleteImages();
                }
                });
            deletePics.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    deletePics.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                        
                    }
                });
            
              deletePics.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    deletePics.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#ffffffff 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#ffffffff 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                    
                }
                });
            
             connect.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                        
                        String text = serverAddr.getText();
                        ADDR = text.substring(0, text.indexOf(':'));
                        PORT = Integer.parseInt(text.substring(text.lastIndexOf(':') + 1));
                        
                        //connect.setDisable(true);
                        
                       // startConnectionToServer(ADDR, PORT);
                            
                        }
                    }).start();
                }
                });
             
             predict.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    new Thread(new Runnable(){

                        @Override
                        public void run() {
                            try {
                        
                                startConnectionToServer(ADDR, PORT);
                                sendRequestToServer(currentCenterImage.imagePath);
                                
                       
                        
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Could nou finish the request!" + ex.toString());
                    }
                        }
                    }).start();
                }
                });
             
              predict.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    predict.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                        
                    }
                });
            
              predict.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    predict.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#ffffffff 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#ffffffff 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                    
                }
                });
              
               charts.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    charts.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#fcfcfc 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#dddddd 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                        
                    }
                });
            
              charts.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    
                    charts.setStyle("-fx-background-color: linear-gradient(#f2f2f2, #d6d6d6), linear-gradient(#ffffffff 0%, #d9d9d9 20%, #d6d6d6 100%), linear-gradient(#ffffffff 0%, #f6f6f6 50%); -fx-background-radius: 8,7,6; -fx-background-insets: 0,1,2;  -fx-text-fill: black; -fx-effect: dropshadow( three-pass-box , rgba(0,0,0,0.6) , 5, 0.0 , 0 , 1 );");
                    
                }
                });
              
              charts.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.NONE);
                    dialogStage.setScene(new Scene(VBoxBuilder.create().
                            children(new Text("Hi"), new Button("Ok.")).
                            alignment(Pos.CENTER).padding(new Insets(5)).build()));
                    dialogStage.show();

                }
            });
             
            
            toolbar.getItems().add(VBoxBuilder.create().spacing(10).children(hbox).build());
            getChildren().add(toolbar);

    }
        
        public void startConnectionToServer(String address, int port) throws IOException {

            try {
      
                servSocket = new Socket(address, port);
                
                outToServer = new DataOutputStream(servSocket.getOutputStream());
                
                inFromServer = new BufferedReader(new InputStreamReader(servSocket.getInputStream()));
                
                System.out.println("Connected with server.");
                
               // JOptionPane.showMessageDialog(null, "Successfully connect to server!");
                
            } catch (UnknownHostException e) {
               JOptionPane.showMessageDialog(null, "Server can't be found!" + e);
            }

        }
        
        public void closeConnectionToServer() throws IOException {

            try {

                servSocket.close();
                
                System.out.println("Closed connection with server.");

            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(null, "Could not close connection to server!" + e);
            }

        }
        
        public void sendRequestToServer(String imagePath) throws IOException {

            if (servSocket.isConnected()) { 
            try {
                
                String imageName = imagePath.substring(imagePath.lastIndexOf("\\") + 1);
               
                String imageSet = "";
                if (imagePath.contains("test1")) {
                    imageSet = "test1";
                } else if (imagePath.contains("test2")) {
                    imageSet = "test2";
                } else if (imagePath.contains("training1")) {
                    imageSet = "training1";
                } else if (imagePath.contains("training2")) {
                    imageSet = "training2";
                } else if (imagePath.contains("training3")) {
                    imageSet = "training3";
                }
                    
      
                outToServer.writeBytes("predict " + imageSet + " " + imageName + "\n");
                outToServer.flush();
                
                System.out.println("Sent request: " + "predict " + imageSet + " " + imageName);
                
                receivePredictionFromServer();
                               
                
                
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Could not send request to server!" + e);
            }
            }

        }
        
        public void receivePredictionFromServer() throws IOException {

            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        
                        String answer = inFromServer.readLine();
                       
                        System.out.println("Received answer: " + answer);

                        //protocol for prediction answer
                        //predicted ElevatorArea 0.56 imageset\imagename

                        String[] results = answer.split(" ");

                        String concept = "";

                        if (results[0].equals("predicted")) {
                            concept = results[1];
                        }

                        FutureTask<Boolean> task = new FutureTask(new InvokeAndWait(concept, answer), true);
                        Platform.runLater(task);
                        task.get();
                        
                        closeConnectionToServer();

                    } catch (SocketException ex) {
                        System.err.println("Socket exception!" + ex.toString());
                    } catch (Exception ex) {
                        System.err.println("Exception occurred: " + ex.toString());
                    }
                }
            }).start();


        }
        
        public void setPredictionStatusLabel(String concept, String answer){
            
            if (!"".equals(concept))
                myTexts.get(concept).setFill(Color.RED);
            
            status.setText(answer);
            
        }
        
        
    }
    
    public static class InvokeAndWait implements Runnable{

        private static String concept;
        private static String answer;
        public InvokeAndWait(String c, String a){
            concept = c;
            answer = a;
        }
        
        @Override
        public void run() {
            
             if (!"".equals(concept))
                myTexts.get(concept).setFill(Color.RED);
            
            status.setText(answer);
        }
    }
    
    public static class DisplayShelf extends Region {
        private static final Duration DURATION = Duration.millis(500);
        private static final Interpolator INTERPOLATOR = Interpolator.EASE_BOTH;
        private static final double SPACING = 50;
        private static final double LEFT_OFFSET = -150;
        private static final double RIGHT_OFFSET = 110;
        private static final double SCALE_SMALL = 0.5;
        
        private PerspectiveImage[] items;
        
        private Group centered = new Group();
        private Group left = new Group();
        private Group center = new Group();
        private Group right = new Group();
        private int centerIndex = 0;
        private Timeline timeline;
        private ScrollBar scrollBar = new ScrollBar();
        private boolean localChange = false;
        private Rectangle clip = new Rectangle();

        public DisplayShelf(HashMap<String, Image> images) {
            // set clip
            setClip(clip);
            // set background gradient using css
            setStyle("-fx-background-color: linear-gradient(to bottom," +
                    "black 60, #00000000 60.1%, whitesmoke 100%);");
            // style scroll bar color
            scrollBar.setStyle("-fx-base: #202020; -fx-background: #202020;");
            // create items
            items = new PerspectiveImage[images.size()];
            int i = 0;
            for (Iterator<Entry<String, Image>> it = images.entrySet().iterator(); it.hasNext();) {
              
                Entry<String, Image> entry = it.next();
                final PerspectiveImage item =
                        items[i] = new PerspectiveImage(entry.getValue(), entry.getKey());
                final double index = i;
                
                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent me) {
                        localChange = true;
                        scrollBar.setValue(index);
                        localChange = false;
                        
                        //save current center image
                        currentCenterImage = item;
                        
                        shiftToCenter(item);
                        
                    }
                });
                ++i;
            }
            
            currentCenterImage = items[0];
            
            // setup scroll bar
            scrollBar.setMax(items.length-1);
            scrollBar.setVisibleAmount(1);
            scrollBar.setUnitIncrement(1);
            scrollBar.setBlockIncrement(1);
            scrollBar.valueProperty().addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable ov) {
                    if (items.length != 0) {
                        if (!localChange) {
                            shiftToCenter(items[(int) scrollBar.getValue()]);
                        }
                    }
                }
            });
            // create content
            centered.getChildren().addAll(left, right, center);
            getChildren().addAll(centered,scrollBar);
            // listen for keyboard events
            setFocusTraversable(true);
            setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent ke) {
                    if (ke.getCode() == KeyCode.LEFT) {
                        shift(1);
                        localChange = true;
                        scrollBar.setValue(centerIndex);
                        localChange = false;
                    } else if (ke.getCode() == KeyCode.RIGHT) {
                        shift(-1);
                        localChange = true;
                        scrollBar.setValue(centerIndex);
                        localChange = false;
                    }
                }
            });
            // update
            update();
        }
        
        public void addImages(HashMap<String, Image> images){
            
            PerspectiveImage[] previous_images = new PerspectiveImage[items.length];
            int i;
            for (i = 0; i < items.length; i++)
                previous_images[i] = items[i];
            
            items = new PerspectiveImage[previous_images.length + images.size()];
            for (i = 0; i < previous_images.length; i++)
                items[i] = previous_images[i];
           
            for (Iterator<Entry<String, Image>> it = images.entrySet().iterator(); it.hasNext();) {
                Entry<String, Image> entry = it.next();
                
                
                
                final PerspectiveImage item =
                        items[i] = new PerspectiveImage(entry.getValue(), entry.getKey());
                final double index = i;
                item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent me) {
                        localChange = true;
                        scrollBar.setValue(index);
                        localChange = false;
                        shiftToCenter(item);
                    }
                });
                ++i;
            }
            // setup scroll bar
            scrollBar.setMax(items.length-1);
            
            update();
            
        }
        
         public void deleteImages(){
            
            items = new PerspectiveImage[0];
            left.getChildren().clear();
            center.getChildren().clear();
            right.getChildren().clear();
            timeline = new Timeline();
            
            // setup scroll bar
            scrollBar.setMax(10);
            
        }

        @Override protected void layoutChildren() {
            // update clip to our size
            clip.setWidth(getWidth());
            clip.setHeight(getHeight());
            // keep centered centered
            centered.setLayoutY((getHeight() - PerspectiveImage.HEIGHT) / 2);
            centered.setLayoutX((getWidth() - PerspectiveImage.WIDTH) / 2);
            // position scroll bar at bottom
            scrollBar.setLayoutX(10);
            scrollBar.setLayoutY(getHeight()-25);
            scrollBar.resize(getWidth()-20,15);
        }

        private void update() {
            // move items to new homes inFromServer groups
            left.getChildren().clear();
            center.getChildren().clear();
            right.getChildren().clear();
            for (int i = 0; i < centerIndex; i++) {
                left.getChildren().add(items[i]);
            }
            center.getChildren().add(items[centerIndex]);
            for (int i = items.length - 1; i > centerIndex; i--) {
                right.getChildren().add(items[i]);
            }
            
            // stop old timeline if there is one running
            if (timeline!=null) timeline.stop();
            // create timeline to animate to new positions
            timeline = new Timeline();
            // add keyframes for left items
            final ObservableList<KeyFrame> keyFrames = timeline.getKeyFrames();
            for (int i = 0; i < left.getChildren().size(); i++) {
                final PerspectiveImage it = items[i];
                double newX = -left.getChildren().size() *
                        SPACING + SPACING * i + LEFT_OFFSET;
                keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                    new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
                    new KeyValue(it.angle, 45.0, INTERPOLATOR)));
            }
            // add keyframe for center item
            final PerspectiveImage centerItem = items[centerIndex];
            keyFrames.add(new KeyFrame(DURATION,
                    new KeyValue(centerItem.translateXProperty(), 0, INTERPOLATOR),
                    new KeyValue(centerItem.scaleXProperty(), 1.0, INTERPOLATOR),
                    new KeyValue(centerItem.scaleYProperty(), 1.0, INTERPOLATOR),
                    new KeyValue(centerItem.angle, 90.0, INTERPOLATOR)));
            // add keyframes for right items
            for (int i = 0; i < right.getChildren().size(); i++) {
                final PerspectiveImage it = items[items.length - i - 1];
                final double newX = right.getChildren().size() *
                        SPACING - SPACING * i + RIGHT_OFFSET;
                keyFrames.add(new KeyFrame(DURATION,
                        new KeyValue(it.translateXProperty(), newX, INTERPOLATOR),
                        new KeyValue(it.scaleXProperty(), SCALE_SMALL, INTERPOLATOR),
                        new KeyValue(it.scaleYProperty(), SCALE_SMALL, INTERPOLATOR),
                        new KeyValue(it.angle, 135.0, INTERPOLATOR)));
            }
            // play animation
            timeline.play();
        }

        private void shiftToCenter(PerspectiveImage item) {
            for (int i = 0; i < left.getChildren().size(); i++) {
                if (left.getChildren().get(i) == item) {
                    int shiftAmount = left.getChildren().size() - i;
                    shift(shiftAmount);
                    return;
                }
            }
            if (center.getChildren().get(0) == item) {
                return;
            }
            for (int i = 0; i < right.getChildren().size(); i++) {
                if (right.getChildren().get(i) == item) {
                    int shiftAmount = -(right.getChildren().size() - i);
                    shift(shiftAmount);
                    return;
                }
            }
        }

        public void shift(int shiftAmount) {
            if (centerIndex <= 0 && shiftAmount > 0) return;
            if (centerIndex >= items.length - 1 && shiftAmount < 0) return;
            centerIndex -= shiftAmount;
            update();
        }
    }

    /**
     * A Node that displays a image with some 2.5D perspective rotation around the Y axis.
     */
    public static class PerspectiveImage extends Parent {
        private static final double REFLECTION_SIZE = 0.20;
        private static final double WIDTH = 200;
        private static final double HEIGHT = WIDTH + (WIDTH*REFLECTION_SIZE);
        private static final double RADIUS_H = WIDTH / 2;
        private static final double BACK = WIDTH / 10;
        private PerspectiveTransform transform = new PerspectiveTransform();
        
        public String imagePath;
        
        
        /** Angle Property */
        private final DoubleProperty angle = new SimpleDoubleProperty(45) {
            @Override protected void invalidated() {
                // when angle changes calculate new transform
                double lx = (RADIUS_H - Math.sin(Math.toRadians(angle.get())) * RADIUS_H - 1);
                double rx = (RADIUS_H + Math.sin(Math.toRadians(angle.get())) * RADIUS_H + 1);
                double uly = (-Math.cos(Math.toRadians(angle.get())) * BACK);
                double ury = -uly;
                transform.setUlx(lx);
                transform.setUly(uly);
                transform.setUrx(rx);
                transform.setUry(ury);
                transform.setLrx(rx);
                transform.setLry(HEIGHT + uly);
                transform.setLlx(lx);
                transform.setLly(HEIGHT + ury);
            }
        };
        public final double getAngle() { return angle.getValue(); }
        public final void setAngle(double value) { angle.setValue(value); }
        public final DoubleProperty angleModel() { return angle; }

        public PerspectiveImage(Image image, String path) {
           
            ImageView imageView = new ImageView(image);
            imageView.setEffect(ReflectionBuilder.create().fraction(REFLECTION_SIZE).build());
            setEffect(transform);
            VBox vbox = new VBox();
            vbox.setSpacing(10);
            
            Group g = new Group();
            
            imagePath = path;
            String imageName = path.substring(path.lastIndexOf('\\') + 1);
          
            Rectangle rectangle = new Rectangle();
            rectangle.setFill(Color.WHITE);

            Text t = new Text(imageName);
            t.setFill(Color.DARKGREY);
            t.setFont(Font.font("Arial", FontWeight.BOLD, 35));
            DropShadow ds = new DropShadow();
            t.setEffect(new Lighting());
//            Reflection r = new Reflection();
//            r.setFraction(0.2f);
//            t.setEffect(r);

            Bounds bounds = t.getBoundsInParent();
            double arcH = 3;
            double arcW = 3;

            double x = 0;
            double y = 0;
            t.setLayoutX(x);
            t.setLayoutY(y);
            double sWidth = 100;
            double sHeight = 100;

            x = sWidth / 2 - (bounds.getWidth() / 2);
            y = sHeight / 2 - (bounds.getHeight() / 2);
            t.setLayoutX(x);
            t.setLayoutY(y);
            bounds = t.getBoundsInParent();
            double baseLineOffset = t.getBaselineOffset();

            rectangle.setLayoutX(x - arcW);
            rectangle.setLayoutY(y - baseLineOffset - arcH);
            rectangle.setArcHeight(arcH);
            rectangle.setArcWidth(arcW);
            rectangle.setWidth(bounds.getWidth() + arcW * 2);
            rectangle.setHeight(bounds.getHeight() + arcH * 2);

            g.getChildren().addAll(rectangle, t);
            
            vbox.getChildren().add(imageView);
            VBox.setMargin(g, new Insets(8, 0, 0, 230));
            vbox.getChildren().add(g);
            
            getChildren().addAll(vbox); //imageView, new Text("imagename"));
        }
    }

    public double getSampleWidth() { return 495; }

    public double getSampleHeight() { return 300; }
}


