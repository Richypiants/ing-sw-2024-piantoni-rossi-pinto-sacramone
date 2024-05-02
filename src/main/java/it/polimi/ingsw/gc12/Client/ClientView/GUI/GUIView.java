package it.polimi.ingsw.gc12.Client.ClientView.GUI;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class GUIView extends View {

    private static GUIView SINGLETON_GUI_INSTANCE = null;

    Stage stage;
    ObservableList<String> languageList = FXCollections.observableArrayList("Italiano", "English");
    ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");

    @FXML
    Label statusLabel;

    @FXML
    TextField nicknameField;

    @FXML
    Button startButton;

    @FXML
    ComboBox<String> language;

    @FXML
    ComboBox<String> connection;

    @FXML
    Label error;

    @FXML
    TreeView newGame;

    @FXML
    Button join;

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
    }

    @Override
    public void titleScreen() {
        FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("/title_screen.fxml"));
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
        });

        Label Codex = (Label) fxmlLoader.getNamespace().get("Codex");
        Button startButton = (Button) fxmlLoader.getNamespace().get("startButton");
        startButton.setOnAction(this::keyPressed);
        Rectangle backgroundLabel = (Rectangle) fxmlLoader.getNamespace().get("backgroundLabel");
        StackPane First = (StackPane) fxmlLoader.getNamespace().get("First");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(Codex, Pos.CENTER);
        StackPane.setMargin(Codex, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(backgroundLabel, Pos.CENTER);
        StackPane.setMargin(backgroundLabel, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(screenHeight * 0.425, 0, 0, 0));

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
            selectLanguage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void selectLanguage() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/connection_setup.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        stage.getScene().setRoot(root);

        language = (ComboBox<String>) fxmlLoader.getNamespace().get("language");
        language.setPromptText("Select language");
        language.setItems(languageList);

        connection = (ComboBox<String>) fxmlLoader.getNamespace().get("connection");
        connection.setValue("Select communication technology");
        connection.setItems(connectionList);

        Button button = (Button) fxmlLoader.getNamespace().get("button");
        button.setOnAction(event -> {
            try {
                yes(event);
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
    protected void yes(ActionEvent event) throws IOException {
        if (nicknameField.getText().length() <= 0) {
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

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/waiting_for_connection.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
        Parent root = fxmlLoader.load(); // Carica il file FXML e ottiene il root

        stage.getScene().setRoot(root);

        Label downloadLabel = (Label) fxmlLoader.getNamespace().get("download");

        if (downloadLabel != null) {
            downloadLabel.setText("Ciao: " + nicknameField.getText() + " - Attendi la fine del caricamento");
        } else {
            System.out.println("Label non trovata nel FXML.");
        }

        //Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        new Thread(() -> ClientController.getInstance().viewState.connect(connection.valueProperty().get(), nicknameField.getText())).start();
    }

    @FXML
    protected void no() throws IOException {
        statusLabel.setText("Status: Not logged in");
    }

    @Override
    public void connectedConfirmation() {
    }

    @Override
    public void lobbyScreen() {
        //Platform.runLater(() -> {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/main_menu.fxml"));
        fxmlLoader.setController(ClientController.getInstance().view);
            Parent root = null; // Carica il file FXML e ottiene il root
            try {
                root = fxmlLoader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        stage.getScene().setRoot(root);
        //});
    }

    @Override
    public void gameScreen() {
    }

    @Override
    public void updateNickname() {

    }

    @Override
    public void updateChat() {

    }

    @Override
    public void showInitialCardsChoice() {

    }

    @Override
    public void showObjectiveCardsChoice() {

    }

    @Override
    public void showField() {

    }

    @Override
    public void showHand() {

    }

    public void BackToTitleScreen(ActionEvent event) throws IOException {
        // Carica il FXML e ottiene il root node correttamente
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/title_screen.fxml"));
        Parent root = fxmlLoader.load();  // Carica la vista FXML

        // Ottiene lo stage corrente da event source
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Crea la scena con la dimensione specificata
        Scene scene = new Scene(root, 1800, 850);

        // Accesso ai componenti nel namespace del FXMLLoader
        Label Codex = (Label) fxmlLoader.getNamespace().get("Codex");
        Button startButton = (Button) fxmlLoader.getNamespace().get("startButton");
        Rectangle backgroundLabel = (Rectangle) fxmlLoader.getNamespace().get("backgroundLabel");
        StackPane First = (StackPane) fxmlLoader.getNamespace().get("First");

        // Dimensione Schermo
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        StackPane.setAlignment(Codex, Pos.CENTER);
        StackPane.setMargin(Codex, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(backgroundLabel, Pos.CENTER);
        StackPane.setMargin(backgroundLabel, new Insets(-screenHeight * 0.2, 0, 0, 0));
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(screenHeight * 0.425, 0, 0, 0));

        // Imposta la scena e massimizza il palcoscenico
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }


    public void NewGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fourth.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/change_nickname.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/main_menu.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}