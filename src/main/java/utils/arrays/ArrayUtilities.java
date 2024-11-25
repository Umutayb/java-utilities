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

    /**
     * Checks if the specified member is the last element in the provided list.
     *
     * @param <ItemType> the type of elements in the list
     * @param items the list of items to check
     * @param member the item to check if it's the last element in the list
     * @return {@code true} if the specified member is the last element in the list,
     *         {@code false} otherwise
     * @throws NullPointerException if the {@code items} list or {@code member} is {@code null}
     */
    public static <ItemType> boolean isLastMemberOf(List<ItemType> items, ItemType member){
        return items.indexOf(member) == (items.size() - 1);
    }
}

