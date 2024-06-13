package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIViews;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class GUITitleView extends GUIView {

    private static GUITitleView titleScreenController = null;
    private final Parent SCENE_ROOT;
    private final ImageView CRANIO_CREATIONS_LOGO;
    private final AnchorPane TITLE_SCREEN_BOX;
    private final ImageView TITLE_SCREEN_GAME_LOGO;
    private Label TITLE_SCREEN_PROMPT;

    private GUITitleView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/fxml/title_screen.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        CRANIO_CREATIONS_LOGO = (ImageView) SCENE_ROOT.lookup("#cranioCreationsLogo");
        TITLE_SCREEN_BOX = (AnchorPane) SCENE_ROOT.lookup("#titleScreenBox");
        TITLE_SCREEN_GAME_LOGO = (ImageView) TITLE_SCREEN_BOX.lookup("#titleScreenGameLogo");
    }

    public static GUITitleView getInstance() {
        if (titleScreenController == null) {
            titleScreenController = new GUITitleView();
        }
        return titleScreenController;
    }

    @Override
    public void titleScreen() {
        Platform.runLater(() -> {
            stage.getScene().setRoot(SCENE_ROOT);

            //FIXME: why does this not work in fxml???
            CRANIO_CREATIONS_LOGO.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/images/cranio_creations_logo_no_bg.png"))));
            CRANIO_CREATIONS_LOGO.setSmooth(true);
            CRANIO_CREATIONS_LOGO.setFitWidth(650);
            CRANIO_CREATIONS_LOGO.setPreserveRatio(true);
            CRANIO_CREATIONS_LOGO.setVisible(true);

            CRANIO_CREATIONS_LOGO.relocate(
                    (screenSizes.getX() - CRANIO_CREATIONS_LOGO.getFitWidth()) / 2,
                    screenSizes.getY() * 10 / 100
            );

            FadeTransition logoTransition = new FadeTransition(Duration.millis(3000), CRANIO_CREATIONS_LOGO);
            logoTransition.setDelay(Duration.millis(1000));
            logoTransition.setFromValue(0);
            logoTransition.setToValue(1);
            logoTransition.setCycleCount(2);
            logoTransition.setAutoReverse(true);
            logoTransition.setOnFinished((event -> {
                CRANIO_CREATIONS_LOGO.setVisible(false);
                TITLE_SCREEN_BOX.setVisible(true);
            }));

            TITLE_SCREEN_BOX.setPrefSize(screenSizes.getX(), screenSizes.getY());

            TITLE_SCREEN_GAME_LOGO.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/images/only_center_logo_no_bg.png"))));
            TITLE_SCREEN_GAME_LOGO.setSmooth(true);
            TITLE_SCREEN_GAME_LOGO.setFitWidth(650);
            TITLE_SCREEN_GAME_LOGO.setFitHeight(650);
            TITLE_SCREEN_GAME_LOGO.setPreserveRatio(true);

            FadeTransition backgroundTransition = new FadeTransition(Duration.millis(4000), TITLE_SCREEN_BOX);
            backgroundTransition.setFromValue(0.0);
            backgroundTransition.setToValue(1.0);

            TITLE_SCREEN_GAME_LOGO.relocate(
                    (screenSizes.getX() - TITLE_SCREEN_GAME_LOGO.getFitWidth()) / 2,
                    screenSizes.getY() * 5 / 100
            );

            TITLE_SCREEN_PROMPT = new Label("Premi INVIO per iniziare");
            TITLE_SCREEN_PROMPT.setId("titleScreenPrompt");
            TITLE_SCREEN_PROMPT.setPrefSize(500, 25);
            TITLE_SCREEN_PROMPT.setOnMouseClicked((event -> ViewState.getCurrentState().keyPressed()));
            TITLE_SCREEN_PROMPT.setOnKeyPressed((event -> ViewState.getCurrentState().keyPressed()));

            TITLE_SCREEN_BOX.getChildren().add(TITLE_SCREEN_PROMPT);

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

            //FIXME: KeyEvent non ricevuto...
            CRANIO_CREATIONS_LOGO.setOnMouseClicked((event) -> {
                CRANIO_CREATIONS_LOGO.setVisible(false);
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration());
            });
            CRANIO_CREATIONS_LOGO.setOnKeyPressed((event) -> {
                CRANIO_CREATIONS_LOGO.setVisible(false);
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration());
            });

            TITLE_SCREEN_GAME_LOGO.setOnMouseClicked((event) -> {
                if (!backgroundTransition.getStatus().equals(Animation.Status.STOPPED)) {
                    titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration()));
                    event.consume();
                }
            });
            TITLE_SCREEN_GAME_LOGO.setOnKeyPressed((event) -> {
                if (!backgroundTransition.getStatus().equals(Animation.Status.STOPPED)) {
                    titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration().add(Duration.millis(1000))));
                    event.consume();
                }
            });

            CRANIO_CREATIONS_LOGO.toFront();
            titleScreenTransition.play();
        });
    }
}
