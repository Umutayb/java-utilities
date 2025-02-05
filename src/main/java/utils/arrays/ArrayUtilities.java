package utils.arrays;

import java.util.ArrayList;
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

    /**
     * Splits a given list into partitions and returns a specific partition based on the partition index.
     *
     * This method uses stream filtering to find and return the elements that belong to the requested partition.
     * The partition index is 0-based.
     *
     * @param <T> The type of elements in the list.
     * @param originalList The original list to be partitioned.
     * @param partitionSize The size of each partition.
     * @param partitionIndex The index of the partition to retrieve (0-based index).
     * @return A list containing the elements from the specified partition.
     * @throws IndexOutOfBoundsException If the partitionIndex is out of bounds (negative or too large).
     */
    public static <T> List<T> getListPartition(List<T> originalList, int partitionSize, int partitionIndex){
        int partitionBottomLimit = partitionSize * partitionIndex;
        return originalList.stream().filter(item -> {
            int itemIndex = originalList.indexOf(item);
            return partitionBottomLimit <= itemIndex && itemIndex < partitionSize + partitionBottomLimit;
        }).toList();
    }

    /**
     * Calculates the total number of partitions needed to divide a list of a given size into partitions
     * of the specified size.
     *
     * If there are leftover elements after dividing the list, an extra partition is counted.
     *
     * @param listSize The total number of elements in the list.
     * @param partitionSize The size of each partition.
     * @return The number of partitions required to divide the list.
     */
    public static int getPartitionCount(int listSize, int partitionSize){
        int partitionCount = listSize / partitionSize;
        return (partitionCount * partitionSize > listSize ? partitionCount + 1 : partitionCount);
    }
}

