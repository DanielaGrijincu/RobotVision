/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package robotvisionclient;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 * @author Danna
 */
public class ToolButtons extends Region{
        
        private Text minimize = new Text("-");
        private Text close = new Text("x");
        
        private Rectangle clip;
        
        private final Group group = new Group();
        
        public ToolButtons(final Stage myStage, double WIDTH){

            close.setEffect(new Lighting());
            close.setFill(Color.WHITESMOKE);
            close.setFont(Font.font(null, FontWeight.BOLD, 13));
            
            minimize.setEffect(new Lighting());
            minimize.setFill(Color.WHITESMOKE);
            minimize.setFont(Font.font(null, FontWeight.BOLD, 15));
            
            clip = new Rectangle(0, 0, WIDTH, 20);
            setClip(clip);
            setStyle("-fx-background-color: linear-gradient(gray 20%, #202020 100%); -fx-padding: 5,5,5,5");
            
            HBox hbox = new HBox();
            hbox.setSpacing(10);
            hbox.setPadding(new Insets(0, 0, 0, WIDTH - 40));
            hbox.getChildren().addAll(minimize,close);
            
            BorderPane pane = new BorderPane();
            pane.setLeft(hbox);
            
            close.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    myStage.close();
                }
                });
            
            minimize.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                    myStage.setIconified(true);
                }
                });
            
            minimize.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                   minimize.setFill(Color.BROWN);
                }
                });
            
            minimize.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                   minimize.setFill(Color.WHITE);
                }
                });
            
            close.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                   close.setFill(Color.BROWN);
                }
                });
            
            close.addEventHandler(MouseEvent.MOUSE_EXITED, new EventHandler() {
                
                @Override
                public void handle(Event arg0) {
                   close.setFill(Color.WHITE);
                }
                });
            
            getChildren().addAll(pane);
        }
}
