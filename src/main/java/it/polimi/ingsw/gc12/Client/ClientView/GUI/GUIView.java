package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientGame;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Model.Player;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

//FIXME: consider removing some Platform.runLater() and restricting some of them to necessary actions only
public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    static Stage stage;
    static Map<String, Parent> sceneRoots; //screenName to associated loader

    ObservableList<Integer> maxPlayersSelector = FXCollections.observableArrayList(2, 3, 4);

    static GenericPair<Double, Double> screenSizes;

    GenericPair<Double, Double> cardSizes = new GenericPair<>(100.0, 66.0);
    GenericPair<Double, Double> clippedPaneCenter = null;
    GenericPair<Double, Double> cornerScaleFactor = new GenericPair<>(2.0 / 9, 2.0 / 5);

    //FIXME: this is probably not the correct MIME type syntax for the data we want to pass...
    DataFormat placeCardDataFormat = new DataFormat("text/genericpair<integer,side>");

    ToggleGroup connection;

    private GUIView() {
        //FIXME: is there a thread problem here in scene initialization?
        new Thread(() -> Application.launch(GUIApplication.class)).start(); //starting the GUI thread

        //Precaricamento di tutti i nodi root di tutte i file fxml
        Map<String, Parent> tmp = new HashMap<>();
        List<String> fxmlFiles = List.of(
                "title_screen",
                "connection_setup",
                "waiting_for_connection",
                "lobby_menu",
                "game_screen"
        );
        FXMLLoader sceneRootLoader;

        for (var fxmlFile : fxmlFiles) {
            sceneRootLoader = new FXMLLoader(GUIView.class.getResource("/fxml/" + fxmlFile + ".fxml"));
            try {
                tmp.put(fxmlFile, sceneRootLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        sceneRoots = Map.copyOf(tmp);

        setScreenSizes();
    }

    public static GUIView getInstance() {
        if (SINGLETON_GUI_INSTANCE == null)
            SINGLETON_GUI_INSTANCE = new GUIView();
        return SINGLETON_GUI_INSTANCE;
    }

    private void setScreenSizes() {
        screenSizes = new GenericPair<>(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void printError(Throwable t) {
        //TODO: popup con l'exception
        Platform.runLater(() -> {
            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

            //Popup error
            Popup errorPopup = new Popup();

            VBox popupErrorBox = new VBox(10);
            popupErrorBox.setAlignment(Pos.CENTER);

            Label error = new Label();
            error.setAlignment(Pos.CENTER);
            error.setTextAlignment(TextAlignment.CENTER);
            // Button okError = new Button("Ok"); // se si ri-aggiunge bottone allora metterlo anche in addAll

            popupErrorBox.getChildren().add(error);
            errorPopup.getContent().addAll(popupErrorBox);

            errorPopup.setHeight(500);
            errorPopup.setWidth(700);
            popupErrorBox.setStyle(style);

            errorPopup.setAutoFix(true);
            errorPopup.setAutoHide(true);
            errorPopup.setHideOnEscape(true);

            error.setText(t.getMessage());
            errorPopup.show(stage);
        });
    }

    @Override
    public void titleScreen() {
        Platform.runLater(() -> {
            //TODO: maybe prima aggiungere schermate loghi
            Parent root = sceneRoots.get("title_screen");
            stage.getScene().setRoot(root);

            ImageView cranioCreationsLogo = new ImageView(String.valueOf(GUIView.class.getResource("/images/cranio_creations_logo_no_bg.png")));
            cranioCreationsLogo.setFitWidth(650);
            cranioCreationsLogo.setPreserveRatio(true);
            cranioCreationsLogo.setOpacity(0.0);

            ((AnchorPane) root).getChildren().add(cranioCreationsLogo);
            cranioCreationsLogo.relocate(
                    (screenSizes.getX() - cranioCreationsLogo.getFitWidth()) / 2,
                    screenSizes.getY() * 5 / 100
            );

            FadeTransition logoTransition = new FadeTransition(Duration.millis(3000));
            logoTransition.setNode(cranioCreationsLogo);
            logoTransition.setDelay(Duration.millis(2000));
            logoTransition.setFromValue(0);
            logoTransition.setToValue(1);
            logoTransition.setCycleCount(2);
            logoTransition.setAutoReverse(true);
            logoTransition.setOnFinished((event -> cranioCreationsLogo.setVisible(false)));

            AnchorPane titleScreenBox = (AnchorPane) root.lookup("#titleScreenBox");
            titleScreenBox.setPrefSize(screenSizes.getX(), screenSizes.getY());
            titleScreenBox.setOpacity(0.0);

            ImageView titleScreenGameLogo = new ImageView(String.valueOf(GUIView.class.getResource("/images/only_center_logo_no_bg.png")));
            titleScreenGameLogo.setFitWidth(650);
            titleScreenGameLogo.setFitHeight(650);
            titleScreenGameLogo.setPreserveRatio(true);
            titleScreenGameLogo.setId("titleScreenLogo");

            titleScreenBox.getChildren().add(titleScreenGameLogo);
            titleScreenGameLogo.relocate(
                    (screenSizes.getX() - titleScreenGameLogo.getFitWidth()) / 2,
                    screenSizes.getY() * 5 / 100
            );

            FadeTransition backgroundTransition = new FadeTransition(Duration.millis(6000));
            backgroundTransition.setNode(titleScreenBox);
            backgroundTransition.setFromValue(0);
            backgroundTransition.setToValue(1);

            Label titleScreenPrompt = new Label("Premi INVIO per iniziare");
            titleScreenPrompt.setId("titleScreenPrompt");
            titleScreenPrompt.setOnMouseClicked((event -> ClientController.getInstance().viewState.keyPressed()));
            titleScreenPrompt.setOnKeyPressed((event -> ClientController.getInstance().viewState.keyPressed()));
            titleScreenPrompt.setPrefSize(500, 25);

            FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000));
            fadeTransition.setNode(titleScreenPrompt);
            fadeTransition.setFromValue(1.0);
            fadeTransition.setToValue(0.1);
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

            //FIXME: ci sono ancora i 2000ms di delay quando clicco qua prima che diventi STOPPED...
            titleScreenGameLogo.setOnMouseClicked((event) -> {
                if (!backgroundTransition.getStatus().equals(Animation.Status.STOPPED)) {
                    titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration()));
                    event.consume();
                }
            });
            titleScreenGameLogo.setOnKeyPressed((event) -> {
                if (!backgroundTransition.getStatus().equals(Animation.Status.STOPPED)) {
                    titleScreenTransition.jumpTo(logoTransition.getTotalDuration().add(backgroundTransition.getTotalDuration()));
                    event.consume();
                }
            });

            titleScreenTransition.play();

            titleScreenBox.getChildren().add(titleScreenPrompt);
            titleScreenPrompt.relocate(
                    (screenSizes.getX() - titleScreenPrompt.getPrefWidth()) / 2,
                    screenSizes.getY() * 85 / 100
            );
        });
    }

    @Override
    public void connectToServerScreen() {
        ImageView titleScreenGameLogo = (ImageView) stage.getScene().getRoot().lookup("#titleScreenLogo");

        AnchorPane titleScreenPane = (AnchorPane) stage.getScene().getRoot().lookup("#titleScreenPane");
        AnchorPane titleScreenBox = (AnchorPane) titleScreenPane.lookup("#titleScreenBox");

        titleScreenBox.getChildren().remove(titleScreenBox.lookup("#titleScreenPrompt"));

        VBox connectionSetupBox = new VBox(50);
        connectionSetupBox.setId("connectionSetupBox");
        connectionSetupBox.setPrefSize(screenSizes.getX() * 3 / 8, screenSizes.getY() / 2);
        connectionSetupBox.setOpacity(0.0);

        Label nicknameLabel = new Label("Scegli il tuo nickname pubblico per connetterti al server: ");
        nicknameLabel.getStyleClass().add("titleScreenLabel");

        TextField nicknameField = new TextField();
        nicknameField.setId("nicknameField");
        nicknameField.setPromptText("Scrivi qui il tuo nickname... (max 10 caratteri)");
        nicknameField.getStyleClass().add("titleScreenTextField");

        Label addressLabel = new Label("Inserisci l'indirizzo IP del server: ");
        addressLabel.getStyleClass().add("titleScreenLabel");

        TextField addressField = new TextField();
        addressField.setId("addressField");
        addressField.setPromptText("localhost");
        addressField.getStyleClass().add("titleScreenTextField");

        Label connectionTechnologyPrompt = new Label("Scegli la tecnologia di comunicazione che preferisci: ");
        connectionTechnologyPrompt.getStyleClass().add("titleScreenLabel");

        HBox connectionTechnology = new HBox(200);

        //ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");
        RadioButton socket = new RadioButton("Socket");
        socket.getStyleClass().add("titleScreenLabel");
        RadioButton rmi = new RadioButton("RMI");
        rmi.getStyleClass().add("titleScreenLabel");
        this.connection = new ToggleGroup();
        this.connection.getToggles().addAll(socket, rmi);
        this.connection.selectToggle(socket);

        connectionTechnology.getChildren().addAll(socket, rmi);

        Button button = new Button("Inizia a scrivere il tuo manoscritto!");
        button.setStyle("-fx-font-size: 18;");
        button.setPrefSize(connectionSetupBox.getPrefWidth(), 10.0);
        button.setOnAction(event -> {
            try {
                waitingForConnection(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        //TODO: sostituire con un popup
        /*Label error = new Label();
        error.setId("#error");
         */

        connectionSetupBox.getChildren().addAll(nicknameLabel, nicknameField, addressLabel,
                addressField, connectionTechnologyPrompt, connectionTechnology, button);
        ((AnchorPane) stage.getScene().getRoot()).getChildren().add(connectionSetupBox);
        connectionSetupBox.relocate(screenSizes.getX() * 9 / 16, screenSizes.getY() / 5);

        TranslateTransition centerLogoTransition = new TranslateTransition(Duration.millis(2000));
        centerLogoTransition.setNode(titleScreenGameLogo);
        centerLogoTransition.setInterpolator(Interpolator.EASE_BOTH);
        centerLogoTransition.setToX(screenSizes.getX() * 5 / 100 - titleScreenGameLogo.getLayoutX());
        centerLogoTransition.setToY((screenSizes.getY() - titleScreenGameLogo.getFitHeight()) / 2 - titleScreenGameLogo.getLayoutY());

        ImageView appearingLogo = new ImageView(String.valueOf(GUIView.class.getResource("/images/transparent_game_logo2.png")));
        appearingLogo.setOpacity(0.0);
        appearingLogo.setFitWidth(650);
        appearingLogo.setFitHeight(650);
        appearingLogo.relocate(
                (screenSizes.getX() - appearingLogo.getFitWidth()) / 2,
                screenSizes.getY() * 5 / 100
        );

        ((AnchorPane) stage.getScene().getRoot().lookup("#titleScreenPane")).getChildren().add(appearingLogo);

        FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000));
        fadeTransition.setNode(appearingLogo);
        fadeTransition.setFromValue(0.0);
        fadeTransition.setToValue(1);
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH);

        TranslateTransition appearingLogoTransition2 = new TranslateTransition(Duration.millis(2000));
        appearingLogoTransition2.setNode(appearingLogo);
        appearingLogoTransition2.setInterpolator(Interpolator.EASE_BOTH);
        appearingLogoTransition2.setByX(screenSizes.getX() * 5 / 100 - appearingLogo.getLayoutX());
        appearingLogoTransition2.setByY((screenSizes.getY() - titleScreenGameLogo.getFitHeight()) / 2 - appearingLogo.getLayoutY());

        FadeTransition connectionBoxTransition = new FadeTransition(Duration.millis(1000));
        connectionBoxTransition.setDelay(Duration.millis(1000));
        connectionBoxTransition.setNode(connectionSetupBox);
        connectionBoxTransition.setFromValue(0.0);
        connectionBoxTransition.setToValue(1);
        connectionBoxTransition.setInterpolator(Interpolator.EASE_BOTH);

        ParallelTransition movement = new ParallelTransition(centerLogoTransition, fadeTransition, appearingLogoTransition2, connectionBoxTransition);
        movement.play();
    }

    @FXML
    protected void waitingForConnection(ActionEvent event) throws IOException {
        AnchorPane titleScreenPane = (AnchorPane) stage.getScene().getRoot().lookup("#titleScreenPane");
        VBox connectionSetupBox = (VBox) titleScreenPane.lookup("#connectionSetupBox");

        TextField nicknameField = (TextField) connectionSetupBox.lookup("#nicknameField");
        //Label error = ((Label) stage.getScene().getRoot().lookup("#error"));
        TextField addressField = (TextField) connectionSetupBox.lookup("#addressField");

        if (nicknameField.getText().isEmpty()) {
            //error.setText("Inserire un nickname prima di proseguire");
            return;
        }

        if (addressField.getText().isEmpty()) {
            addressField.setText("localhost");
        }

        Parent root = sceneRoots.get("waiting_for_connection"); // Carica il file FXML e ottiene il root
        stage.getScene().setRoot(root);

        Label downloadLabel = (Label) root.lookup("#download");
        ProgressIndicator progressIndicator = (ProgressIndicator) root.lookup("#progress");
        downloadLabel.setStyle("-fx-font-size: 30");

        downloadLabel.relocate((screenSizes.getX() - downloadLabel.getPrefWidth()) / 2, screenSizes.getY() * 0.45);
        progressIndicator.relocate((screenSizes.getX() - progressIndicator.getPrefWidth()) / 2, screenSizes.getY() * 0.55);

        // Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ClientController.getInstance().viewState.connect(
                addressField.getText(), ((RadioButton) connection.getSelectedToggle()).getText(), nicknameField.getText())
        ).start();
    }

    @Override
    public void connectedConfirmation() {
        //TODO: maybe consider deleting this for TUI also?
    }

    @Override
    public void lobbyScreen() {
        Platform.runLater(() -> {
            Parent root = sceneRoots.get("lobby_menu");

            Label profile = (Label) root.lookup("#profile");

            profile.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());
            profile.setTextAlignment(TextAlignment.CENTER);
            profile.setAlignment(Pos.TOP_LEFT);

            Button button = (Button) root.lookup("#BackTitleButton");
            button.setOnAction(event -> ClientController.getInstance().viewState.quit());

            ScrollPane lobbiesPane = (ScrollPane) root.lookup("#lobbiesPane");
            VBox lobbiesList = new VBox(10);
            lobbiesList.setPadding(new Insets(10));
            lobbiesList.setAlignment(Pos.TOP_CENTER);

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var lobby : ClientController.getInstance().viewModel.getLobbies().entrySet()) {
                lobbiesList.getChildren().add(createLobbyListElement(lobby.getKey(), lobby.getValue()));
            }

            lobbiesPane.setContent(lobbiesList);

            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

            Button lobby = (Button) root.lookup("#CreateGameButton");
            lobby.setOnAction(event -> {
                // New lobby Popup
                Popup lobbyPopup = new Popup();

                VBox lobbyCreationPopupBox = (VBox) root.lookup("#lobbyCreationPopupBox");

                ComboBox<Integer> maxPlayers = (ComboBox<Integer>) lobbyCreationPopupBox.lookup("#maxPlayersInput");
                maxPlayers.setValue(2);
                maxPlayers.setItems(maxPlayersSelector);

                Button okPlayers = (Button) root.lookup("#okPlayers");

                lobbyPopup.getContent().add(lobbyCreationPopupBox);

                lobbyPopup.setHeight(500);
                lobbyPopup.setWidth(700);
                lobbyCreationPopupBox.setStyle(style);

                lobbyPopup.setAutoFix(true);
                lobbyPopup.setAutoHide(true);
                lobbyPopup.setHideOnEscape(true);

                okPlayers.setOnAction(event2 -> {
                    ClientController.getInstance().viewState.createLobby(maxPlayers.getValue());
                    lobbyCreationPopupBox.setVisible(false);
                    lobbyPopup.hide();
                });

                lobbyPopup.show(stage);
                lobbyCreationPopupBox.setVisible(true);
                lobbyPopup.centerOnScreen();
            });

            // Nickname Popup
            Popup nickPopup = new Popup();

            VBox popupNickBox = new VBox(10);
            popupNickBox.setAlignment(Pos.CENTER);

            Label nickname = new Label("Inserisci un nuovo nickname");
            TextField nicknameField = new TextField();
            Button okNicknameButton = new Button("Ok");

            popupNickBox.getChildren().addAll(nickname, nicknameField, okNicknameButton);
            nickPopup.getContent().add(popupNickBox);

            nickPopup.setHeight(500);
            nickPopup.setWidth(700);
            popupNickBox.setStyle(style);

            nickPopup.setAutoFix(true);
            nickPopup.setAutoHide(true);
            nickPopup.setHideOnEscape(true);

            Button changeNickname = (Button) root.lookup("#nicknameButton");
            changeNickname.setOnAction(event -> {
                nickPopup.show(stage);
                nickPopup.centerOnScreen();
            });

            okNicknameButton.setOnAction(event -> {
                ClientController.getInstance().viewState.setNickname(nicknameField.getText());
                nickPopup.hide();
            });

            stage.getScene().setRoot(root);
        });
    }

    @Override
    public void gameScreen() {
        Platform.runLater(() -> {
            Parent root = sceneRoots.get("game_screen");
            stage.getScene().setRoot(root);

            ClientPlayer thisPlayer = ClientController.getInstance().viewModel.getGame().getThisPlayer();

            showHand();
            showCommonPlacedCards();

            //TODO: estrarre in una funzione
            AnchorPane ownFieldPane = (AnchorPane) root.lookup("#ownFieldPane");
            ownFieldPane.setPrefSize(screenSizes.getX() * 75 / 100, screenSizes.getY() * 50 / 100);
            ownFieldPane.relocate(screenSizes.getX() * 25 / 100, screenSizes.getY() * 35 / 100);

            VBox statsBox = (VBox) root.lookup("#statsBox");

            statsBox.getChildren().clear();

            statsBox.setPrefSize(ownFieldPane.getPrefWidth() / 10, ownFieldPane.getPrefHeight());
            for (var resourceEntry : thisPlayer.getOwnedResources().entrySet()) {
                //TODO: dopo aggiungeremo le immaginette o le icone o le emoji che al momento non abbiamo
                Label resourceInfo = new Label(resourceEntry.getKey().SYMBOL + " x " + resourceEntry.getValue());
                resourceInfo.setAlignment(Pos.CENTER);
                resourceInfo.setPrefSize(statsBox.getPrefWidth(), statsBox.getPrefHeight());
                resourceInfo.setStyle("-fx-font-size: 15px; -fx-font-family: 'Bell MT'; -fx-background-color: #f0f0f0; -fx-border-color: #D50A0AFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                statsBox.getChildren().add(resourceInfo);
            }

            ScrollPane ownFieldScrollPane = (ScrollPane) ownFieldPane.lookup("#ownFieldScrollPane");
            ownFieldScrollPane.setPannable(true);
            ownFieldScrollPane.setPrefSize(ownFieldPane.getPrefWidth() * 9 / 10, ownFieldPane.getPrefHeight());
            drawField(ownFieldScrollPane, thisPlayer, true);

            Button zoomedOwnFieldButton = (Button) ownFieldPane.lookup("#zoomedOwnFieldButton");
            zoomedOwnFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            zoomedOwnFieldButton.setOnMouseClicked((event) -> showField(thisPlayer));

            Button centerOwnFieldButton = (Button) ownFieldPane.lookup("#centerOwnFieldButton");
            centerOwnFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            centerOwnFieldButton.setOnMouseClicked((event) -> {
                ownFieldScrollPane.setHvalue((ownFieldScrollPane.getHmax() + ownFieldScrollPane.getHmin()) / 2);
                ownFieldScrollPane.setVvalue((ownFieldScrollPane.getVmax() + ownFieldScrollPane.getVmin()) / 2);
            });

            ownFieldScrollPane.relocate(ownFieldPane.getPrefWidth() / 10, 0);
            zoomedOwnFieldButton.relocate(ownFieldPane.getPrefWidth() - 50, 20);
            centerOwnFieldButton.relocate(ownFieldPane.getPrefWidth() - 50, 60);
            showOpponentsFieldsMiniaturized();

            Button scoreboardButton = (Button) root.lookup("#scoreboardButton");
            scoreboardButton.setOnMouseClicked((event) -> showScoreboard());
            scoreboardButton.relocate(screenSizes.getX() * 10 / 100, screenSizes.getY() * 10 / 100);
            scoreboardButton.toFront();

            Button leaveButton = (Button) root.lookup("#leaveButton");
            leaveButton.setOnMouseClicked((event) -> ClientController.getInstance().viewState.quit());
            leaveButton.relocate(screenSizes.getX() * 90 / 100, screenSizes.getY() * 90 / 100);
            leaveButton.toFront();

            Button chatButton = (Button) root.lookup("#chatButton");
            chatButton.setOnMouseClicked((event) -> showChat()); //FIXME: make toggleChat()?
            chatButton.relocate(screenSizes.getX() * 95 / 100, screenSizes.getY() * 95 / 100);
            chatButton.toFront();
        });
    }

    private void showScoreboard() {
        Platform.runLater(() -> {
            AnchorPane scoreboardPane = (AnchorPane) stage.getScene().lookup("#scoreboardPane");
            scoreboardPane.setPrefSize(screenSizes.getY() * 50 / 100 / 2, screenSizes.getY() * 50 / 100);
            scoreboardPane.setStyle("-fx-background-image: url('/images/scoreboard.png'); -fx-background-size: stretch;");

            Button hideScoreboardButton = (Button) scoreboardPane.lookup("#hideScoreboardButton");
            hideScoreboardButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            hideScoreboardButton.setOnMouseClicked((event) -> scoreboardPane.setVisible(false));

            hideScoreboardButton.relocate(scoreboardPane.getPrefWidth(), 0);

            //TODO: ???????
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            scoreboardPane.setOnMousePressed((event) -> {
                xOffset.set(scoreboardPane.getLayoutX() - event.getScreenX());
                yOffset.set(scoreboardPane.getLayoutY() - event.getScreenY());
                scoreboardPane.toFront();
            });

            scoreboardPane.setOnMouseDragged((event) -> {
                scoreboardPane.relocate(event.getScreenX() + xOffset.get(), event.getScreenY() + yOffset.get());
            });

            //TODO: Find correct coordinates of each point cell
            ArrayList<GenericPair<Double, Double>> relativeOffsetScaleFactors = new ArrayList<>();

            // relative coordinates
            relativeOffsetScaleFactors.add(new GenericPair<>(0.187, 0.8918)); // 0
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.8915)); // 1
            relativeOffsetScaleFactors.add(new GenericPair<>(0.658, 0.8915)); // 2
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.783)); // 3
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.783)); // 4
            relativeOffsetScaleFactors.add(new GenericPair<>(0.305, 0.783)); // 5
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.783)); // 6
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.6755)); // 7
            relativeOffsetScaleFactors.add(new GenericPair<>(0.303, 0.6755)); // 8
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.6755)); // 9
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.6755)); // 10
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.568)); // 11
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.568)); // 12
            relativeOffsetScaleFactors.add(new GenericPair<>(0.303, 0.568)); // 13
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.568)); // 14
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.4605)); // 15
            relativeOffsetScaleFactors.add(new GenericPair<>(0.303, 0.4605)); // 16
            relativeOffsetScaleFactors.add(new GenericPair<>(0.54, 0.4605)); // 17
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.4605)); // 18
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.353)); // 19
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.30)); // 20
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.353)); // 21
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.245)); // 22
            relativeOffsetScaleFactors.add(new GenericPair<>(0.069, 0.1375)); // 23
            relativeOffsetScaleFactors.add(new GenericPair<>(0.205, 0.05)); // 24
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.03)); // 25
            relativeOffsetScaleFactors.add(new GenericPair<>(0.639, 0.05)); // 26
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.1375)); // 27
            relativeOffsetScaleFactors.add(new GenericPair<>(0.775, 0.245)); // 28
            relativeOffsetScaleFactors.add(new GenericPair<>(0.422, 0.1623)); // 29

            ClientGame thisGame = ClientController.getInstance().viewModel.getGame();

            /*for(var player : thisGame.getPlayers().stream().sorted(Comparator.comparingInt(ClientPlayer::getPoints)).toList().reversed()) {
                GenericPair<Double, Double> scaleFactor = relativeOffsetScaleFactors.get(player.getPoints());
                ImageView token = new ImageView(String.valueOf(GUIView.class.getResource("/images/misc/" + player.getColor().name().toLowerCase())));
                token.setFitHeight(240);
                token.setPreserveRatio(true);

                scoreboardPane.getChildren().add(token);
                token.relocate(scoreboardPane.getPrefWidth() * scaleFactor.getX(),
                         scoreboardPane.getPrefHeight() * scaleFactor.getY() + stackOffset * token.getFitHeight() / stdHeight);
            }*/

            /*
            GenericPair<Double, Double> scaleFactorCircle = relativeOffsetScaleFactors.get(0);
            Circle circle = new Circle();
            circle.setRadius((230 * scoreboardPane.getPrefWidth() / 1575) / 2);
            circle.setFill(Color.RED);

            scoreboardPane.getChildren().add(circle);
            circle.relocate(scoreboardPane.getPrefWidth() * scaleFactorCircle.getX(),
                    scoreboardPane.getPrefHeight() * scaleFactorCircle.getY());
             */

            //FIXME: we need to clear all the previous token, but this also clears the hideScoreboardButton...
            //scoreboardPane.getChildren().clear();

            for (int i = 1; i < 30; i++) {
                GenericPair<Double, Double> scaleFactor = relativeOffsetScaleFactors.get(i);
                ImageView token = new ImageView(String.valueOf(GUIView.class.getResource("/images/misc/red.png")));
                token.setFitWidth(248 * scoreboardPane.getPrefWidth() / 1575);
                token.setPreserveRatio(true);

                scoreboardPane.getChildren().add(token);
                token.relocate(scoreboardPane.getPrefWidth() * scaleFactor.getX(),
                        scoreboardPane.getPrefHeight() * scaleFactor.getY());
            }

            scoreboardPane.setVisible(true);
            scoreboardPane.toFront();
        });
    }

    @Override
    public void updateNickname() {
        Platform.runLater(() -> {
            Label profile = (Label) sceneRoots.get("lobby_menu").lookup("#profile");
            profile.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());
        });
    }

    public OverlayPopup drawOverlayPopup(Pane popupContent, boolean isCloseable) {
        OverlayPopup overlayPopup = new OverlayPopup();
        //TODO: aggiungere per quanto possibile gli elementi dei popup all'fxml?

        AnchorPane content = new AnchorPane();
        content.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
        content.getChildren().add(popupContent);

        if (isCloseable) {
            Button XButton = new Button("X");
            XButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            XButton.setOnMouseClicked((event) -> overlayPopup.hide());

            content.getChildren().add(XButton);
            XButton.relocate(content.getPrefWidth() - 50, 20);
        }

        overlayPopup.getContent().add(content);
        overlayPopup.setWidth(7);
        return overlayPopup;
    }

    //TODO: eventually remove boolean from signature?
    private void drawField(ScrollPane fieldPane, ClientPlayer player, boolean isInteractive) {
        fieldPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fieldPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        //TODO: add background color or texture or image?
        //TODO: resize this pane based on how many cards have been played and center the field on it? or not?
        double clippedPaneWidth = 4000;
        double clippedPaneHeight = 4000 * cardSizes.getY() / cardSizes.getX();
        AnchorPane clippedPane = new AnchorPane();
        clippedPane.setPrefSize(clippedPaneWidth, clippedPaneHeight);
        clippedPane.setCenterShape(true);

        clippedPaneCenter = new GenericPair<>(clippedPaneWidth / 2, clippedPaneHeight / 2);
        for (var cardEntry : player.getPlacedCards().sequencedEntrySet()) {
            //TODO: maybe zoom cards here in this popup?
            ImageView cardImage = new ImageView(String.valueOf(GUIView.class.getResource(cardEntry.getValue().getX().GUI_SPRITES.get(cardEntry.getValue().getY()))));

            //FIXME: correct this: it is needed to get this later, but which size?
            // or maybe later when needed use cardSizes like this, after having decided if values are correct
            cardImage.setFitHeight(cardSizes.getY());
            cardImage.setFitWidth(cardSizes.getX());
            cardImage.setPreserveRatio(true);

            clippedPane.getChildren().add(cardImage);

            cardImage.relocate(
                    clippedPaneCenter.getX() - cardImage.getFitWidth() / 2 + cardImage.getFitWidth() * (1 - cornerScaleFactor.getX()) * cardEntry.getKey().getX(),
                    clippedPaneCenter.getY() - cardImage.getFitHeight() / 2 - cardImage.getFitHeight() * (1 - cornerScaleFactor.getY()) * cardEntry.getKey().getY()
            );
        }

        for (var openCorner : player.getOpenCorners()) {
            var openCornerShape = new Rectangle(cardSizes.getX(), cardSizes.getY()) {
                public final GenericPair<Integer, Integer> COORDINATES = openCorner;
            };
            openCornerShape.setFill(Color.TRANSPARENT);
            openCornerShape.setStyle("-fx-stroke: gray; -fx-stroke-width: 1; -fx-stroke-dash-array: 4 8;");
            openCornerShape.setArcWidth(10);
            openCornerShape.setArcHeight(10);

            if (isInteractive) {
                openCornerShape.setOnDragOver((event) -> {
                    //TODO: implement visual effects
                    if (event.getGestureSource() != openCornerShape && event.getDragboard().hasContent(placeCardDataFormat)) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                });

                openCornerShape.setOnDragDropped((event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE) {
                        GenericPair<Integer, Side> placeCardData = (GenericPair<Integer, Side>) event.getDragboard().getContent(placeCardDataFormat);
                        ClientController.getInstance().viewState.placeCard(
                                openCornerShape.COORDINATES,
                                placeCardData.getX(),
                                placeCardData.getY()
                        );
                        event.setDropCompleted(event.getDragboard().hasContent(placeCardDataFormat));
                    }
                });
            }

            clippedPane.getChildren().add(openCornerShape);

            openCornerShape.relocate(
                    clippedPaneCenter.getX() - cardSizes.getX() / 2 + cardSizes.getX() * (1 - cornerScaleFactor.getX()) * openCorner.getX(),
                    clippedPaneCenter.getY() - cardSizes.getY() / 2 - cardSizes.getY() * (1 - cornerScaleFactor.getY()) * openCorner.getY()
            );
        }

        fieldPane.setContent(clippedPane);
        fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
        fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
    }

    @Override
    public void showChat() {
        Platform.runLater(() -> {
            //TODO: maybe perform chatPane initialization only at the start of a game instead of everytime?
            AnchorPane chatPane = (AnchorPane) stage.getScene().lookup("#chatPane");
            chatPane.setPrefSize(screenSizes.getX() * 30 / 100, screenSizes.getY() * 70 / 100);

            //TODO: ???????
            AtomicReference<Double> xOffset = new AtomicReference<>((double) 0);
            AtomicReference<Double> yOffset = new AtomicReference<>((double) 0);

            chatPane.setOnMousePressed((event) -> {
                xOffset.set(chatPane.getLayoutX() - event.getScreenX());
                yOffset.set(chatPane.getLayoutY() - event.getScreenY());
                chatPane.toFront();
            });

            chatPane.setOnMouseDragged((event) -> {
                chatPane.relocate(event.getScreenX() + xOffset.get(), event.getScreenY() + yOffset.get());
            });

            ScrollPane chatScrollPane = (ScrollPane) stage.getScene().lookup("#chatScrollPane");
            VBox messagesBox = (VBox) chatPane.lookup("#messagesBox");
            ComboBox<String> receiverNicknameSelector = (ComboBox<String>) chatPane.lookup("#receiverSelector");
            TextField messageText = (TextField) chatPane.lookup("#messageText");
            Button hideChatButton = (Button) chatPane.lookup("#hideChatButton");
            Button sendButton = (Button) chatPane.lookup("#sendButton");

            ClientGame thisGame = ClientController.getInstance().viewModel.getGame();

            messagesBox.getChildren().clear();

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var message : thisGame.getChatLog()) {
                messagesBox.getChildren().add(createMessageElement(message));
            }

            chatScrollPane.setVvalue(chatScrollPane.getVmax());

            List<String> nicknames = thisGame.getPlayers().stream().map(Player::getNickname).collect(Collectors.toCollection(ArrayList::new));
            nicknames.addFirst("everyone");
            nicknames.remove(ClientController.getInstance().viewModel.getOwnNickname());
            ObservableList<String> receiverNicknames = FXCollections.observableList(nicknames);
            receiverNicknameSelector.setItems(receiverNicknames);
            receiverNicknameSelector.getSelectionModel().selectFirst();

            hideChatButton.setOnMouseClicked((event) -> chatPane.setVisible(false));

            sendButton.setOnMouseClicked((event) -> {
                String receiver = receiverNicknameSelector.getValue();
                String message = messageText.getText().trim();

                //FIXME: aggiungere lunghezza massima e substring anche qui? Magari aggiungerle ma più di 150 chars?
                if (receiver.equals("everyone"))
                    ClientController.getInstance().viewState.broadcastMessage(message);
                else
                    ClientController.getInstance().viewState.directMessage(receiver, message);
            });

            chatPane.setVisible(true);
            chatPane.toFront();
        });
    }

    //TODO: centrare l'opponentField sulla carta nuova appena piazzata?
    public void showOpponentsFieldsMiniaturized() {
        HBox opponentsFieldsPane = (HBox) stage.getScene().lookup("#opponentsFieldsPane");
        opponentsFieldsPane.setPrefSize(screenSizes.getX() * 75 / 100, screenSizes.getY() * 35 / 100);
        opponentsFieldsPane.relocate(screenSizes.getX() * 25 / 100, 0);

        ClientGame thisGame = ClientController.getInstance().viewModel.getGame();

        String style = "-fx-font-size: 15px; -fx-font-family: 'Bell MT'; -fx-background-color: #f0f0f0; -fx-border-color: #D50A0AFF; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;";

        opponentsFieldsPane.getChildren().clear();

        for (var player : thisGame.getPlayers().stream()
                .filter((player) -> !(player.getNickname().equals(ClientController.getInstance().viewModel.getOwnNickname()))).toList()) {
            VBox opponentInfo = new VBox(5);
            opponentInfo.setAlignment(Pos.CENTER);
            opponentInfo.setPrefSize(opponentsFieldsPane.getPrefWidth() / (thisGame.getPlayersNumber() - 1), opponentsFieldsPane.getPrefHeight());

            Label opponentName = new Label(player.getNickname());
            opponentName.setAlignment(Pos.CENTER);
            opponentName.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() / 10);
            opponentName.setStyle(style);

            HBox opponentData = new HBox(0);
            opponentData.setAlignment(Pos.CENTER);
            opponentData.setPrefSize(opponentInfo.getPrefWidth(), opponentInfo.getPrefHeight() * 9 / 10);

            VBox opponentStats = new VBox(0);
            opponentStats.setAlignment(Pos.CENTER);
            opponentStats.setPrefSize(opponentData.getPrefWidth() / 10, opponentData.getPrefHeight());
            for (var resourceEntry : player.getOwnedResources().entrySet()) {
                //TODO: dopo aggiungeremo le immaginette o le icone o le emoji che al momento non abbiamo
                Label resourceInfo = new Label(resourceEntry.getKey().SYMBOL + " x " + resourceEntry.getValue());
                resourceInfo.setAlignment(Pos.CENTER);
                resourceInfo.setPrefSize(opponentStats.getPrefWidth(), opponentStats.getPrefHeight());
                resourceInfo.setStyle(style);
                opponentStats.getChildren().add(resourceInfo);
            }

            AnchorPane opponentField = new AnchorPane();
            opponentField.setPrefSize(opponentData.getPrefWidth() * 9 / 10, opponentData.getPrefHeight());

            ScrollPane opponentScrollField = new ScrollPane();
            opponentScrollField.setPrefSize(opponentField.getPrefWidth(), opponentField.getPrefHeight());
            opponentScrollField.setPannable(true);
            drawField(opponentScrollField, player, false);

            Button zoomedOwnFieldButton = new Button("[]");
            zoomedOwnFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            zoomedOwnFieldButton.setOnMouseClicked((event) -> showField(player));

            Button centerFieldButton = new Button("+");
            centerFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            centerFieldButton.setOnMouseClicked((event) -> {
                opponentScrollField.setHvalue((opponentScrollField.getHmax() + opponentScrollField.getHmin()) / 2);
                opponentScrollField.setVvalue((opponentScrollField.getVmax() + opponentScrollField.getVmin()) / 2);
            });

            opponentField.getChildren().addAll(opponentScrollField, zoomedOwnFieldButton, centerFieldButton);
            zoomedOwnFieldButton.relocate(opponentField.getPrefWidth() - 50, 20);
            centerFieldButton.relocate(opponentField.getPrefWidth() - 50, 60);

            opponentData.getChildren().addAll(opponentStats, opponentField);
            opponentInfo.getChildren().addAll(opponentName, opponentData);
            opponentsFieldsPane.getChildren().add(opponentInfo);
        }
    }

    @Override
    public void showInitialCardsChoice() {
        Platform.runLater(() -> {
            double popupWidth = 720, popupHeight = 480;
            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

            VBox initialCardsChoiceVBox = new VBox(30);
            initialCardsChoiceVBox.setAlignment(Pos.CENTER);
            initialCardsChoiceVBox.setPrefSize(popupWidth, popupHeight);
            initialCardsChoiceVBox.setStyle(style);
            Label cardLabel = new Label("Seleziona la carta iniziale: ");

            HBox initialChoiceHBox = new HBox(30);
            initialChoiceHBox.setAlignment(Pos.CENTER);
            initialChoiceHBox.setPrefSize(initialCardsChoiceVBox.getPrefWidth(), initialCardsChoiceVBox.getPrefHeight() * 0.9);

            ClientCard initialCard = ClientController.getInstance().viewModel.getGame().getCardsInHand().getFirst();

            ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource(initialCard.GUI_SPRITES.get(Side.FRONT))));
            ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource(initialCard.GUI_SPRITES.get(Side.BACK))));

            frontCardView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
            frontCardView.setPreserveRatio(true);
            backCardView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
            backCardView.setPreserveRatio(true);

            initialChoiceHBox.getChildren().addAll(frontCardView, backCardView);
            initialCardsChoiceVBox.getChildren().addAll(cardLabel, initialChoiceHBox/*, aggiungere scritta "Scegli carta iniziale: "*/);

            OverlayPopup createdPopup = drawOverlayPopup(initialCardsChoiceVBox, false);

            frontCardView.setOnMouseClicked((event) -> {
                ClientController.getInstance().viewState.placeCard(new GenericPair<>(0, 0), 1, Side.FRONT);
                createdPopup.hide();
            });

            backCardView.setOnMouseClicked((event) -> {
                ClientController.getInstance().viewState.placeCard(new GenericPair<>(0, 0), 1, Side.BACK);
                createdPopup.hide();
            });

            createdPopup.show(stage);
        });
    }

    @Override
    public void showObjectiveCardsChoice() {
        Platform.runLater(() -> {
            double popupWidth = 720, popupHeight = 480;
            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

            VBox objectiveChoiceVBox = new VBox(30);
            objectiveChoiceVBox.setAlignment(Pos.CENTER);
            objectiveChoiceVBox.setPrefSize(popupWidth, popupHeight);
            objectiveChoiceVBox.setStyle(style);
            Label cardLabel = new Label("Seleziona la carta obiettivo segreto: ");

            HBox objectiveChoiceHBox = new HBox(30);
            objectiveChoiceHBox.setAlignment(Pos.CENTER);
            objectiveChoiceHBox.setPrefSize(objectiveChoiceVBox.getPrefWidth(), objectiveChoiceVBox.getPrefHeight() * 0.9);

            ArrayList<ClientCard> objectivesSelection = ((ChooseObjectiveCardsState) ClientController.getInstance()
                    .getCurrentState()).objectivesSelection;

            OverlayPopup createdPopup = drawOverlayPopup(objectiveChoiceVBox, false);

            for (int i = 0; i < objectivesSelection.size(); i++) {
                ClientCard objectiveCard = objectivesSelection.get(i);
                ImageView objectiveCardView = new ImageView(String.valueOf(objectiveCard.GUI_SPRITES.get(Side.FRONT)));

                objectiveCardView.setFitWidth(objectiveChoiceHBox.getPrefWidth() * 0.3);
                objectiveCardView.setPreserveRatio(true);

                int cardPosition = i;
                objectiveCardView.setOnMouseClicked((event) -> {
                    ClientController.getInstance().viewState.pickObjective(cardPosition + 1);
                    createdPopup.hide();
                });

                objectiveChoiceHBox.getChildren().add(objectiveCardView);
            }

            objectiveChoiceVBox.getChildren().addAll(cardLabel, objectiveChoiceHBox/*, aggiungere scritta "Scegli carta iniziale: "*/);

            createdPopup.show(stage);
        });
    }

    @Override
    public void showHand() {
        Platform.runLater(() -> {
            HBox handPane = (HBox) stage.getScene().lookup("#handPane");
            handPane.setPrefSize(screenSizes.getX() * 50 / 100, screenSizes.getY() * 15 / 100);
            handPane.relocate(screenSizes.getX() * 40 / 100, screenSizes.getY() * 85 / 100);
            handPane.setAlignment(Pos.CENTER);

            double handPaneHeight = handPane.getPrefHeight();
            double handPaneWidth = handPane.getPrefWidth();
            List<ClientCard> cardsInHand = ClientController.getInstance().viewModel.getGame().getCardsInHand();

            handPane.getChildren().clear();

            for (int i = 0; i < cardsInHand.size(); i++) {
                ClientCard card = cardsInHand.get(i);
                AnchorPane pane = new AnchorPane();

                pane.setPrefSize(handPaneWidth / 3, handPaneHeight);  //FIXME: Diviso 3? (cardInHand.size()?)
                pane.setStyle("-fx-background-color: darkorange;");

                //FIXME: mappa anche per gli sprite GUI come sprite TUI? altrimenti card.XXX_SPRITE
                ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.FRONT))));
                ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource(card.GUI_SPRITES.get(Side.BACK))));

                pane.getChildren().addAll(backCardView, frontCardView);
                backCardView.toBack();

                double paneChildrenHeight = pane.getHeight();
                double paneChildrenWidth = pane.getWidth();

                frontCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                frontCardView.setPreserveRatio(true);

                backCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                backCardView.setPreserveRatio(true);

                //TODO: vs setLayoutX/Y ?
                frontCardView.relocate(pane.getPrefWidth() * 0.2, pane.getPrefHeight() * 0.05);
                backCardView.relocate(pane.getPrefWidth() * 0.2, pane.getPrefHeight() * 0.05);

