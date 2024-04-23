package it.polimi.ingsw.gc12;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("First.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);

        Label Codex = (Label) fxmlLoader.getNamespace().get("Codex");
        Button startButton = (Button) fxmlLoader.getNamespace().get("startButton");
        Rectangle backgroundLabel = (Rectangle) fxmlLoader.getNamespace().get("backgroundLabel");
        StackPane First = (StackPane) fxmlLoader.getNamespace().get("First");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(Codex, Pos.CENTER);
        StackPane.setMargin(Codex, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(backgroundLabel, Pos.CENTER);
        StackPane.setMargin(backgroundLabel, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(screenHeight * 0.425, 0, 0, 0));
        stage.setMaximized(true);

        // Image icon = new Image("C:/Users/jacop/Desktop/Stage.png");
        // stage.getIcons().add(icon);

        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}