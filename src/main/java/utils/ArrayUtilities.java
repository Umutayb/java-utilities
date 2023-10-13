package utils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ArrayUtilities {

    static Random random = new Random();

    public static <ItemType> ItemType getRandomItemFrom(List<ItemType> items) {
        int randomIndex = random.nextInt(items.size());
        return items.get(randomIndex);
    }

}
