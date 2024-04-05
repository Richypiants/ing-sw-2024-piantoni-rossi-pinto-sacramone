package it.polimi.ingsw.gc12.Client.ClientController;

import it.polimi.ingsw.gc12.Client.ClientView.TUI.TUIView;
import it.polimi.ingsw.gc12.Client.ClientView.View;

public class Main {

    public static String language;
    public static View view;

    public static void main(String[] args) {
        if (args[0].equalsIgnoreCase("it"))
            language = "IT";
        else
            language = "EN";

        if (args[1].equalsIgnoreCase("TUI"))
            view = new TUIView();
        /*else
            view = new GUIView();
         */

        /*if(args[2].equalsIgnoreCase("RMI"))
            //Set RMI
        else
            //Set Socket
         */

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
