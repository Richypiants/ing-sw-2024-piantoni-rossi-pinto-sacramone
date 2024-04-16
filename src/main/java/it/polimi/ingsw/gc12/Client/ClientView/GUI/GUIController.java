package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class GUIController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void HelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}