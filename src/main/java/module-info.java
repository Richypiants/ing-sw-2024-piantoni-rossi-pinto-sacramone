module it.polimi.ingsw.gc12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.google.gson;
    requires java.rmi;
    requires jdk.javadoc;
    requires org.fusesource.jansi;
    requires java.desktop;

    opens it.polimi.ingsw.gc12.Model to com.google.gson;
    opens it.polimi.ingsw.gc12.Utilities to com.google.gson;
    exports it.polimi.ingsw.gc12.Model to com.google.gson;
    exports it.polimi.ingsw.gc12.Controller.ServerController.GameStates to com.google.gson;
    opens it.polimi.ingsw.gc12.Controller.ServerController.GameStates to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.Cards to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Cards to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.Conditions to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Conditions to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.ClientModel to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.ClientModel to com.google.gson;


    exports it.polimi.ingsw.gc12.Utilities to com.google.gson, java.rmi;
    exports it.polimi.ingsw.gc12.Controller.ServerController to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Controller.ServerController to com.google.gson;

    exports it.polimi.ingsw.gc12.Listeners;
    exports it.polimi.ingsw.gc12.Utilities.Exceptions;
    opens it.polimi.ingsw.gc12.Utilities.Exceptions to com.google.gson;
    exports it.polimi.ingsw.gc12.Controller;
    exports it.polimi.ingsw.gc12.Controller.Commands.ServerCommands;
    exports it.polimi.ingsw.gc12.Controller.Commands.ClientCommands;
    exports it.polimi.ingsw.gc12.Controller.Commands;
    opens it.polimi.ingsw.gc12.Client.ClientView.GUI to javafx.fxml;
    exports it.polimi.ingsw.gc12.Client.ClientView.GUI;
    exports it.polimi.ingsw.gc12.Network;
    opens it.polimi.ingsw.gc12.Network to com.google.gson;
    exports it.polimi.ingsw.gc12.Network.Server;
    opens it.polimi.ingsw.gc12.Network.Server to com.google.gson;
    exports it.polimi.ingsw.gc12.Network.Client;
    opens it.polimi.ingsw.gc12.Network.Client to com.google.gson;
    exports it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers;
    opens it.polimi.ingsw.gc12.Client.ClientView.GUI.GUIControllers to javafx.fxml;
    // exports it.polimi.ingsw.gc12.Client.ClientView.GUI;
}