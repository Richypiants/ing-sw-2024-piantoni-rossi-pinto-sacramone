package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Controller.ClientController.ClientController;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ErrorLogger {
    /*
        A FileWriter is used to write all the stackTrace of an exception thrown by any operation
         during the running of the client logged with extra infos such as the timestamp and the context
     */
    Path filePath;
    SimpleDateFormat formatter;
    PrintStream err;

    public ErrorLogger(String pathFile) {
        filePath = Paths.get(pathFile);
        formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        try {
            err = new PrintStream(new FileOutputStream(pathFile));
            System.setErr(err);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.err.println("New logging instance from client started at: " + formatter.format(new Date()));
    }

    public void log(Throwable error) {
        String timestamp = formatter.format(new Date());
        err.print("[" + timestamp + "] " + error.getMessage() + "\n" + Arrays.toString(error.getStackTrace()).replaceAll(" ", "\n") + "\n");

        ClientController.getInstance().view.printError(error);
    }
}


