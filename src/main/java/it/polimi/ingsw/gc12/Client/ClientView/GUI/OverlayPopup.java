package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

public class OverlayPopup extends Popup {

    AnchorPane darkening;

    public OverlayPopup() {
        super();

        darkening = new AnchorPane();
        darkening.setId("#darkening");
        darkening.setStyle("-fx-background-color: black;");
        darkening.setOpacity(0.8);
    }

    private void darken(Window window) {
        Node root = window.getScene().getRoot();

        if (root instanceof Pane pane) {
            pane.setDisable(true);
            darkening.setPrefSize(window.getWidth(), window.getHeight());
            pane.getChildren().add(darkening);
        }
    }

    @Override
    public void show(Window window) {
        darken(window);
        getContent().getFirst().setVisible(true);
        super.show(window);
    }

    @Override
    public void hide() {
        Node root = getOwnerWindow().getScene().getRoot();

        if (root instanceof Pane pane) {
            pane.getChildren().remove(darkening);
            pane.setDisable(false);
        }
        getContent().getFirst().setVisible(true);
        super.hide();
    }
}
