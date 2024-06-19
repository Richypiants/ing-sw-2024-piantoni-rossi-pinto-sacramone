package it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Objects;

public class GUIConnectionSetupView extends GUIView {

    private static GUIConnectionSetupView connectionSetupController = null;
    private final Parent SCENE_ROOT;
    private final ImageView CONNECTION_TITLE_SCREEN_GAME_LOGO;
    private final VBox CONNECTION_SETUP_BOX;
    private final ImageView APPEARING_LOGO;
    private final Button CONNECTION_SETUP_SEND_BUTTON;
    private final ToggleGroup CONNECTION_TOGGLE_GROUP;

    private GUIConnectionSetupView() {
        try {
            SCENE_ROOT = new FXMLLoader(GUIView.class.getResource("/Client/fxml/connection_setup.fxml")).load();
        } catch (IOException e) {
            throw new RuntimeException(e); //Should never happen
        }
        CONNECTION_TITLE_SCREEN_GAME_LOGO = (ImageView) SCENE_ROOT.lookup("#connectionTitleScreenGameLogo");
        CONNECTION_SETUP_BOX = (VBox) SCENE_ROOT.lookup("#connectionSetupBox");
        APPEARING_LOGO = (ImageView) SCENE_ROOT.lookup("#appearingLogo");
        CONNECTION_SETUP_SEND_BUTTON = (Button) SCENE_ROOT.lookup("#connectionSetupSendButton");

        CONNECTION_TOGGLE_GROUP =
                ((RadioButton) ((HBox) SCENE_ROOT.lookup("#connectionTechnologySetupBox")).getChildren().getFirst())
                        .getToggleGroup();
    }

    public static GUIConnectionSetupView getInstance() {
        if (connectionSetupController == null) {
            connectionSetupController = new GUIConnectionSetupView();
        }
        return connectionSetupController;
    }

    public ToggleGroup getConnectionChoice() {
        return CONNECTION_TOGGLE_GROUP;
    }

    @Override
    public void connectionSetupScreen() {
        CONNECTION_TITLE_SCREEN_GAME_LOGO.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/Client/images/only_center_logo_no_bg.png"))));
        CONNECTION_TITLE_SCREEN_GAME_LOGO.setSmooth(true);
        CONNECTION_TITLE_SCREEN_GAME_LOGO.setFitWidth(screenSizes.getX() * 40 / 100);
        CONNECTION_TITLE_SCREEN_GAME_LOGO.setFitHeight(screenSizes.getX() * 40 / 100);

        CONNECTION_TITLE_SCREEN_GAME_LOGO.setTranslateX(0);
        CONNECTION_TITLE_SCREEN_GAME_LOGO.setTranslateY(0);
        CONNECTION_TITLE_SCREEN_GAME_LOGO.relocate(
                (screenSizes.getX() - CONNECTION_TITLE_SCREEN_GAME_LOGO.getFitWidth()) / 2,
                screenSizes.getY() * 5 / 100
        );

        CONNECTION_SETUP_BOX.setMaxSize(screenSizes.getX() / 4, 534);
        CONNECTION_SETUP_BOX.relocate(screenSizes.getX() * 9 / 16, (screenSizes.getY() - CONNECTION_SETUP_BOX.getMaxHeight()) / 2);

        CONNECTION_TOGGLE_GROUP.selectToggle(CONNECTION_TOGGLE_GROUP.getToggles().getFirst());

        CONNECTION_SETUP_SEND_BUTTON.setPrefSize(CONNECTION_SETUP_BOX.getMaxWidth(), 50);
        CONNECTION_SETUP_SEND_BUTTON.setOnMouseClicked(event -> connectionLoadingScreen());

        TranslateTransition centerLogoTransition = new TranslateTransition(Duration.millis(2000));
        centerLogoTransition.setNode(CONNECTION_TITLE_SCREEN_GAME_LOGO);
        centerLogoTransition.setInterpolator(Interpolator.EASE_BOTH);
        centerLogoTransition.setToX(screenSizes.getX() * (5.0 / 100 + 1.0 / 16) - CONNECTION_TITLE_SCREEN_GAME_LOGO.getLayoutX());
        centerLogoTransition.setToY((screenSizes.getY() - CONNECTION_TITLE_SCREEN_GAME_LOGO.getFitHeight()) / 2 - CONNECTION_TITLE_SCREEN_GAME_LOGO.getLayoutY());

        APPEARING_LOGO.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/Client/images/transparent_game_logo.png"))));
        APPEARING_LOGO.setSmooth(true);
        APPEARING_LOGO.setOpacity(0.0);
        APPEARING_LOGO.setFitWidth(screenSizes.getX() * 40 / 100);
        APPEARING_LOGO.setFitHeight(screenSizes.getX() * 40 / 100);

        APPEARING_LOGO.setTranslateX(0);
        APPEARING_LOGO.setTranslateY(0);
        APPEARING_LOGO.relocate(
                (screenSizes.getX() - APPEARING_LOGO.getFitWidth()) / 2,
                screenSizes.getY() * 5 / 100
        );

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000));
        fadeTransition.setNode(APPEARING_LOGO);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition appearingLogoTransition2 = new TranslateTransition(Duration.millis(2000));
        appearingLogoTransition2.setNode(APPEARING_LOGO);
        appearingLogoTransition2.setInterpolator(Interpolator.EASE_BOTH);
        appearingLogoTransition2.setByX(screenSizes.getX() * (5.0 / 100 + 1.0 / 16) - APPEARING_LOGO.getLayoutX());
        appearingLogoTransition2.setByY((screenSizes.getY() - CONNECTION_TITLE_SCREEN_GAME_LOGO.getFitHeight()) / 2 - APPEARING_LOGO.getLayoutY());

        FadeTransition connectionBoxTransition = new FadeTransition(Duration.millis(1000));
        connectionBoxTransition.setDelay(Duration.millis(1000));
        connectionBoxTransition.setNode(CONNECTION_SETUP_BOX);
        connectionBoxTransition.setFromValue(0.0);
        connectionBoxTransition.setToValue(1);
        connectionBoxTransition.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition movement = new ParallelTransition(centerLogoTransition, fadeTransition, appearingLogoTransition2, connectionBoxTransition);

        stage.getScene().setRoot(SCENE_ROOT);
        movement.play();
    }
}
