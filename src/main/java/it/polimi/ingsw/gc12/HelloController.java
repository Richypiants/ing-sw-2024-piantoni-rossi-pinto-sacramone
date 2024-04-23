package it.polimi.ingsw.gc12;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    Parent root;
    Scene scene;
    Stage stage;

    @FXML
    Label StatusLabel;

    @FXML
    TextField NicknameField;

    @FXML
    Button startButton;

    @FXML
    TreeView newGame;
    @FXML
    Button join;

    @FXML
    protected void newPane(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    protected void Yes(ActionEvent event) throws IOException, InterruptedException {
        if (NicknameField.getCharacters().length() >= 5) {
            StatusLabel.setText("Status: Nickname Ok - Logged in");
            // wait(2500);
            Parent root = FXMLLoader.load(getClass().getResource("Third.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1800, 850);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        } else if (NicknameField.getCharacters().length() < 5 && NicknameField.getCharacters().length() > 0) {
            StatusLabel.setText("Nickname troppo corto, almeno 5 caratteri");
        } else {
            StatusLabel.setText("Inserire prima un nickname");
        }
    }

    @FXML
    protected void No() throws IOException {
        StatusLabel.setText("Status: Not logged in");
    }

    public void BackToTitleScreen(ActionEvent event) throws IOException {
        // Carica il FXML e ottiene il root node correttamente
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("First.fxml"));
        Parent root = fxmlLoader.load();  // Carica la vista FXML

        // Ottiene lo stage corrente da event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Crea la scena con la dimensione specificata
        Scene scene = new Scene(root, 1800, 850);

        // Accesso ai componenti nel namespace del FXMLLoader
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

        // Imposta la scena e massimizza il palcoscenico
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }


    public void NewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Fourth.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Third.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}