package it.polimi.ingsw.gc12.View.Client.GUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

public class OverlayPopup extends Popup {

    private static final AnchorPane DARKENING_PANE = generateDarkeningPane();

    private Window ownerWindow = null;
    private Node ownerRoot = null;
    private ChangeListener<Boolean> ownerWindowFocusedListener;
    private static OverlayPopup openedPopup = null;

    public OverlayPopup() {
    }

    private static AnchorPane generateDarkeningPane() {
        AnchorPane darkening = new AnchorPane();

        darkening.setId("#darkening");
        darkening.setStyle("-fx-background-color: black;");
        darkening.setOpacity(0.8);

        return darkening;
    }

    public static void closeLingeringOpenedPopup() {
        Platform.runLater(() -> {
            if (openedPopup != null) {
                openedPopup.hide();
                openedPopup = null;
            }
        });
    }

    private void darken() {
        if (ownerWindow == null) return;

        ownerRoot = ownerWindow.getScene().getRoot();

        if (ownerRoot instanceof Pane pane) {
            pane.setDisable(true);
            DARKENING_PANE.setPrefSize(ownerWindow.getWidth(), ownerWindow.getHeight());
            pane.getChildren().add(DARKENING_PANE);
        }
    }

    @Override
    public void show(Window window) {
        if (openedPopup != null) openedPopup.hide();
        openedPopup = this;

        ownerWindow = window;
        ownerRoot = window.getScene().getRoot();
        darken();
        getContent().getFirst().setVisible(true);
        if (ownerWindow.focusedProperty().get()) super.show(window);

        ownerWindowFocusedListener = (observable, oldValue, newValue) -> {
            if (newValue) {
                super.show(window);
            } else {
                super.hide();
            }
        };

        ownerWindow.focusedProperty().addListener(ownerWindowFocusedListener);
    }

    @Override
    public void hide() {
        getContent().getFirst().setVisible(false);
        ownerWindow.focusedProperty().removeListener(ownerWindowFocusedListener);
        openedPopup = null;

        super.hide();

        if (ownerWindow == null) return;

        if (ownerRoot instanceof Pane pane) {
            pane.getChildren().remove(DARKENING_PANE);
            pane.setDisable(false);
        }
    }
}

