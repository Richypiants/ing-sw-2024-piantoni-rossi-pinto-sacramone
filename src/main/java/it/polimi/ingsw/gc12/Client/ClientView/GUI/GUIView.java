package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.GameStates.ChooseObjectiveCardsState;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientCard;
import it.polimi.ingsw.gc12.Model.ClientModel.ClientPlayer;
import it.polimi.ingsw.gc12.Model.GameLobby;
import it.polimi.ingsw.gc12.Utilities.GenericPair;
import it.polimi.ingsw.gc12.Utilities.Side;
import it.polimi.ingsw.gc12.Utilities.Triplet;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    String resourcesPath = System.getProperty("user.dir") + "/src/main/resources/";

    Stage stage;
    ObservableList<String> languageList = FXCollections.observableArrayList("Italiano", "English");
    ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");
    ObservableList<Integer> maxPlayersSelector = FXCollections.observableArrayList(2, 3, 4);

    @FXML
    TextField nicknameField;

    @FXML
    Label profile;

    @FXML
    ComboBox<String> language;

    @FXML
    ComboBox<String> connection;

    @FXML
    Label error;

    GenericPair<Double, Double> cardSizes = null;
    GenericPair<Double, Double> clippedPaneCenter = null;
    GenericPair<Double, Double> cornerScaleFactor = new GenericPair<>(2.0 / 9, 2.0 / 5);

    //FIXME: this is probably not the correct MIME type syntax for the data we want to pass...
    DataFormat placeCardDataFormat = new DataFormat("text/genericpair<integer,side>");

    private GUIView() {
    }

    public static GUIView getInstance() {
        if (SINGLETON_GUI_INSTANCE == null)
            SINGLETON_GUI_INSTANCE = new GUIView();
        return SINGLETON_GUI_INSTANCE;
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
        FXMLLoader fxmlLoader = new FXMLLoader(GUIView.class.getResource("/fxml/title_screen.fxml"));
        ImageView image = new ImageView();
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = null;
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Scene scene = new Scene(root, 1280, 720);
        stage.setScene(scene);

        stage.addEventHandler(KeyEvent.KEY_PRESSED, (event) -> {
            if (event.getCode().equals(KeyCode.F11))
                stage.setFullScreen(!stage.isFullScreen());
            //TODO: + show hint per rientrare in fullscreen
        });

        Button startButton = (Button) fxmlLoader.getNamespace().get("startButton");
        startButton.setOnAction(this::keyPressed);
        //StackPane First = (StackPane) fxmlLoader.getNamespace().get("titleScreen");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(screenHeight * 0.8, 0, 0, 0));

        // Image icon = new Image("C:/Users/jacop/Desktop/Stage.png");
        // stage.getIcons().add(icon);

        stage.setTitle("Codex Naturalis");
        stage.setFullScreen(true);
        stage.setResizable(false);
        stage.centerOnScreen();
        //FIXME: non funziona e non indirizza gli input sulla schermata... stage.requestFocus();
        stage.show();
    }

    @FXML
    public void keyPressed(ActionEvent event) {
        ClientController.getInstance().viewState.keyPressed();
    }

    @Override
    public void connectToServerScreen() {
        try {
            selection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selection() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/connection_setup.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        stage.getScene().setRoot(root);

        //TODO: al posto della lingua, far inserire indirizzo IP del server!
        ComboBox<String> language = (ComboBox<String>) fxmlLoader.getNamespace().get("language");
        language.setPromptText("Select language");
        language.setItems(languageList);

        ComboBox<String> connection = (ComboBox<String>) fxmlLoader.getNamespace().get("connection");
        connection.setValue("Select communication technology");
        connection.setItems(connectionList);

        Button button = (Button) fxmlLoader.getNamespace().get("button");
        button.setOnAction(event -> {
            try {
                waitingForConnection(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Label error = (Label) fxmlLoader.getNamespace().get("error");
        Label nicknameLabel = (Label) fxmlLoader.getNamespace().get("nicknameLabel");
        TextField nicknameField = (TextField) fxmlLoader.getNamespace().get("nicknameField");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(language, Pos.CENTER);
        StackPane.setMargin(language, new Insets(-screenHeight * 0.2, 0, 0, -screenHeight * 0.8));
        StackPane.setAlignment(connection, Pos.CENTER);
        StackPane.setMargin(connection, new Insets(-screenHeight * 0.2, 0, 0, screenHeight * 0.8));
        StackPane.setAlignment(button, Pos.CENTER);
        StackPane.setMargin(button, new Insets(screenHeight * 0.1, 0, 0, 0));
        StackPane.setAlignment(error, Pos.CENTER);
        StackPane.setMargin(error, new Insets(0, 0, 0, 0));

        StackPane.setAlignment(nicknameLabel, Pos.CENTER);
        StackPane.setMargin(nicknameLabel, new Insets(-screenHeight * 0.3, 0, 0, 0));
        StackPane.setAlignment(nicknameField, Pos.CENTER);
        StackPane.setMargin(nicknameField, new Insets(-screenHeight * 0.2, screenHeight * 0.75, 0, screenHeight * 0.75));
    }

    @FXML
    protected void waitingForConnection(ActionEvent event) throws IOException {
        if (nicknameField.getText().isEmpty()) {
            error.setText("Inserire un nickname prima di proseguire");
            return;
        }

        if (connection.valueProperty().get() == null) {
            error.setText("Selezionare una connessione prima di proseguire");
            return;
        }

        if (language.valueProperty().get() == null) {
            error.setText("Selezionare una lingua prima di proseguire");
            return;
        }

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/waiting_for_connection.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        stage.getScene().setRoot(root);

        Label downloadLabel = (Label) fxmlLoader.getNamespace().get("download");
        ProgressIndicator progressIndicator = (ProgressIndicator) fxmlLoader.getNamespace().get("progress");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(downloadLabel, Pos.CENTER);
        StackPane.setMargin(downloadLabel, new Insets(-screenHeight * 0.1, 0, 0, 0));
        StackPane.setAlignment(progressIndicator, Pos.CENTER);
        StackPane.setMargin(progressIndicator, new Insets(screenHeight * 0.1, 0, 0, 0));

        if (downloadLabel != null) {
            downloadLabel.setText("Ciao " + nicknameField.getText() + "\nStiamo caricando il Codex Naturalis");
            downloadLabel.setTextAlignment(TextAlignment.CENTER);
        } else {
            System.out.println("Label non trovata nel FXML");
        }

        // Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ClientController.getInstance().viewState.connect(connection.valueProperty().get(), nicknameField.getText())).start();
    }

    @Override
    public void connectedConfirmation() {

    }

    @Override
    public void lobbyScreen() {
        Platform.runLater(() -> {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/lobby_menu.fxml"));
            fxmlLoader.setController(ClientController.getInstance().view);
            Parent root = null;
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            profile.setText("Profile: " + ClientController.getInstance().viewModel.getOwnNickname());
            profile.setTextAlignment(TextAlignment.CENTER);
            profile.setAlignment(Pos.TOP_LEFT);

            Button button = (Button) fxmlLoader.getNamespace().get("BackTitleButton");
            button.setOnAction(event -> ClientController.getInstance().viewState.quit());

            ScrollPane lobbiesPane = (ScrollPane) fxmlLoader.getNamespace().get("lobbiesPane");
            VBox lobbiesList = new VBox(10);
            lobbiesList.setPadding(new Insets(10));
            lobbiesList.setAlignment(Pos.TOP_CENTER);

            //TODO: invece di ricrearlo ogni volta, salvarlo e updatarlo?
            for (var lobby : ClientController.getInstance().viewModel.getLobbies().entrySet()) {
                lobbiesList.getChildren().add(createLobbyListElement(lobby.getKey(), lobby.getValue()));
            }

            lobbiesPane.setContent(lobbiesList);

            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

            // New lobby Popup
            Popup lobbyPopup = new Popup();

            VBox popupLobbyBox = new VBox(10);
            popupLobbyBox.setAlignment(Pos.CENTER);

            ComboBox<Integer> maxPlayers = (ComboBox<Integer>) fxmlLoader.getNamespace().get("maxPlayersInput");
            maxPlayers.setValue(2);
            maxPlayers.setItems(maxPlayersSelector);

            Label players = (Label) fxmlLoader.getNamespace().get("players");
            Button okPlayers = (Button) fxmlLoader.getNamespace().get("okPlayers");

            popupLobbyBox.getChildren().addAll(players, maxPlayers, okPlayers);
            lobbyPopup.getContent().add(popupLobbyBox);

            lobbyPopup.setHeight(500);
            lobbyPopup.setWidth(700);
            popupLobbyBox.setStyle(style);

            lobbyPopup.setAutoFix(true);
            lobbyPopup.setAutoHide(true);
            lobbyPopup.setHideOnEscape(true);

            okPlayers.setOnAction(event -> {
                ClientController.getInstance().viewState.createLobby(maxPlayers.getValue());
                lobbyPopup.hide();
            });

            Button lobby = (Button) fxmlLoader.getNamespace().get("CreateGameButton");
            lobby.setOnAction(event -> {
                lobbyPopup.show(stage);
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

            Button changeNickname = (Button) fxmlLoader.getNamespace().get("nicknameButton");
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/game_screen.fxml"));
            fxmlLoader.setController(ClientController.getInstance().view);
            Parent root = null; // Carica il file FXML e ottiene il root
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.getScene().setRoot(root);

//            Button leaveGame = (Button) fxmlLoader.getNamespace().get("leaveButton");
//            Button chat = (Button) fxmlLoader.getNamespace().get("chatButton");
//            ImageView score = (ImageView) fxmlLoader.getNamespace().get("score");
//            ImageView objective = (ImageView) fxmlLoader.getNamespace().get("objective");
//            ImageView resource = (ImageView) fxmlLoader.getNamespace().get("resource");
//            ImageView gold = (ImageView) fxmlLoader.getNamespace().get("gold");
//
//            // Dimensione Schermo
//            double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

                    // Posizionamento
//            AnchorPane.setRightAnchor(leaveGame, 20.0);
//            AnchorPane.setBottomAnchor(leaveGame, 20.0);

            showHand();
            showCommonPlacedCards();
            showField(ClientController.getInstance().viewModel.getGame().getThisPlayer());
        });
    }

    @Override
    public void updateNickname() {

    }

    @Override
    public void updateChat() {

    }

    @Override
    public void showInitialCardsChoice() {
        Platform.runLater(() -> {
                    AnchorPane currentPane = (AnchorPane) stage.getScene().getRoot();

                    currentPane.setDisable(true);
                    AnchorPane darkening = new AnchorPane();
                    darkening.setPrefSize(currentPane.getWidth(), currentPane.getHeight());
                    darkening.setId("#darkening");
                    darkening.setStyle("-fx-background-color: black;");
                    darkening.setOpacity(0.8);
                    currentPane.getChildren().add(darkening);
                    darkening.toFront(); //FIXME: there has to be a better way...

                    //TODO: aggiungere per quanto possibile gli elementi del popup all'fxml?
                    Popup initialCardsChoicePopup = new Popup();
                    initialCardsChoicePopup.setWidth(720);
                    initialCardsChoicePopup.setHeight(480);

            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

                    VBox initialCardsChoiceVBox = new VBox(30);
                    initialCardsChoiceVBox.setAlignment(Pos.CENTER);
                    initialCardsChoiceVBox.setPrefSize(initialCardsChoicePopup.getWidth(), initialCardsChoicePopup.getHeight());
                    initialCardsChoiceVBox.setStyle(style);
                    Label cardLabel = new Label("Seleziona la carta iniziale: ");

                    HBox initialChoiceHBox = new HBox(30);
                    initialChoiceHBox.setAlignment(Pos.CENTER);
                    initialChoiceHBox.setPrefSize(initialCardsChoiceVBox.getPrefWidth(), initialCardsChoiceVBox.getPrefHeight() * 0.9);

                    ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource("/images/Gold.jpg")));
                    ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource("/images/Resource.jpg")));

                    frontCardView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
                    frontCardView.setPreserveRatio(true);
                    backCardView.setFitWidth(initialChoiceHBox.getPrefWidth() * 0.3);
                    backCardView.setPreserveRatio(true);

                    frontCardView.setOnMouseClicked((event) -> {
                        ClientController.getInstance().viewState.placeCard(new GenericPair<>(0, 0), 0, Side.FRONT);
                        initialCardsChoicePopup.hide();
                        currentPane.getChildren().remove(darkening);
                        currentPane.getChildren().remove(initialCardsChoicePopup);
                        currentPane.setDisable(false);
                    });

                    backCardView.setOnMouseClicked((event) -> {
                        ClientController.getInstance().viewState.placeCard(new GenericPair<>(0, 0), 0, Side.BACK);
                        initialCardsChoicePopup.hide();
                        currentPane.getChildren().remove(darkening);
                        currentPane.getChildren().remove(initialCardsChoicePopup);
                        currentPane.setDisable(false);
                    });

                    initialChoiceHBox.getChildren().addAll(frontCardView, backCardView);
                    initialCardsChoiceVBox.getChildren().addAll(cardLabel, initialChoiceHBox/*, aggiungere scritta "Scegli carta iniziale: "*/);
                    initialCardsChoicePopup.getContent().addAll(initialCardsChoiceVBox);

                    initialCardsChoicePopup.show(stage);
                }
        );
    }

    @Override
    public void showObjectiveCardsChoice() {
        Platform.runLater(() -> {
                    AnchorPane currentPane = (AnchorPane) stage.getScene().getRoot();

                    currentPane.setDisable(true);
                    AnchorPane darkening = new AnchorPane();
                    darkening.setPrefSize(currentPane.getWidth(), currentPane.getHeight());
                    darkening.setId("#darkening");
                    darkening.setStyle("-fx-background-color: black;");
                    darkening.setOpacity(0.8);
                    currentPane.getChildren().add(darkening);
                    darkening.toFront(); //FIXME: there has to be a better way...

                    //TODO: aggiungere per quanto possibile gli elementi del popup all'fxml?
                    Popup objectiveChoicePopup = new Popup();
                    objectiveChoicePopup.setWidth(720);
                    objectiveChoicePopup.setHeight(480);
            //objectiveChoicePopup.setAutoHide(true);

            String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-padding: 10;";

                    VBox objectiveChoiceVBox = new VBox(30);
                    objectiveChoiceVBox.setAlignment(Pos.CENTER);
                    objectiveChoiceVBox.setPrefSize(objectiveChoicePopup.getWidth(), objectiveChoicePopup.getHeight());
                    objectiveChoiceVBox.setStyle(style);
                    Label cardLabel = new Label("Seleziona la carta obiettivo segreto: ");

                    HBox objectiveChoiceHBox = new HBox(30);
                    objectiveChoiceHBox.setAlignment(Pos.CENTER);
                    objectiveChoiceHBox.setPrefSize(objectiveChoiceVBox.getPrefWidth(), objectiveChoiceVBox.getPrefHeight() * 0.9);

            ArrayList<ClientCard> objectivesSelection = ((ChooseObjectiveCardsState) ClientController.getInstance()
                    .getCurrentState()).objectivesSelection;

            for (int i = 0; i < objectivesSelection.size(); i++) {
                ClientCard objectiveCard = objectivesSelection.get(i);
                ImageView objectiveCardView = new ImageView(String.valueOf(GUIView.class.getResource("/images/Objective.jpg")));

                objectiveCardView.setFitWidth(objectiveChoiceHBox.getPrefWidth() * 0.3);
                objectiveCardView.setPreserveRatio(true);

                int cardPosition = i;
                objectiveCardView.setOnMouseClicked((event) -> {
                    ClientController.getInstance().viewState.pickObjective(cardPosition + 1);
                    objectiveChoicePopup.hide();
                    currentPane.getChildren().remove(darkening);
                    currentPane.getChildren().remove(objectiveChoicePopup);
                    currentPane.setDisable(false);
                });

                objectiveChoiceHBox.getChildren().add(objectiveCardView);
            }

                    objectiveChoiceVBox.getChildren().addAll(cardLabel, objectiveChoiceHBox/*, aggiungere scritta "Scegli carta iniziale: "*/);
                    objectiveChoicePopup.getContent().addAll(objectiveChoiceVBox);

                    objectiveChoicePopup.show(stage);
                }
        );
    }

    @Override
    public void showHand() {
        Platform.runLater(() ->
                {
                    HBox handPane = (HBox) stage.getScene().lookup("#handPane");
                    handPane.setAlignment(Pos.CENTER);

                    double handPaneHeight = handPane.getPrefHeight();
                    double handPaneWidth = handPane.getPrefWidth();
                    List<ClientCard> cardsInHand = ClientController.getInstance().viewModel.getGame().getCardsInHand();

                    handPane.getChildren().clear();

                    for (int i = 0; i < cardsInHand.size(); i++) {
                        ClientCard card = cardsInHand.get(i);
                        AnchorPane pane = new AnchorPane();

                        pane.setPrefSize(handPaneHeight, handPaneWidth / 3);  //FIXME: Diviso 3? (cardInHand.size()?)
                        pane.setStyle("-fx-background-color: darkorange;");

                        //FIXME: mappa anche per gli sprite GUI come sprite TUI? altrimenti card.XXX_SPRITE
                        ImageView frontCardView = new ImageView(String.valueOf(GUIView.class.getResource("/images/Gold.jpg")));
                        ImageView backCardView = new ImageView(String.valueOf(GUIView.class.getResource("/images/Resource.jpg")));

                        pane.getChildren().addAll(backCardView, frontCardView);
                        backCardView.toBack();

                        double paneChildrenHeight = pane.getHeight();
                        double paneChildrenWidth = pane.getWidth();

                        frontCardView.setFitWidth(pane.getPrefWidth() * 0.7);
                        frontCardView.setPreserveRatio(true);

                        backCardView.setFitWidth(pane.getPrefWidth() * 0.5);
                        backCardView.setPreserveRatio(true);

                        //TODO: vs setLayoutX/Y ?
                        frontCardView.setX(pane.getPrefWidth() * 0.2);
                        frontCardView.setY(pane.getPrefHeight() * 0.2);
                        backCardView.setX(pane.getPrefWidth() * 0.4);
                        backCardView.setY(pane.getPrefHeight() * 0.1);

//                        frontCardView.setLayoutX((pane.getPrefWidth() - frontCardView.getFitWidth()) / 2);
//                        frontCardView.setLayoutY((pane.getPrefHeight() - frontCardView.getFitHeight()) / 2);
//                        backCardView.setLayoutX((pane.getPrefWidth() - backCardView.getFitWidth()) / 2);
//                        backCardView.setLayoutY((pane.getPrefHeight() - backCardView.getFitHeight()) / 2);

                        backCardView.setOnMouseClicked((event) -> {
                            frontCardView.toBack();
                            backCardView.toFront();
                        });


                        frontCardView.setOnMouseClicked((event) -> {
                            backCardView.toBack();
                            frontCardView.toFront();
                        });

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

                        handPane.getChildren().add(pane);
                    }
                }
        );
    }

    @Override
    public void showCommonPlacedCards() {
        Platform.runLater(() ->
        {
            //TODO: maybe GridPane and padding?
            VBox deckVBox = (VBox) stage.getScene().lookup("#deckAndVisiblePane");

            //TODO. make vbox and hbox static and clear and refresh only the content
            deckVBox.getChildren().clear();

            // HBox for resource cards
            HBox resourceHBox = new HBox(10);
            resourceHBox.setAlignment(Pos.CENTER);
            resourceHBox.setPrefSize(deckVBox.getPrefWidth(), deckVBox.getPrefHeight() / 3);

            ImageView resourceDeck = new ImageView(String.valueOf(GUIView.class.getResource("/images/Resource.jpg")));
            ImageView resource1 = new ImageView(String.valueOf(GUIView.class.getResource("/images/Resource.jpg")));
            ImageView resource2 = new ImageView(String.valueOf(GUIView.class.getResource("/images/Resource.jpg")));

            resourceDeck.setFitWidth(resourceHBox.getPrefWidth() / 3);
            resourceDeck.setPreserveRatio(true);
            resourceDeck.toFront();
            resource1.setFitWidth(resourceHBox.getPrefWidth() / 3);
            resource1.setPreserveRatio(true);
            resource1.toFront();
            resource2.setFitWidth(resourceHBox.getPrefWidth() / 3);
            resource2.setPreserveRatio(true);
            resource2.toFront();

            resourceHBox.getChildren().addAll(resourceDeck, resource1, resource2);

            // HBox for gold cards
            HBox goldHBox = new HBox(20);
            goldHBox.setAlignment(Pos.CENTER);
            goldHBox.setPrefSize(deckVBox.getPrefWidth(), deckVBox.getPrefHeight() / 3);

            ImageView goldDeck = new ImageView(String.valueOf(GUIView.class.getResource("/images/Gold.jpg")));
            ImageView gold1 = new ImageView(String.valueOf(GUIView.class.getResource("/images/Gold.jpg")));
            ImageView gold2 = new ImageView(String.valueOf(GUIView.class.getResource("/images/Gold.jpg")));

            goldDeck.setFitWidth(goldHBox.getPrefWidth() / 3);
            goldDeck.setPreserveRatio(true);
            gold1.setFitWidth(goldHBox.getPrefWidth() / 3);
            gold1.setPreserveRatio(true);
            gold2.setFitWidth(goldHBox.getPrefWidth() / 3);
            gold2.setPreserveRatio(true);

            goldHBox.getChildren().addAll(goldDeck, gold1, gold2);

            // HBox for objective cards
            HBox objectiveHBox = new HBox(20);
            objectiveHBox.setAlignment(Pos.CENTER);
            objectiveHBox.setPrefSize(deckVBox.getPrefWidth(), deckVBox.getPrefHeight() / 3);

            ImageView commonObjective1 = new ImageView(String.valueOf(GUIView.class.getResource("/images/Objective.jpg")));
            ImageView commonObjective2 = new ImageView(String.valueOf(GUIView.class.getResource("/images/Objective.jpg")));
            ImageView secretObjective = new ImageView(String.valueOf(GUIView.class.getResource("/images/Objective.jpg")));

            commonObjective1.setFitWidth(objectiveHBox.getPrefWidth() / 3);
            commonObjective1.setPreserveRatio(true);
            commonObjective2.setFitWidth(objectiveHBox.getPrefWidth() / 3);
            commonObjective2.setPreserveRatio(true);
            secretObjective.setFitWidth(objectiveHBox.getPrefWidth() / 3);
            secretObjective.setPreserveRatio(true);

            objectiveHBox.getChildren().addAll(commonObjective1, commonObjective2, secretObjective);

            deckVBox.getChildren().addAll(resourceHBox, goldHBox, objectiveHBox);
        });
    }

    @Override
    public void showField(ClientPlayer player) {
        Platform.runLater(() ->
        {
            ScrollPane ownFieldPane = (ScrollPane) stage.getScene().lookup("#ownFieldPane");
            ownFieldPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            ownFieldPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            ownFieldPane.setPannable(true);

            //TODO: add background color or texture or image?
            //TODO: resize this pane based on how many cards have been played and center the field on it? or not?
            double clippedPaneWidth = 4000;
            double clippedPaneHeight = 4000 * ownFieldPane.getPrefHeight() / ownFieldPane.getPrefWidth();
            AnchorPane clippedPane = new AnchorPane();
            clippedPane.setPrefSize(clippedPaneWidth, clippedPaneHeight);
            clippedPane.setCenterShape(true);

            clippedPaneCenter = new GenericPair<>(clippedPaneWidth / 2, clippedPaneHeight / 2);
            for (var cardEntry : ClientController.getInstance().viewModel.getGame().getThisPlayer().getPlacedCards().sequencedEntrySet()) {
                ImageView cardImage = new ImageView(String.valueOf(GUIView.class.getResource("/images/Resource.jpg")));
                cardImage.setFitWidth(100);
                cardImage.setFitWidth(40); //FIXME: correct this: it is needed, but which size?
                cardImage.setPreserveRatio(true);

                //TODO: estrarre fuori
                cardSizes = new GenericPair<>(cardImage.getFitWidth(), cardImage.getFitHeight());

                clippedPane.getChildren().add(cardImage);

                cardImage.relocate(
                        clippedPaneCenter.getX() - cardImage.getFitWidth() / 2 + cardImage.getFitWidth() * (1 - cornerScaleFactor.getX()) * cardEntry.getKey().getX(),
                        clippedPaneCenter.getY() - cardImage.getFitHeight() / 2 - cardImage.getFitHeight() * (1 - cornerScaleFactor.getY()) * cardEntry.getKey().getY()
                );
            }

            for (var openCorner : ClientController.getInstance().viewModel.getGame().getThisPlayer().getOpenCorners()) {
                var openCornerShape = new Rectangle(100, 40) {
                    public final GenericPair<Integer, Integer> COORDINATES = openCorner;
                };
                openCornerShape.setStyle("-fx-fill: lightgray; -fx-stroke: black; -fx-stroke-width: 1; -fx-stroke-dash-array: 2 2;");

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

                clippedPane.getChildren().add(openCornerShape);

                openCornerShape.relocate(
                        clippedPaneCenter.getX() - cardSizes.getX() / 2 + cardSizes.getX() * (1 - cornerScaleFactor.getX()) * openCorner.getX(),
                        clippedPaneCenter.getY() - cardSizes.getY() / 2 - cardSizes.getY() * (1 - cornerScaleFactor.getY()) * openCorner.getY()
                );
            }

            /*for (int i = 0; i < 5; i++) {
                Rectangle openCornerShape = new Rectangle(60, 24);
                openCornerShape.setStyle("-fx-fill: lightgray; -fx-stroke: black; -fx-stroke-width: 1; -fx-stroke-dash-array: 2 2;");

                clippedPane.getChildren().add(openCornerShape);

                openCornerShape.relocate(
                        i * 50,
                        i * 10
                );
            }*/

            ownFieldPane.setContent(clippedPane);
        });
    }

    @Override
    public void showLeaderboard(List<Triplet<String, Integer, Integer>> POINT_STATS) {
        Platform.runLater(() ->
        {

        });
    }

    private HBox createLobbyListElement(UUID lobbyUUID, GameLobby lobby) {
        String style = "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 1; -fx-padding: 10;";

        // Box
        HBox lobbyBox = new HBox(250);
        lobbyBox.setPadding(new Insets(15, 12, 15, 12));

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

        lobbyBox.getChildren().add(playerCount);

        return lobbyBox;
    }
}