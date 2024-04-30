package it.polimi.ingsw.gc12;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;
import javafx.application.Platform;
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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController extends View {

    public static HelloController SINGLETON_GUI_INSTANCE = null;

    Parent root;
    Scene scene;
    Stage stage;
    ObservableList<String> languageList = FXCollections.observableArrayList("Italiano", "English");
    ObservableList<String> connectionList = FXCollections.observableArrayList("Socket", "RMI");

    @FXML
    Label StatusLabel;

    @FXML
    TextField nicknameField;

    @FXML
    Button startButton;

    @FXML
    ComboBox<String> language;

    @FXML
    ComboBox<String> connection;

    @FXML
    Label languageError;
    @FXML
    private Button show;

    @FXML
    TreeView newGame;

    @FXML
    Button join;

    public HelloController() {

    }

    public static HelloController getInstance() {
        if (SINGLETON_GUI_INSTANCE == null)
            SINGLETON_GUI_INSTANCE = new HelloController();
        return SINGLETON_GUI_INSTANCE;
    }

    @Override
    public void printError(Throwable t) {
        //TODO: popup con l'exception
    }

    @Override
    public void titleScreen() {
    }

    @FXML
    public void keyPressed(ActionEvent event) {
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Language.fxml"));
        Parent root = loader.load(); // Carica il file FXML e ottiene il root

        // Ottieni lo stage corrente e imposta la nuova scena
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        language = (ComboBox<String>) loader.getNamespace().get("language");
        language.setPromptText("Select a Language");
        language.setItems(languageList);
    }

    @FXML
    private void selectConnection(ActionEvent event) throws IOException {
        //TODO: manca il retrieve della lingua e la decisione

        if (language.valueProperty().get() == null) {
            languageError.setText("Selezionare prima una lingua");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("Connection.fxml"));
        Parent root = loader.load(); // Carica il file FXML e ottiene il root

        // Ottieni lo stage corrente e imposta la nuova scena
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        connection = (ComboBox<String>) loader.getNamespace().get("connection");
        connection.setValue("Select a Connection");
        connection.setItems(connectionList);
    }

    @FXML
    protected void newPane(ActionEvent event) throws IOException {
        //Before changing scene, we notify the chosen comm technology to the controller so that it initializes it
        ClientController.getInstance().setCommunicationTechnology(connection.valueProperty().get());

        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @FXML
    protected void Yes(ActionEvent event) throws IOException {
        if (nicknameField.getText().length() >= 5) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Download.fxml"));
            Parent root = loader.load(); // Carica il file FXML e ottiene il root

            // Ottieni lo stage corrente e imposta la nuova scena
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1800, 850);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

            Label downloadLabel = (Label) loader.getNamespace().get("download");

            if (downloadLabel != null) {
                downloadLabel.setText("Ciao: " + nicknameField.getText() + " - Attendi la fine del caricamento");
            } else {
                System.out.println("Label non trovata nel FXML.");
            }

        } else if (nicknameField.getText().length() < 5 && !nicknameField.getText().isEmpty()) {
            StatusLabel.setText("Nickname troppo corto, almeno 5 caratteri");
        } else {
            StatusLabel.setText("Inserire prima un nickname");
        }

        new Thread(() -> ClientController.getInstance().viewState.connect(nicknameField.getText()));
    }


    @FXML
    protected void No() throws IOException {
        StatusLabel.setText("Status: Not logged in");
    }

    @Override
    public void connectedConfirmation() {
    }

    @Override
    public void lobbyScreen() {
        Platform.runLater(() -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Third.fxml"));
            Parent root = null; // Carica il file FXML e ottiene il root
            try {
                root = loader.load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Stage oldStage = stage;
            stage = new Stage();
            oldStage.close();
            Scene scene = new Scene(root, 1800, 850);
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
        });
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

    public void BackToTitleScreen(ActionEvent event) throws IOException {
        // Carica il FXML e ottiene il root node correttamente
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("First.fxml"));
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
        Parent root = FXMLLoader.load(getClass().getResource("Fourth.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void ChangeNickname(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Second.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    public void LeaveGame(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Third.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, 1800, 850);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }
}