package it.polimi.ingsw.gc12;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
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
    Button startbutton;

    @FXML
    TreeView newGame;
    @FXML
    Button join;

    @FXML
    protected void newPane(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void Yes(ActionEvent event) throws IOException, InterruptedException {
        if (NicknameField.getCharacters().length() >= 5) {
            StatusLabel.setText("Status: Nickname Ok - Logged in");
            // wait(2500);
            Parent root = FXMLLoader.load(getClass().getResource("Third.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
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
        Parent root = FXMLLoader.load(getClass().getResource("First.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void NewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Fourth.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Third.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}