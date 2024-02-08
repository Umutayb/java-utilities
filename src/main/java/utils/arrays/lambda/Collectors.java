package utils.arrays.lambda;

import java.util.stream.Collector;

/**
 * Utility class for custom collectors.
 *
 * @author Joana Sol Del Valle
 * @version 1.6.2
 */
public class Collectors {

    /**
     * Returns a Collector that accumulates the input elements into a singleton list,
     * and then returns the single element of that list.
     *
     * @param <T> the type of the input elements and the output singleton element
     * @return a Collector that collects elements into a singleton list and returns the single element
     * @throws IllegalStateException if the input stream contains more than one element
     */
    public static <T> Collector<T, ?, T> toSingleton() {
        return java.util.stream.Collectors.collectingAndThen(
                java.util.stream.Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException("Expected exactly one element but found " + list.size());
                    }
                    return list.get(0);
                }
        );
    }
}

