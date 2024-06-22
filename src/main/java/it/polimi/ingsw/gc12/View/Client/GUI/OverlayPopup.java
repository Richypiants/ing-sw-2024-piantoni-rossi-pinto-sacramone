package it.polimi.ingsw.gc12.View.Client.GUI;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Window;

/**
 * Utility class extending the functionalities of the JavaFX {@link Popup} class.
 *
 * <p>This class provides a kind of Popup that darkens the scene root in the background and disables
 * interaction with it, so that the end user may not click anything other than elements in popup content.</p>
 * <p>
 * The popup maintains a reference to the owner window, so that it may be able to hide itself from
 * sight when the window loses focus and gets put behind another window or minimized. It also keeps a
 * reference to the owner scene root, so that it may automatically close itself when the scene root changes.
 */
public class OverlayPopup extends Popup {

    /**
     * The AnchorPane element used for darkening the background, shared among all OverlayPopup instances
     * for performance optimization.
     * It is initialized only once, on class loading.
     */
    private static final AnchorPane DARKENING_PANE = generateDarkeningPane();
    /**
     * The currently opened OverlayPopup instance, needed so that any new popups can hide it before getting shown.
     */
    private static OverlayPopup openedPopup = null;
    /**
     * The owner window of this OverlayPopup.
     */
    private Window ownerWindow = null;
    /**
     * The owner scene root of this OverlayPopup.
     */
    private Node ownerRoot = null;
    /**
     * The listener registered on the focusedProperty of the owner window.
     */
    private ChangeListener<Boolean> ownerWindowFocusedListener;

    /**
     * Constructs a default OverlayPopup instance.
     */
    public OverlayPopup() {
    }

    /**
     * Hides the currently opened OverlayPopup and deletes the reference to it.
     */
    public static void closeLingeringOpenedPopup() {
        Platform.runLater(() -> {
            if (openedPopup != null) {
                openedPopup.hide();
                openedPopup = null;
            }
        });
    }

    /**
     * Generates the AnchorPane used for darkening the backgrounds of the OverlayPanes.
     *
     * @return The AnchorPane used for darkening the backgrounds.
     */
    private static AnchorPane generateDarkeningPane() {
        AnchorPane darkening = new AnchorPane();

        darkening.setId("#darkening");
        darkening.setStyle("-fx-background-color: black;");
        darkening.setOpacity(0.8);

        return darkening;
    }

    /**
     * Disables any interaction with the background and darkens it by showing the darkening pane.
     */
    private void darken() {
        if (ownerWindow == null) return;

        ownerRoot = ownerWindow.getScene().getRoot();

        if (ownerRoot instanceof Pane pane) {
            pane.setDisable(true);
            DARKENING_PANE.setPrefSize(ownerWindow.getWidth(), ownerWindow.getHeight());
            pane.getChildren().add(DARKENING_PANE);
        }
    }

    /**
     * Shows the popup, darkening and disabling the background first and registers a listener to the
     * owner window's focusedProperty, so that the popup may be hidden when the window loses focus.
     *
     * @param window The owner of the popup. This must not be null.
     */
    @Override
    public void show(Window window) {
        if (openedPopup != null) openedPopup.hide();
        openedPopup = this;

        ownerWindow = window;
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

    /**
     * Hides the popup, reenabling the background and unregistering the listener to the owner window's
     * focusedProperty.
     */
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

