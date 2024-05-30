package it.polimi.ingsw.gc12.Utilities;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A utility class that contains common methods used in different contexts of the code.
 */
public class Commons {

    /**
     * Performs a reverse lookup on a map to find the key associated with a value that matches a given condition.
     * This method assumes that the map is bidirectional (biunivocal) where each value uniquely corresponds to a key.
     *
     * @param <K>                   the type of keys maintained by the map
     * @param <V>                   the type of mapped values
     * @param targetMap             the map on which to perform the reverse lookup
     * @param valueMatchingFunction a predicate to test the values in the map and find a match
     * @return the key associated with the value that matches the given function
     * @throws NoSuchElementException if no key matches the given value condition
     */
    public static <K, V> K keyReverseLookup(Map<K, V> targetMap, Predicate<? super V> valueMatchingFunction){
        return targetMap.entrySet().stream()
                .filter((entry) -> valueMatchingFunction.test(entry.getValue()))
                .findFirst()
                .flatMap((entry) -> Optional.of(entry.getKey()))
                .orElseThrow();
    }

}
