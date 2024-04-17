package it.polimi.ingsw.gc12.Controller.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.View;
import it.polimi.ingsw.gc12.Utilities.Exceptions.UnknownStringException;

public class Main {

    public static String language;
    public static View view;

    public static void main(String[] args) throws UnknownStringException {
        if (args[1].equalsIgnoreCase("TUI"))
            //view = new TUIView().run();
        /*else
            view = new GUIView().run();
         */

        if (args[0].equalsIgnoreCase("it"))
            language = "IT";
        else
            language = "EN";

        if (args[2].equalsIgnoreCase("RMI"))
            RMIClientSkeleton.getInstance();
        else if (args[2].equalsIgnoreCase("Socket"))
            SocketClient.getInstance();
        else
            throw new UnknownStringException("Provided technology not recognized");

        initializeApp();
        //view.addListener(this);

        System.out.println("Inserisci il tuo nickname (tutti potranno vederlo):");
        //TODO: Scanner

        //view.setNickname();

        //primitivaDiConnessione();

        System.out.println();

    }

    public static void initializeApp() {
        //TODO: caricare le carte e la loro grafica?
        // serve altro da caricare?

        view.initializeApp();
    }
}
