package it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;

import it.polimi.ingsw.gc12.View.Client.GUI.OverlayPopup;
import it.polimi.ingsw.gc12.View.Client.ViewStates.ViewState;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

/**
 * Singleton class representing the title view in the Graphical User Interface (GUI).
 * It extends the GUIView class and implements methods for displaying the title screen.
 */
public class GUITitleView extends GUIView {

    /**
     * The singleton instance of the {@code GUITitleView}.
     */
    private static GUITitleView titleScreenController = null;

    private final Parent SCENE_ROOT;
    private final ImageView CRANIO_CREATIONS_LOGO;
    private final AnchorPane TITLE_SCREEN_BOX;
    private final ImageView TITLE_SCREEN_GAME_LOGO;
    private final Label TITLE_SCREEN_PROMPT;

    /**
     * Constructs a {@code GUITitleView} instance (private constructor to prevent external instantiation at will).
     * On initialization, it loads the graphical elements from the correct .fxml file.
     */
    private GUITitleView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/Client/fxml/title_screen.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e); //Should never happen
        }
        CRANIO_CREATIONS_LOGO = (ImageView) SCENE_ROOT.lookup("#cranioCreationsLogo");
        TITLE_SCREEN_BOX = (AnchorPane) SCENE_ROOT.lookup("#titleScreenBox");
        TITLE_SCREEN_GAME_LOGO = (ImageView) SCENE_ROOT.lookup("#titleScreenGameLogo");
        TITLE_SCREEN_PROMPT = (Label) SCENE_ROOT.lookup("#titleScreenPrompt");
    }

    /**
     * Returns the singleton instance of the {@code GUITitleView}, also initializing it if it had never been
     * instantiated, as per the Singleton pattern.
     *
     * @return The singleton instance
     */
    public static GUITitleView getInstance() {
        if (titleScreenController == null) {
            titleScreenController = new GUITitleView();
        }
        return titleScreenController;
    }

    /**
     * Displays the title screen with the logos animations, waits for user input to proceed, and transitions to the next view state.
     */
    @Override
    public void titleScreen() {
        OverlayPopup.closeLingeringOpenedPopup();

        Platform.runLater(() -> {
            stage.getScene().setRoot(SCENE_ROOT);

            CRANIO_CREATIONS_LOGO.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/Client/images/cranio_creations_logo_no_bg.png"))));
            CRANIO_CREATIONS_LOGO.setSmooth(true);
            CRANIO_CREATIONS_LOGO.setFitWidth(windowSize.getX() * 40 / 100);
            CRANIO_CREATIONS_LOGO.setFitHeight(windowSize.getX() * 40 / 100);
            CRANIO_CREATIONS_LOGO.setVisible(true);

            CRANIO_CREATIONS_LOGO.relocate(
                    (windowSize.getX() - CRANIO_CREATIONS_LOGO.getFitWidth()) / 2,
                    (windowSize.getY() - CRANIO_CREATIONS_LOGO.getFitHeight()) / 2
            );

            FadeTransition logoTransition = new FadeTransition(Duration.millis(3000), CRANIO_CREATIONS_LOGO);
            logoTransition.setDelay(Duration.millis(1));
            logoTransition.setFromValue(0);
            logoTransition.setToValue(1);
            logoTransition.setCycleCount(2);
            logoTransition.setAutoReverse(true);
            logoTransition.setOnFinished((event -> {
                CRANIO_CREATIONS_LOGO.setVisible(false);
                TITLE_SCREEN_BOX.setVisible(true);
                TITLE_SCREEN_BOX.requestFocus();
            }));

            TITLE_SCREEN_BOX.setPrefSize(windowSize.getX(), windowSize.getY());

            TITLE_SCREEN_GAME_LOGO.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/Client/images/only_center_logo_no_bg.png"))));
            TITLE_SCREEN_GAME_LOGO.setSmooth(true);
            TITLE_SCREEN_GAME_LOGO.setFitWidth(windowSize.getX() * 40 / 100);
            TITLE_SCREEN_GAME_LOGO.setFitHeight(windowSize.getX() * 40 / 100);

            TITLE_SCREEN_GAME_LOGO.relocate(
                    (windowSize.getX() - TITLE_SCREEN_GAME_LOGO.getFitWidth()) / 2,
                    windowSize.getY() * 5 / 100
            );
            TITLE_SCREEN_GAME_LOGO.toFront();

            FadeTransition backgroundTransition = new FadeTransition(Duration.millis(4000), TITLE_SCREEN_BOX);
            backgroundTransition.setFromValue(0.0);
            backgroundTransition.setToValue(1.0);

            TITLE_SCREEN_PROMPT.setPrefSize(500, 25);

            TITLE_SCREEN_PROMPT.relocate(
                    (TITLE_SCREEN_BOX.getPrefWidth() - TITLE_SCREEN_PROMPT.getPrefWidth()) / 2,
                    TITLE_SCREEN_BOX.getPrefHeight() * 80 / 100
            );

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), TITLE_SCREEN_PROMPT);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.2);
            fadeTransition.setCycleCount(Animation.INDEFINITE);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition titleScreenBoxTransition = new ParallelTransition(backgroundTransition, fadeTransition);
            SequentialTransition titleScreenTransition = new SequentialTransition(logoTransition, titleScreenBoxTransition);

            CRANIO_CREATIONS_LOGO.requestFocus();
            CRANIO_CREATIONS_LOGO.setOnMouseClicked((event) -> {
                CRANIO_CREATIONS_LOGO.setVisible(false);
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration());
            });
            CRANIO_CREATIONS_LOGO.setOnKeyPressed((event) -> {
                if (event.getCode().equals(KeyCode.ESCAPE) || event.getCode().equals(KeyCode.F11))
                    return;
                CRANIO_CREATIONS_LOGO.setVisible(false);
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration());
            });

            TITLE_SCREEN_BOX.setOnMouseClicked((event) -> {
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration()));
                event.consume();
            });
            TITLE_SCREEN_BOX.setOnKeyPressed((event) -> {
                if (event.getCode().equals(KeyCode.ESCAPE) || event.getCode().equals(KeyCode.F11))
                    return;
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration()));
                event.consume();
            });

            backgroundTransition.setOnFinished((event) -> {
                TITLE_SCREEN_BOX.requestFocus();
                TITLE_SCREEN_BOX.setOnMouseClicked((event2 -> {
                    titleScreenTransition.stop();
                    ViewState.getCurrentState().keyPressed();
                }));
                TITLE_SCREEN_BOX.setOnKeyPressed((event2 -> {
                    if (event2.getCode().equals(KeyCode.ESCAPE) || event2.getCode().equals(KeyCode.F11))
                        return;
                    titleScreenTransition.stop();
                    ViewState.getCurrentState().keyPressed();
                }));
            });

            CRANIO_CREATIONS_LOGO.toFront();

            titleScreenTransition.play();
        });
    }
}
