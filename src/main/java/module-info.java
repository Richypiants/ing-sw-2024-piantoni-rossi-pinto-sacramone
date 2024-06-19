module it.polimi.ingsw.gc12 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.google.gson;
    requires java.rmi;
    requires jdk.javadoc;
    requires org.fusesource.jansi;
    requires java.desktop;

    opens it.polimi.ingsw.gc12.Model to com.google.gson;
    opens it.polimi.ingsw.gc12.Utilities to com.google.gson;
    exports it.polimi.ingsw.gc12.Model to com.google.gson;
    exports it.polimi.ingsw.gc12.Controller.Server.GameStates to com.google.gson;
    opens it.polimi.ingsw.gc12.Controller.Server.GameStates to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.Server.Cards to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Server.Cards to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.Server.Conditions to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Server.Conditions to com.google.gson;
    exports it.polimi.ingsw.gc12.Model.ClientModel to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.ClientModel to com.google.gson;

    exports it.polimi.ingsw.gc12.Utilities to com.google.gson, java.rmi;
    exports it.polimi.ingsw.gc12.Controller.Server to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Controller.Server to com.google.gson;

    exports it.polimi.ingsw.gc12.Listeners;
    exports it.polimi.ingsw.gc12.Utilities.Exceptions;
    opens it.polimi.ingsw.gc12.Utilities.Exceptions to com.google.gson;
    exports it.polimi.ingsw.gc12.Controller;
    exports it.polimi.ingsw.gc12.Commands.ServerCommands;
    exports it.polimi.ingsw.gc12.Commands.ClientCommands;
    exports it.polimi.ingsw.gc12.Commands;
    exports it.polimi.ingsw.gc12.Network;
    opens it.polimi.ingsw.gc12.Network to com.google.gson;
    exports it.polimi.ingsw.gc12.Network.Server;
    opens it.polimi.ingsw.gc12.Network.Server to com.google.gson;
    exports it.polimi.ingsw.gc12.Network.Client;
    opens it.polimi.ingsw.gc12.Network.Client to com.google.gson;
    exports it.polimi.ingsw.gc12.View.Client.GUI.GUIViews;
    opens it.polimi.ingsw.gc12.View.Client.GUI.GUIViews to javafx.fxml;
    exports it.polimi.ingsw.gc12.View.Client.GUI;
    opens it.polimi.ingsw.gc12.View.Client.GUI to javafx.graphics;
    exports it.polimi.ingsw.gc12.Utilities.Enums to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Utilities.Enums to com.google.gson;
    exports it.polimi.ingsw.gc12.Utilities.JSONParsers to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Utilities.JSONParsers to com.google.gson;
    exports it.polimi.ingsw.gc12.Listeners.Server;
    exports it.polimi.ingsw.gc12.Model.Server to com.google.gson;
    opens it.polimi.ingsw.gc12.Model.Server to com.google.gson;
    exports it.polimi.ingsw.gc12.Utilities.JSONParsers.ClientParsers to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Utilities.JSONParsers.ClientParsers to com.google.gson;
    exports it.polimi.ingsw.gc12.Utilities.JSONParsers.Server to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Utilities.JSONParsers.Server to com.google.gson;
    exports it.polimi.ingsw.gc12.Main.Client to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Main.Client to com.google.gson;
    exports it.polimi.ingsw.gc12.Main.Server to com.google.gson, java.rmi;
    opens it.polimi.ingsw.gc12.Main.Server to com.google.gson;
}