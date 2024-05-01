package it.polimi.ingsw.gc12;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.TitleScreenState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
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
        //FIXME: ... fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load();
        ClientController.getInstance().view = fxmlLoader.getController();
        ClientController.getInstance().viewState = new TitleScreenState();
        ClientController.getInstance().viewState.executeState();

        Scene scene = new Scene(root, 1280, 720);

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
        stage.setFullScreen(true);

        // Image icon = new Image("C:/Users/jacop/Desktop/Stage.png");
        // stage.getIcons().add(icon);

        stage.setTitle("Codex Naturalis");
        stage.setScene(scene);
        stage.show();
    }
}