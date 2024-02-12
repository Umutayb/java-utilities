package utils.arrays;

import java.util.List;
import java.util.Random;

/**
 * Utility class for array-related operations.
 *
 * @author Egecan Sen
 * @version 1.6.2 (Documented in 1.6.2, released in an earlier version)
 */
public class ArrayUtilities {

    /**
     * Returns a random item from the provided list.
     *
     * @param <ItemType> the type of elements in the list
     * @param items      the list from which to select a random item
     * @return a randomly selected item from the list
     * @throws IllegalArgumentException if the list is empty
     */
    public static <ItemType> ItemType getRandomItemFrom(List<ItemType> items) {
        if (items.isEmpty()) {
            throw new IllegalArgumentException("Cannot get a random item from an empty list");
        }
        int randomIndex = new Random().nextInt(items.size());
        return items.get(randomIndex);
    }
}

