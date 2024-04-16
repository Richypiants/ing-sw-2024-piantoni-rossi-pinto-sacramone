package it.polimi.ingsw.gc12;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void newPane(ActionEvent event) throws IOException {
        Parent nickname = FXMLLoader.load(getClass().getResource("Nickname.fxml"));
        Scene nicknameScene = new Scene(nickname);
        Stage stage = new Stage();
        stage.setScene(nicknameScene);
        stage.show();
    }
}