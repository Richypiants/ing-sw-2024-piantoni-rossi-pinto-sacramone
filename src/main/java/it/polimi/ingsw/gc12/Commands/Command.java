package it.polimi.ingsw.gc12.Commands;

import java.io.Serializable;

/**
 * Represents a generic command in this application.
 * <p>
 * Commands that implement this interface encapsulate actions or operations that can be
 * executed within the application. By extending {@link Serializable}, commands can be
 * serialized into a stream of bytes, allowing them to be transferred over a network or
 * stored persistently.
 * <p>
 * Implementing classes should provide specific logic in the {@code execute()} method to
 * perform their intended operation when invoked.
 */
public interface Command extends Serializable {
}
