package it.polimi.ingsw.gc12;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException, InterruptedException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("First.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        // Image icon = new Image("C:/Users/jacop/Desktop/Stage.png");
        // stage.getIcons().add(icon);
        stage.setTitle("Codex Naturalis by Cranio Creation");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}