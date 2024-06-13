package it.polimi.ingsw.gc12.Utilities;

import it.polimi.ingsw.gc12.Client.ClientView.ViewStates.ViewState;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * A utility class for logging errors with extra information such as timestamp and context.
 * This logger redirects the standard error stream to a specified file and formats the log entries for improved readability and ease of use.
 */
public class ErrorLogger {

    /**
     * The file path where the logs will be written.
     */
    Path filePath;

    /**
     * A formatter to format the timestamp in the log entries.
     */
    SimpleDateFormat formatter;

    /**
     * The PrintStream to write the log entries to the file.
     */
    PrintStream err;

    /**
     * Constructs an ErrorLogger that logs to the specified file.
     * It also sets up the redirection of the standard error stream to the specified file.
     *
     * @param pathFile The path of the file where the logs will be written.
     */
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

    /**
     * Logs the given error with a timestamp and stack trace to the file.
     * In addiction, prints only the message error without the stackTrace on the view to let the user understand what caused the error.
     *
     * @param error The throwable error to be logged.
     */
    public void log(Throwable error) {
        String timestamp = formatter.format(new Date());
        err.print("[" + timestamp + "] " + error.getMessage() + "\n" + Arrays.toString(error.getStackTrace()).replaceAll(" ", "\n") + "\n");

        //FIXME: non dovremmo però stampare tutti gli errori... giusto?
        ViewState.getCurrentState().printError(error);
    }
}


