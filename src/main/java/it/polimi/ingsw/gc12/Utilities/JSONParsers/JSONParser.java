package it.polimi.ingsw.gc12.Utilities.JSONParsers;

import it.polimi.ingsw.gc12.Utilities.Enums.Resource;

/**
 * Utility class to handle JSON parsing and serialization for various card types and conditions.
 */
public abstract class JSONParser {

    /**
     * Helper method to convert a String to the corresponding Resource enum.
     *
     * @param resource The resource string.
     * @return The corresponding Resource enum.
     */
    protected static Resource conversionHelper(String resource) {
        return switch (resource) {
            case "FUNGI" -> Resource.FUNGI;
            case "ANIMAL" -> Resource.ANIMAL;
            case "PLANT" -> Resource.PLANT;
            case "INSECT" -> Resource.INSECT;
            case "SCROLL" -> Resource.SCROLL;
            case "INK" -> Resource.INK;
            case "QUILL" -> Resource.QUILL;
            default -> null;
        };
    }
}
