package it.polimi.ingsw.gc12.Utilities;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class Commons {

    public static <K, V> K keyReverseLookup(Map<K, V> targetMap, Predicate<? super V> valueMatchingFunction){
        return targetMap.entrySet().stream()
                .filter((entry) -> valueMatchingFunction.test(entry.getValue()))
                .findFirst() //FIXME: si potrebbe mettere findAny()?
                .flatMap((entry) -> Optional.of(entry.getKey()))
                .orElseThrow();
    }

}
