package it.polimi.ingsw.gc12;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

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
    protected void newPane() throws IOException {
        // RIvedere perchè non funziona alla prima ma alla seconda
        startbutton.setOnAction(event -> {
            Stage boh = (Stage) startbutton.getScene().getWindow();
            boh.close();
        });

        Parent nickname = FXMLLoader.load(getClass().getResource("Second.fxml"));
        Scene nicknameScene = new Scene(nickname);
        Stage stage = new Stage();
        stage.setScene(nicknameScene);
        stage.show();
    }

    @FXML
    protected void Yes() throws IOException {
        if (NicknameField.getCharacters().length() > 0) {
            StatusLabel.setText("Status: Nickname Ok - Logged in");
            Parent nickname = FXMLLoader.load(getClass().getResource("Third.fxml"));
            Scene nicknameScene = new Scene(nickname);
            Stage stage = new Stage();
            stage.setScene(nicknameScene);
            stage.show();
        } else {
            StatusLabel.setText("Inserire prima un Nickname");
        }
    }

    @FXML
    protected void No() throws IOException {
        StatusLabel.setText("Status: Not logged in");
    }


    public void BackToTitleScreen(ActionEvent event) throws IOException {
        Parent titleScreen = FXMLLoader.load(getClass().getResource("First.fxml"));
        Scene titleScreenScene = new Scene(titleScreen);
        Stage stage = new Stage();
        stage.setScene(titleScreenScene);
        stage.show();
    }

    public void NewGame(ActionEvent event) {
        newGame = new TreeView<>();
        join = new Button("JOIN");
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent nickname = FXMLLoader.load(getClass().getResource("Second.fxml"));
        Scene nicknameScene = new Scene(nickname);
        Stage stage = new Stage();
        stage.setScene(nicknameScene);
        stage.show();
    }
}