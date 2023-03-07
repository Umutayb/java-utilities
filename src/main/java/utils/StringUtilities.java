package utils;

import org.apache.commons.lang3.RandomStringUtils;
import java.text.Normalizer;
import resources.Colors;
import java.util.*;

import static resources.Colors.*;

@SuppressWarnings("unused")
public class StringUtilities {   //Utility methods

    private final Printer log = new Printer(StringUtilities.class);
    private final ObjectUtilities objectUtils = new ObjectUtilities();

    /**
     * Basic color codes
     */
    public enum Color {
        CYAN("CYAN_BOLD"),
        RED("RED"),
        GREEN("GREEN"),
        YELLOW("YELLOW"),
        PURPLE("PURPLE"),
        GRAY("GRAY"),
        BLUE("BLUE"),
        BLACK("BLACK_BOLD"),
        PALE("WHITE_BOLD");

        final String value;

        Color(String value){this.value = value;}

        public String getValue() {return value;}
    }

    /**
     * Highlights a given text
     * @param color desired color
     * @param text target text
     * @return returns the colored text
     */
    public String highlighted(Color color, Object text){return (objectUtils.getFieldValue(color.getValue(), Colors.class) + text.toString() + RESET);}

    /**
     * Highlights a given text
     * @param color desired color
     * @param text target text
     * @return -
     */
    public String highlight(Color color, Object text){return (objectUtils.getFieldValue(color.getValue(), Colors.class) + text.toString() + GRAY);}

    /**
     * Reverses a given string
     * @param input target text
     * @return returns the reversed text
     */
    public String reverse(String input){
        StringBuilder reversed = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {reversed.append(input.charAt(input.length() - i - 1));}
        return reversed.toString();
    }

    /**
     * Capitalizes the first letter of the input string
     * @param inputString target text
     * @return returns the capitalized first letter text
     */
    public String firstLetterCapped(String inputString){

        if (inputString!=null){
            String firstLetter = inputString.substring(0, 1);
            String remainingLetters = inputString.substring(1);
            firstLetter = firstLetter.toUpperCase();

            return firstLetter + remainingLetters;
        }
        else
            return null;
    }

    /**
     * Decapitalizes the first letter of the input string
     * @param inputString target text
     * @return returns the decapitalized text
     */
    public String firstLetterDeCapped(String inputString){

        if (inputString!=null){
            String firstLetter = inputString.substring(0, 1);
            String remainingLetters = inputString.substring(1);
            firstLetter = firstLetter.toLowerCase();

            return firstLetter + remainingLetters;
        }
        else
            return null;
    }

    /**
     * Cleans the input string of spaces, numbers, special characters, non-english characters etc.
     * @param inputString target text
     * @return returns the cleaned text
     */
    public String cleanText(String inputString){

        inputString = inputString
                .replaceAll("\\s", "")                    //Cleans spaces
                .replaceAll("[0-9]", "")                  //Cleans numbers
                .replaceAll("[-+^.,'&%/()=\"?!:;_*]*", "") //Cleans special characters
                .replaceAll("[^\\x00-\\x7F]", "");        //Cleans non english characters

        if (inputString.isEmpty())
            inputString = generateRandomString("element",4, true, false);

        return inputString;
    }

    /**
     * Replaces non-english characters in input string
     * @param inputString target text
     * @return returns replaced text
     */
    public String normalize(String inputString){

        return Normalizer
                .normalize(inputString, Normalizer.Form.NFD)
                .replaceAll("ç", "c")
                .replaceAll("ğ", "g")
                .replaceAll("ü", "u")
                .replaceAll("ş", "s")
                .replaceAll("ı", "i")
                .replaceAll("[^\\p{ASCII}]", "");
    }

    /**
     * Shortens string to the given length
     * @param inputString target text
     * @param length desired length
     * @return returns the shortened text
     */
    public String shorten(String inputString, int length) {

        return inputString.substring(0, Math.min(inputString.length(), length));
    }

    /**
     * Generates random text according to the input rules
     * @param keyword desired keywords
     * @param length desired length
     * @param useLetters includes letters if true
     * @param useNumbers includes numvers if true
     * @return returns the randomly created text
     */
    public String generateRandomString(String keyword, int length, boolean useLetters, boolean useNumbers) {
        return keyword + RandomStringUtils.random(length, useLetters, useNumbers);
    }

    /**
     * Measures distance between two given sections in a text
     * @param input target text
     * @param firstKeyword first keyword
     * @param lastKeyword last keyword
     * @return returns the distance value
     */
    public int measureDistanceBetween(String input, String firstKeyword, String lastKeyword){

        // Remove any special chars from string
        final String strOnlyWords = input.replace(",", "").replace(".", "");

        final List<String> words = Arrays.asList(strOnlyWords.split(" "));
        final int index1 = words.indexOf(firstKeyword);
        final int index2 = words.indexOf(lastKeyword);
        int distance = -1;

        // Check index of two words
        if (index1 != -1 && index2 != - 1) {
            distance = index2 - index1;
        }

        return distance;
    }

    /**
     * Turns a given text into a map
     * @param inputString target text
     * @return returns the string map (Map<String, String>)
     */
    public Map<String, String> str2Map(String inputString){

        Map<String, String> outputMap = new HashMap<>();

        inputString = inputString.replaceAll("[{}]*", "");

        String[] pairs = inputString.split(",");

        for(String pair: pairs) {

            String[] keyValue = pair.split("=");

            try{
                if (keyValue[0] != null && keyValue[1] != null)
                    outputMap.put(keyValue[0].trim(), keyValue[1].trim());

                else if (keyValue[0] == null)
                    throw new Exception( "First value of this pair was found to be null");

                else throw new Exception( "Second value of this pair was found to be null");

            }
            catch (Exception gamma){log.new Error(GRAY+gamma.getMessage()+RESET,gamma);}
        }
        return outputMap;
    }

}