//                        frontCardView.setLayoutX((pane.getPrefWidth() - frontCardView.getFitWidth()) / 2);
//                        frontCardView.setLayoutY((pane.getPrefHeight() - frontCardView.getFitHeight()) / 2);
//                        backCardView.setLayoutX((pane.getPrefWidth() - backCardView.getFitWidth()) / 2);
//                        backCardView.setLayoutY((pane.getPrefHeight() - backCardView.getFitHeight()) / 2);

                backCardView.setOnMouseClicked((event) -> frontCardView.toFront());
                frontCardView.setOnMouseClicked((event) -> backCardView.toFront());

                //TODO: Complete implementation of drag-and-drop of cards with all DragEvents specified, and this is horrible...
                int inHandPosition = i + 1;

                frontCardView.setOnDragDetected((event) -> {
                    Dragboard cardDragboard = frontCardView.startDragAndDrop(TransferMode.MOVE);
                    cardDragboard.setDragView(frontCardView.getImage(), cardSizes.getX() / 2, cardSizes.getY() / 2);
                    ClipboardContent cardClipboard = new ClipboardContent();
                    cardClipboard.put(placeCardDataFormat, new GenericPair<>(inHandPosition, Side.FRONT));
                    cardDragboard.setContent(cardClipboard);
                });

                frontCardView.setOnDragDone((event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE && event.isDropCompleted()) {
                    }
                    //TODO: visually clear card from hand?
                });

                backCardView.setOnDragDetected((event) -> {
                    Dragboard cardDragboard = backCardView.startDragAndDrop(TransferMode.MOVE);
                    cardDragboard.setDragView(backCardView.getImage(), cardSizes.getX() / 2, cardSizes.getY() / 2);
                    ClipboardContent cardClipboard = new ClipboardContent();
                    cardClipboard.put(placeCardDataFormat, new GenericPair<>(inHandPosition, Side.BACK));
                    cardDragboard.setContent(cardClipboard);
                });

                backCardView.setOnDragDone((event) -> {
                    if (event.getTransferMode() == TransferMode.MOVE && event.isDropCompleted()) {
                    }
                    //TODO: visually clear card from hand?
                });

                handPane.getChildren().add(pane);
            }
        });
    }

    @Override
    public void showCommonPlacedCards() {
        Platform.runLater(() ->
        {
            //TODO: maybe GridPane and padding?
            VBox deckAndVisiblePane = (VBox) stage.getScene().lookup("#deckAndVisiblePane");
            deckAndVisiblePane.setPrefSize(screenSizes.getX() * 25 / 100, screenSizes.getY() * 25 / 100);
            deckAndVisiblePane.relocate(0, screenSizes.getY() * 75 / 100);

            ClientGame thisGame = ClientController.getInstance().viewModel.getGame();

            // HBox for resource cards
            HBox resourceHBox = (HBox) deckAndVisiblePane.lookup("#resourceHBox");
            resourceHBox.setPrefSize(deckAndVisiblePane.getPrefWidth(), deckAndVisiblePane.getPrefHeight() / 3);

            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            resourceHBox.getChildren().clear();

            ImageView resourceDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckResourceCard().GUI_SPRITES.get(Side.BACK))));

            resourceDeck.setFitWidth(cardSizes.getX());
            resourceDeck.setPreserveRatio(true);
            resourceDeck.setOnMouseClicked((event) ->
                    ClientController.getInstance().viewState.drawFromDeck("Resource")
            );
            resourceHBox.getChildren().add(resourceDeck);

            for (int i = 0; i < thisGame.getPlacedResources().length; i++) {
                if (thisGame.getPlacedResources()[i] != null) {
                    ImageView resource = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedResources()[i].GUI_SPRITES.get(Side.FRONT))));
                    resource.setFitWidth(cardSizes.getX());
                    resource.setPreserveRatio(true);

                    int finalI = i + 1;
                    resource.setOnMouseClicked((event) ->
                            ClientController.getInstance().viewState.drawFromVisibleCards("Resource", finalI)
                    );

                    resourceHBox.getChildren().add(resource);
                }
            }

            // HBox for gold cards
            HBox goldHBox = (HBox) deckAndVisiblePane.lookup("#goldHBox");
            goldHBox.setPrefSize(deckAndVisiblePane.getPrefWidth(), deckAndVisiblePane.getPrefHeight() / 3);

            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            goldHBox.getChildren().clear();

            ImageView goldDeck = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getTopDeckGoldCard().GUI_SPRITES.get(Side.BACK))));

            goldDeck.setFitWidth(cardSizes.getX());
            goldDeck.setPreserveRatio(true);
            goldDeck.setOnMouseClicked((event) ->
                    ClientController.getInstance().viewState.drawFromDeck("Gold")
            );
            goldHBox.getChildren().add(goldDeck);

            for (int i = 0; i < thisGame.getPlacedGolds().length; i++) {
                if (thisGame.getPlacedGolds()[i] != null) {
                    ImageView gold = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getPlacedGolds()[i].GUI_SPRITES.get(Side.FRONT))));
                    gold.setFitWidth(cardSizes.getX());
                    gold.setPreserveRatio(true);

                    int finalI = i + 1;
                    gold.setOnMouseClicked((event) ->
                            ClientController.getInstance().viewState.drawFromVisibleCards("Gold", finalI)
                    );

                    goldHBox.getChildren().add(gold);
                }
            }

            // HBox for objective cards
            HBox objectiveHBox = (HBox) deckAndVisiblePane.lookup("#objectiveHBox");
            objectiveHBox.setPrefSize(deckAndVisiblePane.getPrefWidth(), deckAndVisiblePane.getPrefHeight() / 3);

            //TODO. make imageView static and clear and avoid clearing children and refresh only the content
            objectiveHBox.getChildren().clear();

            for (int i = 0; i < thisGame.getCommonObjectives().length; i++) {
                if (thisGame.getCommonObjectives()[i] != null) {
                    ImageView commonObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getCommonObjectives()[i].GUI_SPRITES.get(Side.FRONT))));
                    commonObjective.setFitWidth(cardSizes.getX());
                    commonObjective.setPreserveRatio(true);

                    objectiveHBox.getChildren().add(commonObjective);
                }
            }

            if (thisGame.getOwnObjective() != null) {
                ImageView secretObjective = new ImageView(String.valueOf(GUIView.class.getResource(thisGame.getOwnObjective().GUI_SPRITES.get(Side.FRONT))));
                secretObjective.setFitWidth(cardSizes.getX());
                secretObjective.setPreserveRatio(true);

                objectiveHBox.getChildren().add(secretObjective);
            }
        });
    }

    //TODO: 1) where do you show your own current resource amount?
    //TODO: 2) add possibility do zoom in/out (hard) or add a zoom button that calls drawField, just as opponentsFields
    // do when clicking on it?
    @Override
    public void showField(ClientPlayer player) {
        Platform.runLater(() ->
        {
            AnchorPane popupContent = new AnchorPane();
            popupContent.setPrefSize(screenSizes.getX() * 70 / 100, screenSizes.getY() * 80 / 100);

            //TODO: add label with name
            // Label playerNameLabel = new Label();

            ScrollPane fieldPane = new ScrollPane();
            fieldPane.setPannable(true);
            fieldPane.setPrefSize(popupContent.getPrefWidth(), popupContent.getPrefHeight());
            //TODO: ??? fieldPane.setFitToHeight();
            drawField(fieldPane, player, false);
            popupContent.getChildren().add(/*playerNameLabel,*/ fieldPane);

            Button centerFieldButton = new Button("+");
            centerFieldButton.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #ff0000; -fx-background-radius: 5px;"); // -fx-padding: 10px 20px;");
            centerFieldButton.setOnMouseClicked((event) -> {
                fieldPane.setHvalue((fieldPane.getHmax() + fieldPane.getHmin()) / 2);
                fieldPane.setVvalue((fieldPane.getVmax() + fieldPane.getVmin()) / 2);
            });

            popupContent.getChildren().add(centerFieldButton);
            centerFieldButton.relocate(popupContent.getPrefWidth() - 50, 60);

            OverlayPopup overlayPopup = drawOverlayPopup(popupContent, true);
            overlayPopup.setX(screenSizes.getX() * 15 / 100);
            overlayPopup.setY(screenSizes.getY() * 10 / 100);
            overlayPopup.show(stage);
        });
    }

    @Override
    public void showLeaderboard(List<Triplet<String, Integer, Integer>> POINTS_STATS, boolean gameEndedDueToDisconnections) {
        Platform.runLater(() ->
        {
        });
    }


    private HBox createLobbyListElement(UUID lobbyUUID, GameLobby lobby) {
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

        // Box
        HBox lobbyBox = new HBox(100);
        lobbyBox.setPadding(new Insets(15, 12, 15, 12));
        lobbyBox.setStyle("-fx-alignment: CENTER; -fx-text-alignment: JUSTIFY; -fx-background-color: #00665C;");

        // Label giocatori
        Label playerCount = new Label(String.valueOf(lobby.getMaxPlayers()));
        playerCount.setStyle("-fx-font-size: 16px;");

        // Label nomi
        for (var player : lobby.getPlayers()) {
            Label playerName = new Label(player.getNickname());
            playerName.setStyle("-fx-font-size: 14px;");
            lobbyBox.getChildren().add(playerName);
        }

        lobbyBox.setStyle(style);
        lobbyBox.getChildren().add(playerCount);

        if (ClientController.getInstance().viewModel.getCurrentLobby() == null) {
            Button joinButton = new Button("JOIN");
            joinButton.setOnAction(e ->
                    {
                        ClientController.getInstance().viewState.joinLobby(lobbyUUID);
                    }
            );
            lobbyBox.getChildren().add(joinButton);
        } else {
            if (ClientController.getInstance().viewModel.getCurrentLobby().equals(lobby)) {
                Button leaveButton = new Button("LEAVE");
                leaveButton.setOnAction(e ->
                        {
                            ClientController.getInstance().viewState.leaveLobby();
                        }
                );
                lobbyBox.getChildren().add(leaveButton);
            }
        }

        return lobbyBox;
    }

    //FIXME: separate parameter receiver from message both in signature here, in TUI and in viewModel?
    private HBox createMessageElement(String message) {
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

        // Box
        HBox messageBox = new HBox(250);
        messageBox.setPadding(new Insets(15, 12, 15, 12));

        // Label messaggio
        Label messageLabel = new Label(String.valueOf(message));
        messageLabel.setStyle("-fx-font-size: 16px;");

        messageBox.setStyle(style);

        messageBox.getChildren().add(messageLabel);

        return messageBox;
    }
}