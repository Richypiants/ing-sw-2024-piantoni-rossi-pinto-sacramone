package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

import java.util.Objects;

public abstract class GUITitleScreenController extends GUIView {

    static Parent sceneRoot = sceneRoots.get("title_screen");

    static ImageView cranioCreationsLogo = (ImageView) sceneRoot.lookup("#cranioCreationsLogo");

    static AnchorPane titleScreenBox = (AnchorPane) sceneRoot.lookup("#titleScreenBox");

    static ImageView titleScreenGameLogo = (ImageView) titleScreenBox.lookup("#titleScreenGameLogo");

    static Label titleScreenPrompt;

    //FIXME: change this new in all names...
    public static void newTitleScreen() {
        Platform.runLater(() -> {
            stage.getScene().setRoot(sceneRoot);

            //FIXME: why does this not work in fxml???
            cranioCreationsLogo.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/images/cranio_creations_logo_no_bg.png"))));
            cranioCreationsLogo.setFitWidth(650);
            cranioCreationsLogo.setPreserveRatio(true);
            cranioCreationsLogo.setVisible(true);

            cranioCreationsLogo.relocate(
                    (screenSizes.getX() - cranioCreationsLogo.getFitWidth()) / 2,
                    screenSizes.getY() * 10 / 100
            );

            FadeTransition logoTransition = new FadeTransition(Duration.millis(3000), cranioCreationsLogo);
            logoTransition.setDelay(Duration.millis(1000));
            logoTransition.setFromValue(0);
            logoTransition.setToValue(1);
            logoTransition.setCycleCount(2);
            logoTransition.setAutoReverse(true);
            logoTransition.setOnFinished((event -> {
                cranioCreationsLogo.setVisible(false);
                titleScreenBox.setVisible(true);
            }));

            titleScreenBox.setPrefSize(screenSizes.getX(), screenSizes.getY());

            titleScreenGameLogo.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/images/only_center_logo_no_bg.png"))));
            titleScreenGameLogo.setFitWidth(650);
            titleScreenGameLogo.setFitHeight(650);
            titleScreenGameLogo.setPreserveRatio(true);

            FadeTransition backgroundTransition = new FadeTransition(Duration.millis(6000), titleScreenBox);
            backgroundTransition.setFromValue(0.0);
            backgroundTransition.setToValue(1.0);

            titleScreenGameLogo.relocate(
                    (screenSizes.getX() - titleScreenGameLogo.getFitWidth()) / 2,
                    screenSizes.getY() * 5 / 100
            );

            titleScreenPrompt = new Label("Premi INVIO per iniziare");
            titleScreenPrompt.setId("titleScreenPrompt");
            titleScreenPrompt.setPrefSize(500, 25);
            titleScreenPrompt.setOnMouseClicked((event -> ClientController.getInstance().viewState.keyPressed()));
            titleScreenPrompt.setOnKeyPressed((event -> ClientController.getInstance().viewState.keyPressed()));

            titleScreenBox.getChildren().add(titleScreenPrompt);

            titleScreenPrompt.relocate(
                    (titleScreenBox.getPrefWidth() - titleScreenPrompt.getPrefWidth()) / 2,
                    titleScreenBox.getPrefHeight() * 85 / 100
            );

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), titleScreenPrompt);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.2);
            fadeTransition.setCycleCount(Animation.INDEFINITE);
            fadeTransition.setAutoReverse(true);
            fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

            ParallelTransition titleScreenBoxTransition = new ParallelTransition(backgroundTransition, fadeTransition);
            SequentialTransition titleScreenTransition = new SequentialTransition(logoTransition, titleScreenBoxTransition);

            //FIXME: KeyEvent non ricevuto...
            cranioCreationsLogo.setOnMouseClicked((event) -> {
                cranioCreationsLogo.setVisible(false);
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration());
            });
            cranioCreationsLogo.setOnKeyPressed((event) -> {
                cranioCreationsLogo.setVisible(false);
                titleScreenTransition.jumpTo(logoTransition.getTotalDuration());
            });

            titleScreenGameLogo.setOnMouseClicked((event) -> {
                if (!backgroundTransition.getStatus().equals(Animation.Status.STOPPED)) {
                    titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration()));
                    event.consume();
                }
            });
            titleScreenGameLogo.setOnKeyPressed((event) -> {
                if (!backgroundTransition.getStatus().equals(Animation.Status.STOPPED)) {
                    titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration().add(Duration.millis(1000))));
                    event.consume();
                }
            });

            cranioCreationsLogo.toFront();
            titleScreenTransition.play();
        });
    }
}
