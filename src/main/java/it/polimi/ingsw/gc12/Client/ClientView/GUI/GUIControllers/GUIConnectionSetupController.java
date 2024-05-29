package it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Objects;

public class GUIConnectionSetupController extends GUIView {

    static Parent sceneRoot = sceneRoots.get("connection_setup");

    static AnchorPane connectionTitleScreenBox = (AnchorPane) sceneRoot.lookup("#connectionTitleScreenBox");

    static ImageView connectionTitleScreenGameLogo = (ImageView) connectionTitleScreenBox.lookup("#connectionTitleScreenGameLogo");

    static VBox connectionSetupBox = (VBox) sceneRoot.lookup("#connectionSetupBox");

    static TextField nicknameField = (TextField) connectionSetupBox.lookup("#nicknameField");

    static TextField addressField = (TextField) connectionSetupBox.lookup("#addressField");

    static HBox connectionTechnologySetupBox = (HBox) connectionSetupBox.lookup("#connectionTechnologySetupBox");

    static Button connectionSetupSendButton;

    static ImageView appearingLogo = (ImageView) connectionTitleScreenBox.lookup("#appearingLogo");

    static ToggleGroup connection;

    public static void connectionSetupScreen() {
        connectionTitleScreenGameLogo.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/images/only_center_logo_no_bg.png"))));
        connectionTitleScreenGameLogo.setFitWidth(650);
        connectionTitleScreenGameLogo.setFitHeight(650);
        connectionTitleScreenGameLogo.setPreserveRatio(true);

        connectionTitleScreenGameLogo.relocate(
                (screenSizes.getX() - appearingLogo.getFitWidth()) / 2,
                screenSizes.getY() * 5 / 100
        );

        connectionSetupBox.setPrefSize(screenSizes.getX() / 4, screenSizes.getY() / 2);

        connectionTechnologySetupBox.getChildren().clear();

        //ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");
        RadioButton socket = new RadioButton("Socket");
        socket.getStyleClass().add("titleScreenLabel");
        RadioButton rmi = new RadioButton("RMI");
        rmi.getStyleClass().add("titleScreenLabel");
        GUIConnectionSetupController.connection = new ToggleGroup();
        GUIConnectionSetupController.connection.getToggles().addAll(socket, rmi);
        GUIConnectionSetupController.connection.selectToggle(socket);

        connectionTechnologySetupBox.getChildren().addAll(socket, rmi);

        connectionSetupSendButton = new Button("Inizia a scrivere il tuo manoscritto!");
        connectionSetupSendButton.getStyleClass().add("button");
        connectionSetupSendButton.setStyle("-fx-font-size: 15.0;");
        connectionSetupSendButton.setPrefSize(connectionSetupBox.getPrefWidth(), 10.0);
        connectionSetupSendButton.setOnMouseClicked(event -> waitingForConnection());

        connectionSetupBox.getChildren().remove(connectionSetupBox.lookup(".button"));
        connectionSetupBox.getChildren().add(connectionSetupSendButton);

        //TODO: sostituire con un popup
        /*Label error = new Label();
        error.setId("#error");
         */

        connectionSetupBox.relocate(screenSizes.getX() * 9 / 16, screenSizes.getY() / 5);

        TranslateTransition centerLogoTransition = new TranslateTransition(Duration.millis(2000));
        centerLogoTransition.setNode(connectionTitleScreenGameLogo);
        centerLogoTransition.setInterpolator(Interpolator.EASE_BOTH);
        centerLogoTransition.setToX(screenSizes.getX() * (5.0 / 100 + 1.0 / 16) - connectionTitleScreenGameLogo.getLayoutX());
        centerLogoTransition.setToY((screenSizes.getY() - connectionTitleScreenGameLogo.getFitHeight()) / 2 - connectionTitleScreenGameLogo.getLayoutY());

        appearingLogo.setImage(new Image(Objects.requireNonNull(GUIView.class.getResourceAsStream("/images/transparent_game_logo2.png"))));
        appearingLogo.setOpacity(0.0);
        appearingLogo.setFitWidth(650);
        appearingLogo.setFitHeight(650);
        appearingLogo.setPreserveRatio(true);
        appearingLogo.relocate(
                (screenSizes.getX() - appearingLogo.getFitWidth()) / 2,
                screenSizes.getY() * 5 / 100
        );

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000));
        fadeTransition.setNode(appearingLogo);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1.0);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition appearingLogoTransition2 = new TranslateTransition(Duration.millis(2000));
        appearingLogoTransition2.setNode(appearingLogo);
        appearingLogoTransition2.setInterpolator(Interpolator.EASE_BOTH);
        appearingLogoTransition2.setByX(screenSizes.getX() * (5.0 / 100 + 1.0 / 16) - appearingLogo.getLayoutX());
        appearingLogoTransition2.setByY((screenSizes.getY() - connectionTitleScreenGameLogo.getFitHeight()) / 2 - appearingLogo.getLayoutY());

        FadeTransition connectionBoxTransition = new FadeTransition(Duration.millis(1000));
        connectionBoxTransition.setDelay(Duration.millis(1000));
        connectionBoxTransition.setNode(connectionSetupBox);
        connectionBoxTransition.setFromValue(0.0);
        connectionBoxTransition.setToValue(1);
        connectionBoxTransition.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition movement = new ParallelTransition(centerLogoTransition, fadeTransition, appearingLogoTransition2, connectionBoxTransition);

        stage.getScene().setRoot(sceneRoot);

        movement.play();
    }
}
