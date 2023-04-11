package utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The NumericUtilities class contains methods for generating random numbers, sorting lists of integers, and shortening doubles.
 *
 * @author Umut Ay Bora
 * @version 1.4.0 (Documented in 1.4.0, released in an earlier version)
 */
public class NumericUtilities {

    /**
     * Generates a random integer within the given range.
     * @param min the minimum value of the range (inclusive)
     * @param max the maximum value of the range (inclusive)
     * @return a random integer within the range
     */
    public int randomNumber(int min, int max){
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }

    /**
     * Sorts a list of integers in ascending or descending order.
     * @param list the list of integers to be sorted
     * @param larger2smaller true if the list should be sorted from largest to smallest, false if it should be sorted from smallest to largest
     * @return a new list containing the sorted integers
     */
    public List<Integer> sortList(List<Integer> list, boolean larger2smaller){
        List<Integer> orderedList = new ArrayList<>();
        int lnIndex;
        int ln;
        Map<String , Integer> map;
        while (list.size() > 0){
            if (larger2smaller) map = getLargestInList(list);
            else map = getSmallestInList(list);
            ln = map.get("number");
            lnIndex = map.get("index");
            list.remove(lnIndex);
            orderedList.add(ln);
        }
        return orderedList;
    }

    /**
     * Finds the largest integer in a list.
     * @param list the list of integers to be searched
     * @return a map containing the largest integer and its index in the list
     */
    public Map<String, Integer> getLargestInList(List<Integer> list){
        Map<String, Integer> largestNumberMap = new HashMap<>();
        int largestNumber = 0;
        int largestNumberIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (largestNumber<list.get(i)){
                largestNumber = list.get(i);
                largestNumberIndex = i;
            }
        }
        largestNumberMap.put("number", largestNumber);
        largestNumberMap.put("index", largestNumberIndex);
        return largestNumberMap;
    }

    /**
     * Finds the smallest integer in a list.
     * @param list the list of integers to be searched
     * @return a map containing the smallest integer and its index in the list
     */
    public Map<String, Integer> getSmallestInList(List<Integer> list){
        Map<String, Integer> largestNumberMap = new HashMap<>();
        int smallestNumber = 999999999;
        int smallestNumberIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (smallestNumber>list.get(i)){
                smallestNumber = list.get(i);
                smallestNumberIndex = i;
            }
        }
        largestNumberMap.put("number", smallestNumber);
        largestNumberMap.put("index", smallestNumberIndex);
        return largestNumberMap;
    }

    /**
     * Shortens a double to two decimal places.
     * @param number the double to be shortened
     * @return the shortened double
     */
    public Double shortenDouble(Double number){
        DecimalFormat formatter = new DecimalFormat("#.##");
        return Double.parseDouble(formatter.format(number).replaceAll(",","."));
    }
}