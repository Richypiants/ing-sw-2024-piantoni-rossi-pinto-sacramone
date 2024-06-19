package it.polimi.ingsw.gc12.Controller;

import it.polimi.ingsw.gc12.Controller.Client.ClientController;
import it.polimi.ingsw.gc12.Controller.Server.ConnectionController;
import it.polimi.ingsw.gc12.Controller.Server.GameController;
import it.polimi.ingsw.gc12.Controller.Server.LobbyController;
import it.polimi.ingsw.gc12.Controller.Server.ServerController;

/**
 * The {@code ControllerInterface} serves as a common interface for the controllers
 * present in the application, providing a unified type for managing different aspects of the application.
 * <p>
 * Implementations of this interface are responsible for handling the logic required in their context.
 * The known implementing classes and interfaces include:
 * <ul>
 *     <li>{@link ClientController}</li>
 *     <li>{@link ClientControllerInterface}</li>
 *     <li>{@link ConnectionController}</li>
 *     <li>{@link GameController}</li>
 *     <li>{@link LobbyController}</li>
 *     <li>{@link ServerController}</li>
 *     <li>{@link ServerControllerInterface}</li>
 * </ul>
 * </p>
 */
public interface ControllerInterface { }
