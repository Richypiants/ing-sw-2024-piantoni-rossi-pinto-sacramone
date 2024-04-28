package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ErrorLogger {
    /*
        A FileWriter is used to write all the stackTrace of an exception thrown by any operation
         during the running of the client logged with extra infos such as the timestamp and the context
     */
    Path filePath;
    SimpleDateFormat formatter;

    public ErrorLogger(String pathFile) {
        filePath = Paths.get(pathFile);
        formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        try {
            System.setErr(new PrintStream(new FileOutputStream(pathFile)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.err.println("New logging instance from client started at: " + formatter.format(new Date()));
    }

    public void log(Throwable error) {
        String timestamp = formatter.format(new Date());
        System.err.println("[" + timestamp + "] " + error.getMessage());
        error.printStackTrace();

        ClientController.getInstance().view.printError(error);
    }
}

